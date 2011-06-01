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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import junit.framework.TestCase;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

public class UtilsTest extends TestCase {

	private final String testFileContent = "Hello, this is a Test-String";
	private final String MD5_EMPTY = "D41D8CD98F00B204E9800998ECF8427E";
	private final String MD5_CATROID = "4F982D927F4784F69AD6D6AF38FD96AD";
	private final String MD5_HELLO_WORLD = "ED076287532E86365E841E92BFC50D8C";
	private File mTestFile;
	private File copiedFile;

	@Override
	protected void setUp() throws Exception {
		try {
			mTestFile = File.createTempFile("testCopyFiles", ".txt");
			if (mTestFile.canWrite()) {
				OutputStream stream = new FileOutputStream(mTestFile);
				stream.write(testFileContent.getBytes());
				stream.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
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
	}

	public void testCopyFile() throws InterruptedException {
		String newpath = mTestFile.getParent() + "/copiedFile.txt";
		Utils.copyFile(mTestFile.getAbsolutePath(), newpath, null, false);
		Thread.sleep(1000); // Wait for thread to write file
		copiedFile = new File(newpath);

		assertTrue(copiedFile.exists());

		FileReader fReader;
		String newContent = "";

		try {
			fReader = new FileReader(copiedFile);

			int read;
			while ((read = fReader.read()) != -1) {
				newContent = newContent + (char) read;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertEquals(testFileContent, newContent);
	}

	public void testDeleteFile() {
		Utils.deleteFile(mTestFile.getAbsolutePath());
		assertFalse(mTestFile.exists());
	}

	public void testConcatPath() {
		String first = "/abc/abc";
		String second = "/def/def/";
		String result = "/abc/abc/def/def/";
		assertEquals(Utils.concatPaths(first, second), result);
		first = "/abc/abc";
		second = "def/def/";
		result = "/abc/abc/def/def/";
		assertEquals(Utils.concatPaths(first, second), result);
		first = "/abc/abc/";
		second = "/def/def/";
		result = "/abc/abc/def/def/";
		assertEquals(Utils.concatPaths(first, second), result);
		first = "/abc/abc/";
		second = "def/def/";
		result = "/abc/abc/def/def/";
		assertEquals(Utils.concatPaths(first, second), result);
	}

	public void testAddDefaultFileEnding() {
		String filename = "test";
		assertEquals(Utils.addDefaultFileEnding(filename), "test" + Consts.PROJECT_EXTENTION);
	}

	public void testChangeFileEndingToPng() {
		String imageName = "blablabla.jpg";
		assertEquals(Utils.changeFileEndingToPng(imageName), "blablabla.png");
		String imageName1 = "blablabla.png";
		assertEquals(Utils.changeFileEndingToPng(imageName1), "blablabla.png");
		String imageName2 = "blablabla.jpeg";
		assertEquals(Utils.changeFileEndingToPng(imageName2), "blablabla.png");
	}

	public void testMD5CheckSumOfFile() {

		PrintWriter printWriter = null;

		File tempDir = new File(Consts.TMP_PATH);
		tempDir.mkdirs();

		File md5TestFile = new File(Consts.TMP_PATH + "/" + "catroid.txt");

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
}
