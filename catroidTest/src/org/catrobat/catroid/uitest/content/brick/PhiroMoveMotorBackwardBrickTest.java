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

import android.widget.ListView;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class PhiroMoveMotorBackwardBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {
	private static final int SET_SPEED = 30;
	private static final int SET_SPEED_INITIALLY = -70;

	private Project project;
	private PhiroMotorMoveBackwardBrick motorBrick;

	public PhiroMoveMotorBackwardBrickTest() {
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

	public void testPhiroMotorActionBrick() {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.brick_phiro_motor_backward_action)));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.phiro_motor_speed)));

		UiTestUtils.testBrickWithFormulaEditor(solo, ProjectManager.getInstance().getCurrentSprite(),
				R.id.brick_phiro_motor_backward_action_speed_edit_text, SET_SPEED, Brick.BrickField.PHIRO_SPEED, motorBrick);

		String[] motors = getActivity().getResources().getStringArray(R.array.brick_phiro_select_motor_spinner);
		assertTrue("Spinner items list too short!", motors.length == 3);

		int phiroSpinnerIndex = 0;

		Spinner currentSpinner = solo.getCurrentViews(Spinner.class).get(phiroSpinnerIndex);
		solo.pressSpinnerItem(phiroSpinnerIndex, -1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[0], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(phiroSpinnerIndex, 1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[1], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(phiroSpinnerIndex, 1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[2], currentSpinner.getSelectedItem());
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript();

		motorBrick = new PhiroMotorMoveBackwardBrick(PhiroMotorMoveBackwardBrick.Motor.MOTOR_RIGHT, SET_SPEED_INITIALLY);

		script.addBrick(motorBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
