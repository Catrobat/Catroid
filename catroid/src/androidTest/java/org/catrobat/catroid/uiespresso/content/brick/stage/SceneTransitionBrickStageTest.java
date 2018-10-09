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

package org.catrobat.catroid.uiespresso.content.brick.stage;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.util.UserVariableAssertions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SceneTransitionBrickStageTest {

	private UserVariable firstVariable = new UserVariable("firstVar");
	private UserVariable secondVariable = new UserVariable("secondVar");

	private String firstSceneBeforeTransitionVariableValue = "firstSceneFirstTime";
	private String secondSceneVariableValue = "secondSceneFirstTime";
	private String firstSceneAfterTransitionVariableValue = "firstSceneSecondTime";

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testContinueScene() {
		UserVariableAssertions.assertUserVariableContainsStringWithTimeout(firstVariable, firstSceneBeforeTransitionVariableValue, 10);
		UserVariableAssertions.assertUserVariableContainsStringWithTimeout(secondVariable, secondSceneVariableValue, 10);
		UserVariableAssertions.assertUserVariableContainsStringWithTimeout(firstVariable, firstSceneAfterTransitionVariableValue, 1000);
	}

	private void createProject() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), getClass().getSimpleName());

		Scene firstScene = project.getDefaultScene();
		Scene secondScene = new Scene("Scene 2", project);

		firstVariable = new UserVariable("firstVar");
		firstScene.getDataContainer().addUserVariable(firstVariable);

		secondVariable = new UserVariable("secondVar");
		secondScene.getDataContainer().addUserVariable(secondVariable);

		Sprite firstBackground = firstScene.getBackgroundSprite();
		Script firstStartScript = new StartScript();

		firstStartScript.addBrick(new SetVariableBrick(new Formula(firstSceneBeforeTransitionVariableValue), firstVariable));
		firstStartScript.addBrick(new SceneTransitionBrick(secondScene.getName()));
		firstStartScript.addBrick(new WaitBrick(500));
		firstStartScript.addBrick(new SetVariableBrick(new Formula(firstSceneAfterTransitionVariableValue), firstVariable));

		firstBackground.addScript(firstStartScript);

		Sprite secondBackground = new Sprite("Background");
		Script secondStartScript = new StartScript();

		secondStartScript.addBrick(new SetVariableBrick(new Formula(secondSceneVariableValue), secondVariable));
		secondStartScript.addBrick(new SceneTransitionBrick(firstScene.getName()));

		secondBackground.addScript(secondStartScript);
		secondScene.addSprite(secondBackground);

		project.addScene(secondScene);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().saveProject(InstrumentationRegistry.getTargetContext());
	}
}
