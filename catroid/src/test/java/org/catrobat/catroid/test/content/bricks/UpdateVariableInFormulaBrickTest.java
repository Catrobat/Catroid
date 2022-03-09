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
package org.catrobat.catroid.test.content.bricks;
import android.content.Context;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.ConcurrentFormulaHashMap;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.test.MockUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.koin.core.module.Module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import kotlin.Lazy;

import static org.catrobat.catroid.test.xmlformat.ClassDiscoverer.getAllSubClassesOf;
import static org.catrobat.catroid.test.xmlformat.ClassDiscoverer.removeAbstractClasses;
import static org.catrobat.catroid.test.xmlformat.ClassDiscoverer.removeInnerClasses;
import static org.junit.Assert.assertEquals;
import static org.koin.java.KoinJavaComponent.inject;

@RunWith(Parameterized.class)
public class UpdateVariableInFormulaBrickTest {

	private UserList userList;
	private UserVariable userVariable;
	private FormulaBrick formulaBrick;
	private static final String VARIABLE_NAME = "Test";
	private static final String VARIABLE_NAME_USERVARIABLE_FORMAT = "\"Test\" ";
	private static final String VARIABLE_NAME_USERLIST_FORMAT = "*Test* ";
	private static final String INVALID_NAME = "Abcd";
	private static final String NEW_VARIABLE_NAME = "NewName";
	private static final String NEW_VARIABLE_USERVARIABLE_FORMAT = "\"NewName\" ";
	private static final String NEW_VARIABLE_USERLIST_FORMAT = "*NewName* ";

	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		List<Object[]> parameters = new ArrayList<>();
		Set<Class<? extends FormulaBrick>> formulaClasses = getAllSubClassesOf(FormulaBrick.class);
		formulaClasses = removeAbstractClasses(formulaClasses);
		formulaClasses = removeInnerClasses(formulaClasses);
		for (Class<?> formulaClazz : formulaClasses) {
			if (!ScriptBrick.class.isAssignableFrom(formulaClazz)) {
				parameters.add(new Object[] {formulaClazz.getName(), formulaClazz});
			}
		}
		return parameters;
	}

	@Parameterized.Parameter
	public String simpleName;
	@Parameterized.Parameter(1)
	public Class formulaClass;

	@Before
	public void setUp() throws IllegalAccessException, InstantiationException {
		Context context = MockUtil.mockContextForProject(dependencyModules);
		Project project = new Project(context, "testProject");
		userVariable = new UserVariable();
		userList = new UserList();
		Scene scene = new Scene();
		Sprite sprite = new Sprite();
		Script script = new WhenScript();
		formulaBrick = (FormulaBrick) formulaClass.newInstance();

		userVariable.setName(VARIABLE_NAME);
		userList.setName(VARIABLE_NAME);

		project.addScene(scene);
		scene.addSprite(sprite);
		sprite.addScript(script);
		script.addBrick(formulaBrick);

		project.addUserVariable(userVariable);
		project.addUserList(userList);
		projectManager.getValue().setCurrentProject(project);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testRenamingVariable() {
		FormulaElement element = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE,
				VARIABLE_NAME, null);
		Formula newFormula = new Formula(element);
		ConcurrentFormulaHashMap map = formulaBrick.getFormulaMap();
		map.forEach((k, v) -> {
			formulaBrick.setFormulaWithBrickField(k, newFormula);
		});

		projectManager.getValue().getCurrentProject().updateUserDataReferences(VARIABLE_NAME,
				NEW_VARIABLE_NAME,
				userVariable);

		map.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					NEW_VARIABLE_USERVARIABLE_FORMAT);
		});
	}

	@Test
	public void testRenamingVariableNoChanges() {
		FormulaElement element = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE,
				VARIABLE_NAME, null);
		Formula newFormula = new Formula(element);
		ConcurrentFormulaHashMap map = formulaBrick.getFormulaMap();
		map.forEach((k, v) -> {
			formulaBrick.setFormulaWithBrickField(k, newFormula);
		});

		projectManager.getValue().getCurrentProject()
				.updateUserDataReferences(INVALID_NAME, NEW_VARIABLE_NAME, userVariable);

		map.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					VARIABLE_NAME_USERVARIABLE_FORMAT);
		});
	}

	@Test
	public void testRenamingList() {
		FormulaElement element = new FormulaElement(FormulaElement.ElementType.USER_LIST,
				VARIABLE_NAME, null);
		Formula newFormula = new Formula(element);
		ConcurrentFormulaHashMap map = formulaBrick.getFormulaMap();
		map.forEach((k, v) -> {
			formulaBrick.setFormulaWithBrickField(k, newFormula);
		});

		projectManager.getValue().getCurrentProject()
				.updateUserDataReferences(VARIABLE_NAME, NEW_VARIABLE_NAME, userList);

		map.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					NEW_VARIABLE_USERLIST_FORMAT);
		});
	}

	@Test
	public void testRenamingListNoChanges() {
		FormulaElement element = new FormulaElement(FormulaElement.ElementType.USER_LIST,
				VARIABLE_NAME, null);
		Formula newFormula = new Formula(element);
		ConcurrentFormulaHashMap map = formulaBrick.getFormulaMap();
		map.forEach((k, v) -> {
			formulaBrick.setFormulaWithBrickField(k, newFormula);
		});

		projectManager.getValue().getCurrentProject()
				.updateUserDataReferences(INVALID_NAME, NEW_VARIABLE_NAME, userList);

		map.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					VARIABLE_NAME_USERLIST_FORMAT);
		});
	}
}
