/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

public class EV3ColorSensor extends EV3Sensor {

	public static final String TAG = EV3ColorSensor.class.getSimpleName();
	private static final int DEFAULT_VALUE = 50;

	public EV3ColorSensor(int port, MindstormsConnection connection, EV3SensorMode mode) {
		// Mode0: Reflected light
		// Mode1: Ambient light
		// Mode2: Color

		super(port, EV3SensorType.EV3_COLOR, mode, connection);
		lastValidValue = DEFAULT_VALUE;
	}

	@Override
	public int getValue() {
		if (this.sensorMode == EV3SensorMode.MODE2) {
			return getRawValue();
		} else {
			return getPercentValue();
		}
	}
}
