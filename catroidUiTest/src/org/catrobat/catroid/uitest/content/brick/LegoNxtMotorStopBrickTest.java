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
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;
import android.widget.Spinner;

import com.jayway.android.robotium.solo.Solo;

public class LegoNxtMotorStopBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {

	private Solo solo;
	private Project project;
	private LegoNxtMotorStopBrick motorStopBrick;

	public LegoNxtMotorStopBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	@Smoke
	public void testMotorActionBrick() {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2 + 1, dragDropListView.getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.motor_stop)));

		String[] motors = getActivity().getResources().getStringArray(R.array.nxt_stop_motor_chooser);
		assertTrue("Spinner items list too short!", motors.length == 5);

		int legoSpinnerIndex = 1;

		if (Build.VERSION.SDK_INT < 15) {
			legoSpinnerIndex = 0;
		}

		Spinner currentSpinner = solo.getCurrentSpinners().get(legoSpinnerIndex);
		solo.pressSpinnerItem(legoSpinnerIndex, 5);
		assertEquals("Wrong item in spinner!", motors[4], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(legoSpinnerIndex, -1);
		assertEquals("Wrong item in spinner!", motors[3], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(legoSpinnerIndex, -1);
		assertEquals("Wrong item in spinner!", motors[2], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(legoSpinnerIndex, -1);
		assertEquals("Wrong item in spinner!", motors[1], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(legoSpinnerIndex, -1);
		assertEquals("Wrong item in spinner!", motors[0], currentSpinner.getSelectedItem());
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);

		motorStopBrick = new LegoNxtMotorStopBrick(sprite, LegoNxtMotorStopBrick.Motor.MOTOR_A);

		script.addBrick(motorStopBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
