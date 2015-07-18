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

import android.hardware.Sensor;
import android.hardware.SensorEventListener;

public class SensorManager implements SensorManagerInterface {
	private final android.hardware.SensorManager sensorManager;

	public SensorManager(android.hardware.SensorManager sensorManager) {
		this.sensorManager = sensorManager;
	}

	@Override
	public void unregisterListener(SensorEventListener listener) {
		this.sensorManager.unregisterListener(listener);
	}

	@Override
	public boolean registerListener(SensorEventListener listener, Sensor sensor, int rate) {
		return this.sensorManager.registerListener(listener, sensor, rate);
	}

	@Override
	public Sensor getDefaultSensor(int type) {
		return this.sensorManager.getDefaultSensor(type);
	}

	@Override
	public void unregisterListener(SensorCustomEventListener listener) {
		SensorLoudness.getSensorLoudness().unregisterListener(listener);
	}

	@Override
	public boolean registerListener(SensorCustomEventListener listener, Sensors sensor) {
		switch (sensor) {
			case LOUDNESS:
				return SensorLoudness.getSensorLoudness().registerListener(listener);
			default:
				return false;
		}
	}
}
