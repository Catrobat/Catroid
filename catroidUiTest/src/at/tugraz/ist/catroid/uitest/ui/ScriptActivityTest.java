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
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ScriptActivityTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private List<Brick> brickListToCheck;

	public ScriptActivityTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		brickListToCheck = UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testCreateNewBrickButton() {
		int brickCountInView = solo.getCurrentListViews().get(0).getCount();
		int brickCountInList = brickListToCheck.size();

		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_wait);
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
			UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);
			solo.clickOnText(getActivity().getString(R.string.settings));
			solo.clickOnText(getActivity().getString(R.string.pref_enable_ms_bricks));
			solo.goBack();
			solo.clickOnText(getActivity().getString(R.string.current_project_button));
			solo.clickOnText(getActivity().getString(R.string.background));
		}
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_sprite);

		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(getActivity().getString(R.string.category_motion)));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(getActivity().getString(R.string.category_looks)));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(getActivity().getString(R.string.category_sound)));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(getActivity().getString(R.string.category_control)));

		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(getActivity().getString(R.string.category_lego_nxt)));

		solo.clickOnText(getActivity().getString(R.string.category_control));
		assertTrue("AddBrickDialog was not opened after selecting a category",
				solo.searchText(getActivity().getString(R.string.brick_wait)));

		solo.goBack();
		assertTrue("Could not go back to BrickCategoryDialog from AddBrickDialog",
				solo.searchText(getActivity().getString(R.string.category_sound)));

		solo.clickOnText(getActivity().getString(R.string.category_lego_nxt));
		assertTrue("AddBrickDialog was not opened after selecting a category",
				solo.searchText(getActivity().getString(R.string.brick_motor_action)));

		solo.goBack();
		assertTrue("Could not go back to BrickCategoryDialog from AddBrickDialog",
				solo.searchText(getActivity().getString(R.string.category_lego_nxt)));
	}

	public void testSimpleDragNDrop() {
		ArrayList<Integer> yPositionList = getListItemYPositions();
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
		ArrayList<Integer> yPositionList = getListItemYPositions();
		assertTrue("Test project brick list smaller than expected", yPositionList.size() >= 6);

		int displayWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();

		longClickAndDrag(30, yPositionList.get(2), displayWidth, yPositionList.get(2) + 500, 40);
		solo.sleep(2000);
		ArrayList<Brick> brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();

		assertEquals("Brick count did not decrease by one after deleting a brick", brickListToCheck.size() - 1,
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
		String comeToFront = getActivity().getString(R.string.brick_come_to_front);
		String goNStepsBack = getActivity().getString(R.string.brick_go_back_layers);

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);
		solo.sleep(200);
		solo.clickOnText(currentProject);
		solo.clickOnText(background);
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_sprite);
		solo.clickOnText(categoryLooks);
		assertTrue("SetCostumeBrick was not renamed for background sprite", solo.searchText(setBackground));
		solo.clickOnText(setBackground);
		assertTrue("SetCostumeBrick was not renamed for background sprite", solo.searchText(setBackground));

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_sprite);
		solo.clickOnText(categoryMotion);
		assertFalse("ComeToFrontBrick is in the brick list!", solo.searchText(comeToFront));
		assertFalse("GoNStepsBackBrick is in the brick list!", solo.searchText(goNStepsBack));
	}

	public void testSelectCategoryDialogOnOrientationChange() {
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_sprite);
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(getActivity().getString(R.string.category_motion)));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(getActivity().getString(R.string.category_looks)));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(getActivity().getString(R.string.category_sound)));
		assertTrue("A category was not visible after opening BrickCategoryDialog",
				solo.searchText(getActivity().getString(R.string.category_control)));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("A category was not visible after changing orientation",
				solo.searchText(getActivity().getString(R.string.category_motion)));
		assertTrue("A category was not visible after changing orientation",
				solo.searchText(getActivity().getString(R.string.category_looks)));
		assertTrue("A category was not visible after changing orientation",
				solo.searchText(getActivity().getString(R.string.category_sound)));
		assertTrue("A category was not visible after changing orientation",
				solo.searchText(getActivity().getString(R.string.category_control)));
		solo.setActivityOrientation(Solo.PORTRAIT);
		assertTrue("A category was not visible after changing orientation",
				solo.searchText(getActivity().getString(R.string.category_motion)));
		assertTrue("A category was not visible after changing orientation",
				solo.searchText(getActivity().getString(R.string.category_looks)));
		assertTrue("A category was not visible after changing orientation",
				solo.searchText(getActivity().getString(R.string.category_sound)));
		assertTrue("A category was not visible after changing orientation",
				solo.searchText(getActivity().getString(R.string.category_control)));
	}

	public void testAddBrickDialogOnOrientationChange() {
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_sprite);
		solo.clickOnText(getActivity().getString(R.string.category_motion));
		assertTrue("Not in AddBrickDialog - category motion",
				solo.searchText(getActivity().getString(R.string.brick_place_at)));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("dialog closed after orientation change",
				solo.searchText(getActivity().getString(R.string.brick_place_at)));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.goBack();

		solo.clickOnText(getActivity().getString(R.string.category_looks));
		assertTrue("Not in AddBrickDialog - category looks",
				solo.searchText(getActivity().getString(R.string.brick_set_costume)));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("dialog closed after orientation change",
				solo.searchText(getActivity().getString(R.string.brick_set_costume)));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.goBack();

		solo.clickOnText(getActivity().getString(R.string.category_sound));
		assertTrue("Not in AddBrickDialog - category motion",
				solo.searchText(getActivity().getString(R.string.brick_play_sound)));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("dialog closed after orientation change",
				solo.searchText(getActivity().getString(R.string.brick_play_sound)));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.goBack();

		solo.clickOnText(getActivity().getString(R.string.category_control));
		assertTrue("Not in AddBrickDialog - category motion",
				solo.searchText(getActivity().getString(R.string.brick_when_started)));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("dialog closed after orientation change",
				solo.searchText(getActivity().getString(R.string.brick_when_started)));
	}

	/**
	 * Returns the absolute pixel y coordinates of the displayed bricks
	 * 
	 * @return a list of the y pixel coordinates of the center of displayed bricks
	 */
	private ArrayList<Integer> getListItemYPositions() {
		ArrayList<Integer> yPositionList = new ArrayList<Integer>();
		ListView listView = solo.getCurrentListViews().get(0);

		for (int i = 0; i < listView.getChildCount(); ++i) {
			View currentViewInList = listView.getChildAt(i);

			Rect globalVisibleRect = new Rect();
			currentViewInList.getGlobalVisibleRect(globalVisibleRect);
			int middleYPos = globalVisibleRect.top + globalVisibleRect.height() / 2;
			yPositionList.add(middleYPos);
		}

		return yPositionList;
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
