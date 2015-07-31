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
package org.catrobat.catroid.test.code;

import junit.framework.TestCase;

import org.catrobat.catroid.test.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CheckForVerifiesOrAssertionsTest extends TestCase {
	private static final String[] DIRECTORIES = Utils.TEST_FILE_DIRECTORIES;
	private static final String[] IGNORED_FILES = { "MockGalleryActivity.java", "UiTestUtils.java",
			"SimulatedSensorManager.java", "SimulatedSoundRecorder.java", "TestUtils.java",
			"MockPaintroidActivity.java", "TestMainMenuActivity.java", "TestErrorListenerInterface.java",
			"XmlTestUtils.java", "MockSoundActivity.java", "Reflection.java", "Utils.java",
			"BaseActivityInstrumentationTestCase.java", "BaseActivityUnitTestCase.java", "Device.java", "Callback.java", "CallbackBrick.java",
			"Util.java", "BeforeAfterSteps.java", "Cucumber.java", "CallbackAction.java", "ObjectSteps.java",
			"CucumberAnnotation.java", "CatroidExampleSteps.java", "PrintBrick.java", "TestFaceDetector.java", "DroneTestUtils.java", "SystemAnimations.java",
			"ObservedInputStream.java", "ObservedOutputStream.java", "LocalConnectionProxy.java", "BluetoothConnectionProxy.java", "DeviceModel.java",
			"BluetoothLogger.java", "ModelRunner.java", "ConnectionDataLogger.java", "MindstormsNXTTestModel.java",
			"FirmataMessage.java" };

	private boolean fileHasVerifiesOrAssertions(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));

		String line;
		while ((line = reader.readLine()) != null) {
			if (line.matches("[^(//)]*assert[A-Za-z]+\\(.*") || line.matches("[^(//)]*verify[A-Za-z]*\\(.*")) {
				reader.close();
				return true;
			}
		}
		reader.close();
		return false;
	}

	public void testForVerifiesOrAssertions() throws IOException {
		StringBuilder errorMessageBuilder = new StringBuilder(2);
		boolean assertionNotFound = false;

		for (String directoryName : DIRECTORIES) {
			File directory = new File(directoryName);
			assertTrue("Couldn't find directory: " + directoryName, directory.exists() && directory.isDirectory());
			assertTrue("Couldn't read directory: " + directoryName, directory.canRead());

			List<File> filesToCheck = Utils.getFilesFromDirectoryByExtension(directory, new String[] { ".java", });
			for (File file : filesToCheck) {
				if (!Arrays.asList(IGNORED_FILES).contains(file.getName()) && !fileHasVerifiesOrAssertions(file)) {
					errorMessageBuilder.append(file.getPath()).append('\n');
					assertionNotFound = true;
				}
			}
		}
		assertFalse("Files potentially without \"verify\" or \"assert\" statements:\n" + errorMessageBuilder,
				assertionNotFound);
	}
}
