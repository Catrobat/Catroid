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
import android.preference.PreferenceScreen;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.DroneConfigPreference;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.DRONE_ALTITUDE_LIMIT;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.DRONE_CONFIGS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.DRONE_ROTATION_SPEED;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.DRONE_SCREEN_KEY;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.DRONE_SETTINGS_CATEGORY;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.DRONE_TILT_ANGLE;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.DRONE_VERTICAL_SPEED;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS;

public class ParrotARDroneSettingsFragment extends PreferenceFragment {

	public static final String TAG = ParrotARDroneSettingsFragment.class.getSimpleName();

	@Override
	public void onResume() {
		super.onResume();
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getPreferenceScreen().getTitle());
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SettingsFragment.setToChosenLanguage(getActivity());
		addPreferencesFromResource(R.xml.drone_preferences);

		if (!BuildConfig.FEATURE_PARROT_AR_DRONE_ENABLED) {
			PreferenceScreen dronePreference = (PreferenceScreen) findPreference(DRONE_SCREEN_KEY);
			dronePreference.setEnabled(false);
			getPreferenceScreen().removePreference(dronePreference);
		} else {
			CheckBoxPreference droneCheckBoxPreference = (CheckBoxPreference) findPreference(SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS);
			final PreferenceCategory droneConnectionSettings = (PreferenceCategory) findPreference(DRONE_SETTINGS_CATEGORY);
			droneConnectionSettings.setEnabled(droneCheckBoxPreference.isChecked());

			final String[] dronePreferences = new String[] {DRONE_CONFIGS, DRONE_ALTITUDE_LIMIT, DRONE_VERTICAL_SPEED,
					DRONE_ROTATION_SPEED, DRONE_TILT_ANGLE};

			droneCheckBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object isChecked) {
					droneConnectionSettings.setEnabled((Boolean) isChecked);
					return true;
				}
			});

			for (String dronePreference : dronePreferences) {
				ListPreference listPreference = (ListPreference) findPreference(dronePreference);

				switch (dronePreference) {
					case DRONE_CONFIGS:
						listPreference.setEntries(R.array.drone_setting_default_config);
						final ListPreference list = listPreference;
						listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
							public boolean onPreferenceChange(Preference preference, Object newValue) {

								int index = list.findIndexOfValue(newValue.toString());
								for (String dronePreference : dronePreferences) {
									ListPreference listPreference = (ListPreference) findPreference(dronePreference);
									switch (dronePreference) {

										case DRONE_ALTITUDE_LIMIT:
											listPreference.setValue("FIRST");
											break;

										case DRONE_VERTICAL_SPEED:
											if (index == 0 || index == 1) {
												listPreference.setValue("SECOND");
											}
											if (index == 2 || index == 3) {
												listPreference.setValue("THIRD");
											}
											break;

										case DRONE_ROTATION_SPEED:
											if (index == 0 || index == 1) {
												listPreference.setValue("SECOND");
											}
											if (index == 2 || index == 3) {
												listPreference.setValue("THIRD");
											}
											break;

										case DRONE_TILT_ANGLE:
											if (index == 0 || index == 1) {
												listPreference.setValue("SECOND");
											}
											if (index == 2 || index == 3) {
												listPreference.setValue("THIRD");
											}
											break;
									}
								}
								return true;
							}
						});
						break;

					case DRONE_ALTITUDE_LIMIT:
						listPreference.setEntries(R.array.drone_altitude_spinner_items);
						break;

					case DRONE_VERTICAL_SPEED:
						listPreference.setEntries(R.array.drone_max_vertical_speed_items);
						break;

					case DRONE_ROTATION_SPEED:
						listPreference.setEntries(R.array.drone_max_rotation_speed_items);
						break;

					case DRONE_TILT_ANGLE:
						listPreference.setEntries(R.array.drone_max_tilt_angle_items);
						break;
				}
				listPreference.setEntryValues(DroneConfigPreference.Preferences.getPreferenceCodes());
			}
		}
	}
}
