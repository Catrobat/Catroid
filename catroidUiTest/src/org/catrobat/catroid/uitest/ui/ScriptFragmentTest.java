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
package org.catrobat.catroid.uitest.ui;

import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptTabActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class ScriptFragmentTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private List<Brick> brickListToCheck;
	private static final String KEY_SETTINGS_MINDSTORM_BRICKS = "setting_mindstorm_bricks";

	public ScriptFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		solo.setActivityOrientation(Solo.PORTRAIT);
		// disable mindstorm bricks, if enabled in test
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (sharedPreferences.getBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false)) {
			sharedPreferences.edit().putBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false).commit();
		}

		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	private void initTestProject() {
		brickListToCheck = UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptTabActivityFromMainMenu(solo);
	}

	private void initEmptyProject() {
		UiTestUtils.createEmptyProject();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptTabActivityFromMainMenu(solo);
	}

	public void testCreateNewBrickButton() {
		initTestProject();
		int brickCountInView = solo.getCurrentListViews().get(0).getCount();
		int brickCountInList = brickListToCheck.size();

		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		solo.clickOnText(solo.getString(R.string.brick_when_started));
		solo.sleep(100);

		assertTrue("Wait brick is not in List", solo.searchText(solo.getString(R.string.brick_wait)));

		assertEquals("Brick count in list view not correct", brickCountInView + 1, solo.getCurrentListViews().get(0)
				.getCount());
		assertEquals("Brick count in brick list not correct", brickCountInList + 1, ProjectManager.getInstance()
				.getCurrentScript().getBrickList().size());
	}

	public void testBrickCategoryDialog() {
		initTestProject();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		// enable mindstorm bricks, if disabled
		if (!sharedPreferences.getBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, false)) {
			sharedPreferences.edit().putBoolean(KEY_SETTINGS_MINDSTORM_BRICKS, true).commit();
		}
		UiTestUtils.clickOnActionBar(solo, R.id.menu_add);
		String categorySoundLabel = solo.getString(R.string.category_sound);
		String categoryLegoNXTLabel = solo.getString(R.string.category_lego_nxt);
		String categoryControlLabel = solo.getString(R.string.category_control);
		String categoryLooksLabel = solo.getString(R.string.category_looks);
		String categoryMotionLabel = solo.getString(R.string.category_motion);

		// Test if all Categories are present
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categoryMotionLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categoryLooksLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categorySoundLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(categoryControlLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(categoryLegoNXTLabel));

		// Test if the correct category opens when clicked
		String brickPlaceAtText = solo.getString(R.string.brick_place_at);
		String brickSetCostume = solo.getString(R.string.brick_set_costume);
		String brickPlaySound = solo.getString(R.string.brick_play_sound);
		String brickWhenStarted = solo.getString(R.string.brick_when_started);
		String brickLegoStopMotor = solo.getString(R.string.motor_stop);

		solo.clickOnText(categoryMotionLabel);
		assertTrue("AddBrickDialog was not opened after selecting a category",
				solo.waitForText(brickPlaceAtText, 0, 2000));
		solo.goBack();

		solo.clickOnText(categoryLooksLabel);
		assertTrue("AddBrickDialog was not opened after selecting a category",
				solo.waitForText(brickSetCostume, 0, 2000));
		solo.goBack();

		solo.clickOnText(categorySoundLabel);
		assertTrue("AddBrickDialog was not opened after selecting a category",
				solo.waitForText(brickPlaySound, 0, 2000));
		solo.goBack();

		solo.clickOnText(categoryControlLabel);
		assertTrue("AddBrickDialog was not opened after selecting a category",
				solo.waitForText(brickWhenStarted, 0, 2000));
		solo.goBack();

		solo.clickOnText(categoryLegoNXTLabel);
		assertTrue("AddBrickDialog was not opened after selecting a category",
				solo.waitForText(brickLegoStopMotor, 0, 2000));
	}

	/**
	 * Tests issue#54.
	 */
	public void testOnlyAddControlBricks() {
		initEmptyProject();
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		assertEquals("Project should contain only one script.", 1, sprite.getNumberOfScripts());

		Script script = sprite.getScript(0);
		assertTrue("Single script isn't empty.", script.getBrickList().isEmpty());

		List<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo);
		UiTestUtils.addNewBrick(solo, R.string.brick_broadcast_receive);
		solo.clickOnScreen(20, yPositionList.get(1));
		solo.sleep(200);

		assertEquals("Two controll bricks should be added.", 2, sprite.getNumberOfScripts());
		assertTrue("First script isn't a start script.", sprite.getScript(0) instanceof StartScript);
		assertTrue("Second script isn't a broadcast script.", sprite.getScript(1) instanceof BroadcastScript);
	}

	public void testSimpleDragNDrop() {
		initTestProject();
		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo);
		assertTrue("Test project brick list smaller than expected", yPositionList.size() >= 6);

		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(4), 10, yPositionList.get(2), 20);
		ArrayList<Brick> brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();

		assertEquals("Brick count not equal before and after dragging & dropping", brickListToCheck.size(),
				brickList.size());
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(0), brickList.get(0));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(3), brickList.get(1));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(1), brickList.get(2));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(2), brickList.get(3));
		assertEquals("Incorrect brick order after dragging & dropping", brickListToCheck.get(4), brickList.get(4));
	}

	public void testDeleteItem() {
		initTestProject();
		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo);
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
		initTestProject();
		String currentProject = solo.getString(R.string.main_menu_continue);
		String background = solo.getString(R.string.background);
		String categoryLooks = solo.getString(R.string.category_looks);
		String categoryMotion = solo.getString(R.string.category_motion);
		String setBackground = solo.getString(R.string.brick_set_background);
		String nextBackground = solo.getString(R.string.brick_next_background);
		String comeToFront = solo.getString(R.string.brick_come_to_front);
		String goNStepsBack = solo.getString(R.string.brick_go_back_layers);

		UiTestUtils.goToHomeActivity(solo.getCurrentActivity());

		solo.clickOnText(currentProject);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.clickOnText(background);
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		UiTestUtils.clickOnActionBar(solo, R.id.menu_add);
		solo.clickOnText(categoryLooks);
		assertTrue("SetCostumeBrick was not renamed for background sprite", solo.searchText(setBackground));
		solo.clickOnText(setBackground);
		solo.clickOnText(solo.getString(R.string.brick_when_started));
		assertTrue("SetCostumeBrick was not renamed for background sprite", solo.searchText(setBackground));
		UiTestUtils.clickOnActionBar(solo, R.id.menu_add);
		solo.clickOnText(categoryLooks);
		assertTrue("NextCostumeBrick was not renamed for background sprite", solo.searchText(nextBackground));
		solo.clickOnText(nextBackground);
		solo.clickOnText(solo.getString(R.string.brick_when_started));
		assertTrue("NextCostumeBrick was not renamed for background sprite", solo.searchText(nextBackground));

		UiTestUtils.clickOnActionBar(solo, R.id.menu_add);
		solo.clickOnText(categoryMotion);
		assertFalse("ComeToFrontBrick is in the brick list!", solo.searchText(comeToFront));
		assertFalse("GoNStepsBackBrick is in the brick list!", solo.searchText(goNStepsBack));
	}
}
