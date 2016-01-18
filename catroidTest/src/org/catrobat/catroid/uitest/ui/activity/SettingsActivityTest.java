/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class SettingsActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private String settings;
	private SharedPreferences preferences;

	public SettingsActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		TestUtils.createEmptyProject();
		settings = solo.getString(R.string.settings);
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		preferences.edit().putBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false).commit();
	}

	@Override
	protected void tearDown() throws Exception {
		preferences.edit().putBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false).commit();
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void testToggleMindstormsNXTBricks() {
		String mindstormsPreferenceString = solo.getString(R.string.preference_title_enable_mindstorms_nxt_bricks);
		String categoryLegoNXTLabel = solo.getString(R.string.category_lego_nxt);

		solo.waitForActivity(MainMenuActivity.class);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.sleep(200);
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.sleep(200);
		solo.scrollListToBottom(fragmentListView);
		solo.sleep(200);
		assertFalse("Lego brick category is showing!", solo.searchText(categoryLegoNXTLabel));
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		solo.clickOnMenuItem(settings);
		solo.waitForActivity(SettingsActivity.class.getSimpleName());

		assertTrue("Wrong title", solo.searchText(solo.getString(R.string.preference_title)));

		solo.clickOnText(mindstormsPreferenceString); // submenu
		solo.waitForText(mindstormsPreferenceString);
		solo.clickOnText(mindstormsPreferenceString); // checkbox
		solo.goBack();
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.sleep(200);
		fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.sleep(200);
		solo.scrollListToBottom(fragmentListView);
		solo.scrollDown();
		solo.sleep(200);
		assertTrue("Lego brick category is not showing!", solo.searchText(categoryLegoNXTLabel));
	}
}
