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
package org.catrobat.catroid.formulaeditor;

import android.util.Log;

public enum Sensors {
	X_ACCELERATION, Y_ACCELERATION, Z_ACCELERATION, COMPASS_DIRECTION, X_INCLINATION, Y_INCLINATION, LOUDNESS,
	FACE_DETECTED, FACE_SIZE, FACE_X_POSITION, FACE_Y_POSITION, OBJECT_X(true), OBJECT_Y(true),
	OBJECT_TRANSPARENCY(true), OBJECT_BRIGHTNESS(true), OBJECT_SIZE(true), OBJECT_ROTATION(true), OBJECT_LAYER(true),
	NXT_SENSOR_1, NXT_SENSOR_2, NXT_SENSOR_3, NXT_SENSOR_4, PHIRO_FRONT_LEFT, PHIRO_FRONT_RIGHT, PHIRO_SIDE_LEFT,
	PHIRO_SIDE_RIGHT, PHIRO_BOTTOM_LEFT, PHIRO_BOTTOM_RIGHT;

	public final boolean isObjectSensor;
	public static final String TAG = Sensors.class.getSimpleName();

	Sensors(boolean isObjectSensor) {
		this.isObjectSensor = isObjectSensor;
	}

	Sensors() {
		this.isObjectSensor = false;
	}

	public static boolean isSensor(String value) {
		return getSensorByValue(value) != null;
	}

	public static Sensors getSensorByValue(String value) {
		try {
			return valueOf(value);
		} catch (IllegalArgumentException illegalArgumentException) {
			Log.e(TAG, Log.getStackTraceString(illegalArgumentException));
		}
		return null;
	}
}
