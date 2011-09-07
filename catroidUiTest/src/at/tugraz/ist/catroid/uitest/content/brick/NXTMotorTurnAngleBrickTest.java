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
import at.tugraz.ist.catroid.content.bricks.NXTMotorTurnAngleBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class NXTMotorTurnAngleBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private Project project;
	private NXTMotorTurnAngleBrick motorBrick;

	private int setAngle;
	private int setAngleInitially;

	public NXTMotorTurnAngleBrickTest() {
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
	public void testMotorTurnAngleBrick() {

		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();

		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0),
				getActivity().getAdapter().getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.",
				solo.getText(getActivity().getString(R.string.brick_motor_turn_angle)));
		assertNotNull("TextView does not exist.", solo.getText(getActivity().getString(R.string.motor_angle)));

		//		solo.clickOnEditText(0);
		//		solo.clearEditText(0);
		//		solo.enterText(0, setAngle + "");
		//		solo.clickOnButton(0);
		//
		//		solo.sleep(300);
		//		int angle = (Integer) UiTestUtils.getPrivateField("angle", motorBrick);
		//		assertEquals("Wrong text in field.", setAngle, angle);
		//		assertEquals("Value in Brick is not updated.", setAngle + "", solo.getEditText(0).getText().toString());

		solo.clickOnButton(0);
		solo.clickInList(1);
		assertEquals("45", solo.getEditText(0).getText().toString());
		solo.clickInList(2);
		assertEquals("90", solo.getEditText(0).getText().toString());
		solo.clickInList(3);
		assertEquals("-45", solo.getEditText(0).getText().toString());
		solo.clickInList(4);
		assertEquals("-90", solo.getEditText(0).getText().toString());
		solo.clickInList(5);
		assertEquals("180", solo.getEditText(0).getText().toString());

		solo.sleep(500);
		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, setAngle + "");
		solo.goBack();
		solo.clickOnButton(0);
		solo.sleep(500);

		int angle = (Integer) UiTestUtils.getPrivateField("angle", motorBrick);
		assertEquals("Wrong text in field.", setAngle, angle);
		assertEquals("Value in Brick is not updated.", setAngle + "", solo.getEditText(0).getText().toString());

		solo.pressSpinnerItem(0, 2);
		assertEquals("C", solo.getCurrentSpinners().get(0).getSelectedItem());
		solo.pressSpinnerItem(0, -1);
		assertEquals("B", solo.getCurrentSpinners().get(0).getSelectedItem());
		solo.pressSpinnerItem(0, -1);
		assertEquals("A", solo.getCurrentSpinners().get(0).getSelectedItem());
		solo.pressSpinnerItem(0, 3);
		assertEquals("A+C", solo.getCurrentSpinners().get(0).getSelectedItem());

	}

	private void createProject() {
		//		setX = 17;
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript("script", sprite);

		setAngleInitially = 90;
		setAngle = 135;

		motorBrick = new NXTMotorTurnAngleBrick(sprite, 0, setAngleInitially);

		script.addBrick(motorBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
