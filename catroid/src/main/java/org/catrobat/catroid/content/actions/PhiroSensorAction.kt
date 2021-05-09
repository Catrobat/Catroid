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
import com.badlogic.gdx.scenes.scene2d.Actor
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.formulaeditor.Sensors

class PhiroSensorAction : Action() {
    private var sensorNumber = 0
    private var scope: Scope? = null
    private var ifAction: Action? = null
    private var elseAction: Action? = null
    private var ifCondition: Formula? = null
    private var ifConditionValue: Boolean? = null
    private var isInitialized = false
    private var isInterpretedCorrectly = false
    protected fun begin() {
        try {
            if (ifCondition == null) {
                isInterpretedCorrectly = false
                return
            }
            val interpretation = ifCondition!!.interpretDouble(scope)
            ifConditionValue =
                if (interpretation.toInt() <= DISTANCE_THRESHOLD_VALUE) true else false
            isInterpretedCorrectly = true
        } catch (interpretationException: InterpretationException) {
            isInterpretedCorrectly = false
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
    }

    override fun act(delta: Float): Boolean {
        if (!isInitialized) {
            begin()
            isInitialized = true
        }
        if (!isInterpretedCorrectly) {
            return true
        }
        return if (ifConditionValue!!) {
            ifAction!!.act(delta)
        } else {
            elseAction!!.act(delta)
        }
    }

    override fun restart() {
        ifAction!!.restart()
        elseAction!!.restart()
        isInitialized = false
        super.restart()
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    fun setIfAction(ifAction: Action?) {
        this.ifAction = ifAction
    }

    fun setElseAction(elseAction: Action?) {
        this.elseAction = elseAction
    }

    fun setIfCondition(ifCondition: Formula?) {
        this.ifCondition = ifCondition
    }

    fun setSensor(sensorNumber: Int) {
        this.sensorNumber = sensorNumber
        setIfCondition(
            Formula(
                FormulaElement(
                    FormulaElement.ElementType.SENSOR,
                    phiroProSensorByNumber.name,
                    null
                )
            )
        )
    }

    private val phiroProSensorByNumber: Sensors
        private get() {
            when (sensorNumber) {
                0 -> return Sensors.PHIRO_FRONT_LEFT
                1 -> return Sensors.PHIRO_FRONT_RIGHT
                2 -> return Sensors.PHIRO_SIDE_LEFT
                3 -> return Sensors.PHIRO_SIDE_RIGHT
                4 -> return Sensors.PHIRO_BOTTOM_LEFT
                5 -> return Sensors.PHIRO_BOTTOM_RIGHT
            }
            return Sensors.PHIRO_SIDE_RIGHT
        }

    override fun setActor(actor: Actor) {
        super.setActor(actor)
        ifAction!!.actor = actor
        elseAction!!.actor = actor
    }

    companion object {
        private const val DISTANCE_THRESHOLD_VALUE = 850
    }
}