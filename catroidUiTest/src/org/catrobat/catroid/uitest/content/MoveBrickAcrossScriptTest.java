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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.os.Handler;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.jayway.android.robotium.solo.Solo;

public class MoveBrickAcrossScriptTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private ArrayList<Brick> brickListToCheck;
	private ArrayList<Brick> secondBrickListForMoving;
	private Sprite firstSprite;

	public MoveBrickAcrossScriptTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
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

	/**
	 * For some unknown reason the brick stays hovering and invisible after MotionEvent.ACTION_DOWN.
	 * This behavior appears only in a test, not in the application itself.
	 */
	@Smoke
	public void testMoveBrickAcrossScript() {
		//		ScriptActivity activity = (ScriptActivity) solo.getCurrentActivity();
		//		ScriptFragment fragment = (ScriptFragment) activity.getFragment(ScriptActivity.FRAGMENT_SCRIPTS);
		//		BrickAdapter adapter = fragment.getAdapter();

		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 1);
		assertTrue("Test project brick list smaller than expected", yPositionList.size() >= 6);

		int numberOfBricks = ProjectManager.getInstance().getCurrentScript().getBrickList().size();
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(6), 10, yPositionList.get(2), 20);
		assertTrue("Number of Bricks inside Script hasn't changed", (numberOfBricks + 1) == ProjectManager
				.getInstance().getCurrentScript().getBrickList().size());
		longClickAndDrag(10, yPositionList.get(6), 10, yPositionList.get(2), 20);
		//		assertTrue("Number of Bricks inside Script hasn't changed", (numberOfBricks - 1) == ProjectManager
		//				.getInstance().getCurrentSprite().getScript(0).getBrickList().size());
		//
		//		assertEquals("Incorrect Brick after dragging over Script", (Brick) adapter.getItem(7) instanceof WaitBrick,
		//				true);
	}

	private void createProject(String projectName) {
		double size = 0.8;

		Project project = new Project(null, projectName);
		firstSprite = new Sprite("cat");

		Script startScript1 = new StartScript(firstSprite);
		Script whenScript1 = new WhenScript(firstSprite);
		Script whenScript2 = new WhenScript(firstSprite);

		brickListToCheck = new ArrayList<Brick>();
		brickListToCheck.add(new HideBrick(firstSprite));
		brickListToCheck.add(new ShowBrick(firstSprite));
		brickListToCheck.add(new SetSizeToBrick(firstSprite, size));
		brickListToCheck.add(new WaitBrick(firstSprite, 100));

		secondBrickListForMoving = new ArrayList<Brick>();
		secondBrickListForMoving.add(new ShowBrick(firstSprite));
		secondBrickListForMoving.add(new WaitBrick(firstSprite, 200));
		secondBrickListForMoving.add(new SetBrightnessBrick(firstSprite, 2.0));
		secondBrickListForMoving.add(new SetXBrick(firstSprite, 100));
		secondBrickListForMoving.add(new SetSizeToBrick(firstSprite, size));

		// adding Bricks: ----------------
		for (Brick brick : brickListToCheck) {
			startScript1.addBrick(brick);
		}

		for (Brick brick : secondBrickListForMoving) {
			whenScript1.addBrick(brick);
		}

		whenScript2.addBrick(new WaitBrick(firstSprite, 300));
		whenScript2.addBrick(new ShowBrick(firstSprite));
		// -------------------------------

		firstSprite.addScript(startScript1);
		firstSprite.addScript(whenScript1);
		firstSprite.addScript(whenScript2);

		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
	}

}
