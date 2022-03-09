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
package org.catrobat.catroid.test.content.actions;

import android.content.Context;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.WaitUntilBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.test.MockUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.koin.core.module.Module;

import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.koin.java.KoinJavaComponent.inject;

@RunWith(JUnit4.class)
public class WaitUntilActionTest {

	private Sprite testSprite;
	private Project project;
	private static final String TEST_USERVARIABLE = "testUservariable";
	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() throws Exception {
		testSprite = new Sprite("testSprite");
		Context context = MockUtil.mockContextForProject(dependencyModules);
		project = new Project(context, "testProject");
		testSprite.removeAllScripts();
		projectManager.getValue().setCurrentProject(project);
		projectManager.getValue().setCurrentSprite(new Sprite("testSprite1"));
		project.removeUserVariable(TEST_USERVARIABLE);
		UserVariable userVariable = new UserVariable(TEST_USERVARIABLE);
		project.addUserVariable(userVariable);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
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
		StartScript testScript = new StartScript();

		WaitUntilBrick waitUntilBrick = new WaitUntilBrick(validFormula);
		testScript.addBrick(waitUntilBrick);
		testSprite.addScript(testScript);
		project.getDefaultScene().addSprite(testSprite);
		projectManager.getValue().setCurrentSprite(testSprite);
		testSprite.initializeEventThreads(EventId.START);

		testSprite.look.act(100f);
	}
}
