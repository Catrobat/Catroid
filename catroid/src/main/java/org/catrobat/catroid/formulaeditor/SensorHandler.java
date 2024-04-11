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
import org.catrobat.catroid.camera.Position;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.sensor.SensorAltitude;
import org.catrobat.catroid.formulaeditor.sensor.SensorLatitude;
import org.catrobat.catroid.formulaeditor.sensor.SensorLocationAccuracy;
import org.catrobat.catroid.formulaeditor.sensor.SensorLongitude;
import org.catrobat.catroid.formulaeditor.sensor.SensorLoudnessSensor;

import androidx.annotation.VisibleForTesting;

public final class SensorHandler implements SensorEventListener, LocationListener,
		GpsStatus.Listener {
	private static final String TAG = SensorHandler.class.getSimpleName();
	private static SensorHandler instance;
	private final SensorManagerInterface sensorManager;
	private final Sensor linearAccelerationSensor;
	private Sensor accelerometerSensor;
	private Sensor magneticFieldSensor;
	private final Sensor rotationVectorSensor;
	public static float signAccelerationZ;
	private final float[] gravity = {0f, 0f, 0f};
	private boolean useLinearAccelerationFallback;
	public static LinearAcceleration linearAcceleration = new LinearAcceleration(0, 0, 0);
	private boolean compassAvailable = true;
	private boolean accelerationAvailable = true;
	private boolean inclinationAvailable = true;

	private LocationManager locationManager;
	private boolean isGpsConnected;
	private Location lastLocationGps;
	private long lastLocationGpsMillis;

	public static Sprite currentSprite;
	public static Scene currentlyEditedScene;
	public static Project currentProject;

	public static float[] rotationMatrix = new float[16];
	public static float[] rotationVector = new float[3];
	public static float[] accelerationXYZ = new float[3];
	public static boolean useRotationVectorFallback;
	private SensorLoudness sensorLoudness;

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
		instance.sensorManager.unregisterListener(instance);

		registerListener(instance);

		if (instance.sensorLoudness != null) {
			instance.sensorLoudness.registerListener(SensorLoudnessSensor.Companion.getInstance());
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

		if (!useRotationVectorFallback) {
			instance.sensorManager.registerListener(listener, instance.rotationVectorSensor,
					android.hardware.SensorManager.SENSOR_DELAY_GAME);
		}

		if (instance.useLinearAccelerationFallback || useRotationVectorFallback) {
			instance.sensorManager.registerListener(listener, instance.accelerometerSensor,
					android.hardware.SensorManager.SENSOR_DELAY_GAME);
		}

		if (useRotationVectorFallback && instance.magneticFieldSensor != null) {
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
		instance.sensorManager.unregisterListener(instance);

		if (instance.sensorLoudness != null) {
			instance.sensorLoudness.unregisterListener(SensorLoudnessSensor.Companion.getInstance());
		}

		if (instance.locationManager != null) {
			instance.locationManager.removeUpdates(instance);
			instance.locationManager.removeGpsStatusListener(instance);
		}
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

	public static int rotateOrientation() {
		if (ProjectManager.getInstance().isCurrentProjectLandscapeMode() ^ isDeviceDefaultRotationLandscape()) {
			return ProjectManager.getInstance().isCurrentProjectLandscapeMode() ? 1 : -1;
		}
		return 0;
	}

	public static Position getPositionAccordingToRotation(Position original) {
		double x = getXAccordingToRotation(original.getX(), original.getY(), rotateOrientation());
		double y = getYAccordingToRotation(original.getX(), original.getY(), rotateOrientation());
		return new Position(x, y);
	}

	public static double getXAccordingToRotation(double x, double y, int rotate) {
		return rotate != 0 ? -y * rotate : x;
	}

	public static double getYAccordingToRotation(double x, double y, int rotate) {
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
				linearAcceleration.setAccelerationX(event.values[0]);
				linearAcceleration.setAccelerationY(event.values[1]);
				linearAcceleration.setAccelerationZ(event.values[2]);
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

		linearAcceleration.setAccelerationX(-1f * (input[0] - gravity[0]));
		linearAcceleration.setAccelerationY(-1f * (input[1] - gravity[1]));
		linearAcceleration.setAccelerationZ(-1f * (input[2] - gravity[2]));
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location == null) {
			return;
		}
		if (location.getProvider().equals(LocationManager.GPS_PROVIDER) || !isGpsConnected) {
			SensorLatitude.Companion.getInstance().updateSensorValue(location.getLatitude());
			SensorLongitude.Companion.getInstance().updateSensorValue(location.getLongitude());
			SensorLocationAccuracy.Companion.getInstance().updateSensorValue((double) location.getAccuracy());
			SensorAltitude.Companion.getInstance().updateSensorValue(location.hasAltitude() ? location.getAltitude() : 0);
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

	public static void setObjectData(Sprite sprite, Scene currentEditedScene,
			Project project) {
		currentSprite = sprite;
		currentlyEditedScene = currentEditedScene;
		currentProject = project;
	}
}
