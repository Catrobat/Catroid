/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.ui.settingsfragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.R;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.NXT_SENSORS;

public class NXTSensorsSettingsFragment extends PreferenceFragment {
	public static final String TAG = NXTSensorsSettingsFragment.class.getSimpleName();

	@Override
	public void onResume() {
		super.onResume();
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getPreferenceScreen().getTitle());
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SettingsFragment.setToChosenLanguage(getActivity());

		addPreferencesFromResource(R.xml.nxt_preferences);
		if (!BuildConfig.FEATURE_LEGO_NXT_ENABLED) {
			PreferenceScreen legoNxtPreference = (PreferenceScreen) findPreference(SettingsFragment.NXT_SCREEN_KEY);
			legoNxtPreference.setEnabled(false);
			getPreferenceScreen().removePreference(legoNxtPreference);
		} else {
			CheckBoxPreference nxtCheckBoxPreference = (CheckBoxPreference) findPreference(SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED);
			final PreferenceCategory nxtConnectionSettings = (PreferenceCategory) findPreference(SettingsFragment.NXT_SETTINGS_CATEGORY);
			nxtConnectionSettings.setEnabled(nxtCheckBoxPreference.isChecked());

			nxtCheckBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object isChecked) {
					nxtConnectionSettings.setEnabled((Boolean) isChecked);
					return true;
				}
			});

			final String[] sensorPreferences = NXT_SENSORS;
			for (String sensorPreference : sensorPreferences) {
				ListPreference listPreference = (ListPreference) findPreference(sensorPreference);
				listPreference.setEntries(R.array.nxt_sensor_chooser);
				listPreference.setEntryValues(NXTSensor.Sensor.getSensorCodes());
			}
		}
	}
}
