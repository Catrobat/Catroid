/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.uitest.content.brick;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.Spinner;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.LegoNxtMotorActionBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;
import at.tugraz.ist.catroid.ui.fragment.ScriptFragment;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class LegoNxtMotorActionBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private static final int SET_SPEED = 30;
	private static final int SET_SPEED_INITIALLY = -70;
	private static final int MAX_SPEED = 100;
	private static final int MIN_SPEED = -100;

	private Solo solo;
	private Project project;
	private LegoNxtMotorActionBrick motorBrick;

	public LegoNxtMotorActionBrickTest() {
		super(ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	@Smoke
	public void testNXTMotorActionBrick() {
		ScriptTabActivity activity = (ScriptTabActivity) solo.getCurrentActivity();
		ScriptFragment fragment = (ScriptFragment) activity.getTabFragment(ScriptTabActivity.INDEX_TAB_SCRIPTS);
		BrickAdapter adapter = fragment.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2 + 1, solo.getCurrentListViews().get(0).getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(getActivity().getString(R.string.brick_motor_action)));
		assertNotNull("TextView does not exist.", solo.getText(getActivity().getString(R.string.motor_speed)));

		String buttonOkText = solo.getString(R.string.ok);
		solo.clickOnEditText(0);
		solo.waitForText(buttonOkText);
		solo.clearEditText(0);
		solo.enterText(0, SET_SPEED + "");
		solo.clickOnButton(buttonOkText);

		int speed = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", SET_SPEED, speed);
		assertEquals("Value in Brick is not updated.", SET_SPEED + "", solo.getEditText(0).getText().toString());
		assertEquals("SeekBar is at wrong position", SET_SPEED + 100, solo.getCurrentProgressBars().get(0)
				.getProgress());

		solo.setProgressBar(0, SET_SPEED_INITIALLY + 100); //robotium doesnt go through proper function onProgressChanged() to change value on progress bar!
		solo.sleep(200);
		speed = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", SET_SPEED_INITIALLY, speed);
		assertEquals("Value in Brick is not updated.", SET_SPEED_INITIALLY + "", solo.getEditText(0).getText()
				.toString());
		assertEquals("SeekBar is at wrong position", SET_SPEED_INITIALLY + 100, solo.getCurrentProgressBars().get(0)
				.getProgress());

		solo.clickOnButton(0);
		int speedCounter = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", speedCounter, speed - 1);
		assertEquals("Value in Brick is not updated.", speed - 1 + "", solo.getEditText(0).getText().toString());
		assertEquals("SeekBar is at wrong position", speed - 1 + 100, solo.getCurrentProgressBars().get(0)
				.getProgress());

		solo.clickOnButton(1);
		speedCounter = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", speedCounter, speed);
		assertEquals("Value in Brick is not updated.", speed + "", solo.getEditText(0).getText().toString());
		assertEquals("SeekBar is at wrong position", speed + 100, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, 1);
		solo.clickOnButton(0);
		solo.clickOnButton(0);
		speed = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", speed, MIN_SPEED);
		assertEquals("Value in Brick is not updated.", speed + "", solo.getEditText(0).getText().toString());
		assertEquals("SeekBar is at wrong position", speed + 100, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, MAX_SPEED + 100);
		solo.clickOnButton(1);
		solo.clickOnButton(1);
		speed = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", speed, MAX_SPEED);
		assertEquals("Value in Brick is not updated.", speed + "", solo.getEditText(0).getText().toString());
		assertEquals("SeekBar is at wrong position", speed + 100, solo.getCurrentProgressBars().get(0).getProgress());

		String[] motors = getActivity().getResources().getStringArray(R.array.nxt_motor_chooser);
		assertTrue("Spinner items list too short!", motors.length == 4);

		Spinner currentSpinner = solo.getCurrentSpinners().get(0);
		solo.pressSpinnerItem(0, 0);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[0], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(0, 1);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[1], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(0, 1);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[2], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(0, 1);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[3], currentSpinner.getSelectedItem());
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);

		motorBrick = new LegoNxtMotorActionBrick(sprite, LegoNxtMotorActionBrick.Motor.MOTOR_A, SET_SPEED_INITIALLY);

		script.addBrick(motorBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
