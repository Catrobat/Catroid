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

package org.catrobat.catroid.content;

import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;

import java.io.Serializable;

public class LegoEV3Setting implements Setting {

	private static final long serialVersionUID = 1L;

	private EV3Port[] portSensorMapping = null;

	public LegoEV3Setting(EV3Sensor.Sensor[] sensorMapping) {
		portSensorMapping = new EV3Port[4];
		for (int i = 0; i < portSensorMapping.length; ++i) {
			portSensorMapping[i] = new EV3Port(i, sensorMapping[i]);
		}
	}

	public void updateMapping(EV3Sensor.Sensor[] sensorMapping) {
		for (int i = 0; i < portSensorMapping.length; ++i) {
			portSensorMapping[i].setNumber(i);
			portSensorMapping[i].setSensor(sensorMapping[i]);
		}
	}

	public EV3Sensor.Sensor[] getSensorMapping() {
		EV3Sensor.Sensor[] sensorMapping = new EV3Sensor.Sensor[4];
		for (int i = 0; i < portSensorMapping.length; ++i) {
			sensorMapping[i] = portSensorMapping[i].getSensor();
		}

		return sensorMapping;
	}

	public static class EV3Port implements Serializable {

		private int number;
		private EV3Sensor.Sensor sensor;

		public EV3Port(int number, EV3Sensor.Sensor sensor) {
			this.number = number;
			this.sensor = sensor;
		}

		public EV3Sensor.Sensor getSensor() {
			return sensor;
		}

		public void setSensor(EV3Sensor.Sensor sensor) {
			this.sensor = sensor;
		}

		public int getNumber() {
			return number;
		}

		public void setNumber(int number) {
			this.number = number;
		}
	}
}
