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
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.WaitUntilBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Operators;

import java.util.HashMap;
import java.util.List;

public class WaitUntilActionTest extends AndroidTestCase {

	private Sprite testSprite;
	private Project project;
	private static final String TEST_USERVARIABLE = "testUservariable";
	private StartScript testScript;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testSprite = new Sprite("testSprite");
		project = new Project(null, "testProject");
		testSprite.removeAllScripts();
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(new Sprite("testSprite1"));
		ProjectManager.getInstance().getCurrentScene().getDataContainer().deleteUserVariableByName(TEST_USERVARIABLE);
		ProjectManager.getInstance().getCurrentScene().getDataContainer().addProjectUserVariable(TEST_USERVARIABLE);
	}

	public void testWaitUntilBrick() {

		Formula validFormula = new Formula(1);
		validFormula.setRoot(new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
				new FormulaElement(FormulaElement.ElementType.NUMBER, "1", null), new FormulaElement(FormulaElement.ElementType.NUMBER, "2", null)));

		runScript(validFormula);
		assertTrue("Not all actions are finished", testSprite.look.getAllActionsAreFinished());
	}

	public void testWaitUntilBrickFail() {

		Formula validFormula = new Formula(1);
		validFormula.setRoot(new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
				new FormulaElement(FormulaElement.ElementType.NUMBER, "2", null), new FormulaElement(FormulaElement.ElementType.NUMBER, "1", null)));

		runScript(validFormula);
		assertFalse("All actions are finished", testSprite.look.getAllActionsAreFinished());
	}

	private void runScript(Formula validFormula) {
		testScript = new StartScript();

		WaitUntilBrick waitUntilBrick = new WaitUntilBrick(validFormula);
		testScript.addBrick(waitUntilBrick);
		testSprite.addScript(testScript);
		project.getDefaultScene().addSprite(testSprite);
		ProjectManager.getInstance().setCurrentSprite(testSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		testSprite.look.act(100f);
	}
}
