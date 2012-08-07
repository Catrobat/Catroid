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
package at.tugraz.ist.catroid.test.content.project;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.Utils;

public class ProjectManagerTest extends InstrumentationTestCase {
	private static final String TEST_PROJECT_NAME = TestUtils.TEST_PROJECT_NAME1;
	private static final String SPRITE_NAME1 = "Zuul";
	private static final String SPRITE_NAME2 = "Zuuul";

	private Context context;
	private Script testScript;
	private Script otherScript;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getInstrumentation().getTargetContext();
		Utils.updateScreenWidthAndHeight(context);
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.deleteTestProjects();
		super.tearDown();
	}

	public void testBasicFunctions() throws NameNotFoundException, IOException, InterruptedException {
		ProjectManager projectManager = ProjectManager.getInstance();
		assertNull("there is a current sprite set", projectManager.getCurrentSprite());
		assertNull("there is a current script set", projectManager.getCurrentScript());

		// initializeNewProject
		projectManager.initializeNewProject(TEST_PROJECT_NAME, context);
		// Wait for asynchronous project saving to finish.
		Thread.sleep(1000);
		assertNotNull("no current project set", projectManager.getCurrentProject());
		assertEquals("The Projectname is not " + TEST_PROJECT_NAME, TEST_PROJECT_NAME, projectManager.getCurrentProject()
				.getName());

		// verify that new project is default project (see StorageHandler.createDefaultProject)
		int spriteCount = projectManager.getCurrentProject().getSpriteList().size();
		assertEquals("New project has wrong number of sprites", 2, spriteCount);
		Sprite catroid = projectManager.getCurrentProject().getSpriteList().get(1);
		assertEquals("Catroid sprite has wrong number of scripts", 2, catroid.getNumberOfScripts());

		// add sprite
		Sprite sprite = new Sprite(SPRITE_NAME1);
		projectManager.addSprite(sprite);
		projectManager.setCurrentSprite(sprite);

		assertNotNull("No current sprite set", projectManager.getCurrentSprite());
		assertEquals("The Spritename is not " + SPRITE_NAME1, SPRITE_NAME1, projectManager.getCurrentSprite()
				.getName());

		// add script
		Script startScript = new StartScript(sprite);
		projectManager.addScript(startScript);
		projectManager.setCurrentScript(startScript);

		assertNotNull("no current script set", projectManager.getCurrentScript());

		// loadProject
		projectManager.loadProject(TEST_PROJECT_NAME, context, false);
		assertNotNull("no current project set", projectManager.getCurrentProject());
		assertEquals("The Projectname is not " + TEST_PROJECT_NAME, TEST_PROJECT_NAME, projectManager.getCurrentProject()
				.getName());
		assertNull("there is a current sprite set", projectManager.getCurrentSprite());
		assertNull("there is a current script set", projectManager.getCurrentScript());

		// addSprite
		Sprite sprite2 = new Sprite(SPRITE_NAME2);
		projectManager.addSprite(sprite2);
		assertTrue("Sprite not in current Project", projectManager.getCurrentProject().getSpriteList()
				.contains(sprite2));

		// addScript
		projectManager.setCurrentSprite(sprite2);
		Script script2 = new StartScript(sprite2);
		projectManager.addScript(script2);
		assertTrue("Script not in current Sprite", projectManager.getCurrentSprite().getScriptIndex(script2) != -1);

		// addBrick
		projectManager.setCurrentScript(script2);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite2);
		projectManager.getCurrentScript().addBrick(setCostumeBrick);
		assertTrue("Brick not in current Script",
				projectManager.getCurrentScript().getBrickList().contains(setCostumeBrick));
	}

	public void testRenameProject() throws IOException, InterruptedException {
		String oldProjectName = "oldProject";
		String newProjectName = "newProject";
		ProjectManager projectManager = ProjectManager.getInstance();

		createTestProject(oldProjectName);
		if (!projectManager.renameProject(newProjectName, context)) {
			fail("could not rename Project");
		}
		assertTrue("could not save project", TestUtils.saveProjectAndWait(this, projectManager.getCurrentProject()));

		File oldProjectFolder = new File(Constants.DEFAULT_ROOT + "/" + oldProjectName);
		File oldProjectFile = new File(Constants.DEFAULT_ROOT + "/" + oldProjectName + "/" + Constants.PROJECTCODE_NAME);

		File newProjectFolder = new File(Constants.DEFAULT_ROOT + "/" + newProjectName);
		File newProjectFile = new File(Constants.DEFAULT_ROOT + "/" + newProjectName + "/" + Constants.PROJECTCODE_NAME);

		String projectFileAsString = TestUtils.getProjectfileAsString(newProjectName);

		assertFalse("Old project folder is still existing", oldProjectFolder.exists());
		assertFalse("Old project file is still existing", oldProjectFile.exists());

		assertTrue("New project folder is not existing", newProjectFolder.exists());
		assertTrue("New project file is not existing", newProjectFile.exists());

		//this fails because catroid is buggy, fix catroid not this test --> we haven't decided yet how to fix the FileChecksumContainer
		assertFalse("old projectName still in project file", projectFileAsString.contains(oldProjectName));
	}

	public Project createTestProject(String projectName) throws IOException, InterruptedException {
		int xPosition = 457;
		int yPosition = 598;
		double size = 0.8;

		Project project = new Project(context, projectName);
		assertTrue("cannot save project", TestUtils.saveProjectAndWait(this, project));
		ProjectManager.getInstance().setProject(project);
		Sprite firstSprite = new Sprite("cat");
		Sprite secondSprite = new Sprite("dog");
		Sprite thirdSprite = new Sprite("horse");
		Sprite fourthSprite = new Sprite("pig");
		testScript = new StartScript(firstSprite);
		otherScript = new StartScript(secondSprite);
		HideBrick hideBrick = new HideBrick(firstSprite);
		ShowBrick showBrick = new ShowBrick(firstSprite);
		SetCostumeBrick costumeBrick = new SetCostumeBrick(firstSprite);
		File image = TestUtils.saveFileToProject(projectName, "image.png", at.tugraz.ist.catroid.test.R.raw.icon,
				context, 0);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(image.getName());
		costumeData.setCostumeName("name");
		costumeBrick.setCostume(costumeData);
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

		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(Utils.md5Checksum(image), image.getAbsolutePath());

		TestUtils.saveProjectAndWait(this, project);
		return project;
	}
}
