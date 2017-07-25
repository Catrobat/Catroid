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

package org.catrobat.catroid.uiespresso.ui.activity;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.PreferenceMatchers;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.phiro.ui.PhiroMainMenuActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.phiro.ui.PhiroMainMenuActivity.PHIRO_INITIALIZED;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_PHIRO_BRICKS;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class PhiroSettingsActivityTest {

	private IdlingResource idlingResource;

	@Rule
	public BaseActivityInstrumentationRule<PhiroMainMenuActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(PhiroMainMenuActivity.class, true, false);

	private List<String> allSettings = new ArrayList<>(Arrays.asList(SETTINGS_SHOW_PHIRO_BRICKS, PHIRO_INITIALIZED));
	private Map<String, Boolean> initialSettings = new HashMap<>();

	@Before
	public void setUp() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());

		for (String setting : allSettings) {
			initialSettings.put(setting, sharedPreferences.getBoolean(setting, false));
		}
		setAllSettingsTo(false);

		baseActivityTestRule.launchActivity(null);

		idlingResource = baseActivityTestRule.getActivity().getIdlingResource();
		Espresso.registerIdlingResources(idlingResource);
	}

	@After
	public void tearDown() {
		SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext()).edit();
		for (String setting : initialSettings.keySet()) {
			sharedPreferencesEditor.putBoolean(setting, initialSettings.get(setting));
		}
		sharedPreferencesEditor.commit();
		initialSettings.clear();
		Espresso.unregisterIdlingResources(idlingResource);
	}

	private void setAllSettingsTo(boolean value) {
		SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext()).edit();

		for (String setting : allSettings) {
			sharedPreferencesEditor.putBoolean(setting, value);
		}
		sharedPreferencesEditor.commit();
	}

	@Test
	public void phiroIsInitiallyActivatedTest() {
		openSettings();

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
		assertTrue("Phiro preference was not initially enabled!", sharedPreferences.getBoolean(SETTINGS_SHOW_PHIRO_BRICKS, false));
	}

	@Test
	public void phiroIsFirstVisibleSettingTest() {
		openSettings();

		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_phiro_bricks))
				.atPosition(0)
				.check(matches(isDisplayed()));
	}

	@Test
	public void phiroSettingsTest() {
		openSettings();

		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_phiro_bricks))
				.perform(click());

		checkPreference(R.string.preference_title_enable_phiro_bricks, SETTINGS_SHOW_PHIRO_BRICKS);

		String phiroLink = UiTestUtils.getResourcesString(R.string.phiro_preference_link);

		Intents.init();
		Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_VIEW), hasData(phiroLink));
		intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
		onData(PreferenceMatchers.withTitle(R.string.phiro_preference_title)).perform(click());
		intended(expectedIntent);
		Intents.release();
	}

	private void openSettings() {
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());

		onView(withText(R.string.settings))
				.perform(click());
	}

	private void checkPreference(int displayedTitleResourceString, String sharedPreferenceTag) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());

		onData(PreferenceMatchers.withTitle(displayedTitleResourceString)).perform(click());
		assertFalse("Preference is still enabled!", sharedPreferences.getBoolean(sharedPreferenceTag, false));

		onData(PreferenceMatchers.withTitle(displayedTitleResourceString)).perform(click());
		assertTrue("Preference is not enabled!", sharedPreferences.getBoolean(sharedPreferenceTag, false));
	}
}
