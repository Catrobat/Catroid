/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.test.AndroidTestCase;
import android.util.Pair;

import org.catrobat.catroid.R;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;

import java.util.ArrayList;
import java.util.List;

public class PreferencesSensorMappingTest extends AndroidTestCase {

	public void testNXTSensorToTextMapping() {
		Context context = this.getContext().getApplicationContext();

		final List<Pair<Integer, NXTSensor.Sensor>> correctMapping = new ArrayList<Pair<Integer, NXTSensor.Sensor>>();

		correctMapping.add(new Pair(R.string.nxt_no_sensor, NXTSensor.Sensor.NO_SENSOR));
		correctMapping.add(new Pair(R.string.nxt_sensor_touch, NXTSensor.Sensor.TOUCH));
		correctMapping.add(new Pair(R.string.nxt_sensor_sound, NXTSensor.Sensor.SOUND));
		correctMapping.add(new Pair(R.string.nxt_sensor_light, NXTSensor.Sensor.LIGHT_INACTIVE));
		correctMapping.add(new Pair(R.string.nxt_sensor_light_active, NXTSensor.Sensor.LIGHT_ACTIVE));
		correctMapping.add(new Pair(R.string.nxt_sensor_ultrasonic, NXTSensor.Sensor.ULTRASONIC));

		String[] sensorNames = context.getResources().getStringArray(R.array.nxt_sensor_chooser);
		String[] sensorPreferencesCodes = NXTSensor.Sensor.getSensorCodes();

		assertEquals("Wrong sensor name count", correctMapping.size(), sensorNames.length);
		assertEquals("Wrong sensor preferences codes count", correctMapping.size(), sensorPreferencesCodes.length);

		for (int i = 0; i < correctMapping.size(); ++i) {
			assertEquals("Wrong mapping of nxt sensor names, maybe the order is wrong",
					context.getString(correctMapping.get(i).first), sensorNames[i]);

			assertEquals("Wrong mapping of nxt sensor preferences codes, maybe the order is wrong",
					correctMapping.get(i).second.getSensorCode(), sensorPreferencesCodes[i]);
		}
	}
}
