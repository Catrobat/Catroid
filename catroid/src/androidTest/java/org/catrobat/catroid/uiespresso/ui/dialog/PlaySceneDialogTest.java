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

package org.catrobat.catroid.uiespresso.ui.dialog;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

import static org.koin.java.KoinJavaComponent.inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class PlaySceneDialogTest {

	private Scene firstScene;
	private Scene secondScene;

	@Rule
	public FragmentActivityTestRule<ProjectActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(ProjectActivity.class, ProjectActivity.EXTRA_FRAGMENT_POSITION,
			ProjectActivity.FRAGMENT_SPRITES);

	@Before
	public void setUp() throws Exception {
		createProject("PlaySceneDialogTest");

		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void playSceneDialogTest() {
		onView(withId(R.id.button_play))
				.perform(click());

		String firstSceneRadioButton = String.format(UiTestUtils.getResourcesString(R.string.play_scene_dialog_default), firstScene.getName());
		String secondSceneRadioButton = String.format(UiTestUtils.getResourcesString(R.string.play_scene_dialog_current), secondScene.getName());

		onView(withText(firstSceneRadioButton))
				.check(matches(isChecked()));

		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		assertEquals(firstScene, projectManager.getCurrentlyPlayingScene());
		assertEquals(firstScene, projectManager.getStartScene());

		onView(withText(secondSceneRadioButton))
				.perform(click());

		assertEquals(secondScene, projectManager.getCurrentlyPlayingScene());
		assertEquals(secondScene, projectManager.getStartScene());

		onView(withText(firstSceneRadioButton))
				.perform(click());

		assertEquals(firstScene, projectManager.getCurrentlyPlayingScene());
		assertEquals(firstScene, projectManager.getStartScene());
	}

	private void createProject(String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		firstScene = project.getDefaultScene();
		secondScene = new Scene("secondScene", project);
		project.addScene(secondScene);

		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		projectManager.setCurrentProject(project);
		projectManager.setCurrentlyEditedScene(secondScene);
	}
}
