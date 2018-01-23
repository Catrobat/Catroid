/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.DontGenerateDefaultProjectActivityInstrumentationRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_CAST_GLOBALLY_ENABLED;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class OrientationDialogTest {

	@Rule
	public DontGenerateDefaultProjectActivityInstrumentationRule<MainMenuActivity> baseActivityTestRule = new
			DontGenerateDefaultProjectActivityInstrumentationRule<>(MainMenuActivity.class, true, false);

	private IdlingResource idlingResource;
	private String chromeCast = SETTINGS_CAST_GLOBALLY_ENABLED;
	private boolean initialChromeCastSetting;

	@Before
	public void setUp() throws Exception {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());

		SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

		initialChromeCastSetting = sharedPreferences.getBoolean(chromeCast, false);

		sharedPreferencesEditor.putBoolean(chromeCast, true);

		sharedPreferencesEditor.commit();

		baseActivityTestRule.launchActivity(null);

		idlingResource = baseActivityTestRule.getActivity().getIdlingResource();
		Espresso.registerIdlingResources(idlingResource);
	}

	@Test
	@Category({Level.Smoke.class, Cat.AppUi.class, Cat.Gadgets.class})
	public void newProject() {
		onView(withId(R.id.main_menu_button_new))
				.perform(click());
		onView(withText(R.string.new_project_dialog_title))
				.check(matches(isDisplayed()));
		onView(withClassName(is("android.support.design.widget.TextInputEditText")))
				.perform(typeText("TestCastProject"), closeSoftKeyboard());
		onView(withText(R.string.ok))
				.perform(click());
		onView(withText(R.string.project_select_screen_title))
				.check(matches(isDisplayed()));
		onView(withId(R.id.cast)).perform(click());
		onView(withText(R.string.ok))
				.perform(click());

		assertTrue(UiTestUtils.getCurrentActivity() instanceof ProjectActivity);
	}

	@After
	public void tearDown() {
		Espresso.unregisterIdlingResources(idlingResource);

		SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext()).edit();

		sharedPreferencesEditor.putBoolean(chromeCast, initialChromeCastSetting);

		sharedPreferencesEditor.commit();
	}
}
