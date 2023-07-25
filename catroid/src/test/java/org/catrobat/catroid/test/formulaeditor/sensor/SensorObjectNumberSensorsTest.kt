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
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyObject
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(SensorHandler::class)
class SensorObjectNumberSensorsTest {

    @Test
    fun backgroundLookDataNotNull() {
        val expectedValue = 34
        val mockSprite = PowerMockito.mock(Sprite::class.java)
        val mockLook = PowerMockito.mock(Look::class.java)
        val mockLookList = PowerMockito.mock(List::class.java) as List<LookData>
        val mockLookData = PowerMockito.mock(LookData::class.java)
        PowerMockito.mockStatic(SensorHandler::class.java)
        mockSprite.look = mockLook
        SensorHandler.currentSprite = mockSprite
        PowerMockito.`when`(mockSprite.lookList).thenReturn(mockLookList)
        PowerMockito.`when`(mockLook.lookData).thenReturn(mockLookData)
        PowerMockito.`when`(mockLookList.indexOf(anyObject())).thenReturn(expectedValue - 1)
        compareToSensor(expectedValue, Sensors.OBJECT_BACKGROUND_NUMBER)
    }

    @Test
    fun backgroundLookDataNullListNotEmpty() {
        val expectedValue = 1
        val mockSprite = PowerMockito.mock(Sprite::class.java)
        val mockLook = PowerMockito.mock(Look::class.java)
        val mockLookList = PowerMockito.mock(List::class.java) as List<LookData>
        val mockLookData = PowerMockito.mock(LookData::class.java)
        PowerMockito.mockStatic(SensorHandler::class.java)
        mockSprite.look = mockLook
        SensorHandler.currentSprite = mockSprite
        PowerMockito.`when`(mockSprite.lookList).thenReturn(mockLookList)
        PowerMockito.`when`(mockLook.lookData).thenReturn(null)
        PowerMockito.`when`(mockLookList.isEmpty()).thenReturn(false)
        PowerMockito.`when`(mockLookList[0]).thenReturn(mockLookData)
        PowerMockito.`when`(mockLookList.indexOf(anyObject())).thenReturn(expectedValue - 1)
        compareToSensor(expectedValue, Sensors.OBJECT_BACKGROUND_NUMBER)
    }

    @Test
    fun backgroundLookDataNullListEmpty() {
        val expectedValue = 239
        val mockSprite = PowerMockito.mock(Sprite::class.java)
        val mockLook = PowerMockito.mock(Look::class.java)
        val mockLookList = PowerMockito.mock(List::class.java) as List<LookData>
        val mockLookData = PowerMockito.mock(LookData::class.java)
        PowerMockito.mockStatic(SensorHandler::class.java)
        mockSprite.look = mockLook
        SensorHandler.currentSprite = mockSprite
        PowerMockito.`when`(mockSprite.lookList).thenReturn(mockLookList)
        PowerMockito.`when`(mockLook.lookData).thenReturn(null)
        PowerMockito.`when`(mockLookList.isEmpty()).thenReturn(true)
        PowerMockito.`when`(mockLookList[0]).thenReturn(mockLookData)
        PowerMockito.`when`(mockLookList.indexOf(anyObject())).thenReturn(expectedValue - 1)
        compareToSensor(1, Sensors.OBJECT_BACKGROUND_NUMBER)
    }

    @Test
    fun objectLookDataNotNull() {
        val expectedValue = 34
        val mockSprite = PowerMockito.mock(Sprite::class.java)
        val mockLook = PowerMockito.mock(Look::class.java)
        val mockLookList = PowerMockito.mock(List::class.java) as List<LookData>
        val mockLookData = PowerMockito.mock(LookData::class.java)
        PowerMockito.mockStatic(SensorHandler::class.java)
        mockSprite.look = mockLook
        SensorHandler.currentSprite = mockSprite
        PowerMockito.`when`(mockSprite.lookList).thenReturn(mockLookList)
        PowerMockito.`when`(mockLook.lookData).thenReturn(mockLookData)
        PowerMockito.`when`(mockLookList.indexOf(anyObject())).thenReturn(expectedValue - 1)
        compareToSensor(expectedValue, Sensors.OBJECT_LOOK_NUMBER)
    }

    @Test
    fun objectLookDataNullListNotEmpty() {
        val expectedValue = 2
        val mockSprite = PowerMockito.mock(Sprite::class.java)
        val mockLook = PowerMockito.mock(Look::class.java)
        val mockLookList = PowerMockito.mock(List::class.java) as List<LookData>
        val mockLookData = PowerMockito.mock(LookData::class.java)
        PowerMockito.mockStatic(SensorHandler::class.java)
        mockSprite.look = mockLook
        SensorHandler.currentSprite = mockSprite
        PowerMockito.`when`(mockSprite.lookList).thenReturn(mockLookList)
        PowerMockito.`when`(mockLook.lookData).thenReturn(null)
        PowerMockito.`when`(mockLookList.isEmpty()).thenReturn(false)
        PowerMockito.`when`(mockLookList[0]).thenReturn(mockLookData)
        PowerMockito.`when`(mockLookList.indexOf(anyObject())).thenReturn(expectedValue - 1)
        compareToSensor(expectedValue, Sensors.OBJECT_LOOK_NUMBER)
    }

    @Test
    fun objectLookDataNullListEmpty() {
        val expectedValue = 269
        val mockSprite = PowerMockito.mock(Sprite::class.java)
        val mockLook = PowerMockito.mock(Look::class.java)
        val mockLookList = PowerMockito.mock(List::class.java) as List<LookData>
        val mockLookData = PowerMockito.mock(LookData::class.java)
        PowerMockito.mockStatic(SensorHandler::class.java)
        mockSprite.look = mockLook
        SensorHandler.currentSprite = mockSprite
        PowerMockito.`when`(mockSprite.lookList).thenReturn(mockLookList)
        PowerMockito.`when`(mockLook.lookData).thenReturn(null)
        PowerMockito.`when`(mockLookList.isEmpty()).thenReturn(true)
        PowerMockito.`when`(mockLookList[0]).thenReturn(mockLookData)
        PowerMockito.`when`(mockLookList.indexOf(anyObject())).thenReturn(expectedValue - 1)
        compareToSensor(1, Sensors.OBJECT_LOOK_NUMBER)
    }

    private fun compareToSensor(value: Int, sensor: Sensors) {
        Assert.assertEquals(value.toDouble(), sensor.getSensor().getSensorValue() as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01
    }
}
