/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.common;

import java.io.IOException;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.StandardProjectHandler;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.test.utils.TestUtils;

public class StandardProjectHandlerTest extends AndroidTestCase {

	private String testProjectName = "testStandardProjectBuilding";

	public StandardProjectHandlerTest() throws IOException {
	}

	@Override
	public void tearDown() {
		TestUtils.clearProject(testProjectName);
	}

	@Override
	public void setUp() {
		TestUtils.clearProject(testProjectName);
	}

	public void testCreateStandardProject() throws IOException {
		Values.SCREEN_WIDTH = 500;
		Values.SCREEN_HEIGHT = 1000;

		Project testProject = StandardProjectHandler.createAndSaveStandardProject(testProjectName, getContext());

		assertEquals("The Project has the wrong name.", testProjectName, testProject.getName());
		assertEquals("wrong number of sprites.", 2, testProject.getSpriteList().size());

		int backgroundSpriteIndex = 0;
		int backgroundCostumeDataIndex = 0;
		int catroidSpriteIndex = 1;
		CostumeData backgroundCostumeData = testProject.getSpriteList().get(backgroundSpriteIndex).getCostumeDataList()
				.get(backgroundCostumeDataIndex);
		assertEquals("wrong size of background image", Values.SCREEN_WIDTH, backgroundCostumeData.getResolution()[0]);
		assertEquals("wrong size of background image", Values.SCREEN_HEIGHT, backgroundCostumeData.getResolution()[1]);
		assertEquals("wrong number of scripts in the Catroid sprite", 2,
				testProject.getSpriteList().get(catroidSpriteIndex).getNumberOfScripts());

		int catroidOnTouchScriptIndex = 1;
		Script whenScript = testProject.getSpriteList().get(catroidSpriteIndex).getScript(catroidOnTouchScriptIndex);
		assertTrue("not a when script", whenScript instanceof WhenScript);
		assertEquals("wrong number of bricks in the touch script", 5, whenScript.getBrickList().size());

		for (int cat_number = 0; cat_number < 3; ++cat_number) {
			CostumeData catCostumeData = testProject.getSpriteList().get(catroidSpriteIndex).getCostumeDataList()
					.get(cat_number);
			assertEquals("wrong size of cat image", Values.SCREEN_WIDTH / 3, catCostumeData.getResolution()[0]);
		}

	}

}
