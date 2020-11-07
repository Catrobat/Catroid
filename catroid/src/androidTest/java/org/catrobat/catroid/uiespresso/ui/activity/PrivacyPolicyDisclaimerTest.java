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

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION;
import static org.hamcrest.Matchers.allOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class PrivacyPolicyDisclaimerTest {

	@Rule
	public ActivityTestRule<MainMenuActivity> activityTestRule =
			new ActivityTestRule<>(MainMenuActivity.class, true, false);

	private int bufferedPreferenceSetting;
	private String privacyPolicyUrl;

	@Before
	public void setUp() {
		bufferedPreferenceSetting = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).getInt(AGREED_TO_PRIVACY_POLICY_VERSION, 0);

		Context context = getInstrumentation().getTargetContext();

		privacyPolicyUrl = context.getString(R.string.privacy_policy_url);

		PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
				.edit()
				.putInt(AGREED_TO_PRIVACY_POLICY_VERSION,
						getInstrumentation().getTargetContext()
								.getString(R.string.dialog_privacy_policy_text)
								.hashCode())
				.commit();
	}

	@After
	public void tearDown() {
		PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
				.edit()
				.putInt(AGREED_TO_PRIVACY_POLICY_VERSION, bufferedPreferenceSetting)
				.commit();
	}

	@Test
	public void mainMenuActivityTest() {
		activityTestRule.launchActivity(new Intent());
		Intents.init();
		openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
		onView(withText(R.string.main_menu_privacy_policy)).perform(click());
		Matcher expectedIntent = allOf(hasAction(Intent.ACTION_VIEW), hasData(privacyPolicyUrl));
		intended(expectedIntent);
		Intents.release();
	}
}
