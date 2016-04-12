/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.test.common;

import android.test.AndroidTestCase;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.common.defaultprojectcreators.DefaultProjectCreator;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DefaultProjectHandlerTest extends AndroidTestCase {

	private static final String TEST_PROJECT_NAME = "testStandardProject";

	public DefaultProjectHandlerTest() throws IOException {
	}

	@Override
	public void tearDown() throws Exception {
		TestUtils.clearProject(TEST_PROJECT_NAME);
		super.tearDown();
	}

	@Override
	public void setUp() {
		TestUtils.clearProject(TEST_PROJECT_NAME);
		DefaultProjectHandler.getInstance().setDefaultProjectCreator(DefaultProjectHandler
				.ProjectCreatorType.PROJECT_CREATOR_DEFAULT);
	}

	public void testCreateScaledDefaultProject() throws IOException {
		ProjectManager projectManager = ProjectManager.getInstance();
		projectManager.setProject(DefaultProjectHandler.createAndSaveDefaultProject(TEST_PROJECT_NAME, getContext()));

		Project currentProject = projectManager.getCurrentProject();
		List<Sprite> spriteList = currentProject.getSpriteList();

		assertEquals("Number of Sprites in defaultProject is incorrect.", 4, spriteList.size());

		assertEquals("Number of Scripts in cloudSprite1 is incorrect.", 1, spriteList.get(1).getNumberOfScripts());
		assertEquals("Number of Scripts in cloudSprite2 is incorrect.", 1, spriteList.get(2).getNumberOfScripts());
		assertEquals("Number of Scripts in birdSprite is incorrect.", 3, spriteList.get(3).getNumberOfScripts());

		assertEquals("Number of Looks in background is incorrect.", 1, spriteList.get(0).getLookDataList().size());
		assertEquals("Number of Looks in cloudSprite1 is incorrect.", 1, spriteList.get(1).getLookDataList().size());
		assertEquals("Number of Looks in cloudSprite2 is incorrect.", 1, spriteList.get(2).getLookDataList().size());
		assertEquals("Number of Looks in birdSprite is incorrect.", 2, spriteList.get(3).getLookDataList().size());
	}

	public void testDefaultProjectScreenshot() throws IOException {
		DefaultProjectHandler.createAndSaveDefaultProject(TEST_PROJECT_NAME, getContext());
		String projectPath = Constants.DEFAULT_ROOT + "/" + TEST_PROJECT_NAME;

		File file = new File(projectPath + "/" + StageListener.SCREENSHOT_MANUAL_FILE_NAME);
		assertFalse("Manual screenshot shouldn't exist in default project", file.exists());

		file = new File(projectPath + "/" + StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME);
		assertTrue("Automatic screenshot should exist in default project", file.exists());
	}

	public void testCreateAllDefaultProjects() throws IOException {
		TestUtils.clearProject(getContext().getString(R.string.default_project_name));
		TestUtils.clearProject(getContext().getString(R.string.default_drone_project_name));
		TestUtils.clearProject(getContext().getString(R.string.default_project_name_physics));

		Project defaultDefaultProject = DefaultProjectHandler.createAndSaveDefaultProject(getContext());
		DefaultProjectHandler defaultProjectHandler = DefaultProjectHandler.getInstance();
		assertEquals("default project name not as expected", getContext().getString(R.string.default_project_name),
				defaultDefaultProject.getName());
		assertTrue("default project does not exist.", StorageHandler.getInstance().projectExists(defaultDefaultProject
				.getName()));

		defaultProjectHandler.setDefaultProjectCreator(DefaultProjectHandler.ProjectCreatorType
				.PROJECT_CREATOR_DRONE);
		if (BuildConfig.FEATURE_PARROT_AR_DRONE_ENABLED) {
			Project droneDefaultProject = DefaultProjectHandler.createAndSaveDefaultProject(getContext());
			assertEquals("default drone project name not as expected", getContext().getString(
					R.string.default_drone_project_name), droneDefaultProject.getName());
			assertTrue("default drone project does not exist.", StorageHandler.getInstance().projectExists(
					droneDefaultProject.getName()));
		} else {
			assertTrue("standard default project creator must be initialized", Reflection.getPrivateField(
					defaultProjectHandler, "DefaultProjectCreator") instanceof DefaultProjectCreator);
		}

		defaultProjectHandler.setDefaultProjectCreator(DefaultProjectHandler.ProjectCreatorType
				.PROJECT_CREATOR_PHYSICS);
		Project physicsDefaultProject = DefaultProjectHandler.createAndSaveDefaultProject(getContext());
		assertEquals("default physics project name not as expected", getContext().getString(
				R.string.default_project_name_physics), physicsDefaultProject.getName());
		assertTrue("default physics project does not exist.", StorageHandler.getInstance().projectExists(
				physicsDefaultProject.getName()));

		TestUtils.clearProject(getContext().getString(R.string.default_drone_project_name));
		TestUtils.clearProject(getContext().getString(R.string.default_project_name_physics));
	}
}
