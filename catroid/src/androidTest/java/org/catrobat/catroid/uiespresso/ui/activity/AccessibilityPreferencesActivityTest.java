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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.AccessibilityPreferencesActivity;
import org.catrobat.catroid.ui.BaseSettingsActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uiespresso.ui.activity.utils.AccessibilityActivitiesUtils;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.DontGenerateDefaultProjectActivityInstrumentationRule;
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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_ADDITIONAL_ICONS;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_DRAGNDROP_DELAY;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_FONTFACE;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_FONTFACE_VALUE_DYSLEXIC;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_HIGH_CONTRAST;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_HIGH_CONTRAST_ICONS;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_LARGE_ELEMENT_SPACING;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_LARGE_ICONS;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_LARGE_TEXT;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_MYPROFILE_ADDITIONAL_ICONS;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_MYPROFILE_DRAGNDROP_DELAY;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_MYPROFILE_FONTFACE;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_MYPROFILE_HIGH_CONTRAST;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_MYPROFILE_HIGH_CONTRAST_ICONS;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_MYPROFILE_LARGE_ELEMENT_SPACING;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_MYPROFILE_LARGE_ICONS;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_MYPROFILE_LARGE_TEXT;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_MYPROFILE_STARTER_BRICKS;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_PROFILE_ACTIVE;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_STARTER_BRICKS;

@RunWith(AndroidJUnit4.class)
public class AccessibilityPreferencesActivityTest {

	private IdlingResource idlingResource;

	private List<String> settings = new ArrayList<>(Arrays.asList(ACCESS_LARGE_TEXT, ACCESS_HIGH_CONTRAST,
			ACCESS_ADDITIONAL_ICONS, ACCESS_LARGE_ICONS, ACCESS_HIGH_CONTRAST_ICONS, ACCESS_LARGE_ELEMENT_SPACING,
			ACCESS_STARTER_BRICKS, ACCESS_DRAGNDROP_DELAY));
	private List<String> myProfileSettings = new ArrayList<>(Arrays.asList(ACCESS_MYPROFILE_LARGE_TEXT, ACCESS_MYPROFILE_HIGH_CONTRAST,
			ACCESS_MYPROFILE_ADDITIONAL_ICONS, ACCESS_MYPROFILE_LARGE_ICONS, ACCESS_MYPROFILE_HIGH_CONTRAST_ICONS, ACCESS_MYPROFILE_LARGE_ELEMENT_SPACING,
			ACCESS_MYPROFILE_STARTER_BRICKS, ACCESS_MYPROFILE_DRAGNDROP_DELAY));
	private Map<String, Boolean> initialSettings = new HashMap<>();
	private String initialActiveFontface;
	private String initialFontfaceSetting;
	private String initialMyProfileFontfaceSetting;

	@Rule
	public DontGenerateDefaultProjectActivityInstrumentationRule<AccessibilityPreferencesActivity> baseActivityTestRule = new
			DontGenerateDefaultProjectActivityInstrumentationRule<>(AccessibilityPreferencesActivity.class);

	@Before
	public void setUp() throws Exception {
		Context context = InstrumentationRegistry.getTargetContext();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

		for (String setting : settings) {
			initialSettings.put(setting, sharedPreferences.getBoolean(setting, false));
		}
		for (String setting : myProfileSettings) {
			initialSettings.put(setting, sharedPreferences.getBoolean(setting, false));
		}
		initialActiveFontface = BaseSettingsActivity.getActiveAccessibilityProfile(context);
		initialFontfaceSetting = BaseSettingsActivity.getAccessibilityFontFace(context);
		initialMyProfileFontfaceSetting = BaseSettingsActivity.getAccessibilityMyProfileFontFace(context);

		AccessibilityActivitiesUtils.resetSettings(settings, myProfileSettings);
		baseActivityTestRule.launchActivity(null);
	}

	@After
	public void tearDown() {
		SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext()).edit();

		for (String setting : initialSettings.keySet()) {
			sharedPreferencesEditor.putBoolean(setting, initialSettings.get(setting));
		}
		sharedPreferencesEditor.putString(ACCESS_PROFILE_ACTIVE, initialActiveFontface);
		sharedPreferencesEditor.putString(ACCESS_FONTFACE, initialFontfaceSetting);
		sharedPreferencesEditor.putString(ACCESS_MYPROFILE_FONTFACE, initialMyProfileFontfaceSetting);
		sharedPreferencesEditor.commit();

		initialSettings.clear();
		BaseSettingsActivity.applyAccessibilitySettings(InstrumentationRegistry.getTargetContext());
		IdlingRegistry.getInstance().unregister(idlingResource);
	}

	@Test
	public void settingsTest() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());

		onView(withText(R.string.preference_access_title_fontface)).perform(click());
		onView(withText(R.string.preference_access_title_fontface_dyslexic)).perform(click());
		assertEquals(sharedPreferences.getString(ACCESS_MYPROFILE_FONTFACE, ""), ACCESS_FONTFACE_VALUE_DYSLEXIC);

		onView(withText(R.string.close)).perform(click());

		onView(withText(R.string.preference_access_title_large_text)).perform(scrollTo(), click());
		assertTrue(sharedPreferences.getBoolean(ACCESS_MYPROFILE_LARGE_TEXT, false));

		onView(withText(R.string.preference_access_title_high_contrast)).perform(scrollTo(), click());
		assertTrue(sharedPreferences.getBoolean(ACCESS_MYPROFILE_HIGH_CONTRAST, false));

		onView(withText(R.string.preference_access_title_additional_icons)).perform(scrollTo(), click());
		assertTrue(sharedPreferences.getBoolean(ACCESS_MYPROFILE_ADDITIONAL_ICONS, false));

		onView(withText(R.string.preference_access_title_large_icons)).perform(scrollTo(), click());
		assertTrue(sharedPreferences.getBoolean(ACCESS_MYPROFILE_LARGE_ICONS, false));

		onView(withText(R.string.preference_access_title_high_contrast_icons)).perform(scrollTo(), click());
		assertTrue(sharedPreferences.getBoolean(ACCESS_MYPROFILE_HIGH_CONTRAST_ICONS, false));

		onView(withText(R.string.preference_access_title_largeelementspacing)).perform(scrollTo(), click());
		assertTrue(sharedPreferences.getBoolean(ACCESS_MYPROFILE_LARGE_ELEMENT_SPACING, false));

		onView(withText(R.string.preference_access_title_starter_bricks)).perform(scrollTo(), click());
		assertTrue(sharedPreferences.getBoolean(ACCESS_MYPROFILE_STARTER_BRICKS, false));

		onView(withText(R.string.preference_access_title_dragndrop_delay)).perform(scrollTo(), click());
		assertTrue(sharedPreferences.getBoolean(ACCESS_MYPROFILE_DRAGNDROP_DELAY, false));

		onView(withId(R.id.scratch_project_apply_button)).perform(click());
		onView(withText(R.string.ok)).perform(click());

		MainMenuActivity mainMenuActivity = (MainMenuActivity) UiTestUtils.getCurrentActivity();
		idlingResource = mainMenuActivity.getIdlingResource();
		IdlingRegistry.getInstance().register(idlingResource);

		for (String setting : settings) {
			assertTrue(sharedPreferences.getBoolean(setting, false));
		}
		assertEquals(sharedPreferences.getString(BaseSettingsActivity.ACCESS_FONTFACE, ""),
				BaseSettingsActivity.ACCESS_FONTFACE_VALUE_DYSLEXIC);
	}
}
