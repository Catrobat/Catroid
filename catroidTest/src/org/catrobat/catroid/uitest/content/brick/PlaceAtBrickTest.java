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
import android.widget.ListView;

import com.robotium.solo.Condition;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class PlaceAtBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {

	private Project project;
	private PlaceAtBrick placeAtBrick;

	public PlaceAtBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		// normally super.setUp should be called first
		// but kept the test failing due to view is null
		// when starting in ScriptActivity
		createProject();
		super.setUp();
	}

	public void testPlaceAtBrick() throws InterruptedException {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 5, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 4, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));

		assertEquals("Wrong Brick instance.", projectBrickList.get(1), adapter.getChild(groupCount - 1, 1));

		assertEquals("Wrong Brick instance.", projectBrickList.get(2), adapter.getChild(groupCount - 1, 2));

		assertEquals("Wrong Brick instance.", projectBrickList.get(3), adapter.getChild(groupCount - 1, 3));
		assertNotNull("TextView does not exist", solo.getText(solo.getString(R.string.brick_place_at)));

		int xPosition = 987;
		int yPosition = 654;

		UiTestUtils.testBrickWithFormulaEditor(solo, ProjectManager.getInstance().getCurrentSprite(),
				R.id.brick_place_at_edit_text_x, xPosition, Brick.BrickField.X_POSITION, placeAtBrick);
		solo.sleep(200);

		UiTestUtils.testBrickWithFormulaEditor(solo, ProjectManager.getInstance().getCurrentSprite(),
				R.id.brick_place_at_edit_text_y, yPosition, Brick.BrickField.Y_POSITION, placeAtBrick);
	}

	public void testBehaviorOfUndoAndRedoButton() {
		solo.clickOnView(solo.getView(R.id.brick_place_at_edit_text_x));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_5));
		View undo = solo.getView(R.id.menu_undo);
		solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return solo.getView(R.id.menu_undo).isEnabled();
			}
		}, 100);
		solo.clickOnText("206");
		solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return solo.getView(R.id.menu_undo).isClickable();
			}
		}, 100);
		assertFalse("Undo button should be disabled", undo.isEnabled());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_6));
		solo.clickOnActionBarItem(R.id.menu_undo);
		View redo = solo.getView(R.id.menu_redo);
		solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return solo.getView(R.id.menu_redo).isEnabled();
			}
		}, 100);
		assertTrue("Redo button should be enabled", redo.isEnabled());

		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_5));
		solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return solo.getView(R.id.menu_redo).isClickable();
			}
		}, 100);
		assertFalse("Redo button should be disabled", redo.isEnabled());
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript();
		script.addBrick(new HideBrick());
		placeAtBrick = new PlaceAtBrick(105, 206);
		script.addBrick(placeAtBrick);
		PlaySoundBrick soundBrick = new PlaySoundBrick();
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName("sound.mp3");
		soundInfo.setTitle("sound.mp3");
		soundBrick.setSoundInfo(soundInfo);
		script.addBrick(soundBrick);

		script.addBrick(new SetSizeToBrick(80));

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
