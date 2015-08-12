/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.brick;

import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class VariableBricksTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private Project project;
	private DataContainer dataContainer;
	private SetVariableBrick setVariableBrick;
	private ChangeVariableBrick changeVariableBrick;
	private Sprite sprite;

	public VariableBricksTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Override
	public void tearDown() throws Exception {
		dataContainer.deleteUserVariableByName("p1");
		dataContainer.deleteUserVariableByName("p2");
		dataContainer.deleteUserVariableByName("sprite_var1");
		dataContainer.deleteUserVariableByName("sprite_var2");
		super.tearDown();
	}

	public void testVariableBricks() {
		Spinner setVariableSpinner = solo.getCurrentViews(Spinner.class).get(0);
		Spinner changeVariableSpinner = solo.getCurrentViews(Spinner.class).get(1);

		solo.clickOnView(setVariableSpinner);
		solo.clickOnText("p2");
		solo.clickOnView(changeVariableSpinner);
		solo.clickOnText("p2", 1);

		//		UiTestUtils.testBrickWithFormulaEditor(solo, 0, 1, 50, "variable_formula", setVariableBrick);
		solo.clickOnText("0");
		UiTestUtils.insertIntegerIntoEditText(solo, 50);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));

		//		UiTestUtils.testBrickWithFormulaEditor(solo, 0, 1, -8, "variable_formula", changeVariableBrick);
		solo.clickOnText("1");
		UiTestUtils.insertDoubleIntoEditText(solo, -8.0);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));

		solo.waitForView(solo.getView(R.id.button_play));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(5000);

		assertEquals("Variable has the wrong value after stage", 42.0,
				dataContainer.getUserVariable("p2", sprite).getValue());
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite = new Sprite("cat");
		Script script = new StartScript();
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);

		dataContainer = project.getDataContainer();
		dataContainer.addProjectUserVariable("p1");
		dataContainer.addProjectUserVariable("p2");
		dataContainer.addSpriteUserVariable("sprite_var1");
		dataContainer.addSpriteUserVariable("sprite_var2");

		setVariableBrick = new SetVariableBrick(0.0);
		script.addBrick(setVariableBrick);
		changeVariableBrick = new ChangeVariableBrick(1.1);
		script.addBrick(changeVariableBrick);

		sprite.addScript(script);
		project.addSprite(sprite);
	}
}
