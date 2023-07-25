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
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorObjectBackgroundNameTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorObjectLookNameTest
import org.catrobat.catroid.formulaeditor.sensor.testutils.LookDataTestable
import org.junit.Assert
import org.junit.Test

class SensorObjectNameSensorsTest {

    @Test
    fun backgroundLookDataNotNull() {
        val expectedValue = "look name"
        val listLength = 4
        val testData = LookDataTestable(1)
        testData.name = expectedValue
        val list = createList(listLength, "false name", "false name")
        SensorObjectBackgroundNameTest.getInstance().setData(testData)
        SensorObjectBackgroundNameTest.getInstance().setLookList(list)
        compareToSensor(expectedValue, SensorObjectBackgroundNameTest.getInstance())
    }

    @Test
    fun backgroundLookDataNullListNotEmpty() {
        val expectedValue = "look name"
        val listLength = 4
        val testData = null
        val list = createList(listLength, expectedValue, "false name")
        SensorObjectBackgroundNameTest.getInstance().setData(testData)
        SensorObjectBackgroundNameTest.getInstance().setLookList(list)
        compareToSensor(expectedValue, SensorObjectBackgroundNameTest.getInstance())
    }

    @Test
    fun backgroundLookDataNullListEmpty() {
        val expectedValue = ""
        val listLength = 0
        val testData = null
        val list = createList(listLength, "false name", "false name")
        SensorObjectBackgroundNameTest.getInstance().setData(testData)
        SensorObjectBackgroundNameTest.getInstance().setLookList(list)
        compareToSensor(expectedValue, SensorObjectBackgroundNameTest.getInstance())
    }

    @Test
    fun objectLookDataNotNull() {
        val expectedValue = "look name"
        val listLength = 6
        val testData = LookDataTestable(1)
        testData.name = expectedValue
        val list = createList(listLength, "false name", "false name")
        SensorObjectLookNameTest.getInstance().setData(testData)
        SensorObjectLookNameTest.getInstance().setLookList(list)
        compareToSensor(expectedValue, SensorObjectLookNameTest.getInstance())
    }

    @Test
    fun objectLookDataNullListNotEmpty() {
        val expectedValue = "look name"
        val listLength = 7
        val testData = null
        val list = createList(listLength, expectedValue, "false name")
        SensorObjectLookNameTest.getInstance().setData(testData)
        SensorObjectLookNameTest.getInstance().setLookList(list)
        compareToSensor(expectedValue, SensorObjectLookNameTest.getInstance())
    }

    @Test
    fun objectLookDataNullListEmpty() {
        val expectedValue = ""
        val listLength = 0
        val testData = null
        val list = createList(listLength, "false name", "false name")
        SensorObjectLookNameTest.getInstance().setData(testData)
        SensorObjectLookNameTest.getInstance().setLookList(list)
        compareToSensor(expectedValue, SensorObjectLookNameTest.getInstance())
    }

    private fun createList(length: Int, firstName: String, otherName: String): List<LookData?> {
        val list: MutableList<LookData?> = mutableListOf()
        if (length > 0) {
            val firstElement = LookDataTestable(2)
            firstElement.name = firstName
            list.add(firstElement)
            repeat(length - 1) {
                val element = LookDataTestable(2)
                element.name = otherName
                list.add(element)
            }
        }
        return list
    }

    private fun compareToSensor(value: String, sensor: Sensor) {
        Assert.assertEquals(value, sensor.getSensorValue() as String)
    }
}
