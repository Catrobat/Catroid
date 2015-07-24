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
package org.catrobat.catroid.test.utiltests;

import android.os.Environment;
import android.test.InstrumentationTestCase;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;

public class UtilFileTest extends InstrumentationTestCase {
	private static final String TAG = UtilFileTest.class.getSimpleName();
	private static final String CATROID_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/Pocket Code";

	private File testDirectory;
	private File subDirectory;
	private File file1;
	private File file2;

	private String projectName = "project1";

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		UtilFile.deleteDirectory(new File(CATROID_DIRECTORY + "/testDirectory"));
		TestUtils.deleteTestProjects(projectName);

		testDirectory = new File(CATROID_DIRECTORY + "/testDirectory");
		testDirectory.mkdir();
		file1 = new File(testDirectory.getAbsolutePath() + "/file1");
		file1.createNewFile();
		subDirectory = new File(testDirectory.getAbsolutePath() + "/subDirectory");
		subDirectory.mkdir();
		file2 = new File(subDirectory.getAbsolutePath() + "/file2");
		file2.createNewFile();
	}

	@Override
	protected void tearDown() throws Exception {
		UtilFile.deleteDirectory(testDirectory);
		TestUtils.deleteTestProjects(projectName);
		super.tearDown();
	}

	public void testDeleteDirectory() {
		UtilFile.deleteDirectory(testDirectory);
		assertFalse("File in subdirectory still exists after call to deleteDirectory", file2.exists());
		assertFalse("Subdirectory in test directory still exists after call to deleteDirectory", subDirectory.exists());
		assertFalse("File in test directory still exists after call to deleteDirectory", file1.exists());
		assertFalse("Test directory still exists after call to deleteDirectory", testDirectory.exists());
	}

	public void testFileSize() throws IOException {
		for (int i = 0; i < 2; i++) {
			UtilFile.saveFileToProject("testDirectory", i + "testsound.mp3",
					org.catrobat.catroid.test.R.raw.longtestsound, getInstrumentation().getContext(),
					UtilFile.FileType.TYPE_SOUND_FILE);
		}

		double expectedSizeInKilobytes = 84.2;
		assertEquals("Unexpected file size String", String.format("%.1f KB", expectedSizeInKilobytes),
				UtilFile.getSizeAsString(testDirectory));

		for (int i = 2; i < 48; i++) {
			UtilFile.saveFileToProject("testDirectory", i + "testsound.mp3",
					org.catrobat.catroid.test.R.raw.longtestsound, getInstrumentation().getContext(),
					UtilFile.FileType.TYPE_SOUND_FILE);
		}
		DecimalFormat decimalFormat = new DecimalFormat("#.0");
		String expected = decimalFormat.format(2.0) + " MB";
		assertEquals("Unexpected file size String", expected, UtilFile.getSizeAsString(testDirectory));

		PrintWriter printWriter = null;

		File testFile = new File(Utils.buildPath(testDirectory.getAbsolutePath(), "catroid.txt"));

		try {
			testFile.createNewFile();

			printWriter = new PrintWriter(testFile);
			printWriter.print("catroid");
		} catch (IOException e) {
			Log.e(TAG, "File handling error", e);
		} finally {
			if (printWriter != null) {
				printWriter.close();
			}
		}

		assertEquals("Unexpected Filesize!", "7 Byte", UtilFile.getSizeAsString(testFile));

		UtilFile.deleteDirectory(testDirectory);
	}

	public void testGetProjectNames() {
		Project project = new Project(null, projectName);
		ProjectManager.getInstance().setProject(project);
		Sprite sprite = new Sprite("new sprite");
		project.addSprite(sprite);
		StorageHandler.getInstance().saveProject(project);

		File catroidDirectoryFile = new File(CATROID_DIRECTORY);
		File project1Directory = new File(catroidDirectoryFile + "/" + projectName);

		List<String> projectList = UtilFile.getProjectNames(catroidDirectoryFile);

		assertTrue("project1 should be in Projectlist - is a valid Catroid project",
				projectList.contains(project1Directory.getName()));
		assertFalse("testDirectory should not be in Projectlist - not a Catroid project",
				projectList.contains(testDirectory.getName()));
	}

	public void testEncodeAndDecodeSpecialCharsForFileSystem() {
		String projectName1 = ".*\"/:<>?\\|%";
		String projectName1Encoded = UtilFile.encodeSpecialCharsForFileSystem(projectName1);
		assertEquals("String projectName1 encoded not equal!", ".%2A%22%2F%3A%3C%3E%3F%5C%7C%25", projectName1Encoded);
		assertEquals("String projectName1 decoded not equal!", projectName1, UtilFile.decodeSpecialCharsForFileSystem(projectName1Encoded));

		String projectName2 = "../*\"/:<>?\\|";
		String projectName2Encoded = UtilFile.encodeSpecialCharsForFileSystem(projectName2);
		assertEquals("String projectName2 encoded not equal!", "..%2F%2A%22%2F%3A%3C%3E%3F%5C%7C", projectName2Encoded);
		assertEquals("String projectName2 decoded not equal!", projectName2, UtilFile.decodeSpecialCharsForFileSystem(projectName2Encoded));

		String projectName3 = "./*T?E\"S/T:T<E>S?T\\T\\E|S%";
		String projectName3Encoded = UtilFile.encodeSpecialCharsForFileSystem(projectName3);
		assertEquals("String projectName3 encoded not equal!", ".%2F%2AT%3FE%22S%2FT%3AT%3CE%3ES%3FT%5CT%5CE%7CS%25", projectName3Encoded);
		assertEquals("String projectName3 decoded not equal!", projectName3, UtilFile.decodeSpecialCharsForFileSystem(projectName3Encoded));

		String projectName4 = ".";
		String projectName4Encoded = UtilFile.encodeSpecialCharsForFileSystem(projectName4);
		assertEquals("String projectName4 encoded not equal!", "%2E", projectName4Encoded);
		assertEquals("String projectName4 decoded not equal!", projectName4, UtilFile.decodeSpecialCharsForFileSystem(projectName4Encoded));

		String projectName5 = "..";
		String projectName5Encoded = UtilFile.encodeSpecialCharsForFileSystem(projectName5);
		assertEquals("String projectName5 encoded not equal!", "%2E%2E", projectName5Encoded);
		assertEquals("String projectName5 decoded not equal!", projectName5, UtilFile.decodeSpecialCharsForFileSystem(projectName5Encoded));

		String projectName6 = "../*T?E\"S/T:%22T<E>S?T\\T\\E|S%äö|üß";
		String projectName6Encoded = UtilFile.encodeSpecialCharsForFileSystem(projectName6);
		assertEquals("String projectName6 encoded not equal!", "..%2F%2AT%3FE%22S%2FT%3A%2522T%3CE%3ES%3FT%5CT%5CE%7CS%25äö%7Cüß", projectName6Encoded);
		assertEquals("String projectName6 decoded not equal!", projectName6, UtilFile.decodeSpecialCharsForFileSystem(projectName6Encoded));
	}
}
