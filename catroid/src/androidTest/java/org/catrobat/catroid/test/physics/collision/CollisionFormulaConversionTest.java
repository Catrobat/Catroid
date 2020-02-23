/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.test.physics.collision;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.ScreenValueHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Locale;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertEquals;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class CollisionFormulaConversionTest {

	private static final String COLLISION_TEST_PROJECT = "COLLISION_TEST_PROJECT";
	private ProjectManager projectManager;

	@Before
	public void setUp() throws Exception {
		ScreenValueHandler.updateScreenWidthAndHeight(InstrumentationRegistry.getInstrumentation().getContext());
		projectManager = ProjectManager.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		projectManager.setCurrentProject(null);
		TestUtils.deleteProjects(COLLISION_TEST_PROJECT);
		TestUtils.removeFromPreferences(InstrumentationRegistry.getInstrumentation().getContext(), Constants.PREF_PROJECTNAME_KEY);
	}

	@Test
	public void testFormulaUpdated() throws IOException {
		String firstSpriteName = "a";
		String secondSpriteName = "b";
		String thirdSpriteName = "ab";
		String collisionTag = CatroidApplication.getAppContext().getString(R.string
				.formula_editor_function_collision);
		Project project = createProjectWithOldCollisionFormulas(COLLISION_TEST_PROJECT,
				ApplicationProvider.getApplicationContext(),
				firstSpriteName, secondSpriteName, thirdSpriteName, collisionTag);

		ProjectManager.updateCollisionFormulasTo993(project);

		Sprite sprite1 = project.getDefaultScene().getSprite(firstSpriteName);
		Brick brick = sprite1.getScript(0).getBrick(0);

		assertThat(brick, is(instanceOf(FormulaBrick.class)));

		FormulaBrick formulaBrick = (FormulaBrick) brick;
		String newFormula =
				formulaBrick.getFormulas().get(0).getTrimmedFormulaString(ApplicationProvider.getApplicationContext());
		String expected = collisionTag + "(" + thirdSpriteName + ") ";
		assertEquals(expected, newFormula);

		TestUtils.deleteProjects();
	}

	@Test
	public void testFormulaUpdatedWithLanguageConversion() throws IOException {
		String firstSpriteName = "sprite1";
		String secondSpriteName = "sprite2";
		String thirdSpriteName = "sprite3";

		Resources res = CatroidApplication.getAppContext().getResources();
		Configuration conf = res.getConfiguration();
		Locale savedLocale = conf.locale;
		conf.locale = Locale.US;
		res.updateConfiguration(conf, null);
		String collisionTag = res.getString(R.string.formula_editor_function_collision);

		conf.locale = savedLocale;
		res.updateConfiguration(conf, null);

		collisionTag = CatroidApplication.getAppContext().getString(R.string
				.formula_editor_function_collision);

		Project project = createProjectWithOldCollisionFormulas(COLLISION_TEST_PROJECT,
				ApplicationProvider.getApplicationContext(),
				firstSpriteName, secondSpriteName, thirdSpriteName, collisionTag);

		ProjectManager.updateCollisionFormulasTo993(project);

		Sprite sprite1 = project.getDefaultScene().getSprite(firstSpriteName);
		Brick brick = sprite1.getScript(0).getBrick(0);

		assertThat(brick, is(instanceOf(FormulaBrick.class)));

		FormulaBrick formulaBrick = (FormulaBrick) brick;
		String newFormula =
				formulaBrick.getFormulas().get(0).getTrimmedFormulaString(ApplicationProvider.getApplicationContext());
		String expected = collisionTag + "(" + thirdSpriteName + ") ";
		assertEquals(expected, newFormula);

		TestUtils.deleteProjects();
	}

	private Project createProjectWithOldCollisionFormulas(String name, Context context, String firstSprite,
			String secondSprite, String thirdSprite, String collisionTag) {
		Project project = new Project(context, name);
		project.setCatrobatLanguageVersion(0.992f);
		Sprite sprite1 = new Sprite(firstSprite);
		Sprite sprite2 = new Sprite(secondSprite);
		Sprite sprite3 = new Sprite(thirdSprite);

		Script firstScript = new StartScript();

		FormulaElement formulaElement = new FormulaElement(FormulaElement.ElementType.COLLISION_FORMULA,
				firstSprite + " " + collisionTag + " " + thirdSprite, null);
		Formula formula1 = new Formula(formulaElement);

		IfLogicBeginBrick ifBrick = new IfLogicBeginBrick(formula1);
		firstScript.addBrick(ifBrick);

		sprite1.addScript(firstScript);

		project.getDefaultScene().addSprite(sprite1);
		project.getDefaultScene().addSprite(sprite2);
		project.getDefaultScene().addSprite(sprite3);

		ProjectManager projectManager = ProjectManager.getInstance();
		projectManager.setCurrentProject(project);
		return project;
	}
}
