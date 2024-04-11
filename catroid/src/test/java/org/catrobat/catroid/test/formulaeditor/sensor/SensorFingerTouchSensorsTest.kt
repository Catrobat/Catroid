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

import org.catrobat.catroid.formulaeditor.sensor.Sensor
import org.catrobat.catroid.formulaeditor.sensor.SensorFingerTouched
import org.catrobat.catroid.formulaeditor.sensor.SensorFingerX
import org.catrobat.catroid.formulaeditor.sensor.SensorFingerY
import org.catrobat.catroid.formulaeditor.sensor.SensorLastFingerIndex
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorNumberCurrentTouchesTest
import org.catrobat.catroid.utils.TouchUtil
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SensorFingerTouchSensorsTest(
    private val name: String,
    private val sensor: Sensor,
    private val expectedValue: Int
) {

    @Before
    fun setup() {
        TouchUtil.reset()
    }

    private fun fingerTouchXTest() {
        TouchUtil.setDummyTouchForSensorTest(
            expectedValue.toFloat(), 0f, false
        )
        compareToSensor(expectedValue, sensor)
    }

    private fun fingerTouchYTest() {
        TouchUtil.setDummyTouchForSensorTest(
            0f, expectedValue.toFloat(), false
        )
        compareToSensor(expectedValue, sensor)
    }

    private fun fingerTouchedTest() {
        setTouchingList(5, expectedValue)
        compareToSensor(expectedValue, sensor)
    }

    private fun lastIndexFingerTest() {
        setTouchingList(expectedValue, 0)
        compareToSensor(expectedValue, sensor)
    }

    private fun numberOfCurrentTouchesTest() {
        fillCurrentlyTouchingList(expectedValue)
        if (sensor is SensorNumberCurrentTouchesTest) {
            val l: () -> Int = { expectedValue }
            sensor.lambda = l
        }
        compareToSensor(expectedValue, sensor)
    }

    @Test
    fun fingerTouchTest() {
        when (sensor) {
            fingerX -> fingerTouchXTest()
            fingerY -> fingerTouchYTest()
            fingerTouched -> fingerTouchedTest()
            lastFingerIndex -> lastIndexFingerTest()
            numberCurrentTouches -> numberOfCurrentTouchesTest()
        }
    }

    private fun setTouchingList(size: Int, shouldTouch: Int) {
        var touchIndex = -1
        if (shouldTouch != 0) {
            touchIndex = size
        }
        for (i in 1..size) {
            var touching = false
            if (i == touchIndex) {
                touching = true
            }
            TouchUtil.setDummyTouchForSensorTest(0f, 0f, touching)
        }
    }

    private fun fillCurrentlyTouchingList(size: Int) {
        for (i in 1..size) {
            TouchUtil.setTouchPointerDummyForTest(i, i)
        }
    }

    private fun compareToSensor(value: Int, sensor: Sensor) {
        Assert.assertEquals(value.toDouble(), sensor.getSensorValue() as Double, DELTA)
    }

    companion object {

        private const val DELTA = 0.01
        private const val touchIndexReturn = 4

        private val fingerX = SensorFingerX.getInstance()
        private val fingerY = SensorFingerY.getInstance()
        private val fingerTouched = SensorFingerTouched.getInstance()
        private val lastFingerIndex = SensorLastFingerIndex.getInstance()
        private val numberCurrentTouches = SensorNumberCurrentTouchesTest.getInstance()

        @JvmStatic
        @AfterClass
        fun tearDown() {
            TouchUtil.reset()
        }

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("fingerXX1", fingerX, 7),
            arrayOf("fingerXX2", fingerX, -7),
            arrayOf("fingerYY1", fingerY, 8),
            arrayOf("fingerYY2", fingerY, -8),
            arrayOf("fingerTouched0", fingerTouched, 0),
            arrayOf("fingerTouched1", fingerTouched, 1),
            arrayOf("lastFingerIndex", lastFingerIndex, touchIndexReturn),
            arrayOf("numberOfCurrentTouches1", numberCurrentTouches, 3),
            arrayOf("numberOfCurrentTouches2", numberCurrentTouches, 21),
        )
    }
}
