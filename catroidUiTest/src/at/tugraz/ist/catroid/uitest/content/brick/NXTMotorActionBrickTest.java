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
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.NXTMotorActionBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class NXTMotorActionBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private Project project;
	private NXTMotorActionBrick motorBrick;

	private int setSpeed;
	private int setSpeedInitially;
	private static final int MAX_SPEED = 100;
	private static final int MIN_SPEED = -100;

	public NXTMotorActionBrickTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		super.tearDown();
	}

	@Smoke
	public void testNXTMotorActionBrick() {

		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();

		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), getActivity().getAdapter().getChild(
				groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(getActivity().getString(R.string.brick_motor_action)));
		assertNotNull("TextView does not exist.", solo.getText(getActivity().getString(R.string.motor_speed)));

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, setSpeed + "");
		solo.goBack();
		solo.clickOnButton(0);

		solo.sleep(300);
		int speed = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", setSpeed, speed);
		assertEquals("Value in Brick is not updated.", setSpeed + "", solo.getEditText(0).getText().toString());
		assertEquals("SeekBar is at wrong position", setSpeed + 100, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, setSpeedInitially + 100); //robotium doesnt go through proper function onProgressChanged() to change value on progress bar!
		solo.sleep(300);

		speed = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", setSpeedInitially, speed);
		assertEquals("Value in Brick is not updated.", setSpeedInitially + "", solo.getEditText(0).getText().toString());
		assertEquals("SeekBar is at wrong position", setSpeedInitially + 100, solo.getCurrentProgressBars().get(0)
				.getProgress());

		solo.clickOnButton(0);
		solo.sleep(300);

		int speed_btn = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", speed_btn, speed - 1);
		assertEquals("Value in Brick is not updated.", speed - 1 + "", solo.getEditText(0).getText().toString());
		assertEquals("SeekBar is at wrong position", speed - 1 + 100, solo.getCurrentProgressBars().get(0)
				.getProgress());

		solo.clickOnButton(1);
		solo.sleep(300);

		speed_btn = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", speed_btn, speed);
		assertEquals("Value in Brick is not updated.", speed + "", solo.getEditText(0).getText().toString());
		assertEquals("SeekBar is at wrong position", speed + 100, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, MIN_SPEED + 100);
		solo.clickOnButton(0);
		solo.clickOnButton(0);
		solo.sleep(300);

		speed = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", speed, MIN_SPEED);
		assertEquals("Value in Brick is not updated.", speed + "", solo.getEditText(0).getText().toString());
		assertEquals("SeekBar is at wrong position", speed + 100, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, MAX_SPEED + 100);
		solo.clickOnButton(1);
		solo.clickOnButton(1);
		solo.sleep(300);

		speed = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", speed, MAX_SPEED);
		assertEquals("Value in Brick is not updated.", speed + "", solo.getEditText(0).getText().toString());
		assertEquals("SeekBar is at wrong position", speed + 100, solo.getCurrentProgressBars().get(0).getProgress());

		solo.sleep(1500);
		String[] array = getActivity().getResources().getStringArray(R.array.nxt_motor_chooser);
		assertTrue("Spinner items list too short!", array.length == 4);

		solo.sleep(1500);
		solo.pressSpinnerItem(0, 0);
		assertEquals("Wrong item in spinner!", array[0], solo.getCurrentSpinners().get(0).getSelectedItem());
		solo.pressSpinnerItem(0, 1);
		assertEquals("Wrong item in spinner!", array[1], solo.getCurrentSpinners().get(0).getSelectedItem());
		solo.pressSpinnerItem(0, 1);
		assertEquals("Wrong item in spinner!", array[2], solo.getCurrentSpinners().get(0).getSelectedItem());
		solo.pressSpinnerItem(0, 1);
		assertEquals("Wrong item in spinner!", array[3], solo.getCurrentSpinners().get(0).getSelectedItem());

	}

	private void createProject() {
		//		setX = 17;
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);

		setSpeedInitially = -70;

		motorBrick = new NXTMotorActionBrick(sprite, NXTMotorActionBrick.Motor.MOTOR_A, setSpeedInitially);

		setSpeed = 30;

		script.addBrick(motorBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
