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

package org.catrobat.catroid.uiespresso.intents.loginfragment;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.SignInActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class LoginDialogFragmentTest {

	@Rule
	public ActivityScenarioRule<SignInActivity> activityRule =
			new ActivityScenarioRule(SignInActivity.class);

	@Before
	public void openLoginDialog() {
		onView(withText("Login"))
				.perform(click());
	}

	@Test
	public void cancelButtonTest() {
		closeSoftKeyboard();
		onView(withText("Cancel")).perform(click());
		onView(withId(R.id.dialog_login_username)).check(doesNotExist());
		onView(withId(R.id.dialog_login_password)).check(doesNotExist());
		onView(withText("Cancel")).check(doesNotExist());
	}
}
