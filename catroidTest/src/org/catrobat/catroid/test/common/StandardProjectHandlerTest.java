/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.common;

import java.io.IOException;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.test.utils.TestUtils;

import android.test.AndroidTestCase;

public class StandardProjectHandlerTest extends AndroidTestCase {

	private String testProjectName = "testStandardProjectBuilding";

	public StandardProjectHandlerTest() throws IOException {
	}

	@Override
	public void tearDown() throws Exception {
		TestUtils.clearProject(testProjectName);
		super.tearDown();
	}

	@Override
	public void setUp() {
		TestUtils.clearProject(testProjectName);
	}

	public void testCreateStandardProject() throws IOException {
		ScreenValues.SCREEN_WIDTH = 720;
		ScreenValues.SCREEN_HEIGHT = 1134;

		Project testProject = StandardProjectHandler.createAndSaveStandardProject(testProjectName, getContext());

		assertEquals("The Project has the wrong name.", testProjectName, testProject.getName());
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

}
