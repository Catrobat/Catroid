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
package at.tugraz.ist.catroid.test.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ChangeXByBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeYByBrick;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.SetYBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.content.bricks.WhenStartedBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.Utils;

public class MediaPathTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = at.tugraz.ist.catroid.test.R.raw.icon;
	private static final int SOUND_FILE_ID = at.tugraz.ist.catroid.test.R.raw.testsound;
	private static final int BIGBLUE_ID = at.tugraz.ist.catroid.test.R.raw.bigblue;
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
		ProjectManager.getInstance().fileChecksumContainer = new FileChecksumContainer();

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
	}

	public void testPathsInProjectFile() throws IOException {
		fillProjectWithAllBricksAndMediaFiles();
		String project = TestUtils.getProjectfileAsString(projectName);

		assertFalse("project contains DEFAULT_ROOT", project.contains(Consts.DEFAULT_ROOT));
		assertFalse("project contains IMAGE_DIRECTORY", project.contains(Consts.IMAGE_DIRECTORY));
		assertFalse("project contains SOUND_DIRECTORY", project.contains(Consts.SOUND_DIRECTORY));
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

		assertEquals("the copy does not equal the original image", Utils.md5Checksum(testImage), Utils
				.md5Checksum(testImageCopy));
		assertEquals("the copy does not equal the original image", Utils.md5Checksum(testImage), Utils
				.md5Checksum(testImageCopy2));
		assertEquals("the copy does not equal the original image", Utils.md5Checksum(testSound), Utils
				.md5Checksum(testSoundCopy));

		//check if copy doesn't save more instances of the same file:
		File directory = new File(Consts.DEFAULT_ROOT + "/" + projectName + "/" + Consts.IMAGE_DIRECTORY);
		File[] filesImage = directory.listFiles();

		//nomedia file is also in images folder
		assertEquals("Wrong amount of files in folder", 2, filesImage.length);
	}

	public void testCopyLargeImage() throws IOException, InterruptedException {
		StorageHandler storage = StorageHandler.getInstance();
		bigBlue2 = storage.copyImage(projectName, bigBlue.getAbsolutePath(), null);
		bigBlue3 = storage.copyImage(projectName, bigBlue.getAbsolutePath(), null);
		fillProjectWithAllBricksAndMediaFiles();

		File directory = new File(Consts.DEFAULT_ROOT + "/" + projectName + "/" + Consts.IMAGE_DIRECTORY);
		File[] filesImage = directory.listFiles();

		//nomedia file is also in images folder
		assertEquals("Wrong amount of files in folder", 3, filesImage.length);
		assertNotSame("The image was not downsized", Utils.md5Checksum(bigBlue), Utils.md5Checksum(bigBlue2));
		assertEquals("The copies are not the same", bigBlue2.hashCode(), bigBlue3.hashCode());
	}

	public void testDecrementUsage() {
		StorageHandler storageHandler = StorageHandler.getInstance();
		storageHandler.deleteFile(testImageCopy.getAbsolutePath());
		FileChecksumContainer container = ProjectManager.getInstance().fileChecksumContainer;
		assertTrue("checksum not in project although file should exist", container.containsChecksum(Utils
				.md5Checksum(testImageCopy)));
		storageHandler.deleteFile(testImageCopy2.getAbsolutePath());
		assertFalse("checksum in project although file should not exist", container.containsChecksum(Utils
				.md5Checksum(testImageCopy2)));

		File directory = new File(Consts.DEFAULT_ROOT + "/" + projectName + "/" + Consts.IMAGE_DIRECTORY);
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

		projectManager.fileChecksumContainer = null; //hack to delete the filechecksumcontainer and see if a new one is created on load
		projectManager.loadProject(projectName, getInstrumentation().getTargetContext(), false);

		assertTrue("does not contain checksum", projectManager.fileChecksumContainer.containsChecksum(checksumImage));
		assertTrue("does not contain checksum", projectManager.fileChecksumContainer.containsChecksum(checksumSound));
		assertFalse("returns true even when the checksum is for sure not added", projectManager.fileChecksumContainer
				.containsChecksum(checksumImage + "5"));

		assertEquals("The path to the file is not found or wrong", testImageCopy.getAbsolutePath(),
				projectManager.fileChecksumContainer.getPath(checksumImage));

		assertEquals("The path to the file is not found or wrong", testSoundCopy.getAbsolutePath(),
				projectManager.fileChecksumContainer.getPath(checksumSound));
	}

	public void testFileChecksumContainerNotInProjectFile() throws IOException {
		fillProjectWithAllBricksAndMediaFiles();
		String projectString = TestUtils.getProjectfileAsString(projectName);
		assertFalse("FileChecksumcontainer is in the project", projectString.contains("FileChecksumContainer"));
		ProjectManager.getInstance().loadProject(projectName, getInstrumentation().getTargetContext(), false);
		projectString = TestUtils.getProjectfileAsString(projectName);
		assertFalse("FileChecksumcontainer is in the project", projectString.contains("FileChecksumContainer"));
	}

	public void testCostumeDataListAndSoundInfoListInProjectFile() throws IOException {
		fillProjectWithAllBricksAndMediaFiles();
		String projectString = TestUtils.getProjectfileAsString(projectName);
		assertTrue("costumeDataList not in project", projectString.contains("costumeDataList"));
		assertTrue("soundList not in project", projectString.contains("soundList"));
		ProjectManager.getInstance().loadProject(projectName, getInstrumentation().getTargetContext(), false);
		projectString = TestUtils.getProjectfileAsString(projectName);
		assertTrue("costumeDataList not in project", projectString.contains("costumeDataList"));
		assertTrue("soundList not in project", projectString.contains("soundList"));
	}

	private void fillProjectWithAllBricksAndMediaFiles() throws IOException {
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript(sprite);
		Script whenScript = new WhenScript(sprite);
		sprite.addScript(script);
		sprite.addScript(whenScript);
		project.addSprite(sprite);

		SetCostumeBrick costumeBrick2 = new SetCostumeBrick(sprite);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(testImageCopy2.getName());
		costumeData.setCostumeName("testImageCopy2");
		costumeBrick2.setCostume(costumeData);
		sprite.getCostumeDataList().add(costumeData);

		ArrayList<Brick> brickList1 = new ArrayList<Brick>();
		ArrayList<Brick> brickList2 = new ArrayList<Brick>();
		brickList1.add(new ChangeXByBrick(sprite, 4));
		brickList1.add(new ChangeYByBrick(sprite, 5));
		brickList1.add(new ComeToFrontBrick(sprite));
		brickList1.add(new GoNStepsBackBrick(sprite, 5));
		brickList1.add(new HideBrick(sprite));
		brickList1.add(new WhenStartedBrick(sprite, script));
		brickList1.add(costumeBrick2);

		SetCostumeBrick costumeBrick = new SetCostumeBrick(sprite);
		costumeData = new CostumeData();
		costumeData.setCostumeFilename(testImageCopy.getName());
		costumeData.setCostumeName("testImageCopy");
		costumeBrick.setCostume(costumeData);
		sprite.getCostumeDataList().add(costumeData);

		PlaySoundBrick soundBrick = new PlaySoundBrick(sprite);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(testSoundCopy.getName());
		soundInfo.setTitle("title");
		soundBrick.setSoundInfo(soundInfo);
		sprite.getSoundList().add(soundInfo);

		brickList2.add(new PlaceAtBrick(sprite, 50, 50));
		brickList2.add(soundBrick);
		brickList2.add(new SetSizeToBrick(sprite, 50));
		brickList2.add(costumeBrick);
		brickList2.add(new SetXBrick(sprite, 50));
		brickList2.add(new SetYBrick(sprite, 50));
		brickList2.add(new ShowBrick(sprite));
		brickList2.add(new WaitBrick(sprite, 1000));

		for (Brick brick : brickList1) {
			script.addBrick(brick);
		}
		for (Brick brick : brickList2) {
			whenScript.addBrick(brick);
		}

		StorageHandler.getInstance().saveProject(project);
	}
}
