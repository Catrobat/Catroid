/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.robot.albert;

public class SensorData {

	private static SensorData instance = null;

	//Sensor variables
	private int left_distance_sensor = 0; //0=infinity 100=0mm
	private int right_distance_sensor = 0;

	private SensorData() {
	}

	public static SensorData getInstance() {
		if (instance == null) {
			instance = new SensorData();
		}
		return instance;
	}

	public void setValueOfLeftDistanceSensor(int value) {
		left_distance_sensor = value;
	}

	public int getValueOfLeftDistanceSensor() {
		return left_distance_sensor;
	}

	public void setValueOfRightDistanceSensor(int value) {
		right_distance_sensor = value;
	}

	public int getValueOfRightDistanceSensor() {
		return right_distance_sensor;
	}

}
