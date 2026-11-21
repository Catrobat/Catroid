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
 * Lets the "When stage is exited" catch scripts of a project finish their work before a real exit
 * of the stage.
 *
 * Real exits are the brick "Finish stage", an Android Back press that leaves the stage directly,
 * and a press of gamepad button B that the project does not use. Opening the stage menu,
 * restarting and the test results of "Finish tests" and failing asserts are no such exits.
 *
 * Without catch scripts (commented out ones do not count) every exit runs at once on the calling
 * thread, exactly as before. With catch scripts, the first exit becomes pending: the next stage
 * frame fires [StageExitedEventId] once to all sprites, the stage keeps running, and once all
 * started catch scripts have finished, the pending exit runs on the UI thread. A further exit
 * request while the exit is pending forces it, so a second Back press or a "Finish stage" brick
 * inside a catch script exits at once.
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
        /** No exit was requested, or every exit ran at once because there are no catch scripts. */
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

    /** The fired stage exited event, which waits for the catch scripts. Only used on the GL thread. */
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
     * Forces a pending exit to run at once.
     *
     * @return true if an exit is pending or has already run, so that nothing else should happen,
     * false if there is no exit
     */
    fun forcePendingExit(): Boolean {
        val forcedExit = synchronized(lock) {
            when (currentState) {
                State.NONE -> return false
                State.PENDING -> takePendingExit(State.FORCED)
                State.EXITED, State.FORCED -> null
            }
        }
        forcedExit?.let(runOnUiThread)
        return true
    }

    /**
     * Requests a stage exit like "Finish stage" for a released-to-pressed edge of B, if the project
     * does not use B. Called on the GL thread for every pressed edge of a gamepad button.
     */
    fun onGamepadButtonPressed(button: GamepadButton) {
        if (button == GamepadButton.B && !GamepadUsage.of(project()).isUsed(GamepadButton.B)) {
            requestFinishStage()
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

        /** Forces a pending exit of the running stage, see [forcePendingExit]. */
        @JvmStatic
        fun forcePendingExitOfRunningStage(): Boolean = ofRunningStage()?.forcePendingExit() ?: false

        private fun runOnStageUiThread(action: Runnable) {
            StageActivity.activeStageActivity?.get()?.runOnUiThread(action)
        }
    }
}
