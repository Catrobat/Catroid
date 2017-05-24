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
package org.catrobat.catroid.uiespresso.ui.dialog;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class TermsOfUseDialogTest {
	private IdlingResource idlingResource;

	@Rule
	public BaseActivityInstrumentationRule<MainMenuActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(MainMenuActivity.class);

	public void registerIdlingResource() {
		idlingResource = baseActivityTestRule.getActivity().getIdlingResource();
		Espresso.registerIdlingResources(idlingResource);
	}

	@Before
	public void setUp() throws Exception {
		baseActivityTestRule.launchActivity(null);
		registerIdlingResource();
	}

	@Test
	public void termsOfUseDialogTest() {
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
		onView(withText(R.string.main_menu_terms_of_use)).perform(click());

		onView(withText(R.string.dialog_terms_of_use_title))
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_terms_of_use_info))
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_terms_of_use_link_text))
				.check(matches(isDisplayed()));

		onView(withText(R.string.ok))
				.perform(click());

		onView(withText(R.string.dialog_terms_of_use_title))
				.check(doesNotExist());
	}

	@After
	public void tearDown() throws Exception {
		unregisterResource();
	}

	public void unregisterResource() {
		Espresso.unregisterIdlingResources(idlingResource);
	}
}
