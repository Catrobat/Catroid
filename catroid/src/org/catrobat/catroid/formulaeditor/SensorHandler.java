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

public final class SensorHandler implements SensorEventListener, SensorCustomEventListener {
	private static final String TAG = SensorHandler.class.getSimpleName();
	private static SensorHandler instance = null;
	private SensorManagerInterface sensorManager = null;
	private Sensor accelerometerSensor = null;
	private Sensor rotationVectorSensor = null;
	private float[] rotationMatrix = new float[16];
	private float[] rotationVector = new float[3];
	public static final float RADIAN_TO_DEGREE_CONST = 180f / (float) Math.PI;

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
				android.hardware.SensorManager.getRotationMatrixFromVector(instance.rotationMatrix,
						instance.rotationVector);
				android.hardware.SensorManager.getOrientation(instance.rotationMatrix, orientations);
				sensorValue = Double.valueOf(orientations[0]);
				return sensorValue * RADIAN_TO_DEGREE_CONST * -1f;

			case X_INCLINATION:

				orientations = new float[3];
				android.hardware.SensorManager.getRotationMatrixFromVector(instance.rotationMatrix,
						instance.rotationVector);
				android.hardware.SensorManager.getOrientation(instance.rotationMatrix, orientations);
				sensorValue = Double.valueOf(orientations[2]);
				return sensorValue * RADIAN_TO_DEGREE_CONST * -1f;

			case Y_INCLINATION:
				orientations = new float[3];
				android.hardware.SensorManager.getRotationMatrixFromVector(instance.rotationMatrix,
						instance.rotationVector);
				android.hardware.SensorManager.getOrientation(instance.rotationMatrix, orientations);

				float xInclinationUsedToExtendRangeOfRoll = orientations[2] * RADIAN_TO_DEGREE_CONST * -1f;

				sensorValue = Double.valueOf(orientations[1]);

				if (Math.abs(xInclinationUsedToExtendRangeOfRoll) <= 90f) {
					return sensorValue * RADIAN_TO_DEGREE_CONST * -1f;
				} else {
					float uncorrectedYInclination = sensorValue.floatValue() * RADIAN_TO_DEGREE_CONST * -1f;

					if (uncorrectedYInclination > 0f) {
						return (double) 180f - uncorrectedYInclination;
					} else {
						return (double) -180f - uncorrectedYInclination;
					}
				}

			case LOUDNESS:
				return Double.valueOf(instance.loudness);
		}
		return 0d;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
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
}
