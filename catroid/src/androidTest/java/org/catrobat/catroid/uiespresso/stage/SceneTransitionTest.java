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

package org.catrobat.catroid.uiespresso.stage;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableEqualsWithTimeout;
import static org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableNotEqualsForTimeMs;

public class SceneTransitionTest {
	private static final String VARIABLE_NAME = "var1";
	private static final String SCENE_2_NAME = "Scene 2";

	private UserVariable userVariable;
	private Project project;
	private Sprite sprite1;
	private Script scene1StartScript1;
	private Script scene1StartScript2;

	private Script scene2StartScript;

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void suspendAllThreadsOnSceneTransition() {
		scene1StartScript1.addBrick(new SceneTransitionBrick(SCENE_2_NAME));
		scene1StartScript2.addBrick(new SetVariableBrick(new Formula(10.0), userVariable));
		scene2StartScript.addBrick(new ChangeVariableBrick(new Formula(1.0), userVariable));

		baseActivityTestRule.launchActivity(null);

		assertUserVariableEqualsWithTimeout(userVariable, 1.0, 1000);
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void continueSuspendedThreadsOnContinueScene() {
		scene1StartScript1.addBrick(new SceneTransitionBrick(SCENE_2_NAME));
		scene1StartScript1.addBrick(new ChangeVariableBrick(new Formula(1.0), userVariable));
		scene1StartScript2.addBrick(new ChangeVariableBrick(new Formula(0.0), userVariable)); // do nothing
		scene1StartScript2.addBrick(new ChangeVariableBrick(new Formula(10.0), userVariable));
		scene2StartScript.addBrick(new SceneTransitionBrick(project.getDefaultScene().getName()));

		baseActivityTestRule.launchActivity(null);

		assertUserVariableEqualsWithTimeout(userVariable, 11.0, 1000);
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void discardSuspendedThreadsOnStartScene() {
		scene1StartScript1.addBrick(new SceneTransitionBrick(SCENE_2_NAME));
		scene1StartScript1.addBrick(new SetVariableBrick(new Formula(1.0), userVariable));
		scene2StartScript.addBrick(new SetVariableBrick(new Formula(2.0), userVariable));
		scene2StartScript.addBrick(new SceneStartBrick(project.getDefaultScene().getName()));

		baseActivityTestRule.launchActivity(null);

		assertUserVariableEqualsWithTimeout(userVariable, 2.0, 1000);
		assertUserVariableNotEqualsForTimeMs(userVariable, 1.0, 200);
	}

	private void createProject() {
		project = UiTestUtils.createEmptyProject("test");
		DataContainer dataContainer = project.getDefaultScene().getDataContainer();
		userVariable = new UserVariable(VARIABLE_NAME, 0.0);
		dataContainer.addUserVariable(userVariable);

		sprite1 = project.getDefaultScene().getBackgroundSprite();
		scene1StartScript1 = new StartScript();
		scene1StartScript2 = new StartScript();
		sprite1.addScript(scene1StartScript2);
		sprite1.addScript(scene1StartScript1);

		Scene scene2 = new Scene(SCENE_2_NAME, project);
		Sprite sprite2 = new Sprite();
		scene2StartScript = new StartScript();
		sprite2.addScript(scene2StartScript);
		scene2.addSprite(sprite2);
		project.addScene(scene2);
	}
}
