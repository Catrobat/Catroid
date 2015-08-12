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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.devices.arduino.phiro.Phiro;
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXT;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;

public final class SensorHandler implements SensorEventListener, SensorCustomEventListener {
	public static final float RADIAN_TO_DEGREE_CONST = 180f / (float) Math.PI;
	private static final String TAG = SensorHandler.class.getSimpleName();
	private static SensorHandler instance = null;
	private static BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);
	private SensorManagerInterface sensorManager = null;
	private Sensor linearAccelerationSensor = null;
	private Sensor accelerometerSensor = null;
	private Sensor magneticFieldSensor = null;
	private Sensor rotationVectorSensor = null;
	private float[] rotationMatrix = new float[16];
	private float[] rotationVector = new float[3];
	private float[] accelerationXYZ = new float[3];
	private float signAccelerationZ = 0f;
	private float[] gravity = new float[] { 0f, 0f, 0f };
	private boolean useLinearAccelerationFallback = false;
	private boolean useRotationVectorFallback = false;
	private float linearAccelerationX = 0f;
	private float linearAccelerationY = 0f;
	private float linearAccelerationZ = 0f;
	private float loudness = 0f;
	private float faceDetected = 0f;
	private float faceSize = 0f;
	private float facePositionX = 0f;
	private float facePositionY = 0f;

	private SensorHandler(Context context) {
		sensorManager = new SensorManager(
				(android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
		linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		if (linearAccelerationSensor == null) {
			useLinearAccelerationFallback = true;
		}

		rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		if (rotationVectorSensor == null) {
			useRotationVectorFallback = true;
		}

		if (useRotationVectorFallback) {
			accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		} else if (useLinearAccelerationFallback) {
			accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}

		Log.d(TAG, "*** LINEAR_ACCELERATION SENSOR: " + linearAccelerationSensor);
		Log.d(TAG, "*** ACCELEROMETER SENSOR: " + accelerometerSensor);
		Log.d(TAG, "*** ROTATION_VECTOR SENSOR: " + rotationVectorSensor);
		Log.d(TAG, "*** MAGNETIC_FIELD SENSOR: " + magneticFieldSensor);
	}

	public static void startSensorListener(Context context) {

		if (instance == null) {
			instance = new SensorHandler(context);
		}
		instance.sensorManager.unregisterListener((SensorEventListener) instance);
		instance.sensorManager.unregisterListener((SensorCustomEventListener) instance);

		instance.registerListener(instance);

		instance.sensorManager.registerListener(instance, Sensors.LOUDNESS);
		FaceDetectionHandler.registerOnFaceDetectedListener(instance);
		FaceDetectionHandler.registerOnFaceDetectionStatusListener(instance);
	}

	public static void registerListener(SensorEventListener listener) {
		if (instance == null) {
			return;
		}

		if (!instance.useLinearAccelerationFallback) {
			instance.sensorManager.registerListener(listener, instance.linearAccelerationSensor,
					android.hardware.SensorManager.SENSOR_DELAY_GAME);
		}

		if (!instance.useRotationVectorFallback) {
			instance.sensorManager.registerListener(listener, instance.rotationVectorSensor,
					android.hardware.SensorManager.SENSOR_DELAY_GAME);
		}

		if (instance.useLinearAccelerationFallback || instance.useRotationVectorFallback) {
			instance.sensorManager.registerListener(listener, instance.accelerometerSensor,
					android.hardware.SensorManager.SENSOR_DELAY_GAME);
		}

		if (instance.useRotationVectorFallback && instance.magneticFieldSensor != null) {
			instance.sensorManager.registerListener(listener, instance.magneticFieldSensor,
					android.hardware.SensorManager.SENSOR_DELAY_GAME);
		}
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

		FaceDetectionHandler.unregisterOnFaceDetectedListener(instance);
		FaceDetectionHandler.unregisterOnFaceDetectionStatusListener(instance);
	}

	public static Double getSensorValue(Sensors sensor) {
		if (instance.sensorManager == null) {
			return 0d;
		}
		Double sensorValue;
		float[] rotationMatrixOut = new float[16];
		switch (sensor) {

			case X_ACCELERATION:
				if (ProjectManager.getInstance().isCurrentProjectLandscape()) {
					return (double) (-instance.linearAccelerationY);
				} else {
					return (double) instance.linearAccelerationX;
				}

			case Y_ACCELERATION:
				if (ProjectManager.getInstance().isCurrentProjectLandscape()) {
					return (double) instance.linearAccelerationX;
				} else {
					return (double) instance.linearAccelerationY;
				}

			case Z_ACCELERATION:
				return (double) instance.linearAccelerationZ;

			case COMPASS_DIRECTION:
				float[] orientations = new float[3];
				if (!instance.useRotationVectorFallback) {
					android.hardware.SensorManager.getRotationMatrixFromVector(instance.rotationMatrix,
							instance.rotationVector);
				}
				if (ProjectManager.getInstance().isCurrentProjectLandscape()) {
					android.hardware.SensorManager.remapCoordinateSystem(instance.rotationMatrix, android.hardware.SensorManager
							.AXIS_Y, android.hardware.SensorManager.AXIS_MINUS_X, rotationMatrixOut);
					android.hardware.SensorManager.getOrientation(rotationMatrixOut, orientations);
				} else {
					android.hardware.SensorManager.getOrientation(instance.rotationMatrix, orientations);
				}
				sensorValue = (double) orientations[0];
				return sensorValue * RADIAN_TO_DEGREE_CONST * -1f;

			case X_INCLINATION:
				if (instance.useRotationVectorFallback) {
					float rawInclinationX;
					if (ProjectManager.getInstance().isCurrentProjectLandscape()) {
						rawInclinationX = RADIAN_TO_DEGREE_CONST * (float) (Math.acos(instance
								.accelerationXYZ[1]));
					} else {
						rawInclinationX = RADIAN_TO_DEGREE_CONST * (float) (Math.acos(instance.accelerationXYZ[0]));
					}
					float correctedInclinationX = 0;

					if (rawInclinationX >= 90 && rawInclinationX <= 180) {
						if (instance.signAccelerationZ > 0) {
							correctedInclinationX = -(rawInclinationX - 90);
						} else {
							correctedInclinationX = -(180 + (90 - rawInclinationX));
						}
					} else if (rawInclinationX >= 0 && rawInclinationX < 90) {
						if (instance.signAccelerationZ > 0) {
							correctedInclinationX = (90 - rawInclinationX);
						} else {
							correctedInclinationX = (90 + rawInclinationX);
						}
					}
					if (ProjectManager.getInstance().isCurrentProjectLandscape()) {
						correctedInclinationX = -correctedInclinationX;
					}
					return (double) correctedInclinationX;
				} else {
					orientations = new float[3];
					android.hardware.SensorManager.getRotationMatrixFromVector(instance.rotationMatrix,
							instance.rotationVector);
					if (ProjectManager.getInstance().isCurrentProjectLandscape()) {
						android.hardware.SensorManager.remapCoordinateSystem(instance.rotationMatrix, android.hardware.SensorManager
								.AXIS_Y, android.hardware.SensorManager.AXIS_MINUS_X, rotationMatrixOut);
						android.hardware.SensorManager.getOrientation(rotationMatrixOut, orientations);
					} else {
						android.hardware.SensorManager.getOrientation(instance.rotationMatrix, orientations);
					}
					sensorValue = (double) orientations[2];
					return sensorValue * RADIAN_TO_DEGREE_CONST * -1f;
				}
			case Y_INCLINATION:
				if (instance.useRotationVectorFallback) {
					float rawInclinationY;
					if (ProjectManager.getInstance().isCurrentProjectLandscape()) {
						rawInclinationY = RADIAN_TO_DEGREE_CONST * (float) (Math.acos(instance.accelerationXYZ[0]));
					} else {
						rawInclinationY = RADIAN_TO_DEGREE_CONST * (float) (Math.acos(instance.accelerationXYZ[1]));
					}
					float correctedInclinationY = 0;
					if (rawInclinationY >= 90 && rawInclinationY <= 180) {
						if (instance.signAccelerationZ > 0) {
							correctedInclinationY = -(rawInclinationY - 90);
						} else {
							correctedInclinationY = -(180 + (90 - rawInclinationY));
						}
					} else if (rawInclinationY >= 0 && rawInclinationY < 90) {
						if (instance.signAccelerationZ > 0) {
							correctedInclinationY = (90 - rawInclinationY);
						} else {
							correctedInclinationY = (90 + rawInclinationY);
						}
					}
					return (double) correctedInclinationY;
				} else {
					orientations = new float[3];
					android.hardware.SensorManager.getRotationMatrixFromVector(instance.rotationMatrix,
							instance.rotationVector);
					if (ProjectManager.getInstance().isCurrentProjectLandscape()) {
						android.hardware.SensorManager.remapCoordinateSystem(instance.rotationMatrix, android.hardware.SensorManager
								.AXIS_Y, android.hardware.SensorManager.AXIS_MINUS_X, rotationMatrixOut);
						android.hardware.SensorManager.getOrientation(rotationMatrixOut, orientations);
					} else {
						android.hardware.SensorManager.getOrientation(instance.rotationMatrix, orientations);
					}

					float xInclinationUsedToExtendRangeOfRoll = orientations[2] * RADIAN_TO_DEGREE_CONST * -1f;

					sensorValue = (double) orientations[1];

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
				}
			case FACE_DETECTED:
				return (double) instance.faceDetected;
			case FACE_SIZE:
				return (double) instance.faceSize;
			case FACE_X_POSITION:
				if (ProjectManager.getInstance().isCurrentProjectLandscape()) {
					return (double) (-instance.facePositionY);
				} else {
					return (double) instance.facePositionX;
				}
			case FACE_Y_POSITION:
				if (ProjectManager.getInstance().isCurrentProjectLandscape()) {
					return (double) instance.facePositionX;
				} else {
					return (double) instance.facePositionY;
				}
			case LOUDNESS:
				return Double.valueOf(instance.loudness);

			case NXT_SENSOR_1:
			case NXT_SENSOR_2:
			case NXT_SENSOR_3:
			case NXT_SENSOR_4:

				LegoNXT nxt = btService.getDevice(BluetoothDevice.LEGO_NXT);
				if (nxt != null) {
					return Double.valueOf(nxt.getSensorValue(sensor));
				}
				break;

			case PHIRO_BOTTOM_LEFT:
			case PHIRO_BOTTOM_RIGHT:
			case PHIRO_FRONT_LEFT:
			case PHIRO_FRONT_RIGHT:
			case PHIRO_SIDE_LEFT:
			case PHIRO_SIDE_RIGHT:
				Phiro phiro = btService.getDevice(BluetoothDevice.PHIRO);
				if (phiro != null) {
					return Double.valueOf(phiro.getSensorValue(sensor));
				}
				break;
		}
		return 0d;
	}

	public static void clearFaceDetectionValues() {
		if (instance != null) {
			instance.faceDetected = 0f;
			instance.faceSize = 0f;
			instance.facePositionX = 0f;
			instance.facePositionY = 0f;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
			case Sensor.TYPE_MAGNETIC_FIELD:
				float[] tempMagneticFieldXYZ = event.values.clone();
				float[] tempInclinationMatrix = new float[9];
				android.hardware.SensorManager.getRotationMatrix(rotationMatrix, tempInclinationMatrix, accelerationXYZ,
						tempMagneticFieldXYZ);    //http://goo.gl/wo6QK5
				break;
			case Sensor.TYPE_ACCELEROMETER:
				accelerationXYZ = event.values.clone();
				if (useLinearAccelerationFallback) {
					determinePseudoLinearAcceleration(accelerationXYZ.clone());
				}
				double normOfG = Math.sqrt(accelerationXYZ[0] * accelerationXYZ[0]
						+ accelerationXYZ[1] * accelerationXYZ[1]
						+ accelerationXYZ[2] * accelerationXYZ[2]);
				accelerationXYZ[0] = (float) (accelerationXYZ[0] / normOfG);
				accelerationXYZ[1] = (float) (accelerationXYZ[1] / normOfG);
				accelerationXYZ[2] = (float) (accelerationXYZ[2] / normOfG);
				signAccelerationZ = Math.signum(event.values[2]);
				break;
			case Sensor.TYPE_LINEAR_ACCELERATION:
				linearAccelerationX = event.values[0];
				linearAccelerationY = event.values[1];
				linearAccelerationZ = event.values[2];
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

	private void determinePseudoLinearAcceleration(float[] input) {
		float alpha = 0.8f;

		gravity[0] = alpha * gravity[0] + ((1 - alpha) * input[0]);
		gravity[1] = alpha * gravity[1] + ((1 - alpha) * input[1]);
		gravity[2] = alpha * gravity[2] + ((1 - alpha) * input[2]);

		linearAccelerationX = -1f * (input[0] - gravity[0]);
		linearAccelerationY = -1f * (input[1] - gravity[1]);
		linearAccelerationZ = -1f * (input[2] - gravity[2]);
	}

	@Override
	public void onCustomSensorChanged(SensorCustomEvent event) {
		switch (event.sensor) {
			case LOUDNESS:
				instance.loudness = event.values[0];
				break;
			case FACE_DETECTED:
				instance.faceDetected = event.values[0];
				break;
			case FACE_SIZE:
				instance.faceSize = event.values[0];
				break;
			case FACE_X_POSITION:
				instance.facePositionX = event.values[0];
				break;
			case FACE_Y_POSITION:
				instance.facePositionY = event.values[0];
				break;
			default:
				Log.v(TAG, "Unhandled sensor: " + event.sensor);
		}
	}
}
