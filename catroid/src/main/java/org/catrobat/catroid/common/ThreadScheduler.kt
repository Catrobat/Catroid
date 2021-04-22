/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import com.badlogic.gdx.utils.Array
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.actions.ScriptSequenceActionWithWaiter
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

class ThreadScheduler(private val actor: Actor) {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(RUNNING, SUSPENDED)
    annotation class SchedulerState

    private val startQueue = Array<ScriptSequenceAction>()
    private val stopQueue = Array<Action>()

    @SchedulerState
    private var state = RUNNING
    fun tick(delta: Float) {
        val actions = actor.actions
        startThreadsInStartQueue()
        runThreadsForOneTick(actions, delta)
        stopThreadsInStopQueue(actions)
    }

    private fun startThreadsInStartQueue() {
        for (thread in startQueue) {
            thread.restart()
            actor.addAction(thread)
        }
        startQueue.clear()
    }

    private fun runThreadsForOneTick(actions: Array<Action>, delta: Float) {
        for (i in 0 until actions.size) {
            val action = actions[i]
            if (state == RUNNING && action.act(delta)) {
                stopQueue.add(action)
            }
        }
    }

    private fun stopThreadsInStopQueue(actions: Array<Action>) {
        actions.removeAll(stopQueue, true)
        for (action in stopQueue) {
            if (action is ScriptSequenceActionWithWaiter) {
                action.notifyWaiter()
            }
        }
        stopQueue.clear()
    }

    fun startThread(sequenceAction: ScriptSequenceAction) {
        removeThreadsWithEqualScriptFromStartQueue(sequenceAction)
        startQueue.add(sequenceAction)
    }

    private fun removeThreadsWithEqualScriptFromStartQueue(sequenceAction: ScriptSequenceAction) {
        val iterator = startQueue.iterator()
        while (iterator.hasNext()) {
            val action = iterator.next()
            if (action.script === sequenceAction.script) {
                if (action is ScriptSequenceActionWithWaiter) {
                    action.notifyWaiter()
                }
                iterator.remove()
            }
        }
    }

    fun stopThreadsWithScript(script: Script) {
        for (action in actor.actions) {
            if (action is ScriptSequenceAction && action.script === script) {
                stopQueue.add(action)
            }
        }
    }

    fun stopThreads(actions: Array<Action>?) {
        stopQueue.addAll(actions)
    }

    fun haveAllThreadsFinished(): Boolean {
        return startQueue.size + actor.actions.size == 0
    }

    fun setState(@SchedulerState schedulerState: Int) {
        state = schedulerState
    }

    companion object {
        const val RUNNING = 0
        const val SUSPENDED = 1
    }
}