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

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.StandardProjectHandler;
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
import at.tugraz.ist.catroid.utils.UtilFile;

public class StorageHandlerTest extends AndroidTestCase {
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

	public void testSerializeProject() {

		int xPosition = 457;
		int yPosition = 598;
		double size = 0.8;

		Project project = new Project(getContext(), "testProject");
		Sprite firstSprite = new Sprite("first");
		Sprite secondSprite = new Sprite("second");
		Sprite thirdSprite = new Sprite("third");
		Sprite fourthSprite = new Sprite("fourth");
		Script testScript = new StartScript(firstSprite);
		Script otherScript = new StartScript(secondSprite);
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
		final int preVersionCode = (Integer) TestUtils.getPrivateField("catroidVersionCode", project, false);
		final int postVersionCode = (Integer) TestUtils.getPrivateField("catroidVersionCode", loadedProject, false);
		assertEquals("Version codes are not equal", preVersionCode, postVersionCode);

		final String preVersionName = (String) TestUtils.getPrivateField("catroidVersionName", project, false);
		final String postVersionName = (String) TestUtils.getPrivateField("catroidVersionName", loadedProject, false);
		assertEquals("Version names are not equal", preVersionName, postVersionName);
	}

	public void testDefaultProject() throws IOException {
		ProjectManager projectManager = ProjectManager.getInstance();
		projectManager.setProject(StandardProjectHandler.createAndSaveStandardProject(getContext()));
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
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		ArrayList<CostumeData> backgroundCostumeList = currentProject.getSpriteList().get(0).getCostumeDataList();
		ArrayList<CostumeData> catroidCostumeList = currentProject.getSpriteList().get(1).getCostumeDataList();
		assertEquals("no background picture or too many pictures in background sprite", 1, backgroundCostumeList.size());
		assertEquals("wrong number of pictures in catroid sprite", 3, catroidCostumeList.size());

		String imagePath = backgroundCostumeList.get(0).getAbsolutePath();
		File testFile = new File(imagePath);
		assertTrue("Image " + backgroundCostumeList.get(0).getCostumeFileName() + " does not exist", testFile.exists());

		imagePath = catroidCostumeList.get(0).getAbsolutePath();
		testFile = new File(imagePath);
		assertTrue("Image " + catroidCostumeList.get(0).getCostumeFileName() + " does not exist", testFile.exists());

		imagePath = catroidCostumeList.get(1).getAbsolutePath();
		testFile = new File(imagePath);
		assertTrue("Image " + catroidCostumeList.get(1).getCostumeFileName() + " does not exist", testFile.exists());

		imagePath = catroidCostumeList.get(2).getAbsolutePath();
		testFile = new File(imagePath);
		assertTrue("Image " + catroidCostumeList.get(2).getCostumeFileName() + " does not exist", testFile.exists());
	}

	public void testAliasesAndXmlHeader() {

		String projectName = "myProject";

		File projectFile = new File(Consts.DEFAULT_ROOT + "/" + projectName);
		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		Project project = new Project(getContext(), projectName);
		Sprite sprite = new Sprite("testSprite");
		Script startScript = new StartScript(sprite);
		Script whenScript = new WhenScript(sprite);
		sprite.addScript(startScript);
		sprite.addScript(whenScript);
		project.addSprite(sprite);

		ArrayList<Brick> startScriptBrickList = new ArrayList<Brick>();
		ArrayList<Brick> whenScriptBrickList = new ArrayList<Brick>();
		startScriptBrickList.add(new ChangeXByBrick(sprite, 4));
		startScriptBrickList.add(new ChangeYByBrick(sprite, 5));
		startScriptBrickList.add(new ComeToFrontBrick(sprite));
		startScriptBrickList.add(new GoNStepsBackBrick(sprite, 5));
		startScriptBrickList.add(new HideBrick(sprite));
		startScriptBrickList.add(new WhenStartedBrick(sprite, startScript));

		whenScriptBrickList.add(new PlaySoundBrick(sprite));
		whenScriptBrickList.add(new SetSizeToBrick(sprite, 50));
		whenScriptBrickList.add(new SetCostumeBrick(sprite));
		whenScriptBrickList.add(new SetXBrick(sprite, 50));
		whenScriptBrickList.add(new SetYBrick(sprite, 50));
		whenScriptBrickList.add(new ShowBrick(sprite));
		whenScriptBrickList.add(new WaitBrick(sprite, 1000));

		for (Brick b : startScriptBrickList) {
			startScript.addBrick(b);
		}
		for (Brick b : whenScriptBrickList) {
			whenScript.addBrick(b);
		}

		storageHandler.saveProject(project);
		String projectString = TestUtils.getProjectfileAsString(projectName);
		assertFalse("project contains package information", projectString.contains("at.tugraz.ist"));

		String xmlHeader = (String) TestUtils.getPrivateField("XML_HEADER", storageHandler, false);
		assertTrue("Project file did not contain correct XML header.", projectString.startsWith(xmlHeader));

		projectFile = new File(Consts.DEFAULT_ROOT + "/" + projectName);
		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
	}

}
