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

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.PathBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public final class FileTestUtils {

	private FileTestUtils() {
		throw new AssertionError();
	}

	public static void assertFileExists(File file) {
		assertTrue("File does not exist: " + file.getAbsolutePath(), file.exists());
	}

	public static void assertFileDoesNotExist(File file) {
		assertFalse("File exists: " + file.getAbsolutePath(), file.exists());
	}

	public static void assertFileExistsInDirectory(File file, File dir) {
		assertTrue("File does not exist: " + dir.getAbsolutePath() + "/" + file.getName(),
				new File(dir.getAbsolutePath(), file.getName()).exists());
	}

	public static void assertFileDoesNotExistInDirectory(File file, File dir) {
		assertFalse("File exists: " + dir.getAbsolutePath() + "/" + file.getName(),
				new File(dir.getAbsolutePath(), file.getName()).exists());
	}

	public static File copyResourceFileToProject(String project, String sceneName, String name, int resourceId,
			Context context, FileTypes type) throws IOException {

		File file;
		switch (type) {
			case IMAGE:
				file = new File(PathBuilder.buildPath(Constants.DEFAULT_ROOT_DIRECTORY.getAbsolutePath(), project, sceneName,
						Constants.IMAGE_DIRECTORY_NAME), name);
				break;
			case SOUND:
				file = new File(PathBuilder.buildPath(Constants.DEFAULT_ROOT_DIRECTORY.getAbsolutePath(), project, sceneName,
						Constants.SOUND_DIRECTORY_NAME), name);
				break;
			case SCREENSHOT:
				file = new File(PathBuilder.buildPath(Constants.DEFAULT_ROOT_DIRECTORY.getAbsolutePath(), project, sceneName),
						name);
				break;
			default:
				file = new File(Constants.DEFAULT_ROOT_DIRECTORY, name);
				break;
		}

		BufferedInputStream in = new BufferedInputStream(context.getResources().openRawResource(resourceId),
				Constants.BUFFER_8K);

		file.getParentFile().mkdirs();
		file.createNewFile();

		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file), Constants.BUFFER_8K);
		byte[] buffer = new byte[Constants.BUFFER_8K];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.close();

		return file;
	}

	public enum FileTypes {
		IMAGE, SOUND, SCREENSHOT
	}
}
