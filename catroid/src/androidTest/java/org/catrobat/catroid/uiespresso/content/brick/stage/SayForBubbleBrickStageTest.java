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
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenTouchDownScript;
import org.catrobat.catroid.content.bricks.SayForBubbleBrick;
import org.catrobat.catroid.rules.FlakyTestRule;
import org.catrobat.catroid.runner.Flaky;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertNotNull;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isFocusable;

@RunWith(AndroidJUnit4.class)
public class SayForBubbleBrickStageTest {
	private Sprite sprite;
	private ScriptEvaluationGateBrick lastBrickInScript;
	private ScriptEvaluationGateBrick firstBrickInScript;

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

	@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
	@Flaky
	@Test
	public void sayForBubbleBrickStageTest() {
		firstBrickInScript.waitUntilEvaluated(3000);
		assertNull(StageActivity.stageListener.getBubbleActorForSprite(sprite));
		onView(isFocusable())
				.perform(click());
		onView(ViewMatchers.isRoot()).perform(CustomActions.wait(1000));
		assertNotNull(StageActivity.stageListener.getBubbleActorForSprite(sprite));
		lastBrickInScript.waitUntilEvaluated(3000);
		assertNull(StageActivity.stageListener.getBubbleActorForSprite(sprite));
	}

	private void createProject() {
		Project project = UiTestUtils.createProjectWithCustomScript("sayBubbleBrickTest",
				new WhenTouchDownScript());
		Script script = UiTestUtils.getDefaultTestScript(project);
		sprite = UiTestUtils.getDefaultTestSprite(project);

		script.addBrick(new SayForBubbleBrick("say something", 2f));
		lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script);

		Script whenStarted = new StartScript();
		firstBrickInScript = ScriptEvaluationGateBrick.appendToScript(whenStarted);
		sprite.addScript(whenStarted);
	}
}
