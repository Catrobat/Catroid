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
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

class GoNStepsBackAction : TemporalAction() {
    private var scope: Scope? = null
    private var steps: Formula? = null
    override fun update(delta: Float) {
        val stepsValue: Float
        stepsValue = try {
            if (steps == null) java.lang.Float.valueOf(0f) else steps!!.interpretFloat(scope)
        } catch (interpretationException: InterpretationException) {
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
            return
        }
        val zPosition = scope!!.sprite.look.zIndex
        if (stepsValue.toInt() > 0 && zPosition - stepsValue.toInt() < Constants.Z_INDEX_FIRST_SPRITE) {
            scope!!.sprite.look.zIndex = Constants.Z_INDEX_FIRST_SPRITE
        } else if (stepsValue.toInt() < 0 && zPosition - stepsValue.toInt() < zPosition) {
            toFront(delta)
        } else {
            goNStepsBack(stepsValue.toInt())
        }
    }

    private fun toFront(delta: Float) {
        val comeToFrontAction = scope!!.sprite.actionFactory.createComeToFrontAction(
            scope!!.sprite
        )
        comeToFrontAction.act(delta)
    }

    private fun goNStepsBack(steps: Int) {
        val zPosition = scope!!.sprite.look.zIndex
        val newSpriteZIndex = Math.max(zPosition - steps, Constants.Z_INDEX_FIRST_SPRITE)
        scope!!.sprite.look.zIndex = newSpriteZIndex
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    fun setSteps(steps: Formula?) {
        this.steps = steps
    }
}