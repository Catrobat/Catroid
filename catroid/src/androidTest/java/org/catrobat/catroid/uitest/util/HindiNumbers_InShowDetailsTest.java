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

package org.catrobat.catroid.uitest.util;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;

import java.util.Arrays;

//Make sure that your PhoneLanguage is Arabic!!
public class HindiNumbers_InShowDetailsTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	public HindiNumbers_InShowDetailsTest() {
		super(MainMenuActivity.class);
	}

	private Solo solo;
	private  String SHOW_DETAILS = "إظهار التفاصيل";
	private String[] HindiNumber = { "٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩", "٫" };

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	public void testHindiNumbers_ShowDetails() throws Exception {
		solo.assertCurrentActivity("Current Activity is not MainMenuActivity", MainMenuActivity.class);
		solo.sleep(500);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.sleep(500);
		solo.sendKey(Solo.MENU);
		solo.clickOnText(SHOW_DETAILS);
		solo.sleep(1200);
		TextView SIZE = (TextView) solo.getView(R.id.details_right_bottom);
		assertTrue(SIZE.isShown());
		assertTrue(SIZE.getVisibility() == View.VISIBLE);
		String Size = String.valueOf(SIZE.getText());
		assertTrue(Arrays.asList(HindiNumber).contains(String.valueOf(Size.charAt(0))));
		assertTrue(Arrays.asList(HindiNumber).contains(String.valueOf(Size.charAt(1))));
		assertTrue(Arrays.asList(HindiNumber).contains(String.valueOf(Size.charAt(2))));
		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.sleep(1000);
		solo.sendKey(Solo.MENU);
		if (solo.searchText(SHOW_DETAILS)) {
			solo.clickOnText(SHOW_DETAILS);
			searchHindiNumbers();
		} else {
			solo.goBack();
			searchHindiNumbers();
		}
		solo.clickOnText(solo.getString(R.string.background));
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.sendKey(Solo.MENU);
		if (solo.searchText(SHOW_DETAILS)) {
			solo.clickOnText(SHOW_DETAILS);
			searchHindiNumbers();
		} else {
			solo.goBack();
			searchHindiNumbers();
		}
	}

	private void searchHindiNumbers() {
		String Hn = "";
		switch (Hn) {
			case "٠":
				break;
			case "١":
				break;
			case "٢":
				break;
			case "٣":
				break;
			case "٤":
				break;
			case "٥":
				break;
			case "٦":
				break;
			case "٧":
				break;
			case "٨":
				break;
			case "٩":
				break;
			case "٫":
				break;
		}
		solo.searchText(Hn);
	}
}
