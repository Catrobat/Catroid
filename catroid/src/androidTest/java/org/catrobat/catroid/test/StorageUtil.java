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

package org.catrobat.catroid.test;

import android.os.Environment;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.test.utils.Reflection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class StorageUtil {

	public static final String TAG = StorageUtil.class.getSimpleName();

	private StorageUtil() {
	}

	public static void createTestDirectory() throws Exception {
		Reflection.setPrivateField(Constants.class, "DEFAULT_ROOT", Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Pocket Code Test");
		File testFolder = new File(Constants.DEFAULT_ROOT);
		if (testFolder.exists()) {
			deleteRecursive(testFolder);
		}
	}

	private static void deleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory()) {
			for (File child : fileOrDirectory.listFiles()) {
				deleteRecursive(child);
			}
		}
		fileOrDirectory.delete();
	}

	public static boolean directoryContains(File directory, List<String> filenames) throws IOException {
		if (!directory.exists()) {
			throw new FileNotFoundException("Directory:" + directory.getName() + " does not exits.");
		}

		if (!directory.isDirectory()) {
			throw new IOException("File:" + directory.getName() + " is not a directory.");
		}

		if (directory.listFiles().length < filenames.size()) {
			return false;
		}

		List<String> filenamesFromDir = new ArrayList<>();

		for (int i = 0; i < directory.listFiles().length; i++) {
			filenamesFromDir.add(directory.listFiles()[i].getName());
		}

		for (String filename : filenames) {
			if (!filenamesFromDir.contains(filename)) {
				return false;
			}
		}

		return true;
	}
}
