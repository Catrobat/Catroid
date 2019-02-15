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

package org.catrobat.catroid.uiespresso.formulaeditor.utils;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

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
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorLocaleVariableComputeTest {
	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);
	private Scene firstScene;
	private Scene secondScene;

	@Test
	public void testComputingLocaleVariableFromSecondScene() {
		onView(withId(R.id.brick_set_x_edit_text))
				.perform(click());
		onFormulaEditor()
				.performCompute();
		ProjectManager.getInstance().setCurrentlyPlayingScene(firstScene);
		onView(withId(R.id.formula_editor_compute_dialog_textview))
				.check(matches(withText("0")));
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
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);

		firstScene = project.getDefaultScene();
		secondScene = new Scene("secondScene", project);
		DataContainer dataContainer = secondScene.getDataContainer();
		UserVariable userVariable = new UserVariable("locale");
		dataContainer.addUserVariable(userVariable);

		SetXBrick setXBrick = new SetXBrick(new Formula(new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, userVariable.getName(), null)));
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();
		script.addBrick(setXBrick);
		sprite.addScript(script);
		project.addScene(secondScene);
		secondScene.addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(secondScene);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		return project;
	}
}
