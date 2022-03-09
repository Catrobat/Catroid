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

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.ConcurrentFormulaHashMap;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.test.PowerMockUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.koin.core.module.Module;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

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

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({CatroidApplication.class})
public class UpdateCollisionActorScriptBrickTest {

	private FormulaBrick formulaBrick;
	private Sprite sprite;
	private static final String VARIABLE_NAME = "Test";
	private static final String DIFFERENT_VARIABLE_NAME = "Abcd";
	private static final String NEW_VARIABLE_NAME = "NewName";
	private static final String REPLACED_VARIABLE = "null(" + NEW_VARIABLE_NAME + ") ";
	private static final String NO_CHANGE_VARIABLE = "null(" + DIFFERENT_VARIABLE_NAME + ") ";
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
			if (ScriptBrick.class.isAssignableFrom(formulaClazz)) {
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
		PowerMockUtil.mockStaticAppContextAndInitializeStaticSingletons(dependencyModules);

		Project project = new Project();
		Scene scene = new Scene();
		sprite = new Sprite(VARIABLE_NAME);
		formulaBrick = (FormulaBrick) formulaClass.newInstance();

		project.addScene(scene);
		scene.addSprite(sprite);
		sprite.addScript(formulaBrick.getScript());

		projectManager.getValue().setCurrentProject(project);
		projectManager.getValue().setCurrentlyEditedScene(scene);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testRenamingChanges() {
		Formula newFormula = new Formula(new FormulaElement(FormulaElement.ElementType.COLLISION_FORMULA,
				VARIABLE_NAME, null));
		ConcurrentFormulaHashMap map = formulaBrick.getFormulaMap();
		map.forEach((k, v) -> {
			formulaBrick.setFormulaWithBrickField(k, newFormula);
		});

		sprite.rename(NEW_VARIABLE_NAME);

		map.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					REPLACED_VARIABLE);
		});
	}

	@Test
	public void testRenamingNoChanges() {
		Formula newFormula = new Formula(new FormulaElement(FormulaElement.ElementType.COLLISION_FORMULA,
				DIFFERENT_VARIABLE_NAME, null));
		ConcurrentFormulaHashMap map = formulaBrick.getFormulaMap();
		map.forEach((k, v) -> {
			formulaBrick.setFormulaWithBrickField(k, newFormula);
		});

		sprite.rename(NEW_VARIABLE_NAME);

		map.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					NO_CHANGE_VARIABLE);
		});
	}
}
