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

import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.Assert
import org.junit.Test

class SensorAITextSensorsTest {

    @Test
    fun textFromCameraTest() {
        val expectedValue: String = "test text"
        Sensors.TEXT_FROM_CAMERA.getSensor().updateSensorValue(expectedValue)
        compareToSensorString(expectedValue, Sensors.TEXT_FROM_CAMERA)
    }

    @Test
    fun textBlocksNumberTest() {
        val expectedValue: Int = 234
        Sensors.TEXT_BLOCKS_NUMBER.getSensor().updateSensorValue(expectedValue.toDouble())
        compareToSensorDouble(expectedValue, Sensors.TEXT_BLOCKS_NUMBER)
    }

    private fun compareToSensorDouble(value: Int, sensor: Sensors) {
        Assert.assertEquals(value.toDouble(), sensor.getSensor().getSensorValue() as Double, DELTA)
    }

    private fun compareToSensorString(value: String, sensor: Sensors) {
        Assert.assertEquals(value, sensor.getSensor().getSensorValue() as String)
    }

    companion object {
        private const val DELTA = 0.01
    }
}
