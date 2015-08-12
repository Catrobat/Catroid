/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.brick;

import android.util.Log;
import android.widget.CheckBox;
import android.widget.ListView;

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
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class IfBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final String TAG = IfBrickTest.class.getSimpleName();
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

	public void testIfBrick() {
		ListView view = UiTestUtils.getScriptListView(solo);
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) view.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();

		UiTestUtils.testBrickWithFormulaEditor(solo, ProjectManager.getInstance().getCurrentSprite(),
				R.id.brick_if_begin_edit_text, 5, Brick.BrickField.IF_CONDITION, ifBrick);

		assertEquals("Incorrect number of bricks.", 6, dragDropListView.getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 0, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());

		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof IfLogicBeginBrick);
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.brick_if_begin)));
	}

	public void testStrings() {
		solo.clickOnView(solo.getView(R.id.brick_if_begin_edit_text));
		solo.sleep(100);

		boolean isFound = solo.searchText(solo.getString(R.string.brick_if_begin_second_part));
		assertTrue("String: " + getActivity().getString(R.string.brick_if_begin_second_part) + " not found!", isFound);

		isFound = solo.searchText(solo.getString(R.string.brick_if_begin));
		assertTrue("String: " + getActivity().getString(R.string.brick_if_begin) + " not found!", isFound);
	}

	public void testIfBrickParts() {
		int dragAndDropSteps = 100;
		ArrayList<Integer> yPosition;
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		Log.d(TAG, "Before drag item 1 to item 4 + 20");
		logBrickListForJenkins(projectBrickList);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);

		int oldYto = yPosition.get(4) + 20;
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(1), 10, oldYto, dragAndDropSteps);

		boolean result = solo.waitForLogMessage("longClickAndDrag finished: " + oldYto, 1000);
		assertTrue("Timeout during longClickAndDrag on item 1! y-Coordinate: " + oldYto, result);

		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(1) instanceof IfLogicBeginBrick));

		Log.d(TAG, "Before drag item 2 to item 0");
		logBrickListForJenkins(projectBrickList);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		oldYto = yPosition.get(0);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(2), 10, oldYto, dragAndDropSteps);

		result = solo.waitForLogMessage("longClickAndDrag finished: " + oldYto, 1000);
		assertTrue("Timeout during longClickAndDrag on item 2! y-Coordinate: " + oldYto, result);

		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(0) instanceof IfLogicBeginBrick));

		Log.d(TAG, "Before drag item 3 to item 0");
		logBrickListForJenkins(projectBrickList);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		oldYto = yPosition.get(0);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(3), 10, oldYto, dragAndDropSteps);

		result = solo.waitForLogMessage("longClickAndDrag finished: " + oldYto, 1000);
		assertTrue("Timeout during longClickAndDrag on item 3! y-Coordinate: " + oldYto, result);

		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());
		assertTrue("Wrong Brick instance - expected IfElseBrick but was "
						+ projectBrickList.get(1).getClass().getSimpleName(),
				projectBrickList.get(1) instanceof IfLogicElseBrick);

		assertTrue("Wrong Brick instance - expected ChangeYByNBrick but was "
						+ projectBrickList.get(2).getClass().getSimpleName(),
				projectBrickList.get(2) instanceof ChangeYByNBrick
		);

		Log.d(TAG, "Before drag item 4 to item 0");
		logBrickListForJenkins(projectBrickList);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		oldYto = yPosition.get(0);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(4) - 10, 10, oldYto, dragAndDropSteps);
		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());

		result = solo.waitForLogMessage("longClickAndDrag finished: " + oldYto, 1000);
		assertTrue("Timeout during longClickAndDrag on item 4! y-Coordinate: " + oldYto, result);

		Log.d(TAG, "After drag item 4 to item 0");
		logBrickListForJenkins(projectBrickList);

		assertTrue("Wrong Brick instance, expected IfLogicEndBrick but was "
						+ projectBrickList.get(2).getClass().getSimpleName(),
				projectBrickList.get(2) instanceof IfLogicEndBrick);

		UiTestUtils.addNewBrick(solo, R.string.brick_broadcast_receive);
		yPosition = UiTestUtils.getListItemYPositions(solo, 0);

		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		assertEquals("Incorrect number of Scripts.", 2, sprite.getNumberOfScripts());

		solo.goBack();

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		solo.clickOnScreen(20, yPosition.get(3));
		clickOnDeleteInDialog();

		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ChangeYByNBrick);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		oldYto = yPosition.get(2) + 20;
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(1), 10, oldYto, dragAndDropSteps);

		result = solo.waitForLogMessage("longClickAndDrag finished: " + oldYto, 1000);
		assertTrue("Timeout during longClickAndDrag! y-Coordinate: " + oldYto, result);

		assertEquals("Incorrect number of bricks.", 0, projectBrickList.size());
		projectBrickList = project.getSpriteList().get(0).getScript(1).getBrickList();

		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ChangeYByNBrick);

		UiTestUtils.addNewBrick(solo, R.string.brick_if_begin);
		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		int addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);
		solo.drag(20, 20, addedYPosition, yPosition.get(3) + 20, dragAndDropSteps);

		UiTestUtils.addNewBrick(solo, R.string.brick_set_look);
		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);
		solo.drag(20, 20, addedYPosition, yPosition.get(5) + 20, dragAndDropSteps);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		oldYto = yPosition.get(5) + 20;
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(4), 10, oldYto, dragAndDropSteps);
		projectBrickList = project.getSpriteList().get(0).getScript(1).getBrickList();

		result = solo.waitForLogMessage("longClickAndDrag finished: " + oldYto, 1000);
		assertTrue("Timeout during longClickAndDrag! y-Coordinate: " + oldYto, result);

		Log.d(TAG, "Final order of bricks");
		logBrickListForJenkins(projectBrickList);

		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ChangeYByNBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof IfLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof SetLookBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof IfLogicElseBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof IfLogicEndBrick);
	}

	public void testCopyIfLogicBeginBrickActionMode() {
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		solo.clickOnCheckBox(1);
		UiTestUtils.acceptAndCloseActionMode(solo);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 7, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof IfLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof ChangeYByNBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof IfLogicElseBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof IfLogicEndBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof IfLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(5) instanceof IfLogicElseBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(6) instanceof IfLogicEndBrick);
	}

	public void testCopyIfLogicElseBrickActionMode() {
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		solo.clickOnCheckBox(3);
		UiTestUtils.acceptAndCloseActionMode(solo);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 7, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof IfLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof ChangeYByNBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof IfLogicElseBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof IfLogicEndBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof IfLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(5) instanceof IfLogicElseBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(6) instanceof IfLogicEndBrick);
	}

	public void testCopyIfLogicEndBrickActionMode() {
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		solo.clickOnCheckBox(4);
		UiTestUtils.acceptAndCloseActionMode(solo);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 7, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof IfLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof ChangeYByNBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof IfLogicElseBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof IfLogicEndBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof IfLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(5) instanceof IfLogicElseBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(6) instanceof IfLogicEndBrick);
	}

	public void testSelectionAfterCopyActionMode() {
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		solo.clickOnCheckBox(1);

		UiTestUtils.acceptAndCloseActionMode(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		solo.clickOnCheckBox(6);

		CheckBox firstIfLogicBeginBrickCheckBox = (CheckBox) solo.getView(R.id.brick_if_begin_checkbox, 0);
		CheckBox secondIfLogicBeginBrickCheckBox = (CheckBox) solo.getView(R.id.brick_if_begin_checkbox, 1);
		CheckBox firstIfLogicElseBrickCheckBox = (CheckBox) solo.getView(R.id.brick_if_else_checkbox, 0);
		CheckBox secondIfLogicElseBrickCheckBox = (CheckBox) solo.getView(R.id.brick_if_else_checkbox, 1);
		// Solo doesn't scroll automatically to get those views
		solo.scrollToBottom();
		CheckBox firstIfLogicEndBrickCheckBox = (CheckBox) solo.getView(R.id.brick_if_end_if_checkbox, 0);
		CheckBox secondIfLogicEndBrickCheckBox = (CheckBox) solo.getView(R.id.brick_if_end_if_checkbox, 1);

		assertFalse("CheckBox is checked but shouldn't be.", firstIfLogicBeginBrickCheckBox.isChecked());
		assertTrue("CheckBox is not checked but should be.", secondIfLogicBeginBrickCheckBox.isChecked());
		assertFalse("CheckBox is checked but shouldn't be.", firstIfLogicElseBrickCheckBox.isChecked());
		assertTrue("CheckBox is not checked but should be.", secondIfLogicElseBrickCheckBox.isChecked());
		assertFalse("CheckBox is checked but shouldn't be.", firstIfLogicEndBrickCheckBox.isChecked());
		assertTrue("CheckBox is not checked but should be.", secondIfLogicEndBrickCheckBox.isChecked());
	}

	public void testSelectionActionMode() {
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		UiTestUtils.clickOnCheckBox(solo, 1);

		CheckBox ifLogicBeginBrickCheckbox = (CheckBox) solo.getView(R.id.brick_if_begin_checkbox);
		CheckBox ifLogicElseBrickCheckbox = (CheckBox) solo.getView(R.id.brick_if_else_checkbox);
		CheckBox ifLogicEndBrickCheckbox = (CheckBox) solo.getView(R.id.brick_if_end_if_checkbox);
		CheckBox changeYByNBrickCheckbox = (CheckBox) solo.getView(R.id.brick_change_y_checkbox);

		assertTrue("CheckBox is not checked but shouldn be.", ifLogicBeginBrickCheckbox.isChecked()
				&& ifLogicElseBrickCheckbox.isChecked() && ifLogicEndBrickCheckbox.isChecked());
		assertFalse("CheckBox is checked but shouldn't be.", changeYByNBrickCheckbox.isChecked());

		UiTestUtils.acceptAndCloseActionMode(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		UiTestUtils.clickOnCheckBox(solo, 1);

		ifLogicBeginBrickCheckbox = (CheckBox) solo.getView(R.id.brick_if_begin_checkbox);
		ifLogicElseBrickCheckbox = (CheckBox) solo.getView(R.id.brick_if_else_checkbox);
		ifLogicEndBrickCheckbox = (CheckBox) solo.getView(R.id.brick_if_end_if_checkbox);
		changeYByNBrickCheckbox = (CheckBox) solo.getView(R.id.brick_change_y_checkbox);

		assertTrue("CheckBox is not checked but shouldn be.", ifLogicBeginBrickCheckbox.isChecked()
				&& ifLogicElseBrickCheckbox.isChecked() && ifLogicEndBrickCheckbox.isChecked());
		assertFalse("CheckBox is checked but shouldn't be.", changeYByNBrickCheckbox.isChecked());
	}

	private void logBrickListForJenkins(ArrayList<Brick> projectBrickList) {
		for (Brick brick : projectBrickList) {
			Log.d(TAG, "Brick at Positon " + projectBrickList.indexOf(brick) + ": " + brick.getClass().getSimpleName());
		}
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript();
		ifBrick = new IfLogicBeginBrick(0);
		IfLogicElseBrick ifElseBrick = new IfLogicElseBrick(ifBrick);
		IfLogicEndBrick ifEndBrick = new IfLogicEndBrick(ifElseBrick, ifBrick);
		ifBrick.setIfElseBrick(ifElseBrick);
		ifBrick.setIfEndBrick(ifEndBrick);

		script.addBrick(ifBrick);
		script.addBrick(new ChangeYByNBrick(-10));
		script.addBrick(ifElseBrick);
		script.addBrick(ifEndBrick);

		sprite.addScript(script);
		sprite.addScript(new StartScript());
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
}
