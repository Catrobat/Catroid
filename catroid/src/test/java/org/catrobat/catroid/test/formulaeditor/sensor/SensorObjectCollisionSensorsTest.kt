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
import org.catrobat.catroid.sensing.CollisionDetection
import org.catrobat.catroid.utils.TouchUtil
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(CollisionDetection::class)
class SensorObjectCollisionSensorsTest {

    @Test
    fun collidesWithFingerCollision() {
        val expectedValue = 1
        val mockLook = mock(Look::class.java)
        val mockSprite = mock(Sprite::class.java)
        mockSprite.look = mockLook
        SensorHandler.currentSprite = mockSprite
        PowerMockito.mockStatic(CollisionDetection::class.java)
        PowerMockito.`when`(
            CollisionDetection.collidesWithFinger(
                mockLook.currentCollisionPolygon, TouchUtil
                    .getCurrentTouchingPoints()
            )
        ).thenReturn(expectedValue.toDouble())
        compareToSensor(expectedValue, Sensors.COLLIDES_WITH_FINGER)
    }

    @Test
    fun collidesWithFingerNoCollision() {
        val expectedValue = 0
        val mockLook = mock(Look::class.java)
        val mockSprite = mock(Sprite::class.java)
        mockSprite.look = mockLook
        SensorHandler.currentSprite = mockSprite
        PowerMockito.mockStatic(CollisionDetection::class.java)
        PowerMockito.`when`(
            CollisionDetection.collidesWithFinger(
                mockLook.currentCollisionPolygon, TouchUtil
                    .getCurrentTouchingPoints()
            )
        ).thenReturn(expectedValue.toDouble())
        compareToSensor(expectedValue, Sensors.COLLIDES_WITH_FINGER)
    }

    private fun compareToSensor(value: Int, sensor: Sensors) {
        Assert.assertEquals(value.toDouble(), sensor.getSensor().getSensorValue() as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01
    }
}
