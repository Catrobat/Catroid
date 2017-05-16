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
package org.catrobat.catroid.uiespresso.localization;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.catrobat.catroid.R;

import org.catrobat.catroid.ui.MainMenuActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.assertion.LayoutAssertions.noEllipsizedText;
import static android.support.test.espresso.assertion.LayoutAssertions.noOverlaps;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.catroid.uiespresso.localization.assertions.LayoutDirectionAssertions.isLayoutDirectionRTL;
import static org.catrobat.catroid.uiespresso.localization.assertions.TextDirectionAssertions.isTextDirectionRTL;
import static org.catrobat.catroid.uiespresso.localization.assertions.VisibilityAssertions.isVisible;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)

public class MainMenuActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private MainMenuActivity mainMenuActivity;
	private ViewInteraction mainMenuContinue;
	private ViewInteraction mainMenuNew;
	private ViewInteraction mainMenuPrograms;
	private ViewInteraction mainMenuHelp;
	private ViewInteraction mainMenuWeb;
	private ViewInteraction mainMenuUpload;

	public MainMenuActivityTest() {
		super(MainMenuActivity.class);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		injectInstrumentation(InstrumentationRegistry.getInstrumentation());
		mainMenuActivity = getActivity();
		mainMenuContinue = Espresso.onView(withId(R.id.main_menu_button_continue));
		mainMenuNew = Espresso.onView(withId(R.id.main_menu_button_new));
		mainMenuPrograms = Espresso.onView(withId(R.id.main_menu_button_programs));
		mainMenuHelp = Espresso.onView(withId(R.id.main_menu_button_help));
		mainMenuWeb = Espresso.onView(withId(R.id.main_menu_button_web));
		mainMenuUpload = Espresso.onView(withId(R.id.main_menu_button_upload));
	}

	@Test
	public void assertNoEllipsizedTextInRTLMode() {
		mainMenuContinue.check(noEllipsizedText());
		mainMenuNew.check(noEllipsizedText());
		mainMenuPrograms.check(noEllipsizedText());
		mainMenuHelp.check(noEllipsizedText());
		mainMenuWeb.check(noEllipsizedText());
		mainMenuUpload.check(noEllipsizedText());
	}

	@Test
	public void assertCompletelyDisplayed() {
		mainMenuContinue.check(matches(isCompletelyDisplayed()));
		mainMenuNew.check(matches(isCompletelyDisplayed()));
		mainMenuPrograms.check(matches(isCompletelyDisplayed()));
		mainMenuHelp.check(matches(isCompletelyDisplayed()));
		mainMenuWeb.check(matches(isCompletelyDisplayed()));
		mainMenuUpload.check(matches(isCompletelyDisplayed()));
	}

	@Test
	public void assertNotNullValueInRTLMode() {
		mainMenuContinue.check(matches(notNullValue()));
		mainMenuNew.check(matches(notNullValue()));
		mainMenuPrograms.check(matches(notNullValue()));
		mainMenuHelp.check(matches(notNullValue()));
		mainMenuWeb.check(matches(notNullValue()));
		mainMenuUpload.check(matches(notNullValue()));
	}

	@Test
	public void assertNoOverLappingBricksInRTLMode() {
		mainMenuContinue.check(noOverlaps());
		mainMenuNew.check(noOverlaps());
		mainMenuPrograms.check(noOverlaps());
		mainMenuHelp.check(noOverlaps());
		mainMenuWeb.check(noOverlaps());
		mainMenuUpload.check(noOverlaps());
	}

	@Test
	public void assertLayoutDirectionIsRTL() {
		mainMenuContinue.check(isLayoutDirectionRTL());
		mainMenuNew.check(isLayoutDirectionRTL());
		mainMenuPrograms.check(isLayoutDirectionRTL());
		mainMenuHelp.check(isLayoutDirectionRTL());
		mainMenuWeb.check(isLayoutDirectionRTL());
		mainMenuUpload.check(isLayoutDirectionRTL());
	}

	@Test
	public void assertTextDirectionIsRTL() {
		mainMenuContinue.check(isTextDirectionRTL());
		mainMenuNew.check(isTextDirectionRTL());
		mainMenuPrograms.check(isTextDirectionRTL());
		mainMenuHelp.check(isTextDirectionRTL());
		mainMenuWeb.check(isTextDirectionRTL());
		mainMenuUpload.check(isTextDirectionRTL());
	}

	@Test
	public void assertIsVisibleBrickInRTL() {
		mainMenuContinue.check(isVisible());
		mainMenuNew.check(isVisible());
		mainMenuPrograms.check(isVisible());
		mainMenuHelp.check(isVisible());
		mainMenuWeb.check(isVisible());
		mainMenuUpload.check(isVisible());
	}
}