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

package org.catrobat.catroid.uiespresso.intents.loginfragment;

import org.catrobat.catroid.R;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SignInActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {

	@Rule
	public ActivityScenarioRule<SignInActivity> activityRule =
			new ActivityScenarioRule<>(SignInActivity.class);

	@Test
	@Category({Cat.AppUi.class, Level.Smoke.class})
	public void testGoogleSignInShouldComeBeforeLoginAndRegister() {
		onView(withId(R.id.sign_in_google_login_button)).check(matches(isDisplayed()));
		onView(withId(R.id.sign_in_or)).check(matches(isDisplayed()));
		onView(withId(R.id.sign_in_login)).check(matches(isDisplayed()));
		onView(withId(R.id.sign_in_register)).check(matches(isDisplayed()));
		onView(withId(R.id.sign_in_google_login_button)).check(isCompletelyAbove(withId(R.id.sign_in_or)));
		onView(withId(R.id.sign_in_or)).check(isCompletelyAbove(withId(R.id.sign_in_login)));
		onView(withId(R.id.sign_in_login)).check(isCompletelyAbove(withId(R.id.sign_in_register)));
	}
}
