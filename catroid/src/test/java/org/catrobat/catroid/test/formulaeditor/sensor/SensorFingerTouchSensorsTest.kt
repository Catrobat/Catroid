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

import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.utils.TouchUtil
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate

@RunWith(PowerMockRunner::class)
@PowerMockRunnerDelegate(Parameterized::class)
@PrepareForTest(TouchUtil::class)
class SensorFingerTouchSensorsTest(
    private val name: String,
    private val sensor: Sensors,
    private val expectedValue: Int
) {

    @Before
    fun setup() {
        PowerMockito.mockStatic(TouchUtil::class.java)
        PowerMockito.`when`(TouchUtil.getLastTouchIndex()).thenReturn(touchIndexReturn)
    }

    private fun fingerTouchXTest() {
        PowerMockito.`when`(TouchUtil.getX(touchIndexReturn)).thenReturn(expectedValue.toFloat())
        compareToSensor(expectedValue, sensor)
    }

    private fun fingerTouchYTest() {
        PowerMockito.`when`(TouchUtil.getY(touchIndexReturn)).thenReturn(expectedValue.toFloat())
        compareToSensor(expectedValue, sensor)
    }

    private fun fingerTouchedTest() {
        PowerMockito.`when`(TouchUtil.isTouching()).thenReturn(expectedValue.toDouble())
        compareToSensor(expectedValue, sensor)
    }

    private fun lastIndexFingerTest() {
        compareToSensor(expectedValue, Sensors.LAST_FINGER_INDEX)
    }

    private fun numberOfCurrentTouchesTest() {
        PowerMockito.`when`(TouchUtil.getNumberOfCurrentTouches()).thenReturn(expectedValue)
        compareToSensor(expectedValue, Sensors.NUMBER_CURRENT_TOUCHES)
    }

    @Test
    fun fingerTouchTest() {
        when (sensor) {
            Sensors.FINGER_X -> fingerTouchXTest()
            Sensors.FINGER_Y -> fingerTouchYTest()
            Sensors.FINGER_TOUCHED -> fingerTouchedTest()
            Sensors.LAST_FINGER_INDEX -> lastIndexFingerTest()
            Sensors.NUMBER_CURRENT_TOUCHES -> numberOfCurrentTouchesTest()
        }
    }

    private fun compareToSensor(value: Int, sensor: Sensors) {
        Assert.assertEquals(value.toDouble(), sensor.getSensor().getSensorValue() as Double, DELTA)
    }

    companion object {

        private const val DELTA = 0.01
        private const val touchIndexReturn = 4

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("fingerXX1", Sensors.FINGER_X, 7),
            arrayOf("fingerXX2", Sensors.FINGER_X, -7),
            arrayOf("fingerYY1", Sensors.FINGER_Y, 8),
            arrayOf("fingerYY2", Sensors.FINGER_Y, -8),
            arrayOf("fingerTouched0", Sensors.FINGER_TOUCHED, 0),
            arrayOf("fingerTouched1", Sensors.FINGER_TOUCHED, 1),
            arrayOf("lastFingerIndex", Sensors.LAST_FINGER_INDEX, touchIndexReturn),
            arrayOf("numberOfCurrentTouches1", Sensors.NUMBER_CURRENT_TOUCHES, 45),
            arrayOf("numberOfCurrentTouches2", Sensors.NUMBER_CURRENT_TOUCHES, 455),
        )
    }
}
