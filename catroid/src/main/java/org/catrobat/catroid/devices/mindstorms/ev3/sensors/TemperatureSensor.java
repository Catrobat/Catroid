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

package org.catrobat.catroid.devices.mindstorms.ev3.sensors;

import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TemperatureSensor extends EV3Sensor {

	public static final String TAG = TemperatureSensor.class.getSimpleName();
	private static final int DEFAULT_VALUE = 0;
	private static final int SENSOR_VALUE_READ_LENGTH = 4;

	public TemperatureSensor(int port, MindstormsConnection connection, EV3SensorMode mode) {
		super(port, EV3SensorType.NXT_TEMPERATURE, mode, connection);
		lastValidValue = DEFAULT_VALUE;
	}

	@Override
	public float getValue() {
		float temperature = ByteBuffer.wrap(getSiValue(SENSOR_VALUE_READ_LENGTH)).order(ByteOrder.LITTLE_ENDIAN)
				.getFloat();

		return temperature;
	}
}
