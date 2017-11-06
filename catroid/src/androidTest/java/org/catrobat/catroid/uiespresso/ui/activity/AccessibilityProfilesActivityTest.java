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

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.AccessibilityProfilesActivity;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;

@RunWith(AndroidJUnit4.class)
public class AccessibilityProfilesActivityTest {

	@Rule
	public BaseActivityInstrumentationRule<AccessibilityProfilesActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(AccessibilityProfilesActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void switchProfileTest() {
		Intents.init();
		Intent intentResult = new Intent();
		Instrumentation.ActivityResult activityResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, intentResult);
		intending(anyIntent()).respondWith(activityResult);

		onView(withId(R.id.access_title_profilestandard))
				.perform(scrollTo(), click());
		intended(allOf(hasExtra(equalTo(AccessibilityProfilesActivity.PROFILE_ID), equalTo(R.id.access_profilestandard))));

		onView(withId(R.id.access_title_profilemyprofile))
				.perform(scrollTo(), click());
		intended(allOf(hasExtra(equalTo(AccessibilityProfilesActivity.PROFILE_ID), equalTo(R.id.access_profilemyprofile))));

		onView(withId(R.id.access_title_profile1))
				.perform(scrollTo(), click());
		intended(allOf(hasExtra(equalTo(AccessibilityProfilesActivity.PROFILE_ID), equalTo(R.id.access_profile1))));

		onView(withId(R.id.access_title_profile2))
				.perform(scrollTo(), click());
		intended(allOf(hasExtra(equalTo(AccessibilityProfilesActivity.PROFILE_ID), equalTo(R.id.access_profile2))));

		onView(withId(R.id.access_title_profile3))
				.perform(scrollTo(), click());
		intended(allOf(hasExtra(equalTo(AccessibilityProfilesActivity.PROFILE_ID), equalTo(R.id.access_profile2))));

		onView(withId(R.id.access_title_profile4))
				.perform(scrollTo(), click());
		intended(allOf(hasExtra(equalTo(AccessibilityProfilesActivity.PROFILE_ID), equalTo(R.id.access_profile4))));
		Intents.release();
	}
}
