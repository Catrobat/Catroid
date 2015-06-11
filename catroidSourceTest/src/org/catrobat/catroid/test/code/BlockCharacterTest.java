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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class BlockCharacterTest extends TestCase {

	private static final String[] DIRECTORIES = Utils.ALL_DIRECTORIES;
	private String errorMessages;
	private boolean errorFound;

	private void checkFileForBlockCharacters(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		StringBuilder errorMessageBuilder = new StringBuilder();

		int lineCount = 1;
		String line;

		while ((line = reader.readLine()) != null) {
			if (line.contains("\uFFFD") || line.contains("\uFFFC") || line.contains("\uFFFF")) {
				errorFound = true;
				errorMessageBuilder.append(file.getPath()).append(" in line ").append(lineCount).append('\n');
			}
			++lineCount;
		}
		reader.close();
		if (errorMessageBuilder.length() > 0) {
			errorMessages += errorMessageBuilder.toString();
		}
	}

	public void testForBlockCharacters() throws IOException {
		errorMessages = "";
		errorFound = false;

		for (String directoryName : DIRECTORIES) {
			File directory = new File(directoryName);
			assertTrue("Couldn't find directory: " + directoryName, directory.exists() && directory.isDirectory());
			assertTrue("Couldn't read directory: " + directoryName, directory.canRead());

			List<File> filesToCheck = Utils.getFilesFromDirectoryByExtension(directory, new String[] { ".java", ".xml",
					".md", ".gradle" });

			for (File file : filesToCheck) {
				checkFileForBlockCharacters(file);
			}
		}

		assertFalse("Files with Block Characters found: \n" + errorMessages, errorFound);
	}
}
