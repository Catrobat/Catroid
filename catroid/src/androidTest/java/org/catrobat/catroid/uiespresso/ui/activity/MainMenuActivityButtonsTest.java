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
import android.support.test.espresso.IdlingResource;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainMenuActivityButtonsTest {
	private IdlingResource mainMenuIdlingResource;

	@Rule
	public BaseActivityInstrumentationRule<MainMenuActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(MainMenuActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		baseActivityTestRule.launchActivity(null);
		mainMenuIdlingResource = baseActivityTestRule.getActivity().getIdlingResource();
		registerIdlingResources(mainMenuIdlingResource);
	}

	@Test
	public void buttonContinueOpensProjectActivity() {

		onView(withId(R.id.main_menu_button_continue))
				.perform(click());

		onView(withId(R.id.fragment_sprites_list))
				.check(matches(isDisplayed()));
	}

	@Test
	public void buttonNewOpensDialog() {
		onView(withId(R.id.main_menu_button_new))
				.perform(click());

		onView(withText(R.string.new_project_dialog_title))
				.check(matches(isDisplayed()));
	}

	@Test
	public void buttonProgramsOpensProgramsList() {
		onView(withId(R.id.main_menu_button_programs))
				.perform(click());

		onView(withText(R.string.programs))
				.check(matches(isDisplayed()));
	}

	@Test
	public void buttonHelpWithConnectionCallsOpenHelpSite() {
		onView(withId(R.id.main_menu_button_help))
				.perform(click());

		Activity activity = UiTestUtils.getCurrentActivity();
		assertTrue(isWebViewActivity(activity));
		String url = ((WebViewActivity)activity).getUrl();
		assertTrue(url.contains(Constants.CATROBAT_HELP_URL));

		pressBack();
	}

	@Test
	public void buttonWebWithConnectionCallsOpenWebSite() {
		onView(withId(R.id.main_menu_button_web))
				.perform(click());

		Activity activity = UiTestUtils.getCurrentActivity();
		assertTrue(isWebViewActivity(activity));
		String url = ((WebViewActivity)activity).getUrl();
		assertTrue(url.contains(Constants.BASE_URL_HTTPS));

		pressBack();
	}

	@Test
	public void buttonUploadWithConnectionOpensLoginDialog() {
		onView(withId(R.id.main_menu_button_upload))
				.perform(click());

		onView(withId(R.id.dialog_sign_in_login))
				.check(matches(isDisplayed()));

		pressBack();
	}

	@After
	public void tearDown() {
		unregisterIdlingResources(mainMenuIdlingResource);
	}

	private boolean isWebViewActivity(Activity activity) {
		return activity != null && activity instanceof WebViewActivity;
	}
}
