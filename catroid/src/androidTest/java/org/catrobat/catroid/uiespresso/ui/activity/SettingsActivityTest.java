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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.PreferenceMatchers;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.ui.activity.utils.SettingsActivityUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
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

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.common.Constants.LANGUAGE_CODE;
import static org.catrobat.catroid.ui.BaseSettingsActivity.SETTINGS_CAST_GLOBALLY_ENABLED;
import static org.catrobat.catroid.ui.BaseSettingsActivity.SETTINGS_CRASH_REPORTS;
import static org.catrobat.catroid.ui.BaseSettingsActivity.SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED;
import static org.catrobat.catroid.ui.BaseSettingsActivity.SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED;
import static org.catrobat.catroid.ui.BaseSettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED;
import static org.catrobat.catroid.ui.BaseSettingsActivity.SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED;
import static org.catrobat.catroid.ui.BaseSettingsActivity.SETTINGS_SHOW_ARDUINO_BRICKS;
import static org.catrobat.catroid.ui.BaseSettingsActivity.SETTINGS_SHOW_HINTS;
import static org.catrobat.catroid.ui.BaseSettingsActivity.SETTINGS_SHOW_NFC_BRICKS;
import static org.catrobat.catroid.ui.BaseSettingsActivity.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS;
import static org.catrobat.catroid.ui.BaseSettingsActivity.SETTINGS_SHOW_PHIRO_BRICKS;
import static org.catrobat.catroid.ui.BaseSettingsActivity.SETTINGS_SHOW_RASPI_BRICKS;
import static org.catrobat.catroid.ui.SettingsActivity.SETTINGS_SHOW_PARROT_JUMPING_SUMO_BRICKS;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {

	@Rule
	public BaseActivityInstrumentationRule<SettingsActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SettingsActivity.class, true, false);

	private List<String> allSettings = new ArrayList<>(Arrays.asList(SETTINGS_SHOW_ARDUINO_BRICKS,
			SETTINGS_SHOW_PHIRO_BRICKS, SETTINGS_SHOW_NFC_BRICKS, SETTINGS_SHOW_HINTS, SETTINGS_CRASH_REPORTS,
			SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED,
			SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED, SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED,
			SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, SETTINGS_SHOW_PARROT_JUMPING_SUMO_BRICKS,
			SETTINGS_SHOW_RASPI_BRICKS,
			SETTINGS_CAST_GLOBALLY_ENABLED));
	private Map<String, Boolean> initialSettings = new HashMap<>();

	@Before
	public void setUp() throws Exception {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());

		for (String setting : allSettings) {
			initialSettings.put(setting, sharedPreferences.getBoolean(setting, false));
		}
		SettingsActivityUtils.setAllSettingsTo(allSettings, true);
		baseActivityTestRule.launchActivity(null);
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
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void basicSettingsTest() {
		SettingsActivityUtils.checkPreference(R.string.preference_title_enable_arduino_bricks, SETTINGS_SHOW_ARDUINO_BRICKS);
		SettingsActivityUtils.checkPreference(R.string.preference_title_enable_phiro_bricks, SETTINGS_SHOW_PHIRO_BRICKS);
		SettingsActivityUtils.checkPreference(R.string.preference_title_enable_nfc_bricks, SETTINGS_SHOW_NFC_BRICKS);
		SettingsActivityUtils.checkPreference(R.string.preference_title_enable_hints, SETTINGS_SHOW_HINTS);
		SettingsActivityUtils.checkPreference(R.string.preference_title_enable_crash_reports, SETTINGS_CRASH_REPORTS);
		SettingsActivityUtils.checkPreference(R.string.preference_title_cast_feature_globally_enabled, SETTINGS_CAST_GLOBALLY_ENABLED);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void legoNxtSettingsTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_mindstorms_nxt_bricks))
				.perform(click());

		SettingsActivityUtils.checkPreference(R.string.preference_title_enable_mindstorms_nxt_bricks, SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED);
		SettingsActivityUtils.checkPreference(R.string.preference_disable_nxt_info_dialog, SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void legoEv3SettingsTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_mindstorms_ev3_bricks))
				.perform(click());

		SettingsActivityUtils.checkPreference(R.string.preference_title_enable_mindstorms_ev3_bricks, SETTINGS_MINDSTORMS_EV3_BRICKS_ENABLED);
		SettingsActivityUtils.checkPreference(R.string.preference_disable_nxt_info_dialog,
				SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void parrotArSettingsTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_quadcopter_bricks))
				.perform(click());

		SettingsActivityUtils.checkPreference(R.string.preference_title_enable_quadcopter_bricks, SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void parrotJumpingSumoSettingsTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_jumpingsumo_bricks)).perform(click());

		SettingsActivityUtils.checkPreference(R.string.preference_title_enable_jumpingsumo_bricks,
				SETTINGS_SHOW_PARROT_JUMPING_SUMO_BRICKS);
	}

	@Test
	public void rasPiSettingsTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_raspi_bricks))
				.perform(click());

		SettingsActivityUtils.checkPreference(R.string.preference_title_enable_raspi_bricks, SETTINGS_SHOW_RASPI_BRICKS);
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
}
