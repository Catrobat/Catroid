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
import android.widget.EditText;
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
		assertTrue("Unit missing for angle!", solo.searchText("°"));

		EditText turnEditText = (EditText) solo.getView(R.id.motor_turn_angle_edit_text);
		assertFalse("Edittext should not be clickable", turnEditText.isClickable());
		assertFalse("Edittext should be disabled", turnEditText.isEnabled());

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
		assertEquals("Wrong value in field!", "45", solo.getEditText(0).getText().toString());
		solo.clickInList(2);
		assertEquals("Wrong value in field!", "90", solo.getEditText(0).getText().toString());
		solo.clickInList(3);
		assertEquals("Wrong value in field!", "-45", solo.getEditText(0).getText().toString());
		solo.clickInList(4);
		assertEquals("Wrong value in field!", "-90", solo.getEditText(0).getText().toString());
		solo.clickInList(5);
		assertEquals("Wrong value in field!", "180", solo.getEditText(0).getText().toString());
		//		solo.scrollDownList(0); //warning randomness!
		//		solo.clickInList(5);
		//		assertEquals("Wrong value in field!", "360", solo.getEditText(0).getText().toString());

		solo.sleep(500);
		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, setAngle + "");
		solo.goBack();
		solo.clickOnButton(0);
		solo.sleep(500);

		int angle = (Integer) UiTestUtils.getPrivateField("degrees", motorBrick);
		assertEquals("Wrong text in field.", setAngle, angle);
		assertEquals("Value in Brick is not updated.", setAngle + "", solo.getEditText(0).getText().toString());

		solo.sleep(200);
		solo.clickOnView(solo.getView(R.id.directions_btn));
		try {
			solo.clickOnEditText(0);
			solo.clearEditText(0);
			solo.goBack();
			solo.clickOnButton(0);
			solo.sleep(500);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			fail("Numberformat Exception should not occur");
		}
		angle = (Integer) UiTestUtils.getPrivateField("degrees", motorBrick);
		assertEquals("Wrong text in field.", 0, angle);
		assertEquals("Value in Brick is not updated.", "0", solo.getEditText(0).getText().toString());

		solo.sleep(2000);
		String[] array = getActivity().getResources().getStringArray(R.array.nxt_motor_chooser);
		solo.sleep(100);
		assertTrue("Spinner items list too short!", array.length == 4);

		solo.sleep(2000);
		solo.pressSpinnerItem(0, 0);
		solo.sleep(500);
		assertEquals("Wrong item in spinner!", array[0], solo.getCurrentSpinners().get(0).getSelectedItem());
		solo.sleep(500);
		solo.pressSpinnerItem(0, 1);
		solo.sleep(500);
		assertEquals("Wrong item in spinner!", array[1], solo.getCurrentSpinners().get(0).getSelectedItem());
		solo.sleep(500);
		solo.pressSpinnerItem(0, 1);
		solo.sleep(500);
		assertEquals("Wrong item in spinner!", array[2], solo.getCurrentSpinners().get(0).getSelectedItem());
		solo.sleep(500);
		solo.pressSpinnerItem(0, 1);
		solo.sleep(1000);
		assertEquals("Wrong item in spinner!", array[3], solo.getCurrentSpinners().get(0).getSelectedItem());

	}

	private void createProject() {
		//		setX = 17;
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);

		setAngleInitially = 90;
		setAngle = 135;

		motorBrick = new NXTMotorTurnAngleBrick(sprite, NXTMotorTurnAngleBrick.Motor.MOTOR_A, setAngleInitially);

		script.addBrick(motorBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
