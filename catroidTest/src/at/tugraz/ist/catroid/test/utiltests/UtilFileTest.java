/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.utiltests;

import java.io.File;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.utils.UtilFile;

public class UtilFileTest extends InstrumentationTestCase {
	private File testDirectory;
	private File subDirectory;
	private File file1;
	private File file2;

	@Override
	protected void setUp() throws Exception {
		final String catroidDirectory = "/sdcard/catroid";

		testDirectory = new File(catroidDirectory + "/testDirectory");
		testDirectory.mkdir();
		file1 = new File(testDirectory.getAbsolutePath() + "/file1");
		file1.createNewFile();
		subDirectory = new File(testDirectory.getAbsolutePath() + "/subDirectory");
		subDirectory.mkdir();
		file2 = new File(subDirectory.getAbsolutePath() + "/file2");
		file2.createNewFile();

		super.setUp();
	}

	private void deleteFile(File file) {
		if (file.exists())
			file.delete();
	}

	@Override
	protected void tearDown() throws Exception {
		deleteFile(file2);
		deleteFile(subDirectory);
		deleteFile(file1);
		deleteFile(testDirectory);
	}

	public void testClearDirectory() {
		UtilFile.clearDirectory(testDirectory);
		assertFalse("File in subdirectory still exists after call to clearDirectory", file2.exists());
		assertFalse("Subdirectory in test directory still exists after call to clearDirectory", subDirectory.exists());
		assertFalse("File in test directory still exists after call to clearDirectory", file1.exists());
		assertTrue("Directory itself was deleted as well after call to clearDirectory", testDirectory.exists());
	}

	public void testDeleteDirectory() {
		UtilFile.deleteDirectory(testDirectory);
		assertFalse("File in subdirectory still exists after call to deleteDirectory", file2.exists());
		assertFalse("Subdirectory in test directory still exists after call to deleteDirectory", subDirectory.exists());
		assertFalse("File in test directory still exists after call to deleteDirectory", file1.exists());
		assertFalse("Test directory still exists after call to deleteDirectory", testDirectory.exists());
	}
}
