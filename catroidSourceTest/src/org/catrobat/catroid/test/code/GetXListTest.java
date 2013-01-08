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
package org.catrobat.catroid.test.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.catrobat.catroid.test.utils.Utils;

import junit.framework.TestCase;

public class GetXListTest extends TestCase {
	private static final String[] DIRECTORIES = { "../catroid", "../catroidTest", "../catroidUiTest" };
	private static final String REGEX_PATTERN = "^.*get(Sprite|Script|Brick)List\\(\\)\\.add\\(.*$";

	private StringBuffer errorMessages;
	private boolean errorFound;

	private void checkFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));

		int lineCount = 1;
		String line = null;

		while ((line = reader.readLine()) != null) {
			if (line.matches(REGEX_PATTERN)) {
				errorFound = true;
				errorMessages
						.append("File " + file.getName() + ":" + lineCount + " contains 'getScriptList().add()'\n");
			}
			++lineCount;
		}
		reader.close();
	}

	public void testGetXListAddNotPresent() throws IOException {
		assertTrue("Pattern didn't match!", "getBrickList().add(new HideBrick(sprite))".matches(REGEX_PATTERN));
		assertTrue("Pattern didn't match!", "getScriptList().add(virtualVariable)".matches(REGEX_PATTERN));
		assertTrue("Pattern didn't match!", "getSpriteList().add(VIRTUAL_08_VARIABLE)".matches(REGEX_PATTERN));
		assertTrue("Pattern didn't match!", "getBrickList().add(virtual_VAR14BLE_)".matches(REGEX_PATTERN));
		assertTrue("Pattern didn't match!", "getScriptList().add(_)".matches(REGEX_PATTERN));
		assertTrue("Pattern didn't match!", "foo(); getScriptList().add(42); bar();".matches(REGEX_PATTERN));
		assertFalse("Pattern matched! But shouldn't!", "getScriptList()add(MyVar)".matches(REGEX_PATTERN));
		assertFalse("Pattern matched! But shouldn't!", "getBrickList.add(MyVar)".matches(REGEX_PATTERN));

		errorMessages = new StringBuffer();
		errorFound = false;

		for (String directoryName : DIRECTORIES) {
			File directory = new File(directoryName);
			assertTrue("Couldn't find directory: " + directoryName, directory.exists() && directory.isDirectory());
			assertTrue("Couldn't read directory: " + directoryName, directory.canRead());

			List<File> filesToCheck = Utils.getFilesFromDirectoryByExtension(directory, ".java");
			for (File file : filesToCheck) {
				checkFile(file);
			}
		}

		assertFalse("Files with Block Characters found: \n" + errorMessages.toString(), errorFound);
	}
}
