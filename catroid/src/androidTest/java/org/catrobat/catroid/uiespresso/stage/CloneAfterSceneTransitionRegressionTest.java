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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.CloneBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CloneAfterSceneTransitionRegressionTest {

	private final String scene2Name = "Scene2";

	private ScriptEvaluationGateBrick scene2Started;

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject("CloneAfterSceneTransitionRegressionTest");
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class})
	@Test
	public void testAskBrickEmptyAnswer() {
		scene2Started.waitUntilEvaluated(3000);

		assertEquals(scene2Name, ProjectManager.getInstance().getCurrentlyPlayingScene().getName());
	}

	private void createProject(String projectName) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Scene scene2 = new Scene(scene2Name, project);
		project.addScene(scene2);

		Sprite sprite1 = new Sprite("sprite1");
		Sprite sprite2 = new Sprite("sprite2");

		project.getDefaultScene().addSprite(sprite1);
		project.getDefaultScene().addSprite(sprite2);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
		ProjectManager.getInstance().setCurrentSprite(sprite1);
		ProjectManager.getInstance().setStartScene(project.getDefaultScene());

		Script sprite1StartScript = new StartScript();
		sprite1.addScript(sprite1StartScript);
		sprite1StartScript.addBrick(new SceneStartBrick(scene2Name));

		Script sprite2StartScript = new StartScript();
		sprite2.addScript(sprite2StartScript);
		sprite2StartScript.addBrick(new CloneBrick());

		Sprite scene2SSprite = new Sprite("scene2SSprite");
		scene2.addSprite(scene2SSprite);
		Script scene2StartScript = new StartScript();
		scene2SSprite.addScript(scene2StartScript);
		scene2Started = ScriptEvaluationGateBrick.appendToScript(scene2StartScript);
	}
}
