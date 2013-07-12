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
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.catrobat.catroid.test.utils.Utils;

public class CheckForAssertionsTest extends TestCase {
	private StringBuffer errorMessages;
	private boolean assertionNotFound;
	private static final String[] DIRECTORIES = { "../catroidUiTest", "../catroidTest" };
	private static final String[] IGNORED_FILES = { "MockGalleryActivity.java", "UiTestUtils.java",
			"SimulatedSensorManager.java", "TestUtils.java", "MockPaintroidActivity.java", "TestMainMenuActivity.java",
			"TestErrorListenerInterface.java", "XmlTestUtils.java", "MockSoundActivity.java", "Reflection.java",
			"BaseActivityInstrumentationTestCase.java" };

	private void checkFileForAssertions(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));

		String line = null;

		while ((line = reader.readLine()) != null) {
			if (line.matches("[^(//)]*assert[A-Za-z]+\\(.*")) {
				reader.close();
				return;
			}
		}
		errorMessages.append(file.getName() + " does not seem to contain assertions\n");
		assertionNotFound = true;
		reader.close();
	}

	public void testForAssertions() throws IOException {
		errorMessages = new StringBuffer();
		assertionNotFound = false;

		for (String directoryName : DIRECTORIES) {
			File directory = new File(directoryName);
			assertTrue("Couldn't find directory: " + directoryName, directory.exists() && directory.isDirectory());
			assertTrue("Couldn't read directory: " + directoryName, directory.canRead());

			List<File> filesToCheck = Utils.getFilesFromDirectoryByExtension(directory, new String[] { ".java", });
			for (File file : filesToCheck) {
				if (!Arrays.asList(IGNORED_FILES).contains(file.getName())) {
					checkFileForAssertions(file);
				}
			}
		}

		assertFalse("Files potentially without assertion statements:\n" + errorMessages.toString(), assertionNotFound);
	}
}
