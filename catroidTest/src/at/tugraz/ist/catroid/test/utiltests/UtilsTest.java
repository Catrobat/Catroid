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

import junit.framework.TestCase;
import at.tugraz.ist.catroid.utils.Utils;

public class UtilsTest extends TestCase {

	private final String testFileContent = "Hello, this is a Test-String";
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
		assertEquals(Utils.addDefaultFileEnding(filename), "test.spf");
	}

	public void testChangeFileEndingToPng() {
		String imageName = "blablabla.jpg";
		assertEquals(Utils.changeFileEndingToPng(imageName), "blablabla.png");
		String imageName1 = "blablabla.png";
		assertEquals(Utils.changeFileEndingToPng(imageName1), "blablabla.png");
		String imageName2 = "blablabla.jpeg";
		assertEquals(Utils.changeFileEndingToPng(imageName2), "blablabla.png");
	}

    //	private void createFile(String filePath) throws IOException {
    //		File toCreate = new File(filePath);
    //		if (!toCreate.exists()) {
    //			toCreate.createNewFile();
    //		}
    //	}

    //	public void testRenameProject() {
    //		String catroidRoot = "/sdcard/catroid"; 
    //		String testDirPath = Utils.concatPaths(catroidRoot, "testDir");
    //		String imagesPath = Utils.concatPaths(testDirPath, "images");
    //		String soundsPath = Utils.concatPaths(testDirPath, "sounds");
    //
    //		try {
    //			createFile(testDirPath);
    //			createFile(Utils.concatPaths(testDirPath, "testDir.spf"));
    //			createFile(imagesPath);
    //			createFile(Utils.concatPaths(imagesPath, "test.png"));
    //			createFile(soundsPath);
    //			createFile(Utils.concatPaths(soundsPath, "test.mp3"));
    //
    //			assertTrue("Project was renamed successfully", Utils.renameProject(null, "/sdcard/catroid/testDir/testDir.spf", "newProject"));
    //			File newProjectFile = new File("/sdcard/catroid/newProject/newProject.spf");
    //			assertTrue("Renamed project file exists in renamed project directory", newProjectFile.exists());
    //			File newTestImageFile = new File("/sdcard/catroid/newProject/images/test.png");
    //			assertTrue("Test image file was moved to renamed folder", newTestImageFile.exists());
    //			File newTestSoundFile = new File("/sdcard/catroid/newProject/sounds/test.mp3");
    //			assertTrue("Test image file was moved to renamed folder", newTestSoundFile.exists());
    //
    //			createFile(testDirPath);
    //			assertFalse("Can't rename to existing directory name", Utils.renameProject(null, "/sdcard/catroid/newProject/newProject.spf", "testDir"));
    //
    //		} catch (IOException e) {
    //			e.printStackTrace();
    //		} finally {
    //			File testDir = new File(testDirPath);
    //			if (testDir.exists()) {
    //				testDir.delete();
    //			}
    //			File newDir = new File("/sdcard/catroid/newProject");
    //			if (newDir.exists()) {
    //				newDir.delete();
    //			}
    //		}
    //
    //		File nonexistantPath = new File("/sdcard/catroid/i/dont/exist.spf");
    //		if (nonexistantPath.exists()) {
    //            nonexistantPath.delete();
    //        }
    //		assertFalse("Can't rename a project that doesn't exist", Utils.renameProject(null, nonexistantPath.getAbsolutePath(), "newProject"));
    //
    //		//assertFalse("If old project path is not set and the current project can't be read from the ConstructionSiteActivity, renaming fails",
    //		//		Utils.renameProject(null, null, "newProject"));
    //		assertFalse("New project name may not be null", Utils.renameProject(null, "oldProject/path", null));
    //	}
}
