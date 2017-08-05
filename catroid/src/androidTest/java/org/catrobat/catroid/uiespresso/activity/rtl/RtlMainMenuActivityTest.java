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

package org.catrobat.catroid.uiespresso.activity.rtl;

import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.uiespresso.activity.rtl.RtlUiTestUtils.checkTextDirection;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResources;
import static org.catrobat.catroid.uiespresso.util.matchers.rtl.RtlTextDirection.isTextRtl;
import static org.catrobat.catroid.uiespresso.util.matchers.rtl.RtlViewDirection.isViewRtl;

@RunWith(AndroidJUnit4.class)
public class RtlMainMenuActivityTest {
	private Locale arabicLocale = new Locale("ar");
	private Configuration config = getResources().getConfiguration();
	private IdlingResource idlingResource;
	@Rule
	public BaseActivityInstrumentationRule<MainMenuActivity> baseActivityTestRule = new BaseActivityInstrumentationRule<>(MainMenuActivity.class,
			true, false);

	@Before
	public void setUp() throws Exception {
		baseActivityTestRule.launchActivity(null);
		idlingResource = baseActivityTestRule.getActivity().getIdlingResource();
		Espresso.registerIdlingResources(idlingResource);
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Test
	public void assertLayoutAndViewsAndTextsAreRtl() throws Exception {
		assertEquals(View.LAYOUT_DIRECTION_RTL, config.getLayoutDirection());
		assertTrue(checkTextDirection(Locale.getDefault().getDisplayName()));
		assertEquals(arabicLocale.getDisplayLanguage(), Locale.getDefault().getDisplayLanguage());

		onView(withId(R.id.main_menu_button_continue))
				.check(matches(isDisplayed()));
		onView(withId(R.id.main_menu_button_new))
				.check(matches(isDisplayed()));
		onView(withId(R.id.main_menu_button_programs))
				.check(matches(isDisplayed()));
		onView(withId(R.id.main_menu_button_help))
				.check(matches(isDisplayed()));
		onView(withId(R.id.main_menu_button_web))
				.check(matches(isDisplayed()));
		onView(withId(R.id.main_menu_button_upload))
				.check(matches(isDisplayed()));

		onView(withId(R.id.main_menu_button_continue))
				.check(matches(isViewRtl(baseActivityTestRule)));
		onView(withId(R.id.main_menu_button_new))
				.check(matches(isViewRtl(baseActivityTestRule)));
		onView(withId(R.id.main_menu_button_programs))
				.check(matches(isViewRtl(baseActivityTestRule)));
		onView(withId(R.id.main_menu_button_help))
				.check(matches(isViewRtl(baseActivityTestRule)));
		onView(withId(R.id.main_menu_button_web))
				.check(matches(isViewRtl(baseActivityTestRule)));
		onView(withId(R.id.main_menu_button_upload))
				.check(matches(isViewRtl(baseActivityTestRule)));

		onView(withText(R.string.main_menu_new))
				.check(matches(isTextRtl()));
		onView(withText(R.string.main_menu_programs))
				.check(matches(isTextRtl()));
		onView(withText(R.string.main_menu_help))
				.check(matches(isTextRtl()));
		onView(withText(R.string.main_menu_web))
				.check(matches(isTextRtl()));
		onView(withText(R.string.main_menu_upload))
				.check(matches(isTextRtl()));
	}
}
