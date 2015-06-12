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

public enum NXTSensorType {
	NO_SENSOR(0x00), TOUCH(0x01), TEMPERATURE(0x02),
	LIGHT_ACTIVE(0x05), LIGHT_INACTIVE(0x06),
	SOUND_DB(0x07), SOUND_DBA(0x08),
	LOW_SPEED(0x0A), LOW_SPEED_9V(0x0B);

	private int sensorTypeValue;

	private NXTSensorType(int sensorTypeValue) {
		this.sensorTypeValue = sensorTypeValue;
	}

	public byte getByte() {
		return (byte) sensorTypeValue;
	}
}
