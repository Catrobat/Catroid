/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.content.brick.stage;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.UserVariableAssertions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.io.asynctask.ProjectSaverKt.saveProjectSerial;

@RunWith(AndroidJUnit4.class)
public class SceneTransitionBrickStageTest {

	private UserVariable firstVariable = new UserVariable("firstVar");
	private UserVariable secondVariable = new UserVariable("secondVar");

	private ScriptEvaluationGateBrick firstBrickInScript;

	@Rule
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testContinueScene() {
		firstBrickInScript.waitUntilEvaluated(3000);
		UserVariableAssertions.assertUserVariableEqualsWithTimeout(firstVariable, 20, 3000);
	}

	private void createProject() {
		Project project = UiTestUtils.createDefaultTestProject(TestUtils.DEFAULT_TEST_PROJECT_NAME);

		Scene firstScene = project.getDefaultScene();
		Scene secondScene = new Scene("Scene 2", project);

		firstVariable = new UserVariable("firstVar");
		project.addUserVariable(firstVariable);

		secondVariable = new UserVariable("secondVar");
		project.addUserVariable(secondVariable);

		Sprite firstBackground = firstScene.getBackgroundSprite();
		Script firstStartScript = new StartScript();

		firstBrickInScript = ScriptEvaluationGateBrick.appendToScript(firstStartScript);

		firstStartScript.addBrick(new SetVariableBrick(new Formula(1), firstVariable));
		firstStartScript.addBrick(new SceneTransitionBrick(secondScene.getName()));
		firstStartScript.addBrick(new WaitBrick(500));

		Formula firstStartScriptFormula = new Formula(
				new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.MULT.name(), null,
						new FormulaElement(FormulaElement.ElementType.NUMBER, "5", null),
						new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, secondVariable.getName(), null)));
		firstStartScript.addBrick(new SetVariableBrick(firstStartScriptFormula, firstVariable));

		firstBackground.addScript(firstStartScript);

		Sprite secondBackground = new Sprite("Background");
		Script secondStartScript = new StartScript();

		Formula secondStartScriptFormula = new Formula(
				new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.PLUS.name(), null,
						new FormulaElement(FormulaElement.ElementType.NUMBER, "3", null),
						new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, firstVariable.getName(), null)));

		secondStartScript.addBrick(new SetVariableBrick(secondStartScriptFormula, secondVariable));
		secondStartScript.addBrick(new SceneTransitionBrick(firstScene.getName()));

		secondBackground.addScript(secondStartScript);
		secondScene.addSprite(secondBackground);

		project.addScene(secondScene);
		saveProjectSerial(project, ApplicationProvider.getApplicationContext());
	}
}
