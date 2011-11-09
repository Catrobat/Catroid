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

package at.tugraz.ist.catroid.test.content;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.Utils;

public class FileChecksumContainerTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = R.raw.icon;
	private StorageHandler storageHandler;
	private ProjectManager projectManager;
	private File testImage;
	private File testSound;
	private String currentProjectName = "testCopyFile2";

	public FileChecksumContainerTest() throws IOException {
	}

	@Override
	protected void setUp() throws Exception {

		TestUtils.clearProject(currentProjectName);
		storageHandler = StorageHandler.getInstance();
		Project testCopyFile = new Project(null, currentProjectName);
		projectManager = ProjectManager.getInstance();
		storageHandler.saveProject(testCopyFile);
		projectManager.setProject(testCopyFile);

		final String imagePath = Consts.DEFAULT_ROOT + "/testImage.png";
		testImage = new File(imagePath);
		if (!testImage.exists()) {
			testImage.createNewFile();
		}
		InputStream in = getInstrumentation().getContext().getResources().openRawResource(IMAGE_FILE_ID);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), Consts.BUFFER_8K);

		byte[] buffer = new byte[Consts.BUFFER_8K];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		final String soundPath = Consts.DEFAULT_ROOT + "/testsound.mp3";
		testSound = new File(soundPath);
		if (!testSound.exists()) {
			testSound.createNewFile();
		}
		in = getInstrumentation().getContext().getResources().openRawResource(R.raw.testsound);
		out = new BufferedOutputStream(new FileOutputStream(testSound), Consts.BUFFER_8K);
		buffer = new byte[Consts.BUFFER_8K];
		length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.clearProject(currentProjectName);
		if (testImage != null && testImage.exists()) {
			testImage.delete();
		}
		if (testSound != null && testSound.exists()) {
			testSound.delete();
		}
	}

	public void testContainer() throws IOException, InterruptedException {

		storageHandler.copyImage(currentProjectName, testImage.getAbsolutePath());

		String checksumImage = Utils.md5Checksum(testImage);

		FileChecksumContainer container = projectManager.fileChecksumContainer;
		assertTrue("Checksum isn't in container", container.containsChecksum(checksumImage));

		//wait to get a different timestamp on next file
		Thread.sleep(2000);

		File newTestImage = storageHandler.copyImage(currentProjectName, testImage.getAbsolutePath());
		File imageDirectory = new File(Consts.DEFAULT_ROOT + "/" + currentProjectName + Consts.IMAGE_DIRECTORY + "/");
		File[] filesImage = imageDirectory.listFiles();

		//nomedia file is also in images folder
		assertEquals("Wrong amount of files in folder", 2, filesImage.length);

		File newTestSound = storageHandler.copySoundFile(testSound.getAbsolutePath());
		String checksumSound = Utils.md5Checksum(testSound);
		assertTrue("Checksum isn't in container", container.containsChecksum(checksumSound));
		File soundDirectory = new File(Consts.DEFAULT_ROOT + "/" + currentProjectName + Consts.SOUND_DIRECTORY);
		File[] filesSound = soundDirectory.listFiles();

		//nomedia file is also in sounds folder
		assertEquals("Wrong amount of files in folder", 2, filesSound.length);

		container.decrementUsage(newTestImage.getAbsolutePath());
		assertTrue("Checksum was deleted", container.containsChecksum(checksumImage));
		container.decrementUsage(newTestImage.getAbsolutePath());
		assertFalse("Checksum wasn't deleted", container.containsChecksum(checksumImage));
		container.decrementUsage(newTestSound.getAbsolutePath());
		assertFalse("Checksum wasn't deleted", container.containsChecksum(checksumSound));
	}

	public void testDeleteFile() throws IOException, InterruptedException {
		File newTestImage1 = storageHandler.copyImage(currentProjectName, testImage.getAbsolutePath());
		//wait to get a different timestamp on next file
		Thread.sleep(2000);

		storageHandler.deleteFile(newTestImage1.getAbsolutePath());
		File imageDirectory = new File(Consts.DEFAULT_ROOT + "/" + currentProjectName + Consts.IMAGE_DIRECTORY);
		File[] filesImage = imageDirectory.listFiles();
		assertEquals("Wrong amount of files in folder", 1, filesImage.length);

		File newTestSound = storageHandler.copySoundFile(testSound.getAbsolutePath());
		storageHandler.deleteFile(newTestSound.getAbsolutePath());

		File soundDirectory = new File(Consts.DEFAULT_ROOT + "/" + currentProjectName + Consts.SOUND_DIRECTORY);
		File[] filesSound = soundDirectory.listFiles();

		assertEquals("Wrong amount of files in folder", 1, filesSound.length);
	}
}
