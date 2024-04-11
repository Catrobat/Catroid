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
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXT
import org.catrobat.catroid.devices.mindstorms.nxt.NXTMotor
import org.catrobat.catroid.formulaeditor.Sensors
import java.util.UUID

class LegoNXTTestable : LegoNXT {

    private val testUUID: UUID = UUID.randomUUID()

    var alive: Boolean = false

    var motorA: NXTMotorTestable? = null
    var motorB: NXTMotorTestable? = null
    var motorC: NXTMotorTestable? = null

    var legoSensor1: LegoSensor? = null
    var legoSensor2: LegoSensor? = null
    var legoSensor3: LegoSensor? = null
    var legoSensor4: LegoSensor? = null

    var isInitialized = false

    var mindstormsConnection: MindstormsConnection? = null

    var lastFrequency: Int? = null
    var lastToneDuration: Int? = null
    var sensorKeepAliveTime = 0
    var batteryStatus = 0

    override fun getName(): String = "Lego NXT Testable"

    override fun getDeviceType(): Class<out BluetoothDevice> = BluetoothDevice.LEGO_NXT

    override fun setConnection(connection: BluetoothConnection?) {
        mindstormsConnection = MindstormsConnectionImpl(connection)
    }

    override fun getBluetoothDeviceUUID(): UUID = testUUID

    override fun isAlive(): Boolean = alive

    override fun disconnect() {
        if (mindstormsConnection?.isConnected == true) {
            mindstormsConnection!!.disconnect()
        }
    }

    override fun playTone(frequency: Int, duration: Int) {
        lastFrequency = frequency
        lastToneDuration = duration
    }

    override fun getKeepAliveTime(): Int = sensorKeepAliveTime

    override fun getBatteryLevel(): Int = batteryStatus

    override fun getMotorA(): NXTMotor? = motorA

    override fun getMotorB(): NXTMotor? = motorB

    override fun getMotorC(): NXTMotor? = motorC

    override fun stopAllMovements() {
        motorA!!.stop()
        motorB!!.stop()
        motorC!!.stop()
    }

    @Synchronized
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
        legoSensor1 = null
        legoSensor2 = null
        legoSensor3 = null
        legoSensor4 = null
        mindstormsConnection!!.disconnect()
        mindstormsConnection = null
    }

    fun setSensor(sensorNew: LegoSensorTestable?, sensorNumber: Int) {
        when (sensorNumber) {
            sensorPort1 -> legoSensor1 = sensorNew
            sensorPort2 -> legoSensor2 = sensorNew
            sensorPort3 -> legoSensor3 = sensorNew
            sensorPort4 -> legoSensor4 = sensorNew
        }
    }

    companion object {
        const val sensorPort1 = 1
        const val sensorPort2 = 2
        const val sensorPort3 = 3
        const val sensorPort4 = 4
    }
}
