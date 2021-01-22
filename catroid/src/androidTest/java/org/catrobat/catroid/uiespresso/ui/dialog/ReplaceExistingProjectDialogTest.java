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
package org.catrobat.catroid.uiespresso.ui.dialog;

import android.content.Context;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.asynctask.ProjectSaveTask;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.recyclerview.dialog.ReplaceExistingProjectDialogFragment;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.catrobat.catroid.web.ProjectDownloader;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(AndroidJUnit4.class)
public class ReplaceExistingProjectDialogTest {

	@Rule
	public BaseActivityTestRule<ProjectActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(ProjectActivity.class, false, false);

	String[] projectNames = {"Project1", "Project2", "Project3"};

	@Before
	public void setUp() throws Exception {
		createProjects();
		baseActivityTestRule.launchActivity(null);
		ReplaceExistingProjectDialogFragment dialog =
				ReplaceExistingProjectDialogFragment.newInstance(projectNames[0], any(ProjectDownloader.class));
		dialog.show(baseActivityTestRule.getActivity().getSupportFragmentManager(),
				ReplaceExistingProjectDialogFragment.TAG);
	}

	@After
	public void tearDown() {
		baseActivityTestRule.deleteAllProjects();
	}

	@Test
	public void testProjectNameAlreadyExists() {
		onView(withText(R.string.name_already_exists))
				.check(matches(isDisplayed()));

		onView(withText(R.string.ok))
				.check(matches(not(isEnabled())));
	}

	@Test
	public void testProjectNameEmpty() {
		onView(withId(R.id.input_edit_text))
				.perform(replaceText(""));

		onView(withText(R.string.name_empty))
				.check(matches(isDisplayed()));

		onView(withText(R.string.ok))
				.check(matches(not(isEnabled())));
	}

	@Test
	public void testNewProjectName() {
		onView(withId(R.id.input_edit_text))
				.perform(replaceText("newProject"));

		onView(withText(R.string.ok))
				.check(matches(isEnabled()));
	}

	void createProjects() {
		Context context = ApplicationProvider.getApplicationContext();

		Project project = null;

		for (String name : projectNames) {
			project = new Project(context, name);
			ProjectSaveTask.task(project, context);
		}

		ProjectManager.getInstance().setCurrentProject(project);
	}
}
