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

package org.catrobat.catroid.uiespresso.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.ui.ProjectUploadActivity;
import org.catrobat.catroid.ui.controller.ProjectUploadController;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.catroid.common.Constants.UPLOAD_RESULT_RECEIVER_RESULT_CODE;
import static org.catrobat.catroid.io.asynctask.ProjectSaverKt.saveProjectSerial;
import static org.catrobat.catroid.ui.ProjectUploadActivityKt.NUMBER_OF_UPLOADED_PROJECTS;
import static org.catrobat.catroid.ui.ProjectUploadActivityKt.PROJECT_DIR;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ProjectUploadDialogTest {
	private Bundle bundle;
	private Project project;
	private SharedPreferences sharedPreferences;
	private static final String PROJECT_NAME = ProjectUploadDialogTest.class.getSimpleName();

	@Rule
	public BaseActivityTestRule<ProjectUploadTestActivity> activityTestRule =
			new BaseActivityTestRule<>(ProjectUploadTestActivity.class, false, false);

	@Before
	public void setUp() throws Exception {
		this.sharedPreferences =
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		this.bundle = new Bundle();

		this.project = new Project(getApplicationContext(),
				PROJECT_NAME, false);
		Scene firstScene = new Scene("scene", project);
		ProjectManager.getInstance().setCurrentProject(project);
		Sprite firstSprite = new Sprite("firstSprite");
		Script firstScript = new StartScript();
		firstSprite.addScript(firstScript);
		firstScene.addSprite(firstSprite);
		project.addScene(firstScene);
		saveProjectSerial(project, getApplicationContext());

		Intent intent = new Intent();
		intent.putExtra(PROJECT_DIR, project.getDirectory());
		activityTestRule.launchActivity(intent);
	}

	@After
	public void tearDown() throws Exception {
		sharedPreferences.edit()
				.remove(NUMBER_OF_UPLOADED_PROJECTS)
				.commit();
		ProjectManager.getInstance().setCurrentProject(null);
	}

	@Test
	public void testUploadControllerGetsCalled() {
		sharedPreferences.edit()
				.putInt(NUMBER_OF_UPLOADED_PROJECTS, 1)
				.commit();

		onView(withText(R.string.next))
				.perform(click());
		InstrumentationRegistry.getInstrumentation().waitForIdleSync();

		onView(withText(R.string.next))
				.perform(click());
		InstrumentationRegistry.getInstrumentation().waitForIdleSync();

		onView(withText(R.string.next))
				.perform(click());

		ProjectUploadController projectUploadController =
				activityTestRule.getActivity().getProjectUploadController();

		Looper.prepare();
		verify(projectUploadController)
				.startUpload(PROJECT_NAME, "", "", this.project);
		Looper.myLooper().quit();
	}

	@Test
	public void uploadSuccessRatingDialogShowing() {
		sharedPreferences.edit()
				.putInt(NUMBER_OF_UPLOADED_PROJECTS, 1)
				.commit();

		onView(withId(R.id.next))
				.perform(click());

		onView(withId(R.id.next))
				.perform(click());

		onView(withText(R.string.next))
				.perform(click());

		InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
			activityTestRule.getActivity().showUploadDialog();
			activityTestRule.getActivity().onReceiveResult(UPLOAD_RESULT_RECEIVER_RESULT_CODE, bundle);
		});

		onView(withText(R.string.rating_dialog_rate_now))
				.check(matches(isDisplayed()));
	}

	@Test
	public void firstUploadSuccessRatingDialogNotShowing() {
		sharedPreferences.edit()
				.putInt(NUMBER_OF_UPLOADED_PROJECTS, 0)
				.commit();

		onView(withId(R.id.next))
				.perform(click());

		onView(withId(R.id.next))
				.perform(click());

		onView(withText(R.string.next))
				.perform(click());

		InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
			activityTestRule.getActivity().showUploadDialog();
			activityTestRule.getActivity().onReceiveResult(UPLOAD_RESULT_RECEIVER_RESULT_CODE, bundle);
		});

		onView(withText(R.string.rating_dialog_rate_now))
				.check(doesNotExist());
	}

	@Test
	public void thirdUploadSuccessRatingDialogNotShowing() {
		sharedPreferences.edit()
				.putInt(NUMBER_OF_UPLOADED_PROJECTS, 2)
				.commit();

		onView(withId(R.id.next))
				.perform(click());

		onView(withId(R.id.next))
				.perform(click());

		onView(withText(R.string.next))
				.perform(click());

		InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
			activityTestRule.getActivity().showUploadDialog();
			activityTestRule.getActivity().onReceiveResult(UPLOAD_RESULT_RECEIVER_RESULT_CODE, bundle);
		});

		onView(withText(R.string.rating_dialog_rate_now))
				.check(doesNotExist());
	}

	@Test
	public void uploadFailRatingDialogNotShowing() {
		sharedPreferences.edit()
				.putInt(NUMBER_OF_UPLOADED_PROJECTS, 1)
				.commit();

		onView(withId(R.id.next))
				.perform(click());

		onView(withId(R.id.next))
				.perform(click());

		onView(withText(R.string.next))
				.perform(click());

		InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
			activityTestRule.getActivity().showUploadDialog();
			activityTestRule.getActivity().onReceiveResult(0, bundle);
		});

		onView(withText(R.string.rating_dialog_rate_now))
				.check(doesNotExist());
	}

	@Test
	public void testUploadDefaultProjectName() {
		String defaultProjectName = getApplicationContext().getResources().getString(R.string.default_project_name);
		String errorMessage = getApplicationContext().getResources().getString(R.string.error_upload_project_with_default_name, defaultProjectName);

		onView(withId(R.id.project_upload_name))
				.perform(replaceText(defaultProjectName));

		onView(withText(errorMessage))
				.check(matches(isDisplayed()));
	}

	public static class ProjectUploadTestActivity extends ProjectUploadActivity {
		@NotNull
		@Override
		protected ProjectUploadController createProjectUploadController() {
			projectUploadController = spy(new ProjectUploadController(this));
			return projectUploadController;
		}

		@Override
		protected void verifyUserIdentity() {
			onTokenCheckComplete(true, false);
		}

		public ProjectUploadController getProjectUploadController() {
			return projectUploadController;
		}
	}
}
