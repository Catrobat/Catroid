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
package org.catrobat.catroid.content.actions

import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.stage.StageActivity

class WaitTillIdleAction : Action() {
    override fun act(delta: Float): Boolean {
        return allActorsIdle()
    }

    private fun allActorsIdle(): Boolean {
        val numberOfActors = StageActivity.stageListener.stage.actors.size
        if (numberOfActors == 0) {
            return false
        }
        var actorsWithNoEventThreads = 0
        var actorWithOnlyThisEventThread = 0
        for (actor in StageActivity.stageListener.stage.actors) {
            val actions = actor.actions
            if (actions.size == 0) {
                actorsWithNoEventThreads++
            }
            if (actions.size == 1) {
                val sequenceAction = actions[0] as ScriptSequenceAction
                if (sequenceAction.actions.contains(this, true)) {
                    actorWithOnlyThisEventThread++
                }
            }
        }
        return numberOfActors == actorsWithNoEventThreads + actorWithOnlyThisEventThread
    }
}