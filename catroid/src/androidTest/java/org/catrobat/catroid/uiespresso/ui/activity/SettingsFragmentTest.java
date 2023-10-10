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

package org.catrobat.catroid.uiespresso.ui.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.hamcrest.Matcher;
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
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.PreferenceMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.SharedPreferenceKeys.ACCESSIBILITY_PROFILE_PREFERENCE_KEY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.DEVICE_LANGUAGE;
import static org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_TAGS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_CRASH_REPORTS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_EV3_BRICKS_CHECKBOX_PREFERENCE;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_CHECKBOX_PREFERENCE;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MULTIPLAYER_VARIABLES_ENABLED;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_ARDUINO_BRICKS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_EMBROIDERY_BRICKS_CHECKBOX_PREFERENCE;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_HINTS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_JUMPING_SUMO_BRICKS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_NFC_BRICKS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_PHIRO_BRICKS_CHECKBOX_PREFERENCE;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_RASPI_BRICKS;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
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
			SETTINGS_SHOW_PHIRO_BRICKS_CHECKBOX_PREFERENCE, SETTINGS_SHOW_NFC_BRICKS, SETTINGS_SHOW_HINTS,
			SETTINGS_CRASH_REPORTS, SETTINGS_MINDSTORMS_NXT_BRICKS_CHECKBOX_PREFERENCE,
			SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED,
			SETTINGS_MINDSTORMS_EV3_BRICKS_CHECKBOX_PREFERENCE,
			SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED,
			SETTINGS_SHOW_EMBROIDERY_BRICKS_CHECKBOX_PREFERENCE,
			SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS, SETTINGS_SHOW_JUMPING_SUMO_BRICKS,
			SETTINGS_SHOW_RASPI_BRICKS, SETTINGS_MULTIPLAYER_VARIABLES_ENABLED,
			SETTINGS_CAST_GLOBALLY_ENABLED, SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS,
			SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS,
			SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS, SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS,
			SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS));
	private Map<String, Boolean> initialSettings = new HashMap<>();
	private Matcher<Intent> expectedBrowserIntent;

	@Before
	public void setUp() throws Exception {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());

		for (String setting : allSettings) {
			initialSettings.put(setting, sharedPreferences.getBoolean(setting, false));
		}
		setAllSettingsTo(true);
		baseActivityTestRule.launchActivity(null);

		Intents.init();
		expectedBrowserIntent = allOf(
				hasAction(Intent.ACTION_VIEW),
				hasData(Uri.parse(Constants.WEB_REQUEST_WIKI_URL)));

		intending(expectedBrowserIntent).respondWith(
				new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
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
		Intents.release();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void basicSettingsTest() {
		checkPreference(R.string.preference_title_enable_arduino_bricks, SETTINGS_SHOW_ARDUINO_BRICKS);

		if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_PHIRO)) {
			checkPreference(R.string.preference_title_enable_phiro_bricks, SETTINGS_SHOW_PHIRO_BRICKS_CHECKBOX_PREFERENCE);
		} else {
			openAppstoreDialog(R.string.preference_title_enable_phiro_bricks);
		}

		if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_EMBROIDERY_DESIGNER)) {
			checkPreference(R.string.preference_title_enable_embroidery_bricks, SETTINGS_SHOW_EMBROIDERY_BRICKS_CHECKBOX_PREFERENCE);
		} else {
			openAppstoreDialog(R.string.preference_title_enable_embroidery_bricks);
		}

		checkPreference(R.string.preference_title_enable_jumpingsumo_bricks, SETTINGS_SHOW_JUMPING_SUMO_BRICKS);
		checkPreference(R.string.preference_title_enable_nfc_bricks, SETTINGS_SHOW_NFC_BRICKS);
		checkPreference(R.string.preference_title_enable_hints, SETTINGS_SHOW_HINTS);
		checkPreference(R.string.preference_title_enable_crash_reports, SETTINGS_CRASH_REPORTS);
		checkPreference(R.string.preference_title_cast_feature_globally_enabled, SETTINGS_CAST_GLOBALLY_ENABLED);
		checkPreference(R.string.preference_title_multiplayer_variables_enabled, SETTINGS_MULTIPLAYER_VARIABLES_ENABLED);
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

		if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_LEGO_NXT_EV3)) {
			checkPreference(R.string.preference_title_enable_mindstorms_nxt_bricks, SETTINGS_MINDSTORMS_NXT_BRICKS_CHECKBOX_PREFERENCE);
		} else {
			openAppstoreDialog(R.string.preference_title_enable_mindstorms_nxt_bricks);
		}

		checkPreference(R.string.preference_disable_nxt_info_dialog, SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void legoEv3SettingsTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_mindstorms_ev3_bricks))
				.perform(click());

		if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_LEGO_NXT_EV3)) {
			checkPreference(R.string.preference_title_enable_mindstorms_ev3_bricks, SETTINGS_MINDSTORMS_EV3_BRICKS_CHECKBOX_PREFERENCE);
		} else {
			openAppstoreDialog(R.string.preference_title_enable_mindstorms_ev3_bricks);
		}

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
	public void rasPiSettingsTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_enable_raspi_bricks))
				.perform(click());

		checkPreference(R.string.preference_title_enable_raspi_bricks, SETTINGS_SHOW_RASPI_BRICKS);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Gadgets.class})
	@Test
	public void aiSettingsTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_ai))
				.perform(click());

		checkPreference(R.string.preference_title_ai_speech_recognition,
				SETTINGS_SHOW_AI_SPEECH_RECOGNITION_SENSORS);
		checkPreference(R.string.preference_title_ai_speech_synthetization,
				SETTINGS_SHOW_AI_SPEECH_SYNTHETIZATION_SENSORS);
		checkPreference(R.string.preference_title_ai_face_detection,
				SETTINGS_SHOW_AI_FACE_DETECTION_SENSORS);
		checkPreference(R.string.preference_title_ai_pose_detection,
				SETTINGS_SHOW_AI_POSE_DETECTION_SENSORS);
		checkPreference(R.string.preference_title_ai_text_recognition,
				SETTINGS_SHOW_AI_TEXT_RECOGNITION_SENSORS);
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
		for (String languageTag : LANGUAGE_TAGS) {
			if (!languageTag.equals(DEVICE_LANGUAGE)) {
				Locale rtlLocale = Locale.forLanguageTag(languageTag);
				onData(hasToString(rtlLocale.getDisplayName(rtlLocale)))
						.check(matches(isDisplayed()));
			}
		}
		onView(withId(android.R.id.button2))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void webAccessSettingTest() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_web_access)).perform(click());
		onView(withText(R.string.preference_screen_web_access_title)).check(matches(isDisplayed()));

		onView(withId(android.R.id.edit)).perform(typeText("domain.net"));

		onView(withId(android.R.id.button1)).check(matches(isDisplayed()));
		onView(withId(android.R.id.button2)).check(matches(isDisplayed()));

		onView(withId(android.R.id.button3)).perform(click());
		intended(expectedBrowserIntent);
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

	private void openAppstoreDialog(int displayedTitleResourceString) {
		onData(PreferenceMatchers.withTitle(displayedTitleResourceString))
				.perform(click());

		if (!Build.BRAND.equals(Constants.DEVICE_BRAND_HUAWEI)) {
			onView(withText(R.string.preference_dialog_google_play))
					.check(matches(isDisplayed()));
		} else {
			onView(withText(R.string.preference_dialog_appgallery))
					.check(matches(isDisplayed()));
		}

		onView(withText(R.string.cancel_button_text))
				.perform(click());
	}
}
