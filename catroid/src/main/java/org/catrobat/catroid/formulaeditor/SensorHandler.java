/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.camera.Position;
import org.catrobat.catroid.camera.VisualDetectionHandler;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.devices.arduino.phiro.Phiro;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3;
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXT;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.utils.TouchUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

public final class SensorHandler implements SensorEventListener, SensorCustomEventListener, LocationListener,
		GpsStatus.Listener {
	private static final float RADIAN_TO_DEGREE_CONST = 180f / (float) Math.PI;
	private static final String TAG = SensorHandler.class.getSimpleName();
	private static SensorHandler instance;
	private static final BluetoothDeviceService BT_SERVICE = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);
	private SensorManagerInterface sensorManager;
	private Sensor linearAccelerationSensor;
	private Sensor accelerometerSensor;
	private Sensor magneticFieldSensor;
	private Sensor rotationVectorSensor;
	private final float[] rotationMatrix = new float[16];
	private final float[] rotationVector = new float[3];
	private float[] accelerationXYZ = new float[3];
	private float signAccelerationZ;
	private final float[] gravity = {0f, 0f, 0f};
	private boolean useLinearAccelerationFallback;
	private boolean useRotationVectorFallback;
	private float linearAccelerationX;
	private float linearAccelerationY;
	private float linearAccelerationZ;
	private static String userLocaleTag = Locale.getDefault().toLanguageTag();

	private boolean compassAvailable = true;
	private boolean accelerationAvailable = true;
	private boolean inclinationAvailable = true;

	private LocationManager locationManager;
	private boolean isGpsConnected;
	private Location lastLocationGps;
	private long lastLocationGpsMillis;

	public static double timerReferenceValue;
	public static double timerPauseValue;

	private static String listeningLanguageSensor;

	private SensorLoudness sensorLoudness;

	private final Map<Sensors, Object> sensorValueMap = new HashMap();

	public static void setUserLocaleTag(String userLocale) {
		userLocaleTag = userLocale;
	}

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
			if (accelerometerSensor == null) {
				accelerationAvailable = false;
				inclinationAvailable = false;
			}
			if (magneticFieldSensor == null) {
				compassAvailable = false;
			}
		} else if (useLinearAccelerationFallback) {
			accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if (accelerometerSensor == null) {
				accelerationAvailable = false;
			}
		}
	}

	public boolean compassAvailable() {
		return compassAvailable;
	}

	public void setLocationManager(LocationManager locationManager) {
		this.locationManager = locationManager;
	}

	public static boolean gpsAvailable() {
		return gpsSensorAvailable() | networkGpsAvailable();
	}

	private static boolean gpsSensorAvailable() {
		return instance.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	private static boolean networkGpsAvailable() {
		return instance.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	private static double startWeekWithMonday() {
		int weekdayOfAndroidCalendar = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		int convertedWeekday;

		if (weekdayOfAndroidCalendar == Calendar.SUNDAY) {
			convertedWeekday = Calendar.SATURDAY;
		} else {
			convertedWeekday = weekdayOfAndroidCalendar - 1;
		}

		return convertedWeekday;
	}

	public boolean accelerationAvailable() {
		return accelerationAvailable;
	}

	public boolean inclinationAvailable() {
		return inclinationAvailable;
	}

	public static SensorHandler getInstance(Context context) {
		if (instance == null) {
			instance = new SensorHandler(context);
		}
		return instance;
	}

	@VisibleForTesting
	public void setAccelerationUnavailable() {
		accelerationAvailable = false;
	}

	@SuppressWarnings("MissingPermission")
	public static void startSensorListener(Context context) {
		if (instance == null) {
			instance = new SensorHandler(context);
		}
		instance.sensorManager.unregisterListener((SensorEventListener) instance);

		registerListener(instance);

		VisualDetectionHandler.addListener(instance);

		if (instance.sensorLoudness != null) {
			instance.sensorLoudness.registerListener(instance);
		}

		if (instance.locationManager != null) {
			instance.locationManager.removeUpdates(instance);
			instance.locationManager.removeGpsStatusListener(instance);
			instance.locationManager.addGpsStatusListener(instance);
			if (gpsSensorAvailable()) {
				instance.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, instance);
			}
			if (networkGpsAvailable()) {
				instance.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, instance);
			}
		}
	}

	public void setSensorLoudness(SensorLoudness sensorLoudness) {
		this.sensorLoudness = sensorLoudness;
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

		if (instance.sensorLoudness != null) {
			instance.sensorLoudness.unregisterListener(instance);
		}

		if (instance.locationManager != null) {
			instance.locationManager.removeUpdates(instance);
			instance.locationManager.removeGpsStatusListener(instance);
		}

		VisualDetectionHandler.removeListener(instance);
	}

	@NonNull
	public static Object getSensorValue(Sensors sensor) {
		if (instance.sensorManager == null) {
			return 0d;
		}
		float[] rotationMatrixOut = new float[16];

		switch (sensor) {
			case X_ACCELERATION:
				return getXAccordingToRotation(instance.linearAccelerationX,
						instance.linearAccelerationY);
			case Y_ACCELERATION:
				return getYAccordingToRotation(instance.linearAccelerationX,
						instance.linearAccelerationY);
			case Z_ACCELERATION:
				return (double) instance.linearAccelerationZ;

			case COMPASS_DIRECTION:
				return calculateCompassDirection(rotationMatrixOut);

			case X_INCLINATION:
				return calculateXInclination(rotationMatrixOut);
			case Y_INCLINATION:
				return calculateYInclination(rotationMatrixOut);

			case TIMER:
				return (SystemClock.uptimeMillis() - timerReferenceValue) / 1000d;
			case DATE_YEAR:
				return Double.valueOf(Calendar.getInstance().get(Calendar.YEAR));
			case DATE_MONTH:
				return Double.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
			case DATE_DAY:
				return Double.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
			case DATE_WEEKDAY:
				return startWeekWithMonday();
			case TIME_HOUR:
				return Double.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
			case TIME_MINUTE:
				return Double.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
			case TIME_SECOND:
				return Double.valueOf(Calendar.getInstance().get(Calendar.SECOND));

			case NXT_SENSOR_1:
			case NXT_SENSOR_2:
			case NXT_SENSOR_3:
			case NXT_SENSOR_4:

				LegoNXT nxt = BT_SERVICE.getDevice(BluetoothDevice.LEGO_NXT);
				if (nxt != null) {
					return Double.valueOf(nxt.getSensorValue(sensor));
				}
				break;

			case EV3_SENSOR_1:
			case EV3_SENSOR_2:
			case EV3_SENSOR_3:
			case EV3_SENSOR_4:
				LegoEV3 ev3 = BT_SERVICE.getDevice(BluetoothDevice.LEGO_EV3);
				if (ev3 != null) {
					return Double.valueOf(ev3.getSensorValue(sensor));
				}
				break;

			case PHIRO_BOTTOM_LEFT:
			case PHIRO_BOTTOM_RIGHT:
			case PHIRO_FRONT_LEFT:
			case PHIRO_FRONT_RIGHT:
			case PHIRO_SIDE_LEFT:
			case PHIRO_SIDE_RIGHT:
				Phiro phiro = BT_SERVICE.getDevice(BluetoothDevice.PHIRO);
				if (phiro != null) {
					return (double) phiro.getSensorValue(sensor);
				}
				break;

			case GAMEPAD_A_PRESSED:
			case GAMEPAD_B_PRESSED:
			case GAMEPAD_DOWN_PRESSED:
			case GAMEPAD_LEFT_PRESSED:
			case GAMEPAD_RIGHT_PRESSED:
			case GAMEPAD_UP_PRESSED:
				return CastManager.getInstance().isButtonPressed(sensor) ? 1.0 : 0.0;

			case LAST_FINGER_INDEX:
				return Double.valueOf(TouchUtil.getLastTouchIndex());
			case FINGER_TOUCHED:
				return TouchUtil.isTouching();
			case FINGER_X:
				return Double.valueOf(TouchUtil.getX(TouchUtil.getLastTouchIndex()));
			case FINGER_Y:
				return Double.valueOf(TouchUtil.getY(TouchUtil.getLastTouchIndex()));
			case NUMBER_CURRENT_TOUCHES:
				return Double.valueOf(TouchUtil.getNumberOfCurrentTouches());
			case NFC_TAG_MESSAGE:
				return String.valueOf(NfcHandler.getLastNfcTagMessage());
			case NFC_TAG_ID:
				return String.valueOf(NfcHandler.getLastNfcTagId());
			case SPEECH_RECOGNITION_LANGUAGE:
				return listeningLanguageSensor;
			case USER_LANGUAGE:
				return userLocaleTag;
			case STAGE_WIDTH:
				return (double) ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth;
			case STAGE_HEIGHT:
				return (double) ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenHeight;
			default:
				return instance.sensorValueMap.containsKey(sensor) ? instance.sensorValueMap.get(sensor) : 0.0d;
		}
		return 0.0d;
	}

	private static Double calculateCompassDirection(float[] rotationMatrixOut) {
		Double sensorValue;
		int rotate;
		float[] orientations = new float[3];
		if (!instance.useRotationVectorFallback) {
			android.hardware.SensorManager.getRotationMatrixFromVector(instance.rotationMatrix,
					instance.rotationVector);
		}
		if ((rotate = rotateOrientation()) == 1) {
			android.hardware.SensorManager.remapCoordinateSystem(instance.rotationMatrix, android.hardware.SensorManager
					.AXIS_Y, android.hardware.SensorManager.AXIS_MINUS_X, rotationMatrixOut);
			android.hardware.SensorManager.getOrientation(rotationMatrixOut, orientations);
		} else if (rotate == -1) {
			android.hardware.SensorManager.remapCoordinateSystem(instance.rotationMatrix, android.hardware.SensorManager
					.AXIS_MINUS_Y, android.hardware.SensorManager.AXIS_X, rotationMatrixOut);
			android.hardware.SensorManager.getOrientation(rotationMatrixOut, orientations);
		} else {
			android.hardware.SensorManager.getOrientation(instance.rotationMatrix, orientations);
		}
		sensorValue = (double) orientations[0];
		return sensorValue * RADIAN_TO_DEGREE_CONST * -1.0d;
	}

	private static Object calculateXInclination(float[] rotationMatrixOut) {
		Double sensorValue;
		float[] orientations;
		int rotate;
		if (instance.useRotationVectorFallback) {
			float rawInclinationX;
			if ((rotate = rotateOrientation()) != 0) {
				rawInclinationX = RADIAN_TO_DEGREE_CONST * (float) (Math.acos(instance
						.accelerationXYZ[1] * rotate));
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
			if (rotateOrientation() != 0) {
				correctedInclinationX = -correctedInclinationX;
			}
			return (double) correctedInclinationX;
		} else {
			orientations = new float[3];
			android.hardware.SensorManager.getRotationMatrixFromVector(instance.rotationMatrix,
					instance.rotationVector);
			if ((rotate = rotateOrientation()) == 1) {
				android.hardware.SensorManager.remapCoordinateSystem(instance.rotationMatrix, android.hardware.SensorManager
						.AXIS_Y, android.hardware.SensorManager.AXIS_MINUS_X, rotationMatrixOut);
				android.hardware.SensorManager.getOrientation(rotationMatrixOut, orientations);
			} else if (rotate == -1) {
				android.hardware.SensorManager.remapCoordinateSystem(instance.rotationMatrix, android.hardware.SensorManager
						.AXIS_MINUS_Y, android.hardware.SensorManager.AXIS_X, rotationMatrixOut);
				android.hardware.SensorManager.getOrientation(rotationMatrixOut, orientations);
			} else {
				android.hardware.SensorManager.getOrientation(instance.rotationMatrix, orientations);
			}
			sensorValue = (double) orientations[2];
			return sensorValue * RADIAN_TO_DEGREE_CONST * -1f;
		}
	}

	private static Object calculateYInclination(float[] rotationMatrixOut) {
		Double sensorValue;
		float[] orientations;
		int rotate;
		if (instance.useRotationVectorFallback) {
			float rawInclinationY;
			if ((rotate = rotateOrientation()) != 0) {
				rawInclinationY = RADIAN_TO_DEGREE_CONST * (float) (Math.acos(instance.accelerationXYZ[0] * rotate));
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
			if (rotateOrientation() == 1) {
				android.hardware.SensorManager.remapCoordinateSystem(instance.rotationMatrix, android.hardware.SensorManager
						.AXIS_Y, android.hardware.SensorManager.AXIS_MINUS_X, rotationMatrixOut);
				android.hardware.SensorManager.getOrientation(rotationMatrixOut, orientations);
			} else if (rotateOrientation() == -1) {
				android.hardware.SensorManager.remapCoordinateSystem(instance.rotationMatrix, android.hardware.SensorManager
						.AXIS_MINUS_Y, android.hardware.SensorManager.AXIS_X, rotationMatrixOut);
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
	}

	public static String getListeningLanguageSensor() {
		return listeningLanguageSensor;
	}

	public static void setListeningLanguageSensor(String listeningLanguageTag) {
		listeningLanguageSensor = listeningLanguageTag;
		Log.d(TAG, "listening language sensor changed to: " + listeningLanguageSensor);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	private static boolean isDeviceDefaultRotationLandscape() {
		int rotation = ((WindowManager) CatroidApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getRotation();
		Configuration config = CatroidApplication.getAppContext().getResources().getConfiguration();

		return config.orientation == Configuration.ORIENTATION_LANDSCAPE && (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
				|| config.orientation == Configuration.ORIENTATION_PORTRAIT && (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270);
	}

	private static int rotateOrientation() {
		if (ProjectManager.getInstance().isCurrentProjectLandscapeMode() ^ isDeviceDefaultRotationLandscape()) {
			return ProjectManager.getInstance().isCurrentProjectLandscapeMode() ? 1 : -1;
		}
		return 0;
	}

	public static Position getPositionAccordingToRotation(Position original) {
		double x = getXAccordingToRotation(original.getX(), original.getY());
		double y = getYAccordingToRotation(original.getX(), original.getY());
		return new Position(x, y);
	}

	private static double getXAccordingToRotation(double x, double y) {
		int rotate = rotateOrientation();
		return rotate != 0 ? -y * rotate : x;
	}

	private static double getYAccordingToRotation(double x, double y) {
		int rotate = rotateOrientation();
		return rotate != 0 ? x * rotate : y;
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
		sensorValueMap.put(event.getSensor(), event.getValue());
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location == null) {
			return;
		}
		if (location.getProvider().equals(LocationManager.GPS_PROVIDER) || !isGpsConnected) {
			sensorValueMap.put(Sensors.LATITUDE, location.getLatitude());
			sensorValueMap.put(Sensors.LONGITUDE, location.getLongitude());
			sensorValueMap.put(Sensors.LOCATION_ACCURACY, location.getAccuracy());
			sensorValueMap.put(Sensors.ALTITUDE, location.hasAltitude() ? location.getAltitude() : 0);
		}

		if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
			lastLocationGpsMillis = SystemClock.elapsedRealtime();
			lastLocationGps = location;
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onGpsStatusChanged(int event) {
		switch (event) {
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				if (lastLocationGps != null) {
					isGpsConnected = (SystemClock.elapsedRealtime() - lastLocationGpsMillis) < 3000;
				}
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				isGpsConnected = true;
				break;
		}
	}

	public static void destroy() {
		if (instance == null) {
			return;
		}
		stopSensorListeners();
		instance = null;
	}
}
