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

package org.catrobat.catroid.uiespresso.ui.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;

import org.catrobat.catroid.R;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.DontGenerateDefaultProjectActivityTestRule;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;

import static org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION;
import static org.catrobat.catroid.uiespresso.util.matchers.BundleMatchers.bundleHasMatchingString;

@Category({Cat.AppUi.class, Level.Functional.class})
@RunWith(Parameterized.class)
public class OpenFromShareLinkTest {

	@Rule
	public DontGenerateDefaultProjectActivityTestRule<MainMenuActivity> baseActivityTestRule = new
			DontGenerateDefaultProjectActivityTestRule<>(MainMenuActivity.class, false, false);

	private int bufferedPreferenceSetting;

	private Matcher expectedWebIntent;
	private Uri shareUri;

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"https://share.catrob.at/pocketcode/"},
				{"https://share.catrob.at/pocketcode/program/817?rec_by_page_id=1&rec_user_specific=0"},
				{"https://share.catrob.at/pocketcode/program/817"},
				{"https://share.catrob.at/luna/"},
				{"https://share.catrob.at/luna/program/817?rec_by_page_id=1&rec_user_specific=0"},
				{"https://share.catrob.at/luna/program/817"},
		});
	}

	@Parameterized.Parameter
	public String targetHttpPath;

	@Before
	public void setUp() throws Exception {
		bufferedPreferenceSetting = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry
				.getTargetContext())
				.getInt(AGREED_TO_PRIVACY_POLICY_VERSION, 0);

		PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
				.edit()
				.putInt(AGREED_TO_PRIVACY_POLICY_VERSION,
						UiTestUtils.getResourcesString(R.string.dialog_privacy_policy_text)
						.hashCode())
				.commit();

		shareUri = new Uri.Builder()
				.encodedPath(targetHttpPath)
				.build();

		expectedWebIntent = hasExtras(bundleHasMatchingString(WebViewActivity.INTENT_PARAMETER_URL, shareUri.toString()));

		Intents.init();

		Intent resultData = new Intent();
		Instrumentation.ActivityResult result =
				new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, resultData);

		intending(expectedWebIntent).respondWith(result);
	}

	@After
	public void tearDown() {
		PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
				.edit()
				.putInt(AGREED_TO_PRIVACY_POLICY_VERSION, bufferedPreferenceSetting)
				.commit();
		Intents.release();
	}

	@Test
	public void testOpenFromShareLinkIntent() {
		Intent shareUriIntent = new Intent();
		shareUriIntent.setData(shareUri);
		shareUriIntent.setAction("android.intent.action.VIEW");

		baseActivityTestRule.launchActivity(shareUriIntent);

		intended(expectedWebIntent);
	}
}
