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
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;
import android.widget.Spinner;

import com.jayway.android.robotium.solo.Solo;

public class LegoNxtMotorTurnAngleBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private static final int SET_ANGLE = 135;

	private Solo solo;
	private Project project;
	private LegoNxtMotorTurnAngleBrick motorBrick;

	public LegoNxtMotorTurnAngleBrickTest() {
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
	public void testMotorTurnAngleBrick() {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.brick_motor_turn_angle)));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.motor_angle)));
		assertTrue("Unit missing for angle!", solo.searchText("°"));

		UiTestUtils.testBrickWithFormulaEditor(solo, 0, 1, SET_ANGLE, "degrees", motorBrick);

		String[] array = getActivity().getResources().getStringArray(R.array.nxt_motor_chooser);
		assertTrue("Spinner items list too short!", array.length == 4);

		int legoSpinnerIndex = 1;

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			legoSpinnerIndex = 0;
		}

		Spinner currentSpinner = solo.getCurrentViews(Spinner.class).get(legoSpinnerIndex);
		solo.pressSpinnerItem(legoSpinnerIndex, 0);
		assertEquals("Wrong item in spinner!", array[0], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(legoSpinnerIndex, 1);
		assertEquals("Wrong item in spinner!", array[1], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(legoSpinnerIndex, 1);
		assertEquals("Wrong item in spinner!", array[2], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(legoSpinnerIndex, 1);
		assertEquals("Wrong item in spinner!", array[3], currentSpinner.getSelectedItem());
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);

		int setAngleInitially = 90;
		motorBrick = new LegoNxtMotorTurnAngleBrick(sprite, LegoNxtMotorTurnAngleBrick.Motor.MOTOR_A, setAngleInitially);

		script.addBrick(motorBrick);
		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
