/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.utils.TestUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class StandardProjectHandlerTest extends AndroidTestCase {

	private static final String TEST_PROJECT_NAME = "testStandardProject";

	public StandardProjectHandlerTest() throws IOException {
	}

	@Override
	public void tearDown() throws Exception {
		TestUtils.clearProject(TEST_PROJECT_NAME);
		super.tearDown();
	}

	@Override
	public void setUp() {
		TestUtils.clearProject(TEST_PROJECT_NAME);
	}

	public void testCreateScaledStandardProject() throws IOException {
		ProjectManager projectManager = ProjectManager.getInstance();
		projectManager.setProject(StandardProjectHandler.createAndSaveStandardProject(TEST_PROJECT_NAME, getContext()));

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
		StandardProjectHandler.createAndSaveStandardProject(TEST_PROJECT_NAME, getContext());
		String projectPath = Constants.DEFAULT_ROOT + "/" + TEST_PROJECT_NAME;

		File file = new File(projectPath + "/" + StageListener.SCREENSHOT_MANUAL_FILE_NAME);
		assertFalse("Manual screenshot shouldn't exist in default project", file.exists());

		file = new File(projectPath + "/" + StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME);
		assertTrue("Automatic screenshot should exist in default project", file.exists());
	}
}
