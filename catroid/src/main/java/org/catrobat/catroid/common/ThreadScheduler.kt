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

package org.catrobat.catroid.common

import androidx.annotation.IntDef
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.actions.ScriptSequenceActionWithWaiter
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Runs the script threads of an [actor]. Started threads wait in a start queue and join the
 * actions of the actor with its next [tick]. Finished and stopped threads leave the actor at the
 * end of a tick.
 */
class ThreadScheduler(private val actor: Actor) {

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(RUNNING, SUSPENDED)
    annotation class SchedulerState

    private val startQueue = GdxArray<ScriptSequenceAction>()
    private val stopQueue = GdxArray<Action>()

    @SchedulerState
    private var state = RUNNING

    fun tick(delta: Float) {
        val actions = actor.actions
        startThreadsInStartQueue()
        runThreadsForOneTick(actions, delta)
        stopThreadsInStopQueue(actions)
    }

    /** Queues [sequenceAction] for the next tick instead of a queued thread of the same script. */
    fun startThread(sequenceAction: ScriptSequenceAction) {
        removeThreadsWithEqualScriptFromStartQueue(sequenceAction.script)
        startQueue.add(sequenceAction)
    }

    /**
     * Removes the started and the queued threads of the script at once, so none of them acts
     * again, not even in the next tick.
     */
    fun removeThreadsWithScript(script: Script) {
        removeThreadsWithEqualScriptFromStartQueue(script)
        actor.actions.filter { it.isThreadOf(script) }.forEach { thread ->
            actor.removeAction(thread)
            thread.notifyWaiterIfAny()
        }
    }

    /** Stops the started threads of [script] at the end of the next tick. */
    fun stopThreadsWithScript(script: Script) {
        actor.actions.filter { it.isThreadOf(script) }.forEach(stopQueue::add)
    }

    /** Stops [threads] at the end of the next tick. */
    fun stopThreads(threads: GdxArray<Action>) {
        stopQueue.addAll(threads)
    }

    fun haveAllThreadsFinished(): Boolean = startQueue.size + actor.actions.size == 0

    fun setState(@SchedulerState schedulerState: Int) {
        state = schedulerState
    }

    private fun startThreadsInStartQueue() {
        startQueue.forEach { thread ->
            thread.restart()
            actor.addAction(thread)
        }
        startQueue.clear()
    }

    /** Lets each thread act once. Threads added while the threads act also act in this tick. */
    private fun runThreadsForOneTick(actions: GdxArray<Action>, delta: Float) {
        var index = 0
        while (index < actions.size) {
            val action = actions[index]
            if (state == RUNNING && action.act(delta)) {
                stopQueue.add(action)
            }
            index++
        }
    }

    private fun stopThreadsInStopQueue(actions: GdxArray<Action>) {
        actions.removeAll(stopQueue, true)
        stopQueue.forEach { it.notifyWaiterIfAny() }
        stopQueue.clear()
    }

    /** Accepts a thread without a script, as the Java version did, for example a mocked one. */
    private fun removeThreadsWithEqualScriptFromStartQueue(script: Script?) {
        startQueue.filter { it.script === script }.forEach { thread ->
            thread.notifyWaiterIfAny()
            startQueue.removeValue(thread, true)
        }
    }

    private fun Action.isThreadOf(script: Script): Boolean =
        this is ScriptSequenceAction && this.script === script

    private fun Action.notifyWaiterIfAny() {
        (this as? ScriptSequenceActionWithWaiter)?.notifyWaiter()
    }

    companion object {
        const val RUNNING = 0
        const val SUSPENDED = 1
    }
}
