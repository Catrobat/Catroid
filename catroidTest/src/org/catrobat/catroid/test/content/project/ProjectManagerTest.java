/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.project;

import java.io.File;
import java.io.IOException;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.Utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.InstrumentationTestCase;

public class ProjectManagerTest extends InstrumentationTestCase {

	private String projectNameOne = "Ulumulu";
	private String spriteNameOne = "Zuul";
	private String spriteNameTwo = "Zuuul";

	private Script testScript;
	private Script otherScript;

	@Override
	public void tearDown() throws Exception {
		TestUtils.clearProject(projectNameOne);
		TestUtils.clearProject("oldProject");
		TestUtils.clearProject("newProject");
		super.tearDown();
	}

	public void testBasicFunctions() throws NameNotFoundException, IOException {
		ProjectManager projectManager = ProjectManager.getInstance();
		assertNull("there is a current sprite set", projectManager.getCurrentSprite());
		assertNull("there is a current script set", projectManager.getCurrentScript());
		Context context = getInstrumentation().getContext().createPackageContext("org.catrobat.catroid",
				Context.CONTEXT_IGNORE_SECURITY);

		// initializeNewProject
		projectManager.initializeNewProject(projectNameOne, context);
		assertNotNull("no current project set", projectManager.getCurrentProject());
		assertEquals("The Projectname is not " + projectNameOne, projectNameOne, projectManager.getCurrentProject()
				.getName());

		// verify that new project is default project (see StorageHandler.createDefaultProject)
		int spriteCount = projectManager.getCurrentProject().getSpriteList().size();
		assertEquals("New project has wrong number of sprites", 5, spriteCount);
		Sprite catroid = projectManager.getCurrentProject().getSpriteList().get(1);
		assertEquals("Catroid sprite has wrong number of scripts", 2, catroid.getNumberOfScripts());

		// add sprite
		Sprite sprite = new Sprite(spriteNameOne);
		projectManager.addSprite(sprite);
		projectManager.setCurrentSprite(sprite);

		assertNotNull("No current sprite set", projectManager.getCurrentSprite());
		assertEquals("The Spritename is not " + spriteNameOne, spriteNameOne, projectManager.getCurrentSprite()
				.getName());

		// add script
		Script startScript = new StartScript(sprite);
		projectManager.addScript(startScript);
		projectManager.setCurrentScript(startScript);

		assertNotNull("no current script set", projectManager.getCurrentScript());

		// loadProject
		projectManager.loadProject(projectNameOne, context, false);
		assertNotNull("no current project set", projectManager.getCurrentProject());
		assertEquals("The Projectname is not " + projectNameOne, projectNameOne, projectManager.getCurrentProject()
				.getName());
		assertNull("there is a current sprite set", projectManager.getCurrentSprite());
		assertNull("there is a current script set", projectManager.getCurrentScript());

		// addSprite
		Sprite sprite2 = new Sprite(spriteNameTwo);
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
		SetLookBrick setLookBrick = new SetLookBrick(sprite2);
		projectManager.getCurrentScript().addBrick(setLookBrick);
		assertTrue("Brick not in current Script",
				projectManager.getCurrentScript().getBrickList().contains(setLookBrick));
	}

	public void testRenameProject() throws IOException {
		String oldProjectName = "oldProject";
		String newProjectName = "newProject";
		ProjectManager projectManager = ProjectManager.getInstance();

		Project project = createTestProject(oldProjectName);
		if (!projectManager.renameProject(newProjectName, getInstrumentation().getContext())) {
			fail("could not rename Project");
		}
		StorageHandler.getInstance().saveProject(project);

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

	public Project createTestProject(String projectName) throws IOException {
		StorageHandler storageHandler = StorageHandler.getInstance();

		int xPosition = 457;
		int yPosition = 598;
		double size = 0.8;

		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		storageHandler.saveProject(project);
		ProjectManager.getInstance().setProject(project);
		Sprite firstSprite = new Sprite("cat");
		Sprite secondSprite = new Sprite("dog");
		Sprite thirdSprite = new Sprite("horse");
		Sprite fourthSprite = new Sprite("pig");
		testScript = new StartScript(firstSprite);
		otherScript = new StartScript(secondSprite);
		HideBrick hideBrick = new HideBrick(firstSprite);
		ShowBrick showBrick = new ShowBrick(firstSprite);
		SetLookBrick lookBrick = new SetLookBrick(firstSprite);
		File image = TestUtils.saveFileToProject(projectName, "image.png", org.catrobat.catroid.test.R.raw.icon,
				getInstrumentation().getContext(), 0);
		LookData lookData = new LookData();
		lookData.setLookFilename(image.getName());
		lookData.setLookName("name");
		lookBrick.setLook(lookData);
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

		storageHandler.saveProject(project);
		return project;
	}
}
