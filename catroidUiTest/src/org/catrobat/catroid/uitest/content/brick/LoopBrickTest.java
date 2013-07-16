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
package org.catrobat.catroid.uitest.content.brick;

import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
import org.catrobat.catroid.content.bricks.NestingBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.widget.ListView;

public class LoopBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private Project project;

	public LoopBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Override
	public void tearDown() throws Exception {
		// normally super.teardown should be called last
		// but tests crashed with Nullpointer
		super.tearDown();
		ProjectManager.getInstance().deleteCurrentProject();
	}

	public void testRepeatBrick() {
		ArrayList<Integer> yPosition;
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(1), 10, yPosition.get(4) + 20, 20);
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(1) instanceof LoopBeginBrick));

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(3), 10, yPosition.get(0), 20);
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(2) instanceof LoopEndBrick));

		// just to get focus
		// seems to be a bug just with the Nexus S 2.3.6
		String spinnerScripts = solo.getString(R.string.scripts);
		solo.clickOnText(spinnerScripts);
		solo.clickOnText(spinnerScripts);
		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(2), 10, yPosition.get(0), 20);

		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance - expected LoopBeginBrick but was "
				+ projectBrickList.get(0).getClass().getSimpleName(), projectBrickList.get(0) instanceof LoopBeginBrick);

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(3), 10, yPosition.get(4) + 20, 20);
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof LoopEndBrick);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(solo.getString(R.string.category_control));
		solo.searchText(solo.getString(R.string.category_control));
		solo.clickOnText(solo.getString(R.string.brick_broadcast_receive));

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		int addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		assertEquals("Incorrect number of Scripts.", 2, sprite.getNumberOfScripts());

		solo.goBack();

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		solo.clickOnScreen(20, yPosition.get(3));
		clickOnDeleteInDialog();

		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ChangeYByNBrick);

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(1), 10, yPosition.get(2) + 20, 20);
		assertEquals("Incorrect number of bricks.", 0, projectBrickList.size());
		projectBrickList = project.getSpriteList().get(0).getScript(1).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ChangeYByNBrick);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(solo.getString(R.string.category_control));
		solo.searchText(solo.getString(R.string.category_control));
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);
		solo.clickOnText(solo.getString(R.string.brick_repeat));

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);
		solo.drag(20, 20, addedYPosition, yPosition.get(3) + 20, 20);

		UiTestUtils.addNewBrick(solo, R.string.brick_set_look);
		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);
		solo.drag(20, 20, addedYPosition, yPosition.get(5) + 20, 20);

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(4), 10, yPosition.get(5) + 20, 20);
		projectBrickList = project.getSpriteList().get(0).getScript(1).getBrickList();

		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof SetLookBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof LoopEndBrick);
	}

	public void testForeverBrick() {
		ArrayList<Integer> yPosition;
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		int addedYPosition;
		float foreverBrickPosition = 500;

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(solo.getString(R.string.category_control));
		solo.searchText(solo.getString(R.string.category_control));
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);
		solo.clickOnText(solo.getString(R.string.brick_forever));

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, 0, 20);
		solo.sleep(200);

		assertEquals("Incorrect number of bricks.", 5, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ForeverBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof LoopEndlessBrick);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(solo.getString(R.string.category_control));
		solo.searchText(solo.getString(R.string.category_control));
		fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);
		solo.clickOnScreen(200, foreverBrickPosition);

		solo.sleep(500);
		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, yPosition.get(2) + 20, 20);
		solo.sleep(200);

		assertEquals("Incorrect number of bricks.", 7, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof ForeverBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof LoopEndlessBrick);
		assertEquals("Wrong LoopBegin-Brick instance", ((NestingBrick) projectBrickList.get(4))
				.getAllNestingBrickParts(false).get(1), projectBrickList.get(2));
		assertEquals("Wrong LoopEnd-Brick instance",
				((NestingBrick) projectBrickList.get(2)).getAllNestingBrickParts(false).get(1), projectBrickList.get(4));

		UiTestUtils.addNewBrick(solo, R.string.brick_change_brightness);
		solo.sleep(500);
		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, yPosition.get(3) + 20, 20);
		solo.sleep(500);

		assertEquals("Incorrect number of bricks.", 8, projectBrickList.size());
		assertTrue("Wrong Brick instance. expected 4, bricklist: " + projectBrickList.toString(),
				projectBrickList.get(4) instanceof ChangeBrightnessByNBrick);

		solo.scrollDownList(0);

		UiTestUtils.addNewBrick(solo, R.string.brick_broadcast);
		solo.sleep(500);
		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, yPosition.get(4), 20);
		solo.sleep(500);

		assertEquals("Incorrect number of bricks.", 9, projectBrickList.size());
		assertTrue("Wrong Brick instance. expected 5, bricklist: " + projectBrickList.toString(),
				projectBrickList.get(5) instanceof BroadcastBrick);
	}

	public void testNestedForeverBricks() {
		ArrayList<Integer> yPosition;
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		int addedYPosition;
		float foreverBrickPosition = 500;

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		solo.clickOnScreen(20, yPosition.get(1));
		clickOnDeleteInDialog();
		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		solo.clickOnScreen(20, yPosition.get(1));
		clickOnDeleteInDialog();

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(solo.getString(R.string.category_looks));
		solo.searchText(solo.getString(R.string.category_looks));
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);
		solo.clickOnText(solo.getString(R.string.brick_clear_graphic_effect));

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, 0, 20);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(solo.getString(R.string.category_control));
		solo.searchText(solo.getString(R.string.category_control));
		fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);
		solo.clickOnScreen(200, foreverBrickPosition);

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, yPosition.get(0), 20);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(solo.getString(R.string.category_control));
		solo.searchText(solo.getString(R.string.category_control));
		fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);
		solo.clickOnScreen(200, foreverBrickPosition);

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, yPosition.get(2), 20);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(solo.getString(R.string.category_control));
		solo.searchText(solo.getString(R.string.category_control));
		fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);
		solo.clickOnScreen(200, foreverBrickPosition);

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, yPosition.get(2), 20);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(solo.getString(R.string.category_control));
		solo.searchText(solo.getString(R.string.category_control));
		fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);
		solo.clickOnScreen(200, foreverBrickPosition);

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, yPosition.get(1), 20);
		solo.sleep(1000);

		checkIfForeverLoopsAreCorrectlyPlaced(0);
		checkIfForeverLoopsAreCorrectlyPlaced(1);
		checkIfForeverLoopsAreCorrectlyPlaced(2);
		checkIfForeverLoopsAreCorrectlyPlaced(3);

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);

		solo.clickOnScreen(20, yPosition.get(0)); // needed because of bug? in Nexus S 2.3.6
		solo.clickOnScreen(20, yPosition.get(1));
		clickOnDeleteInDialog();

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 20, yPosition.get(4), 20, yPosition.get(yPosition.size() - 4) - 20, 20);

		assertTrue("Wrong brick instance. expected 2, bricklist: " + projectBrickList.toString(),
				projectBrickList.get(2) instanceof ClearGraphicEffectBrick);

		UiTestUtils.longClickAndDrag(solo, 20, yPosition.get(2), 20, yPosition.get(0), 20);
		solo.scrollToBottom();
		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 20, yPosition.get(2), 20, yPosition.get(yPosition.size() - 1) + 50, 20);
		assertEquals("Wrong number of bricks", 7, projectBrickList.size());

		projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertTrue("Wrong brick instance. expected 2, bricklist: " + projectBrickList.toString(),
				projectBrickList.get(2) instanceof ClearGraphicEffectBrick);

		checkIfForeverLoopsAreCorrectlyPlaced(0);
		checkIfForeverLoopsAreCorrectlyPlaced(1);
		checkIfForeverLoopsAreCorrectlyPlaced(3);
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
		script.addBrick(new ChangeYByNBrick(sprite, -10));
		script.addBrick(endBrick);

		sprite.addScript(script);
		sprite.addScript(new StartScript(sprite));
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	private void clickOnDeleteInDialog() {
		if (!solo.waitForText(solo.getString(R.string.brick_context_dialog_delete_brick), 0, 5000)) {
			fail("Text not shown in 5 secs!");
		}
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.clickOnText(solo.getString(R.string.yes));
		if (!solo.waitForView(ListView.class, 0, 5000)) {
			fail("Dialog does not close in 5 sec!");
		}
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
				- offset)).getAllNestingBrickParts(false).get(1), projectBrickList.get(position));
		assertEquals("Wrong LoopEnd-Brick instance", ((NestingBrick) projectBrickList.get(position))
				.getAllNestingBrickParts(false).get(1), projectBrickList.get(projectBrickList.size() - offset));
	}
}
