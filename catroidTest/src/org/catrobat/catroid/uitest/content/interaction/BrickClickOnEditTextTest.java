/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.uitest.content.interaction;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;
import java.util.List;

public class BrickClickOnEditTextTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public BrickClickOnEditTextTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		getIntoActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		// workaround to disable mindstorms settings
		// should be disabled no matter if test failed or succeeded
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		// disable mindstorms bricks, if enabled at start
		if (sharedPreferences.getBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false)) {
			sharedPreferences.edit().putBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false).commit();
		}
		super.tearDown();
	}

	public void testIfEditTextAreVisibleAndClickOnTextSetXandYInAddBrickDialog() {

		UiTestUtils.addNewBrick(solo, R.string.brick_set_x);
		solo.sleep(500);
		UiTestUtils.dragFloatingBrickDownwards(solo);
		solo.sleep(500);
		List<Brick> brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("One Brick should be in bricklist", 1, brickListToCheck.size());
		assertTrue("Set brick should be instance of SetXBrick", brickListToCheck.get(0) instanceof SetXBrick);

		UiTestUtils.addNewBrick(solo, R.string.brick_set_y);
		solo.sleep(500);
		UiTestUtils.dragFloatingBrickUpwards(solo);
		solo.sleep(500);

		brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("Two Bricks should be in bricklist", 2, brickListToCheck.size());
		assertTrue("Set brick should be instance of SetYBrick", brickListToCheck.get(0) instanceof SetYBrick);
		assertTrue("Set brick should be instance of SetXBrick", brickListToCheck.get(1) instanceof SetXBrick);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		editTextFieldVisibility(solo.getString(R.string.category_control));
		editTextFieldVisibility(solo.getString(R.string.category_motion));
		editTextFieldVisibility(solo.getString(R.string.category_sound));
		editTextFieldVisibility(solo.getString(R.string.category_looks));

		solo.drag(40, 40, 300, 40, UiTestUtils.DRAG_FRAMES);
		editTextFieldVisibility(solo.getString(R.string.category_data));
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollDownList(fragmentListView);
		editTextFieldVisibility(solo.getString(R.string.category_lego_nxt));
	}

	private void getIntoActivity() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		// enable mindstorms bricks, if disabled at start
		if (!sharedPreferences.getBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false)) {
			sharedPreferences.edit().putBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, true).commit();
		}

		UiTestUtils.createEmptyProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	private void editTextFieldVisibility(String category) {

		solo.clickOnText(category);

		solo.searchText(category);
		int ignoreFirstTwo = 0;

		ArrayList<EditText> editTextList = solo.getCurrentViews(EditText.class);
		for (EditText text : editTextList) {
			if (text.getVisibility() == View.VISIBLE && ignoreFirstTwo > 2) {
				fail("EditTexts should be invisible in AddBrickFragment! Check other brick xmls for more information");
			}
			ignoreFirstTwo++;
		}

		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollDownList(fragmentListView);

		ignoreFirstTwo = 0;
		editTextList = solo.getCurrentViews(EditText.class);
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
