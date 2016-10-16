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

package org.catrobat.catroid.uitest.ui.activity;

import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.AccessibilityPreferencesActivity;
import org.catrobat.catroid.ui.AccessibilityProfilesActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;

public class AccessibilityActivitesTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private String settings;

	public AccessibilityActivitesTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		settings = solo.getString(R.string.settings);
	}

	public void testSettings() {
		solo.waitForActivity(MainMenuActivity.class);

		solo.clickOnMenuItem(settings);
		solo.waitForActivity(SettingsActivity.class.getSimpleName());

		solo.clickOnText(solo.getString(R.string.preference_title_access));
		solo.waitForActivity(AccessibilityPreferencesActivity.class.getSimpleName());
		assertTrue("Accessibility preferences did not open.", solo.searchText(solo.getString(R.string
				.preference_title_access)));

		solo.clickOnText(solo.getString(R.string.preference_access_change_profile));
		solo.waitForActivity(AccessibilityProfilesActivity.class.getSimpleName());
		assertTrue("Accessibility profiles did not open.", solo.searchText(solo.getString(R.string
				.preference_access_predefined_profiles)));

		solo.clickOnView(solo.getView(R.id.access_profilestandard));
		solo.waitForActivity(AccessibilityPreferencesActivity.class.getSimpleName());
		TextView activeProfileLabel = (TextView) solo.getView(R.id.access_label_active_profile);
		assertEquals("Wrong profile!", activeProfileLabel.getText(), solo.getString(R.string.preference_access_selected_profile));
		TextView selectedProfile = (TextView) solo.getView(R.id.access_active_profile_title);
		assertEquals("The expected profile did not open.", selectedProfile.getText(),
				solo.getString(R.string.preference_access_title_profile_standard));

		solo.clickOnText(solo.getString(R.string.preference_access_title_large_text));
		assertTrue("My Profile Dialog did not open.",
				solo.searchText(solo.getString(R.string.preference_access_title_created_profile)));

		solo.clickOnText(solo.getString(R.string.close));
		selectedProfile = (TextView) solo.getView(R.id.access_active_profile_title);
		assertEquals("The selection did not change to My Profile.", selectedProfile.getText(),
				solo.getString(R.string.preference_access_title_profile_myprofile));
	}
}
