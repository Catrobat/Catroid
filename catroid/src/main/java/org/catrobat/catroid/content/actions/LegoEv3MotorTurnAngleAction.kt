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
import org.catrobat.catroid.content.bricks.LegoEv3MotorTurnAngleBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

class LegoEv3MotorTurnAngleAction : TemporalAction() {
    private var motorEnum: LegoEv3MotorTurnAngleBrick.Motor? = null
    private var degrees: Formula? = null
    private var scope: Scope? = null
    private val btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE)
    override fun update(percent: Float) {
        var degreesValue: Int
        try {
            degreesValue = degrees!!.interpretInteger(scope)
        } catch (interpretationException: InterpretationException) {
            degreesValue = 0
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
        var tmpAngle = degreesValue
        var direction = 1
        if (degreesValue < 0) {
            direction = -1
            tmpAngle = degreesValue + -2 * degreesValue
        }
        var step2Angle = 0
        var step3Angle = 0
        if (tmpAngle > POWER_DOWN_RAMP_DEGREES) {
            step2Angle = tmpAngle - POWER_DOWN_RAMP_DEGREES
            step3Angle = POWER_DOWN_RAMP_DEGREES
        } else {
            step2Angle = tmpAngle
        }
        val ev3 = btService.getDevice(BluetoothDevice.LEGO_EV3)
            ?: return
        var outputField = 0x00.toByte()
        when (motorEnum) {
            LegoEv3MotorTurnAngleBrick.Motor.MOTOR_A -> outputField = 0x01.toByte()
            LegoEv3MotorTurnAngleBrick.Motor.MOTOR_B -> outputField = 0x02.toByte()
            LegoEv3MotorTurnAngleBrick.Motor.MOTOR_C -> outputField = 0x04.toByte()
            LegoEv3MotorTurnAngleBrick.Motor.MOTOR_D -> outputField = 0x08.toByte()
            LegoEv3MotorTurnAngleBrick.Motor.MOTOR_B_C -> outputField = 0x06.toByte()
        }
        ev3.moveMotorStepsSpeed(
            outputField,
            0,
            direction * MAX_SPEED,
            0,
            step2Angle,
            step3Angle,
            true
        )
    }

    fun setMotorEnum(motorEnum: LegoEv3MotorTurnAngleBrick.Motor?) {
        this.motorEnum = motorEnum
    }

    fun setDegrees(degrees: Formula?) {
        this.degrees = degrees
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    companion object {
        private const val MAX_SPEED = 100
        private const val POWER_DOWN_RAMP_DEGREES = 20
    }
}