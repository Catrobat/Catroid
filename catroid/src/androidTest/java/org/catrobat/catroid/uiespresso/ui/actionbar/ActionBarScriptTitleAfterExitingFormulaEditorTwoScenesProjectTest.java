/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.actionbar;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.ui.actionbar.utils.ActionBarWrapper.onActionBar;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ActionBarScriptTitleAfterExitingFormulaEditorTwoScenesProjectTest {
	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule =
			new FragmentActivityTestRule<>(
					SpriteActivity.class,
					SpriteActivity.EXTRA_FRAGMENT_POSITION,
					SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		createTwoScenesProject("ActionBarScriptTitleAfterExitingFormulaEditorTwoScenesProjectTest");
		baseActivityTestRule.launchActivity();
	}

	private void createTwoScenesProject(String projectName) {
		Project project = UiTestUtils.createDefaultTestProject(projectName);
		Scene sceneOne = new Scene("testScene1", project);
		Scene sceneTwo = new Scene("testScene2", project);

		project.addScene(sceneOne);
		project.addScene(sceneTwo);

		Script script = UiTestUtils.getDefaultTestScript(project);
		script.addBrick(new ChangeSizeByNBrick(0));
	}

	@Test
	public void actionBarScriptTitleTwoScenesProjectTest() {
		String currentSceneName = ProjectManager.getInstance().getCurrentlyEditedScene().getName();
		String currentSpriteName = ProjectManager.getInstance().getCurrentSprite().getName();
		String scriptsTitle = currentSceneName + ": " + currentSpriteName;

		onActionBar().checkTitleMatches(scriptsTitle);
		onView(withId(R.id.brick_change_size_by_edit_text)).perform(click());
		onActionBar().checkTitleMatches(R.string.formula_editor_title);

		pressBack();
		onActionBar().checkTitleMatches(scriptsTitle);
	}
}
