/*
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.devices.albert;

import android.os.Handler;
import android.util.Log;

import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.util.ArrayList;
import java.util.List;

public final class SensorRobotAlbert {

	//TODO: albert include albert sensor in formula editor
	public static final String KEY_SETTINGS_ROBOT_ALBERT_BRICKS = "setting_robot_albert_bricks";

	private static final int UPDATE_INTERVAL = 50; //New sensor data comes around every 40ms
	private static final String TAG = SensorRobotAlbert.class.getSimpleName();

	private static SensorRobotAlbert instance;

	private boolean usingRobotAlbertBricks;
	private List<SensorCustomEventListener> listenerList = new ArrayList<SensorCustomEventListener>();
	private Handler handler;

	private SensorRobotAlbert() {
		handler = new Handler();
	}    //Periodic update the distance_value

	private Runnable updateDistance = new Runnable() {
		@Override
		public void run() {
			float[] distance = new float[2];
			distance[0] = SensorData.getInstance().getValueOfLeftDistanceSensor();
			distance[1] = SensorData.getInstance().getValueOfRightDistanceSensor();

			if (AlbertImpl.ALBERT_SENSOR_DEBUG_OUTPUT) {
				Log.d(TAG, "Left Sensor value:  "
						+ SensorData.getInstance().getValueOfLeftDistanceSensor());
				Log.d(TAG, "Right Sensor value: "
						+ SensorData.getInstance().getValueOfRightDistanceSensor());
			}

			SensorCustomEvent eventLeft = new SensorCustomEvent(Sensors.ALBERT_ROBOT_DISTANCE_LEFT, distance);
			SensorCustomEvent eventRight = new SensorCustomEvent(Sensors.ALBERT_ROBOT_DISTANCE_RIGHT, distance);
			for (SensorCustomEventListener listener : listenerList) {
				listener.onCustomSensorChanged(eventLeft);
				listener.onCustomSensorChanged(eventRight);
			}
			handler.postDelayed(updateDistance, UPDATE_INTERVAL);
		}
	};

	public static synchronized SensorRobotAlbert getSensorRobotAlbertInstance() {
		if (instance == null) {
			instance = new SensorRobotAlbert();
		}
		return instance;
	}

	public synchronized boolean registerListener(SensorCustomEventListener listener) {
		if (!usingRobotAlbertBricks) {
			return false;
		}

		if (listenerList.contains(listener)) {
			return true;
		}
		listenerList.add(listener);
		updateDistance.run();
		return true;
	}

	public synchronized void unregisterListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.remove(listener);
			if (listenerList.isEmpty()) {
				handler.removeCallbacks(updateDistance);
			}
		}
	}

	//if robot albert bricks are used, set the variable true
	public void setBooleanAlbertBricks(boolean status) {
		usingRobotAlbertBricks = status;
	}

	public boolean getBooleanAlbertBricksUsed() {
		return usingRobotAlbertBricks;
	}


}
