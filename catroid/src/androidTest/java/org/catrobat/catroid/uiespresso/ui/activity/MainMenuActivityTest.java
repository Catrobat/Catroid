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
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.cloudmessaging.CloudMessage;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;

import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class MainMenuActivityTest {

	@Rule
	public ActivityTestRule<MainMenuActivity> activityTestRule = new ActivityTestRule<>(MainMenuActivity.class, true, false);

	private static final String EXPECTED_URL = "https://www.catrobat.org";

	@Test
	public void testBrowserOpensIntendedUrl() {
		Intents.init();
		Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_VIEW), hasData(EXPECTED_URL));
		intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
		Intent intent = new Intent();
		intent.putExtra(CloudMessage.WEB_PAGE_URL, EXPECTED_URL);
		activityTestRule.launchActivity(intent);
		intended(expectedIntent);
		Intents.release();
	}
}
