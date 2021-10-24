/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import androidx.test.core.app.ApplicationProvider;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.io.asynctask.ProjectSaverKt.saveProjectSerial;
import static org.catrobat.catroid.uiespresso.ui.actionbar.utils.ActionBarWrapper.onActionBar;
import static org.catrobat.catroid.uiespresso.ui.actionbar.utils.ActionModeWrapper.onActionMode;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class ActionBarTitleFullyDisplayedTest {
	@Rule
	public BaseActivityTestRule<ProjectActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(ProjectActivity.class, false, false);

	@Before
	public void setUp() throws Exception {
		createTestProject("ActionBarTitleFullyDisplayedTest");
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void actionBarTitleFullyDisplayedTest() {
		String currentProjectName = ProjectManager.getInstance().getCurrentProject().getName();

		onActionBar()
				.checkTitleMatches(currentProjectName);

		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.delete))
				.perform(click());

		assertIsTextCompletelyDisplayed(baseActivityTestRule);

		onView(withId(R.id.overflow))
				.perform(click());

		onView(withText(R.string.select_all))
				.perform(click());

		assertIsTextCompletelyDisplayed(baseActivityTestRule);
		onActionMode().checkTitleMatches(UiTestUtils.getResourcesString(R.string.delete) + " 4");
	}

	private void createTestProject(String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		Scene sceneOne = new Scene("testScene1", project);
		Scene sceneTwo = new Scene("testScene2", project);
		Scene sceneThree = new Scene("testScene2", project);

		project.addScene(sceneOne);
		project.addScene(sceneTwo);
		project.addScene(sceneThree);

		ProjectManager.getInstance().setCurrentProject(project);
		saveProjectSerial(project, ApplicationProvider.getApplicationContext());
	}

	public void assertIsTextCompletelyDisplayed(BaseActivityTestRule<ProjectActivity> activity) {
		TextView text = activity.getActivity().findViewById(R.id.action_bar_title);
		assertEquals(text.getLayout().getEllipsisCount(text.getLayout().getLineCount() - 1), 0);
	}
}
