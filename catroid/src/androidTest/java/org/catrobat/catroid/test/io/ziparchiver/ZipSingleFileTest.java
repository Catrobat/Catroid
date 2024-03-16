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

import org.catrobat.catroid.io.ZipArchiver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ZipSingleFileTest {

	private File tmpFile;
	private File outputArchive;
	private File unzippedDir;

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	@Before
	public void setUp() throws IOException {
		outputArchive = new File(tmpFolder.getRoot(), "folderToZip.zip");
		unzippedDir = new File(tmpFolder.getRoot(), "unzippedFolder");
		tmpFile = File.createTempFile("test", ".png", tmpFolder.getRoot());
	}

	@Test
	public void testZipAndUnzipFile() throws IOException {
		ZipArchiver archiver = new ZipArchiver();

		archiver.zip(outputArchive, new File[] {tmpFile});

		assertTrue(outputArchive.exists());

		archiver.unzip(outputArchive, unzippedDir);

		assertTrue(unzippedDir.exists());
	}
}
