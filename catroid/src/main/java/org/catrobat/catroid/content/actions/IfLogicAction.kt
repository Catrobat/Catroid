/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import com.badlogic.gdx.scenes.scene2d.Actor
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

class IfLogicAction : Action() {
    var scope: Scope? = null
    var ifAction: Action? = null
    var elseAction: Action? = null
    var ifCondition: Formula? = null
    private var ifConditionValue: Boolean = false
    private var isInitialized: Boolean = false
    private var isInterpretedCorrectly: Boolean = false

    private fun interpretCondition() {
        isInterpretedCorrectly = try {
            ifConditionValue = ifCondition?.interpretDouble(scope)?.toInt() != 0
            ifCondition != null
        } catch (interpretationException: InterpretationException) {
            Log.d(javaClass.simpleName, "Formula interpretation for this specific Brick failed.", interpretationException)
            false
        }
    }

    override fun act(delta: Float): Boolean {
        if (!isInitialized) {
            interpretCondition()
            isInitialized = true
        }
        return if (!isInterpretedCorrectly) {
            true
        } else if (ifConditionValue) {
            ifAction?.act(delta) ?: true
        } else {
            elseAction?.act(delta) ?: true
        }
    }

    override fun restart() {
        ifAction?.restart()
        elseAction?.restart()
        isInitialized = false
        super.restart()
    }

    override fun setActor(actor: Actor) {
        super.setActor(actor)
        ifAction?.actor = actor
        elseAction?.actor = actor
    }
}
