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

import android.content.pm.PackageManager.NameNotFoundException;
import android.test.AndroidTestCase;
import android.util.Log;
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
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.SetYBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class StorageHandlerTest extends AndroidTestCase {
	private static final String TAG = StorageHandlerTest.class.getSimpleName();
	private StorageHandler storageHandler;

	public StorageHandlerTest() throws IOException {
		storageHandler = StorageHandler.getInstance();
	}

	@Override
	public void tearDown() {
		TestUtils.clearProject(getContext().getString(R.string.default_project_name));
		TestUtils.clearProject("testProject");
	}

	@Override
	public void setUp() {
		File projectFile = new File(Consts.DEFAULT_ROOT + "/" + getContext().getString(R.string.default_project_name));

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
	}

	public void testSerializeProject() throws NameNotFoundException {

		int xPosition = 457;
		int yPosition = 598;
		double size = 0.8;

		Project project = new Project(getContext(), "testProject");
		Sprite firstSprite = new Sprite("first");
		Sprite secondSprite = new Sprite("second");
		Sprite thirdSprite = new Sprite("third");
		Sprite fourthSprite = new Sprite("fourth");
		Script testScript = new StartScript("testScript", firstSprite);
		Script otherScript = new StartScript("otherScript", secondSprite);
		HideBrick hideBrick = new HideBrick(firstSprite);
		ShowBrick showBrick = new ShowBrick(firstSprite);
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(secondSprite, size);
		ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick(firstSprite);
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(secondSprite, xPosition, yPosition);

		// adding Bricks: ----------------
		testScript.addBrick(hideBrick);
		testScript.addBrick(showBrick);
		testScript.addBrick(setSizeToBrick);
		testScript.addBrick(comeToFrontBrick);

		otherScript.addBrick(placeAtBrick); // secondSprite
		otherScript.setPaused(true);
		// -------------------------------

		firstSprite.addScript(testScript);
		secondSprite.addScript(otherScript);

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
		int actualXPosition = (Integer) TestUtils.getPrivateField("xPosition", (postSpriteList.get(2).getScript(0)
				.getBrickList().get(0)), false);
		int actualYPosition = (Integer) TestUtils.getPrivateField("yPosition", (postSpriteList.get(2).getScript(0)
				.getBrickList().get(0)), false);

		double actualSize = (Double) TestUtils.getPrivateField("size", (postSpriteList.get(1).getScript(0)
				.getBrickList().get(2)), false);

		assertEquals("Size was not deserialized right", size, actualSize);
		assertEquals("XPosition was not deserialized right", xPosition, actualXPosition);
		assertEquals("YPosition was not deserialized right", yPosition, actualYPosition);

		assertFalse("paused should not be set in script", preSpriteList.get(1).getScript(0).isPaused());

		// Test version codes and names
		final int preVersionCode = project.getVersionCode();
		final int postVersionCode = loadedProject.getVersionCode();
		assertEquals("Version codes are not equal", preVersionCode, postVersionCode);

		final String preVersionName = project.getVersionName();
		final String postVersionName = loadedProject.getVersionName();
		assertEquals("Version names are not equal", preVersionName, postVersionName);
	}

	public void testDefaultProject() throws IOException {
		ProjectManager projectManager = ProjectManager.getInstance();
		projectManager.setProject(storageHandler.createDefaultProject(getContext()));
		assertEquals("not the right number of sprites in the default project", 2, projectManager.getCurrentProject()
				.getSpriteList().size());
		assertEquals("not the right number of scripts in the second sprite of default project", 2, projectManager
				.getCurrentProject().getSpriteList().get(1).getNumberOfScripts());
		assertEquals("not the right number of bricks in the first script of Stage", 1, projectManager
				.getCurrentProject().getSpriteList().get(0).getScript(0).getBrickList().size());
		assertEquals("not the right number of bricks in the first script", 1, projectManager.getCurrentProject()
				.getSpriteList().get(1).getScript(0).getBrickList().size());
		assertEquals("not the right number of bricks in the second script", 5, projectManager.getCurrentProject()
				.getSpriteList().get(1).getScript(1).getBrickList().size());

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

	public void testAliasesAndXmlHeader() throws IOException {

		String projectName = "myProject";

		File proj = new File(Consts.DEFAULT_ROOT + "/" + projectName);
		if (proj.exists()) {
			UtilFile.deleteDirectory(proj);
		}

		Project project = new Project(getContext(), projectName);
		Sprite sprite = new Sprite("testSprite");
		Script startScript = new StartScript("testScript", sprite);
		Script tapScript = new TapScript("touchedScript", sprite);
		sprite.addScript(startScript);
		sprite.addScript(tapScript);
		project.addSprite(sprite);

		ArrayList<Brick> startScriptBrickList = new ArrayList<Brick>();
		ArrayList<Brick> tapScriptBrickList = new ArrayList<Brick>();
		startScriptBrickList.add(new ChangeXByBrick(sprite, 4));
		startScriptBrickList.add(new ChangeYByBrick(sprite, 5));
		startScriptBrickList.add(new ComeToFrontBrick(sprite));
		startScriptBrickList.add(new GoNStepsBackBrick(sprite, 5));
		startScriptBrickList.add(new HideBrick(sprite));
		startScriptBrickList.add(new IfStartedBrick(sprite, startScript));

		tapScriptBrickList.add(new IfTouchedBrick(sprite, tapScript));
		tapScriptBrickList.add(new PlaceAtBrick(sprite, 50, 50));
		tapScriptBrickList.add(new PlaySoundBrick(sprite));
		tapScriptBrickList.add(new SetSizeToBrick(sprite, 50));
		tapScriptBrickList.add(new SetCostumeBrick(sprite));
		tapScriptBrickList.add(new SetXBrick(sprite, 50));
		tapScriptBrickList.add(new SetYBrick(sprite, 50));
		tapScriptBrickList.add(new ShowBrick(sprite));
		tapScriptBrickList.add(new WaitBrick(sprite, 1000));

		for (Brick b : startScriptBrickList) {
			startScript.addBrick(b);
		}
		for (Brick b : tapScriptBrickList) {
			tapScript.addBrick(b);
		}

		storageHandler.saveProject(project);
		String projectString = TestUtils.getProjectfileAsString(projectName);
		assertFalse("project contains package information", projectString.contains("at.tugraz.ist"));

		String xmlHeader = (String) TestUtils.getPrivateField("XML_HEADER", storageHandler, false);
		Log.v(TAG, xmlHeader);
		assertTrue("Project file did not contain correct XML header.", projectString.startsWith(xmlHeader));

		proj = new File(Consts.DEFAULT_ROOT + "/" + projectName);
		if (proj.exists()) {
			UtilFile.deleteDirectory(proj);
		}
	}
}
