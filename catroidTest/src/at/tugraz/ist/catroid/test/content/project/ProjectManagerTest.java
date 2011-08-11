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
package at.tugraz.ist.catroid.test.content.project;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.InstrumentationTestCase;
import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
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
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.Utils;

public class ProjectManagerTest extends InstrumentationTestCase {

	private static final String TAG = "ProjectManagerTest";
	String projectNameOne = "Ulumulu";
	String scriptNameOne = "Ulukai";
	String scriptNameTwo = "Ulukai2";
	String spriteNameOne = "Zuul";
	String spriteNameTwo = "Zuuul";

	@Override
	public void tearDown() {
		TestUtils.clearProject(projectNameOne);
		TestUtils.clearProject("oldProject");
		TestUtils.clearProject("newProject");
	}

	public void testBasicFunctions() throws NameNotFoundException, IOException {

		ProjectManager projectManager = ProjectManager.getInstance();
		assertNull("there is a current sprite set", projectManager.getCurrentSprite());
		assertNull("there is a current script set", projectManager.getCurrentScript());

		Context context = getInstrumentation().getContext().createPackageContext("at.tugraz.ist.catroid",
				Context.CONTEXT_IGNORE_SECURITY);
		projectManager.initializeNewProject(projectNameOne, context);
		assertNotNull("no current project set", projectManager.getCurrentProject());
		assertEquals("The Projectname is not " + projectNameOne, projectNameOne, projectManager.getCurrentProject()
				.getName());

		Sprite sprite = new Sprite(spriteNameOne);
		projectManager.addSprite(sprite);
		projectManager.setCurrentSprite(sprite);

		assertNotNull("no current sprite set", projectManager.getCurrentSprite());
		assertEquals("The Spritename is not " + spriteNameOne, spriteNameOne, projectManager.getCurrentSprite()
				.getName());

		Script startScript = new StartScript(scriptNameOne, sprite);
		projectManager.addScript(startScript);
		projectManager.setCurrentScript(startScript);

		assertNotNull("no current script set", projectManager.getCurrentScript());
		assertEquals("The Spritename is not " + scriptNameOne, scriptNameOne, projectManager.getCurrentScript()
				.getName());

		//loadProject ----------------------------------------

		projectManager.loadProject(projectNameOne, context, false);
		assertNotNull("no current project set", projectManager.getCurrentProject());
		assertEquals("The Projectname is not " + projectNameOne, projectNameOne, projectManager.getCurrentProject()
				.getName());
		assertNull("there is a current sprite set", projectManager.getCurrentSprite());
		assertNull("there is a current script set", projectManager.getCurrentScript());

		//addSprite

		Sprite sprite2 = new Sprite(spriteNameTwo);
		projectManager.addSprite(sprite2);
		assertTrue("Sprite not in current Project", projectManager.getCurrentProject().getSpriteList()
				.contains(sprite2));

		//addScript

		projectManager.setCurrentSprite(sprite2);
		Script script2 = new StartScript(scriptNameTwo, sprite2);
		projectManager.addScript(script2);
		assertTrue("Script not in current Sprite", projectManager.getCurrentSprite().getScriptIndex(script2) != -1);

		//addBrick

		projectManager.setCurrentScript(script2);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite2);
		projectManager.getCurrentScript().addBrick(setCostumeBrick);
		assertTrue("Brick not in current Script",
				projectManager.getCurrentScript().getBrickList().contains(setCostumeBrick));
	}

	public void testRenameProject() throws IOException {
		String oldProjectName = "oldProject";
		String newProjectName = "newProject";
		ProjectManager projectManager = ProjectManager.getInstance();

		createTestProject(oldProjectName);
		if (!projectManager.renameProject(newProjectName, getInstrumentation().getContext())) {
			fail("could not rename Project");
		}
		projectManager.saveProject();

		File oldProjectFolder = new File(Consts.DEFAULT_ROOT + "/" + oldProjectName);
		File oldProjectFile = new File(Consts.DEFAULT_ROOT + "/" + oldProjectName + "/" + oldProjectName
				+ Consts.PROJECT_EXTENTION);

		File newProjectFolder = new File(Consts.DEFAULT_ROOT + "/" + newProjectName);
		File newProjectFile = new File(Consts.DEFAULT_ROOT + "/" + newProjectName + "/" + newProjectName
				+ Consts.PROJECT_EXTENTION);

		String projectFileAsString = TestUtils.getProjectfileAsString(newProjectName);

		assertFalse("Old project folder is still existing", oldProjectFolder.exists());
		assertFalse("Old project file is still existing", oldProjectFile.exists());

		assertTrue("New project folder is not existing", newProjectFolder.exists());
		assertTrue("New project file is not existing", newProjectFile.exists());

		//this fails because catroid is buggy, fix catroid not this test --> we haven't decided yet how to fix the FileChecksumContainer
		Log.v(TAG, projectFileAsString);
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
		Script testScript = new StartScript("testScript", firstSprite);
		Script otherScript = new StartScript("otherScript", secondSprite);
		HideBrick hideBrick = new HideBrick(firstSprite);
		ShowBrick showBrick = new ShowBrick(firstSprite);
		SetCostumeBrick costumeBrick = new SetCostumeBrick(firstSprite);
		File image = TestUtils.saveFileToProject(projectName, "image.png", at.tugraz.ist.catroid.test.R.raw.icon,
				getInstrumentation().getContext(), 0);
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

		ProjectManager.getInstance().fileChecksumContainer.addChecksum(Utils.md5Checksum(image),
				image.getAbsolutePath());

		storageHandler.saveProject(project);
		return project;
	}
}
