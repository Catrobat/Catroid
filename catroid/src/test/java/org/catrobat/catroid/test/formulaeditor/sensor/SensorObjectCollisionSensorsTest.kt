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

import android.graphics.PointF
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.sensor.Sensor
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorObjectCollidesWithEdgeTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorObjectCollidesWithFingerTest
import org.catrobat.catroid.stage.StageListener
import org.junit.Assert
import org.junit.Test

class SensorObjectCollisionSensorsTest {

    @Test
    fun collidesWithFingerNoCollision() {
        val expectedValue = 0
        val sprite = Sprite()
        val look = Look(sprite)
        val detector = { _: Array<Polygon>, _: ArrayList<PointF> -> expectedValue.toDouble() }
        val sensor = SensorObjectCollidesWithFingerTest.getInstance()
        sensor.look = look
        sensor.collisionFunction = detector

        compareToSensor(expectedValue, sensor)
    }

    @Test
    fun collidesWithFingerCollision() {
        val expectedValue = 1
        val sprite = Sprite()
        val look = Look(sprite)
        val detector = { _: Array<Polygon>, _: ArrayList<PointF> -> expectedValue.toDouble() }
        val sensor = SensorObjectCollidesWithFingerTest.getInstance()
        sensor.look = look
        sensor.collisionFunction = detector
        compareToSensor(expectedValue, sensor)
    }

    @Test
    fun collidesWithEdgeNoCollision() {
        val expectedValue = 0
        val sprite = Sprite()
        val look = Look(sprite)
        val listener = StageListener(true)
        val screen = Rectangle()
        val detector = { _: Array<Polygon>, _: Rectangle? -> false }
        val sensor = SensorObjectCollidesWithEdgeTest.getInstance()
        sensor.look = look
        sensor.listener = listener
        sensor.rectangle = screen
        sensor.collisionFunction = detector
        compareToSensor(expectedValue, sensor)
    }

    @Test
    fun collidesWithEdgeCollision() {
        val expectedValue = 1
        val sprite = Sprite()
        val look = Look(sprite)
        val listener = StageListener(true)
        val screen = Rectangle()
        val detector = { _: Array<Polygon>, _: Rectangle? -> true }
        val sensor = SensorObjectCollidesWithEdgeTest.getInstance()
        sensor.look = look
        sensor.listener = listener
        sensor.rectangle = screen
        sensor.collisionFunction = detector
        compareToSensor(expectedValue, sensor)
    }

    @Test
    fun collidesNoFrame() {
        val expectedValue = 0
        val sprite = Sprite()
        val look = Look(sprite)

        val listener = StageListener(false)
        val screen = Rectangle()
        val detector = { _: Array<Polygon>, _: Rectangle? -> true }

        val sensor = SensorObjectCollidesWithEdgeTest.getInstance()
        sensor.look = look
        sensor.listener = listener
        sensor.rectangle = screen
        sensor.collisionFunction = detector

        compareToSensor(expectedValue, sensor)
    }

    private fun compareToSensor(value: Int, sensor: Sensor) {
        Assert.assertEquals(value.toDouble(), sensor.getSensorValue() as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01
    }
}
