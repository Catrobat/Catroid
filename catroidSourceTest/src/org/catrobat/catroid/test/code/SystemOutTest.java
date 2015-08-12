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
import java.util.List;
import java.util.ListIterator;

public class SystemOutTest extends TestCase {

	private String errorMessages;
	private boolean errorFound;

	private static final String[] TOAST_STACK_TRACE_DIRECTORIES = Utils.TOAST_STACK_TRACE_TEST_DIRECTORIES;
	private static final String[] IGNORED_FILES = { "SystemOutTest.java", "ToastUtil.java", "BTServer.java" };
	private static final String TOAST_STRING = "Toast.makeText";
	private static final String SUPERTOAST_STRING = "SuperToast";

	private void checkFileForString(File file, String string) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder errorMessageBuilder = new StringBuilder();
		int lineCount = 1;
		String line;

		while ((line = reader.readLine()) != null) {
			if (line.contains(string)) {
				errorFound = true;
				errorMessageBuilder
						.append(file.getPath())
						.append(" in line ")
						.append(lineCount)
						.append('\n');
			}
			++lineCount;
		}
		reader.close();
		if (errorMessageBuilder.length() > 0) {
			errorMessages += errorMessageBuilder.toString();
		}
	}

	private void checkForStringInFiles(String string, String[] directories) throws IOException {
		for (String directoryName : directories) {
			File directory = new File(directoryName);
			assertTrue("Couldn't find directory: " + directoryName, directory.exists() && directory.isDirectory());
			assertTrue("Couldn't read directory: " + directoryName, directory.canRead());

			List<File> filesToCheck = Utils.getFilesFromDirectoryByExtension(directory, new String[] { ".java", });
			if (IGNORED_FILES != null) {
				for (String ignoredFileString : IGNORED_FILES) {
					for (ListIterator<File> listIterator = filesToCheck.listIterator(); listIterator.hasNext(); ) {
						File file = listIterator.next();
						if (file.getName().endsWith(ignoredFileString)) {
							listIterator.remove();
						}
					}
				}
			}

			for (File file : filesToCheck) {
				checkFileForString(file, string);
			}
		}
	}

	public void setUp() {
		errorMessages = "";
		errorFound = false;
	}

	public void testForToast() throws IOException {
		checkForStringInFiles(TOAST_STRING, TOAST_STACK_TRACE_DIRECTORIES);
		assertFalse("Files with 'Toast.makeText(context, text, duration)' found! \nPlease use 'ToastUtil.showError(context, message), or "
				+ "ToastUtil.showSuccess(context, message)' instead\n\n" + errorMessages, errorFound);
	}

	public void testForSuperToast() throws IOException {
		checkForStringInFiles(SUPERTOAST_STRING, TOAST_STACK_TRACE_DIRECTORIES);
		assertFalse("Files with 'SuperToast' found! \nPlease use 'ToastUtil.showError(context, message), or "
				+ "ToastUtil.showSuccess(context, message)' instead\n\n" + errorMessages, errorFound);
	}
}
