/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.uitest.ui;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ScriptActivityTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private List<Brick> brickListToCheck;

	public ScriptActivityTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		// UiTestUtils.createTestProject();
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

	public void testMainMenuButton() {
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_home);
		assertTrue("Clicking on main menu button did not cause main menu to be displayed",
				solo.getCurrentActivity() instanceof MainMenuActivity);
	}

	public void testCreateNewBrickButton() {
		int brickCountInView = solo.getCurrentListViews().get(0).getCount();
		int brickCountInList = brickListToCheck.size();

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.brick_wait));
		solo.sleep(100);

		assertTrue("Wait brick is not in List", solo.searchText(getActivity().getString(R.string.brick_wait)));

		assertEquals("Brick count in list view not correct", brickCountInView + 1, solo.getCurrentListViews().get(0)
				.getCount());
		assertEquals("Brick count in brick list not correct", brickCountInList + 1, ProjectManager.getInstance()
				.getCurrentScript().getBrickList().size());
	}

	public void testSimpleDragNDrop() {
		ArrayList<Integer> yPosList = getListItemYPositions();
		assertTrue("Test project brick list smaller than expected", yPosList.size() >= 6);

		longClickAndDrag(10, yPosList.get(4), 10, yPosList.get(2), 20);
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
		ArrayList<Integer> yPosList = getListItemYPositions();
		assertTrue("Test project brick list smaller than expected", yPosList.size() >= 6);

		int displayWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();

		longClickAndDrag(30, yPosList.get(2), displayWidth, yPosList.get(2), 20);

		ArrayList<Brick> brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();

		assertEquals("Brick count did not decrease by one after deleting a brick", brickListToCheck.size() - 1,
				brickList.size());
		assertEquals("Incorrect brick order after deleting a brick", brickListToCheck.get(0), brickList.get(0));
		assertEquals("Incorrect brick order after deleting a brick", brickListToCheck.get(2), brickList.get(1));
		assertEquals("Incorrect brick order after deleting a brick", brickListToCheck.get(3), brickList.get(2));
		assertEquals("Incorrect brick order after deleting a brick", brickListToCheck.get(4), brickList.get(3));
	}

	/**
	 * Returns the absolute pixel y coordinates of the displayed bricks
	 * 
	 * @return a list of the y pixel coordinates of the center of displayed bricks
	 */
	private ArrayList<Integer> getListItemYPositions() {
		ArrayList<Integer> yPosList = new ArrayList<Integer>();
		ListView listView = solo.getCurrentListViews().get(0);

		for (int i = 0; i < listView.getChildCount(); ++i) {
			View currentViewInList = listView.getChildAt(i);

			Rect globalVisibleRect = new Rect();
			currentViewInList.getGlobalVisibleRect(globalVisibleRect);
			int middleYPos = globalVisibleRect.top + globalVisibleRect.height() / 2;
			yPosList.add(middleYPos);
		}

		return yPosList;
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

		solo.sleep(ViewConfiguration.getLongPressTimeout() + 100);

		handler.post(new Runnable() {

			public void run() {

				for (int i = 0; i <= steps; i++) {
					float x = xFrom + (((xTo - xFrom) / steps) * i);
					float y = yFrom + (((yTo - yFrom) / steps) * i);
					MotionEvent moveEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
							MotionEvent.ACTION_MOVE, x, y, 0);
					getActivity().dispatchTouchEvent(moveEvent);

					solo.sleep(10);
				}
			}
		});

		solo.sleep(steps * 10 + 100);

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
