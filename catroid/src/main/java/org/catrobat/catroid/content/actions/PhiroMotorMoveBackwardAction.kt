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
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

class PhiroMotorMoveBackwardAction : TemporalAction() {
    private var motorEnum: PhiroMotorMoveBackwardBrick.Motor? = null
    private var speed: Formula? = null
    private var scope: Scope? = null
    private val btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE)
    override fun update(percent: Float) {
        var speedValue: Int
        try {
            speedValue = speed!!.interpretInteger(scope)
        } catch (interpretationException: InterpretationException) {
            speedValue = 0
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
        if (speedValue < MIN_SPEED) {
            speedValue = MIN_SPEED
        } else if (speedValue > MAX_SPEED) {
            speedValue = MAX_SPEED
        }
        val phiro = btService.getDevice(BluetoothDevice.PHIRO)
            ?: return
        when (motorEnum) {
            PhiroMotorMoveBackwardBrick.Motor.MOTOR_LEFT -> phiro.moveLeftMotorBackward(speedValue)
            PhiroMotorMoveBackwardBrick.Motor.MOTOR_RIGHT -> phiro.moveRightMotorBackward(speedValue)
            PhiroMotorMoveBackwardBrick.Motor.MOTOR_BOTH -> {
                phiro.moveRightMotorBackward(speedValue)
                phiro.moveLeftMotorBackward(speedValue)
            }
        }
    }

    fun setMotorEnum(motorEnum: PhiroMotorMoveBackwardBrick.Motor?) {
        this.motorEnum = motorEnum
    }

    fun setSpeed(speed: Formula?) {
        this.speed = speed
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    companion object {
        private const val MIN_SPEED = 0
        private const val MAX_SPEED = 100
    }
}