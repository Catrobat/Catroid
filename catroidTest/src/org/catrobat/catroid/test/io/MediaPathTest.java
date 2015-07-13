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
package org.catrobat.catroid.test.io;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MediaPathTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = org.catrobat.catroid.test.R.raw.icon;
	private static final int SOUND_FILE_ID = org.catrobat.catroid.test.R.raw.testsound;
	private static final int BIGBLUE_ID = org.catrobat.catroid.test.R.raw.bigblue;
	private Project project;
	private File testImage;
	private File bigBlue;
	private File testSound;
	private File testImageCopy;
	private File testImageCopy2;
	private File testSoundCopy;

	private File bigBlue2;
	private File bigBlue3;

	private String imageName = "testImage.png";
	private String soundName = "testSound.mp3";
	private String projectName = "testProject7";
	private String bigBlueName = "bigblue.png";

	@Override
	protected void setUp() throws Exception {

		TestUtils.clearProject(projectName);
		TestUtils.clearProject("mockProject");

		project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setFileChecksumContainer(new FileChecksumContainer());

		Project mockProject = new Project(getInstrumentation().getTargetContext(), "mockProject");
		StorageHandler.getInstance().saveProject(mockProject);

		testImage = TestUtils.saveFileToProject(mockProject.getName(), imageName, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		bigBlue = TestUtils.saveFileToProject(mockProject.getName(), bigBlueName, BIGBLUE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		testSound = TestUtils.saveFileToProject(mockProject.getName(), soundName, SOUND_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_SOUND_FILE);

		//copy files with the Storagehandler copy function
		testImageCopy = StorageHandler.getInstance().copyImage(projectName, testImage.getAbsolutePath(), null);
		testImageCopy2 = StorageHandler.getInstance().copyImage(projectName, testImage.getAbsolutePath(), null);
		testSoundCopy = StorageHandler.getInstance().copySoundFile(testSound.getAbsolutePath());
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.clearProject(projectName);
		TestUtils.clearProject("mockProject");
		super.tearDown();
	}

	public void testPathsInProjectFile() throws IOException {
		fillProjectWithAllBricksAndMediaFiles();
		String project = TestUtils.getProjectfileAsString(projectName);

		assertFalse("project contains DEFAULT_ROOT", project.contains(Constants.DEFAULT_ROOT));
		assertFalse("project contains IMAGE_DIRECTORY", project.contains(Constants.IMAGE_DIRECTORY));
		assertFalse("project contains SOUND_DIRECTORY", project.contains(Constants.SOUND_DIRECTORY));
		assertFalse("project contains sdcard/", project.contains("sdcard/"));
	}

	public void testFilenameChecksum() throws IOException {
		fillProjectWithAllBricksAndMediaFiles();

		String project = TestUtils.getProjectfileAsString(projectName);

		String checksumImage = Utils.md5Checksum(testImageCopy);
		String checksumSound = Utils.md5Checksum(testSoundCopy);

		String unexpectedImagenameTags = ">" + imageName + "<";
		String unexpectedSoundnameTags = ">" + soundName + "<";
		assertFalse("the imagename has no checksum", project.contains(unexpectedImagenameTags));
		assertFalse("the soundname has no checksum", project.contains(unexpectedSoundnameTags));

		String expectedImagename = checksumImage + "_" + imageName;
		String expectedSoundname = checksumSound + "_" + soundName;

		assertTrue("expected image name not in project", project.contains(expectedImagename));
		assertTrue("expected sound name not in project", project.contains(expectedSoundname));

		String expectedImagenameTags = ">" + checksumImage + "_" + imageName + "<";
		String expectedSoundnameTags = ">" + checksumSound + "_" + soundName + "<";

		assertTrue("unexpected imagename", project.contains(expectedImagenameTags));
		assertTrue("unexpected soundname", project.contains(expectedSoundnameTags));

		assertEquals("the copy does not equal the original image", Utils.md5Checksum(testImage),
				Utils.md5Checksum(testImageCopy));
		assertEquals("the copy does not equal the original image", Utils.md5Checksum(testImage),
				Utils.md5Checksum(testImageCopy2));
		assertEquals("the copy does not equal the original image", Utils.md5Checksum(testSound),
				Utils.md5Checksum(testSoundCopy));

		//check if copy doesn't save more instances of the same file:
		File directory = new File(Constants.DEFAULT_ROOT + "/" + projectName + "/" + Constants.IMAGE_DIRECTORY);
		File[] filesImage = directory.listFiles();

		//nomedia file is also in images folder
		assertEquals("Wrong amount of files in folder", 2, filesImage.length);
	}

	public void testCopyLargeImage() throws IOException, InterruptedException {
		StorageHandler storage = StorageHandler.getInstance();
		bigBlue2 = storage.copyImage(projectName, bigBlue.getAbsolutePath(), null);
		bigBlue3 = storage.copyImage(projectName, bigBlue.getAbsolutePath(), null);
		fillProjectWithAllBricksAndMediaFiles();

		File directory = new File(Constants.DEFAULT_ROOT + "/" + projectName + "/" + Constants.IMAGE_DIRECTORY);
		File[] filesImage = directory.listFiles();

		//nomedia file is also in images folder
		assertEquals("Wrong amount of files in folder", 3, filesImage.length);
		assertNotSame("The image was not downsized", Utils.md5Checksum(bigBlue), Utils.md5Checksum(bigBlue2));
		assertEquals("The copies are not the same", bigBlue2.hashCode(), bigBlue3.hashCode());
	}

	public void testIncrementUsage() {
		FileChecksumContainer container = ProjectManager.getInstance().getFileChecksumContainer();
		Sprite testSprite = new Sprite("testSprite");
		ArrayList<LookData> lookDataList = new ArrayList<LookData>();

		LookData lookData = new LookData();
		lookData.setLookName("testLook");
		lookData.setLookFilename(Utils.md5Checksum(testImage) + "_" + testImage.getName());
		lookDataList.add(lookData);
		testSprite.setLookDataList(lookDataList);
		project.addSprite(testSprite);
		project.addSprite(testSprite.clone());

		assertEquals("Usage counter has not been incremented!", 3, container.getUsage(Utils.md5Checksum(testImage)));
	}

	public void testDecrementUsage() {
		StorageHandler storageHandler = StorageHandler.getInstance();
		storageHandler.deleteFile(testImageCopy.getAbsolutePath());
		FileChecksumContainer container = ProjectManager.getInstance().getFileChecksumContainer();
		assertTrue("checksum not in project although file should exist",
				container.containsChecksum(Utils.md5Checksum(testImageCopy)));
		storageHandler.deleteFile(testImageCopy2.getAbsolutePath());
		assertFalse("checksum in project although file should not exist",
				container.containsChecksum(Utils.md5Checksum(testImageCopy2)));

		File directory = new File(Constants.DEFAULT_ROOT + "/" + projectName + "/" + Constants.IMAGE_DIRECTORY);
		File[] filesImage = directory.listFiles();

		//nomedia file is also in images folder
		assertEquals("Wrong amount of files in folder - delete unsuccessfull", 1, filesImage.length);

		storageHandler.deleteFile(testImageCopy.getAbsolutePath()); //there a FileNotFoundException is thrown and caught (this is expected behavior)
	}

	public void testContainerOnLoadProject() throws IOException {
		fillProjectWithAllBricksAndMediaFiles();
		ProjectManager projectManager = ProjectManager.getInstance();
		String checksumImage = Utils.md5Checksum(testImage);
		String checksumSound = Utils.md5Checksum(testSound);

		projectManager.setFileChecksumContainer(null); //hack to delete the filechecksumcontainer and see if a new one is created on load
		try {
			ProjectManager.getInstance().loadProject(projectName, getInstrumentation().getTargetContext());
			assertTrue("Load project worked correctly", true);
		} catch (ProjectException projectException) {
			fail("Project is not loaded successfully");
		}

		assertTrue("does not contain checksum",
				projectManager.getFileChecksumContainer().containsChecksum(checksumImage));
		assertTrue("does not contain checksum",
				projectManager.getFileChecksumContainer().containsChecksum(checksumSound));
		assertFalse("returns true even when the checksum is for sure not added", projectManager
				.getFileChecksumContainer().containsChecksum(checksumImage + "5"));

		assertEquals("The path to the file is not found or wrong", testImageCopy.getAbsolutePath(), projectManager
				.getFileChecksumContainer().getPath(checksumImage));

		assertEquals("The path to the file is not found or wrong", testSoundCopy.getAbsolutePath(), projectManager
				.getFileChecksumContainer().getPath(checksumSound));
	}

	public void testFileChecksumContainerNotInProjectFile() throws IOException {
		fillProjectWithAllBricksAndMediaFiles();
		String projectString = TestUtils.getProjectfileAsString(projectName);
		assertFalse("FileChecksumcontainer is in the project", projectString.contains("FileChecksumContainer"));
		try {
			ProjectManager.getInstance().loadProject(projectName, getInstrumentation().getTargetContext());
			assertTrue("Load project worked correctly", true);
		} catch (ProjectException projectException) {
			fail("Project is not loaded successfully");
		}
		projectString = TestUtils.getProjectfileAsString(projectName);
		assertFalse("FileChecksumcontainer is in the project", projectString.contains("FileChecksumContainer"));
	}

	public void testLookDataListAndSoundInfoListInProjectFile() throws IOException {
		fillProjectWithAllBricksAndMediaFiles();
		String projectString = TestUtils.getProjectfileAsString(projectName);
		assertTrue("LookDataList not in project", projectString.contains("lookList"));
		assertTrue("SoundList not in project", projectString.contains("soundList"));
		try {
			ProjectManager.getInstance().loadProject(projectName, getInstrumentation().getTargetContext());
		} catch (ProjectException projectException) {
			fail("Project is not loaded successfully");
		}
		projectString = TestUtils.getProjectfileAsString(projectName);
		assertTrue("LookDataList not in project", projectString.contains("lookList"));
		assertTrue("SoundList not in project", projectString.contains("soundList"));
	}

	private void fillProjectWithAllBricksAndMediaFiles() throws IOException {
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();
		Script whenScript = new WhenScript();
		sprite.addScript(script);
		sprite.addScript(whenScript);
		project.addSprite(sprite);

		SetLookBrick lookBrick2 = new SetLookBrick();
		LookData lookData = new LookData();
		lookData.setLookFilename(testImageCopy2.getName());
		lookData.setLookName("testImageCopy2");
		lookBrick2.setLook(lookData);
		sprite.getLookDataList().add(lookData);

		ArrayList<Brick> brickList1 = new ArrayList<Brick>();
		ArrayList<Brick> brickList2 = new ArrayList<Brick>();
		brickList1.add(new ChangeXByNBrick(4));
		brickList1.add(new ChangeYByNBrick(5));
		brickList1.add(new ComeToFrontBrick());
		brickList1.add(new GoNStepsBackBrick(5));
		brickList1.add(new HideBrick());
		brickList1.add(new WhenStartedBrick(script));
		brickList1.add(lookBrick2);

		SetLookBrick lookBrick = new SetLookBrick();
		lookData = new LookData();
		lookData.setLookFilename(testImageCopy.getName());
		lookData.setLookName("testImageCopy");
		lookBrick.setLook(lookData);
		sprite.getLookDataList().add(lookData);

		PlaySoundBrick soundBrick = new PlaySoundBrick();
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(testSoundCopy.getName());
		soundInfo.setTitle("title");
		soundBrick.setSoundInfo(soundInfo);
		sprite.getSoundList().add(soundInfo);

		brickList2.add(new PlaceAtBrick(50, 50));
		brickList2.add(soundBrick);
		brickList2.add(new SetSizeToBrick(50));
		brickList2.add(lookBrick);
		brickList2.add(new SetXBrick(50));
		brickList2.add(new SetYBrick(50));
		brickList2.add(new ShowBrick());
		brickList2.add(new WaitBrick(1000));

		for (Brick brick : brickList1) {
			script.addBrick(brick);
		}
		for (Brick brick : brickList2) {
			whenScript.addBrick(brick);
		}

		StorageHandler.getInstance().saveProject(project);
	}
}
