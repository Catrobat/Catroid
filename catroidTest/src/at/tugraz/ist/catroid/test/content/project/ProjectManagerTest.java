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
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.constructionSite.content.ProjectValuesManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.UtilFile;

public class ProjectManagerTest extends AndroidTestCase {

	String projectNameOne = "Ulumulu";
	String scriptNameOne = "Ulukai";
	String scriptNameTwo = "Ulukai2";
	String spriteNameOne = "Zuul";
	String spriteNameTwo = "Zuuul";
	private ProjectValuesManager projectValuesManager = ProjectManager.getInstance().getProjectValuesManager();

	@Override
	public void tearDown() {
		File directory = new File(Consts.DEFAULT_ROOT + "/" + projectNameOne);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}
		File oldProjectFolder = new File(Consts.DEFAULT_ROOT + "/" + "oldProject");
		File newProjectFolder = new File(Consts.DEFAULT_ROOT + "/" + "newProject");

		if (newProjectFolder.exists()) {
			UtilFile.deleteDirectory(newProjectFolder);
		}

		if (oldProjectFolder.exists()) {
			UtilFile.deleteDirectory(oldProjectFolder);
		}
	}

	public void testBasicFunctions() throws NameNotFoundException {

		ProjectManager manager = ProjectManager.getInstance();
		assertNull("there is a current sprite set", projectValuesManager.getCurrentSprite());
		assertNull("there is a current script set", projectValuesManager.getCurrentScript());

		Context context = getContext().createPackageContext("at.tugraz.ist.catroid", Context.CONTEXT_IGNORE_SECURITY);
		manager.initializeNewProject(projectNameOne, context);
		assertNotNull("no current project set", manager.getCurrentProject());
		assertEquals("The Projectname is not " + projectNameOne, projectNameOne, manager.getCurrentProject().getName());

		Sprite sprite = new Sprite(spriteNameOne);
		manager.addSprite(sprite);
		projectValuesManager.setCurrentSprite(sprite);

		assertNotNull("no current sprite set", projectValuesManager.getCurrentSprite());
		assertEquals("The Spritename is not " + spriteNameOne, spriteNameOne, projectValuesManager.getCurrentSprite()
				.getName());

		Script script = new Script(scriptNameOne, sprite);
		manager.addScript(script);
		projectValuesManager.setCurrentScript(script);

		assertNotNull("no current script set", projectValuesManager.getCurrentScript());
		assertEquals("The Spritename is not " + scriptNameOne, scriptNameOne, projectValuesManager.getCurrentScript()
				.getName());

		//loadProject ----------------------------------------

		manager.loadProject(projectNameOne, context, false);
		assertNotNull("no current project set", manager.getCurrentProject());
		assertEquals("The Projectname is not " + projectNameOne, projectNameOne, manager.getCurrentProject().getName());
		assertNull("there is a current sprite set", projectValuesManager.getCurrentSprite());
		assertNull("there is a current script set", projectValuesManager.getCurrentScript());

		//resetProject ---------------------------------------

		manager.addSprite(sprite);
		projectValuesManager.setCurrentSprite(sprite);
		manager.addScript(script);
		projectValuesManager.setCurrentScript(script);

		manager.resetProject(context);

		assertNull("there is a current sprite set", projectValuesManager.getCurrentSprite());
		assertNull("there is a current script set", projectValuesManager.getCurrentScript());

		//addSprite

		Sprite sprite2 = new Sprite(spriteNameTwo);
		manager.addSprite(sprite2);
		assertTrue("Sprite not in current Project", projectValuesManager.getSpriteList().contains(sprite2));

		//addScript

		projectValuesManager.setCurrentSprite(sprite2);
		Script script2 = new Script(scriptNameTwo, sprite2);
		manager.addScript(script2);
		assertTrue("Script not in current Sprite",
				projectValuesManager.getCurrentSprite().getScriptList().contains(script2));

		//addBrick

		projectValuesManager.setCurrentScript(script2);
		SetCostumeBrick brick = new SetCostumeBrick(sprite2);
		manager.addBrick(brick);
		assertTrue("Brick not in current Script", projectValuesManager.getCurrentScript().getBrickList()
				.contains(brick));

		//move brick already tested

	}

	public void testRenameProject() throws NameNotFoundException, IOException, InterruptedException {
		String oldProjectName = "oldProject";
		String newProjectName = "newProject";
		ProjectManager projectManager = ProjectManager.getInstance();

		projectManager.setProject(createTestProject(oldProjectName));
		projectManager.renameProject(newProjectName, getContext());

		File oldProjectFolder = new File(Consts.DEFAULT_ROOT + "/" + oldProjectName);
		File oldProjectFile = new File(Consts.DEFAULT_ROOT + "/" + oldProjectName + "/" + oldProjectName
				+ Consts.PROJECT_EXTENTION);

		File newProjectFolder = new File(Consts.DEFAULT_ROOT + "/" + newProjectName);
		File newProjectFile = new File(Consts.DEFAULT_ROOT + "/" + newProjectName + "/" + newProjectName
				+ Consts.PROJECT_EXTENTION);

		assertFalse("Old project folder is still existing", oldProjectFolder.exists());
		assertFalse("Old project file is still existing", oldProjectFile.exists());

		assertTrue("New project folder is not existing", newProjectFolder.exists());
		assertTrue("New project file is not existing", newProjectFile.exists());

	}

	public Project createTestProject(String projectName) throws IOException, NameNotFoundException {
		StorageHandler storageHandler = StorageHandler.getInstance();

		int xPosition = 457;
		int yPosition = 598;
		double scaleValue = 0.8;

		Project project = new Project(getContext(), projectName);
		Sprite firstSprite = new Sprite("cat");
		Sprite secondSprite = new Sprite("dog");
		Sprite thirdSprite = new Sprite("horse");
		Sprite fourthSprite = new Sprite("pig");
		Script testScript = new Script("testScript", firstSprite);
		Script otherScript = new Script("otherScript", secondSprite);
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
		return project;
	}
}
