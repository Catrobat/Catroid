/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import org.catrobat.catroid.R;
import org.catrobat.catroid.sync.ProjectsCategoriesSync;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.ToastUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_TAG_KEY;
import static org.catrobat.catroid.ui.settingsfragments.AccessibilitySettingsFragment.CUSTOM_PROFILE;
import static org.koin.java.KoinJavaComponent.inject;

public class AdvancedModeSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	public static final String TAG = AdvancedModeSettingsFragment.class.getSimpleName();

	private boolean preferenceChanged = false;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.advanced_mode);

		SharedPreferences sharedPreferences =
				PreferenceManager.getDefaultSharedPreferences(getContext());

		toggleSettings(sharedPreferences);
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

		toggleSettings(sharedPreferences);
	}

	private void toggleSettings(SharedPreferences sharedPreferences) {
		boolean isSetToAdvancedMode =
				sharedPreferences.getBoolean(SettingsFragment.SETTINGS_CATBLOCKS_ADVANCED_MODE,
						false);

		if (!isSetToAdvancedMode) {
			CheckBoxPreference setToEnglishCheckbox =
					(CheckBoxPreference) findPreference(SettingsFragment.SETTINGS_SET_TO_ENGLISH_ADVANCED_MODE);
			setToEnglishCheckbox.setEnabled(false);
		}

		Preference advancedModeCheckbox =
				findPreference(SettingsFragment.SETTINGS_CATBLOCKS_ADVANCED_MODE);
		Preference setToEnglishCheckbox =
				findPreference(SettingsFragment.SETTINGS_SET_TO_ENGLISH_ADVANCED_MODE);

		advancedModeCheckbox.setOnPreferenceChangeListener((preference, newValue) -> {
			boolean isAdvancedChanged = (boolean) newValue;
			if (isAdvancedChanged) {
				setToEnglishCheckbox.setEnabled(true);
			}
			return true;
		});

		setToEnglishCheckbox.setOnPreferenceChangeListener((preference, newValue) -> {
			boolean isEnglishChanged = (boolean) newValue;
			changeToEnglish(isSetToAdvancedMode, isEnglishChanged, sharedPreferences);
			return true;
		});
	}

	private void changeToEnglish(boolean isAdvancedMode, boolean isSetToEnglish,
			SharedPreferences sharedPreferences) {
		String language = "en";
		if (isAdvancedMode && isSetToEnglish) {
			if (!sharedPreferences.getString(LANGUAGE_TAG_KEY, "").equals("en")) {
				SettingsFragment.setAdvancedModePreviousLanguage(getActivity().getBaseContext(), sharedPreferences.getString(LANGUAGE_TAG_KEY, ""));
			}
		} else {
			language = sharedPreferences.getString(SettingsFragment.SETTINGS_ADVANCED_MODE_PREVIOUS_LANGUAGE, "");
		}
		if (!language.equals("")) {
			SettingsFragment.setLanguageSharedPreference(getActivity().getBaseContext(), language);
			startActivity(new Intent(getActivity().getBaseContext(), MainMenuActivity.class));
			getActivity().finishAffinity();
			new Thread(() -> inject(ProjectsCategoriesSync.class).getValue().sync(true));
		}
	}
}
