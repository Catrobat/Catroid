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
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import android.test.InstrumentationTestCase;

public class UtilFileTest extends InstrumentationTestCase {
	private File testDirectory;
	private File subDirectory;
	private File file1;
	private File file2;

	private String projectName = "project1";
	private String catroidDirectory = "/sdcard/Pocket Code";

	@Override
	protected void setUp() throws Exception {

		UtilFile.deleteDirectory(new File(catroidDirectory + "/testDirectory"));
		TestUtils.clearProject(projectName);

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

	@Override
	protected void tearDown() throws Exception {
		UtilFile.deleteDirectory(testDirectory);
		TestUtils.clearProject(projectName);
		super.tearDown();
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

	public void testFileSize() throws IOException {
		for (int i = 0; i < 2; i++) {
			UtilFile.saveFileToProject("testDirectory", i + "testsound.mp3",
					org.catrobat.catroid.test.R.raw.longtestsound, getInstrumentation().getContext(),
					UtilFile.TYPE_SOUND_FILE);
		}

		double expectedSizeInKilobytes = 84.2;
		assertEquals("Unexpected file size String", String.format("%.1f KB", expectedSizeInKilobytes),
				UtilFile.getSizeAsString(testDirectory));

		for (int i = 2; i < 48; i++) {
			UtilFile.saveFileToProject("testDirectory", i + "testsound.mp3",
					org.catrobat.catroid.test.R.raw.longtestsound, getInstrumentation().getContext(),
					UtilFile.TYPE_SOUND_FILE);
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
			e.printStackTrace();
		} finally {
			if (printWriter != null) {
				printWriter.close();
			}
		}

		assertEquals("Unexpected Filesize!", "7 Byte", UtilFile.getSizeAsString(testFile));

		UtilFile.deleteDirectory(testDirectory);
	}

	public void testGetProjectFiles() {
		Project project = new Project(null, projectName);
		ProjectManager.INSTANCE.setProject(project);
		Sprite sprite = new Sprite("new sprite");
		project.addSprite(sprite);
		StorageHandler.getInstance().saveProject(project);

		File catroidDirectoryFile = new File(catroidDirectory);
		File project1Directory = new File(catroidDirectoryFile + "/" + projectName);

		List<File> projectList = UtilFile.getProjectFiles(catroidDirectoryFile);

		assertTrue("project1 should be in Projectlist - is a valid Catroid project",
				projectList.contains(project1Directory));
		assertFalse("testDirectory should not be in Projectlist - not a Catroid project",
				projectList.contains(testDirectory));
	}
}
