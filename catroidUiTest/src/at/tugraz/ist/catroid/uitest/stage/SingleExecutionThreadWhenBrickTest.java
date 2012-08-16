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
package at.tugraz.ist.catroid.uitest.stage;

import java.io.File;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.BroadcastScript;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.BroadcastWaitBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetGhostEffectBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class SingleExecutionThreadWhenBrickTest extends ActivityInstrumentationTestCase2<StageActivity> {
	private static final int SCREEN_WIDTH = 480;
	private static final int SCREEN_HEIGHT = 800;

	private Solo solo;
	private Project projectWhenBrick;
	Sprite yellowSprite;
	Sprite greenSprite;
	WhenScript yellowWhenScript;
	BroadcastScript greenBroadcastScript;
	String broadcastMessage = "broadcastMessage";

	public SingleExecutionThreadWhenBrickTest() {
		super(StageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProjectWhenBrick();
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testWaitBrickWhenTapped() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(500);
		for (int i = 1; i <= 10; ++i) {
			solo.sleep(100);
			assertEquals("Wrong executionBrickIndex.", 0, yellowWhenScript.getExecutingBrickIndex());
			assertEquals("Costume has wrong AlphaValue.", (float) 1.0, yellowSprite.costume.getAlphaValue());
			solo.clickOnScreen((SCREEN_WIDTH / 2), (SCREEN_HEIGHT / 2));
		}

		solo.sleep(100);
		assertEquals("Costume has wrong AlphaValue.", (float) 1.0, yellowSprite.costume.getAlphaValue());
		assertEquals("Wrong executionBrickIndex.", 0, yellowWhenScript.getExecutingBrickIndex());
		solo.sleep(2000);
		assertEquals("Costume has wrong AlphaValue.", (float) 0.5, yellowSprite.costume.getAlphaValue());
		assertEquals("Wrong executionBrickIndex.", 1, yellowWhenScript.getExecutingBrickIndex());
	}

	public void testWaitBrickBroadcast() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(500);
		for (int i = 1; i <= 10; ++i) {
			solo.sleep(1000);
			assertEquals("Wrong executionBrickIndex.", 0, greenBroadcastScript.getExecutingBrickIndex());
			assertEquals("Costume has wrong AlphaValue.", (float) 1.0, greenSprite.costume.getAlphaValue());
			solo.clickOnScreen((SCREEN_WIDTH / 2) + 100, (SCREEN_HEIGHT / 2));
		}
		solo.sleep(1000);
		assertEquals("Costume has wrong AlphaValue.", (float) 1.0, greenSprite.costume.getAlphaValue());
		assertEquals("Wrong executionBrickIndex.", 0, greenBroadcastScript.getExecutingBrickIndex());
		solo.sleep(2000);
		assertEquals("Costume has wrong AlphaValue.", (float) 0, greenSprite.costume.getAlphaValue());
		assertEquals("Wrong executionBrickIndex.", 1, greenBroadcastScript.getExecutingBrickIndex());
	}

	private void createProjectWhenBrick() {
		Values.SCREEN_HEIGHT = SCREEN_HEIGHT;
		Values.SCREEN_WIDTH = SCREEN_WIDTH;

		projectWhenBrick = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		// yellow Sprite
		yellowSprite = new Sprite("yellowSprite");

		StartScript yellowStartScript = new StartScript(yellowSprite);
		SetCostumeBrick yellowCostumeBrick = new SetCostumeBrick(yellowSprite);
		SetSizeToBrick yellowSetSizeToBrick = new SetSizeToBrick(yellowSprite, 200d);
		CostumeData yellowCostumeData = new CostumeData();
		String yellowImageName = "yellow_image.bmp";
		yellowCostumeData.setCostumeName(yellowImageName);
		yellowSprite.getCostumeDataList().add(yellowCostumeData);
		yellowCostumeBrick.setCostume(yellowCostumeData);
		yellowStartScript.addBrick(yellowCostumeBrick);
		yellowStartScript.addBrick(yellowSetSizeToBrick);
		yellowSprite.addScript(yellowStartScript);

		yellowWhenScript = new WhenScript(yellowSprite);
		WaitBrick yellowWaitBrick = new WaitBrick(yellowSprite, 2000);
		SetGhostEffectBrick yellowSetGhostEffectBrick = new SetGhostEffectBrick(yellowSprite, 50d);
		yellowWhenScript.addBrick(yellowWaitBrick);
		yellowWhenScript.addBrick(yellowSetGhostEffectBrick);
		yellowSprite.addScript(yellowWhenScript);

		// blue Sprite
		Sprite blueSprite = new Sprite("blueSprite");
		StartScript blueStartScript = new StartScript(blueSprite);
		SetCostumeBrick blueCostumeBrick = new SetCostumeBrick(blueSprite);
		SetSizeToBrick blueSetSizeToBrick = new SetSizeToBrick(blueSprite, 200d);
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(blueSprite);
		broadcastWaitBrick.setSelectedMessage(broadcastMessage);
		CostumeData blueCostumeData = new CostumeData();
		String blueImageName = "blue_image.bmp";

		blueCostumeData.setCostumeName(blueImageName);

		blueSprite.getCostumeDataList().add(blueCostumeData);

		blueCostumeBrick.setCostume(blueCostumeData);
		blueStartScript.addBrick(blueCostumeBrick);
		blueStartScript.addBrick(blueSetSizeToBrick);
		blueStartScript.addBrick(new PlaceAtBrick(blueSprite, 100, 0));

		blueSprite.addScript(blueStartScript);

		WhenScript blueWhenScript = new WhenScript(blueSprite);
		blueWhenScript.addBrick(broadcastWaitBrick);
		blueSprite.addScript(blueWhenScript);

		// green Sprite
		greenSprite = new Sprite("greenSprite");
		StartScript greenStartScript = new StartScript(greenSprite);
		SetCostumeBrick greenCostumeBrick = new SetCostumeBrick(greenSprite);
		SetSizeToBrick greenSetSizeToBrick = new SetSizeToBrick(greenSprite, 200d);
		CostumeData greenCostumeData = new CostumeData();
		String greenImageName = "green_image.bmp";

		greenCostumeData.setCostumeName(greenImageName);

		greenSprite.getCostumeDataList().add(greenCostumeData);

		greenCostumeBrick.setCostume(greenCostumeData);
		greenStartScript.addBrick(greenCostumeBrick);
		greenStartScript.addBrick(greenSetSizeToBrick);
		greenStartScript.addBrick(new PlaceAtBrick(greenSprite, -100, 0));

		greenSprite.addScript(greenStartScript);

		greenBroadcastScript = new BroadcastScript(greenSprite);
		WaitBrick waitBrick = new WaitBrick(greenSprite, 2000);

		SetGhostEffectBrick greenSetGhostEffectBrick2 = new SetGhostEffectBrick(greenSprite, 100d);
		greenBroadcastScript.setBroadcastMessage(broadcastMessage);
		greenBroadcastScript.addBrick(waitBrick);
		greenBroadcastScript.addBrick(greenSetGhostEffectBrick2);
		greenSprite.addScript(greenBroadcastScript);

		projectWhenBrick.addSprite(yellowSprite);

		projectWhenBrick.addSprite(blueSprite);

		projectWhenBrick.addSprite(greenSprite);

		StorageHandler.getInstance().saveProject(projectWhenBrick);

		File yellowImageFile = UiTestUtils.saveFileToProject(projectWhenBrick.getName(), yellowImageName,
				at.tugraz.ist.catroid.uitest.R.raw.yellow_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);

		File blueImageFile = UiTestUtils.saveFileToProject(projectWhenBrick.getName(), blueImageName,
				at.tugraz.ist.catroid.uitest.R.raw.blue_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);

		File greenImageFile = UiTestUtils.saveFileToProject(projectWhenBrick.getName(), greenImageName,
				at.tugraz.ist.catroid.uitest.R.raw.green_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);

		yellowCostumeData.setCostumeFilename(yellowImageFile.getName());

		blueCostumeData.setCostumeFilename(blueImageFile.getName());

		greenCostumeData.setCostumeFilename(greenImageFile.getName());
		StorageHandler.getInstance().saveProject(projectWhenBrick);
		ProjectManager.getInstance().setProject(projectWhenBrick);
	}
}
