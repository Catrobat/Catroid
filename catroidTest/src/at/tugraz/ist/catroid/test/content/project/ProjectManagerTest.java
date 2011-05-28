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
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.util.Utils;

public class ProjectManagerTest extends InstrumentationTestCase {

	private static final String TAG = "ProjectManagerTest";
	String projectNameOne = "Ulumulu";
	String scriptNameOne = "Ulukai";
	String scriptNameTwo = "Ulukai2";
	String spriteNameOne = "Zuul";
	String spriteNameTwo = "Zuuul";

	@Override
	public void tearDown() {
		Utils.clearProject(projectNameOne);
		Utils.clearProject("oldProject");
		Utils.clearProject("newProject");
	}

	public void testBasicFunctions() throws NameNotFoundException {

		ProjectManager manager = ProjectManager.getInstance();
		assertNull("there is a current sprite set", manager.getCurrentSprite());
		assertNull("there is a current script set", manager.getCurrentScript());

		Context context = getInstrumentation().getContext().createPackageContext("at.tugraz.ist.catroid",
				Context.CONTEXT_IGNORE_SECURITY);
		manager.initializeNewProject(projectNameOne, context);
		assertNotNull("no current project set", manager.getCurrentProject());
		assertEquals("The Projectname is not " + projectNameOne, projectNameOne, manager.getCurrentProject().getName());

		Sprite sprite = new Sprite(spriteNameOne);
		manager.addSprite(sprite);
		manager.setCurrentSprite(sprite);

		assertNotNull("no current sprite set", manager.getCurrentSprite());
		assertEquals("The Spritename is not " + spriteNameOne, spriteNameOne, manager.getCurrentSprite().getName());

		Script script = new StartScript(scriptNameOne, sprite);
		manager.addScript(script);
		manager.setCurrentScript(script);

		assertNotNull("no current script set", manager.getCurrentScript());
		assertEquals("The Spritename is not " + scriptNameOne, scriptNameOne, manager.getCurrentScript().getName());

		//loadProject ----------------------------------------

		manager.loadProject(projectNameOne, context, false);
		assertNotNull("no current project set", manager.getCurrentProject());
		assertEquals("The Projectname is not " + projectNameOne, projectNameOne, manager.getCurrentProject().getName());
		assertNull("there is a current sprite set", manager.getCurrentSprite());
		assertNull("there is a current script set", manager.getCurrentScript());

		//resetProject ---------------------------------------

		manager.addSprite(sprite);
		manager.setCurrentSprite(sprite);
		manager.addScript(script);
		manager.setCurrentScript(script);

		manager.resetProject(context);

		assertNull("there is a current sprite set", manager.getCurrentSprite());
		assertNull("there is a current script set", manager.getCurrentScript());

		//addSprite

		Sprite sprite2 = new Sprite(spriteNameTwo);
		manager.addSprite(sprite2);
		assertTrue("Sprite not in current Project", manager.getCurrentProject().getSpriteList().contains(sprite2));

		//addScript

		manager.setCurrentSprite(sprite2);
		Script script2 = new StartScript(scriptNameTwo, sprite2);
		manager.addScript(script2);
		assertTrue("Script not in current Sprite", manager.getCurrentSprite().getScriptList().contains(script2));

		//addBrick

		manager.setCurrentScript(script2);
		SetCostumeBrick brick = new SetCostumeBrick(sprite2);
		manager.getCurrentScript().addBrick(brick);
		assertTrue("Brick not in current Script", manager.getCurrentScript().getBrickList().contains(brick));

		//move brick already tested

	}

	public void testRenameProject() throws NameNotFoundException, IOException {
		String oldProjectName = "oldProject";
		String newProjectName = "newProject";
		ProjectManager projectManager = ProjectManager.getInstance();

		createTestProject(oldProjectName);
		if (!projectManager.renameProject(newProjectName, getInstrumentation().getContext())) {
			fail("could not rename Project");
		}
		projectManager.saveProject(getInstrumentation().getContext());

		File oldProjectFolder = new File(Consts.DEFAULT_ROOT + "/" + oldProjectName);
		File oldProjectFile = new File(Consts.DEFAULT_ROOT + "/" + oldProjectName + "/" + oldProjectName
				+ Consts.PROJECT_EXTENTION);

		File newProjectFolder = new File(Consts.DEFAULT_ROOT + "/" + newProjectName);
		File newProjectFile = new File(Consts.DEFAULT_ROOT + "/" + newProjectName + "/" + newProjectName
				+ Consts.PROJECT_EXTENTION);

		String spfFileAsString = Utils.getProjectfileAsString(newProjectName);

		assertFalse("Old project folder is still existing", oldProjectFolder.exists());
		assertFalse("Old project file is still existing", oldProjectFile.exists());

		assertTrue("New project folder is not existing", newProjectFolder.exists());
		assertTrue("New project file is not existing", newProjectFile.exists());

		//this fails because catroid is buggy, fix catroid not this test --> we haven't decided yet how to fix the FileChecksumContainer
		Log.v(TAG, spfFileAsString);
		assertFalse("old projectName still in spf file", spfFileAsString.contains(oldProjectName));

	}

	public Project createTestProject(String projectName) throws IOException, NameNotFoundException {
		StorageHandler storageHandler = StorageHandler.getInstance();

		int xPosition = 457;
		int yPosition = 598;
		double scaleValue = 0.8;

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
		File image = Utils.saveFileToProject(projectName, "image.png", at.tugraz.ist.catroid.test.R.raw.icon,
				getInstrumentation().getContext(), 0);
		costumeBrick.setCostume(image.getName());
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

		ProjectManager.getInstance().fileChecksumContainer.addChecksum(
				StorageHandler.getInstance().getMD5Checksum(image),
				image.getAbsolutePath());

		storageHandler.saveProject(project);
		return project;
	}
}
