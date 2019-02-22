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
package org.catrobat.catroid.catrobatTestRunner;

import android.support.test.InstrumentationRegistry;

import com.google.common.math.DoubleMath;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.asynctask.ProjectUnzipAndImportTask;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
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
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.common.Constants.CACHE_DIR;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

@RunWith(Parameterized.class)
public class CatrobatTestRunner {

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	private static String TEST_ASSETS_ROOT = "catrobatTests";

	private static String RESULT_VARIABLE_NAME = "RESULT";
	private static String EXPECTED_VARIABLE_NAME = "EXPECTED";
	private static String TIMEOUT_VARIABLE_NAME = "TIMEOUT";
	private static String SETUP_FINISHED_VARIABLE_NAME = "SETUP_FINISHED";

	private static final double EPSILON = 0.001;

	private Project project;
	private String projectName;
	private UserVariable testResult;
	private UserVariable expectedResult;
	private UserVariable timeout;
	private UserVariable setupFinished;

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
		projectName = assetName.replace(Constants.CATROBAT_EXTENSION, "");

		TestUtils.deleteProjects(projectName);
		DEFAULT_ROOT_DIRECTORY.mkdir();
		CACHE_DIR.mkdir();

		InputStream inputStream = InstrumentationRegistry.getContext().getAssets()
				.open(assetPath + "/" + assetName);
		File projectCatrobatFile = StorageOperations.copyStreamToDir(inputStream, CACHE_DIR, assetName);
		ProjectUnzipAndImportTask.task(projectCatrobatFile);

		ProjectManager.getInstance().loadProject(projectName, InstrumentationRegistry.getTargetContext());
		project = ProjectManager.getInstance().getCurrentProject();
		DataContainer dataContainer = project.getDefaultScene().getDataContainer();

		testResult = dataContainer.getProjectUserVariable(RESULT_VARIABLE_NAME);
		expectedResult = dataContainer.getProjectUserVariable(EXPECTED_VARIABLE_NAME);
		timeout = dataContainer.getProjectUserVariable(TIMEOUT_VARIABLE_NAME);
		setupFinished = dataContainer.getProjectUserVariable(SETUP_FINISHED_VARIABLE_NAME);
	}

	@After
	public void tearDown() throws IOException {
		StorageOperations.deleteDir(CACHE_DIR);
		TestUtils.deleteProjects(assetName.replace(Constants.CATROBAT_EXTENSION, ""));
	}

	@Test
	public void run() {
		baseActivityTestRule.launchActivity(null);
		assertUserVariableEqualsWithTimeout(setupFinished,
				1d,
				1000);
		assertUserVariableEqualsWithTimeout(testResult,
				(double) expectedResult.getValue(),
				((Double)timeout.getValue()).intValue());
	}

	public static void assertUserVariableEqualsWithTimeout(UserVariable userVariable, double expectedValue,
			int timeoutMillis) {
		for (int intervalMillis = 10; timeoutMillis > 0; timeoutMillis -= intervalMillis) {
			if (DoubleMath.fuzzyEquals(expectedValue, (Double) userVariable.getValue(), EPSILON)) {
				assertEquals(expectedValue, (Double) userVariable.getValue(), EPSILON);
				return;
			}
			onView(isRoot())
					.perform(CustomActions.wait(intervalMillis));
		}
		assertEquals(expectedValue, (Double) userVariable.getValue(), EPSILON);
	}
}
