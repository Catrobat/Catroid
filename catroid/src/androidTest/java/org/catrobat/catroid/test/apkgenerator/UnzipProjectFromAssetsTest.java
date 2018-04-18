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

package org.catrobat.catroid.test.apkgenerator;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.io.ZipArchiver;
import org.catrobat.catroid.utils.Utils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UnzipProjectFromAssetsTest {

	private static final String TEST_PROJECT_PATH = Utils.buildProjectPath("testUnzipProjectFromAPK");

	@After
	public void tearDown() throws IOException {
		if (new File(TEST_PROJECT_PATH).exists()) {
			StorageHandler.deleteDir(TEST_PROJECT_PATH);
		}
	}

	@Test
	public void testUnzipProjectFromAssets() throws IOException {
		InputStream inputStream = InstrumentationRegistry.getContext().getAssets().open("generated965.zip");
		new ZipArchiver().unzip(inputStream, TEST_PROJECT_PATH);

		assertTrue(new File(TEST_PROJECT_PATH).exists());

		InputStream is = InstrumentationRegistry.getContext().getAssets().open("generated965.zip");
		ZipInputStream zis = new ZipInputStream(is);
		ZipEntry entry;

		while ((entry = zis.getNextEntry()) != null) {
			assertTrue(new File(TEST_PROJECT_PATH, entry.getName()).exists());
		}

		zis.close();
		is.close();
	}
}
