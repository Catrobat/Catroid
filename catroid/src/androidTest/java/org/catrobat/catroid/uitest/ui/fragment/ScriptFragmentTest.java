/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.adapter.BackPackScriptAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.fragment.BackPackScriptFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScriptFragmentTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final String TAG = ScriptFragmentTest.class.getSimpleName();

	private static final int RESOURCE_IMAGE = org.catrobat.catroid.test.R.drawable.catroid_sunglasses;
	private static final int RESOURCE_SOUND = org.catrobat.catroid.test.R.raw.longsound;
	private static final int TIME_TO_WAIT_BACKPACK = 800;
	private static final String DEFAULT_SCRIPT_GROUP_NAME = "Cat";
	private static final String SECOND_SCRIPT_GROUP_NAME = "Dog";
	private static final java.lang.String SECOND_SPRITE_NAME = "second_sprite";
	private static final String TEST_LOOK_NAME = "testLook";
	private static final String TEST_SOUND_NAME = "testSound";
	private static final int VISIBLE = View.VISIBLE;
	private static final int INVISIBLE = View.INVISIBLE;
	private static final int GONE = View.GONE;

	private String unpack;
	private String backpack;
	private String backpackAdd;
	private String backpackTitle;

	private String delete;
	private String deleteDialogTitle;

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

	@Override
	public void setUp() throws Exception {
		super.setUp();

		unpack = solo.getString(R.string.unpack);
		backpack = solo.getString(R.string.backpack);
		backpackAdd = solo.getString(R.string.backpack_add);
		backpackTitle = solo.getString(R.string.backpack_title);

		delete = solo.getString(R.string.delete);
		deleteDialogTitle = solo.getString(R.string.dialog_confirm_delete_backpack_group_title);

		UiTestUtils.clearBackPack(true);
	}

	public void testCopyScript() {
		List<Brick> brickList = UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);

		String expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_copy,
				brickList.size() + 1, brickList.size() + 1);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("No brick has been copied!", 12, numberOfBricks);
	}

	public void testCopyMultipleBricks() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(1);
		solo.clickOnCheckBox(2);

		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForText(solo.getString(R.string.brick_hide));

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("No brick has been copied!", 8, numberOfBricks);
	}

	public void testCopyActionMode() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		String expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_copy, 0, 0);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		solo.clickOnCheckBox(1);

		expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_copy, 1, 1);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForText(solo.getString(R.string.brick_hide));

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("No brick has been copied!", 7, numberOfBricks);
	}

	public void testCopyAddedBrickWithoutAddedScript() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		solo.clickOnCheckBox(0);

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));

		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		solo.sleep(500);
		UiTestUtils.dragFloatingBrickDownwards(solo);
		solo.sleep(500);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);
		solo.clickOnCheckBox(1);

		UiTestUtils.acceptAndCloseActionMode(solo);

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("No brick has been copied!", 2, numberOfBricks);
	}

	public void testCopyFromContextDialog() {
		UiTestUtils.createTestProject();
		for (int index = 0; index < 5; ++index) {
			ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0).getScript(0)
					.addBrick(new ShowBrick());
		}
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0)
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
				.getDefaultScene().getSpriteList().get(0).getNumberOfBricks());
	}

	public void testCopyCopiedBrick() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		solo.clickOnText(solo.getString(R.string.select_all).toUpperCase(Locale.getDefault()));

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));

		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		solo.sleep(500);
		UiTestUtils.dragFloatingBrickDownwards(solo);
		solo.sleep(500);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);
		solo.clickOnCheckBox(1);

		UiTestUtils.acceptAndCloseActionMode(solo);

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("No brick has been copied!", 2, numberOfBricks);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);
		solo.clickOnCheckBox(2);
		UiTestUtils.acceptAndCloseActionMode(solo);

		numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0).getNumberOfBricks();

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

	public void testDragNDropNestedBrick() {
		List<Brick> brickListToCheck = UiTestUtils.createTestProjectNestedBricks();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 0);
		assertTrue("Test project brick list smaller than expected", yPositionList.size() == 8);

		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(1), 10, yPositionList.get(6) - 3, 20);
		ArrayList<Brick> brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();

		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(5), brickList.get(0));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(0), brickList.get(1));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(1), brickList.get(2));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(2), brickList.get(3));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(3), brickList.get(4));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(4), brickList.get(5));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(6), brickList.get(6));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(7), brickList.get(7));
	}

	public void testDeleteActionMode() {
		List<Brick> brickListToCheck = UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);

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

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("Not all Bricks have been deleted!", 0, numberOfBricks);
	}

	public void testCheckboxActionModeEntireLine() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		String expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_delete, 0,
				0);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, 300, false, true));

		UiTestUtils.clickOnCheckBox(solo, 1);

		expectedTitle = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_delete, 1, 1);
		assertTrue("Title not as expected" + expectedTitle, solo.waitForText(expectedTitle, 0, 300, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		assertFalse("ActionMode didn't disappear", solo.waitForText(solo.getString(R.string.delete), 0, 50));
	}

	public void testDeleteActionModeSelectAll() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);

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

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("Not all Bricks have been deleted!", 0, numberOfBricks);
	}

	public void testDeleteActionModeBack() {
		List<Brick> brickListToCheck = UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);

		solo.goBack();
		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("No Brick should have been deleted!", brickListToCheck.size(), numberOfBricks);
	}

	public void testIfLogicReferences() {
		UiTestUtils.createTestProject(UiTestUtils.PROJECTNAME1);
		UiTestUtils.createTestProjectIfBricks();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		assertTrue("Script wasn't backpacked!", solo.waitForText(DEFAULT_SCRIPT_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		clickOnContextMenuItem(DEFAULT_SCRIPT_GROUP_NAME, unpack);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		Script unpackedScript = ProjectManager.getInstance().getCurrentScene().getSpriteList().get(0).getScript(1);
		assertTrue("if bricks have wrong or no references after unpacking", ProjectManager.getInstance()
				.checkCurrentScript(unpackedScript, false));
	}

	public void testDeleteActionModeAllBricks() {
		UiTestUtils.createTestProjectWithEveryBrick();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		List<Brick> brickList = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0).getScript(0)
				.getBrickList();

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);

		for (int position = 1; position < brickList.size(); position++) {
			assertEquals("AlphaValue of " + brickList.get(position).toString() + " is not 100", 100,
					brickList.get(position).getAlphaValue());
		}

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		assertFalse("ActionMode didn't disappear", solo.waitForText(solo.getString(R.string.delete), 0, 50));

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0)
				.getNumberOfBricks();

		assertEquals("Not all Bricks have been deleted!", 0, numberOfBricks);
	}

	public void testDeleteActionModeTwoScripts() {
		UiTestUtils.createTestProjectWithTwoScripts();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(1);
		solo.clickOnCheckBox(2);

		solo.clickOnCheckBox(4);
		solo.clickOnCheckBox(5);

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		assertFalse("ActionMode didn't disappear", solo.waitForText(solo.getString(R.string.delete), 0, 50));

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0)
				.getNumberOfBricks();
		int numberOfScripts = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0)
				.getNumberOfScripts();

		assertEquals("There should be no bricks", 0, numberOfBricks);
		assertEquals("Expected two ScriptBricks", 2, numberOfScripts);
	}

	public void testDeleteActionModeNestedLoops() {
		UiTestUtils.createTestProjectNestedLoops();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);

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

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0)
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

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);

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

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0)
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

		int numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0)
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

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		UiTestUtils.clickOnCheckBox(solo, 0);

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.sleep(500);
		numberOfBricks = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0).getNumberOfBricks();

		assertEquals("Not all Bricks have been deleted!", 0, numberOfBricks);
		assertEquals("Empty View not shown although there are items in the list!", View.VISIBLE,
				solo.getView(android.R.id.empty).getVisibility());
	}

	public void testBackgroundBricks() {
		TestUtils.clearProject(solo.getString(R.string.default_project_name));
		Project defaultProject = null;
		try {
			defaultProject = DefaultProjectHandler.createAndSaveDefaultProject(
					UiTestUtils.DEFAULT_TEST_PROJECT_NAME, getInstrumentation().getTargetContext());
		} catch (IOException e) {
			Log.e(TAG, "Could not create default project", e);
			fail("Could not create default project");
		}

		if (defaultProject == null) {
			fail("Could not create default project");
		}
		ProjectManager.getInstance().setProject(defaultProject);
		StorageHandler.getInstance().saveProject(defaultProject);

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

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnText(solo, selectAll);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnListItem(solo, 0);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnListItem(solo, 1);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnListItem(solo, 0);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());

		solo.goBack();

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnText(solo, selectAll);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnListItem(solo, 0);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnListItem(solo, 1);
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnListItem(solo, 0);
		assertFalse("Select All is still shown", solo.getView(R.id.select_all).isShown());
	}

	public void testBackpackScriptsContextMenu() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);

		assertTrue("BackPack title didn't show up",
				solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Script wasn't backpacked!", solo.waitForText(DEFAULT_SCRIPT_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
	}

	public void testBackpackScriptsDoubleContextMenu() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		solo.goBack();
		backPackFirstScriptWithContextMenu(SECOND_SCRIPT_GROUP_NAME);

		assertTrue("BackPack title didn't show up",
				solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Script wasn't backpacked!", solo.waitForText(DEFAULT_SCRIPT_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Script wasn't backpacked!", solo.waitForText(SECOND_SCRIPT_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
	}

	public void testBackPackScriptsSimpleUnpackingContextMenu() {
		UiTestUtils.createTestProjectWithEveryBrick();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		int brickCountInView = UiTestUtils.getScriptListView(solo).getCount();
		int numberOfBricksInBrickList = ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks();

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		assertTrue("Script wasn't backpacked!", solo.waitForText(DEFAULT_SCRIPT_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
		unpackScriptGroup(DEFAULT_SCRIPT_GROUP_NAME, unpack);
		solo.waitForFragmentByTag(ScriptFragment.TAG);
		solo.sleep(500);

		assertEquals("Brick count in list view not correct", brickCountInView + 7, UiTestUtils.getScriptListView(solo)
				.getCount());
		assertEquals("Brick count in current sprite not correct", numberOfBricksInBrickList + 6,
				ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks());

		UiTestUtils.openBackPack(solo);
		assertTrue("Script wasn't kept in backpack!", solo.waitForText(DEFAULT_SCRIPT_GROUP_NAME, 0,
				TIME_TO_WAIT_BACKPACK));
	}

	public void testBackPackMultipleUnpackingVariablesWithSameName() {
		UiTestUtils.createTestProjectWithUserVariables();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		checkNumberOfElementsInDataContainer();

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		assertTrue("Script wasn't backpacked!", solo.waitForText(DEFAULT_SCRIPT_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
		unpackScriptGroup(DEFAULT_SCRIPT_GROUP_NAME, unpack);
		solo.waitForFragmentByTag(ScriptFragment.TAG);
		solo.sleep(500);

		checkNumberOfElementsInDataContainer();
	}

	public void testBackPackAndUnPackFromDifferentProgrammes() {
		UiTestUtils.createTestProject(UiTestUtils.PROJECTNAME1);
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		assertTrue("Script wasn't backpacked!", solo.waitForText(DEFAULT_SCRIPT_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));

		UiTestUtils.switchToProgrammeBackground(solo, UiTestUtils.PROJECTNAME1, "cat");
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		int brickCountInView = UiTestUtils.getScriptListView(solo).getCount();
		int numberOfBricksInBrickList = ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks();

		UiTestUtils.openBackPack(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(DEFAULT_SCRIPT_GROUP_NAME, unpack);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		assertEquals("Brick count in list view not correct", brickCountInView + 7, UiTestUtils.getScriptListView(solo)
				.getCount());
		assertEquals("Brick count in current sprite not correct", numberOfBricksInBrickList + 6,
				ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks());
	}

	public void testBackPackAndUnPackFromDifferentSprites() {
		UiTestUtils.createTestProjectWithTwoSprites(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		assertTrue("Script wasn't backpacked!", solo.waitForText(DEFAULT_SCRIPT_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.clickOnText(SECOND_SPRITE_NAME);
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		int brickCountInView = UiTestUtils.getScriptListView(solo).getCount();
		int numberOfBricksInBrickList = ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks();

		UiTestUtils.openBackPackFromEmptyAdapter(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(DEFAULT_SCRIPT_GROUP_NAME, unpack);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		assertEquals("Brick count in list view not correct", brickCountInView + 7, UiTestUtils.getScriptListView(solo)
				.getCount());
		assertEquals("Brick count in current sprite not correct", numberOfBricksInBrickList + 6,
				ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks());
	}

	public void testBackPackActionModeCheckingAndTitle() {
		UiTestUtils.createTestProjectWithTwoScripts();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int timeToWaitForTitle = 300;

		String expectedBricksFirstScript = getActivity().getResources().getQuantityString(R.plurals.number_of_bricks_to_backpack,
				3, 3);

		String expectedBricksSecondScript = getActivity().getResources().getQuantityString(R.plurals
						.number_of_bricks_to_backpack,
				6, 6);

		assertFalse("Bricks should not be displayed in title", solo.waitForText(expectedBricksFirstScript, 3, 300, false, true));

		checkIfCheckboxesAreCorrectlyCheckedAndVisible(false, false);

		String expectedTitle = expectedBricksFirstScript;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyCheckedAndVisible(true, false);
		assertTrue("Title not as expected:" + expectedTitle, solo.waitForText(expectedTitle, 0,
				timeToWaitForTitle,
				false, true));

		expectedTitle = expectedBricksSecondScript;

		// Check if multiple-selection is possible
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyCheckedAndVisible(true, true);
		assertTrue("Title not as aspected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = expectedBricksFirstScript;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyCheckedAndVisible(false, true);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = backpack;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyCheckedAndVisible(false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));
	}

	public void testBackPackActionModeIfNothingSelected() {
		UiTestUtils.createTestProjectWithTwoScripts();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);

		int expectedNumberOfBricks = ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks();
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyCheckedAndVisible(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 0, TIME_TO_WAIT_BACKPACK));
		checkIfNumberOfBricksIsEqual(expectedNumberOfBricks);

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyCheckedAndVisible(false, false);
		solo.goBack();
		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 0, TIME_TO_WAIT_BACKPACK));
		checkIfNumberOfBricksIsEqual(expectedNumberOfBricks);
	}

	public void testBackPackActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.createTestProjectWithTwoScripts();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.clickOnCheckBox(1);
		solo.sleep(800);
		checkIfCheckboxesAreCorrectlyCheckedAndVisible(true, true);
		solo.goBack();

		assertFalse("ActionMode didn't disappear", solo.waitForText(backpack, 0, TIME_TO_WAIT_BACKPACK));
		assertFalse("Backpack was opened, but shouldn't be!", solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
	}

	public void testBackPackSelectAll() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		solo.waitForActivity("ScriptActivity");

		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);

		for (Brick brick : ProjectManager.getInstance().getCurrentSprite().getListWithAllBricks()) {
			assertTrue("CheckBox is not Checked!", brick.getCheckBox().isChecked());
		}
		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		fillNewScriptGroupDialog(DEFAULT_SCRIPT_GROUP_NAME);
		assertTrue("BackPack title didn't show up", solo.waitForText(backpackTitle, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("Script wasn't backpacked!", solo.waitForText(DEFAULT_SCRIPT_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
	}

	public void testBackPackScriptsDeleteContextMenu() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);

		BackPackScriptAdapter adapter = getBackPackScriptAdapter();
		int oldCount = adapter.getCount();

		clickOnContextMenuItem(DEFAULT_SCRIPT_GROUP_NAME, delete);
		solo.waitForText(deleteDialogTitle);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		int newCount = adapter.getCount();
		solo.sleep(500);

		assertEquals("Not all scripts were backpacked", 1, oldCount);
		assertEquals("Script group wasn't deleted in backpack", 0, newCount);
		assertEquals("Count of the backpack scriptGroupList is not correct", newCount, BackPackListManager.getInstance().getBackPackedScriptGroups().size());
	}

	public void testBackPackScriptsDeleteActionMode() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		solo.goBack();
		backPackFirstScriptWithContextMenu(SECOND_SCRIPT_GROUP_NAME);

		solo.waitForActivity(BackPackActivity.class);
		solo.waitForFragmentByTag(BackPackScriptFragment.TAG);
		BackPackScriptAdapter adapter = getBackPackScriptAdapter();
		int oldCount = adapter.getCount();

		UiTestUtils.deleteAllItems(solo);

		int newCount = adapter.getCount();
		solo.sleep(500);
		assertTrue("No backpack is emtpy text appeared", solo.searchText(backpack));
		assertTrue("No backpack is emtpy text appeared", solo.searchText(solo.getString(R.string.is_empty)));

		assertEquals("Not all scripts were backpacked", 2, oldCount);
		assertEquals("Script Groups were not deleted in backpack", 0, newCount);
		assertEquals("Count of the backpack scriptGroupList is not correct", newCount, BackPackListManager.getInstance().getBackPackedScriptGroups().size());
	}

	public void testBackPackScriptsActionModeDifferentProgrammes() {
		UiTestUtils.createTestProject(UiTestUtils.PROJECTNAME1);
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackAllScripts(DEFAULT_SCRIPT_GROUP_NAME);
		UiTestUtils.switchToProgrammeBackground(solo, UiTestUtils.PROJECTNAME1, "cat");
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		int brickCountInView = UiTestUtils.getScriptListView(solo).getCount();
		int numberOfBricksInBrickList = ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks();

		UiTestUtils.openBackPack(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		UiTestUtils.openActionMode(solo, unpack, R.id.unpacking);
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);
		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForActivity(ScriptActivity.class);
		solo.sleep(1000);
		assertEquals("Brick count in list view not correct", brickCountInView + 7, UiTestUtils.getScriptListView(solo)
				.getCount());
		assertEquals("Brick count in current sprite not correct", numberOfBricksInBrickList + 6,
				ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks());
		UiTestUtils.deleteAllItems(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertEquals("Brick count in list view not correct", 0, UiTestUtils.getScriptListView(solo)
				.getCount());
		assertEquals("Brick count in current sprite not correct", 0,
				ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks());

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		assertTrue("Backpack items were cleared!", solo.waitForText(backpackTitle, 1, 1000));
	}

	public void testBackPackDeleteActionModeCheckingAndTitle() {
		UiTestUtils.createTestProjectWithTwoScripts();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		solo.goBack();
		backPackFirstScriptWithContextMenu(SECOND_SCRIPT_GROUP_NAME);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int timeToWaitForTitle = 300;

		String expectedTitleOneScriptGroup = delete + " 1 " + solo.getString(R.string.script_group);
		String expectedTitleTwoScriptGroups = delete + " 2 " + solo.getString(R.string.script_groups);

		assertFalse("Script Group should not be displayed in title", solo.waitForText(solo.getString(R.string.script_group), 3, 300, false,
				true));

		checkIfCheckboxesAreCorrectlyCheckedInBackPack(false, false);

		String expectedTitle = expectedTitleOneScriptGroup;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyCheckedInBackPack(true, false);
		assertTrue("Title not as expected:" + expectedTitle, solo.waitForText(expectedTitle, 0,
				timeToWaitForTitle,
				false, true));

		expectedTitle = expectedTitleTwoScriptGroups;

		// Check if multiple-selection is possible
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyCheckedInBackPack(true, true);
		assertTrue("Title not as aspected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = expectedTitleOneScriptGroup;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyCheckedInBackPack(false, true);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = delete;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyCheckedInBackPack(false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));
	}

	public void testBackPackDeleteActionModeIfNothingSelected() {
		UiTestUtils.createTestProjectWithTwoScripts();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		solo.goBack();
		backPackFirstScriptWithContextMenu(SECOND_SCRIPT_GROUP_NAME);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		int expectedNumberOfScriptGroups = BackPackListManager.getInstance().getBackPackedScriptGroups().size();
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyCheckedInBackPack(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT_BACKPACK));
		checkIfNumberOfBricksIsEqualInBackPack(expectedNumberOfScriptGroups);

		UiTestUtils.openActionMode(solo, delete, R.id.delete);
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);
		checkIfCheckboxesAreCorrectlyCheckedInBackPack(false, false);
		solo.goBack();
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT_BACKPACK));
		checkIfNumberOfBricksIsEqualInBackPack(expectedNumberOfScriptGroups);
	}

	public void testBackPackDeleteActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.createTestProjectWithTwoScripts();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		solo.goBack();
		backPackFirstScriptWithContextMenu(SECOND_SCRIPT_GROUP_NAME);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		int expectedNumberOfScriptGroups = BackPackListManager.getInstance().getBackPackedScriptGroups().size();
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.clickOnCheckBox(1);
		solo.sleep(800);
		checkIfCheckboxesAreCorrectlyCheckedInBackPack(true, true);
		solo.goBack();

		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT_BACKPACK));
		checkIfNumberOfBricksIsEqualInBackPack(expectedNumberOfScriptGroups);
	}

	public void testBackPackDeleteSelectAll() {
		UiTestUtils.createTestProjectWithTwoScripts();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		solo.goBack();
		backPackFirstScriptWithContextMenu(SECOND_SCRIPT_GROUP_NAME);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openActionMode(solo, delete, R.id.delete);

		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);

		for (CheckBox checkBox : solo.getCurrentViews(CheckBox.class)) {
			assertTrue("CheckBox is not Checked!", checkBox.isChecked());
		}
		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.waitForText(deleteDialogTitle);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertFalse("Script group wasn't deleted!", solo.waitForText(DEFAULT_SCRIPT_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
		assertFalse("Script group wasn't deleted!", solo.waitForText(SECOND_SCRIPT_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
		assertTrue("No empty bg found!", solo.waitForText(solo.getString(R.string.is_empty), 0, TIME_TO_WAIT_BACKPACK));
	}

	public void testBackPackShowAndHideDetails() {
		UiTestUtils.createTestProjectWithTwoScripts();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		int timeToWait = 300;

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		hideDetails();

		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		solo.clickOnMenuItem(solo.getString(R.string.show_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);

		// Test if showDetails is remembered after pressing back
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		UiTestUtils.openBackPack(solo);
		solo.waitForActivity(BackPackActivity.class.getSimpleName());
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);

		solo.clickOnMenuItem(solo.getString(R.string.hide_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
	}

	public void testBackPackSpecialBricks() {
		UiTestUtils.createEmptyProjectWithoutScript();
		UiTestUtils.createTestProjectWithSpecialBricksForBackPack(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		UiTestUtils.prepareForSpecialBricksTest(getInstrumentation().getContext(), RESOURCE_IMAGE,
				RESOURCE_SOUND, TEST_LOOK_NAME, TEST_SOUND_NAME);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		assertTrue("Script wasn't backpacked!", solo.waitForText(DEFAULT_SCRIPT_GROUP_NAME, 1, TIME_TO_WAIT_BACKPACK));
		solo.goBack();
		solo.goBack();

		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		assertFalse("Visible Backpack was opened despite look should be in hidden backpack", solo.waitForText(unpack, 1, TIME_TO_WAIT_BACKPACK));
		assertFalse("Visible Backpack was opened despite look should be in hidden backpack", solo.waitForText(TEST_LOOK_NAME + "1", 1, TIME_TO_WAIT_BACKPACK));
		assertTrue("Look is not in hidden backpack!", BackPackListManager.getInstance().getHiddenBackpackedLooks().size() == 1);
		solo.goBack();
		solo.goBack();

		solo.clickOnText(solo.getString(R.string.sounds));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		assertFalse("Visible Backpack was opened despite sound should be in hidden backpack", solo.waitForText(unpack, 1, TIME_TO_WAIT_BACKPACK));
		assertFalse("Visible Backpack was opened despite sound should be in hidden backpack", solo.waitForText(TEST_SOUND_NAME + "1", 1, TIME_TO_WAIT_BACKPACK));
		assertTrue("Sound is not in hidden backpack!", BackPackListManager.getInstance().getHiddenBackpackedSounds().size() == 1);
		solo.goBack();
		solo.goBack();
		solo.goBack();

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		assertFalse("Visible Backpack was opened despite sprite should be in hidden backpack", solo.waitForText(unpack, 1, TIME_TO_WAIT_BACKPACK));
		assertTrue("Sprite is not in hidden backpack!", BackPackListManager.getInstance().getHiddenBackpackedSprites().size() == 1);
		assertTrue("Wrong sprite was backpacked!", BackPackListManager.getInstance().getHiddenBackpackedSprites().get(0).getName().equals("dog"));

		UiTestUtils.switchToProgrammeBackground(solo, UiTestUtils.PROJECTNAME3, "cat");
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		ListView listView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		int brickCountInView = listView.getCount();
		int numberOfBricksInBrickList = ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks();

		UiTestUtils.openBackPackFromEmptyAdapter(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(DEFAULT_SCRIPT_GROUP_NAME, unpack);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.waitForActivity(ScriptActivity.class);
		solo.waitForFragmentByTag(ScriptFragment.TAG);
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		listView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		assertEquals("Brick count in list view not correct", brickCountInView + 9, listView.getCount());
		assertEquals("Brick count in current sprite not correct", numberOfBricksInBrickList + 8,
				ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks());

		ProjectManager projectManager = ProjectManager.getInstance();
		DataContainer dataContainer = projectManager.getCurrentProject().getDefaultScene().getDataContainer();
		UserList projectUserList = projectManager.getCurrentProject().getDefaultScene().getDataContainer().getUserList("global_list",
				null);
		UserList spriteUserList = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getDataContainer()
				.getSpriteListOfLists(projectManager.getCurrentSprite()).get(0);
		UserVariable spriteUserVariable = dataContainer.getUserVariable("sprite_var", projectManager.getCurrentSprite());
		UserVariable projectUserVariable = dataContainer.getProjectVariables().get(0);
		assertTrue("Project user list was not unpacked", projectUserList.getName().equals("global_list"));
		assertTrue("Sprite user list was not unpacked", spriteUserList.getName().equals("sprite_list"));
		assertTrue("Project user list was not unpacked", projectUserVariable.getName().equals("global_var"));
		assertTrue("Project user list was not unpacked", spriteUserVariable.getName().equals("sprite_var"));

		List<Brick> unpackedBricks = projectManager.getCurrentSprite().getListWithAllBricks();

		assertTrue("Brick does not contain sprite user list", ((AddItemToUserListBrick) unpackedBricks.get(4))
				.getUserList().getName().equals("sprite_list"));
		assertTrue("Brick does not contain project user list", ((AddItemToUserListBrick) unpackedBricks.get(5))
				.getUserList().getName().equals("global_list"));
		assertTrue("Brick does not contain sprite user variable", ((SetVariableBrick) unpackedBricks.get(6))
				.getUserVariable().getName().equals("sprite_var"));
		assertTrue("Brick does not contain project user variable", ((ChangeVariableBrick) unpackedBricks.get(7))
				.getUserVariable().getName().equals("global_var"));

		solo.goBack();
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Look was not unpacked!", solo.waitForText(TEST_LOOK_NAME, 1, TIME_TO_WAIT_BACKPACK));
		solo.goBack();

		solo.clickOnText(solo.getString(R.string.sounds));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		assertTrue("Sound was not unpacked!", solo.waitForText(TEST_SOUND_NAME, 1, TIME_TO_WAIT_BACKPACK));
		solo.goBack();
		solo.goBack();

		assertTrue("Sprite was not unpacked!", solo.waitForText("dog", 1, TIME_TO_WAIT_BACKPACK));
	}

	public void testBackPackScriptWithUserBrick() {
		UiTestUtils.createTestProjectWithUserBrick();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		assertTrue("Script wasn't backpacked!", solo.waitForText(DEFAULT_SCRIPT_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.clickOnText(SECOND_SPRITE_NAME);
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		int numberOfBricksInBrickList = ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks();

		UiTestUtils.openBackPackFromEmptyAdapter(solo);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(DEFAULT_SCRIPT_GROUP_NAME, unpack);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		assertEquals("Brick count in current sprite not correct", numberOfBricksInBrickList + 7,
				ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks());
		assertEquals("UserBrick prototype count in current sprite not correct", 1,
				ProjectManager.getInstance().getCurrentSprite().getUserBrickList().size());

		UiTestUtils.getIntoUserBrickOverView(solo);
		assertTrue("No UserBrick was unpacked!", solo.waitForText(UiTestUtils.TEST_USER_BRICK_NAME, 0,
				TIME_TO_WAIT_BACKPACK, false,
				true));
	}

	public void testBackPackScriptGroupWithSameName() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		assertTrue("Script wasn't backpacked!", solo.waitForText(DEFAULT_SCRIPT_GROUP_NAME, 0, TIME_TO_WAIT_BACKPACK));
		solo.goBack();

		solo.waitForActivity(ScriptActivity.class);
		solo.waitForFragmentByTag(ScriptFragment.TAG);
		backPackFirstScriptWithContextMenu(DEFAULT_SCRIPT_GROUP_NAME);
		assertTrue("No script group already existing warning appeared!", solo.waitForText(solo.getString(R.string.script_group_name_given),
				0, TIME_TO_WAIT_BACKPACK));
	}

	public void testEmptyActionModeDialogs() {
		UiTestUtils.createEmptyProjectWithoutScript();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		solo.waitForDialogToOpen();
		assertTrue("Nothing to backpack dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_backpack_and_unpack)));
		solo.clickOnButton(0);
		solo.waitForDialogToClose();

		UiTestUtils.openActionMode(solo, delete, R.id.delete);
		solo.waitForDialogToOpen();
		assertTrue("Nothing to delete dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_delete)));
	}

	public void testEmptyActionModeDialogsInBackPack() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackAllScripts(DEFAULT_SCRIPT_GROUP_NAME);
		UiTestUtils.deleteAllItems(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		solo.waitForDialogToOpen();
		assertTrue("Nothing to delete dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_delete)));
		solo.clickOnButton(0);
		solo.waitForDialogToClose();

		UiTestUtils.openActionMode(solo, unpack, R.id.unpacking);
		solo.waitForDialogToOpen();
		assertTrue("Nothing to unpack dialog not shown", solo.waitForText(solo.getString(R.string
				.nothing_to_unpack)));
	}

	public void testOpenBackPackWhenScriptListEmptyButSomethingInBackPack() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		backPackAllScripts(DEFAULT_SCRIPT_GROUP_NAME);

		solo.goBack();
		UiTestUtils.deleteAllItems(solo);

		UiTestUtils.openActionMode(solo, backpack, R.id.backpack);
		solo.waitForActivity(BackPackActivity.class);
		assertTrue("Backpack wasn't opened", solo.waitForText(backpackTitle));
	}

	private BackPackScriptFragment getBackPackScriptFragment() {
		BackPackActivity activity = (BackPackActivity) solo.getCurrentActivity();
		return (BackPackScriptFragment) activity.getFragment(BackPackActivity.FRAGMENT_BACKPACK_SCRIPTS);
	}

	private BackPackScriptAdapter getBackPackScriptAdapter() {
		return (BackPackScriptAdapter) getBackPackScriptFragment().getListAdapter();
	}

	private void backPackFirstScriptWithContextMenu(String scriptGroupName) {
		String brickWhenStarted = solo.getString(R.string.brick_when_started);

		solo.waitForText(brickWhenStarted);
		solo.clickOnText(brickWhenStarted);
		solo.waitForDialogToOpen();
		solo.waitForText(backpackAdd);
		solo.clickOnText(backpackAdd);

		fillNewScriptGroupDialog(scriptGroupName);
	}

	private void backPackAllScripts(String defaultScriptGroupName) {
		UiTestUtils.openBackPackActionModeWhenEmpty(solo);
		solo.waitForActivity("ScriptActivity");
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		UiTestUtils.clickOnText(solo, selectAll);

		UiTestUtils.acceptAndCloseActionMode(solo);
		fillNewScriptGroupDialog(defaultScriptGroupName);
	}

	private void fillNewScriptGroupDialog(String scriptGroupName) {
		solo.waitForDialogToOpen();
		EditText scriptGroupEditText = (EditText) solo.getView(R.id.new_group_dialog_group_name);
		solo.clearEditText(scriptGroupEditText);
		solo.enterText(scriptGroupEditText, scriptGroupName);
		solo.sleep(200);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.ok));

		solo.waitForDialogToClose();
	}

	private void clickOnContextMenuItem(String scriptGroupName, String menuItemName) {
		solo.clickLongOnText(scriptGroupName);
		solo.waitForText(menuItemName);
		solo.clickOnText(menuItemName);
	}

	private void unpackScriptGroup(String scriptGroupName, String menuItemName) {
		clickOnContextMenuItem(scriptGroupName, menuItemName);
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.waitForActivity(ScriptActivity.class);
		solo.sleep(400);
	}

	private void checkVisibilityOfViews(int imageVisibility, int scriptGroupNameVisibility, int scriptGroupDetailsVisibility,
			int checkBoxVisibility) {
		solo.sleep(200);
		assertTrue("Script group image " + getAssertMessageAffix(imageVisibility),
				solo.getView(R.id.fragment_group_backpack_item_image_view).getVisibility() == imageVisibility);
		assertTrue("Script group name " + getAssertMessageAffix(scriptGroupNameVisibility),
				solo.getView(R.id.fragment_group_backpack_item_name_text_view).getVisibility() == scriptGroupNameVisibility);
		assertTrue("Script group details " + getAssertMessageAffix(scriptGroupDetailsVisibility),
				solo.getView(R.id.fragment_group_backpack_item_detail_linear_layout).getVisibility() == scriptGroupDetailsVisibility);
		assertTrue("Checkboxes " + getAssertMessageAffix(checkBoxVisibility),
				solo.getView(R.id.fragment_group_backpack_item_checkbox).getVisibility() == checkBoxVisibility);
	}

	private String getAssertMessageAffix(int visibility) {
		String assertMessageAffix = "";
		switch (visibility) {
			case View.VISIBLE:
				assertMessageAffix = "not visible";
				break;
			case View.GONE:
				assertMessageAffix = "not gone";
				break;
			default:
				break;
		}
		return assertMessageAffix;
	}

	private void checkIfCheckboxesAreCorrectlyCheckedAndVisible(boolean firstCheckboxExpectedChecked,
			boolean secondCheckboxExpectedChecked) {
		solo.sleep(500);
		CheckBox firstCheckBox = solo.getCurrentViews(CheckBox.class).get(0);
		CheckBox secondCheckBox = solo.getCurrentViews(CheckBox.class).get(3);
		assertEquals("First checkbox not correctly checked", firstCheckboxExpectedChecked, firstCheckBox.isChecked());
		assertEquals("Second checkbox not correctly checked", secondCheckboxExpectedChecked, secondCheckBox.isChecked());
		assertTrue("Script checkbox is not visible", firstCheckBox.getVisibility() == VISIBLE);
		assertTrue("Script checkbox is not visible", secondCheckBox.getVisibility() == VISIBLE);
		assertTrue("Non-Script checkbox is visible", solo.getCurrentViews(CheckBox.class).get(1).getVisibility() == INVISIBLE);
		assertTrue("Non-Script checkbox is visible", solo.getCurrentViews(CheckBox.class).get(2).getVisibility() == INVISIBLE);
		assertTrue("Non-Script checkbox is visible", solo.getCurrentViews(CheckBox.class).get(4).getVisibility() == INVISIBLE);
		assertTrue("Non-Script checkbox is visible", solo.getCurrentViews(CheckBox.class).get(5).getVisibility() == INVISIBLE);
	}

	private void checkIfCheckboxesAreCorrectlyCheckedInBackPack(boolean firstCheckboxExpectedChecked,
			boolean secondCheckboxExpectedChecked) {
		solo.sleep(500);
		int start = 0;
		if (solo.getCurrentViews(CheckBox.class).size() > 2) {
			start++;
		}
		CheckBox firstCheckBox = solo.getCurrentViews(CheckBox.class).get(start);
		CheckBox secondCheckBox = solo.getCurrentViews(CheckBox.class).get(start + 1);
		assertEquals("First checkbox not correctly checked", firstCheckboxExpectedChecked, firstCheckBox.isChecked());
		assertEquals("Second checkbox not correctly checked", secondCheckboxExpectedChecked, secondCheckBox.isChecked());
	}

	private void checkIfNumberOfBricksIsEqual(int expectedNumber) {
		int currentNumberOfBricks = ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks();
		assertEquals("Number of bricks is not as expected", expectedNumber, currentNumberOfBricks);
	}

	private void checkIfNumberOfBricksIsEqualInBackPack(int expectedNumber) {
		int currentNumberOfScriptGroups = BackPackListManager.getInstance().getBackPackedScriptGroups().size();
		assertEquals("Number of script groups is not as expected", expectedNumber, currentNumberOfScriptGroups);
	}

	private void hideDetails() {
		if (getBackPackScriptAdapter().getShowDetails()) {
			solo.clickOnMenuItem(solo.getString(R.string.hide_details), true);
			solo.sleep(200);
		}
	}

	private void checkNumberOfElementsInDataContainer() {
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentScene().getDataContainer();
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		UserBrick userBrick = ProjectManager.getInstance().getCurrentUserBrick();

		assertTrue("There is not exactly one global variable in the data container!",
				dataContainer.getProjectVariables().size() == 1);
		assertTrue("There is not exactly one sprite variable in the data container!",
				dataContainer.getVariableListForSprite(sprite).size() == 1);
		assertTrue("There is not exactly one userbrick variable in the data container!",
				dataContainer.getOrCreateVariableListForUserBrick(userBrick).size() == 1);
	}
}
