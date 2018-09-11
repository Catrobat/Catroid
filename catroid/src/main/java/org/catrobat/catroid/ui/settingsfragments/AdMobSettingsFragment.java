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
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import static org.catrobat.catroid.utils.Utils.copyToClipboard;

public class AdMobSettingsFragment extends PreferenceFragment {
	public static final String TAG = AdMobSettingsFragment.class.getSimpleName();
	public static final String ADMOB_SCREEN_KEY = "settings_admob_screen";
	public static final String ADMOB_SETTINGS_DEVICE_ID = "setting_admob_banner_device_id";
	public static final String SETTINGS_SHOW_ADMOB_BRICKS = "settings_admob_bricks";

	@Override
	public void onResume() {
		super.onResume();
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getPreferenceScreen().getTitle());
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SettingsFragment.setToChosenLanguage(getActivity());
		addPreferencesFromResource(R.xml.admob_preferences);

		if (!BuildConfig.FEATURE_ADMOB_ENABLED) {
			PreferenceScreen adMobPreference = (PreferenceScreen) findPreference(ADMOB_SCREEN_KEY);
			adMobPreference.setEnabled(false);
			getPreferenceScreen().removePreference(adMobPreference);
		} else {
			final String hashDeviceId = Utils.getAdMobDeviceId(getActivity());
			final Preference appId = findPreference(ADMOB_SETTINGS_DEVICE_ID);
			appId.setSummary(hashDeviceId);
			appId.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					copyToClipboard(preference.getContext(), hashDeviceId);
					ToastUtil.showSuccess(preference.getContext(), "text copied");
					return false;
				}
			});
		}
	}
}
