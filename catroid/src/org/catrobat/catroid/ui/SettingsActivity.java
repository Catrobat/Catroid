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
package org.catrobat.catroid.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

import org.catrobat.catroid.R;

public class SettingsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {

	SharedPreferences mPreferences;
	long firstClickTime;
	Integer clickcount = 0;
	private static final int TIME_TO_MULTIPLE_CLOCK_TO_UNLOCK_ARDRONE = 3000;
	private static final int TIMES_TO_CLICK_TO_ENABLE_DRONE = 10;

	public static final String setting_quadcopter_bricks = "setting_quadcopter_bricks";
	private static final String setting_quadcopter_bricks_enabled = "setting_quadcopter_bricks_enabled";

	CheckBoxPreference dronePreference = null;
	CheckBoxPreference droneEnabledPreference = null;

	SharedPreferences drone_settings_enabled = null;

	PreferenceScreen screen = null;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.preference_title);
		actionBar.setHomeButtonEnabled(true);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();

		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		try {
			dronePreference = (CheckBoxPreference) findPreference(setting_quadcopter_bricks);
			droneEnabledPreference = (CheckBoxPreference) findPreference(setting_quadcopter_bricks_enabled);

			screen = getPreferenceScreen();
			screen.removePreference(droneEnabledPreference);

			if (!isARDroneSupportEnabled()) {
				screen.removePreference(dronePreference);
			}

			/**
			 * TODO: add setting in code, not in preferneces.xml
			 * drone_settings_enabled = getSharedPreferences(setting_quadcopter_bricks_enabled, MODE_PRIVATE);
			 * SharedPreferences.Editor editor = drone_settings_enabled.edit();
			 * editor.putBoolean(setting_quadcopter_bricks_enabled, false);
			 * editor.commit();
			 */

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private boolean isARDroneSupportEnabled() {
		boolean isChecked = false;
		try {
			isChecked = droneEnabledPreference.isChecked();
		} catch (Exception e) {
			isChecked = false;
		}

		return isChecked;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d("Drone", "onpreferenceChanged");
		if (key.equals("setting_mindstorm_bricks")) {
			countClicks();
		}
	}

	private synchronized void countClicks() {
		Log.d("Drone", "mindStormChanged");

		long currentTime = System.currentTimeMillis();
		if (clickcount == 0) {
			Log.d("Drone", "firstclick");
			firstClickTime = currentTime;
		}

		clickcount++;

		Log.d("Drone", "clickcout = " + clickcount);
		if (clickcount == TIMES_TO_CLICK_TO_ENABLE_DRONE
				&& currentTime < (firstClickTime + TIME_TO_MULTIPLE_CLOCK_TO_UNLOCK_ARDRONE)) {
			firstClickTime = 0;
			clickcount = 0;
			if (droneEnabledPreference.isChecked() == false) {
				Toast.makeText(this, "Enabled ARDrone Settings", Toast.LENGTH_SHORT).show();
				droneEnabledPreference.setChecked(true);
				screen.addPreference(dronePreference);
			} else {
				Toast.makeText(this, "Disabled ARDrone Settings", Toast.LENGTH_SHORT).show();
				droneEnabledPreference.setChecked(false);
				screen.removePreference(dronePreference);
				dronePreference.setChecked(false);
			}
		} else if (currentTime > (firstClickTime + TIME_TO_MULTIPLE_CLOCK_TO_UNLOCK_ARDRONE)) {
			Log.d("Drone", "Clicked to slow ...");
			firstClickTime = 0;
			clickcount = 0;
		}
	}
}
