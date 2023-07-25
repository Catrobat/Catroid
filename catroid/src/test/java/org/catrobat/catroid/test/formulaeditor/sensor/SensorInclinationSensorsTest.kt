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

import android.hardware.SensorManager
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.sensor.SensorXInclination
import org.catrobat.catroid.formulaeditor.sensor.SensorYInclination
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyObject
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(SensorHandler::class, SensorManager::class)
class SensorInclinationSensorsTest {

    @Test
    fun inclinationXRotationPosTest() {
        val expectedValue = 34
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.mockStatic(SensorManager::class.java)
        SensorHandler.useRotationVectorFallback = false
        PowerMockito.`when`(SensorHandler.rotateOrientation()).thenReturn(1)
        PowerMockito.`when`(SensorManager.getOrientation(anyObject(), anyObject())).then {
            SensorXInclination.getInstance().orientations[2] = expectedValue.toFloat(); null
        }
        compareToSensor(
            expectedValue * (-180f / Math.PI.toFloat()).toDouble(),
            Sensors.X_INCLINATION
        )
    }

    @Test
    fun inclinationXRotationNegTest() {
        val expectedValue = 35
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.mockStatic(SensorManager::class.java)
        SensorHandler.useRotationVectorFallback = false
        PowerMockito.`when`(SensorHandler.rotateOrientation()).thenReturn(-1)
        PowerMockito.`when`(SensorManager.getOrientation(anyObject(), anyObject())).then {
            SensorXInclination.getInstance().orientations[2] = expectedValue.toFloat(); null
        }
        compareToSensor(
            expectedValue * (-180f / Math.PI.toFloat()).toDouble(),
            Sensors.X_INCLINATION
        )
    }

    @Test
    fun inclinationXRotationNeutralTest() {
        val expectedValue = 36
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.mockStatic(SensorManager::class.java)
        SensorHandler.useRotationVectorFallback = false
        PowerMockito.`when`(SensorHandler.rotateOrientation()).thenReturn(0)
        PowerMockito.`when`(SensorManager.getOrientation(anyObject(), anyObject())).then {
            SensorXInclination.getInstance().orientations[2] = expectedValue.toFloat(); null
        }
        compareToSensor(
            expectedValue * (-180f / Math.PI.toFloat()).toDouble(),
            Sensors.X_INCLINATION
        )
    }

    @Test
    fun inclinationXFallbackRotationNeutralRawIn90And180SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = -0.1736f
        SensorHandler.signAccelerationZ = 1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 0 }
        compareToSensor((-10).toDouble(), Sensors.X_INCLINATION)
    }

    @Test
    fun inclinationXFallbackRotationNeutralRawIn90And180SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = -0.1736f
        SensorHandler.signAccelerationZ = -1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 0 }
        compareToSensor((-170).toDouble(), Sensors.X_INCLINATION)
    }

    @Test
    fun inclinationXFallbackRotationNeutralRawLess90SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = 0.3420f
        SensorHandler.signAccelerationZ = 1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 0 }
        compareToSensor(20.toDouble(), Sensors.X_INCLINATION)
    }

    @Test
    fun inclinationXFallbackRotationNeutralRawLess90SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = 0.3420f
        SensorHandler.signAccelerationZ = -1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 0 }
        compareToSensor(160.toDouble(), Sensors.X_INCLINATION)
    }

    @Test
    fun inclinationXFallbackRotationPosRawIn90And180SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = -0.1736f
        SensorHandler.signAccelerationZ = 1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 1 }
        compareToSensor(10.toDouble(), Sensors.X_INCLINATION)
    }

    @Test
    fun inclinationXFallbackRotationPosRawIn90And180SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = -0.1736f
        SensorHandler.signAccelerationZ = -1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 1 }
        compareToSensor(170.toDouble(), Sensors.X_INCLINATION)
    }

    @Test
    fun inclinationXFallbackRotationPosRawLess90SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = 0.3420f
        SensorHandler.signAccelerationZ = 1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 1 }
        compareToSensor((-20).toDouble(), Sensors.X_INCLINATION)
    }

    @Test
    fun inclinationXFallbackRotationPosRawLess90SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = 0.3420f
        SensorHandler.signAccelerationZ = -1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 1 }
        compareToSensor((-160).toDouble(), Sensors.X_INCLINATION)
    }

    @Test
    fun inclinationXFallbackRotationNegRawIn90And180SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = 0.1736f
        SensorHandler.signAccelerationZ = 1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { -1 }
        compareToSensor(10.toDouble(), Sensors.X_INCLINATION)
    }

    @Test
    fun inclinationXFallbackRotationNrgRawIn90And180SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = 0.1736f
        SensorHandler.signAccelerationZ = -1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { -1 }
        compareToSensor(170.toDouble(), Sensors.X_INCLINATION)
    }

    @Test
    fun inclinationXFallbackRotationNegRawLess90SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = -0.3420f
        SensorHandler.signAccelerationZ = 1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { -1 }
        compareToSensor((-20).toDouble(), Sensors.X_INCLINATION)
    }

    @Test
    fun inclinationXFallbackRotationNegRawLess90SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = -0.3420f
        SensorHandler.signAccelerationZ = -1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { -1 }
        compareToSensor((-160).toDouble(), Sensors.X_INCLINATION)
    }

    @Test
    fun inclinationYLess90() {
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.mockStatic(SensorManager::class.java)
        SensorHandler.useRotationVectorFallback = false
        PowerMockito.`when`(SensorHandler.rotateOrientation()).thenReturn(1)
        PowerMockito.`when`(SensorManager.getOrientation(anyObject(), anyObject())).then {
            SensorYInclination.getInstance().orientations[2] = (-1.2217).toFloat()
            SensorYInclination.getInstance().orientations[1] = (-1.2217).toFloat()
            null
        }
        compareToSensor(
            70.toDouble(), Sensors
                .Y_INCLINATION
        )
    }

    @Test
    fun inclinationYGreater90UncorrectedLess0() {
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.mockStatic(SensorManager::class.java)
        SensorHandler.useRotationVectorFallback = false
        PowerMockito.`when`(SensorHandler.rotateOrientation()).thenReturn(1)
        PowerMockito.`when`(SensorManager.getOrientation(anyObject(), anyObject())).then {
            SensorYInclination.getInstance().orientations[2] = (-1.7453).toFloat()
            SensorYInclination.getInstance().orientations[1] = 1.7453.toFloat()
            null
        }
        compareToSensor(
            (-80).toDouble(), Sensors
                .Y_INCLINATION
        )
    }

    @Test
    fun inclinationYGreater90UncorrectedGreater0() {
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.mockStatic(SensorManager::class.java)
        SensorHandler.useRotationVectorFallback = false
        PowerMockito.`when`(SensorHandler.rotateOrientation()).thenReturn(1)
        PowerMockito.`when`(SensorManager.getOrientation(anyObject(), anyObject())).then {
            SensorYInclination.getInstance().orientations[2] = (-1.7453).toFloat()
            SensorYInclination.getInstance().orientations[1] = (-1.7453).toFloat()
            null
        }
        compareToSensor(
            80.toDouble(), Sensors
                .Y_INCLINATION
        )
    }

    @Test
    fun inclinationYFallbackRotationNeutralRawIn90And180SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = -0.1736f
        SensorHandler.signAccelerationZ = 1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 0 }
        compareToSensor((-10).toDouble(), Sensors.Y_INCLINATION)
    }

    @Test
    fun inclinationYFallbackRotationNeutralRawIn90And180SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = -0.1736f
        SensorHandler.signAccelerationZ = -1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 0 }
        compareToSensor((-170).toDouble(), Sensors.Y_INCLINATION)
    }

    @Test
    fun inclinationYFallbackRotationNeutralRawLess90SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = 0.3420f
        SensorHandler.signAccelerationZ = 1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 0 }
        compareToSensor(20.toDouble(), Sensors.Y_INCLINATION)
    }

    @Test
    fun inclinationYFallbackRotationNeutralRawLess90SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[1] = 0.3420f
        SensorHandler.signAccelerationZ = -1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 0 }
        compareToSensor(160.toDouble(), Sensors.Y_INCLINATION)
    }

    @Test
    fun inclinationYFallbackRotationPosRawIn90And180SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = -0.1736f
        SensorHandler.signAccelerationZ = 1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 1 }
        compareToSensor((-10).toDouble(), Sensors.Y_INCLINATION)
    }

    @Test
    fun inclinationYFallbackRotationPosRawIn90And180SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = -0.1736f
        SensorHandler.signAccelerationZ = -1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 1 }
        compareToSensor((-170).toDouble(), Sensors.Y_INCLINATION)
    }

    @Test
    fun inclinationYFallbackRotationPosRawLess90SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = 0.3420f
        SensorHandler.signAccelerationZ = 1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 1 }
        compareToSensor(20.toDouble(), Sensors.Y_INCLINATION)
    }

    @Test
    fun inclinationYFallbackRotationPosRawLess90SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = 0.3420f
        SensorHandler.signAccelerationZ = -1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { 1 }
        compareToSensor(160.toDouble(), Sensors.Y_INCLINATION)
    }

    @Test
    fun inclinationYFallbackRotationNegRawIn90And180SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = 0.1736f
        SensorHandler.signAccelerationZ = 1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { -1 }
        compareToSensor((-10).toDouble(), Sensors.Y_INCLINATION)
    }

    @Test
    fun inclinationYFallbackRotationNrgRawIn90And180SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = 0.1736f
        SensorHandler.signAccelerationZ = -1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { -1 }
        compareToSensor((-170).toDouble(), Sensors.Y_INCLINATION)
    }

    @Test
    fun inclinationYFallbackRotationNegRawLess90SignPosTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = -0.3420f
        SensorHandler.signAccelerationZ = 1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { -1 }
        compareToSensor(20.toDouble(), Sensors.Y_INCLINATION)
    }

    @Test
    fun inclinationYFallbackRotationNegRawLess90SignNegTest() {
        SensorHandler.useRotationVectorFallback = true
        SensorHandler.accelerationXYZ[0] = -0.3420f
        SensorHandler.signAccelerationZ = -1f
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.`when`(SensorHandler.rotateOrientation()).then { -1 }
        compareToSensor(160.toDouble(), Sensors.Y_INCLINATION)
    }

    private fun compareToSensor(value: Double, sensor: Sensors) {
        Assert.assertEquals(value, sensor.getSensor().getSensorValue() as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01
    }
}
