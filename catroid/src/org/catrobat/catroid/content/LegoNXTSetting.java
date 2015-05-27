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

package org.catrobat.catroid.content;

import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;

import java.io.Serializable;

public class LegoNXTSetting implements Setting {

	private static final long serialVersionUID = 1L;

	private NXTPort[] portSensorMapping = null;

	public LegoNXTSetting(NXTSensor.Sensor[] sensorMapping) {
		portSensorMapping = new NXTPort[4];
		for (int i = 0; i < portSensorMapping.length; ++i) {
			portSensorMapping[i] = new NXTPort(i, sensorMapping[i]);
		}
	}

	public void updateMapping(NXTSensor.Sensor[] sensorMapping) {
		for (int i = 0; i < portSensorMapping.length; ++i) {
			portSensorMapping[i].setNumber(i);
			portSensorMapping[i].setSensor(sensorMapping[i]);
		}
	}

	public NXTSensor.Sensor[] getSensorMapping() {
		NXTSensor.Sensor[] sensorMapping = new NXTSensor.Sensor[4];
		for (int i = 0; i < portSensorMapping.length; ++i) {
			sensorMapping[i] = portSensorMapping[i].getSensor();
		}

		return sensorMapping;
	}

	public static class NXTPort implements Serializable {

		private int number;
		private NXTSensor.Sensor sensor;

		public NXTPort(int number, NXTSensor.Sensor sensor) {
			this.number = number;
			this.sensor = sensor;
		}

		public NXTSensor.Sensor getSensor() {
			return sensor;
		}

		public void setSensor(NXTSensor.Sensor sensor) {
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
