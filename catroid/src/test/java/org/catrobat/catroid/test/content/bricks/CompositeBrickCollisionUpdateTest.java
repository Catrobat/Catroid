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
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.CompositeBrick;
import org.catrobat.catroid.content.bricks.ConcurrentFormulaHashMap;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static org.junit.Assert.assertEquals;
import static org.koin.java.KoinJavaComponent.inject;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({CatroidApplication.class})
public class CompositeBrickCollisionUpdateTest {

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
		return Arrays.asList(new Object[][] {
				{IfThenLogicBeginBrick.class.getSimpleName(), IfThenLogicBeginBrick.class},
				{ForeverBrick.class.getSimpleName(), ForeverBrick.class},
				{RepeatBrick.class.getSimpleName(), RepeatBrick.class},
				{RepeatUntilBrick.class.getSimpleName(), RepeatUntilBrick.class},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Class<CompositeBrick> compositeBrickClass;

	@Before
	public void setUp() throws IllegalAccessException, InstantiationException {
		PowerMockUtil.mockStaticAppContextAndInitializeStaticSingletons(dependencyModules);

		Project project = new Project();
		Scene scene = new Scene();
		sprite = new Sprite(VARIABLE_NAME);
		Script script = new WhenScript();
		CompositeBrick compositeBrick = compositeBrickClass.newInstance();
		formulaBrick = new SetXBrick();

		project.addScene(scene);
		scene.addSprite(sprite);
		sprite.addScript(script);
		script.addBrick(compositeBrick);
		compositeBrick.getNestedBricks().add(formulaBrick);

		projectManager.getValue().setCurrentProject(project);
		projectManager.getValue().setCurrentlyEditedScene(scene);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testRenameSprite() {
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
	public void testRenameSpriteNoChange() {
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
