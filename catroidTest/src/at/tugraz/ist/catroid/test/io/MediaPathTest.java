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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import at.tugraz.ist.catroid.utils.UtilFile;

public class MediaPathTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = at.tugraz.ist.catroid.test.R.raw.icon;
	private static final int SOUND_FILE_ID = at.tugraz.ist.catroid.test.R.raw.testsound;

	public void testPathsInSpfFile() throws IOException {
		String projectName = "myProject";

		File projectFile = new File(Consts.DEFAULT_ROOT + "/" + projectName);
		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);
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
		File image = savePictureInProject(projectName, "testimage.png", IMAGE_FILE_ID);
		costumeBrick.setCostume(image.getName());

		PlaySoundBrick soundBrick = new PlaySoundBrick(sprite);
		File soundFile = saveSoundFileInProject(projectName, "sound", SOUND_FILE_ID);
		soundBrick.setPathToSoundfile(soundFile.getName());

		project.getFileChecksumContainer().addChecksum(StorageHandler.getInstance().getMD5Checksum(image),
				image.getAbsolutePath());
		project.getFileChecksumContainer().addChecksum(StorageHandler.getInstance().getMD5Checksum(soundFile),
				soundFile.getAbsolutePath());

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
		String spf = StorageHandler.getInstance().getProjectfileAsString(projectName);
		System.out.println(spf);
		assertFalse("project contains path", spf.contains(Consts.DEFAULT_ROOT) || spf.contains(Consts.IMAGE_DIRECTORY)
				|| spf.contains(Consts.SOUND_DIRECTORY) || spf.contains("sdcard/"));

		projectFile = new File(Consts.DEFAULT_ROOT + "/" + projectName);
		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

	}

	private File savePictureInProject(String project, String name, int fileID) throws IOException {

		BufferedInputStream in = new BufferedInputStream(getInstrumentation().getContext().getResources()
				.openRawResource(fileID));
		final String imagePath = Consts.DEFAULT_ROOT + "/" + project + Consts.IMAGE_DIRECTORY + "/" + name;
		File testImage = new File(imagePath);
		testImage.createNewFile();

		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), 1024);
		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		return testImage;
	}

	private File saveSoundFileInProject(String project, String name, int fileID) throws IOException {
		// Note: File needs to be copied as MediaPlayer has no access to resources
		BufferedInputStream inputStream = new BufferedInputStream(getInstrumentation().getContext().getResources()
				.openRawResource(fileID));
		final String pathToSoundfile = Consts.DEFAULT_ROOT + "/" + project + Consts.SOUND_DIRECTORY + "/" + name;
		File soundFile = new File(pathToSoundfile);
		soundFile.createNewFile();

		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(soundFile), 1024);

		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}

		inputStream.close();
		outputStream.flush();
		outputStream.close();

		return soundFile;
	}

}
