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

import android.media.MediaPlayer;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;

@RunWith(AndroidJUnit4.class)
public class SceneTransitionWithSoundBrickStageTest {

	private ScriptEvaluationGateBrick lastBrickFirstScript;
	private ScriptEvaluationGateBrick lastBrickSecondScript;

	private String firstSceneName;
	private Script secondScript;

	private static ProjectManager projectManager;
	private File soundFile;

	@Rule
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, false,
			false);

	@BeforeClass
	public static void setUpProjectManager() {
		projectManager = ProjectManager.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		createProject(getClass().getSimpleName());
		baseActivityTestRule.launchActivity(null);
	}

	@After
	public void tearDown() throws IOException {
		TestUtils.deleteProjects(getClass().getSimpleName());
		if (soundFile != null && soundFile.exists()) {
			soundFile.delete();
		}
	}

	@Test
	public void testStopSoundOnSceneTransition() {
		runProject(lastBrickSecondScript);
		assertFalse(getMediaplayer().isPlaying());
	}

	@Test
	public void testContinueSoundAfterSceneTransition() {
		secondScript.addBrick(new SceneTransitionBrick(firstSceneName));
		runProject(lastBrickFirstScript);
		assertTrue(getMediaplayer().isPlaying());
	}

	@Test
	public void testContinueSoundDoesNotStartFromBeginning() {
		secondScript.addBrick(new SceneTransitionBrick(firstSceneName));
		runProject(lastBrickFirstScript);
		MediaPlayer mediaPlayer = getMediaplayer();
		assertTrue(mediaPlayer.isPlaying());
		assertTrue(mediaPlayer.getCurrentPosition() > 50);
	}

	private void runProject(ScriptEvaluationGateBrick scriptBrick) {
		scriptBrick.waitUntilEvaluated(3000);
	}

	private MediaPlayer getMediaplayer() {
		return SoundManager.getInstance().getMediaPlayers().get(0);
	}

	private void createProject(String projectName) throws IOException {
		Project project = UiTestUtils.createDefaultTestProject(projectName);
		Script script = UiTestUtils.getDefaultTestScript(project);
		XstreamSerializer.getInstance().saveProject(project);

		firstSceneName = project.getDefaultScene().getName();
		Scene secondScene = new Scene("Scene 2", project);
		project.addScene(secondScene);

		PlaySoundBrick soundBrick = new PlaySoundBrick();
		soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.testsoundui,
				new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
				"testsoundui.mp3");
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setFile(soundFile);
		soundInfo.setName("testSound");
		soundBrick.setSound(soundInfo);
		script.addBrick(soundBrick);
		script.addBrick(new WaitBrick(500));
		script.addBrick(new SceneTransitionBrick(secondScene.getName()));
		projectManager.getCurrentSprite().getSoundList().add(soundInfo);

		secondScript = new StartScript();
		Sprite secondSprite = new Sprite("Sprite2");
		secondSprite.addScript(secondScript);
		secondScene.addSprite(secondSprite);

		lastBrickFirstScript = ScriptEvaluationGateBrick.appendToScript(script);
		lastBrickSecondScript = ScriptEvaluationGateBrick.appendToScript(secondScript);
	}
}
