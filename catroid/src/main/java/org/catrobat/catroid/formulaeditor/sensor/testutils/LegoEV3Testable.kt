/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.formulaeditor.sensor.testutils

import org.catrobat.catroid.bluetooth.base.BluetoothConnection
import org.catrobat.catroid.bluetooth.base.BluetoothDevice
import org.catrobat.catroid.devices.mindstorms.LegoSensor
import org.catrobat.catroid.devices.mindstorms.MindstormsConnection
import org.catrobat.catroid.devices.mindstorms.MindstormsConnectionImpl
import org.catrobat.catroid.devices.mindstorms.ev3.EV3Motor
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3
import org.catrobat.catroid.formulaeditor.Sensors
import java.util.UUID

class LegoEV3Testable : LegoEV3 {

    private val testUUID: UUID = UUID.randomUUID()

    var alive: Boolean = false

    var motorA: EV3MotorTestable? = null
    var motorB: EV3MotorTestable? = null
    var motorC: EV3MotorTestable? = null
    var motorD: EV3MotorTestable? = null

    var legoSensor1: LegoSensor? = null
    var legoSensor2: LegoSensor? = null
    var legoSensor3: LegoSensor? = null
    var legoSensor4: LegoSensor? = null

    var isInitialized = false
    var lastFrequency: Int? = null
    var lastToneDuration: Int? = null
    var lastVolume: Int? = null
    var mindstormsConnection: MindstormsConnection? = null
    var ledValue: Int? = null

    var speedA: Int? = null
    var chainlayerA: Int? = null
    var step1TachoA: Int? = null
    var step2TachoA: Int? = null
    var step3TachoA: Int? = null
    var brakeA: Boolean? = null

    var speedB: Int? = null
    var chainlayerB: Int? = null
    var step1TachoB: Int? = null
    var step2TachoB: Int? = null
    var step3TachoB: Int? = null
    var brakeB: Boolean? = null

    var speedC: Int? = null
    var chainlayerC: Int? = null
    var step1TachoC: Int? = null
    var step2TachoC: Int? = null
    var step3TachoC: Int? = null
    var brakeC: Boolean? = null

    var speedD: Int? = null
    var chainlayerD: Int? = null
    var step1TachoD: Int? = null
    var step2TachoD: Int? = null
    var step3TachoD: Int? = null
    var brakeD: Boolean? = null

    override fun getName(): String = "Lego EV3 Testable"
    override fun getDeviceType(): Class<out BluetoothDevice> = BluetoothDevice.LEGO_EV3

    override fun playTone(frequency: Int, duration: Int, volumeInPercent: Int) {
        lastFrequency = frequency
        lastToneDuration = duration
        lastVolume = volumeInPercent
    }

    override fun getMotorA(): EV3Motor? = motorA

    override fun getMotorB(): EV3Motor? = motorB

    override fun getMotorC(): EV3Motor? = motorC

    override fun getMotorD(): EV3Motor? = motorD

    override fun setConnection(connection: BluetoothConnection?) {
        mindstormsConnection = MindstormsConnectionImpl(connection)
    }

    override fun isAlive(): Boolean = alive

    override fun getSensorValue(sensor: Sensors): Float {
        return when (sensor) {
            Sensors.EV3_SENSOR_1 -> return if (legoSensor1 != null) legoSensor1!!.lastSensorValue else 0f
            Sensors.EV3_SENSOR_2 -> return if (legoSensor2 != null) legoSensor2!!.lastSensorValue else 0f
            Sensors.EV3_SENSOR_3 -> return if (legoSensor3 != null) legoSensor3!!.lastSensorValue else 0f
            Sensors.EV3_SENSOR_4 -> return if (legoSensor4 != null) legoSensor4!!.lastSensorValue else 0f
            else -> -1f
        }
    }

    override fun getSensor1(): LegoSensor? = legoSensor1
    override fun getSensor2(): LegoSensor? = legoSensor2
    override fun getSensor3(): LegoSensor? = legoSensor3
    override fun getSensor4(): LegoSensor? = legoSensor4

    override fun initialise() {
        if (isInitialized) {
            return
        }
        isInitialized = true
    }

    fun setSensor(sensorNew: LegoSensorTestable?, sensorNumber: Int) {
        when (sensorNumber) {
            sensorPort1 -> legoSensor1 = sensorNew
            sensorPort2 -> legoSensor2 = sensorNew
            sensorPort3 -> legoSensor3 = sensorNew
            sensorPort4 -> legoSensor4 = sensorNew
        }
    }

    override fun start() {
        initialise()
    }

    override fun pause() {
        stopAllMovements()
    }

    override fun destroy() {
        motorA = null
        motorB = null
        motorC = null
        motorD = null
        legoSensor1 = null
        legoSensor2 = null
        legoSensor3 = null
        legoSensor4 = null
        mindstormsConnection!!.disconnect()
        mindstormsConnection = null
    }

    override fun stopAllMovements() {
        motorA!!.stop()
        motorB!!.stop()
        motorC!!.stop()
        motorD!!.stop()
    }

    override fun moveMotorStepsSpeed(
        outputField: Byte,
        chainLayer: Int,
        speed: Int,
        step1Tacho: Int,
        step2Tacho: Int,
        step3Tacho: Int,
        brake: Boolean
    ) {
        when (outputField.toInt()) {
            sensorPort1 -> {
                chainlayerA = chainLayer
                speedA = speed
                step1TachoA = step1Tacho
                step2TachoA = step2Tacho
                step3TachoA = step3Tacho
                brakeA = brake
            }

            sensorPort2 -> {
                chainlayerB = chainLayer
                speedB = speed
                step1TachoB = step1Tacho
                step2TachoB = step2Tacho
                step3TachoB = step3Tacho
                brakeB = brake
            }

            sensorPort3 -> {
                chainlayerC = chainLayer
                speedC = speed
                step1TachoC = step1Tacho
                step2TachoC = step2Tacho
                step3TachoC = step3Tacho
                brakeC = brake
            }

            sensorPort4 -> {
                chainlayerD = chainLayer
                speedD = speed
                step1TachoD = step1Tacho
                step2TachoD = step2Tacho
                step3TachoD = step3Tacho
                brakeD = brake
            }
        }
    }

    override fun moveMotorSpeed(outputField: Byte, chainLayer: Int, speed: Int) {
        when (outputField.toInt()) {
            sensorPort1 -> {
                chainlayerA = chainLayer
                speedA = speed
            }

            sensorPort2 -> {
                chainlayerB = chainLayer
                speedB = speed
            }

            sensorPort3 -> {
                chainlayerC = chainLayer
                speedC = speed
            }

            sensorPort4 -> {
                chainlayerD = chainLayer
                speedD = speed
            }
        }
    }

    override fun stopMotor(outputField: Byte, chainLayer: Int, brake: Boolean) {
        when (outputField.toInt()) {
            sensorPort1 -> {
                chainlayerA = chainLayer
                brakeA = brake
            }

            sensorPort2 -> {
                chainlayerB = chainLayer
                brakeB = brake
            }

            sensorPort3 -> {
                chainlayerC = chainLayer
                brakeC = brake
            }

            sensorPort4 -> {
                chainlayerD = chainLayer
                brakeD = brake
            }
        }
    }

    override fun setLed(ledStatus: Int) {
        ledValue = ledStatus
    }

    override fun disconnect() {
        if (mindstormsConnection?.isConnected == true) {
            mindstormsConnection!!.disconnect()
        }
    }

    override fun getBluetoothDeviceUUID(): UUID = testUUID

    companion object {
        const val sensorPort1 = 1
        const val sensorPort2 = 2
        const val sensorPort3 = 3
        const val sensorPort4 = 4
    }
}
