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

package org.catrobat.catroid.uiespresso.ui.activity.rtl;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.matcher.PreferenceMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Locale;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.Constants.LANGUAGE_TAG_KEY;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResources;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResourcesString;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.core.StringStartsWith.startsWith;

@RunWith(AndroidJUnit4.class)
public class LanguageSwitchMainMenuTest {
	private IdlingResource idlingResource;
	private static final Locale ARABICLOCALE = new Locale("ar");
	private static final Locale DEUTSCHLOCALE = Locale.GERMAN;
	private Configuration conf = getResources().getConfiguration();
	private Locale defaultLocale = Locale.getDefault();

	@Rule
	public BaseActivityInstrumentationRule<SettingsActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SettingsActivity.class);

	@Before
	public void setUp() throws Exception {
		baseActivityTestRule.launchActivity(null);
	}

	@After
	public void tearDown() throws Exception {
		resetToDefaultLanguage();
		Espresso.unregisterIdlingResources(idlingResource);
	}

	private void resetToDefaultLanguage() {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
				.edit();
		editor.putString(LANGUAGE_TAG_KEY, defaultLocale.getLanguage());
		editor.commit();
		SettingsActivity.updateLocale(InstrumentationRegistry.getTargetContext(), defaultLocale.getLanguage(),
				defaultLocale.getCountry());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Test
	public void testChangeLanguageToArabic() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_language))
				.perform(click());
		onData(hasToString(startsWith(ARABICLOCALE.getDisplayName(ARABICLOCALE))))
				.perform(click());
		MainMenuActivity mainMenuActivity = (MainMenuActivity) UiTestUtils.getCurrentActivity();
		idlingResource = mainMenuActivity.getIdlingResource();
		Espresso.registerIdlingResources(idlingResource);

		assertEquals(Locale.getDefault().getDisplayLanguage(), ARABICLOCALE.getDisplayLanguage());
		assertTrue(RtlUiTestUtils.checkTextDirection(Locale.getDefault().getDisplayName()));
		assertEquals(View.LAYOUT_DIRECTION_RTL, conf.getLayoutDirection());
		String buttonContinueName = getResourcesString(R.string.main_menu_continue);
		onView(withId(R.id.main_menu_button_continue))
				.check(matches(withText(containsString(buttonContinueName))));
		onView(withId(R.id.main_menu_button_new))
				.check(matches(withText(R.string.main_menu_new)));
		onView(withId(R.id.main_menu_button_programs))
				.check(matches(withText(R.string.main_menu_programs)));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testChangeLanguageToDeutsch() {
		onData(PreferenceMatchers.withTitle(R.string.preference_title_language))
				.perform(click());
		onData(hasToString(startsWith(DEUTSCHLOCALE.getDisplayName(DEUTSCHLOCALE))))
				.perform(click());
		MainMenuActivity mainMenuActivity = (MainMenuActivity) UiTestUtils.getCurrentActivity();
		idlingResource = mainMenuActivity.getIdlingResource();
		Espresso.registerIdlingResources(idlingResource);

		assertEquals(Locale.getDefault().getDisplayLanguage(), DEUTSCHLOCALE
				.getDisplayLanguage());
		String buttonContinueName = getResourcesString(R.string.main_menu_continue);
		onView(withId(R.id.main_menu_button_continue))
				.check(matches(withText(containsString(buttonContinueName))));
		onView(withId(R.id.main_menu_button_new))
				.check(matches(withText(R.string.main_menu_new)));
		onView(withId(R.id.main_menu_button_programs))
				.check(matches(withText(R.string.main_menu_programs)));
	}
}
