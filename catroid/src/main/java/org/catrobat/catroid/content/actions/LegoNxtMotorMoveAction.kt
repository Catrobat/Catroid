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
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

class LegoNxtMotorMoveAction : TemporalAction() {
    private val btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE)
    private var motorEnum: LegoNxtMotorMoveBrick.Motor? = null
    private var speed: Formula? = null
    private var scope: Scope? = null
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
        val nxt = btService.getDevice(BluetoothDevice.LEGO_NXT)
            ?: return
        when (motorEnum) {
            LegoNxtMotorMoveBrick.Motor.MOTOR_A -> nxt.motorA.move(speedValue)
            LegoNxtMotorMoveBrick.Motor.MOTOR_B -> nxt.motorB.move(speedValue)
            LegoNxtMotorMoveBrick.Motor.MOTOR_C -> nxt.motorC.move(speedValue)
            LegoNxtMotorMoveBrick.Motor.MOTOR_B_C -> {
                nxt.motorB.move(speedValue)
                nxt.motorC.move(speedValue)
            }
        }
    }

    fun setMotorEnum(motorEnum: LegoNxtMotorMoveBrick.Motor?) {
        this.motorEnum = motorEnum
    }

    fun setSpeed(speed: Formula?) {
        this.speed = speed
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    companion object {
        private const val MIN_SPEED = -100
        private const val MAX_SPEED = 100
    }
}