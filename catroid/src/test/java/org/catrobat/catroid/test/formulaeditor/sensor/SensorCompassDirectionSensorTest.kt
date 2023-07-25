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
import org.catrobat.catroid.formulaeditor.sensor.SensorCompassDirection
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyObject
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(SensorHandler::class, SensorManager::class, SensorCompassDirection::class)
class SensorCompassDirectionSensorTest {

    @Test
    fun compassDirectionTestRotationPos() {
        val expectedValue = 34
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.mockStatic(SensorManager::class.java)
        SensorHandler.useRotationVectorFallback = false
        PowerMockito.`when`(SensorHandler.rotateOrientation()).thenReturn(1)
        PowerMockito.`when`(SensorManager.getOrientation(anyObject(), anyObject())).then {
            SensorCompassDirection.getInstance().orientations[0] = expectedValue.toFloat(); null
        }
        compareToSensor(
            expectedValue * (-180f / Math.PI.toFloat()).toDouble(),
            Sensors.COMPASS_DIRECTION
        )
    }

    @Test
    fun compassDirectionTestRotationNeg() {
        val expectedValue = 35
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.mockStatic(SensorManager::class.java)
        SensorHandler.useRotationVectorFallback = false
        PowerMockito.`when`(SensorHandler.rotateOrientation()).thenReturn(-1)
        PowerMockito.`when`(SensorManager.getOrientation(anyObject(), anyObject())).then {
            SensorCompassDirection.getInstance().orientations[0] = expectedValue.toFloat(); null
        }
        compareToSensor(
            expectedValue * (-180f / Math.PI.toFloat()).toDouble(),
            Sensors.COMPASS_DIRECTION
        )
    }

    @Test
    fun compassDirectionTestRotationNeutral() {
        val expectedValue = 35
        PowerMockito.mockStatic(SensorHandler::class.java)
        PowerMockito.mockStatic(SensorManager::class.java)
        SensorHandler.useRotationVectorFallback = false
        PowerMockito.`when`(SensorHandler.rotateOrientation()).thenReturn(0)
        PowerMockito.`when`(SensorManager.getOrientation(anyObject(), anyObject())).then {
            SensorCompassDirection.getInstance().orientations[0] = expectedValue.toFloat(); null
        }
        compareToSensor(
            expectedValue * (-180f / Math.PI.toFloat()).toDouble(),
            Sensors.COMPASS_DIRECTION
        )
    }

    private fun compareToSensor(value: Double, sensor: Sensors) {
        Assert.assertEquals(value, sensor.getSensor().getSensorValue() as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01
    }
}
