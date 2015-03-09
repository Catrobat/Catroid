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
package org.catrobat.catroid.devices.mindstorms.nxt.sensors;

import android.content.Context;

import org.catrobat.catroid.R;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;

public class NXTSensorFactory {

	private Context context;
	private MindstormsConnection connection;

	public NXTSensorFactory(Context context, MindstormsConnection connection) {
		this.context = context;
		this.connection = connection;
	}

	public NXTSensor create(String sensorTypeName, int port) {

		if (equals(sensorTypeName, R.string.nxt_sensor_touch)) {
			return new NXTTouchSensor(port, connection);
		}

		if (equals(sensorTypeName, R.string.nxt_sensor_sound)) {
			return new NXTSoundSensor(port, connection);
		}

		if (equals(sensorTypeName, R.string.nxt_sensor_light)) {
			return new NXTLightSensor(port, connection);
		}

		if (equals(sensorTypeName, R.string.nxt_sensor_ultrasonic)) {
			return new NXTI2CUltraSonicSensor(connection);
		}

		throw new MindstormsException("No valid sensor found!"); // Should never occur
	}

	public boolean isSensorAssigned(String sensorTypeName) {
			return !( equals(sensorTypeName, R.string.nxt_no_sensor)
					  || sensorTypeName == null
					  || sensorTypeName.isEmpty()
			);
	}

	private boolean equals(String sensorTypeName, int sensorType) {
		return sensorTypeName.equals(context.getString(sensorType));
	}

}
