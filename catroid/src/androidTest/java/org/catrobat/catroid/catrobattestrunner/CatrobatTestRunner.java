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
package org.catrobat.catroid.catrobattestrunner;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.google.common.math.DoubleMath;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.actions.AssertEqualsAction;
import org.catrobat.catroid.content.bricks.AssertEqualsBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.asynctask.ProjectLoadTask;
import org.catrobat.catroid.io.asynctask.ProjectUnzipAndImportTask;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.TestUtils;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.contrib.ActivityResultMatchers.hasResultCode;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertTrue;

import static org.catrobat.catroid.common.Constants.CACHE_DIR;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

@RunWith(Parameterized.class)
public class CatrobatTestRunner {

	@Rule
	public ActivityTestRule<StageActivity> baseActivityTestRule = new
			ActivityTestRule<>(StageActivity.class, true, false);

	private static final String TEST_ASSETS_ROOT = "catrobatTests";

	private static final int TIMEOUT = 10000;

	@Parameterized.Parameters(name = "{0} - {1}")
	public static Iterable<Object[]> data() throws IOException {
		return getCatrobatAssetsFromPath(TEST_ASSETS_ROOT);
	}

	@Parameterized.Parameter
	public String assetPath;

	@Parameterized.Parameter(1)
	public String assetName;

	private static List<Object[]> getCatrobatAssetsFromPath(String path) throws IOException {
		List<Object[]> parameters = new ArrayList<>();

		for (String asset : InstrumentationRegistry.getContext().getAssets().list(path)) {
			if (asset.endsWith(Constants.CATROBAT_EXTENSION)) {
				parameters.add(new Object[] {path, asset});
			} else {
				parameters.addAll(getCatrobatAssetsFromPath(path + "/" + asset));
			}
		}
		return parameters;
	}

	@Before
	public void setUp() throws Exception {
		String projectName = assetName.replace(Constants.CATROBAT_EXTENSION, "");

		TestUtils.deleteProjects(projectName);
		DEFAULT_ROOT_DIRECTORY.mkdir();
		CACHE_DIR.mkdir();

		InputStream inputStream = InstrumentationRegistry.getContext().getAssets()
				.open(assetPath + "/" + assetName);

		File projectArchive = StorageOperations
				.copyStreamToDir(inputStream, CACHE_DIR, assetName);

		assertTrue(ProjectUnzipAndImportTask
				.task(projectArchive));

		File projectDir = new File(DEFAULT_ROOT_DIRECTORY, projectName);

		assertTrue(ProjectLoadTask
				.task(projectDir, InstrumentationRegistry.getTargetContext()));

		Project project = ProjectManager.getInstance().getCurrentProject();
	}

	@After
	public void tearDown() throws IOException {
		StorageOperations.deleteDir(CACHE_DIR);
		TestUtils.deleteProjects(assetName.replace(Constants.CATROBAT_EXTENSION, ""));
	}

	@Test
	public void run() {
		baseActivityTestRule.launchActivity(null);
		waitForReady();
		Instrumentation.ActivityResult result = baseActivityTestRule.getActivityResult();
		if (result.getResultCode() != StageActivity.STAGE_ACTIVITY_TEST_SUCCESS) {
			fail(result.getResultData().getStringExtra(AssertEqualsAction.MESSAGE));
		}
	}

	private void waitForReady() {
		int intervalMillis = 10;
		for (int waitedFor = 0; waitedFor < TIMEOUT; waitedFor += intervalMillis) {
			if (baseActivityTestRule.getActivity().isFinishing()) {
				return;
			}
			espressoWait(intervalMillis);
		}
		fail("Timeout after " + TIMEOUT + "ms\n"
				+ "Test never got into ready state - is the AssertEqualsBrick reached?\n");
	}

	private void espressoWait(final int milliSeconds) {
		onView(isRoot()).perform(new ViewAction() {
			@Override
			public String getDescription() {
				return "Wait for X milliseconds";
			}

			@Override
			public Matcher<View> getConstraints() {
				return isDisplayed();
			}

			@Override
			public void perform(UiController uiController, View view) {
				uiController.loopMainThreadUntilIdle();
				uiController.loopMainThreadForAtLeast(milliSeconds);
			}
		});
	}
}
