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
package org.catrobat.catroid.test.formulaeditor.parser;

import android.content.Context;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.UserDataWrapper;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.koin.core.module.Module;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import kotlin.Lazy;

import static junit.framework.Assert.assertEquals;

import static org.koin.java.KoinJavaComponent.inject;

@RunWith(JUnit4.class)
public class ParserTestUserVariables {

	private static final double USER_VARIABLE_1_VALUE_TYPE_DOUBLE = 5d;
	private static final String PROJECT_USER_VARIABLE = "projectUserVariable";
	private static final double USER_VARIABLE_2_VALUE_TYPE_DOUBLE = 3.141592d;
	private static final String SPRITE_USER_VARIABLE = "spriteUserVariable";
	private static final double USER_VARIABLE_RESET = 0.0d;
	private static final String USER_VARIABLE_3_VALUE_TYPE_STRING = "My Little User Variable";
	private static final String PROJECT_USER_VARIABLE_2 = "projectUserVariable2";

	private Project project;
	private Sprite firstSprite;
	private Scope scope;

	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() {
		Context context = MockUtil.mockContextForProject(dependencyModules);
		project = new Project(context, "testProject");
		firstSprite = new Sprite("firstSprite");
		StartScript startScript = new StartScript();
		ChangeSizeByNBrick changeBrick = new ChangeSizeByNBrick(10);
		firstSprite.addScript(startScript);
		startScript.addBrick(changeBrick);
		project.getDefaultScene().addSprite(firstSprite);
		projectManager.getValue().setCurrentProject(project);
		projectManager.getValue().setCurrentSprite(firstSprite);

		project.addUserVariable(new UserVariable(PROJECT_USER_VARIABLE, USER_VARIABLE_1_VALUE_TYPE_DOUBLE));
		firstSprite.addUserVariable(new UserVariable(SPRITE_USER_VARIABLE, USER_VARIABLE_2_VALUE_TYPE_DOUBLE));
		project.addUserVariable(new UserVariable(PROJECT_USER_VARIABLE_2, USER_VARIABLE_3_VALUE_TYPE_STRING));

		scope = new Scope(project, firstSprite, new SequenceAction());
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testUserVariableInterpretation() {
		assertEquals(USER_VARIABLE_1_VALUE_TYPE_DOUBLE, interpretUserVariable(PROJECT_USER_VARIABLE));
		assertEquals(USER_VARIABLE_2_VALUE_TYPE_DOUBLE, interpretUserVariable(SPRITE_USER_VARIABLE));
		assertEquals(USER_VARIABLE_3_VALUE_TYPE_STRING, interpretUserVariable(PROJECT_USER_VARIABLE_2));
	}

	@Test
	public void testUserVariableResetting() {
		UserDataWrapper.resetAllUserData(project);

		assertEquals(USER_VARIABLE_RESET, interpretUserVariable(PROJECT_USER_VARIABLE));
		assertEquals(USER_VARIABLE_RESET, interpretUserVariable(SPRITE_USER_VARIABLE));
		assertEquals(USER_VARIABLE_RESET, interpretUserVariable(PROJECT_USER_VARIABLE_2));
	}

	@Test
	public void testNotExistingUserVariable() {
		FormulaEditorTestUtil.testSingleTokenError(InternTokenType.USER_VARIABLE,
				"NOT_EXISTING_USER_VARIABLE", 0, scope);
	}

	private Object interpretUserVariable(String userVariableName) {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.USER_VARIABLE, userVariableName));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula(scope);
		Formula userVariableFormula = new Formula(parseTree);

		return userVariableFormula.interpretObject(scope);
	}
}
