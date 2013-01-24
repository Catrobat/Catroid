/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.utiltests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Smoke;

public class UtilsTest extends AndroidTestCase {
	private final String testFileContent = "Hello, this is a Test-String";
	private final String MD5_EMPTY = "D41D8CD98F00B204E9800998ECF8427E";
	private final String MD5_CATROID = "4F982D927F4784F69AD6D6AF38FD96AD";
	private final String MD5_HELLO_WORLD = "ED076287532E86365E841E92BFC50D8C";
	private File mTestFile;
	private File copiedFile;

	@Override
	protected void setUp() throws Exception {
		OutputStream outputStream = null;
		try {
			mTestFile = File.createTempFile("testCopyFiles", ".txt");
			if (mTestFile.canWrite()) {
				outputStream = new FileOutputStream(mTestFile);
				outputStream.write(testFileContent.getBytes());
				outputStream.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		if (mTestFile != null && mTestFile.exists()) {
			mTestFile.delete();
		}
		if (copiedFile != null && copiedFile.exists()) {
			copiedFile.delete();
		}
		super.tearDown();
	}

	public void testMD5CheckSumOfFile() {

		PrintWriter printWriter = null;

		File tempDir = new File(Constants.TMP_PATH);
		tempDir.mkdirs();

		File md5TestFile = new File(Utils.buildPath(Constants.TMP_PATH, "catroid.txt"));

		if (md5TestFile.exists()) {
			md5TestFile.delete();
		}

		try {
			md5TestFile.createNewFile();
			assertEquals("MD5 sums are not the same for empty file", MD5_EMPTY, Utils.md5Checksum(md5TestFile));

			printWriter = new PrintWriter(md5TestFile);
			printWriter.print("catroid");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (printWriter != null) {
				printWriter.close();
			}
		}

		assertEquals("MD5 sums are not the same for catroid file", MD5_CATROID, Utils.md5Checksum(md5TestFile));

		UtilFile.deleteDirectory(tempDir);
	}

	public void testMD5CheckSumOfString() {
		assertEquals("MD5 sums do not match!", MD5_CATROID, Utils.md5Checksum("catroid"));
		assertEquals("MD5 sums do not match!", MD5_EMPTY, Utils.md5Checksum(""));
		assertEquals("MD5 sums do not match!", MD5_HELLO_WORLD, Utils.md5Checksum("Hello World!"));
	}

	public void testBuildPath() {
		String first = "/abc/abc";
		String second = "/def/def/";
		String result = "/abc/abc/def/def";
		assertEquals(Utils.buildPath(first, second), result);

		first = "/abc/abc";
		second = "def/def/";
		result = "/abc/abc/def/def";
		assertEquals(Utils.buildPath(first, second), result);

		first = "/abc/abc/";
		second = "/def/def/";
		result = "/abc/abc/def/def";
		assertEquals(Utils.buildPath(first, second), result);

		first = "/abc/abc/";
		second = "def/def/";
		result = "/abc/abc/def/def";
		assertEquals(Utils.buildPath(first, second), result);
	}

	public void testUniqueName() {
		String first = Utils.getUniqueName();
		String second = Utils.getUniqueName();
		String third = Utils.getUniqueName();
		assertFalse("Same unique name!", first.equals(second));
		assertFalse("Same unique name!", first.equals(third));
		assertFalse("Same unique name!", second.equals(third));
	}

	public void testDeleteSpecialCharactersFromString() {
		String testString = "This:IsA-\" */ :<Very>?|Very\\\\Long_Test_String";
		String newString = Utils.deleteSpecialCharactersInString(testString);
		assertEquals("Strings are not equal!", "ThisIsA-  VeryVeryLong_Test_String", newString);
	}

	public void testBuildProjectPath() {
		if (!Utils.externalStorageAvailable()) {
			fail("No SD card present");
		}
		String projectName = "test?Projekt\"1";
		String expectedPath = Constants.DEFAULT_ROOT + "/testProjekt1";
		assertEquals("Paths are different!", expectedPath, Utils.buildProjectPath(projectName));
	}

	@Smoke
	public void testDebuggableFlagShouldBeSet() throws Exception {
		// Ensure Utils  returns true in isApplicationDebuggable
		Reflection.setPrivateField(Utils.class, "isUnderTest", false);
		assertTrue("Debug flag not set!", Utils.isApplicationDebuggable(getContext()));
	}
}
