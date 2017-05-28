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

package org.catrobat.catroid.test.ui;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.test.ActivityInstrumentationTestCase2;
import android.util.LayoutDirection;

import com.robotium.solo.Solo;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.Multilingual;
import org.catrobat.catroid.ui.SettingsActivity;

import java.util.Locale;

public class MultilingualTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	public MultilingualTest() {
		super(MainMenuActivity.class);
	}

	private static Solo solo;
	private static final int LTR = LayoutDirection.LTR;
	private static final int RTL = LayoutDirection.RTL;
	private static final Locale ArabicLocale = new Locale("ar");
	private static final Locale UrduLocale = new Locale("ur");
	private static final Locale FarsiLocale = new Locale("fa");
	private static final Locale DeutschLocale = Locale.GERMAN;
	private static final String DEUTSCH = "Deutsch";
	private static final String ARABIC = "العربية";
	private static final String URDU = "اردو";
	private static final String FARSI = "فارسی";
	// Arabic Strings
	private static final String APP_NAME_ARABIC = "بوكت كوود";
	private static final String BUTTON_CONTINUE_NAME_ARABIC = "متابعة";
	private static final String BUTTON_NEW_NAME_ARABIC = "جديد";
	private static final String BUTTON_PROGRAMS_NAME_ARABIC = "البرامج";
	// Urdu Strings
	private static final String APP_NAME_URDU = "پاکٹ کوڈ";
	private static final String BUTTON_CONTINUE_NAME_URDU = "جاری رکھیں";
	private static final String BUTTON_NEW_NAME_URDU = "نیا";
	private static final String BUTTON_PROGRAMS_NAME_URDU = "پروگرامات";
	// Farsi Strings
	private static final String APP_NAME_FARSI = "پاکت کد";
	private static final String BUTTON_CONTINUE_NAME_FARSI = "ادامه";
	private static final String BUTTON_NEW_NAME_FARSI = "جدید";
	private static final String BUTTON_PROGRAMS_NAME_FARSI = "برنامه ها";
	// Deutsch Strings
	private static final String BUTTON_CONTINUE_NAME_Deutsch = "Fortsetzen";
	private static final String BUTTON_NEW_NAME_Deutsch = "Neu";
	private static final String BUTTON_PROGRAMS_NAME_Deutsch = "Programme";


	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	public void testChangeLanguageToArabic() throws Exception {
		gotoMultilingualActivity();
		solo.searchText(ARABIC);
		solo.clickOnText(ARABIC);
		solo.sleep(1000);
		assertTrue(isRTL());
		int CurrentDirection = getActivity().getResources().getConfiguration().getLayoutDirection();
		Locale CurrentLocale = Locale.getDefault();
		assertEquals("Current LayoutDirection is not RTL", CurrentDirection, RTL);
		assertEquals("Current Locale is not Arabic", CurrentLocale, ArabicLocale);
		String APP_NAME = solo.getString(R.string.app_name);
		String BUTTON_PROGRAMS_NAME = solo.getString(R.string.main_menu_programs);
		String BUTTON_CONTINUE_NAME = solo.getString(R.string.main_menu_continue);
		String BUTTON_NEW_NAME = solo.getString(R.string.main_menu_new);
		assertEquals(" Hey *_* there is a mistake", APP_NAME, APP_NAME_ARABIC);
		assertEquals("Hey *_* there is a mistake", BUTTON_CONTINUE_NAME_ARABIC, BUTTON_CONTINUE_NAME);
		assertEquals("Hey *_* there is a mistake", BUTTON_NEW_NAME_ARABIC, BUTTON_NEW_NAME);
		assertEquals("Hey *_* there is a mistake", BUTTON_PROGRAMS_NAME_ARABIC, BUTTON_PROGRAMS_NAME);
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	public void testChangeLanguageToUrdu() throws Exception {
		gotoMultilingualActivity();
		solo.searchText(URDU);
		solo.clickOnText(URDU);
		solo.sleep(1000);
		assertTrue(isRTL());
		int CurrentDirection = getActivity().getResources().getConfiguration().getLayoutDirection();
		Locale CurrentLocale = Locale.getDefault();
		assertEquals("Current LayoutDirection is not RTL", CurrentDirection, RTL);
		assertEquals("Current Locale is not Urdu", CurrentLocale, UrduLocale);
		String APP_NAME = solo.getString(R.string.app_name);
		String BUTTON_PROGRAMS_NAME = solo.getString(R.string.main_menu_programs);
		String BUTTON_CONTINUE_NAME = solo.getString(R.string.main_menu_continue);
		String BUTTON_NEW_NAME = solo.getString(R.string.main_menu_new);
		assertEquals(" Hey *_* there is a mistake", APP_NAME, APP_NAME_URDU);
		assertEquals("Hey *_* there is a mistake", BUTTON_CONTINUE_NAME_URDU, BUTTON_CONTINUE_NAME);
		assertEquals("Hey *_* there is a mistake", BUTTON_NEW_NAME_URDU, BUTTON_NEW_NAME);
		assertEquals("Hey *_* there is a mistake", BUTTON_PROGRAMS_NAME_URDU, BUTTON_PROGRAMS_NAME);
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	public void testChangeLanguageToFarsi() throws Exception {
		gotoMultilingualActivity();
		solo.searchText(FARSI);
		solo.clickOnText(FARSI);
		solo.sleep(1000);
		assertTrue(isRTL());
		int CurrentDirection = getActivity().getResources().getConfiguration().getLayoutDirection();
		Locale CurrentLocale = Locale.getDefault();
		assertEquals("Current LayoutDirection is not RTL", CurrentDirection, RTL);
		assertEquals("Current Locale is not Farsi", CurrentLocale, FarsiLocale);
		String APP_NAME = solo.getString(R.string.app_name);
		String BUTTON_PROGRAMS_NAME = solo.getString(R.string.main_menu_programs);
		String BUTTON_CONTINUE_NAME = solo.getString(R.string.main_menu_continue);
		String BUTTON_NEW_NAME = solo.getString(R.string.main_menu_new);
		assertEquals(" Hey *_* there is a mistake", APP_NAME, APP_NAME_FARSI);
		assertEquals("Hey *_* there is a mistake", BUTTON_CONTINUE_NAME_FARSI, BUTTON_CONTINUE_NAME);
		assertEquals("Hey *_* there is a mistake", BUTTON_NEW_NAME_FARSI, BUTTON_NEW_NAME);
		assertEquals("Hey *_* there is a mistake", BUTTON_PROGRAMS_NAME_FARSI, BUTTON_PROGRAMS_NAME);
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	public void testChangeLanguageToDeutsch() throws Exception {
		gotoMultilingualActivity();
		solo.searchText(DEUTSCH);
		solo.clickOnText(DEUTSCH);
		solo.sleep(1000);
		assertFalse(isRTL());
		int CurrentDirection = getActivity().getResources().getConfiguration().getLayoutDirection();
		Locale CurrentLocale = Locale.getDefault();
		assertEquals("Current LayoutDirection is not LTR", CurrentDirection, LTR);
		assertEquals("Current Locale is not Deutsch", CurrentLocale, DeutschLocale);
		String BUTTON_Programs_NAME = solo.getString(R.string.main_menu_programs);
		String BUTTON_CONTINUE_NAME = solo.getString(R.string.main_menu_continue);
		String BUTTON_NEW_NAME = solo.getString(R.string.main_menu_new);
		assertEquals(" Hey *_* there is a mistake", BUTTON_Programs_NAME, BUTTON_PROGRAMS_NAME_Deutsch);
		assertEquals("Hey *_* there is a mistake", BUTTON_CONTINUE_NAME, BUTTON_CONTINUE_NAME_Deutsch);
		assertEquals("Hey *_* there is a mistake", BUTTON_NEW_NAME, BUTTON_NEW_NAME_Deutsch);
	}

	private void gotoMultilingualActivity() {
		solo.assertCurrentActivity("Current Activity is not MainMenuActivity ", MainMenuActivity.class);
		solo.sleep(500);
		solo.sendKey(solo.MENU);
		solo.clickOnMenuItem(solo.getString(R.string.settings));
		solo.assertCurrentActivity("Current Activity is not SettingsActivity", SettingsActivity.class);
		solo.sleep(500);
		solo.clickOnText(solo.getString(R.string.preference_title_language));
		solo.assertCurrentActivity("Current Activity is not Multilingual", Multilingual.class);
		solo.sleep(500);
	}

	public static boolean isRTL() {
		return isRTL(Locale.getDefault());
	}

	public static boolean isRTL(Locale locale) {
		final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
		return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
				directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
	}
}
