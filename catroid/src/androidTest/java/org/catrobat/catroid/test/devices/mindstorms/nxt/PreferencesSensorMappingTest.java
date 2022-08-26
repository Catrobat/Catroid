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

package org.catrobat.catroid.test.devices.mindstorms.nxt;

import android.content.Context;

import org.catrobat.catroid.R;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.test.core.app.ApplicationProvider;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

@RunWith(Parameterized.class)
public class PreferencesSensorMappingTest {

	private static final int EXPECTED_MAPPING_SIZE = 6;

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"NO_SENSOR", R.string.nxt_no_sensor, NXTSensor.Sensor.NO_SENSOR},
				{"TOUCH", R.string.nxt_sensor_touch, NXTSensor.Sensor.TOUCH},
				{"SOUND", R.string.nxt_sensor_sound, NXTSensor.Sensor.SOUND},
				{"LIGHT_INACTIVE", R.string.nxt_sensor_light, NXTSensor.Sensor.LIGHT_INACTIVE},
				{"LIGHT_ACTIVE", R.string.nxt_sensor_light_active, NXTSensor.Sensor.LIGHT_ACTIVE},
				{"ULTRASONIC", R.string.nxt_sensor_ultrasonic, NXTSensor.Sensor.ULTRASONIC},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public int sensorNameStringId;
	@Parameterized.Parameter(2)
	public NXTSensor.Sensor sensor;

	private String sensorName;

	private ArrayList<String> sensorNames = new ArrayList<>();
	private ArrayList<String> sensorCodes = new ArrayList<>();

	@Before
	public void setUp() {
		Context context = ApplicationProvider.getApplicationContext();
		sensorNames.addAll(Arrays.asList(context.getResources().getStringArray(R.array.nxt_sensor_chooser)));
		sensorCodes.addAll(Arrays.asList(NXTSensor.Sensor.getSensorCodes()));
		sensorName = context.getString(sensorNameStringId);
	}

	@Test
	public void testNameAndPreferenceSameIndex() {
		int sensorNameIndex = sensorNames.indexOf(sensorName);
		int sensorCodeIndex = sensorCodes.indexOf(sensor.getSensorCode());
		assertSame(sensorCodeIndex, sensorNameIndex);
	}

	@Test
	public void testMappingContainsNameAndCode() {
		assertThat(sensorNames, hasItem(sensorName));
		assertThat(sensorCodes, hasItem(sensor.getSensorCode()));
	}

	@Test
	public void testMappingSize() {
		assertEquals(EXPECTED_MAPPING_SIZE, sensorNames.size());
		assertEquals(EXPECTED_MAPPING_SIZE, sensorCodes.size());
	}
}
