/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.uitest.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.SettingsActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

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
	}

	public void testToggleMindstormBricks() {
		String currentProject = getActivity().getString(R.string.current_project_button);
		String background = getActivity().getString(R.string.background);
		String settings = getActivity().getString(R.string.settings);
		String prefMsBricks = getActivity().getString(R.string.pref_enable_ms_bricks);
		String categoryLegoNXTLabel = solo.getString(R.string.category_lego_nxt);
		SharedPreferences preferances = PreferenceManager.getDefaultSharedPreferences(getActivity());

		//disable mindstorm bricks, if enabled at start
		if (preferances.getBoolean("setting_mindstorm_bricks", false)) {
			solo.clickOnText(settings);
			solo.clickOnText(prefMsBricks);
			solo.goBack();
		}

		solo.clickOnText(currentProject);
		solo.clickOnText(background);
		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_add);
		assertFalse("Lego brick category is showing!", solo.searchText(categoryLegoNXTLabel));
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		solo.clickOnText(settings);
		solo.waitForActivity(SettingsActivity.class.getSimpleName());
		solo.clickOnText(prefMsBricks);
		solo.goBack();
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.clickOnText(currentProject);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.clickOnText(background);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_add);
		assertTrue("Lego brick category is not showing!", solo.searchText(categoryLegoNXTLabel));
		// needed to fix NullPointerException in next Testcase
		solo.finishInactiveActivities();
	}
}
