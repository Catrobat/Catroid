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
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.devices.raspberrypi.RaspberryPiService
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

class RaspiPwmAction : TemporalAction() {
    private var pinNumberFormula: Formula? = null
    private var pwmFrequencyFormula: Formula? = null
    private var pwmPercentageFormula: Formula? = null
    private var scope: Scope? = null
    private var pinInterpretation = 0
    private var frequencyInterpretation = 0.0
    private var percentageInterpretation = 0.0
    override fun begin() {
        try {
            pinInterpretation =
                if (pinNumberFormula == null) Integer.valueOf(0) else pinNumberFormula!!.interpretInteger(
                    scope
                )
        } catch (interpretationException: InterpretationException) {
            pinInterpretation = 0
            Log.d(
                TAG, "Formula interpretation for this specific Brick failed. (pin)",
                interpretationException
            )
        }
        try {
            frequencyInterpretation =
                if (pwmFrequencyFormula == null) java.lang.Double.valueOf(0.0) else pwmFrequencyFormula!!
                    .interpretDouble(scope)
        } catch (interpretationException: InterpretationException) {
            frequencyInterpretation = 0.0
            Log.d(
                TAG, "Formula interpretation for this specific Brick failed. (frequency)",
                interpretationException
            )
        }
        try {
            percentageInterpretation =
                if (pwmPercentageFormula == null) java.lang.Double.valueOf(0.0) else pwmPercentageFormula!!
                    .interpretDouble(scope)
        } catch (interpretationException: InterpretationException) {
            percentageInterpretation = 0.0
            Log.d(
                TAG, "Formula interpretation for this specific Brick failed. (percentage)",
                interpretationException
            )
        }
    }

    override fun update(percent: Float) {
        val connection = RaspberryPiService.getInstance().connection
        try {
            Log.d(
                TAG, "RPi pwm pin=" + pinInterpretation + ", " + percentageInterpretation
                    + "%, " + frequencyInterpretation + "Hz"
            )
            connection.setPWM(pinInterpretation, frequencyInterpretation, percentageInterpretation)
        } catch (e: Exception) {
            Log.e(TAG, "RPi: exception during setPwm: $e")
        }
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    fun setPinNumberFormula(pinNumberFormula: Formula?) {
        this.pinNumberFormula = pinNumberFormula
    }

    fun setPwmFrequencyFormula(pwmFrequencyFormula: Formula?) {
        this.pwmFrequencyFormula = pwmFrequencyFormula
    }

    fun setPwmPercentageFormula(pwmPercentageFormula: Formula?) {
        this.pwmPercentageFormula = pwmPercentageFormula
    }

    companion object {
        private val TAG = RaspiPwmAction::class.java.simpleName
    }
}