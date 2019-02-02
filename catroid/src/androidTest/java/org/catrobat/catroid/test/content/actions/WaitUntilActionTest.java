/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.WaitUntilBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class WaitUntilActionTest {

	private Sprite testSprite;
	private Project project;
	private static final String TEST_USERVARIABLE = "testUservariable";
	private StartScript testScript;

	@Before
	public void setUp() throws Exception {
		testSprite = new SingleSprite("testSprite");
		project = new Project(InstrumentationRegistry.getTargetContext(), "testProject");
		testSprite.removeAllScripts();
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(new SingleSprite("testSprite1"));
		ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer().removeUserVariable(TEST_USERVARIABLE);
		UserVariable userVariable = new UserVariable(TEST_USERVARIABLE);
		ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer().addUserVariable(userVariable);
	}

	@Test
	public void testWaitUntilBrick() {

		Formula validFormula = new Formula(1);
		validFormula.setRoot(new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
				new FormulaElement(FormulaElement.ElementType.NUMBER, "1", null), new FormulaElement(FormulaElement.ElementType.NUMBER, "2", null)));

		runScript(validFormula);
		assertTrue(testSprite.look.haveAllThreadsFinished());
	}

	@Test
	public void testWaitUntilBrickFail() {

		Formula validFormula = new Formula(1);
		validFormula.setRoot(new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.SMALLER_THAN.name(), null,
				new FormulaElement(FormulaElement.ElementType.NUMBER, "2", null), new FormulaElement(FormulaElement.ElementType.NUMBER, "1", null)));

		runScript(validFormula);
		assertFalse(testSprite.look.haveAllThreadsFinished());
	}

	private void runScript(Formula validFormula) {
		testScript = new StartScript();

		WaitUntilBrick waitUntilBrick = new WaitUntilBrick(validFormula);
		testScript.addBrick(waitUntilBrick);
		testSprite.addScript(testScript);
		project.getDefaultScene().addSprite(testSprite);
		ProjectManager.getInstance().setCurrentSprite(testSprite);
		testSprite.initializeEventThreads(EventId.START);

		testSprite.look.act(100f);
	}
}
