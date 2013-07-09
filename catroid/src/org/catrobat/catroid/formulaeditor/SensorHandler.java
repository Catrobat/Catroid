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
package org.catrobat.catroid.formulaeditor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class SensorHandler implements SensorEventListener, SensorCustomEventListener {
	private static final String TAG = SensorHandler.class.getSimpleName();
	private static SensorHandler instance = null;
	private SensorManagerInterface sensorManager = null;
	private Sensor accelerometerSensor = null;
	private Sensor rotationVectorSensor = null;
	private float[] rotationMatrix = new float[16];
	private float[] rotationVector = new float[3];
	public static final float radianToDegreeConst = 180f / (float) Math.PI;

	private float linearAcceleartionX = 0f;
	private float linearAcceleartionY = 0f;
	private float linearAcceleartionZ = 0f;

	private float loudness = 0f;

	private SensorHandler(Context context) {
		sensorManager = new SensorManager(
				(android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
		accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
	}

	public static void startSensorListener(Context context) {

		if (instance == null) {
			instance = new SensorHandler(context);
		}
		instance.sensorManager.unregisterListener((SensorEventListener) instance);
		instance.sensorManager.unregisterListener((SensorCustomEventListener) instance);
		instance.sensorManager.registerListener(instance, instance.accelerometerSensor,
				android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
		instance.sensorManager.registerListener(instance, instance.rotationVectorSensor,
				android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
		instance.sensorManager.registerListener(instance, Sensors.LOUDNESS);
	}

	public static void registerListener(SensorEventListener listener) {
		if (instance == null) {
			return;
		}
		instance.sensorManager.registerListener(listener, instance.accelerometerSensor,
				android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
		instance.sensorManager.registerListener(listener, instance.rotationVectorSensor,
				android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
	}

	public static void unregisterListener(SensorEventListener listener) {
		if (instance == null) {
			return;
		}
		instance.sensorManager.unregisterListener(listener);
	}

	public static void stopSensorListeners() {
		if (instance == null) {
			return;
		}
		instance.sensorManager.unregisterListener((SensorEventListener) instance);
		instance.sensorManager.unregisterListener((SensorCustomEventListener) instance);
	}

	public static Double getSensorValue(Sensors sensor) {
		if (instance.sensorManager == null) {
			return 0d;
		}
		Double sensorValue = 0.0;
		switch (sensor) {

			case X_ACCELERATION:
				return Double.valueOf(instance.linearAcceleartionX);

			case Y_ACCELERATION:
				return Double.valueOf(instance.linearAcceleartionY);

			case Z_ACCELERATION:
				return Double.valueOf(instance.linearAcceleartionZ);

			case COMPASS_DIRECTION:
				float[] orientations = new float[3];
				getRotationMatrixFromVector(instance.rotationMatrix, instance.rotationVector);
				android.hardware.SensorManager.getOrientation(instance.rotationMatrix, orientations);
				sensorValue = Double.valueOf(orientations[0]);
				return sensorValue * radianToDegreeConst * -1f;

			case X_INCLINATION:

				orientations = new float[3];
				getRotationMatrixFromVector(instance.rotationMatrix, instance.rotationVector);
				android.hardware.SensorManager.getOrientation(instance.rotationMatrix, orientations);
				sensorValue = Double.valueOf(orientations[2]);
				return sensorValue * radianToDegreeConst * -1f;

			case Y_INCLINATION:
				orientations = new float[3];
				getRotationMatrixFromVector(instance.rotationMatrix, instance.rotationVector);
				android.hardware.SensorManager.getOrientation(instance.rotationMatrix, orientations);
				sensorValue = Double.valueOf(orientations[1]);
				return sensorValue * radianToDegreeConst * -1f;

			case LOUDNESS:
				return Double.valueOf(instance.loudness);
		}

		return 0d;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	@Override
	public void onCustomAccuracyChanged(Sensors sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
			case Sensor.TYPE_LINEAR_ACCELERATION:
				linearAcceleartionX = event.values[0];
				linearAcceleartionY = event.values[1];
				linearAcceleartionZ = event.values[2];
				break;
			case Sensor.TYPE_ROTATION_VECTOR:
				rotationVector[0] = event.values[0];
				rotationVector[1] = event.values[1];
				rotationVector[2] = event.values[2];
				break;
			default:
				Log.v(TAG, "Unhandled sensor type: " + event.sensor.getType());
		}

	}

	@Override
	public void onCustomSensorChanged(SensorCustomEvent event) {
		switch (event.sensor) {
			case LOUDNESS:
				instance.loudness = event.values[0];
				break;
			default:
				Log.v(TAG, "Unhandled sensor: " + event.sensor);
		}
	}

	//For API Level < 9
	//Taken from: https://android.googlesource.com/platform/frameworks/base/+/fa33565714e4192dbab446ee1fbccb87dd414bed/core/java/android/hardware/SensorManager.java
	public static void getRotationMatrixFromVector(float[] R, float[] rotationVector) {

		float q0;
		float q1 = rotationVector[0];
		float q2 = rotationVector[1];
		float q3 = rotationVector[2];

		q0 = 1 - q1 * q1 - q2 * q2 - q3 * q3;
		q0 = (q0 > 0) ? (float) java.lang.Math.sqrt(q0) : 0;

		float sq_q1 = 2 * q1 * q1;
		float sq_q2 = 2 * q2 * q2;
		float sq_q3 = 2 * q3 * q3;
		float q1_q2 = 2 * q1 * q2;
		float q3_q0 = 2 * q3 * q0;
		float q1_q3 = 2 * q1 * q3;
		float q2_q0 = 2 * q2 * q0;
		float q2_q3 = 2 * q2 * q3;
		float q1_q0 = 2 * q1 * q0;

		R[0] = 1 - sq_q2 - sq_q3;
		R[1] = q1_q2 - q3_q0;
		R[2] = q1_q3 + q2_q0;
		R[3] = 0.0f;

		R[4] = q1_q2 + q3_q0;
		R[5] = 1 - sq_q1 - sq_q3;
		R[6] = q2_q3 - q1_q0;
		R[7] = 0.0f;

		R[8] = q1_q3 - q2_q0;
		R[9] = q2_q3 + q1_q0;
		R[10] = 1 - sq_q1 - sq_q2;
		R[11] = 0.0f;

		R[12] = R[13] = R[14] = 0.0f;
		R[15] = 1.0f;

	}
}
