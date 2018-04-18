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

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.io.ZipArchiver;
import org.catrobat.catroid.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ZipArchiverTest {

	private File tmpFile;
	private File archive;
	private File unzippedDir;

	@Before
	public void setUp() throws IOException {
		archive = new File(Utils.buildPath(Constants.DEFAULT_ROOT, "folderToZip.zip"));
		unzippedDir = new File(Utils.buildPath(Constants.DEFAULT_ROOT, "unzippedFolder"));
		tmpFile = File.createTempFile("test", ".png", new File(Constants.DEFAULT_ROOT));
	}

	@After
	public void tearDown() throws IOException {
		tmpFile.delete();
		archive.delete();
		StorageHandler.deleteDir(unzippedDir.getAbsolutePath());
	}

	@Test
	public void testZipAndUnzipFile() throws IOException {
		ZipArchiver archiver = new ZipArchiver();

		archiver.zip(archive.getAbsolutePath(), new File[] {tmpFile});

		assertTrue(archive.exists());

		archiver.unzip(archive.getAbsolutePath(), unzippedDir.getAbsolutePath());

		assertTrue(unzippedDir.exists());
	}
}
