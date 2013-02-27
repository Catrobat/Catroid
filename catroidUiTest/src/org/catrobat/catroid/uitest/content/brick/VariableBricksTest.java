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

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.widget.Spinner;
import com.jayway.android.robotium.solo.Solo;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class VariableBricksTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private Project project;
	private UserVariablesContainer userVariablesContainer;
	private SetVariableBrick setVariableBrick;
	private ChangeVariableBrick changeVariableBrick;

	public VariableBricksTest() {
		super(ScriptActivity.class);
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
		userVariablesContainer.deleteUserVariableByName("p1");
		userVariablesContainer.deleteUserVariableByName("p2");
		userVariablesContainer.deleteUserVariableByName("sprite_var1");
		userVariablesContainer.deleteUserVariableByName("sprite_var2");
		super.tearDown();
	}

	@Smoke
	public void testVariableBricks() {
		Log.d("TEST", solo.getCurrentSpinners().toString());
		Spinner set_var_spinner = solo.getCurrentSpinners().get(1);
		Spinner change_var_spinner = solo.getCurrentSpinners().get(2);

		solo.clickOnView(set_var_spinner);
		solo.clickOnText("p2");
		UiTestUtils.testBrickWithFormulaEditor(solo, 0, 1, 50, "variable_formula", setVariableBrick);

		solo.clickOnView(change_var_spinner);
		solo.clickOnText("p2");
		UiTestUtils.testBrickWithFormulaEditor(solo, 0, 1, -8, "variable_formula", changeVariableBrick);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(1000);

		assertEquals("Variable has the wrong value after stage", 42, userVariablesContainer.getUserVariable("p2", "cat").getValue());

		solo.goBack();
		solo.goBack();
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);

		userVariablesContainer = project.getUserVariables();
		userVariablesContainer.addProjectUserVariable("p1", 0.0);
		userVariablesContainer.addProjectUserVariable("p2", 0.0);
		userVariablesContainer.addSpriteUserVariable("sprite_var1", 0.0);
		userVariablesContainer.addSpriteUserVariable("sprite_var2", 0.0);

		setVariableBrick = new SetVariableBrick(sprite, 0.0);
		script.addBrick(setVariableBrick);
		changeVariableBrick = new ChangeVariableBrick(sprite, 0.0);
		script.addBrick(changeVariableBrick);

		sprite.addScript(script);
		project.addSprite(sprite);
	}

}
