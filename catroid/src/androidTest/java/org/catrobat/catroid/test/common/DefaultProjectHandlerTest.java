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
package org.catrobat.catroid.test.common;

import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageOperations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.Constants.SCREENSHOT_AUTOMATIC_FILE_NAME;
import static org.catrobat.catroid.common.Constants.SCREENSHOT_MANUAL_FILE_NAME;

@RunWith(AndroidJUnit4.class)
public class DefaultProjectHandlerTest {

	private Project project;

	@Before
	public void setUp() throws IOException {
		DefaultProjectHandler.getInstance()
				.setDefaultProjectCreator(DefaultProjectHandler.ProjectCreatorType.PROJECT_CREATOR_DEFAULT);

		String projectName = "defaultProject";

		project = DefaultProjectHandler
				.createAndSaveDefaultProject(projectName, ApplicationProvider.getApplicationContext(), false);
	}

	@After
	public void tearDown() throws Exception {
		StorageOperations.deleteDir(project.getDirectory());
	}

	@Test
	public void testCreateScaledDefaultProject() {
		Scene currentScene = project.getDefaultScene();
		List<Sprite> spriteList = currentScene.getSpriteList();

		assertEquals(4, spriteList.size());

		assertEquals(1, spriteList.get(1).getNumberOfScripts());
		assertEquals(1, spriteList.get(2).getNumberOfScripts());
		assertEquals(3, spriteList.get(3).getNumberOfScripts());

		assertEquals(1, spriteList.get(0).getLookList().size());
		assertEquals(1, spriteList.get(1).getLookList().size());
		assertEquals(1, spriteList.get(2).getLookList().size());
		assertEquals(2, spriteList.get(3).getLookList().size());
	}

	@Test
	public void testDefaultProjectScreenshot() {
		Scene currentScene = project.getDefaultScene();

		File file = new File(currentScene.getDirectory(), SCREENSHOT_MANUAL_FILE_NAME);
		assertFalse(file.exists());

		file = new File(currentScene.getDirectory(), SCREENSHOT_AUTOMATIC_FILE_NAME);
		assertTrue(file.exists());
	}
}
