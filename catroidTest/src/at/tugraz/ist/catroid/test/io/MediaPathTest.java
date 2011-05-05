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
package at.tugraz.ist.catroid.test.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ChangeXByBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeYByBrick;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.IfStartedBrick;
import at.tugraz.ist.catroid.content.bricks.IfTouchedBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.SetYBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.util.Utils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class MediaPathTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = at.tugraz.ist.catroid.test.R.raw.icon;
	private static final int SOUND_FILE_ID = at.tugraz.ist.catroid.test.R.raw.testsound;
	private Project project;
	private File testImage;
	private File testSound;
	private File testImageCopy;
	private File testSoundCopy;
	private String imageName = "testImage.png";
	private String soundName = "testSound.mp3";
	private String projectName = "testProject3";

	@Override
	protected void setUp() throws Exception {

		Utils.clearProject(projectName);

		project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		Project mockProject = new Project(getInstrumentation().getTargetContext(), "mockProject");
		StorageHandler.getInstance().saveProject(mockProject);

		testImage = Utils.saveFileToProject(projectName, imageName, IMAGE_FILE_ID, getInstrumentation().getContext(),
				Utils.TYPE_IMAGE_FILE);

		testImageCopy = StorageHandler.getInstance().copyImage(projectName, testImage.getAbsolutePath());

		testSound = Utils.saveFileToProject(projectName, soundName, SOUND_FILE_ID, getInstrumentation().getContext(),
				Utils.TYPE_SOUND_FILE);

		testSoundCopy = StorageHandler.getInstance().copySoundFile(testSound.getAbsolutePath());
	}

	@Override
	protected void tearDown() throws Exception {

		File projectFile = new File(Consts.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
	}

	public void testPathsInSpfFile() throws IOException {
		createProjectWithAllBricksAndMediaFiles();
		String spf = StorageHandler.getInstance().getProjectfileAsString(projectName);

		assertFalse("project contains DEFAULT_ROOT", spf.contains(Consts.DEFAULT_ROOT));
		assertFalse("project contains IMAGE_DIRECTORY", spf.contains(Consts.IMAGE_DIRECTORY));
		assertFalse("project contains SOUND_DIRECTORY", spf.contains(Consts.SOUND_DIRECTORY));
		assertFalse("project contains sdcard/", spf.contains("sdcard/"));
	}

	public void testFilenameChecksum() throws IOException {
		createProjectWithAllBricksAndMediaFiles();
		String spf = StorageHandler.getInstance().getProjectfileAsString(projectName);

		String checksumImage = StorageHandler.getInstance().getMD5Checksum(testImageCopy);
		String checksumSound = StorageHandler.getInstance().getMD5Checksum(testSoundCopy);

		String expectedImagename = checksumImage + "_" + imageName;
		String expectedSoundname = checksumSound + "_" + soundName;

		assertTrue("expected image name not in spf", spf.contains(expectedImagename));
		assertTrue("expected sound name not in spf", spf.contains(expectedSoundname));

		String expectedImagenameTags = ">" + checksumImage + "_" + imageName + "<";
		String expectedSoundnameTags = ">" + checksumSound + "_" + soundName + "<";

		assertTrue("the imagename is not only the expected name", spf.contains(expectedImagenameTags));
		assertTrue("the soundname is not only the expected name", spf.contains(expectedSoundnameTags));

		String unexpectedImagenameTags = ">" + imageName + "<";
		String unexpectedSoundnameTags = ">" + soundName + "<";
		assertFalse("the imagename has no checksum", spf.contains(unexpectedImagenameTags));
		assertFalse("the soundname has no checksum", spf.contains(unexpectedSoundnameTags));

		StorageHandler storage = StorageHandler.getInstance();
		assertEquals("the copy does not equal the original image", storage.getMD5Checksum(testImage),
				storage.getMD5Checksum(testImageCopy));
		assertEquals("the copy does not equal the original image", storage.getMD5Checksum(testSound),
				storage.getMD5Checksum(testSoundCopy));
	}

	private void createProjectWithAllBricksAndMediaFiles() throws IOException {
		Sprite sprite = new Sprite("testSprite");
		Script script = new Script("testScript", sprite);
		Script touchedScript = new Script("touchedScript", sprite);
		sprite.getScriptList().add(script);
		sprite.getScriptList().add(touchedScript);
		project.getSpriteList().add(sprite);

		ArrayList<Brick> brickList1 = new ArrayList<Brick>();
		ArrayList<Brick> brickList2 = new ArrayList<Brick>();
		brickList1.add(new ChangeXByBrick(sprite, 4));
		brickList1.add(new ChangeYByBrick(sprite, 5));
		brickList1.add(new ComeToFrontBrick(sprite));
		brickList1.add(new GoNStepsBackBrick(sprite, 5));
		brickList1.add(new HideBrick(sprite));
		brickList1.add(new IfStartedBrick(sprite, script));

		SetCostumeBrick costumeBrick = new SetCostumeBrick(sprite);

		costumeBrick.setCostume(testImageCopy.getName());

		PlaySoundBrick soundBrick = new PlaySoundBrick(sprite);
		soundBrick.setPathToSoundfile(testSoundCopy.getName());

		project.getFileChecksumContainer().addChecksum(StorageHandler.getInstance().getMD5Checksum(testImageCopy),
					testImageCopy.getAbsolutePath());
		project.getFileChecksumContainer().addChecksum(StorageHandler.getInstance().getMD5Checksum(testSoundCopy),
					testSoundCopy.getAbsolutePath());

		brickList2.add(new IfTouchedBrick(sprite, touchedScript));
		brickList2.add(new PlaceAtBrick(sprite, 50, 50));
		brickList2.add(soundBrick);
		brickList2.add(new ScaleCostumeBrick(sprite, 50));
		brickList2.add(costumeBrick);
		brickList2.add(new SetXBrick(sprite, 50));
		brickList2.add(new SetYBrick(sprite, 50));
		brickList2.add(new ShowBrick(sprite));
		brickList2.add(new WaitBrick(sprite, 1000));

		for (Brick brick : brickList1) {
			script.addBrick(brick);
		}
		for (Brick brick : brickList2) {
			touchedScript.addBrick(brick);
		}

		StorageHandler.getInstance().saveProject(project);
	}

}
