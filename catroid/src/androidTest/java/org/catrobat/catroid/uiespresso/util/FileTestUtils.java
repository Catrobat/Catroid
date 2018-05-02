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

import org.catrobat.catroid.io.ResourceImporter;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.utils.PathBuilder.buildScenePath;

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

	public static File copyResourceFileToProject(String projectName, String sceneName, String fileName, int resourceId,
			Context context, FileTypes type) throws IOException {

		switch (type) {
			case IMAGE:
				File imageDir = new File(buildScenePath(projectName, sceneName), IMAGE_DIRECTORY_NAME);
				return ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), resourceId,
						imageDir, fileName, 1);
			case SOUND:
				File soundDir = new File(buildScenePath(projectName, sceneName), SOUND_DIRECTORY_NAME);
				return ResourceImporter.createSoundFileFromResourcesInDirectory(context.getResources(), resourceId,
						soundDir, fileName);
			case SCREENSHOT:
				File sceneDir = new File(buildScenePath(projectName, sceneName));
				return ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(), resourceId,
						sceneDir, fileName, 1);
			default:
				throw new IllegalArgumentException("No File Type was specified.");
		}
	}

	public enum FileTypes {
		IMAGE, SOUND, SCREENSHOT
	}
}
