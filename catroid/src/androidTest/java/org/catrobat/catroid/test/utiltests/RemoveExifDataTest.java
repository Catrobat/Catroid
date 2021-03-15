/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.test.utiltests;

import org.catrobat.catroid.utils.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.exifinterface.media.ExifInterface;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.Constants.EXIFTAGS_FOR_EXIFREMOVER;

@RunWith(AndroidJUnit4.class)
public class RemoveExifDataTest {

	private static final String IMAGE_NAME = "Exif.jpg";
	private static final String CACHE_FOLDER =
			ApplicationProvider.getApplicationContext().getCacheDir().getAbsolutePath();

	@Test
	public void testRemoveExifData() throws IOException {
		InputStream originalImage =
				InstrumentationRegistry.getInstrumentation().getContext().getAssets().open(IMAGE_NAME);

		File cacheFolderFile = new File(CACHE_FOLDER);
		File cacheFile = new File(cacheFolderFile, IMAGE_NAME);
		byte[] buf = new byte[originalImage.available()];
		originalImage.read(buf);
		OutputStream outputStream = new FileOutputStream(cacheFile);
		outputStream.write(buf);

		ExifInterface exif = new ExifInterface(cacheFile.getAbsolutePath());

		assertFalse(exif.getAttribute(ExifInterface.TAG_ARTIST).isEmpty());
		assertFalse(exif.getAttribute(ExifInterface.TAG_DATETIME).isEmpty());

		Utils.removeExifData(cacheFolderFile, IMAGE_NAME);

		exif = new ExifInterface(cacheFile.getAbsolutePath());
		for (String exifTag: EXIFTAGS_FOR_EXIFREMOVER) {
			String tag = exif.getAttribute(exifTag);
			assertTrue(tag == null || tag.isEmpty());
		}

		cacheFile.delete();
	}
}
