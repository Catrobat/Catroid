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
import java.io.PrintWriter;
import java.util.ArrayList;

import android.content.pm.PackageManager.NameNotFoundException;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.TapScript;
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

public class StorageHandlerTest extends AndroidTestCase {
	private StorageHandler storageHandler;

	public StorageHandlerTest() throws IOException {
		storageHandler = StorageHandler.getInstance();
	}

	@Override
	public void tearDown() {
		Utils.clearProject(getContext().getString(R.string.default_project_name));
		Utils.clearProject("testProject");
	}

	@Override
	public void setUp() {
		File defProject = new File(Consts.DEFAULT_ROOT + "/" + getContext().getString(R.string.default_project_name));

		if (defProject.exists()) {
			UtilFile.deleteDirectory(defProject);
		}
	}

	public void testSerializeProject() throws NameNotFoundException {

		int xPosition = 457;
		int yPosition = 598;
		double scaleValue = 0.8;

		Project project = new Project(getContext(), "testProject");
		Sprite firstSprite = new Sprite("first");
		Sprite secondSprite = new Sprite("second");
		Sprite thirdSprite = new Sprite("third");
		Sprite fourthSprite = new Sprite("fourth");
		Script testScript = new StartScript("testScript", firstSprite);
		Script otherScript = new StartScript("otherScript", secondSprite);
		HideBrick hideBrick = new HideBrick(firstSprite);
		ShowBrick showBrick = new ShowBrick(firstSprite);
		ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(secondSprite, scaleValue);
		ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick(firstSprite);
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(secondSprite, xPosition, yPosition);

		// adding Bricks: ----------------
		testScript.addBrick(hideBrick);
		testScript.addBrick(showBrick);
		testScript.addBrick(scaleCostumeBrick);
		testScript.addBrick(comeToFrontBrick);

		otherScript.addBrick(placeAtBrick); // secondSprite
		otherScript.setPaused(true);
		// -------------------------------

		firstSprite.getScriptList().add(testScript);
		secondSprite.getScriptList().add(otherScript);

		project.addSprite(firstSprite);
		project.addSprite(secondSprite);
		project.addSprite(thirdSprite);
		project.addSprite(fourthSprite);

		storageHandler.saveProject(project);

		Project loadedProject = storageHandler.loadProject("testProject");

		ArrayList<Sprite> preSpriteList = (ArrayList<Sprite>) project.getSpriteList();
		ArrayList<Sprite> postSpriteList = (ArrayList<Sprite>) loadedProject.getSpriteList();

		// Test sprite names:
		assertEquals("First sprite does not match after deserialization", preSpriteList.get(0).getName(),
				postSpriteList.get(0).getName());
		assertEquals("Second sprite does not match after deserialization", preSpriteList.get(1).getName(),
				postSpriteList.get(1).getName());
		assertEquals("Third sprite does not match after deserialization", preSpriteList.get(2).getName(),
				postSpriteList.get(2).getName());
		assertEquals("Fourth sprite does not match after deserialization", preSpriteList.get(3).getName(),
				postSpriteList.get(3).getName());
		assertEquals("Fifth sprite does not match after deserialization", preSpriteList.get(4).getName(),
				postSpriteList.get(4).getName());

		// Test project name:
		assertEquals("Title missmatch after deserialization", project.getName(), loadedProject.getName());

		// Test random brick values
		assertEquals("Scale was not deserialized right", scaleValue, ((ScaleCostumeBrick) (postSpriteList.get(1)
				.getScriptList().get(0).getBrickList().get(2))).getScale());
		assertEquals("XPosition was not deserialized right", xPosition, ((PlaceAtBrick) (postSpriteList.get(2)
				.getScriptList().get(0).getBrickList().get(0))).getXPosition());
		assertEquals("YPosition was not deserialized right", yPosition, ((PlaceAtBrick) (postSpriteList.get(2)
				.getScriptList().get(0).getBrickList().get(0))).getYPosition());

		assertFalse("paused should not be set in script", preSpriteList.get(1).getScriptList().get(0).isPaused());

		// Test version codes and names
		final int preVersionCode = project.getVersionCode();
		final int postVersionCode = loadedProject.getVersionCode();
		assertEquals("Version codes are not equal", preVersionCode, postVersionCode);

		final String preVersionName = project.getVersionName();
		final String postVersionName = loadedProject.getVersionName();
		assertEquals("Version names are not equal", preVersionName, postVersionName);
	}

	public void testDefaultProject() throws IOException {
		StorageHandler handler = StorageHandler.getInstance();
		ProjectManager project = ProjectManager.getInstance();
		project.setProject(handler.createDefaultProject(getContext()));
		assertEquals("not the right number of sprites in the default project", 2, project.getCurrentProject()
				.getSpriteList().size());
		assertEquals("not the right number of scripts in the second sprite of default project", 2, project
				.getCurrentProject()
				.getSpriteList().get(1).getScriptList().size());
		assertEquals("not the right number of bricks in the first script of Stage", 1, project.getCurrentProject()
				.getSpriteList().get(0).getScriptList().get(0).getBrickList().size());
		assertEquals("not the right number of bricks in the first script", 1, project.getCurrentProject()
				.getSpriteList().get(1).getScriptList().get(0).getBrickList().size());
		assertEquals("not the right number of bricks in the second script", 5, project.getCurrentProject()
				.getSpriteList().get(1).getScriptList().get(1).getBrickList().size());

		//test if images are existing:
		String imagePath = Consts.DEFAULT_ROOT + "/" + getContext().getString(R.string.default_project_name)
				+ Consts.IMAGE_DIRECTORY + "/" + Consts.NORMAL_CAT;
		File testFile = new File(imagePath);
		assertTrue("Image " + Consts.NORMAL_CAT + " does not exist", testFile.exists());
		imagePath = Consts.DEFAULT_ROOT + "/" + getContext().getString(R.string.default_project_name)
				+ Consts.IMAGE_DIRECTORY + "/" + Consts.BANZAI_CAT;
		testFile = new File(imagePath);
		assertTrue("Image " + Consts.BANZAI_CAT + " does not exist", testFile.exists());
		imagePath = Consts.DEFAULT_ROOT + "/" + getContext().getString(R.string.default_project_name)
				+ Consts.IMAGE_DIRECTORY + "/" + Consts.CHESHIRE_CAT;
		testFile = new File(imagePath);
		assertTrue("Image " + Consts.BACKGROUND + " does not exist", testFile.exists());
		imagePath = Consts.DEFAULT_ROOT + "/" + getContext().getString(R.string.default_project_name)
				+ Consts.IMAGE_DIRECTORY + "/" + Consts.BACKGROUND;
		testFile = new File(imagePath);
		assertTrue("Image " + Consts.BACKGROUND + " does not exist", testFile.exists());

	}

	public void testAliases() throws IOException {

		String projectName = "myProject";

		File proj = new File(Consts.DEFAULT_ROOT + "/" + projectName);
		if (proj.exists()) {
			UtilFile.deleteDirectory(proj);
		}

		Project project = new Project(getContext(), projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript("testScript", sprite);
		Script touchedScript = new TapScript("touchedScript", sprite);
		sprite.getScriptList().add(script);
		sprite.getScriptList().add(touchedScript);
		project.getSpriteList().add(sprite);

		ArrayList<Brick> a1 = new ArrayList<Brick>();
		ArrayList<Brick> a2 = new ArrayList<Brick>();
		a1.add(new ChangeXByBrick(sprite, 4));
		a1.add(new ChangeYByBrick(sprite, 5));
		a1.add(new ComeToFrontBrick(sprite));
		a1.add(new GoNStepsBackBrick(sprite, 5));
		a1.add(new HideBrick(sprite));
		a1.add(new IfStartedBrick(sprite, script));

		a2.add(new IfTouchedBrick(sprite, touchedScript));
		a2.add(new PlaceAtBrick(sprite, 50, 50));
		a2.add(new PlaySoundBrick(sprite));
		a2.add(new ScaleCostumeBrick(sprite, 50));
		a2.add(new SetCostumeBrick(sprite));
		a2.add(new SetXBrick(sprite, 50));
		a2.add(new SetYBrick(sprite, 50));
		a2.add(new ShowBrick(sprite));
		a2.add(new WaitBrick(sprite, 1000));

		for (Brick b : a1) {
			script.addBrick(b);
		}
		for (Brick b : a2) {
			touchedScript.addBrick(b);
		}

		StorageHandler.getInstance().saveProject(project);
		String spf = Utils.getProjectfileAsString(projectName);
		assertFalse("project contains package information", spf.contains("at.tugraz.ist"));

		proj = new File(Consts.DEFAULT_ROOT + "/" + projectName);
		if (proj.exists()) {
			UtilFile.deleteDirectory(proj);
		}
	}

	/*
	 * This test documents that our calculation of the MD5 checksum is correct aswell as that checksums should be
	 * upper case only
	 */
	public void testMD5Checksum() {
		String md5EmptyFile = "D41D8CD98F00B204E9800998ECF8427E";
		String md5CatroidString = "4F982D927F4784F69AD6D6AF38FD96AD";

		PrintWriter out = null;

		File tempDir = new File(Consts.TMP_PATH);
		tempDir.mkdirs();

		File md5TestFile = new File(Consts.TMP_PATH + "/" + "catroid.txt");

		if (md5TestFile.exists()) {
			md5TestFile.delete();
		}

		assertEquals("MD5 sums are not the same for empty file", md5EmptyFile,
				storageHandler.getMD5Checksum(md5TestFile));

		try {
			out = new PrintWriter(md5TestFile);
			out.print("catroid");
		} catch (IOException e) {

		} finally {
			if (out != null) {
				out.close();
			}
		}

		assertEquals("MD5 sums are not the same for catroid file", md5CatroidString,
				storageHandler.getMD5Checksum(md5TestFile));

		UtilFile.deleteDirectory(tempDir);
	}
}
