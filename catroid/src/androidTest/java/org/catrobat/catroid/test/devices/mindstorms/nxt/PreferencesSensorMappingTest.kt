/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.test.devices.mindstorms.nxt

import android.content.Context
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor
import org.junit.Before
import androidx.test.core.app.ApplicationProvider
import junit.framework.Assert
import org.catrobat.catroid.R
import org.hamcrest.MatcherAssert
import org.catrobat.catroid.test.devices.mindstorms.nxt.PreferencesSensorMappingTest
import org.hamcrest.Matchers
import org.junit.Test
import java.util.ArrayList
import java.util.Arrays

@RunWith(Parameterized::class)
class PreferencesSensorMappingTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var sensorNameStringId = 0
    @JvmField
    @Parameterized.Parameter(2)
    var sensor: NXTSensor.Sensor? = null
    private var sensorName: String? = null
    private val sensorNames = ArrayList<String?>()
    private val sensorCodes = ArrayList<String?>()
    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        sensorNames.addAll(Arrays.asList(*context.resources.getStringArray(R.array.nxt_sensor_chooser)))
        sensorCodes.addAll(Arrays.asList(*NXTSensor.Sensor.getSensorCodes()))
        sensorName = context.getString(sensorNameStringId)
    }

    @Test
    fun testNameAndPreferenceSameIndex() {
        val sensorNameIndex = sensorNames.indexOf(sensorName)
        val sensorCodeIndex = sensorCodes.indexOf(sensor!!.sensorCode)
        Assert.assertSame(sensorCodeIndex, sensorNameIndex)
    }

    @Test
    fun testMappingContainsNameAndCode() {
        MatcherAssert.assertThat(sensorNames, Matchers.hasItem(sensorName))
        MatcherAssert.assertThat(
            sensorCodes, Matchers.hasItem(
                sensor!!.sensorCode
            )
        )
    }

    @Test
    fun testMappingSize() {
        Assert.assertEquals(EXPECTED_MAPPING_SIZE, sensorNames.size)
        Assert.assertEquals(EXPECTED_MAPPING_SIZE, sensorCodes.size)
    }

    companion object {
        private const val EXPECTED_MAPPING_SIZE = 6
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        "NO_SENSOR",
                        R.string.nxt_no_sensor,
                        NXTSensor.Sensor.NO_SENSOR
                    ),
                    arrayOf(
                        "TOUCH",
                        R.string.nxt_sensor_touch,
                        NXTSensor.Sensor.TOUCH
                    ),
                    arrayOf(
                        "SOUND",
                        R.string.nxt_sensor_sound,
                        NXTSensor.Sensor.SOUND
                    ),
                    arrayOf(
                        "LIGHT_INACTIVE",
                        R.string.nxt_sensor_light,
                        NXTSensor.Sensor.LIGHT_INACTIVE
                    ),
                    arrayOf(
                        "LIGHT_ACTIVE",
                        R.string.nxt_sensor_light_active,
                        NXTSensor.Sensor.LIGHT_ACTIVE
                    ),
                    arrayOf(
                        "ULTRASONIC",
                        R.string.nxt_sensor_ultrasonic,
                        NXTSensor.Sensor.ULTRASONIC
                    )
                )
            )
        }
    }
}