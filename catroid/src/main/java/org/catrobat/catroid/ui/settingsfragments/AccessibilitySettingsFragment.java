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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.ToastUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AccessibilitySettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	public static final String TAG = AccessibilitySettingsFragment.class.getSimpleName();

	private boolean preferenceChanged = false;
	private static final String ACCESSIBILITY_PROFILES_SCREEN_KEY = "setting_accessibility_profile_screen";
	static final String CUSTOM_PROFILE = "accessibility_profile_is_custom";

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.accessibility_preferences);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SettingsFragment.setToChosenLanguage(getActivity());
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getPreferenceScreen().getTitle());
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (preferenceChanged) {
			startActivity(new Intent(getActivity().getBaseContext(), MainMenuActivity.class));
			startActivity(new Intent(getActivity().getBaseContext(), SettingsActivity.class));
			ToastUtil.showSuccess(getActivity(), getString(R.string.accessibility_settings_applied));
			getActivity().finishAffinity();
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		preferenceChanged = true;
		sharedPreferences.edit()
				.putBoolean(CUSTOM_PROFILE, true)
				.apply();
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		String key = preference.getKey();
		switch (key) {
			case ACCESSIBILITY_PROFILES_SCREEN_KEY:
				getFragmentManager().beginTransaction()
						.replace(R.id.content_frame, new AccessibilityProfilesFragment(),
								AccessibilityProfilesFragment.TAG)
						.addToBackStack(AccessibilityProfilesFragment.TAG)
						.commit();
				break;
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}
