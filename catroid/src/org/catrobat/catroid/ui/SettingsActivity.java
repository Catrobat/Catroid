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
package org.catrobat.catroid.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.R;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;

public class SettingsActivity extends SherlockPreferenceActivity {

	public static final String SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED = "settings_mindstorms_nxt_bricks_enabled";
	public static final String SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED = "settings_mindstorms_nxt_show_sensor_info_box_disabled";
	public static final String SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS = "setting_parrot_ar_drone_bricks";
	private static final String SETTINGS_SHOW_PHIRO_BRICKS = "setting_enable_phiro_bricks";
	public static final String SETTINGS_SHOW_ARDUINO_BRICKS = "setting_arduino_bricks";
	public static final String SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY = "setting_parrot_ar_drone_catrobat_terms_of_service_accepted_permanently";
	PreferenceScreen screen = null;

	public static final String NXT_SENSOR_1 = "setting_mindstorms_nxt_sensor_1";
	public static final String NXT_SENSOR_2 = "setting_mindstorms_nxt_sensor_2";
	public static final String NXT_SENSOR_3 = "setting_mindstorms_nxt_sensor_3";
	public static final String NXT_SENSOR_4 = "setting_mindstorms_nxt_sensor_4";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		ListPreference listPreference = (ListPreference) findPreference(getResources().getString(
				R.string.preference_key_select_camera));
		int cameraCount = Camera.getNumberOfCameras();
		String[] entryValues = new String[cameraCount];
		CharSequence[] entries = new CharSequence[cameraCount];
		for (int id = 0; id < cameraCount; id++) {
			entryValues[id] = Integer.toString(id);
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			Camera.getCameraInfo(id, cameraInfo);
			switch (cameraInfo.facing) {
				case CameraInfo.CAMERA_FACING_FRONT:
					entries[id] = getResources().getText(R.string.camera_facing_front);
					break;
				case CameraInfo.CAMERA_FACING_BACK:
					entries[id] = getResources().getText(R.string.camera_facing_back);
					break;
				default:
					Log.d("CAMERA", "No Camera detected");
			}
		}
		listPreference.setEntries(entries);
		listPreference.setEntryValues(entryValues);

		setNXTSensors();

		ActionBar actionBar = getSupportActionBar();

		actionBar.setTitle(R.string.preference_title);
		actionBar.setHomeButtonEnabled(true);

		screen = getPreferenceScreen();

		if (!BuildConfig.FEATURE_LEGO_NXT_ENABLED) {
			CheckBoxPreference legoNxtPreference = (CheckBoxPreference) findPreference(SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED);
			legoNxtPreference.setEnabled(false);
			screen.removePreference(legoNxtPreference);
		}

		if (!BuildConfig.FEATURE_PARROT_AR_DRONE_ENABLED) {
			CheckBoxPreference dronePreference = (CheckBoxPreference) findPreference(SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS);
			dronePreference.setEnabled(false);
			screen.removePreference(dronePreference);
		}

		if (!BuildConfig.FEATURE_PHIRO_ENABLED) {
			PreferenceScreen phiroPreference = (PreferenceScreen) findPreference(SETTINGS_SHOW_PHIRO_BRICKS);
			phiroPreference.setEnabled(false);
			screen.removePreference(phiroPreference);
		}

		if (!BuildConfig.FEATURE_ARDUINO_ENABLED) {
			PreferenceScreen arduinoPreference = (PreferenceScreen) findPreference(SETTINGS_SHOW_ARDUINO_BRICKS);
			arduinoPreference.setEnabled(false);
			screen.removePreference(arduinoPreference);
		}
	}

	private void setNXTSensors() {

		boolean areChoosersEnabled = getMindstormsNXTSensorChooserEnabled(this);

		final String[] sensorPreferences = new String[] { NXT_SENSOR_1, NXT_SENSOR_2, NXT_SENSOR_3, NXT_SENSOR_4 };
		for (int i = 0; i < sensorPreferences.length; ++i) {
			ListPreference listPreference = (ListPreference) findPreference(sensorPreferences[i]);
			listPreference.setEntryValues(NXTSensor.Sensor.getSensorCodes());
			listPreference.setEntries(R.array.nxt_sensor_chooser);
			listPreference.setEnabled(areChoosersEnabled);
		}
	}

	public static void setTermsOfServiceAgreedPermanently(Context context, boolean agreed) {
		setBooleanSharedPreference(agreed, SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY,
				context);
	}

	public static boolean isDroneSharedPreferenceEnabled(Context context, boolean defaultValue) {
		return getBooleanSharedPreference(defaultValue, SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, context);
	}

	public static boolean isFaceDetectionPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false,
				context.getString(R.string.preference_key_use_face_detection), context);
	}

	public static void setFaceDetectionSharedPreferenceEnabled(Context context, boolean value) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(context.getString(R.string.preference_key_use_face_detection), value);
		editor.commit();
	}

	public static boolean isMindstormsNXTSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, context);
	}

	public static boolean areTermsOfServiceAgreedPermanently(Context context) {
		return getBooleanSharedPreference(false,
				SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY, context);
	}

	public static boolean isPhiroSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_PHIRO_BRICKS, context);
	}

	public static void setPhiroSharedPreferenceEnabled(Context context, boolean value) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(SETTINGS_SHOW_PHIRO_BRICKS, value);
		editor.commit();
	}

	public static void setArduinoSharedPreferenceEnabled(Context context, boolean value) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(SETTINGS_SHOW_ARDUINO_BRICKS, value);
		editor.commit();
	}

	public static boolean isArduinoSharedPreferenceEnabled(Context context) {
		return getBooleanSharedPreference(false, SETTINGS_SHOW_ARDUINO_BRICKS, context);
	}

	private static void setBooleanSharedPreference(boolean value, String settingsString, Context context) {
		getSharedPreferences(context).edit().putBoolean(settingsString, value).commit();
	}

	private static boolean getBooleanSharedPreference(boolean defaultValue, String settingsString, Context context) {
		return getSharedPreferences(context).getBoolean(settingsString, defaultValue);
	}

	private static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static NXTSensor.Sensor[] getLegoMindstormsNXTSensorMapping(Context context) {

		final String[] sensorPreferences =
				new String[] { NXT_SENSOR_1, NXT_SENSOR_2, NXT_SENSOR_3, NXT_SENSOR_4 };

		NXTSensor.Sensor[] sensorMapping = new NXTSensor.Sensor[4];
		for (int i = 0; i < 4; i++) {
			String sensor = getSharedPreferences(context).getString(sensorPreferences[i], null);
			sensorMapping[i] = NXTSensor.Sensor.getSensorFromSensorCode(sensor);
		}

		return sensorMapping;
	}

	public static NXTSensor.Sensor getLegoMindstormsNXTSensorMapping(Context context, String sensorSetting) {
		String sensor = getSharedPreferences(context).getString(sensorSetting, null);
		return NXTSensor.Sensor.getSensorFromSensorCode(sensor);
	}

	public static void setLegoMindstormsNXTSensorMapping(Context context, NXTSensor.Sensor[] sensorMapping) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();

		editor.putString(NXT_SENSOR_1, sensorMapping[0].getSensorCode());
		editor.putString(NXT_SENSOR_2, sensorMapping[1].getSensorCode());
		editor.putString(NXT_SENSOR_3, sensorMapping[2].getSensorCode());
		editor.putString(NXT_SENSOR_4, sensorMapping[3].getSensorCode());

		editor.commit();
	}

	public static void setLegoMindstormsNXTSensorMapping(Context context, NXTSensor.Sensor sensor, String sensorSetting) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putString(sensorSetting, sensor.getSensorCode());
		editor.commit();
	}

	public static void enableLegoMindstormsNXTBricks(Context context) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, true);
		editor.commit();
	}

	public static void setLegoMindstormsNXTSensorChooserEnabled(Context context, boolean enable) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean("mindstorms_nxt_sensor_chooser_in_settings", enable);
		editor.commit();
	}

	public static boolean getMindstormsNXTSensorChooserEnabled(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean("mindstorms_nxt_sensor_chooser_in_settings", false);
	}

	public static void disableLegoMindstormsSensorInfoDialog(Context context) {
		SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED, true);
		editor.commit();
	}

	public static boolean getShowLegoMindstormsSensorInfoDialog(Context context) {
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED, false);
	}

	public static void resetSharedPreferences(Context context) {
		getSharedPreferences(context).edit().clear().commit();
	}
}
