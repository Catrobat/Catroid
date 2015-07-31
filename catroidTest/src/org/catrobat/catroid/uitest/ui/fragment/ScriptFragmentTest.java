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
package org.catrobat.catroid.uitest.ui.fragment;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScriptFragmentTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final String TAG = ScriptFragmentTest.class.getSimpleName();

	public ScriptFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void tearDown() throws Exception {
		// disable mindstorms bricks, if enabled in test
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (sharedPreferences.getBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false)) {
			sharedPreferences.edit().putBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false).commit();
		}
		super.tearDown();
	}

	public void testCopyScript() {
		List<Brick> brickList = UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);

		String expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_copy,
				brickList.size() + 1, brickList.size() + 1);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("No brick has been copied!", 12, numberOfBricks);
	}

	public void testCopyMultipleBricks() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(1);
		solo.clickOnCheckBox(2);

		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForText(solo.getString(R.string.brick_hide));

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("No brick has been copied!", 8, numberOfBricks);
	}

	public void testCopyActionMode() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		String expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_copy, 0, 0);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		solo.clickOnCheckBox(1);

		expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_copy, 1, 1);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForText(solo.getString(R.string.brick_hide));

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("No brick has been copied!", 7, numberOfBricks);
	}

	public void testCopyAddedBrickWithoutAddedScript() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		solo.clickOnCheckBox(0);

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));

		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		solo.sleep(500);
		UiTestUtils.dragFloatingBrickDownwards(solo);
		solo.sleep(500);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		solo.clickOnCheckBox(1);

		UiTestUtils.acceptAndCloseActionMode(solo);

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("No brick has been copied!", 2, numberOfBricks);
	}

	public void testCopyFromContextDialog() {
		UiTestUtils.createTestProject();
		for (int index = 0; index < 5; ++index) {
			ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).getScript(0)
					.addBrick(new ShowBrick());
		}
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getNumberOfBricks();

		solo.clickOnText(solo.getString(R.string.brick_hide));
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_copy_brick));
		solo.sleep(200);

		ArrayList<Integer> yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		int addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);
		solo.drag(20, 20, addedYPosition, yPosition.get(yPosition.size() - 1) + 20, 20);
		solo.sleep(200);

		assertEquals("Brick was not copied", numberOfBricks + 1, ProjectManager.getInstance().getCurrentProject()
				.getSpriteList().get(0).getNumberOfBricks());
	}

	public void testCopyCopiedBrick() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		solo.clickOnText(solo.getString(R.string.select_all).toUpperCase(Locale.getDefault()));

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));

		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		solo.sleep(500);
		UiTestUtils.dragFloatingBrickDownwards(solo);
		solo.sleep(500);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		solo.clickOnCheckBox(1);

		UiTestUtils.acceptAndCloseActionMode(solo);

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("No brick has been copied!", 2, numberOfBricks);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		solo.clickOnCheckBox(2);
		UiTestUtils.acceptAndCloseActionMode(solo);

		numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).getNumberOfBricks();

		assertEquals("No brick has been copied!", 3, numberOfBricks);
	}

	public void testCreateNewBrickButton() {
		List<Brick> brickListToCheck = UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		int brickCountInView = UiTestUtils.getScriptListView(solo).getCount();
		int brickCountInList = brickListToCheck.size();

		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		UiTestUtils.dragFloatingBrick(solo, 1);
		solo.sleep(100);

		assertTrue("Wait brick is not in List", solo.searchText(solo.getString(R.string.brick_wait)));

		assertEquals("Brick count in list view not correct", brickCountInView + 1, UiTestUtils.getScriptListView(solo)
				.getCount());
		assertEquals("Brick count in brick list not correct", brickCountInList + 1, ProjectManager.getInstance()
				.getCurrentScript().getBrickList().size());
	}

	public void testBrickCategoryDialog() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		// enable mindstorms bricks, if disabled
		if (!sharedPreferences.getBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false)) {
			sharedPreferences.edit().putBoolean(SettingsActivity.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, true).commit();
		}
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		String categorySoundLabel = solo.getString(R.string.category_sound);
		String categoryLegoNXTLabel = solo.getString(R.string.category_lego_nxt);
		String categoryControlLabel = solo.getString(R.string.category_control);
		String categoryLooksLabel = solo.getString(R.string.category_looks);
		String categoryMotionLabel = solo.getString(R.string.category_motion);
		String categoryDataLabel = solo.getString(R.string.category_data);

		// Test if all Categories are present
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categoryMotionLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categoryLooksLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categorySoundLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(categoryControlLabel));
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categoryDataLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(categoryLegoNXTLabel));

		// Test if the correct category opens when clicked
		String brickPlaceAtText = solo.getString(R.string.brick_place_at);
		String brickSetLook = solo.getString(R.string.brick_set_look);
		String brickPlaySound = solo.getString(R.string.brick_play_sound);
		String brickWhenStarted = solo.getString(R.string.brick_when_started);
		String brickLegoStopMotor = solo.getString(R.string.nxt_motor_stop);
		String brickSetVariable = solo.getString(R.string.brick_set_variable);

		solo.scrollListToTop(fragmentListView);
		solo.clickOnText(categoryMotionLabel);
		assertTrue("AddBrickDialog was not opened after selecting a category",
				solo.waitForText(brickPlaceAtText, 0, 2000));
		solo.goBack();

		solo.clickOnText(categoryLooksLabel);
		assertTrue("AddBrickDialog was not opened after selecting a category", solo.waitForText(brickSetLook, 0, 2000));
		solo.goBack();

		solo.clickOnText(categorySoundLabel);
		assertTrue("AddBrickDialog was not opened after selecting a category",
				solo.waitForText(brickPlaySound, 0, 2000));
		solo.goBack();

		solo.clickOnText(categoryControlLabel);
		assertTrue("AddBrickDialog was not opened after selecting a category",
				solo.waitForText(brickWhenStarted, 0, 2000));
		solo.goBack();

		fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);
		solo.clickOnText(categoryDataLabel);
		assertTrue("AddBrickDialog was not opened after selecting a category",
				solo.waitForText(brickSetVariable, 0, 2000));
		solo.goBack();

		solo.clickOnText(categoryLegoNXTLabel);
		assertTrue("AddBrickDialog was not opened after selecting a category",
				solo.waitForText(brickLegoStopMotor, 0, 2000));
	}

	/**
	 * Tests issue#54. https://github.com/Catrobat/Catroid/issues/54
	 */

	public void testOnlyAddControlBricks() {
		UiTestUtils.createEmptyProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		assertEquals("Project should contain only one script.", 1, sprite.getNumberOfScripts());

		Script script = sprite.getScript(0);
		assertTrue("Single script isn't empty.", script.getBrickList().isEmpty());

		List<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.addNewBrick(solo, R.string.brick_broadcast_receive);
		solo.clickOnScreen(20, yPositionList.get(0) + 20);
		solo.sleep(200);

		assertEquals("Two control bricks should be added.", 2, sprite.getNumberOfScripts());
		assertTrue("First script isn't a start script.", sprite.getScript(0) instanceof StartScript);
		assertTrue("Second script isn't a broadcast script.", sprite.getScript(1) instanceof BroadcastScript);
	}

	public void testCopyButtonNotVisibleScriptCategory() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		assertFalse("Copy Button visible!", UiTestUtils.menuButtonVisible(solo, R.id.copy));

		String categoryLooksLabel = solo.getString(R.string.category_looks);
		solo.clickOnText(categoryLooksLabel);

		assertFalse("Copy Button visible!", UiTestUtils.menuButtonVisible(solo, R.id.copy));
	}

	public void testSimpleDragNDrop() {
		List<Brick> brickListToCheck = UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		assertTrue("Test project brick list smaller than expected", yPositionList.size() >= 6);

		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(4), 10, yPositionList.get(2) - 3, 20);
		ArrayList<Brick> brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();

		assertEquals("Brick count not equal before and after dragging & dropping", brickListToCheck.size(),
				brickList.size());
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(0), brickList.get(0));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(3), brickList.get(1));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(1), brickList.get(2));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(2), brickList.get(3));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(4), brickList.get(4));
	}

	public void testDeleteActionMode() {
		List<Brick> brickListToCheck = UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		String expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_delete, 0,
				0);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		solo.clickOnCheckBox(0);

		expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_delete,
				brickListToCheck.size() + 1, brickListToCheck.size() + 1);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		assertFalse("ActionMode didn't disappear", solo.waitForText(solo.getString(R.string.delete), 0, 50));

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("Not all Bricks have been deleted!", 0, numberOfBricks);
	}

	public void testCheckboxActionModeEntireLine() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		String expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_delete, 0,
				0);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		UiTestUtils.clickOnCheckBox(solo, 1);
		solo.clickOnView(solo.getView(R.id.brick_hide_layout));
		UiTestUtils.clickOnCheckBox(solo, 1);

		expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_delete, 1, 1);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		assertFalse("ActionMode didn't disappear", solo.waitForText(solo.getString(R.string.delete), 0, 50));
	}

	public void testDeleteActionModeSelectAll() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);

		for (CheckBox checkBox : solo.getCurrentViews(CheckBox.class)) {
			assertTrue("CheckBox is not Checked!", checkBox.isChecked());
		}
		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		String yes = solo.getString(R.string.yes);
		UiTestUtils.clickOnText(solo, yes);

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("Not all Bricks have been deleted!", 0, numberOfBricks);
	}

	public void testDeleteActionModeBack() {
		List<Brick> brickListToCheck = UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);

		solo.goBack();
		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("No Brick should have been deleted!", brickListToCheck.size(), numberOfBricks);
	}

	public void testDeleteActionModeAllBricks() {
		UiTestUtils.createTestProjectWithEveryBrick();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		List<Brick> brickList = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).getScript(0)
				.getBrickList();

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);

		for (int position = 1; position < brickList.size(); position++) {
			assertEquals("AlphaValue of " + brickList.get(position).toString() + " is not 100", 100,
					brickList.get(position).getAlphaValue());
		}

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		assertFalse("ActionMode didn't disappear", solo.waitForText(solo.getString(R.string.delete), 0, 50));

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("Not all Bricks have been deleted!", 0, numberOfBricks);
	}

	public void testDeleteActionModeTwoScripts() {
		UiTestUtils.createTestProjectForActionModeDelete();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(1);
		solo.clickOnCheckBox(2);

		solo.clickOnCheckBox(4);
		solo.clickOnCheckBox(5);

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		assertFalse("ActionMode didn't disappear", solo.waitForText(solo.getString(R.string.delete), 0, 50));

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getNumberOfBricks();
		int numberOfScripts = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getNumberOfScripts();

		assertEquals("There should be no bricks", 0, numberOfBricks);
		assertEquals("Expected two ScriptBricks", 2, numberOfScripts);
	}

	public void testDeleteActionModeNestedLoops() {
		UiTestUtils.createTestProjectNestedLoops();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		UiTestUtils.clickOnCheckBox(solo, 3);
		String expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_delete, 2,
				2);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		UiTestUtils.clickOnCheckBox(solo, 4);
		assertEquals("Fourth checkbox should be checked", true, solo.getCurrentViews(CheckBox.class).get(4).isChecked());

		solo.sleep(500);
		UiTestUtils.clickOnCheckBox(solo, 1);
		expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_delete, 5, 5);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		UiTestUtils.clickOnCheckBox(solo, 1);

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		assertFalse("ActionMode didn't disappear", solo.waitForText(solo.getString(R.string.delete), 0, 50));

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getNumberOfBricks();
		int numberOfForeverBricks = 0;
		int numberOfEndBricks = 0;

		ListView dragAndDropListView = solo.getCurrentViews(ListView.class).get(0);
		List<Brick> currentBrickList = new ArrayList<Brick>();

		for (int position = 0; position < dragAndDropListView.getChildCount(); position++) {
			currentBrickList.add((Brick) dragAndDropListView.getItemAtPosition(position));
		}

		for (Brick currentBrick : currentBrickList) {
			if (currentBrick instanceof ForeverBrick) {
				numberOfForeverBricks++;
			}

			if (currentBrick instanceof LoopEndBrick) {
				numberOfEndBricks++;
			}
		}

		assertEquals("There should be only 1 ForeverBrick", 1, numberOfForeverBricks);
		assertEquals("There should be only 1 LoopEndBrick", 1, numberOfEndBricks);
		assertEquals("Wrong number of bricks left", 3, numberOfBricks);
	}

	public void testDeleteActionModeIfBricks() {
		UiTestUtils.createTestProjectIfBricks();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(2);
		solo.clickOnCheckBox(5);
		String expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_delete, 4,
				4);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		solo.sleep(500);
		solo.clickOnCheckBox(5);
		solo.clickOnCheckBox(2);
		expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_delete, 0, 0);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		solo.sleep(300);
		solo.clickOnCheckBox(3);
		expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_delete, 3, 3);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		assertFalse("ActionMode didn't disappear", solo.waitForText(solo.getString(R.string.delete), 0, 50));

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getNumberOfBricks();

		ListView dragAndDropListView = solo.getCurrentViews(ListView.class).get(0);
		List<Brick> currentBrickList = new ArrayList<Brick>();

		for (int position = 0; position < dragAndDropListView.getChildCount(); position++) {
			currentBrickList.add((Brick) dragAndDropListView.getItemAtPosition(position));
		}

		assertEquals("Wrong number of bricks left", 2, numberOfBricks);
	}

	public void testDeleteItem() {
		List<Brick> brickListToCheck = UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		assertTrue("Test project brick list smaller than expected", yPositionList.size() >= 6);

		solo.waitForText(solo.getString(R.string.brick_when_started));
		solo.clickOnText(solo.getString(R.string.brick_when_started));
		solo.waitForText(solo.getString(R.string.brick_context_dialog_delete_script));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_script));
		solo.waitForText(solo.getString(R.string.no));
		solo.clickOnButton(solo.getString(R.string.no));

		solo.sleep(500);
		ArrayList<Brick> brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();

		solo.clickOnText(solo.getString(R.string.brick_show));
		solo.waitForText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.waitForText(solo.getString(R.string.yes));
		solo.clickOnButton(solo.getString(R.string.yes));
		if (!solo.waitForView(ListView.class, 0, 5000)) {
			fail("Dialog does not close in 5 sec!");
		}
		brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();

		assertEquals("Wrong size of BrickList - one item should be removed", brickListToCheck.size() - 1,
				brickList.size());

		assertEquals("Incorrect brick order after deleting a brick", brickListToCheck.get(0), brickList.get(0));
		assertEquals("Incorrect brick order after deleting a brick", brickListToCheck.get(2), brickList.get(1));
		assertEquals("Incorrect brick order after deleting a brick", brickListToCheck.get(3), brickList.get(2));
		assertEquals("Incorrect brick order after deleting a brick", brickListToCheck.get(4), brickList.get(3));
	}

	public void testEmptyView() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getNumberOfBricks();
		assertTrue("There are no bricks!", numberOfBricks > 0);

		TextView emptyViewHeading = (TextView) solo.getCurrentActivity()
				.findViewById(R.id.fragment_script_text_heading);
		TextView emptyViewDescription = (TextView) solo.getCurrentActivity().findViewById(
				R.id.fragment_script_text_description);

		// The Views are gone, we can still make assumptions about them
		assertEquals("Empty View heading is not correct", solo.getString(R.string.scripts), emptyViewHeading.getText()
				.toString());
		assertEquals("Empty View description is not correct",
				solo.getString(R.string.fragment_script_text_description), emptyViewDescription.getText().toString());

		assertEquals("Empty View shown although there are items in the list!", View.GONE,
				solo.getView(android.R.id.empty).getVisibility());

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		UiTestUtils.clickOnCheckBox(solo, 0);

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.sleep(500);
		numberOfBricks = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).getNumberOfBricks();

		assertEquals("Not all Bricks have been deleted!", 0, numberOfBricks);
		assertEquals("Empty View not shown although there are items in the list!", View.VISIBLE,
				solo.getView(android.R.id.empty).getVisibility());
	}

	public void testBackgroundBricks() {
		Project standardProject = null;
		try {
			standardProject = StandardProjectHandler.createAndSaveStandardProject(
					UiTestUtils.DEFAULT_TEST_PROJECT_NAME, getInstrumentation().getTargetContext());
		} catch (IOException e) {
			Log.e(TAG, "Could not create standard project", e);
			fail("Could not create standard project");
		}

		if (standardProject == null) {
			fail("Could not create standard project");
		}
		ProjectManager.getInstance().setProject(standardProject);
		StorageHandler.getInstance().saveProject(standardProject);

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		String categoryLooks = solo.getString(R.string.category_looks);
		String categoryMotion = solo.getString(R.string.category_motion);
		String setBackground = solo.getString(R.string.brick_set_background);
		String nextBackground = solo.getString(R.string.brick_next_background);
		String comeToFront = solo.getString(R.string.brick_come_to_front);
		String ifOnEdgeBounce = solo.getString(R.string.brick_if_on_edge_bounce);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryLooks);
		assertTrue("SetLookBrick was not renamed for background sprite", solo.searchText(setBackground));
		solo.clickOnText(setBackground);
		solo.sleep(500);
		UiTestUtils.dragFloatingBrickDownwards(solo);
		solo.sleep(500);
		assertTrue("SetLookBrick was not renamed for background sprite", solo.searchText(setBackground));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryLooks);
		assertTrue("NextLookBrick was not renamed for background sprite", solo.searchText(nextBackground));
		solo.clickOnText(nextBackground);
		solo.sleep(500);
		UiTestUtils.dragFloatingBrickDownwards(solo);
		solo.sleep(500);
		assertTrue("NextLookBrick was not renamed for background sprite", solo.searchText(nextBackground));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryMotion);
		assertFalse("ComeToFrontBrick is in the brick list!", solo.searchText(comeToFront));
		assertFalse("IfOnEdgeBounceBrick is in the brick list!", solo.searchText(ifOnEdgeBounce));
	}

	public void testOptionsMenuItems() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		int timeToWait = 200;

		String rename = solo.getString(R.string.rename);
		String showDetails = solo.getString(R.string.show_details);
		//String delete = solo.getString(R.string.delete);

		UiTestUtils.openOptionsMenu(solo);

		//TODO: refactor this assertion
		//this works with the current Jenkins devices. On other devices with a different screen
		//size "delete" can also be an options menu item and should be asserted.
		//assertFalse("Found menu item '" + delete + "'", solo.waitForText(delete, 1, timeToWait, false, true));
		assertFalse("Found menu item '" + rename + "'", solo.waitForText(rename, 1, timeToWait, false, true));
		assertFalse("Found menu item '" + showDetails + "'", solo.waitForText(showDetails, 1, timeToWait, false, true));
	}

	public void testBottombarElementsVisibilty() {
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		assertTrue("Bottombar is not visible", solo.getView(R.id.bottom_bar).getVisibility() == View.VISIBLE);
		assertTrue("Add button is not visible", solo.getView(R.id.button_add).getVisibility() == View.VISIBLE);
		assertTrue("Play button is not visible", solo.getView(R.id.button_play).getVisibility() == View.VISIBLE);
		assertTrue("Bottombar separator is not visible",
				solo.getView(R.id.bottom_bar_separator).getVisibility() == View.VISIBLE);
	}

	@SuppressWarnings("deprecation")
	public void testReturnFromStageAfterInvokingFormulaEditor() {
		if (Settings.System.getInt(getActivity().getContentResolver(), Settings.System.ALWAYS_FINISH_ACTIVITIES, 0) == 0) {
			Log.i(TAG, "Developer option 'Don't keep activities' is not set.");
			return;
		}

		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		solo.clickOnView(solo.getView(R.id.brick_set_size_to_edit_text));
		solo.waitForFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);

		solo.goBack();

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);

		solo.waitForActivity(StageActivity.class);
		solo.goBack();
		solo.goBack();

		solo.assertCurrentActivity("Wrong Activity", ScriptActivity.class);
	}

	public void testSelectAllActionModeButton() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnText(solo, selectAll);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 0);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 1);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 0);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());

		solo.goBack();

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnText(solo, selectAll);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 0);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 1);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnCheckBox(solo, 0);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());
	}
}
