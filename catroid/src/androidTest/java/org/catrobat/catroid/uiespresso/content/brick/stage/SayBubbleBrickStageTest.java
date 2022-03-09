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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SayBubbleBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertNotNull;

import static org.koin.java.KoinJavaComponent.inject;

@RunWith(AndroidJUnit4.class)
public class SayBubbleBrickStageTest {
	private Sprite sprite;
	private ScriptEvaluationGateBrick lastBrickInScript;

	@Rule
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject("sayBubbleBrickTest");
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Test
	public void sayBubbleBrickStageTest() {
		lastBrickInScript.waitUntilEvaluated(3000);
		assertNotNull(StageActivity.stageListener.getBubbleActorForSprite(sprite));
	}

	private void createProject(String projectName) {
		String sayString = "say something";

		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		sprite = new Sprite("testSprite");
		Script script = new StartScript();
		script.addBrick(new SayBubbleBrick(sayString));
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		projectManager.setCurrentProject(project);
		projectManager.setCurrentSprite(sprite);
		lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script);
	}
}
