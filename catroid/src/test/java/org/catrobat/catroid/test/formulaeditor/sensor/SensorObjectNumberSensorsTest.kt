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
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorObjectBackgroundNumberTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorObjectLookNumberTest
import org.catrobat.catroid.formulaeditor.sensor.testutils.LookDataTestable
import org.junit.Assert
import org.junit.Test

class SensorObjectNumberSensorsTest {

    @Test
    fun backgroundLookDataNotNull() {
        val expectedValue = 34
        val testData = LookDataTestable(1)
        val list = createList(testData, expectedValue)
        SensorObjectBackgroundNumberTest.getInstance().setData(testData)
        SensorObjectBackgroundNumberTest.getInstance().setLookList(list)
        compareToSensor(expectedValue, SensorObjectBackgroundNumberTest.getInstance())
    }

    @Test
    fun backgroundLookDataNullListNotEmpty() {

        val expectedValue = 1
        val testData = null
        val list = createList(testData, expectedValue)
        SensorObjectBackgroundNumberTest.getInstance().setData(testData)
        SensorObjectBackgroundNumberTest.getInstance().setLookList(list)
        compareToSensor(expectedValue, SensorObjectBackgroundNumberTest.getInstance())
    }

    @Test
    fun backgroundLookDataNullListEmpty() {
        val expectedValue = 1
        val testData = null
        val list = listOf<LookDataTestable?>()
        SensorObjectBackgroundNumberTest.getInstance().setData(testData)
        SensorObjectBackgroundNumberTest.getInstance().setLookList(list)
        compareToSensor(expectedValue, SensorObjectBackgroundNumberTest.getInstance())
    }

    @Test
    fun objectLookDataNotNull() {
        val expectedValue = 36
        val testData = LookDataTestable(1)
        val list = createList(testData, expectedValue)
        SensorObjectLookNumberTest.getInstance().setData(testData)
        SensorObjectLookNumberTest.getInstance().setLookList(list)
        compareToSensor(expectedValue, SensorObjectLookNumberTest.getInstance())
    }

    @Test
    fun objectLookDataNullListNotEmpty() {
        val expectedValue = 1
        val testData = null
        val list = createList(testData, expectedValue)
        SensorObjectBackgroundNumberTest.getInstance().setData(testData)
        SensorObjectBackgroundNumberTest.getInstance().setLookList(list)
        compareToSensor(expectedValue, SensorObjectBackgroundNumberTest.getInstance())
    }

    @Test
    fun objectLookDataNullListEmpty() {
        val expectedValue = 1
        val testData = null
        val list = listOf<LookDataTestable?>()
        SensorObjectBackgroundNumberTest.getInstance().setData(testData)
        SensorObjectBackgroundNumberTest.getInstance().setLookList(list)
        compareToSensor(expectedValue, SensorObjectBackgroundNumberTest.getInstance())
    }

    private fun createList(data: LookData?, index: Int): List<LookData?> {
        val list: MutableList<LookData?> = mutableListOf()
        repeat(index - 1) {
            list.add(LookDataTestable(2))
        }
        list.add(data)
        return list
    }

    private fun compareToSensor(value: Int, sensor: Sensor) {
        Assert.assertEquals(value.toDouble(), sensor.getSensorValue() as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01
    }
}
