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
package at.tugraz.ist.catroid.uitest.ui.dialog;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class FormulaEditorDialogTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private Project project;
	private PlaceAtBrick placeAtBrick;
	private static final int INITIAL_X = 8;
	private static final int INITIAL_Y = 7;

	public FormulaEditorDialogTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		placeAtBrick = new PlaceAtBrick(sprite, INITIAL_X, INITIAL_Y);
		script.addBrick(placeAtBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	public void testFormulaEditorDialogAndSimpleInterpretation() {
		//		String brickWhenStartedText = solo.getString(R.string.brick_when_started);
		//		solo.clickLongOnText(brickWhenStartedText);
		//		solo.clickOnText(getActivity().getString(R.string.delete));
		//		solo.sleep(500);

		//		UiTestUtils.addNewBrick(solo, R.string.brick_place_at);

		String newXFormula = "10 + 12 - 2 * 3 - 4 ";
		int newXValue = 10 + 12 - 2 * 3 - 4;
		String newYFormula = "rand( cos( 90 ) , 10 * sin( 90 ) ) ";

		int X_POS_EDIT_TEXT_ID = 0;
		int Y_POS_EDIT_TEXT_ID = 1;
		int FORMULA_EDITOR_EDIT_TEXT_ID = 2;

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.clearEditText(FORMULA_EDITOR_EDIT_TEXT_ID);
		solo.enterText(FORMULA_EDITOR_EDIT_TEXT_ID, "999 " + newXFormula);
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_save));
		solo.sleep(50);
		assertTrue("Toast not found", solo.searchText(solo.getString(R.string.formula_editor_parse_fail)));

		solo.clearEditText(FORMULA_EDITOR_EDIT_TEXT_ID);
		solo.enterText(FORMULA_EDITOR_EDIT_TEXT_ID, newXFormula);
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_save));
		solo.sleep(50);
		assertTrue("Toast not found", solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		solo.enterText(FORMULA_EDITOR_EDIT_TEXT_ID, "++++");
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_discard));
		solo.sleep(50);
		assertTrue("Toast not found", solo.searchText(solo.getString(R.string.formula_editor_changes_discarded)));
		assertEquals("Wrong text in FormulaEditor", newXFormula, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		solo.clearEditText(FORMULA_EDITOR_EDIT_TEXT_ID);
		solo.enterText(FORMULA_EDITOR_EDIT_TEXT_ID, newYFormula);
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_save));
		solo.sleep(50);
		assertTrue("Toast not found", solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.sleep(50);
		assertEquals("Wrong text in FormulaEditor", newXFormula, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		solo.sleep(50);
		assertEquals("Wrong text in FormulaEditor", newYFormula, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.clickOnButton(solo.getString(R.string.formula_editor_button_return));
		solo.sleep(300);

		//Interpretation test
		Formula formula = (Formula) UiTestUtils.getPrivateField("xPositionFormula", placeAtBrick);
		assertEquals("Wrong text in field", newXValue, formula.interpret());

		formula = (Formula) UiTestUtils.getPrivateField("xPositionFormula", placeAtBrick);

		int newYValue = formula.interpret().intValue();

		assertTrue("Wrong text in field", newYValue > 0 && newYValue < 10);

	}

}
