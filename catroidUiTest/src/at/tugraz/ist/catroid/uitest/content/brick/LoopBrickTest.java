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
package at.tugraz.ist.catroid.uitest.content.brick;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.BroadcastBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeBrightnessBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeYByBrick;
import at.tugraz.ist.catroid.content.bricks.ClearGraphicEffectBrick;
import at.tugraz.ist.catroid.content.bricks.ForeverBrick;
import at.tugraz.ist.catroid.content.bricks.LoopBeginBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndlessBrick;
import at.tugraz.ist.catroid.content.bricks.NestingBrick;
import at.tugraz.ist.catroid.content.bricks.RepeatBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class LoopBrickTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private Project project;

	public LoopBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptTabActivityFromMainMenu(solo);
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		ProjectManager.getInstance().deleteCurrentProject();
		UiTestUtils.clearAllUtilTestProjects();

		super.tearDown();
	}

	public void testRepeatBrick() {
		ArrayList<Integer> yPos;
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();

		yPos = UiTestUtils.getListItemYPositions(solo);
		UiTestUtils.longClickAndDrag(solo, 10, yPos.get(1), 10, yPos.get(4) + 20, 20);
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(1) instanceof LoopBeginBrick));

		yPos = UiTestUtils.getListItemYPositions(solo);
		UiTestUtils.longClickAndDrag(solo, 10, yPos.get(3), 10, yPos.get(0), 20);
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(2) instanceof LoopEndBrick));

		yPos = UiTestUtils.getListItemYPositions(solo);
		UiTestUtils.longClickAndDrag(solo, 10, yPos.get(2), 10, 0, 20);

		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance - expected LoopBeginBrick but was "
				+ projectBrickList.get(0).getClass().getSimpleName(),
				(projectBrickList.get(0) instanceof LoopBeginBrick));

		yPos = UiTestUtils.getListItemYPositions(solo);
		UiTestUtils.longClickAndDrag(solo, 10, yPos.get(3), 10, yPos.get(4) + 20, 20);
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(2) instanceof LoopEndBrick));

		UiTestUtils.addNewBrick(solo, R.string.brick_broadcast_receive);
		yPos = UiTestUtils.getListItemYPositions(solo);
		int addedYPos = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPos, yPos.get(2), 20);

		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		assertEquals("Incorrect number of Scripts.", 2, sprite.getNumberOfScripts());

		solo.goBack();

		yPos = UiTestUtils.getListItemYPositions(solo);
		solo.clickOnScreen(20, yPos.get(3));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));

		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(0) instanceof ChangeYByBrick));

		yPos = UiTestUtils.getListItemYPositions(solo);
		UiTestUtils.longClickAndDrag(solo, 10, yPos.get(1), 10, yPos.get(2) + 20, 20);
		assertEquals("Incorrect number of bricks.", 0, projectBrickList.size());
		projectBrickList = project.getSpriteList().get(0).getScript(1).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(0) instanceof ChangeYByBrick));

		UiTestUtils.addNewBrick(solo, R.string.brick_repeat);
		yPos = UiTestUtils.getListItemYPositions(solo);
		addedYPos = UiTestUtils.getAddedListItemYPosition(solo);
		solo.drag(20, 20, addedYPos, yPos.get(3) + 20, 20);

		UiTestUtils.addNewBrick(solo, R.string.brick_set_costume);
		yPos = UiTestUtils.getListItemYPositions(solo);
		addedYPos = UiTestUtils.getAddedListItemYPosition(solo);
		solo.drag(20, 20, addedYPos, yPos.get(5) + 20, 20);

		yPos = UiTestUtils.getListItemYPositions(solo);
		UiTestUtils.longClickAndDrag(solo, 10, yPos.get(4), 10, yPos.get(5) + 20, 20);
		projectBrickList = project.getSpriteList().get(0).getScript(1).getBrickList();

		assertTrue("Wrong Brick instance.", (projectBrickList.get(2) instanceof SetCostumeBrick));
		assertTrue("Wrong Brick instance.", (projectBrickList.get(3) instanceof LoopEndBrick));

	}

	public void testForeverBrick() {
		ArrayList<Integer> yPos;
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		int addedYPos;

		UiTestUtils.addNewBrick(solo, R.string.brick_forever);
		yPos = UiTestUtils.getListItemYPositions(solo);
		addedYPos = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPos, 0, 20);
		solo.sleep(200);

		assertEquals("Incorrect number of bricks.", 5, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ForeverBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof LoopEndlessBrick);

		UiTestUtils.addNewBrick(solo, R.string.brick_forever);
		solo.sleep(500);
		yPos = UiTestUtils.getListItemYPositions(solo);
		addedYPos = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPos, yPos.get(3), 20);
		solo.sleep(200);

		assertEquals("Incorrect number of bricks.", 7, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof ForeverBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof LoopEndlessBrick);
		assertEquals("Wrong LoopBegin-Brick instance", ((NestingBrick) projectBrickList.get(4))
				.getAllNestingBrickParts().get(0), projectBrickList.get(2));
		assertEquals("Wrong LoopEnd-Brick instance", ((NestingBrick) projectBrickList.get(2)).getAllNestingBrickParts()
				.get(1), projectBrickList.get(4));

		UiTestUtils.addNewBrick(solo, R.string.brick_change_brightness);
		solo.sleep(500);
		yPos = UiTestUtils.getListItemYPositions(solo);
		addedYPos = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPos, yPos.get(6) + 20, 20);
		solo.sleep(200);

		assertEquals("Incorrect number of bricks.", 8, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof ChangeBrightnessBrick);

		solo.scrollDownList(0);

		UiTestUtils.addNewBrick(solo, R.string.brick_broadcast);
		solo.sleep(500);
		yPos = UiTestUtils.getListItemYPositions(solo);
		addedYPos = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPos, yPos.get(7) + 20, 20);
		solo.sleep(200);

		assertEquals("Incorrect number of bricks.", 9, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(7) instanceof BroadcastBrick);
	}

	public void testNestedForeverBricks() {
		ArrayList<Integer> yPos;
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		int addedYPos;

		yPos = UiTestUtils.getListItemYPositions(solo);
		solo.clickOnScreen(20, yPos.get(1));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.waitForDialogToClose(1000);
		yPos = UiTestUtils.getListItemYPositions(solo);
		solo.clickOnScreen(20, yPos.get(1));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));

		UiTestUtils.addNewBrick(solo, R.string.brick_clear_graphic_effect);
		yPos = UiTestUtils.getListItemYPositions(solo);
		addedYPos = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPos, 0, 20);

		UiTestUtils.addNewBrick(solo, R.string.brick_forever);
		yPos = UiTestUtils.getListItemYPositions(solo);
		addedYPos = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPos, yPos.get(0), 20);

		UiTestUtils.addNewBrick(solo, R.string.brick_forever);
		yPos = UiTestUtils.getListItemYPositions(solo);
		addedYPos = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPos, yPos.get(2), 20);

		UiTestUtils.addNewBrick(solo, R.string.brick_forever);
		yPos = UiTestUtils.getListItemYPositions(solo);
		addedYPos = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPos, yPos.get(2), 20);

		UiTestUtils.addNewBrick(solo, R.string.brick_forever);
		yPos = UiTestUtils.getListItemYPositions(solo);
		addedYPos = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPos, yPos.get(1), 20);
		solo.sleep(1000);

		checkIfForeverLoopsAreCorrectlyPlaced(0);
		checkIfForeverLoopsAreCorrectlyPlaced(1);
		checkIfForeverLoopsAreCorrectlyPlaced(2);
		checkIfForeverLoopsAreCorrectlyPlaced(3);

		yPos = UiTestUtils.getListItemYPositions(solo);
		solo.clickOnScreen(20, yPos.get(1));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));

		yPos = UiTestUtils.getListItemYPositions(solo);
		UiTestUtils.longClickAndDrag(solo, 20, yPos.get(4), 20, yPos.get(yPos.size() - 3), 20);

		assertTrue("Wrong brick instance.", projectBrickList.get(3) instanceof ClearGraphicEffectBrick);

		UiTestUtils.longClickAndDrag(solo, 20, yPos.get(4), 20, yPos.get(yPos.size() - 2) + 20, 20);
		assertEquals("Wrong number of bricks", 6, projectBrickList.size());

		projectBrickList = project.getSpriteList().get(0).getScript(1).getBrickList();
		assertTrue("Wrong brick instance.", projectBrickList.get(0) instanceof ClearGraphicEffectBrick);

		checkIfForeverLoopsAreCorrectlyPlaced(0);
		checkIfForeverLoopsAreCorrectlyPlaced(1);
		checkIfForeverLoopsAreCorrectlyPlaced(2);
	}

	private void createProject() {
		LoopBeginBrick beginBrick;
		LoopEndBrick endBrick;

		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);

		beginBrick = new RepeatBrick(sprite, 3);
		endBrick = new LoopEndBrick(sprite, beginBrick);

		script.addBrick(beginBrick);
		script.addBrick(new ChangeYByBrick(sprite, -10));
		script.addBrick(endBrick);

		sprite.addScript(script);
		sprite.addScript(new StartScript(sprite));
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	private void checkIfForeverLoopsAreCorrectlyPlaced(int position) {
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		int offset = 1;
		for (int i = 0; i < position; i++) {
			if (projectBrickList.get(i) instanceof ForeverBrick) {
				offset++;
			}
		}

		assertEquals("Wrong LoopBegin-Brick instance", ((NestingBrick) projectBrickList.get(projectBrickList.size()
				- offset)).getAllNestingBrickParts().get(0), projectBrickList.get(position));
		assertEquals("Wrong LoopEnd-Brick instance", ((NestingBrick) projectBrickList.get(position))
				.getAllNestingBrickParts().get(1), projectBrickList.get(projectBrickList.size() - offset));
	}
}
