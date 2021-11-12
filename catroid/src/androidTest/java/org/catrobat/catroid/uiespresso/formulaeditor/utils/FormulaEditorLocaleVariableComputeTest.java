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

package org.catrobat.catroid.uiespresso.formulaeditor.utils;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorLocaleVariableComputeTest {
	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);
	private Scene firstScene;
	private Scene secondScene;

	@Test
	public void testComputingLocaleVariableFromSecondScene() {
		onView(withId(R.id.brick_set_x_edit_text))
				.perform(click());
		onFormulaEditor()
				.performCompute();
		inject(ProjectManager.class).getValue().setCurrentlyPlayingScene(firstScene);
		onView(withId(R.id.formula_editor_compute_dialog_textview))
				.check(matches(withText("0")));
		pressBack();
	}

	@Before
	public void setUp() throws Exception {
		createProject(FormulaEditorLocaleVariableComputeTest.class.getName());
		baseActivityTestRule.launchActivity();
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(FormulaEditorLocaleVariableComputeTest.class.getName());
	}

	public Project createProject(String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);

		firstScene = project.getDefaultScene();
		secondScene = new Scene("secondScene", project);
		UserVariable userVariable = new UserVariable("locale");
		project.addUserVariable(userVariable);

		SetXBrick setXBrick = new SetXBrick(new Formula(new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, userVariable.getName(), null)));
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();
		script.addBrick(setXBrick);
		sprite.addScript(script);
		project.addScene(secondScene);
		secondScene.addSprite(sprite);
		inject(ProjectManager.class).getValue().setCurrentProject(project);
		inject(ProjectManager.class).getValue().setCurrentlyEditedScene(secondScene);
		inject(ProjectManager.class).getValue().setCurrentSprite(sprite);

		return project;
	}
}
