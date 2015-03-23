/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.test.web;

import android.test.AndroidTestCase;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.UtilZip;

import java.io.File;
import java.io.IOException;

public class ZipTest extends AndroidTestCase {

	public ZipTest() {
		super();
	}

	public void testZipUnzip() throws IOException {

		String pathToTest = Constants.TMP_PATH + "/test1/";

		File testfile = new File(pathToTest + "test2/testfile.txt");
		testfile.getParentFile().mkdirs();
		testfile.createNewFile();

		String[] paths = { pathToTest };

		String zipFileName = Constants.TMP_PATH + "/testzip" + Constants.CATROBAT_EXTENSION;
		File zipFile = new File(zipFileName);
		if (zipFile.exists()) {
			zipFile.delete();
		}

		zipFile.getParentFile().mkdirs();
		zipFile.createNewFile();

		if (!UtilZip.writeToZipFile(paths, zipFileName)) {
			zipFile.delete();
			assertFalse("zip failed", true);
			return;
		}
		testfile.delete();
		testfile.getParentFile().delete();

		if (!UtilZip.unZipFile(zipFileName, Constants.TMP_PATH + "/")) {
			zipFile.delete();
			assertFalse("unzip failed", true);
			return;
		}

		File checkfile = new File(pathToTest + "/test2/testfile.txt");

		assertTrue("File was not recreated from zip.", checkfile.exists());

		zipFile.delete();

		File tempDirectory = new File(Constants.TMP_PATH);
		if (tempDirectory.exists()) {
			UtilFile.deleteDirectory(tempDirectory);
		}
	}
}
