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
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.MediaPlayerWithSoundDetails;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PlaySoundAndWaitStageTest {

	private static ProjectManager projectManager;
	private SoundInfo soundInfo;
	private ScriptEvaluationGateBrick scriptEvaluationGateBrick;
	private Project project;
	private UserVariable userVariable;
	private RepeatBrick repeatBrick;

	@BeforeClass
	public static void setUpProjectManager() {
		projectManager = ProjectManager.getInstance();
	}

	@Rule
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, false, false);

	@Before
	public void setUp() throws IOException {
		createProject(getClass().getSimpleName());
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testPlayAndWaitInLoop() {
		scriptEvaluationGateBrick.waitUntilEvaluated(5000);
		List<MediaPlayerWithSoundDetails> mediaPlayers =
				SoundManager.getInstance().getMediaPlayers();
		assertEquals(mediaPlayers.get(0).getDuration(), mediaPlayers.get(0).getCurrentPosition(), 150);
	}

	@Test
	public void testPlayAndWaitStopSameSoundDifferentScript() {
		repeatBrick.setFormulaWithBrickField(Brick.BrickField.TIMES_TO_REPEAT, new Formula(1));
		Script script2 = new StartScript();
		PlaySoundAndWaitBrick soundBrick = new PlaySoundAndWaitBrick();
		soundBrick.setSound(soundInfo);
		script2.addBrick(soundBrick);
		soundBrick.setParent(script2.getScriptBrick());
		projectManager.getCurrentSprite().getSoundList().add(soundInfo);
		project.getDefaultScene().getBackgroundSprite().addScript(script2);
		ScriptEvaluationGateBrick.appendToScript(script2).waitUntilEvaluated(5000);
		assertEquals(1.0, userVariable.getValue());
	}

	private void createProject(String projectName) throws IOException {
		project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		Sprite sprite = project.getDefaultScene().getBackgroundSprite();
		XstreamSerializer.getInstance().saveProject(project);
		project.getDefaultScene().addSprite(sprite);
		projectManager.setCurrentProject(project);
		projectManager.setCurrentSprite(sprite);
		Script script = new StartScript();
		PlaySoundAndWaitBrick soundBrick = new PlaySoundAndWaitBrick();
		File soundFile = TestUtils.createSoundFile(project, org.catrobat.catroid.test.R.raw.testsound2,
				"testsound2.mp3");
		soundInfo = new SoundInfo();
		soundInfo.setFile(soundFile);
		soundInfo.setName("testSound");
		soundBrick.setSound(soundInfo);
		repeatBrick = new RepeatBrick(new Formula(2));
		repeatBrick.addBrick(soundBrick);
		repeatBrick.setParent(script.getScriptBrick());
		userVariable = new UserVariable("variable", 0);
		SetVariableBrick setVariableBrick = new SetVariableBrick(new Formula(1), userVariable);
		script.addBrick(repeatBrick);
		script.addBrick(new WaitBrick(100));
		script.addBrick(setVariableBrick);
		project.getDefaultScene().getBackgroundSprite().addScript(script);
		projectManager.getCurrentSprite().getSoundList().add(soundInfo);
		scriptEvaluationGateBrick = ScriptEvaluationGateBrick.appendToScript(script);
	}
}
