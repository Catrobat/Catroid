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
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.widget.ListView;

public class IfBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private Project project;
	private IfLogicBeginBrick ifBrick;

	public IfBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Smoke
	public void testIfBrick() {
		ListView view = UiTestUtils.getScriptListView(solo);
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) view.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();

		UiTestUtils.testBrickWithFormulaEditor(solo, 0, 1, 5, "ifCondition", ifBrick);

		assertEquals("Incorrect number of bricks.", 6, dragDropListView.getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 0, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());

		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof IfLogicBeginBrick);
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.brick_if_begin)));
	}

	public void testStrings() {

		solo.clickOnEditText(0);
		solo.sleep(100);

		boolean isFound = solo.searchText(solo.getString(R.string.brick_if_begin_second_part));
		assertTrue("String: " + getActivity().getString(R.string.brick_if_begin_second_part) + " not found!", isFound);

		isFound = solo.searchText(solo.getString(R.string.brick_if_begin));
		assertTrue("String: " + getActivity().getString(R.string.brick_if_begin) + " not found!", isFound);

		solo.goBack();
		solo.goBack();
	}

	public void testIfBrickParts() {
		ArrayList<Integer> yPosition;
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		Log.e("info", "Befor drag item 1 to item 4 + 20");
		logBrickListForJenkins(projectBrickList);

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(1), 10, yPosition.get(4) + 20, 20);
		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(1) instanceof IfLogicBeginBrick));

		Log.e("info", "Befor drag item 2 to item 0");
		logBrickListForJenkins(projectBrickList);

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(2), 10, yPosition.get(0), 20);
		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(0) instanceof IfLogicBeginBrick));

		// just to get focus
		// seems to be a bug just with the Nexus S 2.3.6
		String spinnerScripts = solo.getString(R.string.scripts);
		solo.clickOnText(spinnerScripts);
		solo.clickOnText(spinnerScripts);

		Log.e("info", "Befor drag item 3 to item 0");
		logBrickListForJenkins(projectBrickList);

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(3), 10, yPosition.get(0), 20);

		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());
		assertTrue("Wrong Brick instance - expected IfElseBrick but was "
				+ projectBrickList.get(1).getClass().getSimpleName(),
				projectBrickList.get(1) instanceof IfLogicElseBrick);

		assertTrue("Wrong Brick instance - expected ChangeYByNBrick but was "
				+ projectBrickList.get(2).getClass().getSimpleName(),
				projectBrickList.get(2) instanceof ChangeYByNBrick);

		Log.e("info", "Befor drag item 4 to item 0");
		logBrickListForJenkins(projectBrickList);

		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(4) - 10, 10, yPosition.get(0), 20);
		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());

		Log.e("info", "After drag item 4 to item 0");
		logBrickListForJenkins(projectBrickList);

		//TODO Test commented lines on local test-device in order to find strange jenkins error
		// junit.framework.AssertionFailedError: Wrong Brick instance, expected IfLogicEndBrick but was ChangeYByNBrick
		// assert below!

		//
		//		assertTrue("Wrong Brick instance, expected IfLogicEndBrick but was "
		//				+ projectBrickList.get(2).getClass().getSimpleName(),
		//				projectBrickList.get(2) instanceof IfLogicEndBrick);
		//

		//		UiTestUtils.addNewBrick(solo, R.string.brick_broadcast_receive);
		//		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		//		int addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);
		//
		//		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		//		assertEquals("Incorrect number of Scripts.", 2, sprite.getNumberOfScripts());
		//
		//		solo.goBack();
		//
		//		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		//		solo.clickOnScreen(20, yPosition.get(3));
		//		clickOnDeleteInDialog();
		//
		//		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		//		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ChangeYByNBrick);
		//
		//		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		//		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(1), 10, yPosition.get(2) + 20, 20);
		//		assertEquals("Incorrect number of bricks.", 0, projectBrickList.size());
		//		projectBrickList = project.getSpriteList().get(0).getScript(1).getBrickList();
		//		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		//		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ChangeYByNBrick);
		//
		//		UiTestUtils.addNewBrick(solo, R.string.brick_if_begin);
		//		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		//		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);
		//		solo.drag(20, 20, addedYPosition, yPosition.get(3) + 20, 20);
		//
		//		UiTestUtils.addNewBrick(solo, R.string.brick_set_look);
		//		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		//		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);
		//		solo.drag(20, 20, addedYPosition, yPosition.get(5) + 20, 20);
		//
		//		yPosition = UiTestUtils.getListItemYPositions(solo, 1);
		//		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(4), 10, yPosition.get(5) + 20, 20);
		//		projectBrickList = project.getSpriteList().get(0).getScript(1).getBrickList();
		//
		//		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ChangeYByNBrick);
		//		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof IfLogicBeginBrick);
		//		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof SetLookBrick);
		//		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof IfLogicElseBrick);
		//		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof IfLogicEndBrick);
	}

	private void logBrickListForJenkins(ArrayList<Brick> projectBrickList) {
		for (Brick brick : projectBrickList) {
			Log.e("info", "Brick at Positon " + projectBrickList.indexOf(brick) + ": "
					+ brick.getClass().getSimpleName());
		}
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		ifBrick = new IfLogicBeginBrick(sprite, 0);
		IfLogicElseBrick ifElseBrick = new IfLogicElseBrick(sprite, ifBrick);
		IfLogicEndBrick ifEndBrick = new IfLogicEndBrick(sprite, ifElseBrick, ifBrick);
		ifBrick.setIfElseBrick(ifElseBrick);
		ifBrick.setIfEndBrick(ifEndBrick);

		script.addBrick(ifBrick);
		script.addBrick(new ChangeYByNBrick(sprite, -10));
		script.addBrick(ifElseBrick);
		script.addBrick(ifEndBrick);

		sprite.addScript(script);
		sprite.addScript(new StartScript(sprite));
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	//	private void clickOnDeleteInDialog() {
	//		if (!solo.waitForText(solo.getString(R.string.brick_context_dialog_delete_brick), 0, 5000)) {
	//			fail("Text not shown in 5 secs!");
	//		}
	//		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));
	//		if (!solo.waitForView(ListView.class, 0, 5000)) {
	//			fail("Dialog does not close in 5 sec!");
	//		}
	//	}
}
