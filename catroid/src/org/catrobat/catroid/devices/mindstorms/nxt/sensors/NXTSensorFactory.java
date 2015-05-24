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

import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;

public class NXTSensorFactory {

	private MindstormsConnection connection;

	public NXTSensorFactory(MindstormsConnection connection) {
		this.connection = connection;
	}

	public NXTSensor create(NXTSensor.Sensor sensorType, int port) {

		switch (sensorType) {
			case TOUCH:
				return new NXTTouchSensor(port, connection);

			case SOUND:
				return new NXTSoundSensor(port, connection);

			case LIGHT_INACTIVE:
				return new NXTLightSensor(port, connection);

			case LIGHT_ACTIVE:
				return new NXTLightSensorActive(port, connection);

			case ULTRASONIC:
				return new NXTI2CUltraSonicSensor(connection);

			default:
				throw new MindstormsException("No valid sensor found!"); // Should never occur
		}
	}
}
