/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.devices.mindstorms.ev3.sensors;

import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;

public class EV3SensorFactory {

	private MindstormsConnection connection;

	public EV3SensorFactory(MindstormsConnection connection) {
		this.connection = connection;
	}

	public EV3Sensor create(EV3Sensor.Sensor sensorType, int port) {

		switch (sensorType) {
			case INFRARED:
				return new EV3InfraredSensor(port, connection);
			case COLOR:
				return new EV3ColorSensor(port, connection, EV3SensorMode.MODE2);
			case COLOR_AMBIENT:
				return new EV3ColorSensor(port, connection, EV3SensorMode.MODE0);
			case COLOR_REFLECT:
				return new EV3ColorSensor(port, connection, EV3SensorMode.MODE1);
			case TOUCH:
				return new EV3TouchSensor(port, connection);
			case HT_NXT_COLOR:
				return new HiTechnicColorSensor(port, connection, EV3SensorMode.MODE1);
			case NXT_TEMPERATURE_C:
				return new TemperatureSensor(port, connection, EV3SensorMode.MODE0);
			case NXT_TEMPERATURE_F:
				return new TemperatureSensor(port, connection, EV3SensorMode.MODE1);
			case NXT_LIGHT:
				return new EV3LightSensorNXT(port, connection, EV3SensorMode.MODE1);
			case NXT_LIGHT_ACTIVE:
				return new EV3LightSensorNXT(port, connection, EV3SensorMode.MODE0);
			default:
				throw new MindstormsException("No valid sensor found!"); // Should never occur
		}
	}
}
