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

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.utils.TestUtils;

import java.io.File;
import java.io.IOException;

public class StandardProjectHandlerTest extends AndroidTestCase {

	private static final String TEST_PROJECT_NAME = "testStandardProjectBuilding";
	private static final int BACKGROUNDIMAGE_WIDTH = 720;
	private static final int BACKGROUNDIMAGE_HEIGHT = 1134;

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
		assertEquals("wrong number of sprites.", 5, testProject.getSpriteList().size());

		int backgroundSpriteIndex = 0;
		int backgroundLookDataIndex = 0;
		int catroidSpriteIndex = 1;
		LookData backgroundLookData = testProject.getSpriteList().get(backgroundSpriteIndex).getLookDataList()
				.get(backgroundLookDataIndex);
		assertEquals("wrong size of background image", ScreenValues.SCREEN_WIDTH, backgroundLookData.getMeasure()[0]);
		assertEquals("wrong size of background image", ScreenValues.SCREEN_HEIGHT, backgroundLookData.getMeasure()[1]);
		assertEquals("wrong number of scripts in the Catroid sprite", 2,
				testProject.getSpriteList().get(catroidSpriteIndex).getNumberOfScripts());

		int catroidOnTouchScriptIndex = 1;
		Script whenScript = testProject.getSpriteList().get(catroidSpriteIndex).getScript(catroidOnTouchScriptIndex);
		assertTrue("not a when script", whenScript instanceof WhenScript);
		assertEquals("wrong number of bricks in the touch script", 4, whenScript.getBrickList().size());

		for (catroidSpriteIndex = 1; catroidSpriteIndex <= 4; catroidSpriteIndex++) {
			for (int moleNumber = 0; moleNumber < 3; ++moleNumber) {
				LookData catLookData = testProject.getSpriteList().get(catroidSpriteIndex).getLookDataList()
						.get(moleNumber);
				assertEquals("wrong size of mole image", 720, catLookData.getMeasure()[0]);
				assertEquals("wrong size of mole image", 542, catLookData.getMeasure()[1]);
			}
		}
	}

	public void testCreateScaledStandardProject() throws IOException {
		ScreenValues.SCREEN_WIDTH = 800;
		ScreenValues.SCREEN_HEIGHT = 1280;
		//double scale = ((double) ScreenValues.SCREEN_WIDTH) / (double) BACKGROUNDIMAGE_WIDTH;
		double scale = (ScreenValues.SCREEN_HEIGHT) / (double) BACKGROUNDIMAGE_HEIGHT;

		Project testProject = StandardProjectHandler.createAndSaveStandardProject(TEST_PROJECT_NAME, getContext());

		int backgroundSpriteIndex = 0;
		int backgroundLookDataIndex = 0;
		int catroidSpriteIndex = 1;
		LookData backgroundLookData = testProject.getSpriteList().get(backgroundSpriteIndex).getLookDataList()
				.get(backgroundLookDataIndex);
		assertEquals("wrong height of background image", ScreenValues.SCREEN_HEIGHT, backgroundLookData.getMeasure()[1]);
		//note: the expected value is not ScreenValues.SCREEN_HEIGHT
		assertEquals("wrong width of background image", (int) (BACKGROUNDIMAGE_WIDTH * scale),
				backgroundLookData.getMeasure()[0]);

		for (catroidSpriteIndex = 1; catroidSpriteIndex <= 4; catroidSpriteIndex++) {
			for (int moleNumber = 0; moleNumber < 3; ++moleNumber) {
				LookData catLookData = testProject.getSpriteList().get(catroidSpriteIndex).getLookDataList()
						.get(moleNumber);
				assertEquals("wrong size of mole image", (int) (720d * scale), catLookData.getMeasure()[0]);
				assertEquals("wrong size of mole image", (int) (542d * scale), catLookData.getMeasure()[1]);
			}
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
