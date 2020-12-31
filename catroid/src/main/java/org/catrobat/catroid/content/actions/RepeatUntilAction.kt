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
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

class RepeatUntilAction : RepeatAction() {
    var executedCount = 0
        private set
    var sprite: Sprite? = null
    var repeatCondition: Formula? = null
    private var isCurrentLoopInitialized = false
    private var currentTime = 0f
    var isLoopDelay = false

    private fun isValidConditionFormula(): Boolean {
        try {
            repeatCondition?.interpretDouble(sprite) ?: return false
        } catch (interpretationException: InterpretationException) {
            Log.d(
                javaClass.simpleName, "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
            return false
        }
        return true
    }

    private fun isConditionTrue(): Boolean = try {
        repeatCondition?.interpretDouble(sprite) != 0.0
    } catch (interpretationException: InterpretationException) {
        Log.d(
            javaClass.simpleName, "Formula interpretation for this specific Brick failed.",
            interpretationException
        )
        true
    }

    public override fun delegate(delta: Float): Boolean {
        if (!isValidConditionFormula()) {
            return true
        }
        if (!isCurrentLoopInitialized) {
            if (isConditionTrue()) {
                return true
            }
            currentTime = 0f
            isCurrentLoopInitialized = true
        }
        currentTime += delta
        if (action.act(delta) && noLoopDelayNeeded()) {
            executedCount++
            if (isConditionTrue()) {
                return true
            }
            isCurrentLoopInitialized = false
            action?.restart()
        }
        return false
    }

    private fun noLoopDelayNeeded(): Boolean = currentTime >= LOOP_DELAY || !isLoopDelay

    override fun restart() {
        isCurrentLoopInitialized = false
        executedCount = 0
        super.restart()
    }

    companion object {
        private const val LOOP_DELAY = 0.02f
    }
}
