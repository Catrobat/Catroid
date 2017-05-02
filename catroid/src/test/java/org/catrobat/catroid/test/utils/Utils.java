/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.test.utils;

import org.junit.Ignore;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

// Ignored, so JUnit won't try to SRC_TEST this class.
@Ignore
public final class Utils {

	public static final String SRC = "src/main/java";
	public static final String ANDROID_TEST = "src/androidTest";
	public static final String ANDROID_TEST_SRC = ANDROID_TEST + "/java";
	public static final String SRC_TEST = "src/test/java";
	public static final String BLUETOOTH = "catroidBluetoothTestServer/src";

	public static final String[] TEST_FILE_DIRECTORIES = {ANDROID_TEST_SRC, SRC_TEST};
	public static final String[] TOAST_STACK_TRACE_TEST_DIRECTORIES = {SRC, SRC_TEST, BLUETOOTH};
	public static final String[] VERSION_NAME_AND_CODE_TEST_DIRECTORIES = {ANDROID_TEST};
	public static final String[] SLEEP_TEST_DIRECTORIES = {};

	private Utils() {
		throw new AssertionError();
	}

	public static List<File> getFilesFromDirectoryByExtension(File directory, String extension) {
		String[] extensions = {extension};
		return getFilesFromDirectoryByExtension(directory, extensions);
	}

	public static List<File> getFilesFromDirectoryByExtension(File directory, final String[] extensions) {
		List<File> filesFound = new ArrayList<>();
		File[] contents = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				// ignore automatically created build.xml files
				if (pathname.getName().equals("build.xml")) {
					return false;
				}
				for (String extension : extensions) {
					if (pathname.getName().endsWith(extension)) {
						return true;
					}
				}
				return (pathname.isDirectory() && !pathname.getName().equals("gen") && !pathname.getName().equals(
						"build"));
			}
		});

		for (File file : contents) {
			if (file.isDirectory()) {
				filesFound.addAll(getFilesFromDirectoryByExtension(file, extensions));
			} else {
				filesFound.add(file);
			}
		}

		return filesFound;
	}
}
