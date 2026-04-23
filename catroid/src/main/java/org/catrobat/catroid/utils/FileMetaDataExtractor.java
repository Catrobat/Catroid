/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.utils;

import android.content.Context;
import android.text.format.Formatter;
import android.util.Log;

import org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;

public final class FileMetaDataExtractor {

	private static final String TAG = FileMetaDataExtractor.class.getSimpleName();

	private FileMetaDataExtractor() {
		throw new AssertionError();
	}

	public static long getSizeOfFileOrDirectoryInByte(File fileOrDirectory) {
		if (!fileOrDirectory.exists()) {
			return 0;
		}
		if (fileOrDirectory.isFile()) {
			return fileOrDirectory.length();
		}

		File[] contents = fileOrDirectory.listFiles();
		if (contents == null) {
			return 0;
		}

		long size = 0;
		for (File file : contents) {
			size += file.isDirectory() ? getSizeOfFileOrDirectoryInByte(file) : file.length();
		}
		return size;
	}

	public static String getSizeAsString(File fileOrDirectory, Context context) {
		long bytes = FileMetaDataExtractor.getSizeOfFileOrDirectoryInByte(fileOrDirectory);
		return Formatter.formatShortFileSize(context, bytes);
	}

	public static List<String> getProjectNames(File directory) {
		if (directory.listFiles() == null) {
			return Collections.emptyList();
		}

		List<String> projectNames = new ArrayList<>();

		for (File file : directory.listFiles()) {
			File xmlFile = new File(file, CODE_XML_FILE_NAME);
			if (!xmlFile.exists()) {
				continue;
			}

			ProjectMetaDataParser metaDataParser = new ProjectMetaDataParser(xmlFile);

			try {
				projectNames.add(metaDataParser.getProjectMetaData().getName());
			} catch (IOException e) {
				Log.e(TAG, "Well, that's awkward.", e);
			}
		}
		return projectNames;
	}

	public static String encodeSpecialCharsForFileSystem(String projectName) {
		if (projectName.equals(".") || projectName.equals("..")) {
			projectName = projectName.replace(".", "%2E");
		} else {
			projectName = projectName.replace("%", "%25");
			projectName = projectName.replace("\"", "%22");
			projectName = projectName.replace("/", "%2F");
			projectName = projectName.replace(":", "%3A");
			projectName = projectName.replace("<", "%3C");
			projectName = projectName.replace(">", "%3E");
			projectName = projectName.replace("?", "%3F");
			projectName = projectName.replace("\\", "%5C");
			projectName = projectName.replace("|", "%7C");
			projectName = projectName.replace("*", "%2A");
		}
		return projectName;
	}

	public static String decodeSpecialCharsForFileSystem(String projectName) {
		projectName = projectName.replace("%2E", ".");

		projectName = projectName.replace("%2A", "*");
		projectName = projectName.replace("%7C", "|");
		projectName = projectName.replace("%5C", "\\");
		projectName = projectName.replace("%3F", "?");
		projectName = projectName.replace("%3E", ">");
		projectName = projectName.replace("%3C", "<");
		projectName = projectName.replace("%3A", ":");
		projectName = projectName.replace("%2F", "/");
		projectName = projectName.replace("%22", "\"");
		projectName = projectName.replace("%25", "%");
		return projectName;
	}
}
