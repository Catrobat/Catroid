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

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.RASPBERRY_SCREEN_KEY;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.RASPI_CONNECTION_SETTINGS_CATEGORY;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.RASPI_HOST;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.RASPI_PORT;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_RASPI_BRICKS;

public class RaspberryPiSettingsFragment extends PreferenceFragment {

	public static final String TAG = RaspberryPiSettingsFragment.class.getSimpleName();

	@Override
	public void onResume() {
		super.onResume();
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getPreferenceScreen().getTitle());
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SettingsFragment.setToChosenLanguage(getActivity());
		addPreferencesFromResource(R.xml.raspberry_preferences);

		if (!BuildConfig.FEATURE_RASPI_ENABLED) {
			PreferenceScreen raspiPreference = (PreferenceScreen) findPreference(RASPBERRY_SCREEN_KEY);
			raspiPreference.setEnabled(false);
			getPreferenceScreen().removePreference(raspiPreference);
		} else {
			CheckBoxPreference raspiCheckBoxPreference = (CheckBoxPreference) findPreference(SETTINGS_SHOW_RASPI_BRICKS);
			final PreferenceCategory rpiConnectionSettings = (PreferenceCategory) findPreference(RASPI_CONNECTION_SETTINGS_CATEGORY);
			rpiConnectionSettings.setEnabled(raspiCheckBoxPreference.isChecked());

			raspiCheckBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object isChecked) {
					rpiConnectionSettings.setEnabled((Boolean) isChecked);
					return true;
				}
			});

			final EditTextPreference host = (EditTextPreference) findPreference(RASPI_HOST);
			host.setSummary(host.getText());
			host.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					host.setSummary(newValue.toString());
					return true;
				}
			});

			final EditTextPreference port = (EditTextPreference) findPreference(RASPI_PORT);
			port.setSummary(port.getText());
			port.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					port.setSummary(newValue.toString());
					return true;
				}
			});
		}
	}

	public static boolean isRaspiSharedPreferenceEnabled(Context context) {
		PreferenceManager.setDefaultValues(context, R.xml.raspberry_preferences, true);
		Boolean isPreferenceEnabled = PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(SETTINGS_SHOW_RASPI_BRICKS, false);
		PreferenceManager.setDefaultValues(context, R.xml.preferences, true);
		return isPreferenceEnabled;
	}
}
