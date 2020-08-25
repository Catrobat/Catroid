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
package org.catrobat.catroid.catrobattestrunner;

import android.Manifest.permission;
import android.app.Instrumentation;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.asynctask.ProjectLoadTask;
import org.catrobat.catroid.io.asynctask.ProjectUnzipAndImportTask;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.TestUtils;
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

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertTrue;

import static org.catrobat.catroid.common.Constants.CACHE_DIR;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.stage.TestResult.STAGE_ACTIVITY_TEST_SUCCESS;
import static org.catrobat.catroid.stage.TestResult.TEST_RESULT_MESSAGE;

@RunWith(Parameterized.class)
public class CatrobatTestRunner {

	@Rule
	public ActivityTestRule<StageActivity> baseActivityTestRule = new
			ActivityTestRule<>(StageActivity.class, true, false);
	@Rule
	public GrantPermissionRule runtimePermissionRule =
			GrantPermissionRule.grant(
					permission.READ_EXTERNAL_STORAGE,
					permission.WRITE_EXTERNAL_STORAGE
			);

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

		String[] assets = InstrumentationRegistry.getInstrumentation().getContext().getAssets().list(path);
		if (null == assets) {
			fail("Could not load assets");
			return parameters;
		}
		for (String asset : assets) {
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

		InputStream inputStream =
				InstrumentationRegistry.getInstrumentation().getContext().getAssets()
				.open(assetPath + "/" + assetName);

		File projectArchive = StorageOperations
				.copyStreamToDir(inputStream, CACHE_DIR, assetName);

		assertTrue(ProjectUnzipAndImportTask
				.task(projectArchive));

		File projectDir = new File(DEFAULT_ROOT_DIRECTORY, projectName);

		assertTrue(ProjectLoadTask
				.task(projectDir, ApplicationProvider.getApplicationContext()));
	}

	@After
	public void tearDown() throws IOException {
		StorageOperations.deleteDir(CACHE_DIR);
		TestUtils.deleteProjects(assetName.replace(Constants.CATROBAT_EXTENSION, ""));
	}

	@Test
	public void run() throws InterruptedException {
		baseActivityTestRule.launchActivity(null);
		waitForReady();
		Instrumentation.ActivityResult result = baseActivityTestRule.getActivityResult();
		if (result.getResultCode() != STAGE_ACTIVITY_TEST_SUCCESS) {
			fail(result.getResultData().getStringExtra(TEST_RESULT_MESSAGE));
		}
	}

	private void waitForReady() throws InterruptedException {
		int intervalMillis = 10;
		for (int waitedFor = 0; waitedFor < TIMEOUT; waitedFor += intervalMillis) {
			if (baseActivityTestRule.getActivity().isFinishing()) {
				return;
			}
			Thread.sleep(intervalMillis);
		}

		fail("Timeout after " + TIMEOUT + "ms\n"
				+ "Test never got into ready state - is the AssertEqualsBrick reached?\n");
	}
}
