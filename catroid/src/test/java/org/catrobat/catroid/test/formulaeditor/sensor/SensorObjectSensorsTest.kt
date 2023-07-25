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

import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mockito

@RunWith(Parameterized::class)
class SensorObjectSensorsTest(
    private val name: String,
    private val sensor: Sensors
) {

    @Test
    fun objectSensorsTest() {
        compareToSensor(expectedValue, sensor)
    }

    private fun compareToSensor(value: Int, sensor: Sensors) {
        Assert.assertEquals(value.toDouble(), sensor.getSensor().getSensorValue() as Double, DELTA)
    }

    companion object {

        private const val DELTA = 0.01
        lateinit var mockSprite: Sprite
        lateinit var mockLook: Look
        const val expectedValue = 53

        @BeforeClass
        @JvmStatic
        fun setup() {
            mockSprite = Mockito.mock(Sprite::class.java)
            mockLook = Mockito.mock(Look::class.java)
            SensorHandler.currentSprite = mockSprite
            mockSprite.look = mockLook
            Mockito.`when`(mockLook.angularVelocityInUserInterfaceDimensionUnit)
                .thenReturn(expectedValue.toFloat())
            Mockito.`when`(mockLook.brightnessInUserInterfaceDimensionUnit)
                .thenReturn(expectedValue.toFloat())
            Mockito.`when`(mockLook.colorInUserInterfaceDimensionUnit)
                .thenReturn(expectedValue.toFloat())
            Mockito.`when`(mockLook.distanceToTouchPositionInUserInterfaceDimensions)
                .thenReturn(expectedValue.toFloat())
            Mockito.`when`(mockLook.lookDirectionInUserInterfaceDimensionUnit)
                .thenReturn(expectedValue.toFloat())
            Mockito.`when`(mockLook.motionDirectionInUserInterfaceDimensionUnit)
                .thenReturn(expectedValue.toFloat())
            Mockito.`when`(mockLook.sizeInUserInterfaceDimensionUnit)
                .thenReturn(expectedValue.toFloat())
            Mockito.`when`(mockLook.transparencyInUserInterfaceDimensionUnit)
                .thenReturn(expectedValue.toFloat())
            Mockito.`when`(mockLook.xInUserInterfaceDimensionUnit)
                .thenReturn(expectedValue.toFloat())
            Mockito.`when`(mockLook.xVelocityInUserInterfaceDimensionUnit)
                .thenReturn(expectedValue.toFloat())
            Mockito.`when`(mockLook.yInUserInterfaceDimensionUnit)
                .thenReturn(expectedValue.toFloat())
            Mockito.`when`(mockLook.yVelocityInUserInterfaceDimensionUnit)
                .thenReturn(expectedValue.toFloat())
        }

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("objectAngularVelocity", Sensors.OBJECT_ANGULAR_VELOCITY),
            arrayOf("objectBrightness", Sensors.OBJECT_BRIGHTNESS),
            arrayOf("objectColor", Sensors.OBJECT_COLOR),
            arrayOf("objectDistanceTo", Sensors.OBJECT_DISTANCE_TO),
            arrayOf("objectLookDirection", Sensors.LOOK_DIRECTION),
            arrayOf("objectMotionDirection", Sensors.MOTION_DIRECTION),
            arrayOf("objectSize", Sensors.OBJECT_SIZE),
            arrayOf("objectTransparency", Sensors.OBJECT_TRANSPARENCY),
            arrayOf("objectX", Sensors.OBJECT_X),
            arrayOf("objectXVelocity", Sensors.OBJECT_X_VELOCITY),
            arrayOf("objectY", Sensors.OBJECT_Y),
            arrayOf("objectYVelocity", Sensors.OBJECT_Y_VELOCITY),
        )
    }
}
