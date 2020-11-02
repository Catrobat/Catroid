/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.regression.activitydestroy;

import android.preference.PreferenceManager;

import org.catrobat.catroid.R;
import org.catrobat.catroid.rules.FlakyTestRule;
import org.catrobat.catroid.runner.Flaky;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uiespresso.util.rules.DontGenerateDefaultProjectActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.hamcrest.Matchers.is;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainMenuFragemtnActivityRecreateRegressionTest {

	@Rule
	public DontGenerateDefaultProjectActivityTestRule<MainMenuActivity> baseActivityTestRule = new
			DontGenerateDefaultProjectActivityTestRule<>(MainMenuActivity.class, false, false);

	@Rule
	public FlakyTestRule flakyTestRule = new FlakyTestRule();

	private static final String AGREED_TO_PRIVACY_POLICY_SETTINGS_KEY = "AgreedToPrivacyPolicy";
	private boolean bufferedPreferenceSetting;

	@Before
	public void setUp() throws Exception {
		bufferedPreferenceSetting =
				PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
				.getBoolean(AGREED_TO_PRIVACY_POLICY_SETTINGS_KEY, false);

		PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
				.edit()
				.putBoolean(AGREED_TO_PRIVACY_POLICY_SETTINGS_KEY, true)
				.commit();
		baseActivityTestRule.launchActivity(null);
	}

	@After
	public void tearDown() {
		PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
				.edit()
				.putBoolean(AGREED_TO_PRIVACY_POLICY_SETTINGS_KEY, bufferedPreferenceSetting)
				.commit();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Quarantine.class})
	@Flaky
	@Test
	public void testActivityRecreateOrientation() {
		onView(withId(R.id.floating_action_button))
				.perform(click());
		onView(withId(R.id.input_edit_text))
				.check(matches(isDisplayed()));
		onView(withClassName(is("com.google.android.material.textfield.TextInputEditText")))
				.perform(typeText("TestProject"), closeSoftKeyboard());
		onView(withText(R.string.ok))
				.perform(click());

		InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
			@Override
			public void run() {
				baseActivityTestRule.getActivity().recreate();
			}
		});

		InstrumentationRegistry.getInstrumentation().waitForIdleSync();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Quarantine.class})
	@Flaky
	@Test
	public void testActivityRecreateNewProgramDialog() {
		onView(withId(R.id.floating_action_button))
				.perform(click());

		onView(withId(R.id.input_edit_text)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> baseActivityTestRule.getActivity().recreate());

		InstrumentationRegistry.getInstrumentation().waitForIdleSync();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Quarantine.class})
	@Flaky
	@Test
	public void testActivityRecreateTermsOfUseDialog() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.main_menu_terms_of_use)).perform(click());

		onView(withId(R.id.dialog_terms_of_use_text_view_info)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
			@Override
			public void run() {
				baseActivityTestRule.getActivity().recreate();
			}
		});

		InstrumentationRegistry.getInstrumentation().waitForIdleSync();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Quarantine.class})
	@Flaky
	@Test
	public void testActivityRecreateAboutDialog() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.main_menu_about)).perform(click());

		onView(withText(R.string.dialog_about_title)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
			@Override
			public void run() {
				baseActivityTestRule.getActivity().recreate();
			}
		});

		InstrumentationRegistry.getInstrumentation().waitForIdleSync();
	}
}
