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

package org.catrobat.catroid.uiespresso.util;

import java.io.File;

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
}
