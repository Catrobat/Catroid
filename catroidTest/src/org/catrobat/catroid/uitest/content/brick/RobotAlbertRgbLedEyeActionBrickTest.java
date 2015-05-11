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

import android.os.Build;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.widget.ListView;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.RobotAlbertRgbLedEyeBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class RobotAlbertRgbLedEyeActionBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {

	private static final int SET_RED = 50;
	private static final int SET_RED_INITIALLY = 10;
	private static final int SET_GREEN = 40;
	private static final int SET_GREEN_INITIALLY = 20;
	private static final int SET_BLUE = 30;
	private static final int SET_BLUE_INITIALLY = 90;

	private static final String SET_RED_STRING = "0+250";
	private static final String SET_GREEN_STRING = "0+250";
	private static final String SET_BLUE_STRING = "0+250";

	private Project project;
	private RobotAlbertRgbLedEyeBrick brick;

	public RobotAlbertRgbLedEyeActionBrickTest() {
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
	public void testRobotAlbertRgbLedActionBrick() {
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
				solo.getText(solo.getString(R.string.brick_robot_albert_rgb_led_action)));

		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.robot_albert_rgb_led_red)));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.robot_albert_rgb_led_green)));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.robot_albert_rgb_led_blue)));

		UiTestUtils.testBrickWithFormulaEditor(solo, ProjectManager.getInstance().getCurrentSprite(),
				R.id.robot_albert_rgb_led_action_red_edit_text, SET_RED, Brick.BrickField.ROBOT_ALBERT_RGB_RED, brick);
		UiTestUtils.testBrickWithFormulaEditor(solo, ProjectManager.getInstance().getCurrentSprite(),
				R.id.robot_albert_rgb_led_action_green_edit_text, SET_GREEN, Brick.BrickField.ROBOT_ALBERT_RGB_GREEN, brick);
		UiTestUtils.testBrickWithFormulaEditor(solo, ProjectManager.getInstance().getCurrentSprite(),
				R.id.robot_albert_rgb_led_action_blue_edit_text, SET_BLUE, Brick.BrickField.ROBOT_ALBERT_RGB_BLUE, brick);

		UiTestUtils.testBrickWithFormulaEditor(ProjectManager.getInstance().getCurrentSprite(), solo,
				R.id.robot_albert_rgb_led_action_red_edit_text, SET_RED_STRING, Brick.BrickField.ROBOT_ALBERT_RGB_RED, brick);
		UiTestUtils.testBrickWithFormulaEditor(ProjectManager.getInstance().getCurrentSprite(), solo,
				R.id.robot_albert_rgb_led_action_green_edit_text, SET_GREEN_STRING, Brick.BrickField.ROBOT_ALBERT_RGB_GREEN, brick);
		UiTestUtils.testBrickWithFormulaEditor(ProjectManager.getInstance().getCurrentSprite(), solo,
				R.id.robot_albert_rgb_led_action_blue_edit_text, SET_BLUE_STRING, Brick.BrickField.ROBOT_ALBERT_RGB_BLUE, brick);

		String[] eyes = getActivity().getResources().getStringArray(R.array.robot_albert_eye_chooser);
		assertTrue("Spinner items list too short!", eyes.length == 3);

		int spinnerIndex = 1;

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			spinnerIndex = 0;
		}
		//TODO: seams that the if above isn't true......
		spinnerIndex = 0;
		Log.d("RobotAlbert-Test", "2: spinnerIndex=" + spinnerIndex);

		Spinner currentSpinner = solo.getCurrentViews(Spinner.class).get(spinnerIndex);
		solo.pressSpinnerItem(spinnerIndex, 0);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", eyes[0], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(spinnerIndex, 1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", eyes[1], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(spinnerIndex, 1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", eyes[2], currentSpinner.getSelectedItem());

	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript();

		brick = new RobotAlbertRgbLedEyeBrick(RobotAlbertRgbLedEyeBrick.Eye.Left,
				SET_RED_INITIALLY, SET_GREEN_INITIALLY, SET_BLUE_INITIALLY);

		script.addBrick(brick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
