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
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorCompassDirectionTest
import org.junit.Assert
import org.junit.Test

class SensorCompassDirectionSensorTest {

    @Test
    fun compassDirectionTestRotationPos() {
        val expectedValue = 34
        SensorHandler.useRotationVectorFallback = true
        val rotateOrientationTestFKT = { -> 1 }
        val getOrientationTestFKT = { r: FloatArray, v: FloatArray ->
            v[0] = r[0]
            floatArrayOf(0f, 0f, 0f)
        }
        val remapCoordinateSystemTestFKT = { inR: FloatArray, X: Int, Y: Int, outR: FloatArray ->
            outR[0] = expectedValue.toFloat()
            true
        }
        val getRotationMatrixFromVectorTestFKT = { r: FloatArray, vector: FloatArray -> }

        val sensor = SensorCompassDirectionTest.getInstance()
        sensor.rotateOrientationTest = rotateOrientationTestFKT
        sensor.getOrientationTest = getOrientationTestFKT
        sensor.remapCoordinateSystemTest = remapCoordinateSystemTestFKT
        sensor.getRotationMatrixFromVectorTest = getRotationMatrixFromVectorTestFKT

        compareToSensor(
            expectedValue.toDouble() * (-180f / Math.PI.toFloat()).toDouble(),
            SensorCompassDirectionTest.getInstance()
        )
    }

    @Test
    fun compassDirectionTestRotationNeg() {
        val expectedValue = 54
        SensorHandler.useRotationVectorFallback = true
        val rotateOrientationTestFKT = { -> -1 }
        val getOrientationTestFKT = { r: FloatArray, v: FloatArray ->
            v[0] = r[0]
            floatArrayOf(0f, 0f, 0f)
        }
        val remapCoordinateSystemTestFKT = { inR: FloatArray, X: Int, Y: Int, outR: FloatArray ->
            outR[0] = expectedValue.toFloat()
            true
        }
        val getRotationMatrixFromVectorTestFKT = { r: FloatArray, vector: FloatArray -> }

        val sensor = SensorCompassDirectionTest.getInstance()
        sensor.rotateOrientationTest = rotateOrientationTestFKT
        sensor.getOrientationTest = getOrientationTestFKT
        sensor.remapCoordinateSystemTest = remapCoordinateSystemTestFKT
        sensor.getRotationMatrixFromVectorTest = getRotationMatrixFromVectorTestFKT

        compareToSensor(
            expectedValue.toDouble() * (-180f / Math.PI.toFloat()).toDouble(),
            SensorCompassDirectionTest.getInstance()
        )
    }

    @Test
    fun compassDirectionTestRotationNeutral() {
        val expectedValue = 63
        SensorHandler.useRotationVectorFallback = true
        val rotateOrientationTestFKT = { -> 0 }
        val getOrientationTestFKT = { r: FloatArray, v: FloatArray ->
            v[0] = expectedValue.toFloat()
            floatArrayOf(0f, 0f, 0f)
        }
        val remapCoordinateSystemTestFKT = { inR: FloatArray, X: Int, Y: Int, outR: FloatArray ->
            true
        }
        val getRotationMatrixFromVectorTestFKT = { r: FloatArray, vector: FloatArray -> }

        val sensor = SensorCompassDirectionTest.getInstance()
        sensor.rotateOrientationTest = rotateOrientationTestFKT
        sensor.getOrientationTest = getOrientationTestFKT
        sensor.remapCoordinateSystemTest = remapCoordinateSystemTestFKT
        sensor.getRotationMatrixFromVectorTest = getRotationMatrixFromVectorTestFKT

        compareToSensor(
            expectedValue.toDouble() * (-180f / Math.PI.toFloat()).toDouble(),
            SensorCompassDirectionTest.getInstance()
        )
    }

    @Test
    fun compassDirectionTestRotationPosFallBack() {
        val expectedValue = 34
        SensorHandler.useRotationVectorFallback = false
        val rotateOrientationTestFKT = { -> 1 }
        val getOrientationTestFKT = { r: FloatArray, v: FloatArray ->
            v[0] = r[0]
            floatArrayOf(0f, 0f, 0f)
        }
        val remapCoordinateSystemTestFKT = { inR: FloatArray, X: Int, Y: Int, outR: FloatArray ->
            outR[0] = expectedValue.toFloat()
            true
        }
        val getRotationMatrixFromVectorTestFKT = { r: FloatArray, vector: FloatArray -> }

        val sensor = SensorCompassDirectionTest.getInstance()
        sensor.rotateOrientationTest = rotateOrientationTestFKT
        sensor.getOrientationTest = getOrientationTestFKT
        sensor.remapCoordinateSystemTest = remapCoordinateSystemTestFKT
        sensor.getRotationMatrixFromVectorTest = getRotationMatrixFromVectorTestFKT

        compareToSensor(
            expectedValue.toDouble() * (-180f / Math.PI.toFloat()).toDouble(),
            SensorCompassDirectionTest.getInstance()
        )
    }

    @Test
    fun compassDirectionTestRotationNegFallBack() {
        val expectedValue = 54
        SensorHandler.useRotationVectorFallback = false
        val rotateOrientationTestFKT = { -> -1 }
        val getOrientationTestFKT = { r: FloatArray, v: FloatArray ->
            v[0] = r[0]
            floatArrayOf(0f, 0f, 0f)
        }
        val remapCoordinateSystemTestFKT = { inR: FloatArray, X: Int, Y: Int, outR: FloatArray ->
            outR[0] = expectedValue.toFloat()
            true
        }
        val getRotationMatrixFromVectorTestFKT = { r: FloatArray, vector: FloatArray -> }

        val sensor = SensorCompassDirectionTest.getInstance()
        sensor.rotateOrientationTest = rotateOrientationTestFKT
        sensor.getOrientationTest = getOrientationTestFKT
        sensor.remapCoordinateSystemTest = remapCoordinateSystemTestFKT
        sensor.getRotationMatrixFromVectorTest = getRotationMatrixFromVectorTestFKT

        compareToSensor(
            expectedValue.toDouble() * (-180f / Math.PI.toFloat()).toDouble(),
            SensorCompassDirectionTest.getInstance()
        )
    }

    @Test
    fun compassDirectionTestRotationNeutralFallBack() {
        val expectedValue = 63
        SensorHandler.useRotationVectorFallback = false
        val rotateOrientationTestFKT = { -> 0 }
        val getOrientationTestFKT = { r: FloatArray, v: FloatArray ->
            v[0] = expectedValue.toFloat()
            floatArrayOf(0f, 0f, 0f)
        }
        val remapCoordinateSystemTestFKT = { inR: FloatArray, X: Int, Y: Int, outR: FloatArray ->
            true
        }
        val getRotationMatrixFromVectorTestFKT = { r: FloatArray, vector: FloatArray -> }

        val sensor = SensorCompassDirectionTest.getInstance()
        sensor.rotateOrientationTest = rotateOrientationTestFKT
        sensor.getOrientationTest = getOrientationTestFKT
        sensor.remapCoordinateSystemTest = remapCoordinateSystemTestFKT
        sensor.getRotationMatrixFromVectorTest = getRotationMatrixFromVectorTestFKT

        compareToSensor(
            expectedValue.toDouble() * (-180f / Math.PI.toFloat()).toDouble(),
            SensorCompassDirectionTest.getInstance()
        )
    }

    private fun compareToSensor(value: Double, sensor: Sensor) {
        Assert.assertEquals(value, sensor.getSensorValue() as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01
    }
}
