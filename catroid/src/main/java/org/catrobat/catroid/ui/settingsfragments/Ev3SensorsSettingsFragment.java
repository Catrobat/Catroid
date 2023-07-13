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

package org.catrobat.catroid.ui.settingsfragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.R;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.EV3_SCREEN_KEY;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.EV3_SENSORS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.EV3_SETTINGS_CATEGORY;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED;

public class Ev3SensorsSettingsFragment extends PreferenceFragment {
	public static final String TAG = Ev3SensorsSettingsFragment.class.getSimpleName();

	@Override
	public void onResume() {
		super.onResume();
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getPreferenceScreen().getTitle());
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SettingsFragment.setToChosenLanguage(getActivity());

		addPreferencesFromResource(R.xml.ev3_preferences);

		if (!BuildConfig.FEATURE_LEGO_EV3_ENABLED) {
			CheckBoxPreference legoEv3Preference = (CheckBoxPreference) findPreference(EV3_SCREEN_KEY);
			legoEv3Preference.setEnabled(false);
			getPreferenceScreen().removePreference(legoEv3Preference);
		} else {
			CheckBoxPreference ev3CheckBoxPreference = (CheckBoxPreference) findPreference(SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED);
			final PreferenceCategory ev3ConnectionSettings = (PreferenceCategory) findPreference(EV3_SETTINGS_CATEGORY);
			ev3ConnectionSettings.setEnabled(ev3CheckBoxPreference.isChecked());

			ev3CheckBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object isChecked) {
					ev3ConnectionSettings.setEnabled((Boolean) isChecked);
					return true;
				}
			});

			final String[] sensorPreferences = EV3_SENSORS;
			for (String sensorPreference : sensorPreferences) {
				ListPreference listPreference = (ListPreference) findPreference(sensorPreference);
				listPreference.setEntries(R.array.ev3_sensor_chooser);
				listPreference.setEntryValues(EV3Sensor.Sensor.getSensorCodes());
			}
		}
	}
}
