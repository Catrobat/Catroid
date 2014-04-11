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

import junit.framework.TestCase;

import org.catrobat.catroid.test.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SleepTest extends TestCase {
	private static final String[] DIRECTORIES = { "../catroidCucumberTest" };
	private static final String REGEX_PATTERN = "^.*Thread\\.sleep\\(\\w+\\).*$";

	private String errorMessages;
	private boolean errorFound;
	private Pattern regexPattern;

	private void checkFileForThreadSleep(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder errorMessageBuilder = new StringBuilder(28);
		Matcher regexMatcher = regexPattern.matcher("");
		int lineCount = 1;
		String line;

		while ((line = reader.readLine()) != null) {
			if (regexMatcher.reset(line).matches()) {
				errorFound = true;
				errorMessageBuilder
						.append("File ")
						.append(file.getName())
						.append(':')
						.append(lineCount)
						.append(" contains \"Thread.sleep()\"");
			}
			++lineCount;
		}
		reader.close();
		if (errorMessageBuilder.length() > 0) {
			errorMessages += errorMessageBuilder.toString();
		}
	}

	public void testThreadSleepNotPresentInAnyUiTests() throws IOException {
		assertTrue("Pattern didn't match!", "Thread.sleep(1337)".matches(REGEX_PATTERN));
		assertTrue("Pattern didn't match!", "Thread.sleep(virtualVariable)".matches(REGEX_PATTERN));
		assertTrue("Pattern didn't match!", "Thread.sleep(VIRTUAL_08_VARIABLE)".matches(REGEX_PATTERN));
		assertTrue("Pattern didn't match!", "Thread.sleep(virtual_VAR14BLE_)".matches(REGEX_PATTERN));
		assertTrue("Pattern didn't match!", "Thread.sleep(_)".matches(REGEX_PATTERN));
		assertTrue("Pattern didn't match!", "foo(); Thread.sleep(42); bar();".matches(REGEX_PATTERN));
		assertFalse("Pattern matched! But shouldn't!", "Thread.sleep()".matches(REGEX_PATTERN));
		assertFalse("Pattern matched! But shouldn't!", "Thread.sleep(.)".matches(REGEX_PATTERN));
		assertFalse("Pattern matched! But shouldn't!", "Thread.sleep(\"foobar\")".matches(REGEX_PATTERN));
		assertFalse("Pattern matched! But shouldn't!", "Thread.sleep(\"42\")".matches(REGEX_PATTERN));
		assertFalse("Pattern matched! But shouldn't!", "Thread0sleep(MyVar)".matches(REGEX_PATTERN));

		errorMessages = "";
		errorFound = false;
		regexPattern = Pattern.compile(REGEX_PATTERN);

		for (String directoryName : DIRECTORIES) {
			File directory = new File(directoryName);
			assertTrue("Couldn't find directory: " + directoryName, directory.exists() && directory.isDirectory());
			assertTrue("Couldn't read directory: " + directoryName, directory.canRead());

			List<File> filesToCheck = Utils.getFilesFromDirectoryByExtension(directory, ".java");
			for (File file : filesToCheck) {
				checkFileForThreadSleep(file);
			}
		}

		assertFalse("Files with Block Characters found: \n" + errorMessages, errorFound);
	}
}
