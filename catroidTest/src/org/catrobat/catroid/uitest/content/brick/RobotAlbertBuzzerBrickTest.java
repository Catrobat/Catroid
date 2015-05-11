/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2015 The Catrobat Team
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

import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.RobotAlbertBuzzerBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class RobotAlbertBuzzerBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {
	private static final int SET_FREQ = 30;
	private static final int SET_FREQ_INITIALLY = 40;
	private static final String SET_FREQ_STRING = "20+30";

	private Project project;
	private RobotAlbertBuzzerBrick brick;

	public RobotAlbertBuzzerBrickTest() {
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

	@Smoke
	public void testRobotAlbertBuzzerBrick() {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.",
				solo.getText(solo.getString(R.string.brick_robot_albert_buzzer_action)));

		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.robot_albert_buzzer_frequency)));

		UiTestUtils.testBrickWithFormulaEditor(solo, ProjectManager.getInstance().getCurrentSprite(), R.id.robot_albert_buzzer_frequency_edit_text, SET_FREQ,
				Brick.BrickField.ROBOT_ALBERT_BUZZER, brick);

		//TODO: Prints an error after setting it from init to new value, but I can see that it was set. Executing the command a second time seams to solve that. Why not the first time??
		UiTestUtils.testBrickWithFormulaEditor(solo, ProjectManager.getInstance().getCurrentSprite(),
				R.id.robot_albert_buzzer_frequency_edit_text, SET_FREQ, Brick.BrickField.ROBOT_ALBERT_BUZZER, brick);

		UiTestUtils.testBrickWithFormulaEditor(ProjectManager.getInstance().getCurrentSprite(), solo,
				R.id.robot_albert_buzzer_frequency_edit_text, SET_FREQ_STRING, Brick.BrickField.ROBOT_ALBERT_BUZZER, brick);
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript();

		brick = new RobotAlbertBuzzerBrick(SET_FREQ_INITIALLY);

		script.addBrick(brick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
