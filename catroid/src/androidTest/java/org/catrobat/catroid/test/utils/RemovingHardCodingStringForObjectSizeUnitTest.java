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
import org.catrobat.catroid.ui.MyProjectsActivity;

import java.util.Locale;

public class RemovingHardCodingStringForObjectSizeUnitTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	public RemovingHardCodingStringForObjectSizeUnitTest() {
		super(MainMenuActivity.class);
	}

	private static Solo solo;
	private static final String BUTTON_NAME_PROGRAMS = "البرامج";
	private static final String MENU_ITEM_ShowDetails = "إظهار التفاصيل";
	private static final String KiloByte_Text = "كيلوبايت";

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	// Make sure that your PhoneLanguage is Arabic
	// tested on SAMSUNG GALAXY S5
	public void testHardCodingStringForKB_MB_GB_B() throws Exception {
		solo.assertCurrentActivity("MainMenuActivity is not the Current Activity", MainMenuActivity.class);
		solo.sleep(1000);
		assertTrue(isRTL());
		solo.clickOnButton(BUTTON_NAME_PROGRAMS);
		solo.assertCurrentActivity("MyProjectActivity is not the Current Activity", MyProjectsActivity.class);
		solo.sleep(1000);
		solo.sendKey(Solo.MENU);
		solo.sleep(500);
		solo.clickOnText(MENU_ITEM_ShowDetails);
		String kilobyteStr = getActivity().getString(R.string.kiloByte_short);
		assertEquals(kilobyteStr, KiloByte_Text);
	}

	private static boolean isRTL() {
		return isRTL(Locale.getDefault());
	}

	private static boolean isRTL(Locale locale) {
		final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
		return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
				directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
	}
}
