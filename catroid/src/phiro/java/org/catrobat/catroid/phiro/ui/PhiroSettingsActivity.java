/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.phiro.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.text.Html;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.TextSizeUtil;

public class PhiroSettingsActivity extends SettingsActivity {

	private static final String PHIRO_LINK = "phiro_preference_link";
	public static final String SETTINGS_PHIRO_CATEGORY = "setting_phiro_bricks";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initPreferences();

		super.onCreate(savedInstanceState);

		if (getAccessibilityLargeTextEnabled(getApplicationContext())) {
			TextSizeUtil.enlargePreferenceScreen(this, SETTINGS_PHIRO_CATEGORY);
		}
		setupLink();
	}

	private void initPreferences() {
		preferences = new int[] {
				R.xml.preferences_phirocode,
				R.xml.preferences_nxt,
				R.xml.preferences_ev3,
				R.xml.preferences_drone,
				R.xml.preferences_arduino,
				R.xml.preferences_nfc,
				R.xml.preferences_raspberry,
				R.xml.preferences_cast,
				R.xml.preferences_accessibility,
				R.xml.preferences_hints,
				R.xml.preferences_crash_reports
		};
	}

	private void setupLink() {
		final String aboutPhiroUrl = getString(R.string.about_link_template, getString(R.string.phiro_preference_link),
				getString(R.string.phiro_preference_title));

		Preference linkPreference = findPreference(PHIRO_LINK);
		linkPreference.setTitle(Html.fromHtml(aboutPhiroUrl));

		linkPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.phiro_preference_link)));
				startActivity(intent);
				return true;
			}
		});
	}
}
