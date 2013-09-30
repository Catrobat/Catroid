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

import android.os.Handler;
import android.util.Log;

import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.util.ArrayList;

public class SensorRobotAlbert {

	private static SensorRobotAlbert instance = null;
	private static final int UPDATE_INTERVAL = 50; //New sensordata comes around every 40ms

	private ArrayList<SensorCustomEventListener> listenerList = new ArrayList<SensorCustomEventListener>();

	private Handler handler;
	private float[] distance = new float[2];
	private static boolean DEBUG_OUTPUT = true;

	public static final String KEY_SETTINGS_ROBOT_ALBERT_BRICKS = "setting_robot_albert_bricks";
	public boolean usingRobotAlbertBricks = false;

	//Periodic update the distance_value
	Runnable updateDistance = new Runnable() {
		@Override
		public void run() {

			distance[0] = SensorData.getInstance().getValueOfLeftDistanceSensor();
			distance[1] = SensorData.getInstance().getValueOfRightDistanceSensor();

			if (DEBUG_OUTPUT == true) {
				Log.d("SensorRobotAlbert", "LeftSensorvalue:  "
						+ SensorData.getInstance().getValueOfLeftDistanceSensor());
				Log.d("SensorRobotAlbert", "RightSensorvalue: "
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

	private SensorRobotAlbert() {
		handler = new Handler();
	};

	public static SensorRobotAlbert getSensorRobotAlbertInstance() {
		if (instance == null) {
			instance = new SensorRobotAlbert();
		}
		return instance;
	}

	public synchronized boolean registerListener(SensorCustomEventListener listener) {

		if (usingRobotAlbertBricks == false) {
			return false;
		}

		if (listenerList.contains(listener)) {
			return true;
		}
		listenerList.add(listener);

		try {
			updateDistance.run();
		} catch (Exception e) {
			Log.w(SensorRobotAlbert.class.getSimpleName(), "Could not register SensorCustomEventListener", e);
			listenerList.remove(listener);
			return false;
		}

		return true;
	}

	public synchronized void unregisterListener(SensorCustomEventListener listener) {
		if (listenerList.contains(listener)) {
			listenerList.remove(listener);
			if (listenerList.size() == 0) {
				handler.removeCallbacks(updateDistance);
			}

		}
	}

	//if robot albert bricks are used, set the variable true
	public void setBooleanAlbertBricks(boolean status) {
		usingRobotAlbertBricks = status;
	}
}
