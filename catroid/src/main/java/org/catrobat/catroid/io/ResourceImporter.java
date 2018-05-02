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

package org.catrobat.catroid.io;

import android.content.res.Resources;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.soundrecorder.SoundRecorder;
import org.catrobat.catroid.utils.ImageEditing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public final class ResourceImporter {

	private ResourceImporter() {
		throw new AssertionError();
	}

	public static File createImageFileFromResourcesInDirectory(Resources resources, File dstDir, String fileName,
			int resourceId, double scaleFactor) throws IOException {

		if (scaleFactor <= 0) {
			throw new IllegalArgumentException("scaleFactor was: " + scaleFactor + ", it has to be > 0.");
		}

		if (!fileName.toLowerCase(Locale.US).endsWith(Constants.DEFAULT_IMAGE_EXTENSION)) {
			fileName += Constants.DEFAULT_IMAGE_EXTENSION;
		}

		InputStream inputStream = resources.openRawResource(resourceId);
		File file = StorageHandler.createFileFromStreamInDir(inputStream, dstDir, fileName);

		if (scaleFactor != 1) {
			ImageEditing.scaleImageFile(file, scaleFactor);
		}

		return file;
	}

	public static File createSoundFileFromResourcesInDirectory(Resources resources, File dstDir, String fileName,
			int resourceId) throws IOException {

		if (!fileName.toLowerCase(Locale.US).endsWith(SoundRecorder.RECORDING_EXTENSION)) {
			throw new IllegalArgumentException("Extension " + SoundRecorder.RECORDING_EXTENSION + " is required.");
		}

		InputStream inputStream = resources.openRawResource(resourceId);
		return StorageHandler.createFileFromStreamInDir(inputStream, dstDir, fileName);
	}
}
