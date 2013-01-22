/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.ui;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class SettingsActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;

	public SettingsActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		super.tearDown();
		solo = null;
	}

	public void testToggleMindstormBricks() {
		int actionBarIconIndex = 0;
		String currentProject = solo.getString(R.string.main_menu_continue);
		String background = solo.getString(R.string.background);
		String settings = solo.getString(R.string.main_menu_settings);
		String mindstormsPreferenceString = solo.getString(R.string.pref_enable_ms_bricks);
		String categoryLegoNXTLabel = solo.getString(R.string.category_lego_nxt);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		//disable mindstorm bricks, if enabled at start
		if (preferences.getBoolean("setting_mindstorm_bricks", false)) {
			solo.clickOnMenuItem(settings);
			solo.assertCurrentActivity("Wrong Activity", SettingsActivity.class);
			solo.clickOnText(mindstormsPreferenceString);
			solo.goBack();
		}

		solo.clickOnText(currentProject);
		solo.clickOnText(background);
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnText(solo.getString(R.string.scripts));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		assertFalse("Lego brick category is showing!", solo.searchText(categoryLegoNXTLabel));
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		solo.clickOnMenuItem(settings);
		solo.waitForActivity(SettingsActivity.class.getSimpleName());

		assertTrue("Wrong title", solo.searchText(solo.getString(R.string.pref_title)));

		solo.clickOnText(mindstormsPreferenceString);
		solo.clickOnImage(actionBarIconIndex);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.clickOnText(currentProject);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.clickOnText(background);
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		assertTrue("Lego brick category is not showing!", solo.searchText(categoryLegoNXTLabel));
	}
}
