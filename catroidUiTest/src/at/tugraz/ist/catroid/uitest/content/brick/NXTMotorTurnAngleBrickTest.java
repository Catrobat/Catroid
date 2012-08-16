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
import android.widget.Spinner;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.NXTMotorTurnAngleBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;
import at.tugraz.ist.catroid.ui.fragment.ScriptFragment;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class NXTMotorTurnAngleBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private static final int SET_ANGLE = 135;

	private Solo solo;
	private Project project;
	private NXTMotorTurnAngleBrick motorBrick;

	public NXTMotorTurnAngleBrickTest() {
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
	}

	@Smoke
	public void testMotorTurnAngleBrick() {
		ScriptTabActivity activity = (ScriptTabActivity) solo.getCurrentActivity();
		ScriptFragment fragment = (ScriptFragment) activity.getTabFragment(ScriptTabActivity.INDEX_TAB_SCRIPTS);
		BrickAdapter adapter = fragment.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getGroupCount();

		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.",
				solo.getText(getActivity().getString(R.string.brick_motor_turn_angle)));
		assertNotNull("TextView does not exist.", solo.getText(getActivity().getString(R.string.motor_angle)));
		assertTrue("Unit missing for angle!", solo.searchText("°"));

		EditText turnEditText = (EditText) solo.getView(R.id.motor_turn_angle_edit_text);
		assertFalse("Edittext should not be clickable", turnEditText.isClickable());
		assertFalse("Edittext should be disabled", turnEditText.isEnabled());

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

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, SET_ANGLE + "");
		solo.clickOnButton(0);

		int angle = (Integer) UiTestUtils.getPrivateField("degrees", motorBrick);
		assertEquals("Wrong text in field.", SET_ANGLE, angle);
		assertEquals("Value in Brick is not updated.", SET_ANGLE + "", solo.getEditText(0).getText().toString());

		solo.sleep(200);
		solo.clickOnView(solo.getView(R.id.directions_btn));
		try {
			solo.clickOnEditText(0);
			solo.clearEditText(0);
			solo.clickOnButton(0);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			fail("Numberformat Exception should not occur");
		}
		angle = (Integer) UiTestUtils.getPrivateField("degrees", motorBrick);
		assertEquals("Wrong text in field.", 0, angle);
		assertEquals("Value in Brick is not updated.", "0", solo.getEditText(0).getText().toString());

		String[] array = getActivity().getResources().getStringArray(R.array.nxt_motor_chooser);
		assertTrue("Spinner items list too short!", array.length == 4);

		Spinner currentSpinner = solo.getCurrentSpinners().get(0);
		solo.pressSpinnerItem(0, 0);
		assertEquals("Wrong item in spinner!", array[0], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(0, 1);
		assertEquals("Wrong item in spinner!", array[1], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(0, 1);
		assertEquals("Wrong item in spinner!", array[2], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(0, 1);
		assertEquals("Wrong item in spinner!", array[3], currentSpinner.getSelectedItem());
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);

		int setAngleInitially = 90;
		motorBrick = new NXTMotorTurnAngleBrick(sprite, NXTMotorTurnAngleBrick.Motor.MOTOR_A, setAngleInitially);

		script.addBrick(motorBrick);
		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
