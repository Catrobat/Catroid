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

package org.catrobat.catroid.uiespresso.ui.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.runner.AndroidJUnit4;

import com.facebook.FacebookSdk;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.transfers.DeleteTestUserTask;
import org.catrobat.catroid.ui.recyclerview.activity.ProjectUploadActivity;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.ui.recyclerview.dialog.UploadProgressDialogFragment.NUMBER_OF_UPLOADED_PROJECTS;
import static org.catrobat.catroid.uiespresso.util.matchers.BundleMatchers.bundleHasMatchingString;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
public class ProjectUploadActivityTest implements DeleteTestUserTask.OnDeleteTestUserCompleteListener {
	@Rule
	public BaseActivityInstrumentationRule<ProjectUploadActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProjectUploadActivity.class);
	String token = Constants.NO_TOKEN;
	String projectName = "ProjectUploadActivityTest";
	private String testUser = "testUser" + System.currentTimeMillis();
	String testEmail = testUser + "@gmail.com";
	private String testPassword = "pwspws";
	private Matcher expectedIntent;
	private int sharedPreferenceBuffer;
	private SharedPreferences sharedPreferences;

	@Before
	public void setUp() throws Exception {
		ServerCalls.useTestUrl = true;
		createProject(projectName);
		FacebookSdk.sdkInitialize(InstrumentationRegistry.getTargetContext());

		boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
				"de", "at", token, InstrumentationRegistry.getTargetContext());

		assertTrue(userRegistered);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
		sharedPreferenceBuffer = sharedPreferences.getInt(NUMBER_OF_UPLOADED_PROJECTS, 0);
		token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		boolean tokenOk = ServerCalls.getInstance().checkToken(token, testUser);
		assertTrue(tokenOk);
		Intent intent = new Intent(InstrumentationRegistry.getTargetContext(), ProjectUploadActivity.class)
				.putExtra(ProjectUploadActivity.PROJECT_NAME, Utils.getCurrentProjectName(InstrumentationRegistry.getTargetContext()));

		// to avoid showing RatePocketCodeDialogFragment during the test
		sharedPreferences.edit()
				.putInt(NUMBER_OF_UPLOADED_PROJECTS, 2018)
				.commit();
		baseActivityTestRule.launchActivity(intent);
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(projectName);
		sharedPreferences.edit().putInt(NUMBER_OF_UPLOADED_PROJECTS, sharedPreferenceBuffer).commit();
		ServerCalls.useTestUrl = false;
	}

	@Test
	public void testUploadDialog() {
		Intents.init();
		onView(withId(R.id.project_upload_name))
				.check(matches(withText(containsString(projectName))));
		onView(withId(R.id.next))
				.check(matches(isDisplayed()))
				.check(matches(isEnabled()))
				.perform(click());

		// check if upload dialog is shown
		onView(withText(R.string.upload_tag_dialog_title))
				.check(matches(isDisplayed()));
		onView(withId(android.R.id.button1))
				.check(matches(isDisplayed()));
		onView(withId(android.R.id.button2))
				.check(matches(isDisplayed()))
				.perform(click());

		onView(withId(R.id.next))
				.perform(click());
		onView(withId(android.R.id.button1))
				.perform(click());

		expectedIntent = allOf(hasExtras(bundleHasMatchingString(ProjectUploadActivity.PROJECT_NAME, Utils.getCurrentProjectName(InstrumentationRegistry.getTargetContext()))));
		Intent resultData = new Intent();
		Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

		intending(expectedIntent).respondWith(result);

		onView(withId(R.id.dialog_upload_progess_message))
				.check(matches(isDisplayed()));
		onView(withId(android.R.id.button2))
				.check(matches(isDisplayed()))
				.perform(click());

		assertTrue(baseActivityTestRule.getActivity().isFinishing());
	}

	private void createProject(String projectName) throws IOException {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		XstreamSerializer.getInstance().saveProject(project);

		Sprite sprite = new SingleSprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
		ProjectManager.getInstance().setCurrentSprite(sprite);

		File imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.drawable.catroid_banzai,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				"catroid_sunglasses.png",
				1);

		File imageFile2 = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.drawable.catroid_sunglasses,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				"catroid_sunglasses.png",
				1);

		List<LookData> lookDataList = ProjectManager.getInstance().getCurrentSprite().getLookList();
		LookData lookData = new LookData();
		lookData.setFile(imageFile);
		lookData.setName("testLook1");
		lookDataList.add(lookData);

		LookData lookData2 = new LookData();
		lookData2.setFile(imageFile2);
		lookData2.setName("testLook2");
		lookDataList.add(lookData2);
	}

	@Override
	public void onDeleteTestUserComplete(Boolean deleted) {
		assertTrue(deleted);
	}
}
