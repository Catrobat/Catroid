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
package org.catrobat.catroid.test.io;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.catrobat.catroid.common.Constants.PROJECTCODE_NAME;
import static org.catrobat.catroid.common.Constants.PROJECTCODE_NAME_TMP;
import static org.catrobat.catroid.utils.Utils.buildProjectPath;

public class StorageHandlerTest extends AndroidTestCase {
	private final StorageHandler storageHandler;
	private final String projectName = TestUtils.DEFAULT_TEST_PROJECT_NAME;

	public StorageHandlerTest() throws IOException {
		storageHandler = StorageHandler.getInstance();
	}

	@Override
	public void setUp() {
		TestUtils.deleteTestProjects();
	}

	@Override
	public void tearDown() throws Exception {
		TestUtils.deleteTestProjects();
		super.tearDown();
	}

	public void testSerializeProject() {
		final int xPosition = 457;
		final int yPosition = 598;
		final float size = 0.8f;

		Project project = new Project(getContext(), projectName);
		Sprite firstSprite = new Sprite("first");
		Sprite secondSprite = new Sprite("second");
		Sprite thirdSprite = new Sprite("third");
		Sprite fourthSprite = new Sprite("fourth");
		Script testScript = new StartScript();
		Script otherScript = new StartScript();
		HideBrick hideBrick = new HideBrick();
		ShowBrick showBrick = new ShowBrick();
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

		storageHandler.saveProject(project);

		Project loadedProject = storageHandler.loadProject(projectName);

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
		Formula actualXPosition = ((FormulaBrick) postSpriteList.get(2).getScript(0).getBrickList().get(0))
				.getFormulaWithBrickField(Brick.BrickField.X_POSITION);
		Formula actualYPosition = ((FormulaBrick) postSpriteList.get(2).getScript(0).getBrickList().get(0))
				.getFormulaWithBrickField(Brick.BrickField.Y_POSITION);

		Formula actualSize = ((FormulaBrick) postSpriteList.get(1).getScript(0).getBrickList().get(2))
				.getFormulaWithBrickField(Brick.BrickField.SIZE);

		assertEquals("Size was not deserialized right", size, interpretFormula(actualSize, null));
		assertEquals("XPosition was not deserialized right", xPosition,
				interpretFormula(actualXPosition, null).intValue());
		assertEquals("YPosition was not deserialized right", yPosition,
                interpretFormula(actualYPosition, null).intValue());


		assertFalse("paused should not be set in script", preSpriteList.get(1).getScript(0).isPaused());

		// Test version codes and names
		//		final int preVersionCode = (Integer) TestUtils.getPrivateField("catroidVersionCode", project, false);
		//		final int postVersionCode = (Integer) TestUtils.getPrivateField("catroidVersionCode", loadedProject, false);
		//		assertEquals("Version codes are not equal", preVersionCode, postVersionCode);
		//
		//		final String preVersionName = (String) TestUtils.getPrivateField("catroidVersionName", project, false);
		//		final String postVersionName = (String) TestUtils.getPrivateField("catroidVersionName", loadedProject, false);
		//		assertEquals("Version names are not equal", preVersionName, postVersionName);
	}

	public void testDefaultProject() throws IOException {
		ProjectManager projectManager = ProjectManager.getInstance();
		projectManager.setProject(StandardProjectHandler.createAndSaveStandardProject(projectName, getContext()));

		// Test background
		assertEquals("not the right number of sprites in the default project", 5, projectManager.getCurrentProject()
				.getSpriteList().size());
		assertEquals("not the right number of scripts in the second sprite of default project", 2, projectManager
				.getCurrentProject().getSpriteList().get(1).getNumberOfScripts());
		assertEquals("not the right number of bricks in the first script of Stage", 3, projectManager
				.getCurrentProject().getSpriteList().get(0).getScript(0).getBrickList().size());

		//test if images are existing:
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		ArrayList<LookData> backgroundLookList = currentProject.getSpriteList().get(0).getLookDataList();
		assertEquals("no background picture or too many pictures in background sprite", 1, backgroundLookList.size());

		String imagePath = backgroundLookList.get(0).getAbsolutePath();
		File testFile = new File(imagePath);
		assertTrue("Image " + backgroundLookList.get(0).getLookFileName() + " does not exist", testFile.exists());

		// Test the 4 moles
		for (int i = 0; i < 4; i++) {
			assertEquals("not the right number of bricks in the first script", 12, projectManager.getCurrentProject()
					.getSpriteList().get(i + 1).getScript(0).getBrickList().size());
			assertEquals("not the right number of bricks in the second script", 4, projectManager.getCurrentProject()
					.getSpriteList().get(i + 1).getScript(1).getBrickList().size());

			//test if images are existing:
			ArrayList<LookData> catroidLookList = currentProject.getSpriteList().get(i + 1).getLookDataList();
			assertEquals("wrong number of pictures in catroid sprite", 3, catroidLookList.size());

			imagePath = catroidLookList.get(0).getAbsolutePath();
			testFile = new File(imagePath);
			assertTrue("Image " + catroidLookList.get(0).getLookFileName() + " does not exist", testFile.exists());

			imagePath = catroidLookList.get(1).getAbsolutePath();
			testFile = new File(imagePath);
			assertTrue("Image " + catroidLookList.get(1).getLookFileName() + " does not exist", testFile.exists());

			imagePath = catroidLookList.get(2).getAbsolutePath();
			testFile = new File(imagePath);
			assertTrue("Image " + catroidLookList.get(2).getLookFileName() + " does not exist", testFile.exists());
		}
	}

	public void testSanityCheck() throws IOException {
		final int xPosition = 457;
		final int yPosition = 598;
		final float size = 0.8f;

		final Project project = new Project(getContext(), projectName);
		Sprite firstSprite = new Sprite("first");
		Sprite secondSprite = new Sprite("second");
		Sprite thirdSprite = new Sprite("third");
		Sprite fourthSprite = new Sprite("fourth");
		Script testScript = new StartScript();
		Script otherScript = new StartScript();
		HideBrick hideBrick = new HideBrick();
		ShowBrick showBrick = new ShowBrick();
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


		File tmpCodeFile = new File(buildProjectPath(project.getName()), PROJECTCODE_NAME_TMP);
		File currentCodeFile = new File(buildProjectPath(project.getName()), PROJECTCODE_NAME);
		assertFalse(tmpCodeFile.getName() + " exists!", tmpCodeFile.exists());
		assertFalse(currentCodeFile.getName() + " exists!", currentCodeFile.exists());

		storageHandler.saveProject(project);

		assertTrue(currentCodeFile.getName() + " was not created!", currentCodeFile.exists());
		assertTrue(PROJECTCODE_NAME + " is empty!", currentCodeFile.length() > 0);

		// simulate 1st Option: tmp_code.xml exists but code.xml doesn't exist --> saveProject process will restore from tmp_code.xml
		if (!tmpCodeFile.createNewFile()) {
			fail("Could not create tmp file");
		}
		UtilFile.copyFile(tmpCodeFile, currentCodeFile);
		String currentCodeFileXml = Files.toString(currentCodeFile, Charsets.UTF_8);
		assertTrue("Could not delete " + currentCodeFile.getName(), currentCodeFile.delete());

		storageHandler.saveProject(project);

		assertTrue(currentCodeFile.getName() + " was not created!", currentCodeFile.exists());
		assertTrue(PROJECTCODE_NAME + " is empty!", currentCodeFile.length() > 0);
		assertTrue("Sanity Check Failed. New Code File is not equal with tmp file.", currentCodeFileXml.equals(Files.toString(currentCodeFile, Charsets.UTF_8)));

		// simulate 2nd Option: tmp_code.xml and code.xml exist --> saveProject process will discard tmp_code.xml and use code.xml
		if (!tmpCodeFile.createNewFile()) {
			fail("Could not create tmp file");
		}

		storageHandler.saveProject(project);

		assertFalse("Sanity Check Failed. tmp file was not discarded.", tmpCodeFile.exists());
	}

	// TODO: add XML header validation based on xsd

    private Float interpretFormula(Formula formula, Sprite sprite) {
        try {
            return formula.interpretFloat(sprite);
        } catch (InterpretationException interpretationException) {
            Log.d(getClass().getSimpleName(), "Formula interpretation for Formula failed.", interpretationException);
        }
        return Float.NaN;
    }

	//	public void testAliasesAndXmlHeader() {
	//
	//		String projectName = "myProject";
	//
	//		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);
	//		if (projectFile.exists()) {
	//			UtilFile.deleteDirectory(projectFile);
	//		}
	//
	//		Project project = new Project(getContext(), projectName);
	//		Sprite sprite = new Sprite("testSprite");
	//		Script startScript = new StartScript(sprite);
	//		Script whenScript = new WhenScript(sprite);
	//		sprite.addScript(startScript);
	//		sprite.addScript(whenScript);
	//		project.addSprite(sprite);
	//
	//		ArrayList<Brick> startScriptBrickList = new ArrayList<Brick>();
	//		ArrayList<Brick> whenScriptBrickList = new ArrayList<Brick>();
	//		startScriptBrickList.add(new ChangeXByNBrick(sprite, 4));
	//		startScriptBrickList.add(new ChangeYByNBrick(sprite, 5));
	//		startScriptBrickList.add(new ComeToFrontBrick(sprite));
	//		startScriptBrickList.add(new GoNStepsBackBrick(sprite, 5));
	//		startScriptBrickList.add(new HideBrick(sprite));
	//		startScriptBrickList.add(new WhenStartedBrick(sprite, startScript));
	//
	//		whenScriptBrickList.add(new PlaySoundBrick(sprite));
	//		whenScriptBrickList.add(new SetSizeToBrick(sprite, 50));
	//		whenScriptBrickList.add(new SetLookBrick(sprite));
	//		whenScriptBrickList.add(new SetXBrick(sprite, 50));
	//		whenScriptBrickList.add(new SetYBrick(sprite, 50));
	//		whenScriptBrickList.add(new ShowBrick(sprite));
	//		whenScriptBrickList.add(new WaitBrick(sprite, 1000));
	//
	//		for (Brick b : startScriptBrickList) {
	//			startScript.addBrick(b);
	//		}
	//		for (Brick b : whenScriptBrickList) {
	//			whenScript.addBrick(b);
	//		}
	//
	//		storageHandler.saveProject(project);
	//		String projectString = TestUtils.getProjectfileAsString(projectName);
	//		assertFalse("project contains package information", projectString.contains("org.catrobat"));
	//
	//		String xmlHeader = (String) Reflection.getPrivateField(XmlSerializer.class, "XML_HEADER");
	//		assertTrue("Project file did not contain correct XML header.", projectString.startsWith(xmlHeader));
	//
	//		projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);
	//		if (projectFile.exists()) {
	//			UtilFile.deleteDirectory(projectFile);
	//		}
	//	}
}
