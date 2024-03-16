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

import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.sensor.testutils.LookTestable
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

class SensorObjectSensorsTest {
    @Test
    fun objectAngularVelocityTest() {
        compareToSensor(angularExpected, Sensors.OBJECT_ANGULAR_VELOCITY)
    }

    @Test
    fun objectBrightnessTest() {
        compareToSensor(brightnessExpected, Sensors.OBJECT_BRIGHTNESS)
    }

    @Test
    fun objectColorTest() {
        compareToSensor(colorExpected, Sensors.OBJECT_COLOR)
    }

    @Test
    fun objectDistanceToTouchPositionTest() {
        compareToSensor(distanceExpected, Sensors.OBJECT_DISTANCE_TO)
    }

    @Test
    fun objectLookDirectionTest() {
        compareToSensor(lookDirExpected, Sensors.LOOK_DIRECTION)
    }

    @Test
    fun objectMotionDirectionTest() {
        compareToSensor(motionDirExpected, Sensors.MOTION_DIRECTION)
    }

    @Test
    fun objectSizeTest() {
        compareToSensor(sizeExpected, Sensors.OBJECT_SIZE)
    }

    @Test
    fun objectTransparencyTest() {
        compareToSensor(transparencyExpected, Sensors.OBJECT_TRANSPARENCY)
    }

    @Test
    fun objectXTest() {
        compareToSensor(xExpected, Sensors.OBJECT_X)
    }

    @Test
    fun objectXVelocityTest() {
        compareToSensor(xVelocityExpected, Sensors.OBJECT_X_VELOCITY)
    }

    @Test
    fun objectYTest() {
        compareToSensor(yExpected, Sensors.OBJECT_Y)
    }

    @Test
    fun objectYVelocityTest() {
        compareToSensor(yVelocityExpected, Sensors.OBJECT_Y_VELOCITY)
    }

    private fun compareToSensor(value: Float, sensor: Sensors) {
        Assert.assertEquals(value.toDouble(), sensor.getSensor().getSensorValue() as Double, DELTA)
    }

    companion object {

        private const val DELTA = 0.01
        lateinit var sprite: Sprite
        lateinit var look: LookTestable
        private const val angularExpected = 34f
        private const val brightnessExpected = 33f
        private const val colorExpected = 23f
        private const val distanceExpected = 74f
        private const val lookDirExpected = 4f
        private const val motionDirExpected = 654f
        private const val sizeExpected = 5.4f
        private const val transparencyExpected = 0.56f
        private const val xExpected = 43f
        private const val xVelocityExpected = 643f
        private const val yExpected = 27f
        private const val yVelocityExpected = 185f

        @BeforeClass
        @JvmStatic
        fun setup() {
            sprite = Sprite()
            look = LookTestable(sprite)

            look.angularVelocity = angularExpected
            look.objectBrightness = brightnessExpected
            look.color = colorExpected
            look.distance = distanceExpected
            look.lookDirection = lookDirExpected
            look.motionDirection = motionDirExpected
            look.sizeInUserInterface = sizeExpected
            look.transparency = transparencyExpected
            look.xInUserInterface = xExpected
            look.xVelocity = xVelocityExpected
            look.yInUserInterface = yExpected
            look.yVelocity = yVelocityExpected

            sprite.look = look
            SensorHandler.currentSprite = sprite
        }
    }
}
