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
import org.catrobat.catroid.bluetooth.base.BluetoothDevice
import org.catrobat.catroid.common.CatroidService
import org.catrobat.catroid.common.ServiceProvider
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick.Eye
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

class PhiroRGBLightAction : TemporalAction() {
    private var eyeEnum: Eye? = null
    private var red: Formula? = null
    private var green: Formula? = null
    private var blue: Formula? = null
    private var scope: Scope? = null
    private val btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE)
    override fun update(percent: Float) {
        val redValue = updateFormulaValue(red)
        val greenValue = updateFormulaValue(green)
        val blueValue = updateFormulaValue(blue)
        val phiro = btService.getDevice(BluetoothDevice.PHIRO)
        if (eyeEnum == Eye.LEFT) {
            phiro.setLeftRGBLightColor(redValue, greenValue, blueValue)
        } else if (eyeEnum == Eye.RIGHT) {
            phiro.setRightRGBLightColor(redValue, greenValue, blueValue)
        } else if (eyeEnum == Eye.BOTH) {
            phiro.setLeftRGBLightColor(redValue, greenValue, blueValue)
            phiro.setRightRGBLightColor(redValue, greenValue, blueValue)
        } else {
            Log.d(TAG, "Error: EyeEnum:$eyeEnum")
        }
    }

    private fun updateFormulaValue(rgbFormula: Formula?): Int {
        var rgbValue: Int
        try {
            rgbValue = rgbFormula!!.interpretInteger(scope)
        } catch (interpretationException: InterpretationException) {
            rgbValue = 0
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
        if (rgbValue < MIN_VALUE) {
            rgbValue = MIN_VALUE
        } else if (rgbValue > MAX_VALUE) {
            rgbValue = MAX_VALUE
        }
        return rgbValue
    }

    fun setEyeEnum(eyeEnum: Eye?) {
        this.eyeEnum = eyeEnum
    }

    fun setRed(red: Formula?) {
        this.red = red
    }

    fun setGreen(green: Formula?) {
        this.green = green
    }

    fun setBlue(blue: Formula?) {
        this.blue = blue
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    companion object {
        private val TAG = PhiroRGBLightAction::class.java.simpleName
        private const val MIN_VALUE = 0
        private const val MAX_VALUE = 255
    }
}