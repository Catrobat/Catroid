/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.stage

import org.catrobat.catroid.content.EventWrapper
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.WhenStageExitedScript
import org.catrobat.catroid.content.eventids.StageExitedEventId
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.stage.gamepad.GamepadUsage

/**
 * Lets the "When stage is exited" catch scripts of a project finish their work before the project
 * is exited.
 *
 * Exits are an Android Back press, which leads to the stage menu or, in a shortcut or standalone
 * APK, leaves the stage, and the brick "Finish stage". A press of gamepad button B that the project
 * does not use presses Android Back. The buttons of the stage menu, restarting and the test results
 * of "Finish tests" and failing asserts are no such exits.
 *
 * Without catch scripts (commented out ones do not count) every exit runs at once on the calling
 * thread, exactly as before. With catch scripts, the first exit becomes pending: the next stage
 * frame fires [StageExitedEventId] once to all sprites, so the catch scripts run in parallel like
 * broadcast scripts, the stage keeps running, and once all started catch scripts have finished, the
 * pending exit runs on the UI thread. A further exit request while the exit is pending forces it,
 * so a second Back press or a "Finish stage" brick inside a catch script exits at once. Changing the
 * scene while the catch scripts run ends them, and the pending exit runs.
 *
 * Exit requests may come from any thread. [onFrame] and [onGamepadButtonPressed] run on the GL
 * thread of the stage.
 *
 * @param project returns the project of the stage, or `null` if there is none
 * @param finishStage finishes the stage silently, like the brick "Finish stage"
 * @param runOnUiThread runs the given exit on the UI thread of the stage
 */
class StageExitController @JvmOverloads constructor(
    private val project: () -> Project?,
    private val finishStage: Runnable = Runnable { StageActivity.finishStage() },
    private val runOnUiThread: (Runnable) -> Unit = ::runOnStageUiThread
) {

    /** The progress of a real exit of the stage. */
    enum class State {
        /**
         * No exit was requested, every exit ran at once because there are no catch scripts, or the
         * last exit was [reset].
         */
        NONE,

        /** An exit waits for the catch scripts. */
        PENDING,

        /** The pending exit ran after all catch scripts had finished. */
        EXITED,

        /** The pending exit ran early because a further exit was requested. */
        FORCED
    }

    private val lock = Any()
    private var currentState = State.NONE
    private var pendingExit: Runnable? = null

    /** The fired stage exited event of the pending exit, which waits for the catch scripts. */
    @Volatile
    private var stageExitedEvent: EventWrapper? = null

    val state: State
        get() = synchronized(lock) { currentState }

    /**
     * Requests a real exit of the stage that runs [exitAction]: at once without catch scripts,
     * once the catch scripts have finished, or at once if an exit is already pending. The
     * [exitAction] of a request that does not start a pending exit is dropped then, because the
     * pending exit runs instead. Requests after the exit are ignored.
     */
    fun requestExit(exitAction: Runnable) {
        if (!hasCatchScripts()) {
            exitAction.run()
            return
        }
        val forcedExit = synchronized(lock) {
            when (currentState) {
                State.NONE -> {
                    currentState = State.PENDING
                    pendingExit = exitAction
                    stageExitedEvent = null
                    null
                }
                State.PENDING -> takePendingExit(State.FORCED)
                State.EXITED, State.FORCED -> null
            }
        }
        forcedExit?.let(runOnUiThread)
    }

    /** Requests a real exit that finishes the stage silently, like the brick "Finish stage". */
    fun requestFinishStage() = requestExit(finishStage)

    /**
     * Forgets the last exit, so that the next one runs the catch scripts again. The stage calls it
     * when the stage menu opens, because the project can continue from there.
     */
    fun reset() {
        synchronized(lock) {
            currentState = State.NONE
            pendingExit = null
        }
    }

    /**
     * Presses Android Back on the UI thread for a released-to-pressed edge of B, if the project
     * does not use B: the catch scripts run, then the stage menu opens or, in a shortcut or
     * standalone APK, the stage closes. Called on the GL thread for every pressed edge of a gamepad
     * button.
     */
    fun onGamepadButtonPressed(button: GamepadButton) {
        if (button == GamepadButton.B && !GamepadUsage.of(project()).isUsed(GamepadButton.B)) {
            runOnUiThread(Runnable { StageActivity.activeStageActivity?.get()?.onBackPressed() })
        }
    }

    /**
     * Fires the stage exited event of a pending exit once and runs the pending exit as soon as all
     * catch scripts started by it have finished. Called on the GL thread once per running frame.
     */
    fun onFrame() {
        if (state != State.PENDING) {
            return
        }
        val event = stageExitedEvent ?: fireStageExitedEvent()
        if (event.isWaitingForSprite()) {
            return
        }
        val finishedExit = synchronized(lock) {
            if (currentState == State.PENDING) takePendingExit(State.EXITED) else null
        }
        finishedExit?.let(runOnUiThread)
    }

    /**
     * Ends the catch scripts of a pending exit whose catch scripts have started, because the scene
     * that they belong to is left: [stopCatchScripts] stops them for good, so they do not resume
     * when the project continues that scene later, and the pending exit runs. An exit whose catch
     * scripts have not started yet stays pending, so the next frame starts the catch scripts of the
     * new scene. Called on the GL thread before a scene starts or continues.
     */
    fun onSceneLeaving(stopCatchScripts: Runnable) {
        val endedExit = synchronized(lock) {
            if (currentState == State.PENDING && stageExitedEvent != null) {
                takePendingExit(State.EXITED)
            } else {
                null
            }
        }
        endedExit?.let {
            stopCatchScripts.run()
            runOnUiThread(it)
        }
    }

    private fun fireStageExitedEvent(): EventWrapper =
        EventWrapper(StageExitedEventId, true).also { event ->
            stageExitedEvent = event
            project()?.fireToAllSprites(event)
        }

    private fun takePendingExit(newState: State): Runnable? {
        currentState = newState
        return pendingExit.also { pendingExit = null }
    }

    private fun hasCatchScripts(): Boolean =
        project()?.sceneList.orEmpty().asSequence()
            .flatMap { scene -> scene.spriteList }
            .flatMap { sprite -> sprite.scriptList }
            .any { script -> script is WhenStageExitedScript && !script.isCommentedOut }

    companion object {
        /** The exit controller of the running stage, or `null` if no stage runs. */
        @JvmStatic
        fun ofRunningStage(): StageExitController? = StageActivity.stageListener?.stageExitController

        /**
         * Requests a real exit of the running stage, see [requestExit]. Without a running stage,
         * [exitAction] runs at once.
         */
        @JvmStatic
        fun requestExitOfRunningStage(exitAction: Runnable) {
            ofRunningStage()?.requestExit(exitAction) ?: exitAction.run()
        }

        private fun runOnStageUiThread(action: Runnable) {
            StageActivity.activeStageActivity?.get()?.runOnUiThread(action)
        }
    }
}
