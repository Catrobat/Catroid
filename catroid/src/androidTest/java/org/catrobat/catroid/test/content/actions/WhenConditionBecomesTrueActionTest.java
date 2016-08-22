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
package org.catrobat.catroid.test.content.actions;

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenConditionScript;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.WhenConditionBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.HashMap;
import java.util.List;

public class WhenConditionBecomesTrueActionTest extends AndroidTestCase {

	private Sprite testSprite;
	private Project project;

	private static final String COUNTER_VARIABLE_NAME = "counterVariable";
	private static final String CONDITION_VARIABLE_NAME = "conditionVariable";

	private static final double FALSE_CONSTANT = 0.0;
	private static final double TRUE_CONSTANT = 1.0;

	private WhenConditionScript whenConditionScript;
	private UserVariable counterVariable;
	private UserVariable conditionVariable;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testSprite = new Sprite("testSprite");
		project = new Project(null, "testProject");

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(new Sprite("testSprite1"));

		ProjectManager.getInstance().getCurrentProject().getDataContainer().deleteUserVariableByName(COUNTER_VARIABLE_NAME);
		ProjectManager.getInstance().getCurrentProject().getDataContainer().addProjectUserVariable(COUNTER_VARIABLE_NAME);
		counterVariable = ProjectManager.getInstance().getCurrentProject().getDataContainer()
				.getUserVariable(COUNTER_VARIABLE_NAME, null);

		ProjectManager.getInstance().getCurrentProject().getDataContainer().deleteUserVariableByName(CONDITION_VARIABLE_NAME);
		ProjectManager.getInstance().getCurrentProject().getDataContainer().addProjectUserVariable(CONDITION_VARIABLE_NAME);
		conditionVariable = ProjectManager.getInstance().getCurrentProject().getDataContainer()
				.getUserVariable(CONDITION_VARIABLE_NAME, null);
	}

	@Override
	protected void tearDown() throws Exception {
		ProjectManager.getInstance().getCurrentProject().getDataContainer().deleteUserVariableByName(COUNTER_VARIABLE_NAME);
		ProjectManager.getInstance().getCurrentProject().getDataContainer().deleteUserVariableByName(CONDITION_VARIABLE_NAME);
	}

	public void testConditionBecomesTrueOnce() {

		FormulaElement conditionFormulaElement = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE,
				CONDITION_VARIABLE_NAME, null);
		Formula conditionFormula = new Formula(conditionFormulaElement);

		addWhenConditionScriptToSprite(conditionFormula);

		int timesToTrigger = 1;
		runScriptAndTriggerCondition(timesToTrigger);

		assertEquals("script should be executed once", Double.valueOf(timesToTrigger), counterVariable.getValue());
	}

	public void testConditionBecomesTrueMultipleTimes() {

		FormulaElement conditionFormulaElement = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE,
				CONDITION_VARIABLE_NAME, null);
		Formula conditionFormula = new Formula(conditionFormulaElement);

		addWhenConditionScriptToSprite(conditionFormula);

		int timesToTrigger = 5;
		runScriptAndTriggerCondition(timesToTrigger);

		assertEquals("script should be executed 5 times", Double.valueOf(timesToTrigger), counterVariable.getValue());
	}

	public void testConditionConstantTrue() {

		Formula conditionFormula = new Formula(TRUE_CONSTANT);
		addWhenConditionScriptToSprite(conditionFormula);

		int timesToTrigger = 3;
		runScriptAndTriggerCondition(timesToTrigger);

		assertEquals("script should be executed once", 1.0, counterVariable.getValue());
	}

	public void testConditionConstantFalse() {

		Formula conditionFormula = new Formula(FALSE_CONSTANT);
		addWhenConditionScriptToSprite(conditionFormula);

		int timesToTrigger = 3;
		runScriptAndTriggerCondition(timesToTrigger);

		assertEquals("script should not be executed", 0.0, counterVariable.getValue());
	}

	private void addWhenConditionScriptToSprite(Formula conditionFormula) {

		WhenConditionBrick whenBrick = new WhenConditionBrick(conditionFormula);
		whenConditionScript = (WhenConditionScript) whenBrick.getScriptSafe();

		ChangeVariableBrick changeVariableBrick = new ChangeVariableBrick(new Formula(1.0));
		changeVariableBrick.setUserVariable(counterVariable);
		whenConditionScript.addBrick(changeVariableBrick);

		testSprite.addScript(whenConditionScript);

		project.addSprite(testSprite);
		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(whenConditionScript);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());
	}

	private void runScriptAndTriggerCondition(int timesToTrigger) {

		for (int i = 0; i < timesToTrigger; i++) {
			testSprite.getActionFactory().createSetVariableAction(testSprite, new Formula(FALSE_CONSTANT), conditionVariable).act(100f);
			testSprite.look.act(100f);
			testSprite.look.act(100f);
			testSprite.getActionFactory().createSetVariableAction(testSprite, new Formula(TRUE_CONSTANT), conditionVariable).act(100f);
			testSprite.look.act(100f);
			testSprite.look.act(100f);
		}
	}
}
