/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SayForBubbleBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.annotations.Flaky;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class SayForBubbleBrickStageTest {
	private Sprite sprite;
	private ScriptEvaluationGateBrick lastBrickInScript;

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject("sayBubbleBrickTest");
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Flaky
	@Test
	public void sayForBubbleBrickStageTest() {
		onView(isRoot()).perform(CustomActions.wait(1000));
		assertNotNull(StageActivity.stageListener.getBubbleActorForSprite(sprite));
		lastBrickInScript.waitUntilEvaluated(3000);
		assertNull(StageActivity.stageListener.getBubbleActorForSprite(sprite));
	}

	private void createProject(String projectName) {
		String sayString = "say something";
		float duration = 2f;
		Project project = new Project(null, projectName);
		sprite = new Sprite("testSprite");
		Script script = new StartScript();
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		script.addBrick(new SayForBubbleBrick(sayString, duration));
		lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script);
	}
}
