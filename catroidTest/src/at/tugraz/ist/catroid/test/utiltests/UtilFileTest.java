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
package at.tugraz.ist.catroid.test.utiltests;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

public class UtilFileTest extends InstrumentationTestCase {

	private static final String TEST_PROJECT_NAME = TestUtils.TEST_PROJECT_NAME1;

	private File testDirectory;
	private File subDirectory;
	private File file1;
	private File file2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		testDirectory = new File(Constants.DEFAULT_ROOT + "/" + TestUtils.TEST_PROJECT_NAME2);
		testDirectory.mkdirs();
		file1 = new File(testDirectory.getAbsolutePath() + "/file1");
		file1.createNewFile();
		subDirectory = new File(testDirectory.getAbsolutePath() + "/subDirectory");
		subDirectory.mkdirs();
		file2 = new File(subDirectory.getAbsolutePath() + "/file2");
		file2.createNewFile();

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.deleteTestProjects();
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
			UtilFile.saveFileToProject(testDirectory.getName(), i + "testsound.mp3",
					at.tugraz.ist.catroid.test.R.raw.longtestsound, getInstrumentation().getContext(),
					UtilFile.TYPE_SOUND_FILE);
		}
		DecimalFormat decimalFormat = new DecimalFormat("#.#");
		String expected = decimalFormat.format(84.2) + " KB";
		assertEquals("not the expected string", expected, UtilFile.getSizeAsString(testDirectory));

		for (int i = 2; i < 48; i++) {
			UtilFile.saveFileToProject(testDirectory.getName(), i + "testsound.mp3",
					at.tugraz.ist.catroid.test.R.raw.longtestsound, getInstrumentation().getContext(),
					UtilFile.TYPE_SOUND_FILE);
		}
		decimalFormat = new DecimalFormat("#.0");
		expected = decimalFormat.format(2.0) + " MB";
		assertEquals("not the expected string", expected, UtilFile.getSizeAsString(testDirectory));

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

	public void testGetProjectFiles() throws InterruptedException {
		Project project = new Project(getInstrumentation().getTargetContext(), TEST_PROJECT_NAME);
		ProjectManager.getInstance().setProject(project);
		Sprite sprite = new Sprite("new sprite");
		project.addSprite(sprite);
		assertTrue("could not save project",
				TestUtils.saveProjectAndWait(this, ProjectManager.getInstance().getCurrentProject()));

		String catroidDirectoryPath = Constants.DEFAULT_ROOT;
		File catroidDirectory = new File(catroidDirectoryPath);
		File project1Directory = new File(catroidDirectory + "/" + TEST_PROJECT_NAME);

		List<File> projectList = UtilFile.getProjectFiles(catroidDirectory);

		assertTrue("project1 should be in Projectlist - is a valid Catroid project",
				projectList.contains(project1Directory));
		assertFalse("testDirectory should not be in Projectlist - not a Catroid project",
				projectList.contains(testDirectory));
	}
}
