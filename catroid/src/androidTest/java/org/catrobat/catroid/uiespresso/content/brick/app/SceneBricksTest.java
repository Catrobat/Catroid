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

package org.catrobat.catroid.uiespresso.content.brick.app;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class SceneBricksTest {
	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		String sceneName = "New Scene";
		Script script = UiTestUtils.createProjectAndGetStartScript(this.getClass().getSimpleName());

		script.addBrick(new SceneStartBrick(sceneName));
		script.addBrick(new SceneStartBrick(sceneName));
		script.addBrick(new SceneTransitionBrick(sceneName));
		script.addBrick(new SceneTransitionBrick(sceneName));
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testAllSceneBricksSpinnersShowTheNewAddedScene() {
		String newSceneName =
				ApplicationProvider.getApplicationContext().getString(R.string.default_scene_name) + " (1)";
		onBrickAtPosition(1)
				.onSpinner(R.id.brick_scene_start_spinner)
				.performSelectNameable(R.string.new_option);
		onView(withId(android.R.id.button1))
				.perform(click());

		onBrickAtPosition(1)
				.onSpinner(R.id.brick_scene_start_spinner)
				.checkShowsText(newSceneName);

		List<String> sceneStartBrickSpinnerValues = new ArrayList<>();
		sceneStartBrickSpinnerValues.add(ApplicationProvider.getApplicationContext().getString(R.string.new_option));
		sceneStartBrickSpinnerValues.add(ApplicationProvider.getApplicationContext().getString(R.string.default_scene_name));
		sceneStartBrickSpinnerValues.add(newSceneName);

		List<String> sceneTransitionBrickSpinnerValues = new ArrayList<>();
		sceneTransitionBrickSpinnerValues.add(ApplicationProvider.getApplicationContext().getString(R.string.new_option));
		sceneTransitionBrickSpinnerValues.add(newSceneName);

		onBrickAtPosition(2)
				.onSpinner(R.id.brick_scene_start_spinner)
				.checkNameableValuesAvailable(sceneStartBrickSpinnerValues);

		onBrickAtPosition(3)
				.onSpinner(R.id.brick_scene_transition_spinner)
				.checkNameableValuesAvailable(sceneTransitionBrickSpinnerValues);
		onBrickAtPosition(4)
				.onSpinner(R.id.brick_scene_transition_spinner)
				.checkNameableValuesAvailable(sceneTransitionBrickSpinnerValues);
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(this.getClass().getSimpleName());
	}
}
