/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content;

import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

public class BrickClickOnEditTextTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private static final String KEY_SETTINGS_MINDSTORM_BRICKS = "setting_mindstorm_bricks";

	public BrickClickOnEditTextTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		getIntoActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		// workaround to disable mindstorm settings
		// should be disabled no matter if test failed or succeeded
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		// disable mindstorm bricks, if enabled at start
		if (sharedPreferences.getBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false)) {
			sharedPreferences.edit().putBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false).commit();
		}

		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testIfEditTextAreVisibleAndClickOnTextSetXandYInAddBrickDialog() {
		String categoryMotionText = solo.getString(R.string.category_motion);

		int categoryStringId = 0;
		float screenWidth = 0;
		float getTextViewXPosition = 0;

		UiTestUtils.clickOnBottomBar(solo, R.id.btn_add);
		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_set_x);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(categoryMotionText);
		ArrayList<Integer> listOfYPosition = UiTestUtils.getListItemYPositions(solo);
		screenWidth = solo.getCurrentActivity().getResources().getDisplayMetrics().widthPixels;

		getTextViewXPosition = (float) ((screenWidth / 2.0) * 0.75);
		solo.clickOnScreen(getTextViewXPosition, listOfYPosition.get(1));
		solo.clickOnScreen(200, 200);

		List<Brick> brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("One Brick should be in bricklist", 1, brickListToCheck.size());
		assertTrue("Set brick should be instance of SetXBrick", brickListToCheck.get(0) instanceof SetXBrick);

		UiTestUtils.clickOnBottomBar(solo, R.id.btn_add);
		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_set_y);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(categoryMotionText);
		listOfYPosition = UiTestUtils.getListItemYPositions(solo);
		screenWidth = solo.getCurrentActivity().getResources().getDisplayMetrics().widthPixels;
		getTextViewXPosition = (float) ((screenWidth / 2.0) * 0.75);

		solo.clickOnScreen(getTextViewXPosition, listOfYPosition.get(2));
		solo.clickOnScreen(200, 200);

		brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("One Brick should be in bricklist", 2, brickListToCheck.size());
		assertTrue("Set brick should be instance of SetYBrick", brickListToCheck.get(0) instanceof SetYBrick);
		assertTrue("Set brick should be instance of SetXBrick", brickListToCheck.get(1) instanceof SetXBrick);

		UiTestUtils.clickOnBottomBar(solo, R.id.btn_add);
		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_set_x);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(categoryMotionText);

		ArrayList<EditText> editTextList = solo.getCurrentEditTexts();
		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollDown();
		editTextList = solo.getCurrentEditTexts();
		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollUp();
		solo.goBack();
		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_set_size_to);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(solo.getString(R.string.category_looks));

		editTextList = solo.getCurrentEditTexts();
		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollDown();
		editTextList = solo.getCurrentEditTexts();
		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollUp();
		solo.goBack();
		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_stop_all_sounds);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(solo.getString(R.string.category_sound));

		editTextList = solo.getCurrentEditTexts();

		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollDown();
		editTextList = solo.getCurrentEditTexts();
		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollUp();
		solo.goBack();
		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_when_started);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(solo.getString(R.string.category_control));

		editTextList = solo.getCurrentEditTexts();

		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollDown();
		editTextList = solo.getCurrentEditTexts();
		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollUp();
		solo.goBack();
		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_motor_action);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(solo.getString(R.string.category_lego_nxt));

		editTextList = solo.getCurrentEditTexts();

		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollDown();
		editTextList = solo.getCurrentEditTexts();
		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
		}

		solo.scrollUp();
		solo.goBack();
	}

	private void getIntoActivity() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		// enable mindstorm bricks, if disabled at start
		if (!sharedPreferences.getBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false)) {
			sharedPreferences.edit().putBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, true).commit();
		}

		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createEmptyProject();

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}
}
