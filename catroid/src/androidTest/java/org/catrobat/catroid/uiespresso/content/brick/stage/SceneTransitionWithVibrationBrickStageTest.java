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

package org.catrobat.catroid.uiespresso.content.brick.stage;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SceneTransitionWithVibrationBrickStageTest {

	private ScriptEvaluationGateBrick lastBrickCalmScript;
	private ScriptEvaluationGateBrick lastBrickTransitionScript;
	private String firstSceneName;
	private String calmSceneName = "CalmScene";
	private Script calmScript;
	private Project project;

	private double vibrationDurationInSeconds = 0.2;
	private int waitDurationInMilliseconds = 100;

	@Rule
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		project = UiTestUtils.createDefaultTestProject(TestUtils.DEFAULT_TEST_PROJECT_NAME);
		createVibrationScene();
		createCalmScene();
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testVibrationStoppedOnSceneTransition() {
		lastBrickCalmScript.waitUntilEvaluated(3000);
		assertFalse(baseActivityTestRule.getActivity().vibrationManager.hasActiveVibration());
	}

	@Test
	public void testVibrationContinueOnSceneTransition() {
		calmScript.addBrick(new SceneTransitionBrick(firstSceneName));
		lastBrickTransitionScript.waitUntilEvaluated(3000);
		assertTrue(baseActivityTestRule.getActivity().vibrationManager.hasActiveVibration());
	}

	private void createVibrationScene() {
		Scene firstScene = project.getDefaultScene();
		firstSceneName = firstScene.getName();

		Script vibrationScript = new StartScript();
		vibrationScript.addBrick(new VibrationBrick(vibrationDurationInSeconds));

		Script transitionScript = new StartScript();
		transitionScript.addBrick(new WaitBrick(waitDurationInMilliseconds));
		transitionScript.addBrick(new SceneTransitionBrick(calmSceneName));

		Sprite vibrationSprite = new Sprite("VibrationSprite");
		vibrationSprite.addScript(vibrationScript);
		vibrationSprite.addScript(transitionScript);

		firstScene.addSprite(vibrationSprite);

		lastBrickTransitionScript = ScriptEvaluationGateBrick.appendToScript(transitionScript);
	}

	private void createCalmScene() {
		Scene calmScene = new Scene(calmSceneName, project);

		calmScript = new StartScript();
		Sprite calmSprite = new Sprite("CalmSprite");
		calmSprite.addScript(calmScript);
		calmScene.addSprite(calmSprite);
		project.addScene(calmScene);

		lastBrickCalmScript = ScriptEvaluationGateBrick.appendToScript(calmScript);
	}
}
