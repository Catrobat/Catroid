/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.util;

import android.content.Context;
import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public final class FileTestUtils {
	private static final String TAG = FileTestUtils.class.getSimpleName();

	// Suppress default constructor for noninstantiability
	private FileTestUtils() {
		throw new AssertionError();
	}

	public static void assertFileExists(String... path) {
		String fullPath = Utils.buildPath(path);
		assertTrue("File does not exists: " + fullPath, new File(fullPath).exists());
	}

	public static void assertFileDoesNotExist(String... path) {
		String fullPath = Utils.buildPath(path);
		assertFalse("File exists: " + fullPath, new File(fullPath).exists());
	}

	/**
	 * saves a file into the project folder
	 * if project == null or "" file will be saved into Catroid folder
	 *
	 * @param project Folder where the file will be saved, this folder should exist
	 * @param name    Name of the file
	 * @param fileID  the id of the file --> needs the right context
	 * @param context
	 * @param type    type of the file: 0 = imagefile, 1 = soundfile
	 * @return the file
	 * @throws IOException
	 */
	public static File saveFileToProject(String project, String sceneName, String name, int fileID, Context context, FileTypes type) {
		String filePath;
		String defaultRoot = Constants.DEFAULT_ROOT;
		if (project == null || project.equalsIgnoreCase("")) {
			filePath = defaultRoot + "/";
		} else {
			switch (type) {
				case IMAGE:
					filePath = defaultRoot + "/" + project + "/" + sceneName + "/" + Constants.IMAGE_DIRECTORY + "/";
					break;
				case SOUND:
					filePath = defaultRoot + "/" + project + "/" + sceneName + "/" + Constants.SOUND_DIRECTORY + "/";
					break;
				case SCREENSHOT:
					filePath = defaultRoot + "/" + project + "/" + sceneName + "/";
					break;
				case ROOT:
					filePath = defaultRoot + "/" + project + "/";
					break;
				default:
					filePath = defaultRoot + "/";
					break;
			}
		}
		BufferedInputStream in = new BufferedInputStream(context.getResources().openRawResource(fileID),
				Constants.BUFFER_8K);

		try {
			File file = new File(filePath + name);
			file.getParentFile().mkdirs();
			file.createNewFile();

			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file), Constants.BUFFER_8K);
			byte[] buffer = new byte[Constants.BUFFER_8K];
			int length = 0;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.flush();
			out.close();

			return file;
		} catch (IOException e) {
			Log.e(TAG, "File handling error", e);
			return null;
		}
	}

	public enum FileTypes {
		IMAGE, SOUND, ROOT, SCREENSHOT
	}
}
