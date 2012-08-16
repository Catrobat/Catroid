/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.uitest.content;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.SetYBrick;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class BrickClickOnEditTextTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;

	public BrickClickOnEditTextTest() {
		super(ScriptTabActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createEmptyProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		// workaround to disable mindstorm settings
		// should be disabled no matter if test failed or succeeded
		String settingsText = solo.getString(R.string.settings);
		String prefMsBricks = solo.getString(R.string.pref_enable_ms_bricks);

		UiTestUtils.goToHomeActivity(getActivity());
		solo.clickOnText(settingsText);
		solo.clickOnText(prefMsBricks);

		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testIfEditTextAreVisibleAndClickOnTextSetXandYInAddBrickDialog() {
		// clicks on spriteName needed to get focus on listview for solo without adding hovering brick
		String spriteName = solo.getString(R.string.sprite_name);

		String settingsText = solo.getString(R.string.settings);
		String prefMsBricks = solo.getString(R.string.pref_enable_ms_bricks);
		String categoryMotionText = solo.getString(R.string.category_motion);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

		//disable mindstorm bricks, if enabled at start
		if (!prefs.getBoolean("setting_mindstorm_bricks", false)) {
			UiTestUtils.goToHomeActivity(getActivity());
			solo.clickOnText(settingsText);
			solo.clickOnText(prefMsBricks);
			solo.goBack();
			solo.waitForActivity(MainMenuActivity.class.getSimpleName());
			UiTestUtils.clearAllUtilTestProjects();
			UiTestUtils.createEmptyProject();
			solo.sleep(200);
			solo.clickOnText(solo.getString(R.string.current_project_button));
			solo.sleep(100);
			solo.waitForActivity(ProjectActivity.class.getSimpleName());
			solo.clickInList(1);
		}

		int categoryStringId = 0;
		float screenWidth = 0;
		float getTextViewXPosition = 0;

		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_add);
		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_set_x);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(categoryMotionText);
		ArrayList<Integer> listOfYPosition = UiTestUtils.getListItemYPositions(solo);
		screenWidth = solo.getCurrentActivity().getResources().getDisplayMetrics().widthPixels;

		getTextViewXPosition = (float) ((screenWidth / 2.0) * 0.75);
		solo.clickOnScreen(getTextViewXPosition, listOfYPosition.get(1));
		solo.clickOnText(spriteName);

		List<Brick> brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("One Brick should be in bricklist", 1, brickListToCheck.size());
		assertTrue("Set brick should be instance of SetXBrick", brickListToCheck.get(0) instanceof SetXBrick);

		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_add);
		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_set_y);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(categoryMotionText);
		listOfYPosition = UiTestUtils.getListItemYPositions(solo);
		screenWidth = solo.getCurrentActivity().getResources().getDisplayMetrics().widthPixels;
		getTextViewXPosition = (float) ((screenWidth / 2.0) * 0.75);

		solo.clickOnScreen(getTextViewXPosition, listOfYPosition.get(2));
		solo.clickOnText(spriteName);

		brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("One Brick should be in bricklist", 2, brickListToCheck.size());
		assertTrue("Set brick should be instance of SetYBrick", brickListToCheck.get(0) instanceof SetYBrick);
		assertTrue("Set brick should be instance of SetXBrick", brickListToCheck.get(1) instanceof SetXBrick);

		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_add);
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
}
