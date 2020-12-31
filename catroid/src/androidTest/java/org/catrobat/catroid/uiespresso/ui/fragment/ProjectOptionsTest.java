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

package org.catrobat.catroid.uiespresso.ui.fragment;

import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.asynctask.ProjectSaveTask;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.common.Constants.CATROBAT_EXTENSION;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.common.Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.instanceOf;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ProjectOptionsTest {

	private static final String PROJECT_NAME = "projectName";
	private static final String NEW_PROJECT_NAME = "newProjectName";
	private static final String EXISTING_PROJECT_NAME = "existingProjectName";
	private static final String DESCRIPTION = "myDescription";
	private static final String NOTES_AND_CREDITS = "myNotesAndCredits";
	private static Project project = null;

	@Rule
	public FragmentActivityTestRule<ProjectActivity> baseActivityTestRule =
			new FragmentActivityTestRule<>(ProjectActivity.class, ProjectActivity.EXTRA_FRAGMENT_POSITION,
			ProjectActivity.FRAGMENT_SPRITES);

	@Before
	public void setUp() throws Exception {
		project = UiTestUtils.createEmptyProject(EXISTING_PROJECT_NAME);
		new ProjectSaveTask(project, ApplicationProvider.getApplicationContext()).execute();
		project = UiTestUtils.createEmptyProject(PROJECT_NAME);
		new ProjectSaveTask(project, ApplicationProvider.getApplicationContext()).execute();
		baseActivityTestRule.launchActivity(null);

		openContextualActionModeOverflowMenu();
		onView(withText(R.string.project_options))
				.perform(click());
		onView(withText(R.string.project_options))
				.check(matches(isDisplayed()));
	}

	@After
	public void tearDown() {
		baseActivityTestRule.deleteAllProjects();
	}

	@Test
	public void changeProjectName() {
		onView(allOf(withText(PROJECT_NAME), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(EXISTING_PROJECT_NAME));

		onView(withText(R.string.name_already_exists))
				.check(matches(isDisplayed()));

		onView(allOf(withText(EXISTING_PROJECT_NAME), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(NEW_PROJECT_NAME));

		closeSoftKeyboard();

		File projectFile = new File(DEFAULT_ROOT_DIRECTORY, PROJECT_NAME);
		Assert.assertTrue(projectFile.exists());

		pressBack();

		Assert.assertFalse(projectFile.exists());

		project = ProjectManager.getInstance().getCurrentProject();
		onView(withText(NEW_PROJECT_NAME))
				.check(matches(isDisplayed()));
		Assert.assertEquals(NEW_PROJECT_NAME, project.getName());
	}

	@Test
	public void changeProjectDescription() {
		onView(allOf(withId(R.id.project_options_description), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(DESCRIPTION));

		closeSoftKeyboard();

		pressBack();

		Assert.assertEquals(DESCRIPTION, project.getDescription());
	}

	@Test
	public void changeProjectNotesAndCredits() {
		onView(allOf(withId(R.id.project_options_notes_and_credits), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(NOTES_AND_CREDITS));

		closeSoftKeyboard();

		pressBack();

		Assert.assertEquals(NOTES_AND_CREDITS, project.getNotesAndCredits());
	}

	@Test
	public void checkTags() {
		onView(withText(R.string.project_options_tags))
				.check(doesNotExist());

		pressBack();

		List<String> tagsList =
				new ArrayList<>(Arrays.asList("Game", "Animation", "Tutorial"));

		project.setTags(tagsList);

		openContextualActionModeOverflowMenu();
		onView(withText(R.string.project_options))
				.perform(click());

		onView(withText("Game"))
				.check(matches(isDisplayed()));

		onView(withText("Animation"))
				.check(matches(isDisplayed()));

		onView(withText("Tutorial"))
				.check(matches(isDisplayed()));
	}

	@Test
	public void changeAspectRatio() {
		Assert.assertEquals(ScreenModes.STRETCH, project.getScreenMode());

		onView(withId(R.id.project_options_aspect_ratio))
				.perform(click());

		onView(withId(R.id.project_options_aspect_ratio))
				.check(matches(isNotChecked()));

		pressBack();

		Assert.assertEquals(ScreenModes.MAXIMIZE, project.getScreenMode());
	}

	@Test
	public void uploadProject() {
		onView(withId(R.id.project_options_upload))
				.perform(ViewActions.scrollTo())
				.perform(click());

		onView(anyOf(withId(R.id.upload_layout), withText(R.string.login)))
				.check(matches(isDisplayed()));
	}

	@Test
	public void saveExternal() throws IOException {
		if (EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY.exists()) {
			StorageOperations.deleteDir(EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY);
		}

		onView(withId(R.id.project_options_save_external))
				.perform(ViewActions.scrollTo())
				.perform(click());

		File externalProjectZip = new File(EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY,
				project.getDirectory().getName() + CATROBAT_EXTENSION);
		Assert.assertTrue(externalProjectZip.exists());
	}

	@Test
	public void moreDetails() {
		onView(withId(R.id.project_options_more_details))
				.perform(ViewActions.scrollTo())
				.perform(click());

		onView(withId(R.id.more_details_layout))
				.check(matches(isDisplayed()));
	}

	@Test
	public void deleteProject() {
		onView(withId(R.id.project_options_delete))
				.perform(ViewActions.scrollTo())
				.perform(click());

		onView(withText(UiTestUtils.getResources().getQuantityString(R.plurals.delete_projects, 1)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_confirm_delete)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button2), withText(R.string.no)))
				.check(matches(isDisplayed()));

		File projectFile = new File(DEFAULT_ROOT_DIRECTORY, PROJECT_NAME);
		Assert.assertTrue(projectFile.exists());

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.perform(click());

		Assert.assertFalse(projectFile.exists());
	}
}
