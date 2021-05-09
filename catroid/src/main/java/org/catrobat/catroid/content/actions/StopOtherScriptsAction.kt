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
import com.badlogic.gdx.utils.Array
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite

class StopOtherScriptsAction : Action() {
    private var currentScript: Script? = null
    private var sprite: Sprite? = null
    override fun act(delta: Float): Boolean {
        if (actor !is Look || actor.actions == null) {
            return true
        }
        sprite!!.releaseAllPointers()
        val look = actor as Look
        look.stopThreads(getOtherThreads(look))
        return true
    }

    private fun getOtherThreads(look: Look): Array<Action> {
        val otherThreads = Array(look.actions)
        val it = otherThreads.iterator()
        while (it.hasNext()) {
            val action = it.next()
            if (action is ScriptSequenceAction && action.script === currentScript) {
                it.remove()
            }
        }
        return otherThreads
    }

    fun setCurrentScript(currentScript: Script?) {
        this.currentScript = currentScript
    }

    fun setSprite(sprite: Sprite?) {
        this.sprite = sprite
    }
}