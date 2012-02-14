/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.web;

import java.io.File;
import java.io.IOException;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.UtilZip;

public class ZipTest extends AndroidTestCase {

	public ZipTest() {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testZipUnzip() throws IOException {

		String pathToTest = Consts.TMP_PATH + "/test1/";

		File testfile = new File(pathToTest + "test2/testfile.txt");
		testfile.getParentFile().mkdirs();
		testfile.createNewFile();

		String[] paths = { pathToTest };

		String zipFileName = Consts.TMP_PATH + "/testzip" + Consts.CATROID_EXTENTION;
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

		if (!UtilZip.unZipFile(zipFileName, Consts.TMP_PATH + "/")) {
			zipFile.delete();
			assertFalse("unzip failed", true);
			return;
		}

		File checkfile = new File(pathToTest + "/test2/testfile.txt");

		assertTrue("File was not recreated from zip.", checkfile.exists());

		zipFile.delete();

		File tempDirectory = new File(Consts.TMP_PATH);
		if (tempDirectory.exists()) {
			UtilFile.deleteDirectory(tempDirectory);
		}
	}
}
