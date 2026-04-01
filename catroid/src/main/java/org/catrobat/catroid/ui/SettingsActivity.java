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

package org.catrobat.catroid.ui;

import android.os.Bundle;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.settingsfragments.AccessibilitySettingsFragment;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import androidx.appcompat.widget.Toolbar;

import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfilesFragment.SETTINGS_FRAGMENT_INTENT_KEY;

public class SettingsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preference);

		getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, new SettingsFragment())
				.commit();

		if (getIntent().getExtras() != null && getIntent()
				.getBooleanExtra(SETTINGS_FRAGMENT_INTENT_KEY, false)) {

			getFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new AccessibilitySettingsFragment())
					.addToBackStack(AccessibilitySettingsFragment.TAG)
					.commit();
		}

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle(R.string.preference_title);
	}

	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStack();
		} else {
			super.onBackPressed();
		}
	}
}
