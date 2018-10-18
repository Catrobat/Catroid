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

package org.catrobat.catroid.uiespresso.ui.dialog;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PlaySceneDialogTest {

	private Scene firstScene;
	private Scene secondScene;

	@Rule
	public BaseActivityInstrumentationRule<ProjectActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProjectActivity.class, ProjectActivity.EXTRA_FRAGMENT_POSITION,
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

		assertEquals(firstScene, ProjectManager.getInstance().getCurrentlyPlayingScene());
		assertEquals(firstScene, ProjectManager.getInstance().getStartScene());

		onView(withText(secondSceneRadioButton))
				.perform(click());

		assertEquals(secondScene, ProjectManager.getInstance().getCurrentlyPlayingScene());
		assertEquals(secondScene, ProjectManager.getInstance().getStartScene());

		onView(withText(firstSceneRadioButton))
				.perform(click());

		assertEquals(firstScene, ProjectManager.getInstance().getCurrentlyPlayingScene());
		assertEquals(firstScene, ProjectManager.getInstance().getStartScene());
	}

	private void createProject(String projectName) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		firstScene = project.getDefaultScene();
		secondScene = new Scene("secondScene", project);
		project.addScene(secondScene);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(secondScene);
	}
}
