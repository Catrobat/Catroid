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

import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
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

import java.util.ArrayList;
import java.util.Locale;

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

	public void testRepeatBrick() {
		ArrayList<Integer> yPosition;
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(1), 10, yPosition.get(4) + 20, 20);
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(1) instanceof LoopBeginBrick));

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(3), 10, yPosition.get(0), 20);
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(2) instanceof LoopEndBrick));

		// just to get focus
		// seems to be a bug just with the Nexus S 2.3.6
		solo.clickOnText(solo.getString(R.string.brick_when_started));
		solo.goBack();

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(2), 10, yPosition.get(0), 20);

		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance - expected LoopBeginBrick but was "
				+ projectBrickList.get(0).getClass().getSimpleName(), projectBrickList.get(0) instanceof LoopBeginBrick);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(3), 10, yPosition.get(4) + 20, 20);
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof LoopEndBrick);

		UiTestUtils.addNewBrick(solo, R.string.category_control, R.string.brick_broadcast_receive);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		int addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		assertEquals("Incorrect number of Scripts.", 2, sprite.getNumberOfScripts());

		solo.goBack();

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		solo.clickOnScreen(20, yPosition.get(3));
		clickOnDeleteInDialog();

		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ChangeYByNBrick);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
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

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);
		solo.drag(20, 20, addedYPosition, yPosition.get(3) + 20, 20);

		UiTestUtils.addNewBrick(solo, R.string.brick_set_look);
		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);
		solo.drag(20, 20, addedYPosition, yPosition.get(5) + 20, 20);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(4), 10, yPosition.get(5) + 20, 20);
		projectBrickList = project.getSpriteList().get(0).getScript(1).getBrickList();

		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof SetLookBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof LoopEndBrick);
	}

	public void testForeverBrick() {
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();

		UiTestUtils.addNewBrick(solo, R.string.category_control, R.string.brick_forever);
		UiTestUtils.dragFloatingBrickUpwards(solo, 1);

		assertEquals("Incorrect number of bricks.", 5, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ForeverBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof LoopEndlessBrick);

		UiTestUtils.addNewBrick(solo, R.string.category_control, R.string.brick_forever);
		UiTestUtils.dragFloatingBrickDownwards(solo, 0);

		assertEquals("Incorrect number of bricks.", 7, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof ForeverBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof LoopEndlessBrick);
		assertEquals("Wrong LoopBegin-Brick instance", ((NestingBrick) projectBrickList.get(4))
				.getAllNestingBrickParts(false).get(1), projectBrickList.get(2));
		assertEquals("Wrong LoopEnd-Brick instance",
				((NestingBrick) projectBrickList.get(2)).getAllNestingBrickParts(false).get(1), projectBrickList.get(4));

		UiTestUtils.addNewBrick(solo, R.string.brick_change_brightness);
		solo.sleep(500);
		UiTestUtils.dragFloatingBrick(solo, 1.25f);

		assertEquals("Incorrect number of bricks.", 8, projectBrickList.size());
		assertTrue("Wrong Brick instance. expected 4=ChangeBrightnessByNBrick, bricklist: "
				+ projectBrickList.toString(), projectBrickList.get(4) instanceof ChangeBrightnessByNBrick);
	}

	public void testNestedForeverBricks() {
		ArrayList<Integer> yPosition;
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		int addedYPosition;

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		solo.clickOnScreen(20, yPosition.get(1));
		clickOnDeleteInDialog();
		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		solo.clickOnScreen(20, yPosition.get(1));
		clickOnDeleteInDialog();

		UiTestUtils.addNewBrick(solo, R.string.category_looks, R.string.brick_clear_graphic_effect);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, 0, 20);

		UiTestUtils.addNewBrick(solo, R.string.category_control, R.string.brick_forever);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, yPosition.get(0), 20);

		UiTestUtils.addNewBrick(solo, R.string.category_control, R.string.brick_forever);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, yPosition.get(2), 20);

		UiTestUtils.addNewBrick(solo, R.string.category_control, R.string.brick_forever);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, yPosition.get(2), 20);

		UiTestUtils.addNewBrick(solo, R.string.category_control, R.string.brick_forever);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, yPosition.get(1), 20);
		solo.sleep(1000);

		checkIfForeverLoopsAreCorrectlyPlaced(0);
		checkIfForeverLoopsAreCorrectlyPlaced(1);
		checkIfForeverLoopsAreCorrectlyPlaced(2);
		checkIfForeverLoopsAreCorrectlyPlaced(3);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);

		//just to gain focus
		solo.clickOnScreen(20, yPosition.get(0)); // needed because of bug? in Nexus S 2.3.6
		solo.goBack();
		solo.clickOnScreen(20, yPosition.get(1));
		clickOnDeleteInDialog();

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 20, yPosition.get(4), 20, yPosition.get(yPosition.size() - 4) - 20, 20);

		assertTrue("Wrong brick instance. expected 2, bricklist: " + projectBrickList.toString(),
				projectBrickList.get(2) instanceof ClearGraphicEffectBrick);

		UiTestUtils.longClickAndDrag(solo, 20, yPosition.get(2), 20, yPosition.get(0), 20);
		solo.scrollToBottom();
		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		UiTestUtils.longClickAndDrag(solo, 20, yPosition.get(2), 20, yPosition.get(yPosition.size() - 1) + 50, 20);
		assertEquals("Wrong number of bricks", 7, projectBrickList.size());

		projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertTrue("Wrong brick instance. expected 2, bricklist: " + projectBrickList.toString(),
				projectBrickList.get(2) instanceof ClearGraphicEffectBrick);

		checkIfForeverLoopsAreCorrectlyPlaced(0);
		checkIfForeverLoopsAreCorrectlyPlaced(1);
		checkIfForeverLoopsAreCorrectlyPlaced(3);
	}

	public void testCopyForeverBrickActionMode() {
		deleteAllBricks();

		UiTestUtils.addNewBrick(solo, R.string.category_control, R.string.brick_forever);
		UiTestUtils.tapFloatingBrick(solo);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 2, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ForeverBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof LoopEndlessBrick);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		UiTestUtils.clickOnCheckBox(solo, 1);
		UiTestUtils.acceptAndCloseActionMode(solo);

		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ForeverBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof LoopEndlessBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof ForeverBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof LoopEndlessBrick);
	}

	public void testLoopEndBrickCheckBoxVisibleActionMode() {
		deleteAllBricks();

		UiTestUtils.addNewBrick(solo, R.string.category_control, R.string.brick_repeat);
		UiTestUtils.dragFloatingBrickDownwards(solo, 0);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());

		boolean isCheckBoxVisible = false;

		for (View currentView : solo.getViews()) {
			if (currentView.getId() == R.id.brick_loop_end_checkbox) {
				isCheckBoxVisible = currentView.getVisibility() == View.VISIBLE;
				break;
			}
		}

		assertTrue("CheckBock is not visible.", isCheckBoxVisible);

		UiTestUtils.acceptAndCloseActionMode(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());

		isCheckBoxVisible = false;

		for (View currentView : solo.getViews()) {
			if (currentView.getId() == R.id.brick_loop_end_checkbox) {
				isCheckBoxVisible = currentView.getVisibility() == View.VISIBLE;
				break;
			}
		}

		assertTrue("CheckBock is not visible.", isCheckBoxVisible);
	}

	public void testCopyLoopEndBrickActionMode() {
		deleteAllBricks();

		UiTestUtils.addNewBrick(solo, R.string.category_control, R.string.brick_repeat);
		UiTestUtils.dragFloatingBrickDownwards(solo, 0);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		UiTestUtils.clickOnCheckBox(solo, 2);

		UiTestUtils.acceptAndCloseActionMode(solo);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof RepeatBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof LoopEndBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof RepeatBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof LoopEndBrick);
	}

	public void testSelectionAfterCopyActionMode() {
		deleteAllBricks();

		UiTestUtils.addNewBrick(solo, R.string.category_control, R.string.brick_repeat);
		UiTestUtils.dragFloatingBrickDownwards(solo, 0);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		solo.clickOnCheckBox(1);

		UiTestUtils.acceptAndCloseActionMode(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		solo.clickOnCheckBox(3);

		CheckBox firstRepeatBrickCheckBox = (CheckBox) solo.getView(R.id.brick_repeat_checkbox, 0);
		CheckBox secondRepeatBrickCheckBox = (CheckBox) solo.getView(R.id.brick_repeat_checkbox, 1);
		CheckBox firstLoopEndBrickCheckBox = (CheckBox) solo.getView(R.id.brick_loop_end_checkbox, 0);
		CheckBox secondLoopEndBrickCheckBox = (CheckBox) solo.getView(R.id.brick_loop_end_checkbox, 1);

		assertFalse("CheckBox is checked but shouldn't be.", firstRepeatBrickCheckBox.isChecked());
		assertTrue("CheckBox is not checked but should be.", secondRepeatBrickCheckBox.isChecked());
		assertFalse("CheckBox is checked but shouldn't be.", firstLoopEndBrickCheckBox.isChecked());
		assertTrue("CheckBox is not checked but should be.", secondLoopEndBrickCheckBox.isChecked());
	}

	public void testSelectionActionMode() {
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		UiTestUtils.clickOnCheckBox(solo, 1);

		CheckBox repeatBrickCheckbox = (CheckBox) solo.getView(R.id.brick_repeat_checkbox);
		CheckBox loopEndBrickCheckbox = (CheckBox) solo.getView(R.id.brick_loop_end_checkbox);
		CheckBox changeYByNBrickCheckbox = (CheckBox) solo.getView(R.id.brick_change_y_checkbox);

		assertTrue("CheckBox is not checked but should be.",
				repeatBrickCheckbox.isChecked() && loopEndBrickCheckbox.isChecked());
		assertFalse("CheckBox is checked but shouldn't be.", changeYByNBrickCheckbox.isChecked());

		UiTestUtils.acceptAndCloseActionMode(solo);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		UiTestUtils.clickOnCheckBox(solo, 1);

		repeatBrickCheckbox = (CheckBox) solo.getView(R.id.brick_repeat_checkbox);
		loopEndBrickCheckbox = (CheckBox) solo.getView(R.id.brick_loop_end_checkbox);
		changeYByNBrickCheckbox = (CheckBox) solo.getView(R.id.brick_change_y_checkbox);

		assertTrue("CheckBox is not checked but should be.",
				repeatBrickCheckbox.isChecked() && loopEndBrickCheckbox.isChecked());
		assertFalse("CheckBox is checked but shouldn't be.", changeYByNBrickCheckbox.isChecked());
	}

	private void createProject() {
		LoopEndBrick endBrick;

		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript();

		RepeatBrick repeatBrick = new RepeatBrick(3);
		endBrick = new LoopEndBrick(repeatBrick);

		script.addBrick(repeatBrick);
		script.addBrick(new ChangeYByNBrick(-10));
		script.addBrick(endBrick);

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

	private void deleteAllBricks() {
		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		UiTestUtils.clickOnText(solo, solo.getString(R.string.select_all).toUpperCase(Locale.getDefault()));
		UiTestUtils.acceptAndCloseActionMode(solo);
		UiTestUtils.clickOnText(solo, solo.getString(R.string.yes));
	}
}
