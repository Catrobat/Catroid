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
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.util.rules.DontGenerateDefaultProjectActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.assertCurrentActivityIsInstanceOf;
import static org.hamcrest.Matchers.allOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class OrientationDialogTest {

	@Rule
	public DontGenerateDefaultProjectActivityTestRule<MainMenuActivity> baseActivityTestRule = new
			DontGenerateDefaultProjectActivityTestRule<>(MainMenuActivity.class, true, false);

	private boolean bufferedChromeCastSetting;
	private int bufferedPrivacyPolicyPreferenceSetting;

	@Before
	public void setUp() throws Exception {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());

		bufferedChromeCastSetting = sharedPreferences
				.getBoolean(SETTINGS_CAST_GLOBALLY_ENABLED, false);

		bufferedPrivacyPolicyPreferenceSetting = sharedPreferences
				.getInt(AGREED_TO_PRIVACY_POLICY_VERSION, 0);

		PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
				.edit()
				.putBoolean(SETTINGS_CAST_GLOBALLY_ENABLED, true)
				.putInt(AGREED_TO_PRIVACY_POLICY_VERSION, Constants.CATROBAT_TERMS_OF_USE_ACCEPTED)
				.commit();

		baseActivityTestRule.launchActivity(null);
	}

	@After
	public void tearDown() {
		PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
				.edit()
				.putBoolean(SETTINGS_CAST_GLOBALLY_ENABLED, bufferedChromeCastSetting)
				.putInt(AGREED_TO_PRIVACY_POLICY_VERSION,
						bufferedPrivacyPolicyPreferenceSetting)
				.commit();
	}

	@Test
	@Category({Level.Smoke.class, Cat.AppUi.class, Cat.Gadgets.class})
	public void testCreateNewCastProject() {
		onView(withId(R.id.newProjectFloatingActionButton))
				.perform(click());
		onView(withId(R.id.input_edit_text))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), isEnabled())));

		onView(allOf(withId(R.id.input_edit_text), isDisplayed()))
				.perform(replaceText("TestCastProject"), closeSoftKeyboard());

		onView(withId(R.id.cast_radio_button))
				.perform(click());

		onView(withId(R.id.example_project_switch))
				.check(matches(allOf(isDisplayed(), isNotChecked())));

		onView(withText(R.string.ok))
				.perform(click());

		assertCurrentActivityIsInstanceOf(ProjectActivity.class);
	}
}
