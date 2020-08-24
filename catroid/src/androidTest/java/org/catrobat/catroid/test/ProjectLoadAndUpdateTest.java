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
package org.catrobat.catroid.test;

import android.content.Context;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.UiTestCatroidApplication;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.io.ProjectLoadAndUpdate;
import org.catrobat.catroid.io.ZipArchiver;
import org.catrobat.catroid.io.asynctask.ProjectLoadStringProvider;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.ScreenValueHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

@RunWith(AndroidJUnit4.class)
public class ProjectLoadAndUpdateTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static final String PROJECT_NAME = "testProject";

	private static final float CATROBAT_LANGUAGE_VERSION_NOT_SUPPORTED = 0.0f;
	private static final String ZIP_FILENAME_WRONG_NESTING_BRICKS = "CoinCatcher2.catrobat";
	private static final String PROJECT_NAME_NESTING_BRICKS = "Coin Catcher 2";

	private ProjectManager projectManager;
	private ProjectLoadAndUpdate projectLoadAndUpdate;
	private ProjectLoadStringProvider updateStringProvider;

	@Before
	public void setUp() throws Exception {
		Context targetContext = ApplicationProvider.getApplicationContext();
		ScreenValueHandler.updateScreenWidthAndHeight(targetContext);
		projectManager = UiTestCatroidApplication.projectManager;
		projectLoadAndUpdate = new ProjectLoadAndUpdate();
		updateStringProvider = new ProjectLoadStringProvider(targetContext);
	}

	@After
	public void tearDown() throws Exception {
		projectManager.setCurrentProject(null);
		TestUtils.deleteProjects(PROJECT_NAME);
		TestUtils.removeFromPreferences(ApplicationProvider.getApplicationContext(), Constants.PREF_PROJECTNAME_KEY);
	}

	@Test
	public void testCatrobatLanguageVersionNotSupported() throws IOException, ProjectException {
		Project project = TestUtils
				.createProjectWithLanguageVersion(CATROBAT_LANGUAGE_VERSION_NOT_SUPPORTED, PROJECT_NAME);

		try {
			projectLoadAndUpdate.loadProject(project.getDirectory(), updateStringProvider);
			fail("Project shouldn't be compatible");
		} catch (CompatibilityProjectException expected) {
		}

		TestUtils.deleteProjects();
	}

	@Test(expected = ProjectException.class)
	public void testLoadUnexistingProject() throws Exception {
		projectLoadAndUpdate.loadProject(new File(PROJECT_NAME), updateStringProvider);
	}

	@Test
	public void testLoadProjectWithInvalidNestingBrickReferences() throws IOException, ProjectException {
		DEFAULT_ROOT_DIRECTORY.mkdir();

		InputStream inputStream =
				InstrumentationRegistry.getInstrumentation().getContext().getAssets().open(ZIP_FILENAME_WRONG_NESTING_BRICKS);
		File projectDir = new File(DEFAULT_ROOT_DIRECTORY, PROJECT_NAME_NESTING_BRICKS);
		new ZipArchiver().unzip(inputStream, projectDir);

		projectLoadAndUpdate.loadProject(projectDir, updateStringProvider);
		Project project = projectManager.getCurrentProject();

		assertNotNull(project);
		assertEquals(PROJECT_NAME_NESTING_BRICKS, project.getName());

		TestUtils.deleteProjects(PROJECT_NAME_NESTING_BRICKS);
	}
}
