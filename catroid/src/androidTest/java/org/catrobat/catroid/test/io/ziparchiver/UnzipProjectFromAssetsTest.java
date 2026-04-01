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

package org.catrobat.catroid.test.io.ziparchiver;

import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.ZipArchiver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

@RunWith(AndroidJUnit4.class)
public class UnzipProjectFromAssetsTest {

	private File projectDir;

	@Before
	public void setUp() {
		projectDir = new File(DEFAULT_ROOT_DIRECTORY, UnzipProjectFromAssetsTest.class.getSimpleName());
	}

	@After
	public void tearDown() throws IOException {
		if (projectDir.isDirectory()) {
			StorageOperations.deleteDir(projectDir);
		}
	}

	@Test
	public void testUnzipProjectFromAssets() throws IOException {
		String assetName = "Air_fight_0.5.catrobat";
		InputStream inputStream =
				InstrumentationRegistry.getInstrumentation().getContext().getAssets().open(assetName);

		new ZipArchiver().unzip(inputStream, projectDir);

		assertTrue(projectDir.exists());

		inputStream = InstrumentationRegistry.getInstrumentation().getContext().getAssets().open(assetName);
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		ZipEntry entry;

		while ((entry = zipInputStream.getNextEntry()) != null) {
			assertTrue(new File(projectDir, entry.getName()).exists());
		}

		zipInputStream.close();
		inputStream.close();
	}
}
