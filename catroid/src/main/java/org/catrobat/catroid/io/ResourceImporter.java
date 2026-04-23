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

package org.catrobat.catroid.io;

import android.content.res.Resources;

import org.catrobat.catroid.utils.ImageEditing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public final class ResourceImporter {

	private ResourceImporter() {
		throw new AssertionError();
	}

	public static File createImageFileFromResourcesInDirectory(Resources resources, int resourceId, File dstDir,
			String fileName, double scaleFactor) throws IOException {

		if (scaleFactor <= 0) {
			throw new IllegalArgumentException("scaleFactor was: " + scaleFactor + ", it has to be > 0.");
		}

		InputStream inputStream = resources.openRawResource(resourceId);
		File file = StorageOperations.copyStreamToDir(inputStream, dstDir, fileName);

		if (scaleFactor != 1) {
			ImageEditing.scaleImageFile(file, scaleFactor);
		}

		return file;
	}

	public static File createSoundFileFromResourcesInDirectory(Resources resources, int resourceId, File dstDir,
			String fileName) throws IOException {

		InputStream inputStream = resources.openRawResource(resourceId);
		return StorageOperations.copyStreamToDir(inputStream, dstDir, fileName);
	}
}
