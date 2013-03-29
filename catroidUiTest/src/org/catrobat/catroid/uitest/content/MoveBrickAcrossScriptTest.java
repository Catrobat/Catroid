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
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;

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
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	@Smoke
	public void testMoveBrickAcrossScript() {
		ScriptActivity activity = (ScriptActivity) solo.getCurrentActivity();
		ScriptFragment fragment = (ScriptFragment) activity.getFragment(ScriptActivity.FRAGMENT_SCRIPTS);
		BrickAdapter adapter = fragment.getAdapter();

		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 1);
		assertTrue("Test project brick list smaller than expected", yPositionList.size() >= 4);

		int numberOfBricks = ProjectManager.getInstance().getCurrentScript().getBrickList().size();
		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(2), 10, yPositionList.get(5), 10);
		assertTrue("Number of Bricks inside Script hasn't changed", (numberOfBricks - 1) == ProjectManager
				.getInstance().getCurrentScript().getBrickList().size());
		assertEquals("Incorrect Brick after dragging over Script", (Brick) adapter.getItem(4) instanceof WaitBrick,
				true);
	}

	private void createProject(String projectName) {
		double size = 0.8;

		Project project = new Project(null, projectName);
		firstSprite = new Sprite("cat");

		Script startScript1 = new StartScript(firstSprite);
		Script whenScript1 = new WhenScript(firstSprite);
		Script whenScript2 = new WhenScript(firstSprite);

		brickListToCheck = new ArrayList<Brick>();
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
