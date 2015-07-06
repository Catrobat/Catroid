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
package org.catrobat.catroid.test.license;

import junit.framework.TestCase;

import org.catrobat.catroid.test.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LicenseTest extends TestCase {

	private static final String[] DIRECTORIES = Utils.SOURCE_AND_RESOURCE_DIRECTORIES;

	private List<String> agplLicenseText;
	private boolean allLicenseTextsPresentAndCorrect;
	private String errorMessages;

	@Override
	public void setUp() throws IOException {
		allLicenseTextsPresentAndCorrect = true;
		errorMessages = "";
		agplLicenseText = readLicenseFile(new File("res/agpl_license_text.txt"));
	}

	private List<String> readLicenseFile(File licenseTextFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(licenseTextFile));
		String line;
		List<String> licenseText = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			if (line.length() > 0) {
				licenseText.add(line);
			}
		}
		reader.close();
		return licenseText;
	}

	private void checkFileForLicense(File file, List<String> licenseText) throws IOException {
		StringBuilder fileContentsBuilder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));

		String line;
		while ((line = reader.readLine()) != null) {
			fileContentsBuilder.append(line);
		}

		final String fileContents = fileContentsBuilder.toString();

		int lastPosition = 0;
		boolean notFound = false;
		boolean wrongOrder = false;
		for (String licenseTextLine : licenseText) {
			int position = fileContents.indexOf(licenseTextLine);
			if (position == -1) {
				notFound = true;
			} else if (position <= lastPosition) {
				wrongOrder = true;
			}

			lastPosition = position;
		}

		StringBuilder errorMessageBuilder = new StringBuilder();
		if (notFound) {
			allLicenseTextsPresentAndCorrect = false;
			errorMessageBuilder.append("License text was not found in file ").append(file.getCanonicalPath())
					.append('\n');
		} else if (wrongOrder) {
			allLicenseTextsPresentAndCorrect = false;
			errorMessageBuilder.append("License text was found in the wrong order in file ")
					.append(file.getCanonicalPath()).append('\n');
		}
		reader.close();
		errorMessages += errorMessageBuilder.toString();
	}

	public void testLicensePresentInAllFiles() throws IOException {
		for (String directoryName : DIRECTORIES) {
			File directory = new File(directoryName);
			assertTrue("Couldn't find directory: " + directoryName, directory.exists() && directory.isDirectory());
			assertTrue("Couldn't read directory: " + directoryName, directory.canRead());

			List<File> filesToCheck = Utils.getFilesFromDirectoryByExtension(directory, new String[] { ".java", ".xml",
					".rb" });
			for (File file : filesToCheck) {
				checkFileForLicense(file, agplLicenseText);
			}
		}
		assertTrue("Correct license text was not found in all files:\n" + errorMessages,
				allLicenseTextsPresentAndCorrect);
	}
}
