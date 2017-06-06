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

import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.test.ActivityInstrumentationTestCase2;
import android.util.LayoutDirection;
import android.widget.Toast;

import com.robotium.solo.Solo;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.Multilingual;

import java.util.Locale;

/**
 * Created by Null on 3/27/2017.
 */

public class MultilingualTest extends ActivityInstrumentationTestCase2 <MainMenuActivity> {

	public MultilingualTest() {
		super(MainMenuActivity.class);
	}

	private Solo solo;

	@Override
	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	@Override
	protected void runTest() throws Throwable {
		super.runTest();
	}

	public void testArabicText() throws Exception {

		// make English as App_Language
		solo.sendKey(Solo.MENU);
		solo.clickInList(5);
		solo.clickInList(1);
		solo.assertCurrentActivity("Language", Multilingual.class);
		solo.sleep(2000);
		solo.scrollDown();
		solo.clickInList(4);
		solo.waitForActivity(MainMenuActivity.class);
		solo.assertCurrentActivity("MainMenu", MainMenuActivity.class);
		//
		String s0 = getActivity().getString(R.string.app_name);
		String s01 = getActivity().getString(R.string.programs);
		// make Arabic as App_Language
		solo.sendKey(Solo.MENU);
		solo.clickInList(5);
		solo.clickInList(1);
		solo.assertCurrentActivity("Language", Multilingual.class);
		solo.scrollDown();
		solo.scrollDown();
		solo.scrollDown();
		solo.scrollDown();
		solo.scrollDown();
		solo.scrollDown();
		solo.clickInList(4);
		solo.sleep(2000);
		solo.waitForActivity(MainMenuActivity.class);
		solo.assertCurrentActivity("MainMenu", MainMenuActivity.class);
		String s = getActivity().getString(R.string.app_name);
		assertEquals(s, "بوكت كوود");
		assertNotSame(s0, "s");
		String s1 = getActivity().getString(R.string.programs);
		assertEquals(s1, "البرامج");
		assertNotSame(s01, s1);
	}

	public void testArabic_LayoutDirection() throws Exception {
		solo.sleep(5000);
		int L = getActivity().getResources().getConfiguration().orientation;
		int L1 = LayoutDirection.RTL;
		assertEquals(L1, L);
	}
	public void testFarsiText() throws Exception {

		// make English as App_Language
		solo.sendKey(Solo.MENU);
		solo.clickInList(5);
		solo.clickInList(1);
		solo.assertCurrentActivity("Language", Multilingual.class);
		solo.sleep(2000);
		solo.scrollDown();
		solo.clickInList(4);
		solo.waitForActivity(MainMenuActivity.class);
		solo.assertCurrentActivity("MainMenu", MainMenuActivity.class);
		//
		String s0 = getActivity().getString(R.string.app_name);
		String s01 = getActivity().getString(R.string.programs);
		// make Farsi as App_Language
		solo.sendKey(Solo.MENU);
		solo.clickInList(5);
		solo.clickInList(1);
		solo.assertCurrentActivity("Language", Multilingual.class);
		solo.scrollDown();
		solo.scrollDown();
		solo.scrollDown();
		solo.scrollDown();
		solo.scrollDown();
		solo.scrollDown();
		solo.clickInList(6);
		solo.sleep(2000);
		solo.waitForActivity(MainMenuActivity.class);
		solo.assertCurrentActivity("MainMenu", MainMenuActivity.class);
		String s = getActivity().getString(R.string.app_name);
		assertEquals(s, "پاکت کد");
		assertNotSame(s0, "s");
		String s1 = getActivity().getString(R.string.programs);
		assertEquals(s1, "برنامه ها");
		assertNotSame(s01, s1);
	}
	public void testFarsi_LayoutDirection() throws Exception {
		solo.sleep(5000);
		int LD = getActivity().getResources().getConfiguration().orientation;
		int Ld = LayoutDirection.RTL;
		assertEquals(Ld, LD);
	}

	public void testGermanText() throws Exception {
		// make English as App_Language
		solo.sendKey(Solo.MENU);
		solo.clickInList(5);
		solo.clickInList(1);
		solo.assertCurrentActivity("Language", Multilingual.class);
		solo.sleep(2000);
		solo.scrollDown();
		solo.clickInList(4);
		solo.waitForActivity(MainMenuActivity.class);
		solo.assertCurrentActivity("MainMenu", MainMenuActivity.class);
		//
		String c = getActivity().getString(R.string.main_menu_continue);
		String p = getActivity().getString(R.string.programs);
		solo.sendKey(Solo.MENU);
		solo.clickInList(5);
		solo.clickInList(1);
		solo.assertCurrentActivity("Language", Multilingual.class);
		solo.clickInList(9);
		solo.sleep(2000);
		solo.waitForActivity(MainMenuActivity.class);
		solo.assertCurrentActivity("MainMenu", MainMenuActivity.class);
		String f = getActivity().getString(R.string.main_menu_continue);
		assertEquals(f, "Fortsetzen");
		assertNotSame(c, "s");
		String pp = getActivity().getString(R.string.programs);
		assertEquals(pp, "Programme");
		assertNotSame(p, pp);

	}
}
