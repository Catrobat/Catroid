/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.intents.looks.gallery;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectActivity;
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

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.catrobat.catroid.uiespresso.util.matchers.BundleMatchers.bundleHasExtraIntent;
import static org.catrobat.catroid.uiespresso.util.matchers.BundleMatchers.bundleHasMatchingString;
import static org.hamcrest.core.AllOf.allOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasType;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SpriteFromGalleryIntentTest {

	private Matcher<Intent> expectedChooserIntent;
	private Matcher<Intent> expectedGetContentIntent;
	private final String lookFileName = "catroid_sunglasses.png";
	private final String projectName = getClass().getSimpleName();
	private final File tmpPath = new File(
			Environment.getExternalStorageDirectory().getAbsolutePath(), "Pocket Code Test Temp");

	@Rule
	public FragmentActivityTestRule<ProjectActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(ProjectActivity.class, ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);

	@Rule
	public GrantPermissionRule runtimePermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

	@Before
	public void setUp() throws Exception {
		createProject(projectName);

		baseActivityTestRule.launchActivity();
		Intents.init();

		expectedGetContentIntent = allOf(
				hasAction("android.intent.action.GET_CONTENT"),
				hasType("image/*"));

		String chooserTitle = UiTestUtils.getResourcesString(R.string.select_look_from_gallery);
		expectedChooserIntent = allOf(
				hasAction("android.intent.action.CHOOSER"),
				hasExtras(bundleHasMatchingString("android.intent.extra.TITLE", chooserTitle)),
				hasExtras(bundleHasExtraIntent(expectedGetContentIntent)));

		if (!tmpPath.exists()) {
			tmpPath.mkdirs();
		}

		File imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.drawable.catroid_banzai,
				tmpPath,
				lookFileName,
				1);

		Intent resultData = new Intent();
		resultData.setData(Uri.fromFile(imageFile));

		Instrumentation.ActivityResult result =
				new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

		intending(expectedChooserIntent).respondWith(result);
	}

	@After
	public void tearDown() throws IOException {
		Intents.release();
		baseActivityTestRule.finishActivity();
		StorageOperations.deleteDir(tmpPath);
		try {
			StorageOperations.deleteDir(new File(DEFAULT_ROOT_DIRECTORY, projectName));
		} catch (IOException e) {
			Log.d(getClass().getSimpleName(), "couldnt clean up project");
		}
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testSpriteFromGalleryIntentTest() {
		onView(withId(R.id.button_add))
				.perform(click());

		onView(withId(R.id.dialog_new_look_gallery))
				.perform(click());

		intended(expectedChooserIntent);

		onView(withText(R.string.new_sprite_dialog_title))
				.check(matches(isDisplayed()));
		onView(withText(R.string.ok))
				.perform(click());

		onRecyclerView().atPosition(1).onChildView(R.id.title_view)
				.check(matches(withText(lookFileName.replace(".png", ""))));
	}

	private void createProject(String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
		XstreamSerializer.getInstance().saveProject(project);
	}
}
