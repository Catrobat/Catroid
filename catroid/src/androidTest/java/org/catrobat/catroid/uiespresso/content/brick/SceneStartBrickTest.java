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

package org.catrobat.catroid.uiespresso.content.brick;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.utils.SpinnerUtils.checkIfSpinnerOnBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.utils.SpinnerUtils.clickSelectCheckSpinnerValueOnBrick;

@RunWith(AndroidJUnit4.class)
public class SceneStartBrickTest {
	private int brickPosition;
	private String sceneName;
	private String sceneName2 = "testScene2";

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testSceneStartBrick() {
		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_scene_start);
		checkIfSpinnerOnBrickAtPositionShowsString(R.id.brick_scene_start_spinner, brickPosition, sceneName2);
	}

	@Test
	public void testDismissNewSceneDialog() {
		BrickTestUtils.onScriptList().atPosition(brickPosition).onChildView(withId(R.id.brick_scene_start_spinner))
				.perform(click());
		onView(withText(R.string.brick_variable_spinner_create_new_variable))
				.perform(click());
		closeSoftKeyboard();
		pressBack();

		assertEquals("Not in ScriptActivity", "ui.ScriptActivity",
				UiTestUtils.getCurrentActivity().getLocalClassName());
		checkIfSpinnerOnBrickAtPositionShowsString(R.id.brick_scene_start_spinner, brickPosition, sceneName2);
	}

	@Test
	public void testSelectSceneAndPlay() {
		checkIfSpinnerOnBrickAtPositionShowsString(R.id.brick_scene_start_spinner, brickPosition, sceneName2);
		onView(withId(R.id.button_play))
				.perform(click());

		Scene scene = ProjectManager.getInstance().getSceneToPlay();
		assertEquals("scene not set", scene.getName(), sceneName2);
		pressBack();
		pressBack();

		clickSelectCheckSpinnerValueOnBrick(R.id.brick_scene_start_spinner, brickPosition, sceneName);
		onView(withId(R.id.button_play))
				.perform(click());
		scene = ProjectManager.getInstance().getSceneToPlay();
		assertEquals("scene not set", sceneName, scene.getName());
	}

	private void createProject() {
		String projectName = "sceneStartBrick";

		BrickTestUtils.createProjectAndGetStartScript(projectName)
				.addBrick(new SceneStartBrick(sceneName2));
		brickPosition = 1;

		Project project = ProjectManager.getInstance().getCurrentProject();
		Scene scene2 = new Scene(null, sceneName2, project);
		project.addScene(scene2);
		sceneName = project.getDefaultScene().getName();
	}
}
