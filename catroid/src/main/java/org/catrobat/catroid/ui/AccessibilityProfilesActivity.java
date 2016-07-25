/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.catrobat.catroid.R;

public class AccessibilityProfilesActivity extends BaseActivity {

	private static final String TAG = AccessibilityProfilesActivity.class.getSimpleName();
	public static final String PROFILE_ID = "profile_id";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accessibility_profiles);
		setUpActionBar();
		setReturnByPressingBackButton(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		updateAccessibilityActiveProfile();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return false;
	}

	private void setUpActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.preference_access_predefined_profiles);
		actionBar.setHomeButtonEnabled(true);
	}

	public void doMyBidding(View view) {
		Intent intent = new Intent(AccessibilityProfilesActivity.this, AccessibilityPreferencesActivity.class);
		intent.putExtra(PROFILE_ID, view.getId());
		AccessibilityProfilesActivity.this.startActivity(intent);
	}

	private void updateAccessibilityActiveProfile() {
		Context context = getApplicationContext();
		TextView activeProfileTitle = (TextView) findViewById(R.id.access_active_profile_title);
		TextView activeProfileSummary = (TextView) findViewById(R.id.access_active_profile_summary);
		ImageView activeProfileImage = (ImageView) findViewById(R.id.access_active_profile_image);

		String profile = SettingsActivity.getActiveAccessibilityProfile(context);
		String title = "";
		String summary = "";
		int image = -1;

		if (profile.equals(SettingsActivity.ACCESS_PROFILE_STANDARD)) {
			title = getResources().getString(R.string.preference_access_title_profile_standard);
			summary = getResources().getString(R.string.preference_access_summary_profile_standard);
			image = R.drawable.nolb_standard_myprofile;
		} else if (profile.equals(SettingsActivity.ACCESS_PROFILE_MYPROFILE)) {
			title = getResources().getString(R.string.preference_access_title_profile_myprofile);
			summary = getResources().getString(R.string.preference_access_summary_profile_myprofile);
			image = R.drawable.nolb_standard_myprofile;
		} else if (profile.equals(SettingsActivity.ACCESS_PROFILE_1)) {
			title = getResources().getString(R.string.preference_access_title_profile_1);
			summary = getResources().getString(R.string.preference_access_summary_profile_1);
			image = R.drawable.nolb_argus;
		} else if (profile.equals(SettingsActivity.ACCESS_PROFILE_2)) {
			title = getResources().getString(R.string.preference_access_title_profile_2);
			summary = getResources().getString(R.string.preference_access_summary_profile_2);
			image = R.drawable.nolb_odin;
		} else if (profile.equals(SettingsActivity.ACCESS_PROFILE_3)) {
			title = getResources().getString(R.string.preference_access_title_profile_3);
			summary = getResources().getString(R.string.preference_access_summary_profile_3);
			image = R.drawable.nolb_fenrir;
		} else if (profile.equals(SettingsActivity.ACCESS_PROFILE_4)) {
			title = getResources().getString(R.string.preference_access_title_profile_4);
			summary = getResources().getString(R.string.preference_access_summary_profile_4);
			image = R.drawable.nolb_tiro;
		}

		activeProfileTitle.setText(title);
		activeProfileSummary.setText(summary);
		activeProfileImage.setImageResource(image);
	}
}
