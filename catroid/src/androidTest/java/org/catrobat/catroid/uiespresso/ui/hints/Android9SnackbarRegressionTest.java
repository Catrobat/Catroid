/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.hints;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class Android9SnackbarRegressionTest {

	private boolean hintSetting;
	private Set<String> hintList;

	@Rule
	public BaseActivityInstrumentationRule<ProjectActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProjectActivity.class, ProjectActivity.EXTRA_FRAGMENT_POSITION,
			ProjectActivity.FRAGMENT_SPRITES);

	@Before
	public void setUp() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
		hintSetting = sharedPreferences
				.getBoolean(SettingsFragment.SETTINGS_SHOW_HINTS, false);
		hintList = new HashSet<>(sharedPreferences.getStringSet(SnackbarUtil.SHOWN_HINT_LIST, new HashSet<String>()));

		sharedPreferences.edit()
				.putBoolean(SettingsFragment.SETTINGS_SHOW_HINTS, true)
				.putStringSet(SnackbarUtil.SHOWN_HINT_LIST, new HashSet<String>())
				.commit();

		createProject();
		baseActivityTestRule.launchActivity();
	}

	@After
	public void tearDown() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());

		sharedPreferences.edit()
				.putBoolean(SettingsFragment.SETTINGS_SHOW_HINTS, hintSetting)
				.putStringSet(SnackbarUtil.SHOWN_HINT_LIST, hintList)
				.commit();
	}

	@Category({Cat.AppUi.class, Level.Detailed.class})
	@Test
	public void snackbarTest() {
		onView(isRoot()).perform(CustomActions.wait(200));
		onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(R.string.hint_objects)))
				.check(matches(isDisplayed()));
		onView(allOf(withId(android.support.design.R.id.snackbar_action), withText(R.string.got_it)))
				.check(matches(isDisplayed()));
	}

	public void createProject() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), getClass().getSimpleName());
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
	}
}
