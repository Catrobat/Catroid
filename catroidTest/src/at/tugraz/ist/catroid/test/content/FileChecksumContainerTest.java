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
package at.tugraz.ist.catroid.test.content;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.Utils;

public class FileChecksumContainerTest extends InstrumentationTestCase {
	private static final int IMAGE_FILE_ID = R.raw.icon;
	private static final String TEST_PROJECT_NAME = TestUtils.TEST_PROJECT_NAME1;

	private Context context;
	private StorageHandler storageHandler;
	private ProjectManager projectManager;
	private File testImage;
	private File testSound;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getInstrumentation().getTargetContext();

		Project testCopyFile = new Project(getInstrumentation().getTargetContext(), TEST_PROJECT_NAME);
		testCopyFile.virtualScreenHeight = 1000;
		testCopyFile.virtualScreenWidth = 1000;

		storageHandler = StorageHandler.getInstance();
		projectManager = ProjectManager.getInstance();
		assertTrue("cannot save project", TestUtils.saveProjectAndWait(this, testCopyFile));
		projectManager.setProject(testCopyFile);

		testImage = new File(Constants.DEFAULT_ROOT + "/testImage.png");
		writeResourceStream(IMAGE_FILE_ID, testImage);

		testSound = new File(Constants.DEFAULT_ROOT + "/testsound.mp3");
		writeResourceStream(R.raw.testsound, testSound);
	}

	private void writeResourceStream(int rawResourceID, File file) throws IOException {
		InputStream in = context.getResources().openRawResource(rawResourceID);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file), Constants.BUFFER_8K);

		byte[] buffer = new byte[Constants.BUFFER_8K];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();
	}

	@Override
	protected void tearDown() throws Exception {
		if (testImage != null && testImage.exists()) {
			testImage.delete();
		}
		if (testSound != null && testSound.exists()) {
			testSound.delete();
		}
		TestUtils.deleteTestProjects();
		super.tearDown();
	}

	public void testContainer() throws IOException, InterruptedException {

		storageHandler.copyImage(TEST_PROJECT_NAME, testImage.getAbsolutePath(), null);

		String checksumImage = Utils.md5Checksum(testImage);

		FileChecksumContainer fileChecksumContainer = projectManager.getFileChecksumContainer();
		assertTrue("Checksum isn't in container", fileChecksumContainer.containsChecksum(checksumImage));

		//wait to get a different timestamp on next file
		Thread.sleep(2000);

		File newTestImage = storageHandler.copyImage(TEST_PROJECT_NAME, testImage.getAbsolutePath(), null);
		File imageDirectory = new File(Constants.DEFAULT_ROOT + "/" + TEST_PROJECT_NAME + "/"
				+ Constants.IMAGE_DIRECTORY + "/");
		File[] filesImage = imageDirectory.listFiles();

		//nomedia file is also in images folder
		assertEquals("Wrong amount of files in folder", 2, filesImage.length);

		File newTestSound = storageHandler.copySoundFile(testSound.getAbsolutePath());
		String checksumSound = Utils.md5Checksum(testSound);
		assertTrue("Checksum isn't in container", fileChecksumContainer.containsChecksum(checksumSound));
		File soundDirectory = new File(Constants.DEFAULT_ROOT + "/" + TEST_PROJECT_NAME + "/"
				+ Constants.SOUND_DIRECTORY);
		File[] filesSound = soundDirectory.listFiles();

		//nomedia file is also in sounds folder
		assertEquals("Wrong amount of files in folder", 2, filesSound.length);

		fileChecksumContainer.decrementUsage(newTestImage.getAbsolutePath());
		assertTrue("Checksum was deleted", fileChecksumContainer.containsChecksum(checksumImage));
		fileChecksumContainer.decrementUsage(newTestImage.getAbsolutePath());
		assertFalse("Checksum wasn't deleted", fileChecksumContainer.containsChecksum(checksumImage));
		fileChecksumContainer.decrementUsage(newTestSound.getAbsolutePath());
		assertFalse("Checksum wasn't deleted", fileChecksumContainer.containsChecksum(checksumSound));
	}

	public void testDeleteFile() throws IOException, InterruptedException {
		File newTestImage1 = storageHandler.copyImage(TEST_PROJECT_NAME, testImage.getAbsolutePath(), null);
		//wait to get a different timestamp on next file
		Thread.sleep(2000);

		storageHandler.deleteFile(newTestImage1.getAbsolutePath());
		File imageDirectory = new File(Constants.DEFAULT_ROOT + "/" + TEST_PROJECT_NAME + "/"
				+ Constants.IMAGE_DIRECTORY);
		File[] filesImage = imageDirectory.listFiles();
		assertEquals("Wrong amount of files in folder", 1, filesImage.length);

		File newTestSound = storageHandler.copySoundFile(testSound.getAbsolutePath());
		storageHandler.deleteFile(newTestSound.getAbsolutePath());

		File soundDirectory = new File(Constants.DEFAULT_ROOT + "/" + TEST_PROJECT_NAME + "/"
				+ Constants.SOUND_DIRECTORY);
		File[] filesSound = soundDirectory.listFiles();

		assertEquals("Wrong amount of files in folder", 1, filesSound.length);
	}
}
