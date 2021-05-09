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
import org.catrobat.catroid.devices.arduino.ArduinoImpl
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

class ArduinoSendDigitalValueAction : TemporalAction() {
    private var pinNumber: Formula? = null
    private var pinValue: Formula? = null
    private var scope: Scope? = null
    private var pin = 0
    private var value = 0
    private var restart = false
    override fun begin() {
        var pinNumberInterpretation: Int
        var pinValueInterpretation: Int
        try {
            pinNumberInterpretation =
                if (pinNumber == null) Integer.valueOf(0) else pinNumber!!.interpretInteger(scope)
        } catch (interpretationException: InterpretationException) {
            pinNumberInterpretation = 0
            Log.d(
                javaClass.simpleName, "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
        try {
            pinValueInterpretation =
                if (pinValue == null) Integer.valueOf(0) else pinValue!!.interpretInteger(scope)
        } catch (interpretationException: InterpretationException) {
            pinValueInterpretation = 0
            Log.d(
                javaClass.simpleName, "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
        if (!restart
            && pinNumberInterpretation >= 0
            && pinNumberInterpretation < ArduinoImpl.NUMBER_OF_DIGITAL_PINS
        ) {
            pin = pinNumberInterpretation
            value = pinValueInterpretation
        }
        restart = false
    }

    override fun update(percent: Float) {
        val arduino = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).getDevice(
            BluetoothDevice.ARDUINO
        )
        arduino?.setDigitalArduinoPin(pin, value)
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    fun setPinNumber(newPinNumber: Formula?) {
        pinNumber = newPinNumber
    }

    fun setPinValue(newpinValue: Formula?) {
        pinValue = newpinValue
    }
}