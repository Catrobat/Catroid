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

import android.util.Log
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

class WaitUntilAction : Action() {
    private var completed = false
    private var condition: Formula? = null
    private var scope: Scope? = null
    private var currentTime = 0f
    fun setCondition(condition: Formula?) {
        this.condition = condition
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    override fun act(delta: Float): Boolean {
        if (completed) {
            return true
        }
        currentTime += delta
        currentTime = if (currentTime < LOOP_DELAY) {
            return false
        } else {
            0.0f
        }
        try {
            completed = condition!!.interpretBoolean(scope)
        } catch (e: InterpretationException) {
            completed = false
            Log.d(javaClass.simpleName, "Formula interpretation for this specific Brick failed.", e)
        }
        return completed
    }

    override fun restart() {
        completed = false
    }

    companion object {
        private const val LOOP_DELAY = 0.02f
    }
}