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

package org.catrobat.catroid.test.utils;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.Multilingual;
import org.catrobat.catroid.ui.MyProjectsActivity;

/**
 * Created by Null on 3/28/2017.
 */

public class RemovingHardCodingStringForObjectSizeUnit extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	public RemovingHardCodingStringForObjectSizeUnit() {
		super(MainMenuActivity.class);
	}

	private static Solo solo;
	private static String ArabicLanguage = "العربية";
	private static String BUTTON_NAME_PROGRAMS = "البرامج";
	private static String MENU_ITEM_ShowDetails = "إظهار التفاصيل";
	private static String Byte_Text = "بايت";
	private static String KiloByte_Text = "كيلوبايت";
	private static String MegaByte_Text = "ميغابايت";
	private static String GigaByte_Text = "غيغابايت";

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

	public void testHardCodingStringForKB_MB_GB_B() throws Exception {
		solo.assertCurrentActivity("MainMenu", MainMenuActivity.class);
		solo.sendKey(solo.MENU);
		solo.clickInList(5);
		solo.clickInList(1);
		solo.sleep(3000);
		solo.assertCurrentActivity("Language", Multilingual.class);
		solo.scrollDown();
		solo.clickInList(4);
		solo.sleep(3000);
		solo.assertCurrentActivity("MainMenu", MainMenuActivity.class);
		solo.sleep(2000);
		solo.clickOnButton("Programs");
		solo.assertCurrentActivity("Programs", MyProjectsActivity.class);
		solo.sleep(2000);
		solo.sendKey(solo.MENU);
		solo.sleep(1000);
		solo.clickOnMenuItem("Show details");
		solo.sleep(5000);
		assertTrue(solo.searchText("KB", 1, true));
		//
		solo.goBack();
		solo.assertCurrentActivity("MainMenu", MainMenuActivity.class);
		solo.sendKey(solo.MENU);
		solo.clickInList(5);
		solo.clickInList(1);
		solo.sleep(3000);
		solo.assertCurrentActivity("Language", Multilingual.class);
		solo.searchText(ArabicLanguage);
		solo.clickOnText(ArabicLanguage);
		//
		solo.assertCurrentActivity("MainMenu", MainMenuActivity.class);
		solo.sleep(2000);
		solo.clickOnButton(BUTTON_NAME_PROGRAMS);
		solo.assertCurrentActivity("Programs", MyProjectsActivity.class);
		solo.sleep(2000);
		solo.sendKey(solo.MENU);
		solo.sleep(1000);
		solo.clickOnMenuItem(MENU_ITEM_ShowDetails);
		solo.sleep(5000);
		assertTrue(solo.searchText("كيلوبايت", 1, true));
		String byteStr = getActivity().getString(R.string.Byte_short);
		String kilobyteStr = getActivity().getString(R.string.kiloByte_short);
		String megabyteStr = getActivity().getString(R.string.MegaByte_short);
		String gigabyteStr = getActivity().getString(R.string.GigaByte_short);
		assertEquals(byteStr, Byte_Text);
		assertEquals(kilobyteStr, KiloByte_Text);
		assertEquals(megabyteStr, MegaByte_Text);
		assertEquals(gigabyteStr, GigaByte_Text);
	}
}


