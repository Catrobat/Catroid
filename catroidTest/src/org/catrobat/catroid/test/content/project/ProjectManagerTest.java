/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.project;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.InstrumentationTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProjectManagerTest extends InstrumentationTestCase {

	private String projectNameOne = "Ulumulu";
	private String spriteNameOne = "Zuul";
	private String spriteNameTwo = "Zuuul";

	private Script testScript;
	private Script otherScript;

	@Override
	public void setUp() throws Exception {
		TestUtils.clearProject(projectNameOne);
		TestUtils.clearProject("oldProject");
		TestUtils.clearProject("newProject");
		super.setUp();
	}

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
		Context context = getInstrumentation().getContext().createPackageContext(
				getInstrumentation().getTargetContext().getPackageName(), Context.CONTEXT_IGNORE_SECURITY);

		projectManager.initializeNewProject(projectNameOne, context, false, false);
		assertNotNull("no current project set", projectManager.getCurrentProject());
		assertEquals("The Projectname is not " + projectNameOne, projectNameOne, projectManager.getCurrentProject()
				.getName());

		int spriteCount = projectManager.getCurrentProject().getSpriteList().size();
		assertEquals("New project has wrong number of sprites", 5, spriteCount);
		Sprite catroid = projectManager.getCurrentProject().getSpriteList().get(1);
		assertEquals("Catroid sprite has wrong number of scripts", 2, catroid.getNumberOfScripts());

		Sprite sprite = new Sprite(spriteNameOne);
		projectManager.addSprite(sprite);
		projectManager.setCurrentSprite(sprite);

		assertNotNull("No current sprite set", projectManager.getCurrentSprite());
		assertEquals("The Spritename is not " + spriteNameOne, spriteNameOne, projectManager.getCurrentSprite()
				.getName());

		Script startScript = new StartScript();
		projectManager.addScript(startScript);
		projectManager.setCurrentScript(startScript);

		assertNotNull("no current script set", projectManager.getCurrentScript());

		try {
			ProjectManager.getInstance().loadProject(projectNameOne, context);
			assertTrue("Load project worked correctly", true);
		} catch (ProjectException projectException) {
			fail("Project is not loaded successfully");
		}
		assertNotNull("no current project set", projectManager.getCurrentProject());
		assertEquals("The Projectname is not " + projectNameOne, projectNameOne, projectManager.getCurrentProject()
				.getName());
		assertNull("there is a current sprite set", projectManager.getCurrentSprite());
		assertNull("there is a current script set", projectManager.getCurrentScript());

		Sprite sprite2 = new Sprite(spriteNameTwo);
		projectManager.addSprite(sprite2);
		assertTrue("Sprite not in current Project", projectManager.getCurrentProject().getSpriteList()
				.contains(sprite2));

		projectManager.setCurrentSprite(sprite2);
		Script script2 = new StartScript();
		projectManager.addScript(script2);
		assertTrue("Script not in current Sprite", projectManager.getCurrentSprite().getScriptIndex(script2) != -1);

		projectManager.setCurrentScript(script2);
		SetLookBrick setLookBrick = new SetLookBrick();
		projectManager.getCurrentScript().addBrick(setLookBrick);
		assertTrue("Brick not in current Script",
				projectManager.getCurrentScript().getBrickList().contains(setLookBrick));
	}

	public void testEmptyProject() throws NameNotFoundException, IOException {
		ProjectManager projectManager = ProjectManager.getInstance();
		Context context = getInstrumentation().getContext().createPackageContext(
				getInstrumentation().getTargetContext().getPackageName(), Context.CONTEXT_IGNORE_SECURITY);

		projectManager.initializeNewProject(projectNameOne, context, true, false);
		Project currentProject = projectManager.getCurrentProject();
		assertNotNull("no current project set", currentProject);

		assertEquals("Wrong project name", projectNameOne, currentProject.getName());
		assertEquals("Wrong number of sprites", 1, currentProject.getSpriteList().size());

		DataContainer variablesContainer = currentProject.getDataContainer();

		@SuppressWarnings("unchecked")
		List<UserVariable> userVariableList = (List<UserVariable>) Reflection.getPrivateField(
				DataContainer.class, variablesContainer, "projectVariables");
		@SuppressWarnings("unchecked")
		Map<Sprite, List<UserVariable>> spriteVariablesMap = (Map<Sprite, List<UserVariable>>) Reflection
				.getPrivateField(DataContainer.class, variablesContainer, "spriteVariables");

		assertEquals("Wrong number of variables", 0, userVariableList.size());
		assertEquals("Wrong number of variables", 0, spriteVariablesMap.size());

		Sprite background = currentProject.getSpriteList().get(0);
		assertEquals("Wrong sprite name", context.getString(R.string.background), background.getName());
		assertEquals("Script list not empty", 0, background.getNumberOfScripts());
		assertEquals("Brick list not empty", 0, background.getNumberOfBricks());
		assertEquals("Look data not empty", 0, background.getLookDataList().size());
		assertEquals("Sound list not empty", 0, background.getSoundList().size());
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

	public void testNestingBrickReferences() throws Throwable {
		ProjectManager projectManager = ProjectManager.getInstance();
		TestUtils.createTestProjectWithWrongIfClauseReferences();

		projectManager.checkNestingBrickReferences(true);

		List<Brick> newBrickList = projectManager.getCurrentProject().getSpriteList().get(0).getScript(0)
				.getBrickList();

		assertEquals("Wrong reference", newBrickList.get(2), ((IfLogicBeginBrick) newBrickList.get(0)).getIfElseBrick());
		assertEquals("Wrong reference", newBrickList.get(9), ((IfLogicBeginBrick) newBrickList.get(0)).getIfEndBrick());

		assertEquals("Wrong reference", newBrickList.get(0), ((IfLogicElseBrick) newBrickList.get(2)).getIfBeginBrick());
		assertEquals("Wrong reference", newBrickList.get(9), ((IfLogicElseBrick) newBrickList.get(2)).getIfEndBrick());

		assertEquals("Wrong reference", newBrickList.get(6), ((IfLogicBeginBrick) newBrickList.get(4)).getIfElseBrick());
		assertEquals("Wrong reference", newBrickList.get(8), ((IfLogicBeginBrick) newBrickList.get(4)).getIfEndBrick());

		assertEquals("Wrong reference", newBrickList.get(4), ((IfLogicElseBrick) newBrickList.get(6)).getIfBeginBrick());
		assertEquals("Wrong reference", newBrickList.get(8), ((IfLogicElseBrick) newBrickList.get(6)).getIfEndBrick());

		assertEquals("Wrong reference", newBrickList.get(4), ((IfLogicEndBrick) newBrickList.get(8)).getIfBeginBrick());
		assertEquals("Wrong reference", newBrickList.get(6), ((IfLogicEndBrick) newBrickList.get(8)).getIfElseBrick());

		assertEquals("Wrong reference", newBrickList.get(0), ((IfLogicEndBrick) newBrickList.get(9)).getIfBeginBrick());
		assertEquals("Wrong reference", newBrickList.get(2), ((IfLogicEndBrick) newBrickList.get(9)).getIfElseBrick());
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
		testScript = new StartScript();
		otherScript = new StartScript();
		HideBrick hideBrick = new HideBrick();
		ShowBrick showBrick = new ShowBrick();
		SetLookBrick lookBrick = new SetLookBrick();
		File image = TestUtils.saveFileToProject(projectName, "image.png", org.catrobat.catroid.test.R.raw.icon,
				getInstrumentation().getContext(), 0);
		LookData lookData = new LookData();
		lookData.setLookFilename(image.getName());
		lookData.setLookName("name");
		lookBrick.setLook(lookData);
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(size);
		ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick();
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(xPosition, yPosition);

		testScript.addBrick(hideBrick);
		testScript.addBrick(showBrick);
		testScript.addBrick(setSizeToBrick);
		testScript.addBrick(comeToFrontBrick);

		otherScript.addBrick(placeAtBrick);
		otherScript.setPaused(true);

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
