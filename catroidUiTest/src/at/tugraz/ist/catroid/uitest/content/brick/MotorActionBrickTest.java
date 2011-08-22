/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
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
import at.tugraz.ist.catroid.content.bricks.MotorActionBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class MotorActionBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private Project project;
	private MotorActionBrick motorBrick;

	private double setDuration;
	private int setSpeed;
	private int setSpeedInitially;
	private static final int MAX_SPEED = 100;
	private static final int MIN_SPEED = -100;

	public MotorActionBrickTest() {
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
	public void testMotorActionBrick() {

		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();

		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0),
				getActivity().getAdapter().getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(getActivity().getString(R.string.brick_motor_action)));
		assertNotNull("TextView does not exist.", solo.getText(getActivity().getString(R.string.motor_duration)));
		assertNotNull("TextView does not exist.", solo.getText(getActivity().getString(R.string.motor_speed)));

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, setDuration + "");
		solo.clickOnButton(0);

		solo.sleep(300);
		double duration = (Double) UiTestUtils.getPrivateField("duration", motorBrick);
		assertEquals("Wrong text in field.", setDuration, duration);
		assertEquals("Value in Brick is not updated.", setDuration + "", solo.getEditText(0).getText().toString());

		assertEquals("SeekBar is at wrong position", setSpeedInitially + 100, solo.getCurrentProgressBars().get(0)
				.getProgress());

		solo.clickOnEditText(1);
		solo.clearEditText(0);
		solo.enterText(0, setSpeed + "");
		solo.clickOnButton(0);

		solo.sleep(300);
		int speed = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", setSpeed, speed);
		assertEquals("Value in Brick is not updated.", setSpeed + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", setSpeed + 100, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, setSpeedInitially + 100); //robotium doesnt go through proper function onProgressChanged() to change value on progress bar!
		solo.sleep(300);

		speed = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", setSpeedInitially, speed);
		assertEquals("Value in Brick is not updated.", setSpeedInitially + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", setSpeedInitially + 100, solo.getCurrentProgressBars().get(0)
				.getProgress());

		solo.clickOnButton(0);
		solo.sleep(300);

		int speed_btn = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", speed_btn, speed - 1);
		assertEquals("Value in Brick is not updated.", speed - 1 + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", speed - 1 + 100, solo.getCurrentProgressBars().get(0)
				.getProgress());

		solo.clickOnButton(1);
		solo.sleep(300);

		speed_btn = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", speed_btn, speed);
		assertEquals("Value in Brick is not updated.", speed + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", speed + 100, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, MIN_SPEED + 100);
		solo.clickOnButton(0);
		solo.clickOnButton(0);
		solo.sleep(300);

		speed = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", speed, MIN_SPEED);
		assertEquals("Value in Brick is not updated.", speed + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", speed + 100, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, MAX_SPEED + 100);
		solo.clickOnButton(1);
		solo.clickOnButton(1);
		solo.sleep(300);

		speed = (Integer) UiTestUtils.getPrivateField("speed", motorBrick);
		assertEquals("Wrong text in field.", speed, MAX_SPEED);
		assertEquals("Value in Brick is not updated.", speed + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", speed + 100, solo.getCurrentProgressBars().get(0).getProgress());

		solo.sleep(500);
		solo.pressSpinnerItem(0, 0);
		assertEquals("A", solo.getCurrentSpinners().get(0).getSelectedItem());
		solo.pressSpinnerItem(0, 1);
		assertEquals("B", solo.getCurrentSpinners().get(0).getSelectedItem());
		solo.pressSpinnerItem(0, 1);
		assertEquals("C", solo.getCurrentSpinners().get(0).getSelectedItem());

	}

	private void createProject() {
		//		setX = 17;
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript("script", sprite);

		setSpeedInitially = -70;

		motorBrick = new MotorActionBrick(sprite, 0, setSpeedInitially, 5);

		setDuration = 3.0;
		setSpeed = 30;

		script.addBrick(motorBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
