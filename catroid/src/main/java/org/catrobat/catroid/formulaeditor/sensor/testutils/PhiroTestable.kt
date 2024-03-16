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
import org.catrobat.catroid.bluetooth.base.BluetoothDevice.PHIRO
import org.catrobat.catroid.devices.arduino.phiro.Phiro
import org.catrobat.catroid.formulaeditor.Sensors
import java.util.UUID

class PhiroTestable : Phiro {
    private val testUUID: UUID = UUID.randomUUID()

    var isAliveValue = false

    var bottomLeftValue = 0
    var bottomRightValue = 0
    var frontLeftValue = 0
    var frontRightValue = 0
    var sideLeftValue = 0
    var sideRightValue = 0

    var leftMotorSpeed = 0
    var rightMotorSpeed = 0

    var leftLED = arrayOf(0, 0, 0)
    var rightLED = arrayOf(0, 0, 0)
    var isInitialized = false

    var lastFrequency: Int? = null
    var lastToneDuration: Int? = null

    var currentFirmwareVersion = "Current Firmware Version"
    var nextFirmwareVersion = "Next Firmware Version"
    var btConnection: BluetoothConnection? = null

    override fun getBluetoothDeviceUUID(): UUID = testUUID

    override fun playTone(toneFrequency: Int, duration: Int) {
        lastFrequency = toneFrequency
        lastToneDuration = duration
    }

    override fun moveLeftMotorForward(speed: Int) {
        leftMotorSpeed += speed
    }

    override fun moveLeftMotorBackward(speed: Int) {
        leftMotorSpeed -= speed
    }

    override fun moveRightMotorForward(speed: Int) {
        rightMotorSpeed += speed
    }

    override fun moveRightMotorBackward(speed: Int) {
        rightMotorSpeed -= speed
    }

    override fun stopLeftMotor() {
        leftMotorSpeed = 0
    }

    override fun stopRightMotor() {
        rightMotorSpeed = 0
    }

    override fun stopAllMovements() {
        stopLeftMotor()
        stopRightMotor()
    }

    override fun setLeftRGBLightColor(red: Int, green: Int, blue: Int) {
        leftLED[0] = red
        leftLED[1] = green
        leftLED[2] = blue
    }

    override fun setRightRGBLightColor(red: Int, green: Int, blue: Int) {
        rightLED[0] = red
        rightLED[1] = green
        rightLED[2] = blue
    }

    override fun reportFirmwareVersion() {
        currentFirmwareVersion = nextFirmwareVersion
    }

    override fun getName(): String = "Phiro Testable"

    override fun getDeviceType(): Class<out BluetoothDevice> = PHIRO

    override fun setConnection(connection: BluetoothConnection?) {
        btConnection = connection
    }

    override fun disconnect() {
        btConnection?.disconnect()
    }

    override fun isAlive(): Boolean = isAliveValue

    override fun getSensorValue(sensor: Sensors?): Int {
        return when (sensor) {
            Sensors.PHIRO_BOTTOM_LEFT -> bottomLeftValue
            Sensors.PHIRO_BOTTOM_RIGHT -> bottomRightValue
            Sensors.PHIRO_FRONT_LEFT -> frontLeftValue
            Sensors.PHIRO_FRONT_RIGHT -> frontRightValue
            Sensors.PHIRO_SIDE_LEFT -> sideLeftValue
            Sensors.PHIRO_SIDE_RIGHT -> sideRightValue
            else -> 0
        }
    }

    fun setSensorValue(sensor: Sensors, value: Int) {
        when (sensor) {
            Sensors.PHIRO_BOTTOM_LEFT -> bottomLeftValue = value
            Sensors.PHIRO_BOTTOM_RIGHT -> bottomRightValue = value
            Sensors.PHIRO_FRONT_LEFT -> frontLeftValue = value
            Sensors.PHIRO_FRONT_RIGHT -> frontRightValue = value
            Sensors.PHIRO_SIDE_LEFT -> sideLeftValue = value
            Sensors.PHIRO_SIDE_RIGHT -> sideRightValue = value
        }
    }

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
        stopAllMovements()
        leftLED[0] = 0
        leftLED[1] = 0
        leftLED[2] = 0
        rightLED[0] = 0
        rightLED[2] = 0
        rightLED[2] = 0
        lastFrequency = null
        lastToneDuration = null
    }
}
