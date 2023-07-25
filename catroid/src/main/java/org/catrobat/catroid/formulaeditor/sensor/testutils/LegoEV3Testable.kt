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
import org.catrobat.catroid.devices.mindstorms.ev3.EV3Motor
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3
import org.catrobat.catroid.formulaeditor.Sensors
import java.util.UUID

class LegoEV3Testable : LegoEV3 {

    private val testUUID: UUID = UUID.fromString("00001101-0000-1000-Test0-00805F9B34FB")

    var motorA: EV3MotorTestable? = null
    var motorB: EV3MotorTestable? = null
    var motorC: EV3MotorTestable? = null
    var motorD: EV3MotorTestable? = null

    var legoSensor1: LegoSensor? = null
    var legoSensor2: LegoSensor? = null
    var legoSensor3: LegoSensor? = null
    var legoSensor4: LegoSensor? = null

    var isInitialized = false

    override fun getName(): String = "Lego EV3 Testable"
    override fun getDeviceType(): Class<out BluetoothDevice> {
        TODO("Not yet implemented 1")
    }

    override fun playTone(frequency: Int, duration: Int, volumeInPercent: Int) {
        TODO("Not yet implemented 1")
    }

    override fun getMotorA(): EV3Motor? = motorA

    override fun getMotorB(): EV3Motor? = motorB

    override fun getMotorC(): EV3Motor? = motorC

    override fun getMotorD(): EV3Motor? = motorD

    override fun setConnection(connection: BluetoothConnection?) {
        TODO("Not yet implemented 1")
    }

    override fun isAlive(): Boolean {
        TODO("Not yet implemented1")
    }

    override fun getSensorValue(sensor: Sensors): Float {
        when (sensor) {
            Sensors.EV3_SENSOR_1 -> return if (legoSensor1 != null) legoSensor1!!.lastSensorValue else 0f
            Sensors.EV3_SENSOR_2 -> return if (legoSensor2 != null) legoSensor2!!.lastSensorValue else 0f
            Sensors.EV3_SENSOR_3 -> return if (legoSensor3 != null) legoSensor3!!.lastSensorValue else 0f
            Sensors.EV3_SENSOR_4 -> return if (legoSensor4 != null) legoSensor4!!.lastSensorValue else 0f
        }
        return -1f
    }

    override fun getSensor1(): LegoSensor? = legoSensor1
    override fun getSensor2(): LegoSensor? = legoSensor2
    override fun getSensor3(): LegoSensor? = legoSensor3
    override fun getSensor4(): LegoSensor? = legoSensor4

    override fun initialise() {
        synchronized(this) {
            if (isInitialized) {
                return
            }

            motorA = EV3MotorTestable(0)
            motorB = EV3MotorTestable(1)
            motorC = EV3MotorTestable(2)
            motorD = EV3MotorTestable(3)

            createSensors()

            isInitialized = true
        }
    }

    private fun createSensors() {
        legoSensor1 = EV3SensorTestable()
        legoSensor2 = EV3SensorTestable()
        legoSensor3 = EV3SensorTestable()
        legoSensor4 = EV3SensorTestable()
    }

    override fun start() {
        initialise()
    }

    override fun pause() {
        TODO("Not yet implemented 1")
    }

    override fun destroy() {
        TODO("Not yet implemented")
    }

    override fun stopAllMovements() {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun moveMotorSpeed(outputField: Byte, chainLayer: Int, speed: Int) {
        TODO("Not yet implemented")
    }

    override fun stopMotor(outputField: Byte, chainLayer: Int, brake: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setLed(ledStatus: Int) {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }

    override fun getBluetoothDeviceUUID(): UUID = testUUID
}
