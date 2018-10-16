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
package org.catrobat.catroid.test;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ControlStructureBrick;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.ZipArchiver;
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
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import static org.catrobat.catroid.common.Constants.CURRENT_CATROBAT_LANGUAGE_VERSION;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

@RunWith(AndroidJUnit4.class)
public class ProjectManagerTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static final String OLD_PROJECT = "OLD_PROJECT";
	private static final String NEW_PROJECT = "NEW_PROJECT";
	private static final String DOES_NOT_EXIST = "DOES_NOT_EXIST";

	private static final float CATROBAT_LANGUAGE_VERSION_NOT_SUPPORTED = 0.0f;
	private static final String ZIP_FILENAME_WRONG_NESTING_BRICKS = "CoinCatcher2.catrobat";
	private static final String PROJECT_NAME_NESTING_BRICKS = "Coin Catcher 2";

	private ProjectManager projectManager;

	@Before
	public void setUp() throws Exception {
		ScreenValueHandler.updateScreenWidthAndHeight(InstrumentationRegistry.getTargetContext());
		projectManager = ProjectManager.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		projectManager.setProject(null);
		TestUtils.deleteProjects(OLD_PROJECT, NEW_PROJECT);
		TestUtils.removeFromPreferences(InstrumentationRegistry.getTargetContext(), Constants.PREF_PROJECTNAME_KEY);
	}

	@Test
	public void testShouldReturnFalseIfCatrobatLanguageVersionNotSupported() throws IOException, ProjectException {
		TestUtils.createProjectWithLanguageVersion(CATROBAT_LANGUAGE_VERSION_NOT_SUPPORTED,
				"testProject");

		try {
			projectManager.loadProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, InstrumentationRegistry.getTargetContext());
			fail("Project shouldn't be compatible");
		} catch (CompatibilityProjectException expected) {
		}

		TestUtils.deleteProjects();

		TestUtils.createProjectWithLanguageVersion(CURRENT_CATROBAT_LANGUAGE_VERSION,
				"testProject");

		projectManager.loadProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, InstrumentationRegistry.getTargetContext());
	}

	@Test
	public void testShouldKeepExistingProjectIfCannotLoadNewProject() throws IOException,
			CompatibilityProjectException, OutdatedVersionProjectException, LoadingProjectException {

		TestUtils.createProjectWithLanguageVersion(CURRENT_CATROBAT_LANGUAGE_VERSION,
				OLD_PROJECT);

		projectManager.loadProject(OLD_PROJECT, InstrumentationRegistry.getTargetContext());

		TestUtils.createProjectWithLanguageVersion(CATROBAT_LANGUAGE_VERSION_NOT_SUPPORTED,
				"testProject");

		try {
			projectManager.loadProject(NEW_PROJECT, InstrumentationRegistry.getTargetContext());
			fail("Expected ProjectException while loading  project " + NEW_PROJECT);
		} catch (ProjectException expected) {
		}

		Project currentProject = projectManager.getCurrentProject();

		assertNotNull(currentProject);
		assertEquals(OLD_PROJECT, currentProject.getName());

		TestUtils.deleteProjects(OLD_PROJECT, NEW_PROJECT);
	}

	@Test
	public void testLoadProjectException() throws Exception {
		assertNull(projectManager.getCurrentProject());

		exception.expect(ProjectException.class);
		projectManager.loadProject(DOES_NOT_EXIST, InstrumentationRegistry.getTargetContext());
	}

	@Test
	public void testSavingAProjectDuringDelete() throws IOException, CompatibilityProjectException,
			OutdatedVersionProjectException, LoadingProjectException {
		TestUtils.createProjectWithLanguageVersion(
				CURRENT_CATROBAT_LANGUAGE_VERSION, TestUtils.DEFAULT_TEST_PROJECT_NAME);

		projectManager.loadProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, InstrumentationRegistry.getTargetContext());

		Project currentProject = projectManager.getCurrentProject();
		assertNotNull(String.format("Could not load %s project.", TestUtils.DEFAULT_TEST_PROJECT_NAME), currentProject);

		File directory = new File(DEFAULT_ROOT_DIRECTORY, TestUtils.DEFAULT_TEST_PROJECT_NAME);
		assertTrue(String.format("Directory %s does not exist", directory.getPath()), directory.exists());

		// simulate multiple saving trigger asynchronous (occurs in black box testing)
		for (int i = 0; i < 3; i++) {
			currentProject.setDescription(currentProject.getDescription() + i);
			projectManager.saveProject(InstrumentationRegistry.getTargetContext());
		}

		// simulate deletion, saveProject asyncTask will be "automatically" cancelled (Please remark: there is still a chance
		// of a race condition, because we rely on a "project" reference which gets used in a multithreaded environment)
		projectManager.setProject(null);
		StorageOperations.deleteDir(directory);

		assertFalse(directory.exists());
	}

	@Test
	public void testLoadProjectWithInvalidNestingBrickReferences() throws CompatibilityProjectException,
			IOException,
			OutdatedVersionProjectException,
			LoadingProjectException {

		DEFAULT_ROOT_DIRECTORY.mkdir();

		InputStream inputStream = InstrumentationRegistry.getContext().getAssets().open(ZIP_FILENAME_WRONG_NESTING_BRICKS);

		new ZipArchiver().unzip(inputStream, new File(DEFAULT_ROOT_DIRECTORY, PROJECT_NAME_NESTING_BRICKS));

		projectManager.loadProject(PROJECT_NAME_NESTING_BRICKS, InstrumentationRegistry.getTargetContext());
		Project project = projectManager.getCurrentProject();

		assertNotNull(project);
		assertEquals(PROJECT_NAME_NESTING_BRICKS, project.getName());

		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Script script : sprite.getScriptList()) {
					assertFalse(containsControlBricksWithInvalidReferences(script.getBrickList()));
				}
			}
		}

		TestUtils.deleteProjects(PROJECT_NAME_NESTING_BRICKS);
	}

	private boolean containsControlBricksWithInvalidReferences(List<Brick> bricks) {
		for (Brick brick : bricks) {
			if (brick instanceof ControlStructureBrick && hasInvalidReference((ControlStructureBrick) brick, bricks)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasInvalidReference(ControlStructureBrick brick, List<Brick> bricks) {
		List<Brick> brickParts = brick.getAllParts();
		if (brickParts.contains(null)) {
			return true;
		}
		for (Brick brickPart : brickParts) {
			if (!(bricks.contains(brickPart))) {
				return true;
			}
		}
		return false;
	}
}
