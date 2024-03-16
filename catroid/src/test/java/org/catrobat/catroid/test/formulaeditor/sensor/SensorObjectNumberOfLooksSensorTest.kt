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

import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.formulaeditor.sensor.Sensor
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorObjectNumberOfLooksTest
import org.catrobat.catroid.formulaeditor.sensor.testutils.LookDataTestable
import org.junit.Assert
import org.junit.Test

class SensorObjectNumberOfLooksSensorTest {

    @Test
    fun lookDataNotNullIsWebRequest() {
        val expectedValue = 5
        val testData = LookDataTestable(1)
        testData.isWebRequest = true
        val list = createList(expectedValue, false, true)
        SensorObjectNumberOfLooksTest.getInstance().setData(testData)
        SensorObjectNumberOfLooksTest.getInstance().setLookList(list)
        compareToSensor(expectedValue + 1, SensorObjectNumberOfLooksTest.getInstance())
    }

    @Test
    fun lookDataNotNullIsNotWebRequest() {
        val expectedValue = 5
        val testData = LookDataTestable(1)
        testData.isWebRequest = false
        val list = createList(expectedValue, false, false)
        SensorObjectNumberOfLooksTest.getInstance().setData(testData)
        SensorObjectNumberOfLooksTest.getInstance().setLookList(list)
        compareToSensor(expectedValue, SensorObjectNumberOfLooksTest.getInstance())
    }

    @Test
    fun lookDataNullListNotEmptyIsWebRequest() {
        val expectedValue = 8
        val testData = null
        val list = createList(expectedValue, false, true)
        SensorObjectNumberOfLooksTest.getInstance().setData(testData)
        SensorObjectNumberOfLooksTest.getInstance().setLookList(list)
        compareToSensor(expectedValue + 1, SensorObjectNumberOfLooksTest.getInstance())
    }

    @Test
    fun lookDataNullListNotEmptyIsNotWebRequest() {
        val expectedValue = 14
        val testData = null
        val list = createList(expectedValue, false, false)
        SensorObjectNumberOfLooksTest.getInstance().setData(testData)
        SensorObjectNumberOfLooksTest.getInstance().setLookList(list)
        compareToSensor(expectedValue, SensorObjectNumberOfLooksTest.getInstance())
    }

    @Test
    fun lookDataNullLIsEmpty() {
        val expectedValue = 0
        val testData = null
        val list = createList(expectedValue, false, false)
        SensorObjectNumberOfLooksTest.getInstance().setData(testData)
        SensorObjectNumberOfLooksTest.getInstance().setLookList(list)
        compareToSensor(expectedValue, SensorObjectNumberOfLooksTest.getInstance())
    }

    private fun createList(length: Int, firstIsNull: Boolean, isWebrequest: Boolean):
        List<LookData?> {
        val list: MutableList<LookData?> = mutableListOf()
        if (length != 0) {
            var len = length
            if (firstIsNull) {
                list.add(null)
                len -= 1
            }
            repeat(len) {
                val data = LookDataTestable(2)
                data.isWebRequest = isWebrequest
                list.add(data)
            }
        }
        return list
    }

    private fun compareToSensor(value: Int, sensor: Sensor) {
        Assert.assertEquals(value.toDouble(), sensor.getSensorValue() as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01
    }
}
