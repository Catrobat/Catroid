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

package org.catrobat.catroid.uiespresso.ui.fragment;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.asynctask.ProjectSaver;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.hamcrest.Matcher;
import org.hamcrest.core.AllOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.catroid.R.id.tab_layout;
import static org.catrobat.catroid.common.Constants.CATROBAT_EXTENSION;
import static org.catrobat.catroid.common.Constants.DOWNLOAD_DIRECTORY;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.onToast;
import static org.catrobat.catroid.uiespresso.util.actions.TabActionsKt.selectTabAtPosition;
import static org.catrobat.catroid.uiespresso.util.matchers.BundleMatchers.bundleHasExtraIntent;
import static org.catrobat.catroid.uiespresso.util.matchers.BundleMatchers.bundleHasMatchingString;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasCategories;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasType;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ProjectOptionsTest {

	private static final String PROJECT_NAME = "projectName";
	private static final String NEW_PROJECT_NAME = "newProjectName";
	private static final String EXISTING_PROJECT_NAME = "existingProjectName";
	private static final String DESCRIPTION = "myDescription";
	private static final String NOTES_AND_CREDITS = "myNotesAndCredits";
	private static final Integer DURATION_WAIT_FOR_ZIP_FILE_IN_MILLISECONDS = 3000;
	private static Project project = null;
	private static Context context = null;

	@Rule
	public FragmentActivityTestRule<ProjectActivity> baseActivityTestRule =
			new FragmentActivityTestRule<>(ProjectActivity.class, ProjectActivity.EXTRA_FRAGMENT_POSITION,
					ProjectActivity.FRAGMENT_SPRITES);

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		context = ApplicationProvider.getApplicationContext();
		project = UiTestUtils.createDefaultTestProject(EXISTING_PROJECT_NAME);
		new ProjectSaver(project, context).saveProjectAsync();
		project = UiTestUtils.createDefaultTestProject(PROJECT_NAME);
		new ProjectSaver(project, context).saveProjectAsync();
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
	public void changeProjectName() throws IOException {
		onView(allOf(withText(PROJECT_NAME), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(EXISTING_PROJECT_NAME));

		onView(withText(R.string.name_already_exists))
				.check(matches(isDisplayed()));

		onView(allOf(withText(EXISTING_PROJECT_NAME), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(NEW_PROJECT_NAME));

		closeSoftKeyboard();

		File projectFile = new File(DEFAULT_ROOT_DIRECTORY, PROJECT_NAME);
		assertTrue(projectFile.exists());

		pressBack();

		assertFalse(projectFile.exists());

		project = ProjectManager.getInstance().getCurrentProject();
		onView(withText(NEW_PROJECT_NAME))
				.check(matches(isDisplayed()));
		assertEquals(NEW_PROJECT_NAME, project.getName());

		onView(withText(R.string.default_project_background_name))
				.perform(click());

		onView(withId(tab_layout)).perform(selectTabAtPosition(1));

		onView(withId(R.id.button_add))
				.perform(click());

		Matcher<Intent> expectedPaintNewLookIntent = createLookFromPaintroid();

		onView(withId(R.id.dialog_new_look_paintroid))
				.perform(click());

		intended(expectedPaintNewLookIntent);

		onRecyclerView().atPosition(0).onChildView(R.id.title_view)
				.check(matches(withText(R.string.default_project_background_name)));
	}

	private Matcher<Intent> createLookFromPaintroid() throws IOException {
		String lookFileName = "catroid_sunglasses.png";

		Intents.init();

		Matcher<Intent> expectedGetContentIntent = AllOf.allOf(
				hasAction("android.intent.action.GET_CONTENT"),
				hasType("image/*"));

		String chooserTitle = UiTestUtils.getResourcesString(R.string.select_look_from_gallery);
		Matcher<Intent> expectedChooserIntent = AllOf.allOf(
				hasAction("android.intent.action.CHOOSER"),
				hasExtras(bundleHasMatchingString("android.intent.extra.TITLE", chooserTitle)),
				hasExtras(bundleHasExtraIntent(expectedGetContentIntent)));

		File imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.drawable.catroid_banzai,
				tmpFolder.getRoot(),
				lookFileName,
				1);

		Intent resultData = new Intent();
		resultData.setData(Uri.fromFile(imageFile));

		Instrumentation.ActivityResult result =
				new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

		intending(expectedChooserIntent).respondWith(result);

		Matcher<Intent> expectedPaintNewLookIntent = AllOf.allOf(
				hasComponent(Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME),
				hasAction("android.intent.action.MAIN"),
				hasCategories(hasItem(equalTo("android.intent.category.LAUNCHER"))));

		Instrumentation.ActivityResult resultPaintroid = new Instrumentation.ActivityResult(Activity.RESULT_OK, null);

		intending(expectedPaintNewLookIntent).respondWith(resultPaintroid);

		return expectedPaintNewLookIntent;
	}

	@Test
	public void changeProjectDescription() {
		onView(allOf(withId(R.id.project_options_description), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(DESCRIPTION));

		closeSoftKeyboard();

		pressBack();

		project = ProjectManager.getInstance().getCurrentProject();
		assertEquals(DESCRIPTION, project.getDescription());
	}

	@Test
	public void changeProjectNotesAndCredits() {
		onView(allOf(withId(R.id.project_options_notes_and_credits), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(NOTES_AND_CREDITS));

		closeSoftKeyboard();

		pressBack();

		project = ProjectManager.getInstance().getCurrentProject();
		assertEquals(NOTES_AND_CREDITS, project.getNotesAndCredits());
	}

	@Test
	public void checkTags() {
		onView(withText(R.string.project_options_tags))
				.check(doesNotExist());

		pressBack();

		List<String> tagsList =
				new ArrayList<>(Arrays.asList("Game", "Animation", "Tutorial"));

		project = ProjectManager.getInstance().getCurrentProject();
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
		assertEquals(ScreenModes.STRETCH, project.getScreenMode());

		onView(withId(R.id.project_options_aspect_ratio))
				.perform(click());

		onView(withId(R.id.project_options_aspect_ratio))
				.check(matches(isChecked()));

		pressBack();

		assertEquals(ScreenModes.MAXIMIZE, project.getScreenMode());
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
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

			String pendingToastText =
					context.getString(R.string.notification_save_project_to_external_storage_pending);
			File externalProjectZip = new File(DOWNLOAD_DIRECTORY,
					project.getDirectory().getName() + CATROBAT_EXTENSION);
			if (externalProjectZip.exists()) {
				StorageOperations.deleteFile(externalProjectZip);
			}
			assertFalse(externalProjectZip.exists());

			onView(withId(R.id.project_options_save_external))
					.perform(ViewActions.scrollTo())
					.perform(click());

			onToast(withText(pendingToastText))
					.check(matches(isDisplayed()));
			onView(isRoot()).perform(CustomActions
					.wait(DURATION_WAIT_FOR_ZIP_FILE_IN_MILLISECONDS));

			assertTrue(externalProjectZip.exists());
		}
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
		assertTrue(projectFile.exists());

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.perform(click());

		assertFalse(projectFile.exists());
	}
}
