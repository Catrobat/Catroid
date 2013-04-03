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
package org.catrobat.catroid.uitest.ui.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class ScriptFragmentTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private static final String KEY_SETTINGS_MINDSTORM_BRICKS = "setting_mindstorm_bricks";

	public ScriptFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		// disable mindstorm bricks, if enabled in test
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (sharedPreferences.getBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false)) {
			sharedPreferences.edit().putBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false).commit();
		}

		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testCreateNewBrickButton() {
		List<Brick> brickListToCheck = UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		int brickCountInView = UiTestUtils.getScriptListView(solo).getCount();
		int brickCountInList = brickListToCheck.size();

		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		solo.clickOnText(solo.getString(R.string.brick_when_started));
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

		// enable mindstorm bricks, if disabled
		if (!sharedPreferences.getBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false)) {
			sharedPreferences.edit().putBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, true).commit();
		}
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		String categorySoundLabel = solo.getString(R.string.category_sound);
		String categoryLegoNXTLabel = solo.getString(R.string.category_lego_nxt);
		String categoryControlLabel = solo.getString(R.string.category_control);
		String categoryLooksLabel = solo.getString(R.string.category_looks);
		String categoryMotionLabel = solo.getString(R.string.category_motion);
		String categoryUserVariablesLabel = solo.getString(R.string.category_variables);

		// Test if all Categories are present
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categoryMotionLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categoryLooksLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categorySoundLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(categoryControlLabel));
		ListView fragmentListView = solo.getCurrentListViews().get(solo.getCurrentListViews().size() - 1);
		solo.scrollListToBottom(fragmentListView);
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(categoryUserVariablesLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(categoryLegoNXTLabel));

		// Test if the correct category opens when clicked
		String brickPlaceAtText = solo.getString(R.string.brick_place_at);
		String brickSetLook = solo.getString(R.string.brick_set_look);
		String brickPlaySound = solo.getString(R.string.brick_play_sound);
		String brickWhenStarted = solo.getString(R.string.brick_when_started);
		String brickLegoStopMotor = solo.getString(R.string.motor_stop);
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

		fragmentListView = solo.getCurrentListViews().get(solo.getCurrentListViews().size() - 1);
		solo.scrollListToBottom(fragmentListView);
		solo.clickOnText(categoryUserVariablesLabel);
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

		List<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.addNewBrick(solo, R.string.brick_broadcast_receive);
		solo.clickOnScreen(20, yPositionList.get(0) + 20);
		solo.sleep(200);

		assertEquals("Two control bricks should be added.", 2, sprite.getNumberOfScripts());
		assertTrue("First script isn't a start script.", sprite.getScript(0) instanceof StartScript);
		assertTrue("Second script isn't a broadcast script.", sprite.getScript(1) instanceof BroadcastScript);
	}

	public void testSimpleDragNDrop() {
		List<Brick> brickListToCheck = UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 1);
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

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		solo.clickOnCheckBox(0);

		String expectedTitle = solo.getString(R.string.delete) + " " + Integer.toString(brickListToCheck.size() + 1)
				+ " " + solo.getString(R.string.brick_multiple);

		int timeToWaitForTitle = 300;
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(solo.getString(R.string.delete), 0, 50));

		int numberOfBricks = ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(0).getNumberOfBricks();

		assertEquals("Not all Bricks have been deleted!", 0, numberOfBricks);
	}

	public void testDeleteActionModeBack() {
		List<Brick> brickListToCheck = UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		solo.clickOnCheckBox(0);

		solo.goBack();
		int numberOfBricks = ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(0).getNumberOfBricks();

		assertEquals("No Brick should have been deleted!", brickListToCheck.size(), numberOfBricks);
	}

	public void testDeleteActionModeAllBricks() {
		UiTestUtils.createTestProjectWithEveryBrick();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		List<Brick> brickList = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0).getScript(0)
				.getBrickList();

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);
		solo.clickOnCheckBox(0);

		for (int position = 1; position < brickList.size(); position++) {
			assertEquals("AlphaValue of " + brickList.get(position).toString() + " is not 100", 100,
					brickList.get(position).getAlphaValue());
		}

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(solo.getString(R.string.delete), 0, 50));

		int numberOfBricks = ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(0).getNumberOfBricks();

		assertEquals("Not all Bricks have been deleted!", 0, numberOfBricks);
	}

	public void testDeleteActionModeTwoScripts() {
		UiTestUtils.createTestProjectForActionModeDelete();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);

		solo.clickOnCheckBox(1);
		solo.clickOnCheckBox(2);

		solo.clickOnCheckBox(4);
		solo.clickOnCheckBox(5);

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(solo.getString(R.string.delete), 0, 50));

		int numberOfBricks = ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(0).getNumberOfBricks();
		int numberOfScripts = ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(0).getNumberOfScripts();

		assertEquals("There should be no bricks", 0, numberOfBricks);
		assertEquals("Expected two ScriptBricks", 2, numberOfScripts);
	}

	public void testDeleteActionModeNestedLoops() {
		UiTestUtils.createTestProjectNestedLoops();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete);

		solo.clickOnCheckBox(3);
		String expectedTitle = solo.getString(R.string.delete) + " " + 3 + " "
				+ solo.getString(R.string.brick_multiple);
		int timeToWaitForTitle = 300;
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		solo.clickOnCheckBox(4);
		assertEquals("Fourth checkbox should be checked", true, solo.getCurrentCheckBoxes().get(4).isChecked());

		solo.sleep(500);
		solo.clickOnCheckBox(1);
		expectedTitle = solo.getString(R.string.delete) + " " + 6 + " " + solo.getString(R.string.brick_multiple);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		solo.clickOnCheckBox(1);
		solo.clickOnCheckBox(3);

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(solo.getString(R.string.delete), 0, 50));

		int numberOfBricks = ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(0).getNumberOfBricks();
		int numberOfForeverBricks = 0;
		int numberOfEndBricks = 0;

		ListView dragAndDropListView = solo.getCurrentListViews().get(1);
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

		solo.clickOnCheckBox(2);
		solo.clickOnCheckBox(5);
		String expectedTitle = solo.getString(R.string.delete) + " " + 5 + " "
				+ solo.getString(R.string.brick_multiple);
		int timeToWaitForTitle = 300;
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		solo.sleep(500);
		solo.clickOnCheckBox(5);
		expectedTitle = solo.getString(R.string.delete);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		solo.sleep(300);
		solo.clickOnCheckBox(3);
		expectedTitle = solo.getString(R.string.delete) + " " + 5 + " " + solo.getString(R.string.brick_multiple);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("ActionMode didn't disappear", solo.waitForText(solo.getString(R.string.delete), 0, 50));

		int numberOfBricks = ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(0).getNumberOfBricks();

		ListView dragAndDropListView = solo.getCurrentListViews().get(1);
		List<Brick> currentBrickList = new ArrayList<Brick>();

		for (int position = 0; position < dragAndDropListView.getChildCount(); position++) {
			currentBrickList.add((Brick) dragAndDropListView.getItemAtPosition(position));
		}

		assertEquals("Wrong number of bricks left", 0, numberOfBricks);
	}

	public void testDeleteItem() {
		List<Brick> brickListToCheck = UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 1);
		assertTrue("Test project brick list smaller than expected", yPositionList.size() >= 6);

		Display display = getActivity().getWindowManager().getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int displayWidth = display.getWidth();

		UiTestUtils.longClickAndDrag(solo, 30, yPositionList.get(2), displayWidth, yPositionList.get(2), 40);
		solo.sleep(1000);
		ArrayList<Brick> brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();

		assertEquals("This brick shouldn't be deleted due TrashView does not exist", brickListToCheck.size(),
				brickList.size());

		solo.clickOnScreen(20, yPositionList.get(2));
		if (!solo.waitForText(solo.getString(R.string.brick_context_dialog_delete_brick), 0, 5000)) {
			fail("Text not shown in 5 secs!");
		}
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));
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

	public void testBackgroundBricks() {
		Project standardProject = null;
		try {
			standardProject = StandardProjectHandler.createAndSaveStandardProject(
					UiTestUtils.DEFAULT_TEST_PROJECT_NAME, getInstrumentation().getTargetContext());
		} catch (IOException e) {
			fail("Could not create standard project");
			e.printStackTrace();
		}

		if (standardProject == null) {
			fail("Could not create standard project");
		}
		ProjectManager.INSTANCE.setProject(standardProject);
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
		solo.clickOnText(solo.getString(R.string.brick_when_started));
		assertTrue("SetLookBrick was not renamed for background sprite", solo.searchText(setBackground));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(categoryLooks);
		assertTrue("NextLookBrick was not renamed for background sprite", solo.searchText(nextBackground));
		solo.clickOnText(nextBackground);
		solo.clickOnText(solo.getString(R.string.brick_when_started));
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
		String delete = solo.getString(R.string.delete);
		String showDetails = solo.getString(R.string.show_details);

		UiTestUtils.openOptionsMenu(solo);

		assertFalse("Found menu item '" + rename + "'", solo.waitForText(rename, 1, timeToWait, false, true));
		assertFalse("Found menu item '" + delete + "'", solo.waitForText(delete, 1, timeToWait, false, true));
		assertFalse("Found menu item '" + showDetails + "'", solo.waitForText(showDetails, 1, timeToWait, false, true));
	}

	public void testOptionsEnableLegoMindstormBricks() {
		UiTestUtils.createTestProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		String settings = solo.getString(R.string.preference_title);
		String mindstormsPreferenceString = solo.getString(R.string.preference_title_enable_mindstorm_bricks);
		String categoryLegoNXTLabel = solo.getString(R.string.category_lego_nxt);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		if (sharedPreferences.getBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false)) {
			sharedPreferences.edit().putBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false).commit();
		}

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		assertFalse("Lego brick category is showing!", solo.searchText(categoryLegoNXTLabel));

		solo.sleep(300);
		solo.goBack();
		assertEquals("Action bar navigation spinner doesn't show \'" + solo.getString(R.string.scripts) + "\'",
				solo.getString(R.string.scripts), UiTestUtils.getActionbarSpinnerOnPreHoneyComb(solo).getSelectedItem()
						.toString());

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		UiTestUtils.openOptionsMenu(solo);

		solo.clickOnText(settings);
		solo.assertCurrentActivity("Wrong Activity", SettingsActivity.class);
		solo.clickOnText(mindstormsPreferenceString);
		solo.goBack();

		solo.sleep(500);
		ListView fragmentListView = solo.getCurrentListViews().get(solo.getCurrentListViews().size() - 1);
		solo.scrollListToBottom(fragmentListView);
		assertTrue("Lego brick category is not showing!", solo.searchText(categoryLegoNXTLabel));

		UiTestUtils.openOptionsMenu(solo);
		solo.clickOnText(settings);
		solo.assertCurrentActivity("Wrong Activity", SettingsActivity.class);
		solo.clickOnText(mindstormsPreferenceString);
		solo.goBack();

		assertFalse("Lego brick category is showing!", solo.searchText(categoryLegoNXTLabel));
	}
}
