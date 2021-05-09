/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.formulaeditor.UserVariable

class ForVariableFromToAction : LoopAction() {
    private var controlVariable: UserVariable? = null
    private var from: Formula? = null
    private var to: Formula? = null
    private var scope: Scope? = null
    private var isCurrentLoopInitialized = false
    private var isRepeatActionInitialized = false
    private var fromValue = 0
    private var toValue = 0
    private var executedCount = 0
    private var step = 1
    public override fun delegate(delta: Float): Boolean {
        if (!isRepeatActionInitialized && !interpretParameters()) {
            return true
        }
        if (!isCurrentLoopInitialized) {
            currentTime = 0f
            isCurrentLoopInitialized = true
        }
        setControlVariable(fromValue + step * executedCount)
        currentTime = currentTime + delta
        if (action != null && action.act(delta) && !isLoopDelayNeeded()) {
            executedCount++
            if (Math.abs(step * executedCount) > Math.abs(toValue - fromValue)) {
                return true
            }
            isCurrentLoopInitialized = false
            action.restart()
        }
        return false
    }

    override fun restart() {
        isCurrentLoopInitialized = false
        isRepeatActionInitialized = false
        executedCount = 0
        super.restart()
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    fun setRange(from: Formula?, to: Formula?) {
        this.from = from
        this.to = to
    }

    fun setControlVariable(variable: UserVariable?) {
        controlVariable = variable
    }

    private fun interpretParameters(): Boolean {
        isRepeatActionInitialized = true
        return try {
            val fromInterpretation =
                if (from == null) java.lang.Double.valueOf(0.0) else from!!.interpretDouble(scope)
            fromValue = fromInterpretation.toInt()
            val toInterpretation =
                if (to == null) java.lang.Double.valueOf(0.0) else to!!.interpretDouble(scope)
            toValue = toInterpretation.toInt()
            setStepValue()
            true
        } catch (interpretationException: InterpretationException) {
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
            false
        }
    }

    private fun setStepValue() {
        step = if (fromValue <= toValue) 1 else -1
    }

    private fun setControlVariable(value: Int) {
        controlVariable!!.value = value.toDouble()
    }
}