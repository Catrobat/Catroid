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

package org.catrobat.catroid.uiespresso.ui.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.catrobat.catroid.R;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.PreferenceMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.SharedPreferenceKeys.ACCESSIBILITY_PROFILE_PREFERENCE_KEY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_CODE;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.PARROT_JUMPING_SUMO_SCREEN_KEY;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_CRASH_REPORTS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_ARDUINO_BRICKS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_HINTS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_NFC_BRICKS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_PHIRO_BRICKS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_RASPI_BRICKS;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SettingsFragmentTest {

	@Rule
	public BaseActivityTestRule<SettingsActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(SettingsActivity.class, true, false);

	private List<String> allSettings = new ArrayList<>(Arrays.asList(SETTINGS_SHOW_ARDUINO_BRICKS,
			SETTINGS_SHOW_PHIRO_BRICKS, SETTINGS_SHOW_NFC_BRICKS, SETTINGS_SHOW_HINTS, SETTINGS_CRASH_REPORTS,
			SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED,
			SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED, SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED,
			SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, PARROT_JUMPING_SUMO_SCREEN_KEY,
			SETTINGS_SHOW_RASPI_BRICKS,
			SETTINGS_CAST_GLOBALLY_ENABLED));
	private Map<String, Boolean> initialSettings = new HashMap<>();

	@Before
	public void setUp() throws Exception {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());

		for (String setting : allSettings) {
			initialSettings.put(setting, sharedPreferences.getBoolean(setting, false));
		}
		setAllSettingsTo(true);
		baseActivityTestRule.launchActivity(null);
	}

	private void setAllSettingsTo(boolean value) {
		SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
				.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit();

		for (String setting : allSettings) {
			sharedPreferencesEditor.putBoolean(setting, value);
		}
		sharedPreferencesEditor.putInt(ACCESSIBILITY_PROFILE_PREFERENCE_KEY, R.id.default_profile);
		sharedPreferencesEditor.commit();
	}

	@After
	public void tearDown() {
		SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
				.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit();
		for (String setting : initialSettings.keySet()) {
			sharedPreferencesEditor.putBoolean(setting, initialSettings.get(setting));
		}
		sharedPreferencesEditor.putInt(ACCESSIBILITY_PROFILE_PREFERENCE_KEY, R.id.default_profile);
		sharedPreferencesEditor.commit();
		initialSettings.clear();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void basicSettingsTest() {
		checkPreference(R.string.preference_title_enable_arduino_bricks, SETTINGS_SHOW_ARDUINO_BRICKS);
		checkPreference(R.string.preference_title_enable_phiro_bricks, SETTINGS_SHOW_PHIRO_BRICKS);
		checkPreference(R.string.preference_title_enable_nfc_bricks, SETTINGS_SHOW_NFC_BRICKS);
		checkPreference(R.string.preference_title_enable_hints, SETTINGS_SHOW_HINTS);
		checkPreference(R.string.preference_title_enable_crash_reports, SETTINGS_CRASH_REPORTS);
		checkPreference(R.string.preference_title_cast_feature_globally_enabled, SETTINGS_CAST_GLOBALLY_ENABLED);
	}

	@Category({Cat.AppUi.class, Level.Functional.class, Cat.Quarantine.class})
	@Test
	public void noMultipleSelectAccessibilityProfilesTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_accessibility))
				.perform(click());

		onData(PreferenceMatchers.withTitle(R.string.preference_title_accessibility_predefined_profile_headline))
				.perform(click());

		onView(allOf(withId(R.id.radio_button), withParent(withId(R.id.argus))))
				.perform(click());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void legoNxtSettingsTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_mindstorms_nxt_bricks))
				.perform(click());

		checkPreference(R.string.preference_title_enable_mindstorms_nxt_bricks, SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED);
		checkPreference(R.string.preference_disable_nxt_info_dialog, SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void legoEv3SettingsTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_mindstorms_ev3_bricks))
				.perform(click());

		checkPreference(R.string.preference_title_enable_mindstorms_ev3_bricks, SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED);
		checkPreference(R.string.preference_disable_nxt_info_dialog,
				SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void parrotArSettingsTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_quadcopter_bricks))
				.perform(click());

		checkPreference(R.string.preference_title_enable_quadcopter_bricks, SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void parrotJumpingSumoSettingsTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_jumpingsumo_bricks)).perform(click());

		checkPreference(R.string.preference_title_enable_jumpingsumo_bricks, PARROT_JUMPING_SUMO_SCREEN_KEY);
	}

	@Test
	public void rasPiSettingsTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_raspi_bricks))
				.perform(click());

		checkPreference(R.string.preference_title_enable_raspi_bricks, SETTINGS_SHOW_RASPI_BRICKS);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void languageSettingTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_language))
				.perform(click());
		onView(withText(R.string.preference_title_language))
				.check(matches(isDisplayed()));
		onData(is(instanceOf(String.class))).atPosition(0)
				.check(matches(withText(R.string.device_language)));
		for (String rtlLanguage : LANGUAGE_CODE) {

			if (rtlLanguage.equals("sd")) {
				onData(hasToString("سنڌي"))
						.check(matches(isDisplayed()));
			} else if (rtlLanguage.length() == 2) {
				Locale rtlLocale = new Locale(rtlLanguage);
				onData(hasToString(rtlLocale.getDisplayName(rtlLocale)))
						.check(matches(isDisplayed()));
			} else if (rtlLanguage.length() == 6) {
				String language = rtlLanguage.substring(0, 2);
				String country = rtlLanguage.substring(4);
				Locale rtlLocale = new Locale(language, country);
				onData(hasToString(rtlLocale.getDisplayName(rtlLocale)))
						.check(matches(isDisplayed()));
			}
		}
		onView(withId(android.R.id.button2))
				.check(matches(isDisplayed()));
	}

	private void checkPreference(int displayedTitleResourceString, String sharedPreferenceTag) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());

		onData(PreferenceMatchers.withTitle(displayedTitleResourceString))
				.perform(click());

		assertFalse(sharedPreferences.getBoolean(sharedPreferenceTag, false));

		onData(PreferenceMatchers.withTitle(displayedTitleResourceString))
				.perform(click());

		assertTrue(sharedPreferences.getBoolean(sharedPreferenceTag, false));
	}
}
