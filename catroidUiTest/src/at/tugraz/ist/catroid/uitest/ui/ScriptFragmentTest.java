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
package at.tugraz.ist.catroid.uitest.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ScriptFragmentTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Solo solo;
	private List<Brick> brickListToCheck;

	public ScriptFragmentTest() {
		super(ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		brickListToCheck = UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.setActivityOrientation(Solo.PORTRAIT);
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testCreateNewBrickButton() {
		int brickCountInView = solo.getCurrentListViews().get(0).getCount();
		int brickCountInList = brickListToCheck.size();

		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		solo.clickOnText(getActivity().getString(R.string.brick_when_started));
		solo.sleep(100);

		assertTrue("Wait brick is not in List", solo.searchText(getActivity().getString(R.string.brick_wait)));

		assertEquals("Brick count in list view not correct", brickCountInView + 1, solo.getCurrentListViews().get(0)
				.getCount());
		assertEquals("Brick count in brick list not correct", brickCountInList + 1, ProjectManager.getInstance()
				.getCurrentScript().getBrickList().size());
	}

	public void testBrickCategoryDialog() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

		if (!pref.getBoolean("setting_mindstorm_bricks", false)) {
			UiTestUtils.goToHomeActivity(solo.getCurrentActivity());
			solo.clickOnText(getActivity().getString(R.string.settings));
			solo.clickOnText(getActivity().getString(R.string.pref_enable_ms_bricks));
			solo.goBack();
			solo.clickOnText(getActivity().getString(R.string.current_project_button));
			solo.clickOnText(getActivity().getString(R.string.background));
		}

		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_add);
		String categorySoundLabel = solo.getString(R.string.category_sound);
		String categoryLegoNXTLabel = solo.getString(R.string.category_lego_nxt);
		String categoryControlLabel = solo.getString(R.string.category_control);

		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(getActivity().getString(R.string.category_motion)));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(getActivity().getString(R.string.category_looks)));
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categorySoundLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(categoryControlLabel));

		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(categoryLegoNXTLabel));

		solo.clickOnText(getActivity().getString(R.string.category_control));
		assertTrue("AddBrickDialog was not opened after selecting a category",
				solo.searchText(getActivity().getString(R.string.brick_wait)));

		solo.goBack();
		assertTrue("Could not go back to BrickCategoryDialog from AddBrickDialog", solo.searchText(categorySoundLabel));

		solo.clickOnText(categoryLegoNXTLabel);
		assertTrue("AddBrickDialog was not opened after selecting a category",
				solo.searchText(getActivity().getString(R.string.brick_motor_action)));

		solo.goBack();
		assertTrue("Could not go back to BrickCategoryDialog from AddBrickDialog",
				solo.searchText(categoryLegoNXTLabel));

		// needed to fix NullPointerException in next Testcase
		solo.finishInactiveActivities();
	}

	public void testSimpleDragNDrop() {
		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo);
		assertTrue("Test project brick list smaller than expected", yPositionList.size() >= 6);

		longClickAndDrag(10, yPositionList.get(4), 10, yPositionList.get(2), 20);
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
		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo);
		assertTrue("Test project brick list smaller than expected", yPositionList.size() >= 6);

		Display display = getActivity().getWindowManager().getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int displayWidth = display.getWidth();

		longClickAndDrag(30, yPositionList.get(2), displayWidth, yPositionList.get(2), 40);
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
		String currentProject = getActivity().getString(R.string.current_project_button);
		String background = getActivity().getString(R.string.background);
		String categoryLooks = getActivity().getString(R.string.category_looks);
		String categoryMotion = getActivity().getString(R.string.category_motion);
		String setBackground = getActivity().getString(R.string.brick_set_background);
		String nextBackground = getActivity().getString(R.string.brick_next_background);
		String comeToFront = getActivity().getString(R.string.brick_come_to_front);
		String goNStepsBack = getActivity().getString(R.string.brick_go_back_layers);

		UiTestUtils.goToHomeActivity(solo.getCurrentActivity());

		solo.clickOnText(currentProject);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.clickOnText(background);
		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_add);
		solo.clickOnText(categoryLooks);
		assertTrue("SetCostumeBrick was not renamed for background sprite", solo.searchText(setBackground));
		solo.clickOnText(setBackground);
		solo.clickOnText(getActivity().getString(R.string.brick_when_started));
		assertTrue("SetCostumeBrick was not renamed for background sprite", solo.searchText(setBackground));
		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_add);
		solo.clickOnText(categoryLooks);
		assertTrue("NextCostumeBrick was not renamed for background sprite", solo.searchText(nextBackground));
		solo.clickOnText(nextBackground);
		solo.clickOnText(getActivity().getString(R.string.brick_when_started));
		assertTrue("NextCostumeBrick was not renamed for background sprite", solo.searchText(nextBackground));

		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_add);
		solo.clickOnText(categoryMotion);
		assertFalse("ComeToFrontBrick is in the brick list!", solo.searchText(comeToFront));
		assertFalse("GoNStepsBackBrick is in the brick list!", solo.searchText(goNStepsBack));

		// needed to fix NullPointerException in next Testcase
		solo.finishInactiveActivities();
	}

	public void testSelectCategoryDialogOnOrientationChange() {
		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_add);
		String categoryMotionLabel = solo.getString(R.string.category_motion);
		String categoryLooksLabel = solo.getString(R.string.category_looks);
		String categorySoundLabel = solo.getString(R.string.category_sound);
		String categoryControlLabel = solo.getString(R.string.category_control);

		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categoryMotionLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categoryLooksLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog", solo.searchText(categorySoundLabel));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(categoryControlLabel));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(200);
		assertTrue("A category was not visible after changing orientation", solo.searchText(categoryMotionLabel));
		assertTrue("A category was not visible after changing orientation", solo.searchText(categoryLooksLabel));
		assertTrue("A category was not visible after changing orientation", solo.searchText(categorySoundLabel));
		assertTrue("A category was not visible after changing orientation", solo.searchText(categoryControlLabel));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(200);
		assertTrue("A category was not visible after changing orientation", solo.searchText(categoryMotionLabel));
		assertTrue("A category was not visible after changing orientation", solo.searchText(categoryLooksLabel));
		assertTrue("A category was not visible after changing orientation", solo.searchText(categorySoundLabel));
		assertTrue("A category was not visible after changing orientation", solo.searchText(categoryControlLabel));
	}

	public void testAddBrickDialogOnOrientationChange() {
		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_add);

		String brickPlaceAtText = solo.getString(R.string.brick_place_at);
		String brickSetCostume = solo.getString(R.string.brick_set_costume);
		String brickPlaySound = solo.getString(R.string.brick_play_sound);
		String brickWhenStarted = solo.getString(R.string.brick_when_started);

		solo.clickOnText(getActivity().getString(R.string.category_motion));

		assertTrue("Not in AddBrickDialog - category motion", solo.searchText(brickPlaceAtText));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("dialog closed after orientation change", solo.searchText(brickPlaceAtText));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(1000);
		solo.goBack();
		solo.sleep(1000);

		solo.clickOnText(getActivity().getString(R.string.category_looks));
		assertTrue("Not in AddBrickDialog - category looks", solo.searchText(brickSetCostume));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("dialog closed after orientation change", solo.searchText(brickSetCostume));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.goBack();

		solo.clickOnText(getActivity().getString(R.string.category_sound));
		assertTrue("Not in AddBrickDialog - category motion", solo.searchText(brickPlaySound));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("dialog closed after orientation change", solo.searchText(brickPlaySound));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.goBack();

		solo.clickOnText(getActivity().getString(R.string.category_control));
		assertTrue("Not in AddBrickDialog - category motion", solo.searchText(brickWhenStarted));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("dialog closed after orientation change", solo.searchText(brickWhenStarted));
	}

	private void longClickAndDrag(final float xFrom, final float yFrom, final float xTo, final float yTo,
			final int steps) {
		Handler handler = new Handler(getActivity().getMainLooper());

		handler.post(new Runnable() {

			public void run() {
				MotionEvent downEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						MotionEvent.ACTION_DOWN, xFrom, yFrom, 0);
				getActivity().dispatchTouchEvent(downEvent);
			}
		});

		solo.sleep(ViewConfiguration.getLongPressTimeout() + 200);

		handler.post(new Runnable() {
			public void run() {

				for (int i = 0; i <= steps; i++) {
					float x = xFrom + (((xTo - xFrom) / steps) * i);
					float y = yFrom + (((yTo - yFrom) / steps) * i);
					MotionEvent moveEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
							MotionEvent.ACTION_MOVE, x, y, 0);
					getActivity().dispatchTouchEvent(moveEvent);

					solo.sleep(20);
				}
			}
		});

		solo.sleep(steps * 20 + 200);

		handler.post(new Runnable() {

			public void run() {
				MotionEvent upEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						MotionEvent.ACTION_UP, xTo, yTo, 0);
				getActivity().dispatchTouchEvent(upEvent);
			}
		});

		solo.sleep(1000);
	}
}
