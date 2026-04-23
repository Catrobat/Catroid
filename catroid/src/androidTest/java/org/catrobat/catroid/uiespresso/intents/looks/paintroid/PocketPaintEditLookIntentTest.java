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

package org.catrobat.catroid.uiespresso.intents.looks.paintroid;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.catrobat.catroid.uiespresso.util.matchers.BundleMatchers.bundleHasMatchingString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.AllOf.allOf;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasCategories;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class PocketPaintEditLookIntentTest {

	private Matcher expectedIntent;
	private final String lookName = "catroid_sunglasses";
	private final String projectName = getClass().getSimpleName();
	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_LOOKS);

	@Before
	public void setUp() throws Exception {

		Project project = createProject(projectName);
		File lookFolder = new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME);
		File lookFile = new File(lookFolder, "catroid_sunglasses.png");

		baseActivityTestRule.launchActivity();
		Intents.init();

		expectedIntent = allOf(
				hasComponent(Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME),
				hasAction("android.intent.action.MAIN"),
				hasCategories(hasItem(equalTo("android.intent.category.LAUNCHER"))),
				hasExtras(bundleHasMatchingString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, lookFile.getAbsolutePath())));

		Intent resultData = new Intent();

		resultData.putExtra(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, lookFile.getAbsolutePath());
		Instrumentation.ActivityResult result =
				new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

		intending(expectedIntent).respondWith(result);
	}

	@After
	public void tearDown() {
		Intents.release();
		baseActivityTestRule.finishActivity();
		try {
			StorageOperations.deleteDir(new File(DEFAULT_ROOT_DIRECTORY, projectName));
		} catch (IOException e) {
			Log.d(getClass().getSimpleName(), "Cannot clean up project");
		}
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testOpenPaintroid() {
		onRecyclerView().atPosition(0).perform(click());

		intended(expectedIntent);

		onRecyclerView().atPosition(0).onChildView(R.id.title_view)
				.check(matches(withText(lookName)));
	}

	private Project createProject(String projectName) throws IOException {
		Project project = UiTestUtils.createDefaultTestProject(projectName);
		XstreamSerializer.getInstance().saveProject(project);

		File imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.drawable.catroid_banzai,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				"catroid_sunglasses.png",
				1);

		List<LookData> lookDataList = ProjectManager.getInstance().getCurrentSprite().getLookList();
		LookData lookData = new LookData();
		lookData.setFile(imageFile);
		lookData.setName(lookName);
		lookDataList.add(lookData);
		return project;
	}
}
