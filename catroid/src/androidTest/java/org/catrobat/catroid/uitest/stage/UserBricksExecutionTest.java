/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.uitest.stage;

import android.util.Log;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.List;

public class UserBricksExecutionTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private static final String TAG = UserBricksExecutionTest.class.getSimpleName();

	private Project project;
	private Sprite sprite;
	private int xChangeValue = 5;
	private int yChangeValue = 10;
	private String projectName = "testProject";
	private String variableName = "local_var";

	public UserBricksExecutionTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		Reflection.setPrivateField(ProjectManager.class, ProjectManager.getInstance(), "asynchronousTask", false);
		sprite = new SingleSprite("testSprite");
		project = new Project(null, projectName);

		project.getDefaultScene().addSprite(new SingleSprite("background"));
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		UiTestUtils.prepareStageForTest();
	}

	public void testUserVariableAsUserBrickParameter() {
		createUserVariableProject();

		ProjectManager.getInstance().setCurrentSprite(sprite);
		checkSpritePosition(0, 0);
		playProject();
		checkSpritePosition(xChangeValue, 0);
	}

	public void testUserBrickParallelExecutionInStage() {
		createParallelProject();

		ProjectManager.getInstance().setCurrentSprite(sprite);
		checkSpritePosition(0, 0);
		playProject();
		checkSpritePosition(xChangeValue * 3, yChangeValue * 3);
	}

	private void checkSpritePosition(Integer expectedX, Integer expectedY) {
		float x = sprite.look.getXInUserInterfaceDimensionUnit();
		float y = sprite.look.getYInUserInterfaceDimensionUnit();

		assertEquals("Unexpected sprite x position: ", (float) expectedX, x);
		assertEquals("Unexpected sprite y position: ", (float) expectedY, y);
	}

	private void createParallelProject() {
		String variableOneName = "variable1";
		String variableTwoName = "variable2";

		UserScriptDefinitionBrick definitionBrick = new UserScriptDefinitionBrick();
		definitionBrick.addUIText("test");
		definitionBrick.addUILocalizedVariable(variableOneName);
		definitionBrick.addUILocalizedVariable(variableTwoName);
		Formula variableOneFormula = new Formula(new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, variableOneName, null));
		Formula variableTwoFormula = new Formula(new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, variableTwoName, null));
		ChangeXByNBrick xBrick = new ChangeXByNBrick(variableOneFormula);
		ChangeYByNBrick yBrick = new ChangeYByNBrick(variableTwoFormula);

		definitionBrick.getUserScript().addBrick(xBrick);
		definitionBrick.getUserScript().addBrick(yBrick);

		UserBrick firstUserBrickFirstScript = new UserBrick(definitionBrick);
		UserBrick secondUserBrickFirstScript = new UserBrick(definitionBrick);
		firstUserBrickFirstScript.updateUserBrickParametersAndVariables();
		secondUserBrickFirstScript.updateUserBrickParametersAndVariables();

		UserBrick firstUserBrickSecondScript = new UserBrick(definitionBrick);
		firstUserBrickSecondScript.updateUserBrickParametersAndVariables();

		StartScript firstStartScript = new StartScript();
		firstStartScript.addBrick(firstUserBrickFirstScript);
		firstStartScript.addBrick(secondUserBrickFirstScript);
		sprite.addScript(firstStartScript);

		StartScript secondStartScript = new StartScript();
		secondStartScript.addBrick(firstUserBrickSecondScript);
		sprite.addScript(secondStartScript);

		setValueFormula(firstUserBrickFirstScript, xChangeValue, yChangeValue);
		setValueFormula(secondUserBrickFirstScript, xChangeValue, yChangeValue);
		setValueFormula(firstUserBrickSecondScript, xChangeValue, yChangeValue);

		setVariableFormula(xBrick, variableOneName);
		setVariableFormula(yBrick, variableTwoName);

		StorageHandler.getInstance().saveProject(project);
	}

	private void createUserVariableProject() {
		String userBrickParameterVariableName = "variable1";

		UserScriptDefinitionBrick definitionBrick = new UserScriptDefinitionBrick();
		definitionBrick.addUIText("test");
		definitionBrick.addUILocalizedVariable(userBrickParameterVariableName);
		Formula variableOneFormula = new Formula(new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, userBrickParameterVariableName, null));
		ChangeXByNBrick xBrick = new ChangeXByNBrick(variableOneFormula);
		definitionBrick.getUserScript().addBrick(xBrick);

		UserBrick userBrick = new UserBrick(definitionBrick);
		userBrick.updateUserBrickParametersAndVariables();

		ProjectManager projectManager = ProjectManager.getInstance();
		DataContainer dataContainer = projectManager.getCurrentProject().getDefaultScene().getDataContainer();
		dataContainer.addSpriteUserVariable(variableName);
		UserVariable spriteUserVariable = dataContainer.getUserVariable(variableName, sprite);

		StartScript script = new StartScript();
		SetVariableBrick setVariableBrick = new SetVariableBrick(new Formula(xChangeValue), spriteUserVariable);
		script.addBrick(setVariableBrick);
		script.addBrick(userBrick);
		sprite.addScript(script);

		setVariableFormula(userBrick.getUserBrickParameters().get(0), variableName);
		setVariableFormula(xBrick, userBrickParameterVariableName);
		userBrick.updateUserBrickParametersAndVariables();

		StorageHandler.getInstance().saveProject(project);
	}

	private void setValueFormula(UserBrick userBrick, Integer xValue, Integer yValue) {
		List<Formula> formulaList = userBrick.getFormulas();

		Formula xFormula = formulaList.get(0);
		xFormula.setRoot(new FormulaElement(FormulaElement.ElementType.NUMBER, xValue.toString(), null));
		Formula yFormula = formulaList.get(1);
		yFormula.setRoot(new FormulaElement(FormulaElement.ElementType.NUMBER, yValue.toString(), null));

		try {
			assertEquals("userBrick.formula.interpretDouble: ", (float) xValue, xFormula.interpretFloat(sprite));
			assertEquals("userBrick.formula.interpretDouble: ", (float) yValue, yFormula.interpretFloat(sprite));
		} catch (InterpretationException e) {
			Log.e(TAG, "Interpretation Error", e);
		}
	}

	private void setVariableFormula(FormulaBrick formulaBrick, String variableName) {
		List<Formula> formulaList = formulaBrick.getFormulas();

		for (Formula formula : formulaList) {
			formula.setRoot(new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, variableName, null));
		}
	}

	private void playProject() {
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		String programsString = solo.getString(R.string.main_menu_programs);
		solo.waitForText(programsString);
		solo.clickOnButton(programsString);
		solo.waitForActivity(MyProjectsActivity.class);
		solo.waitForText(projectName);
		solo.clickOnText(projectName);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(500);
	}
}
