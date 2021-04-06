/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.fragment;

import android.content.Context;
import android.preference.PreferenceManager;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION;
import static org.catrobat.catroid.uiespresso.ui.actionbar.utils.ActionBarWrapper.onActionBar;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class CurrentProjectTest {

	@Rule
	public BaseActivityTestRule<MainMenuActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(MainMenuActivity.class, false, false);

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(currentProject, downloadedProject);
		PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
				.edit().putInt(AGREED_TO_PRIVACY_POLICY_VERSION, bufferedPreferenceSetting).commit();
	}

	@Before
	public void setUp() throws Exception {
		bufferedPreferenceSetting = PreferenceManager
				.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
				.getInt(AGREED_TO_PRIVACY_POLICY_VERSION, 0);

		PreferenceManager.getDefaultSharedPreferences(applicationContext)
				.edit().putInt(AGREED_TO_PRIVACY_POLICY_VERSION, Constants.CATROBAT_TERMS_OF_USE_ACCEPTED).commit();
		createProject();
		baseActivityTestRule.launchActivity(null);
	}

	Context applicationContext = ApplicationProvider.getApplicationContext();
	private final String currentProject = "currentProject";
	private final String downloadedProject = "downloadedProject";
	int bufferedPreferenceSetting;

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testCurrentProjectAfterDownload() {
		onView(withId(R.id.currentProjectLayout)).perform(click());
		onActionBar().checkTitleMatches(currentProject);
		pressBack();
		onView(withText(R.string.main_menu_programs)).perform(click());
		Project project2 = new Project(applicationContext, downloadedProject);
		XstreamSerializer.getInstance().saveProject(project2);
		pressBack();
		onView(withId(R.id.currentProjectLayout)).perform(click());
		onActionBar().checkTitleMatches(downloadedProject);
		pressBack();
		onView(withText(R.string.main_menu_programs)).perform(click());
	}

	private void createProject() {
		Project project = new Project(applicationContext, currentProject);

		ProjectManager.getInstance().setCurrentProject(project);
		XstreamSerializer.getInstance().saveProject(project);
	}
}
