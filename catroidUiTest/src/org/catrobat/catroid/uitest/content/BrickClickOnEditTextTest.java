/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
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
import android.widget.ListView;

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
		ArrayList<Integer> yPosition;
		int addedYPosition;

		UiTestUtils.addNewBrick(solo, R.string.brick_set_x);
		solo.sleep(500);
		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, yPosition.get(0), 20);
		solo.sleep(200);

		List<Brick> brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("One Brick should be in bricklist", 1, brickListToCheck.size());
		assertTrue("Set brick should be instance of SetXBrick", brickListToCheck.get(0) instanceof SetXBrick);

		UiTestUtils.addNewBrick(solo, R.string.brick_set_y);
		solo.sleep(500);
		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, yPosition.get(1), 20);
		solo.sleep(200);

		brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("Two Bricks should be in bricklist", 2, brickListToCheck.size());
		assertTrue("Set brick should be instance of SetYBrick", brickListToCheck.get(0) instanceof SetYBrick);
		assertTrue("Set brick should be instance of SetXBrick", brickListToCheck.get(1) instanceof SetXBrick);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		editTextFieldVisibility(solo.getString(R.string.category_control));
		editTextFieldVisibility(solo.getString(R.string.category_motion));
		editTextFieldVisibility(solo.getString(R.string.category_sound));
		editTextFieldVisibility(solo.getString(R.string.category_looks));
		editTextFieldVisibility(solo.getString(R.string.category_variables));
		ListView fragmentListView = solo.getCurrentListViews().get(solo.getCurrentListViews().size() - 1);
		solo.scrollDownList(fragmentListView);
		editTextFieldVisibility(solo.getString(R.string.category_lego_nxt));

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

	private void editTextFieldVisibility(String category) {

		solo.clickOnText(category);

		solo.searchText(category);
		int ignoreFirstTwo = 0;

		ArrayList<EditText> editTextList = solo.getCurrentEditTexts();
		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE && ignoreFirstTwo > 2) {
				fail("EditTexts should be invisible in AddBrickFragment! Check other brick xmls for more information");
			}
			ignoreFirstTwo++;
		}

		ListView fragmentListView = solo.getCurrentListViews().get(solo.getCurrentListViews().size() - 1);
		solo.scrollDownList(fragmentListView);

		ignoreFirstTwo = 0;
		editTextList = solo.getCurrentEditTexts();
		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE && ignoreFirstTwo > 2) {
				fail("EditTexts should be invisible in AddBrickDialog! Check other brick xmls for more information");
			}
			ignoreFirstTwo++;
		}

		solo.scrollUpList(fragmentListView);
		solo.goBack();
	}
}
