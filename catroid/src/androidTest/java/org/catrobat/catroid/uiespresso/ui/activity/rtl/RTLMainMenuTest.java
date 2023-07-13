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

package org.catrobat.catroid.uiespresso.ui.activity.rtl;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.view.View;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.uiespresso.util.rules.DontGenerateDefaultProjectActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Locale;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResources;

@RunWith(AndroidJUnit4.class)
public class RTLMainMenuTest {

	@Rule
	public DontGenerateDefaultProjectActivityTestRule<MainMenuActivity> baseActivityTestRule = new
			DontGenerateDefaultProjectActivityTestRule<>(MainMenuActivity.class, false, false);

	private int bufferedPrivacyPolicyPreferenceSetting;

	private static final Locale ARABIC_LOCALE = new Locale("ar");
	private static final Locale GERMAN_LOCALE = Locale.GERMAN;

	private Configuration conf = getResources().getConfiguration();

	@Before
	public void setUp() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());

		bufferedPrivacyPolicyPreferenceSetting = sharedPreferences
				.getInt(AGREED_TO_PRIVACY_POLICY_VERSION, 0);

		PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
				.edit()
				.putInt(AGREED_TO_PRIVACY_POLICY_VERSION, Constants.CATROBAT_TERMS_OF_USE_ACCEPTED)
				.commit();
	}

	@After
	public void tearDown() {
		PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
				.edit()
				.putInt(AGREED_TO_PRIVACY_POLICY_VERSION,
						bufferedPrivacyPolicyPreferenceSetting)
				.commit();
		SettingsFragment.removeLanguageSharedPreference(ApplicationProvider.getApplicationContext());
		SensorHandler.stopSensorListeners();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.RTLTests.class})
	@Test
	public void testSetLanguageToArabic() {
		String arabicLanguageTag = "ar";
		SensorHandler.startSensorListener(ApplicationProvider.getApplicationContext());
		SettingsFragment.setLanguageSharedPreference(ApplicationProvider.getApplicationContext(), arabicLanguageTag);
		baseActivityTestRule.launchActivity(null);

		assertEquals(Locale.getDefault().getDisplayLanguage(), ARABIC_LOCALE.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirectionIsRtl(Locale.getDefault().getDisplayName()));
		assertEquals(View.LAYOUT_DIRECTION_RTL, conf.getLayoutDirection());
		assertEquals(arabicLanguageTag, (String) SensorHandler.getSensorValue(Sensors.USER_LANGUAGE));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.RTLTests.class})
	@Test
	public void testSetLanguageToGerman() {
		String germanLanguageTag = "de";
		SensorHandler.startSensorListener(ApplicationProvider.getApplicationContext());
		SettingsFragment.setLanguageSharedPreference(ApplicationProvider.getApplicationContext(), germanLanguageTag);
		baseActivityTestRule.launchActivity(null);

		assertEquals(Locale.getDefault().getDisplayLanguage(), GERMAN_LOCALE.getDisplayLanguage());
		assertFalse(RtlUiTestUtils.checkTextDirectionIsRtl(Locale.getDefault().getDisplayName()));
		assertEquals(View.LAYOUT_DIRECTION_LTR, conf.getLayoutDirection());
		assertEquals(germanLanguageTag, (String) SensorHandler.getSensorValue(Sensors.USER_LANGUAGE));
	}
}
