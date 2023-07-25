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

package org.catrobat.catroid.test.formulaeditor.sensor

import org.catrobat.catroid.cast.CastManager
import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate

@RunWith(PowerMockRunner::class)
@PowerMockRunnerDelegate(Parameterized::class)
@PrepareForTest(CastManager::class)
class SensorGamePadSensorsTest(
    private val name: String,
    private val sensor: Sensors,
    private val returnValue: Boolean,
    private val expectedValue: Int
) {

    @Before
    fun setup() {
        mockCastManager = mock(CastManager::class.java)
        PowerMockito.`when`(mockCastManager.isButtonPressed(sensor)).thenReturn(returnValue)
        PowerMockito.mockStatic(CastManager::class.java)
        PowerMockito.`when`(CastManager.getInstance()).thenReturn(mockCastManager)
    }

    @Test
    fun objectSensorsTest() {
        compareToSensor(expectedValue, sensor)
    }

    private fun compareToSensor(value: Int, sensor: Sensors) {
        Assert.assertEquals(value.toDouble(), sensor.getSensor().getSensorValue() as Double, DELTA)
    }

    companion object {

        lateinit var mockCastManager: CastManager

        private const val DELTA = 0.01

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("gamePadButtonAPressed", Sensors.GAMEPAD_A_PRESSED, true, 1),
            arrayOf("gamePadButtonANotPressed", Sensors.GAMEPAD_A_PRESSED, false, 0),
            arrayOf("gamePadButtonBPressed", Sensors.GAMEPAD_B_PRESSED, true, 1),
            arrayOf("gamePadButtonBNotPressed", Sensors.GAMEPAD_B_PRESSED, false, 0),
            arrayOf("gamePadButtonUPPressed", Sensors.GAMEPAD_UP_PRESSED, true, 1),
            arrayOf("gamePadButtonUPNotPressed", Sensors.GAMEPAD_UP_PRESSED, false, 0),
            arrayOf("gamePadButtonDOWNPressed", Sensors.GAMEPAD_DOWN_PRESSED, true, 1),
            arrayOf("gamePadButtonDOWNNotPressed", Sensors.GAMEPAD_DOWN_PRESSED, false, 0),
            arrayOf("gamePadButtonLEFTPressed", Sensors.GAMEPAD_LEFT_PRESSED, true, 1),
            arrayOf("gamePadButtonLEFTNotPressed", Sensors.GAMEPAD_LEFT_PRESSED, false, 0),
            arrayOf("gamePadButtonRIGHTPressed", Sensors.GAMEPAD_RIGHT_PRESSED, true, 1),
            arrayOf("gamePadButtonRIGHTNotPressed", Sensors.GAMEPAD_RIGHT_PRESSED, false, 0),

            )
    }
}
