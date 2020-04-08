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

package org.catrobat.catroid.uiespresso.content.brick.stage;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.rules.FlakyTestRule;
import org.catrobat.catroid.runner.Flaky;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.catrobat.catroid.utils.VibrationUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertFalse;

@RunWith(AndroidJUnit4.class)
public class SceneTransitionWithVibrationBrickStageTest {

	private ScriptEvaluationGateBrick lastBrickFirstScript;
	private ScriptEvaluationGateBrick lastBrickSecondScript;
	private String firstSceneName;
	private Script secondScript;

	@Rule
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, true, false);

	@Rule
	public FlakyTestRule flakyTestRule = new FlakyTestRule();

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testVibrationStoppedOnSceneTransition() {
		lastBrickSecondScript.waitUntilEvaluated(3000);
		assertFalse(VibrationUtil.isActive());
	}

	@Flaky
	@Test
	public void testVibrationContinueOnSceneTransition() {
		secondScript.addBrick(new SceneTransitionBrick(firstSceneName));
		lastBrickFirstScript.waitUntilEvaluated(3000);
		assertTrue(VibrationUtil.isActive());
	}

	private void createProject() {
		int vibrationDuration = 1;
		Project project = new Project(ApplicationProvider.getApplicationContext(), getClass().getSimpleName());
		ProjectManager.getInstance().setCurrentProject(project);

		Scene firstScene = project.getDefaultScene();
		firstSceneName = firstScene.getName();
		Scene secondScene = new Scene("Scene2", project);

		Script script = new StartScript();
		script.addBrick(new VibrationBrick(vibrationDuration));
		script.addBrick(new SceneTransitionBrick(secondScene.getName()));
		Sprite sprite = new Sprite("Sprite1");
		sprite.addScript(script);
		firstScene.addSprite(sprite);

		secondScript = new StartScript();
		Sprite secondSprite = new Sprite("Sprite2");
		secondSprite.addScript(secondScript);
		secondScene.addSprite(secondSprite);
		project.addScene(secondScene);
		lastBrickFirstScript = ScriptEvaluationGateBrick.appendToScript(script);
		lastBrickSecondScript = ScriptEvaluationGateBrick.appendToScript(secondScript);
	}
}
