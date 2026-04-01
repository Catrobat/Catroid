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

package org.catrobat.catroid.uiespresso.ui.dialog;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.PreferenceMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.R.id.currentProjectLayout;
import static org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION;
import static org.catrobat.catroid.common.SharedPreferenceKeys.DISABLE_HINTS_DIALOG_SHOWN_PREFERENCE_KEY;
import static org.catrobat.catroid.io.asynctask.ProjectSaverKt.saveProjectSerial;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_HINTS;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class DisableHintDialogTest {

	private boolean hintSetting;
	private Set<String> hintList;
	int bufferedPreferenceSetting;

	@Rule
	public BaseActivityTestRule<MainMenuActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(MainMenuActivity.class, false, false);

	@Before
	public void setUp() throws Exception {
		createProject("firstProject");

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());
		hintSetting = sharedPreferences.getBoolean(SettingsFragment.SETTINGS_SHOW_HINTS, false);
		hintList = new HashSet<>(sharedPreferences.getStringSet(SnackbarUtil.SHOWN_HINT_LIST, new HashSet<String>()));
		bufferedPreferenceSetting = sharedPreferences.getInt(AGREED_TO_PRIVACY_POLICY_VERSION, 0);

		getDefaultSharedPreferences()
				.edit()
				.putBoolean(SETTINGS_SHOW_HINTS, true)
				.putStringSet(SnackbarUtil.SHOWN_HINT_LIST, new HashSet<String>())
				.putBoolean(DISABLE_HINTS_DIALOG_SHOWN_PREFERENCE_KEY, false)
				.putInt(AGREED_TO_PRIVACY_POLICY_VERSION, Constants.CATROBAT_TERMS_OF_USE_ACCEPTED)
				.apply();

		baseActivityTestRule.launchActivity(null);
	}

	@After
	public void tearDown() throws Exception {
		baseActivityTestRule.deleteAllProjects();

		PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
				.edit()
				.putBoolean(SETTINGS_SHOW_HINTS, hintSetting)
				.putStringSet(SnackbarUtil.SHOWN_HINT_LIST, hintList)
				.putBoolean(DISABLE_HINTS_DIALOG_SHOWN_PREFERENCE_KEY, true)
				.putInt(AGREED_TO_PRIVACY_POLICY_VERSION, bufferedPreferenceSetting)
				.apply();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void disableHintDialogTest() {
		onView(withId(currentProjectLayout)).perform(click());

		onView(withText(R.string.dialog_disable_hints_title))
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_disable_hints_text))
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_disable_hints_button_hide)).perform(click());

		pressBack();

		openActionBarOverflowOrOptionsMenu(baseActivityTestRule.getActivity());
		onView(withText(R.string.settings)).perform(click());
		checkPreferenceHide(R.string.preference_title_enable_hints, SETTINGS_SHOW_HINTS);

		pressBack();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void enableHintDialogTest() {
		onView(withId(currentProjectLayout)).perform(click());

		onView(withText(R.string.dialog_disable_hints_title))
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_disable_hints_text))
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_disable_hints_button_show)).perform(click());

		pressBack();

		openActionBarOverflowOrOptionsMenu(baseActivityTestRule.getActivity());
		onView(withText(R.string.settings)).perform(click());
		checkPreferenceShow(R.string.preference_title_enable_hints, SETTINGS_SHOW_HINTS);

		pressBack();
	}

	private void createProject(String projectName) {
		Project project = UiTestUtils.createDefaultTestProject(projectName);
		saveProjectSerial(project, ApplicationProvider.getApplicationContext());
	}

	private SharedPreferences getDefaultSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());
	}

	private void checkPreferenceHide(int displayedTitleResourceString, String sharedPreferenceTag) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());

		assertFalse(sharedPreferences.getBoolean(sharedPreferenceTag, false));

		onData(PreferenceMatchers.withTitle(displayedTitleResourceString))
				.perform(click());

		assertTrue(sharedPreferences.getBoolean(sharedPreferenceTag, true));
	}

	private void checkPreferenceShow(int displayedTitleResourceString, String sharedPreferenceTag) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());

		assertTrue(sharedPreferences.getBoolean(sharedPreferenceTag, true));

		onData(PreferenceMatchers.withTitle(displayedTitleResourceString))
				.perform(click());

		assertFalse(sharedPreferences.getBoolean(sharedPreferenceTag, false));
	}
}
