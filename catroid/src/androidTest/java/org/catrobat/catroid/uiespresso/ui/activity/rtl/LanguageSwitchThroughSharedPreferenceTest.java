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

package org.catrobat.catroid.uiespresso.ui.activity.rtl;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.uiespresso.util.rules.DontGenerateDefaultProjectActivityInstrumentationRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResources;

@RunWith(AndroidJUnit4.class)
public class LanguageSwitchThroughSharedPreferenceTest {

	@Rule
	public DontGenerateDefaultProjectActivityInstrumentationRule<MainMenuActivity> baseActivityTestRule = new
			DontGenerateDefaultProjectActivityInstrumentationRule<>(MainMenuActivity.class);

	private boolean bufferedPrivacyPolicyPreferenceSetting;
	private boolean bufferedImportFromExternalStoragePreferenceSetting;

	private static final Locale ARABIC_LOCALE = new Locale("ar");
	private static final Locale GERMAN_LOCALE = Locale.GERMAN;

	private Configuration conf = getResources().getConfiguration();

	@Before
	public void setUp() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());

		bufferedPrivacyPolicyPreferenceSetting = sharedPreferences
				.getBoolean(AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY, false);

		bufferedImportFromExternalStoragePreferenceSetting = sharedPreferences
				.getBoolean(SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG, false);

		PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
				.edit()
				.putBoolean(AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY, true)
				.putBoolean(SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG, false)
				.commit();
	}

	@After
	public void tearDown() {
		PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
				.edit()
				.putBoolean(AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY, bufferedPrivacyPolicyPreferenceSetting)
				.putBoolean(SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG, bufferedImportFromExternalStoragePreferenceSetting)
				.commit();
		SettingsFragment.removeLanguageSharedPreference(InstrumentationRegistry.getTargetContext());
	}

	@Test
	public void testSetLanguageToArabic() {
		SettingsFragment.setLanguageSharedPreference(InstrumentationRegistry.getTargetContext(), "ar");
		baseActivityTestRule.launchActivity(null);

		assertEquals(Locale.getDefault().getDisplayLanguage(), ARABIC_LOCALE.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirectionIsRtl(Locale.getDefault().getDisplayName()));
		assertEquals(View.LAYOUT_DIRECTION_RTL, conf.getLayoutDirection());
	}

	@Test
	public void testSetLanguageToGerman() {
		SettingsFragment.setLanguageSharedPreference(InstrumentationRegistry.getTargetContext(), "de");
		baseActivityTestRule.launchActivity(null);

		assertEquals(Locale.getDefault().getDisplayLanguage(), GERMAN_LOCALE.getDisplayLanguage());
		assertFalse(RtlUiTestUtils.checkTextDirectionIsRtl(Locale.getDefault().getDisplayName()));
		assertEquals(View.LAYOUT_DIRECTION_LTR, conf.getLayoutDirection());
	}
}
