/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

public class SettingsActivity extends SherlockPreferenceActivity {

	public static final String SETTINGS_SHOW_LEGO_NXT_BRICKS = "setting_mindstorm_bricks";
	public static final String SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS = "setting_parrot_ar_drone_bricks";
	public static final String SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY = "setting_parrot_ar_drone_catrobat_terms_of_service_accepted_permanently";
	PreferenceScreen screen = null;

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

		ActionBar actionBar = getSupportActionBar();

		actionBar.setTitle(R.string.preference_title);
		actionBar.setHomeButtonEnabled(true);

		screen = getPreferenceScreen();

		if (!BuildConfig.FEATURE_LEGO_NXT_ENABLED) {
			CheckBoxPreference dronePreference = (CheckBoxPreference) findPreference(SETTINGS_SHOW_LEGO_NXT_BRICKS);
			dronePreference.setEnabled(false);
			screen.removePreference(dronePreference);
		}

		if (!BuildConfig.FEATURE_PARROT_AR_DRONE_ENABLED) {
			CheckBoxPreference dronePreference = (CheckBoxPreference) findPreference(SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS);
			dronePreference.setEnabled(false);
			screen.removePreference(dronePreference);
		}
	}

	public static void setTermsOfServiceAgreedPermanently(Context context, boolean agreed) {
		setBooleanSharedPreference(agreed, SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY,
				context);
	}

	public static boolean isDroneSharedPreferenceEnabled(Context context, boolean defaultValue) {
		return getBooleanSharedPreference(defaultValue, SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, context);
	}

	public static boolean areTermsOfServiceAgreedPermanently(Context context) {
		return getBooleanSharedPreference(false,
				SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY, context);
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
}
