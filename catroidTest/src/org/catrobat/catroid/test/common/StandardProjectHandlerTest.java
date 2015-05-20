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

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.ImageEditing;

import java.io.File;
import java.io.IOException;

public class StandardProjectHandlerTest extends AndroidTestCase {

	private static final String TEST_PROJECT_NAME = "testStandardProjectBuilding";
	private static final int BACKGROUNDIMAGE_WIDTH = 1776;
	private static final int BACKGROUNDIMAGE_HEIGHT = 1943;

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

	public void testCreateStandardProject() throws IOException {
		ScreenValues.SCREEN_WIDTH = BACKGROUNDIMAGE_WIDTH;
		ScreenValues.SCREEN_HEIGHT = BACKGROUNDIMAGE_HEIGHT;

		Project testProject = StandardProjectHandler.createAndSaveStandardProject(TEST_PROJECT_NAME, getContext());

		assertEquals("The Project has the wrong name.", TEST_PROJECT_NAME, testProject.getName());
		assertEquals("wrong number of sprites.", 2, testProject.getSpriteList().size());

		int catroidSpriteIndex = 1;
		assertEquals("wrong number of scripts in the Catroid sprite", 2,
				testProject.getSpriteList().get(catroidSpriteIndex).getNumberOfScripts());

		int catroidOnTouchScriptIndex = 1;
		Script whenScript = testProject.getSpriteList().get(catroidSpriteIndex).getScript(catroidOnTouchScriptIndex);
		assertTrue("not a when script", whenScript instanceof WhenScript);
		assertEquals("wrong number of bricks in the touch script", 1, whenScript.getBrickList().size());

		for (int birdNumber = 0; birdNumber < 2; birdNumber++) {
			LookData catLookData = testProject.getSpriteList().get(1).getLookDataList()
					.get(birdNumber);
			assertEquals("wrong width of bird image " + birdNumber, 328, catLookData.getMeasure()[0]);
			assertEquals("wrong height of bird image " + birdNumber, 328, catLookData.getMeasure()[1]);
		}
	}

	public void testCreateScaledStandardProject() throws IOException {
		ScreenValues.SCREEN_WIDTH = 800;
		ScreenValues.SCREEN_HEIGHT = 1280;
		double scale = ImageEditing.calculateScaleFactorToScreenSize(
				R.drawable.default_project_background, getContext());

		Project testProject = StandardProjectHandler.createAndSaveStandardProject(TEST_PROJECT_NAME, getContext());

		for (int birdNumber = 0; birdNumber < 2; birdNumber++) {
			LookData catLookData = testProject.getSpriteList().get(1).getLookDataList()
					.get(birdNumber);
			assertEquals("wrong size of mole image", (int) (300d * scale), catLookData.getMeasure()[0]);
			assertEquals("wrong size of mole image", (int) (300d * scale), catLookData.getMeasure()[1]);
		}
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
