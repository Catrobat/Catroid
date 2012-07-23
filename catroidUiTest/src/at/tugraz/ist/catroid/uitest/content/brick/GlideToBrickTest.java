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

import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.GlideToBrick;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class GlideToBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;

	public GlideToBrickTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testNumberInput() {
		String whenStartedText = solo.getString(R.string.brick_when_started);
		solo.clickLongOnText(whenStartedText);
		solo.clickOnText(getActivity().getString(R.string.delete));

		UiTestUtils.addNewBrick(solo, R.string.brick_glide);
		solo.clickOnText(whenStartedText);

		double duration = 1.5;
		int xPosition = 123;
		int yPosition = 567;
		//UiTestUtils.testBrickWithFormulaEditor(solo, 0, 1, Y_TO_CHANGE, "yMovementFormula", changeYByBrick);
		solo.clickOnEditText(0);
		solo.clearEditText(3);
		solo.enterText(3, String.valueOf(duration));
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_save));
		solo.sleep(200);

		solo.clickOnEditText(1);
		solo.clearEditText(3);
		solo.enterText(3, String.valueOf(xPosition));
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_save));
		solo.sleep(200);

		solo.clickOnEditText(2);
		solo.clearEditText(3);
		solo.enterText(3, String.valueOf(yPosition));
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_save));
		solo.sleep(200);

		assertEquals("Text not updated within FormulaEditor", duration,
				Double.parseDouble(solo.getEditText(0).getText().toString()));
		assertEquals("Text not updated within FormulaEditor", xPosition,
				Integer.parseInt(solo.getEditText(1).getText().toString().substring(0, 3)));
		assertEquals("Text not updated within FormulaEditor", yPosition,
				Integer.parseInt(solo.getEditText(2).getText().toString().substring(0, 3)));

		solo.clickOnButton(solo.getString(R.string.formula_editor_button_return));
		solo.sleep(200);

		//		assertEquals("Wrong text in field", newValue, formula.interpret());
		//		assertEquals("Text not updated in the brick list", newValue,
		//				Double.parseDouble(solo.getEditText(0).getText().toString()));

		//		UiTestUtils.clickEnterClose(solo, 0, String.valueOf(duration));
		//		UiTestUtils.clickEnterClose(solo, 1, String.valueOf(xPosition));
		//		UiTestUtils.clickEnterClose(solo, 2, String.valueOf(yPosition));

		ProjectManager manager = ProjectManager.getInstance();
		List<Brick> brickList = manager.getCurrentScript().getBrickList();
		GlideToBrick glideToBrick = (GlideToBrick) brickList.get(0);

		Formula formula = (Formula) UiTestUtils.getPrivateField("durationInSecondsFormula", glideToBrick);
		Double temp = formula.interpret().doubleValue();

		assertEquals("Wrong duration input in Glide to brick", Math.round(duration * 1000), Math.round(temp * 1000));
		formula = (Formula) UiTestUtils.getPrivateField("xDestinationFormula", glideToBrick);
		int temp2 = formula.interpret().intValue();
		assertEquals("Wrong x input in Glide to brick", xPosition, temp2);

		formula = (Formula) UiTestUtils.getPrivateField("yDestinationFormula", glideToBrick);
		temp2 = formula.interpret().intValue();
		assertEquals("Wrong y input in Glide to brick", yPosition, temp2);
	}

	public void testResizeInputFields() {
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		createProject();
		solo.sleep(200);
		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.clickOnText(solo.getCurrentListViews().get(0).getItemAtPosition(0).toString());
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		UiTestUtils.testDoubleEditText(solo, 0, 1.1, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, 12345.67, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, -1, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, 12345.678, 60, false);

		for (int i = 1; i < 3; i++) {
			UiTestUtils.testIntegerEditText(solo, i, 1, 60, true);
			UiTestUtils.testIntegerEditText(solo, i, 123456, 60, true);
			UiTestUtils.testIntegerEditText(solo, i, -1, 60, true);
			UiTestUtils.testIntegerEditText(solo, i, 1234567, 60, false);
		}
	}

	private void createProject() {
		int xValue = 800;
		int yValue = 0;
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		Brick glideToBrick = new GlideToBrick(sprite, xValue, yValue, 1000);
		script.addBrick(glideToBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
