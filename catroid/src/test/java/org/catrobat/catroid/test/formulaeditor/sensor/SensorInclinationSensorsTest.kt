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

import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.sensor.Sensor
import org.catrobat.catroid.formulaeditor.sensor.SensorXInclination
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorXInclinationTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorYInclinationTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SensorInclinationSensorsTest {

    @Before
    fun resetSensor() {
        SensorXInclinationTest.getInstance().resetFunctions()
    }

    @Test
    fun inclinationXRotationPosTest() {
        val expectedValue = 34
        SensorHandler.useRotationVectorFallback = false
        val sensor = SensorXInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 1 }
        sensor.setGetOrientationFunction { _: FloatArray, _: FloatArray ->
            SensorXInclination.orientations[2] = expectedValue.toFloat()
            floatArrayOf(0f, 0f, 0f)
        }
        compareToSensor(
            expectedValue.toDouble() * (-180f / Math.PI.toFloat()).toDouble(),
            sensor
        )
    }

    @Test
    fun inclinationXRotationNegTest() {
        val expectedValue = 35
        SensorHandler.useRotationVectorFallback = false
        val sensor = SensorXInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> -1 }
        sensor.setGetOrientationFunction { _: FloatArray, _: FloatArray ->
            SensorXInclination.orientations[2] = expectedValue.toFloat()
            floatArrayOf(0f, 0f, 0f)
        }
        compareToSensor(
            expectedValue * (-180f / Math.PI.toFloat()).toDouble(),
            sensor
        )
    }

    @Test
    fun inclinationXRotationNeutralTest() {
        val expectedValue = 36
        SensorHandler.useRotationVectorFallback = false
        val sensor = SensorXInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> -0 }
        sensor.setGetOrientationFunction { _: FloatArray, _: FloatArray ->
            SensorXInclination.orientations[2] = expectedValue.toFloat()
            floatArrayOf(0f, 0f, 0f)
        }
        compareToSensor(
            expectedValue * (-180f / Math.PI.toFloat()).toDouble(),
            sensor
        )
    }

    @Test
    fun inclinationXFallbackRotationNeutralRawIn90And180SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = -0.1736f
        SensorHandler.signAccelerationZ = 1f
        val sensor = SensorXInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 0 }
        compareToSensor((-10).toDouble(), sensor)
    }

    @Test
    fun inclinationXFallbackRotationNeutralRawIn90And180SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = -0.1736f
        SensorHandler.signAccelerationZ = -1f
        val sensor = SensorXInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 0 }
        compareToSensor((-170).toDouble(), sensor)
    }

    @Test
    fun inclinationXFallbackRotationNeutralRawLess90SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = 0.3420f
        SensorHandler.signAccelerationZ = 1f
        val sensor = SensorXInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 0 }
        compareToSensor(20.toDouble(), sensor)
    }

    @Test
    fun inclinationXFallbackRotationNeutralRawLess90SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = 0.3420f
        SensorHandler.signAccelerationZ = -1f
        val sensor = SensorXInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 0 }
        compareToSensor(160.toDouble(), sensor)
    }

    @Test
    fun inclinationXFallbackRotationPosRawIn90And180SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = -0.1736f
        SensorHandler.signAccelerationZ = 1f
        val sensor = SensorXInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 1 }
        compareToSensor(10.toDouble(), sensor)
    }

    @Test
    fun inclinationXFallbackRotationPosRawIn90And180SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = -0.1736f
        SensorHandler.signAccelerationZ = -1f
        val sensor = SensorXInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 1 }
        compareToSensor(170.toDouble(), sensor)
    }

    @Test
    fun inclinationXFallbackRotationPosRawLess90SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = 0.3420f
        SensorHandler.signAccelerationZ = 1f
        val sensor = SensorXInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 1 }
        compareToSensor((-20).toDouble(), sensor)
    }

    @Test
    fun inclinationXFallbackRotationPosRawLess90SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = 0.3420f
        SensorHandler.signAccelerationZ = -1f
        val sensor = SensorXInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 1 }
        compareToSensor((-160).toDouble(), sensor)
    }

    @Test
    fun inclinationXFallbackRotationNegRawIn90And180SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = 0.1736f
        SensorHandler.signAccelerationZ = 1f
        val sensor = SensorXInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> -1 }
        compareToSensor(10.toDouble(), sensor)
    }

    @Test
    fun inclinationXFallbackRotationNrgRawIn90And180SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = 0.1736f
        SensorHandler.signAccelerationZ = -1f
        val sensor = SensorXInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> -1 }
        compareToSensor(170.toDouble(), sensor)
    }

    @Test
    fun inclinationXFallbackRotationNegRawLess90SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = -0.3420f
        SensorHandler.signAccelerationZ = 1f
        val sensor = SensorXInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> -1 }
        compareToSensor((-20).toDouble(), sensor)
    }

    @Test
    fun inclinationXFallbackRotationNegRawLess90SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = -0.3420f
        SensorHandler.signAccelerationZ = -1f
        val sensor = SensorXInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> -1 }
        compareToSensor((-160).toDouble(), sensor)
    }

    @Test
    fun inclinationYLess90() {
        SensorHandler.useRotationVectorFallback = false
        val sensor = SensorYInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 1 }
        sensor.setGetOrientationFunction { _, floatA ->
            floatA[1] = (-1.2217).toFloat()
            floatA[2] = (-1.2217).toFloat()
            floatArrayOf(0f, 0f, 0f)
        }
        compareToSensor(70.toDouble(), sensor)
    }

    @Test
    fun inclinationYGreater90UncorrectedLess0() {
        SensorHandler.useRotationVectorFallback = false
        val sensor = SensorYInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 1 }
        sensor.setGetOrientationFunction { _, floatA ->
            floatA[1] = 1.7453.toFloat()
            floatA[2] = (-1.7453).toFloat()
            floatArrayOf(0f, 0f, 0f)
        }
        compareToSensor((-80).toDouble(), sensor)
    }

    @Test
    fun inclinationYGreater90UncorrectedGreater0() {
        SensorHandler.useRotationVectorFallback = false
        val sensor = SensorYInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 1 }
        sensor.setGetOrientationFunction { _, floatA ->
            floatA[1] = (-1.7453).toFloat()
            floatA[2] = (-1.7453).toFloat()
            floatArrayOf(0f, 0f, 0f)
        }
        compareToSensor(80.toDouble(), sensor)
    }

    @Test
    fun inclinationYFallbackRotationNeutralRawIn90And180SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = -0.1736f
        SensorHandler.signAccelerationZ = 1f
        val sensor = SensorYInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 0 }
        compareToSensor((-10).toDouble(), sensor)
    }

    @Test
    fun inclinationYFallbackRotationNeutralRawIn90And180SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = -0.1736f
        SensorHandler.signAccelerationZ = -1f
        val sensor = SensorYInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 0 }
        compareToSensor((-170).toDouble(), sensor)
    }

    @Test
    fun inclinationYFallbackRotationNeutralRawLess90SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = 0.3420f
        SensorHandler.signAccelerationZ = 1f
        val sensor = SensorYInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 0 }
        compareToSensor(20.toDouble(), sensor)
    }

    @Test
    fun inclinationYFallbackRotationNeutralRawLess90SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = 0.3420f
        SensorHandler.signAccelerationZ = -1f
        val sensor = SensorYInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 0 }
        compareToSensor(160.toDouble(), sensor)
    }

    @Test
    fun inclinationYFallbackRotationPosRawIn90And180SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = -0.1736f
        SensorHandler.signAccelerationZ = 1f
        val sensor = SensorYInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 1 }
        compareToSensor((-10).toDouble(), sensor)
    }

    @Test
    fun inclinationYFallbackRotationPosRawIn90And180SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = -0.1736f
        SensorHandler.signAccelerationZ = -1f
        val sensor = SensorYInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 1 }
        compareToSensor((-170).toDouble(), sensor)
    }

    @Test
    fun inclinationYFallbackRotationPosRawLess90SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = 0.3420f
        SensorHandler.signAccelerationZ = 1f
        val sensor = SensorYInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 1 }
        compareToSensor(20.toDouble(), sensor)
    }

    @Test
    fun inclinationYFallbackRotationPosRawLess90SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = 0.3420f
        SensorHandler.signAccelerationZ = -1f
        val sensor = SensorYInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> 1 }
        compareToSensor(160.toDouble(), sensor)
    }

    @Test
    fun inclinationYFallbackRotationNegRawIn90And180SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = 0.1736f
        SensorHandler.signAccelerationZ = 1f
        val sensor = SensorYInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> -1 }
        compareToSensor((-10).toDouble(), sensor)
    }

    @Test
    fun inclinationYFallbackRotationNrgRawIn90And180SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = 0.1736f
        SensorHandler.signAccelerationZ = -1f
        val sensor = SensorYInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> -1 }
        compareToSensor((-170).toDouble(), sensor)
    }

    @Test
    fun inclinationYFallbackRotationNegRawLess90SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = -0.3420f
        SensorHandler.signAccelerationZ = 1f
        val sensor = SensorYInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> -1 }
        compareToSensor(20.toDouble(), sensor)
    }

    @Test
    fun inclinationYFallbackRotationNegRawLess90SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = -0.3420f
        SensorHandler.signAccelerationZ = -1f
        val sensor = SensorYInclinationTest.getInstance()
        sensor.setRotateOrientationFunction { -> -1 }
        compareToSensor(160.toDouble(), sensor)
    }

    private fun compareToSensor(value: Double, sensor: Sensor) {
        Assert.assertEquals(value, sensor.getSensorValue() as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01
    }
}
