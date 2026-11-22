/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.AvailableLanguages;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Locale;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.PreferenceMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.CatroidApplication.defaultSystemLanguage;
import static org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_TAG_KEY;
import static org.junit.Assert.assertEquals;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Simulates an installation from Google Play that only contains the German language split
 * (as if the device language was German) and checks that only installed languages can be chosen.
 */
@RunWith(AndroidJUnit4.class)
public class LanguageSettingAvailableLanguagesTest {

	private static final String[] GERMAN_PLAY_INSTALL_SPLITS = {"config.de", "config.xxhdpi"};

	@Rule
	public BaseActivityTestRule<SettingsActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(SettingsActivity.class, true, false);

	private SharedPreferences sharedPreferences;
	private String initialLanguageTag;

	@Before
	public void setUp() {
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());
		initialLanguageTag = sharedPreferences.getString(LANGUAGE_TAG_KEY, null);
		AvailableLanguages.setOverrideForTesting(new AvailableLanguages(GERMAN_PLAY_INSTALL_SPLITS));
	}

	@After
	public void tearDown() {
		AvailableLanguages.setOverrideForTesting(null);
		if (initialLanguageTag == null) {
			sharedPreferences.edit().remove(LANGUAGE_TAG_KEY).commit();
		} else {
			sharedPreferences.edit().putString(LANGUAGE_TAG_KEY, initialLanguageTag).commit();
		}
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void languageListOnlyShowsInstalledLanguages() {
		baseActivityTestRule.launchActivity(null);

		onData(PreferenceMatchers.withTitle(R.string.preference_title_language))
				.perform(click());

		onView(withText(R.string.device_language)).check(matches(isDisplayed()));
		onView(withText(displayName("de"))).check(matches(isDisplayed()));
		onView(withText(displayName("en"))).check(matches(isDisplayed()));

		onView(withText(displayName("fr"))).check(doesNotExist());
		onView(withText(displayName("en-GB"))).check(doesNotExist());
		onView(withText(displayName("af"))).check(doesNotExist());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void notInstalledChosenLanguageFallsBackToDeviceLanguage() {
		Context context = ApplicationProvider.getApplicationContext();

		sharedPreferences.edit().putString(LANGUAGE_TAG_KEY, "fr").commit();
		assertEquals(Locale.forLanguageTag(defaultSystemLanguage),
				SettingsFragment.getChosenLocale(context));

		sharedPreferences.edit().putString(LANGUAGE_TAG_KEY, "de").commit();
		assertEquals(Locale.forLanguageTag("de"), SettingsFragment.getChosenLocale(context));
	}

	private static String displayName(String languageTag) {
		Locale locale = Locale.forLanguageTag(languageTag);
		return locale.getDisplayName(locale);
	}
}
