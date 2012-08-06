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
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.SetBrightnessBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetGhostEffectBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.TurnLeftBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ComplexStageTest extends ActivityInstrumentationTestCase2<StageActivity> {
	private final int screenWidth = 480;
	private final int screenHeight = 800;

	private Solo solo;
	private Project project;

	public ComplexStageTest() {
		super(StageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
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

	public void testShowTexture() {
		byte[] redPixel = { (byte) 237, 28, 36, (byte) 255 };
		byte[] redBrightnessPixel = { (byte) 109, 0, 0, (byte) 255 };
		byte[] greenPixel = { 34, (byte) 177, 76, (byte) 255 };
		byte[] yellowPixel = { (byte) 255, (byte) 242, 0, (byte) 255 };
		byte[] bluePixel = { 0, (byte) 162, (byte) 232, (byte) 255 };
		byte[] whitePixel = { (byte) 255, (byte) 255, (byte) 255, (byte) 255 };
		byte[] blackPixel = { (byte) 0, (byte) 0, (byte) 0, (byte) 255 };
		byte[] blackBrightnessPixel = { (byte) 127, (byte) 127, (byte) 127, (byte) 255 };

		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(500);
		byte[] screenArray = StageActivity.stageListener.getPixels(0, 0, screenWidth, screenHeight);

		UiTestUtils.comparePixelArrayWithPixelScreenArray(redPixel, screenArray, -41, -41, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(redPixel, screenArray, -41, -2, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(redPixel, screenArray, -2, -41, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(redPixel, screenArray, -2, -2, screenWidth, screenHeight);

		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 1, -2, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 40, -2, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 1, -41, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 40, -41, screenWidth, screenHeight);

		UiTestUtils.comparePixelArrayWithPixelScreenArray(yellowPixel, screenArray, -21, 21, screenWidth, screenHeight);

		UiTestUtils.comparePixelArrayWithPixelScreenArray(whitePixel, screenArray, 0, 0, screenWidth, screenHeight);

		UiTestUtils.comparePixelArrayWithPixelScreenArray(blackPixel, screenArray, -80, -80, screenWidth, screenHeight);

		solo.clickOnScreen((screenWidth / 2) + 21, (screenHeight / 2) - 21);
		solo.sleep(300);
		screenArray = StageActivity.stageListener.getPixels(0, 0, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(bluePixel, screenArray, 21, 21, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(bluePixel, screenArray, 0, 0, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(bluePixel, screenArray, 21 - 40, 21 - 40, screenWidth,
				screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(redPixel, screenArray, 21 - 41, 21 - 41, screenWidth,
				screenHeight);

		solo.clickOnScreen((screenWidth / 2) - 21, (screenHeight / 2) - 21);
		solo.sleep(300);
		screenArray = StageActivity.stageListener.getPixels(0, 0, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(whitePixel, screenArray, -31, 21, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(bluePixel, screenArray, 21, 21, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(redPixel, screenArray, -41, -41, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 40, -41, screenWidth, screenHeight);

		solo.clickOnScreen((screenWidth / 2) + 21, (screenHeight / 2) + 21);
		solo.sleep(300);
		screenArray = StageActivity.stageListener.getPixels(0, 0, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 1, -2, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 40, -2, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 1, -41, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 40, -41, screenWidth, screenHeight);

		solo.clickOnScreen((screenWidth / 2) - 21, (screenHeight / 2) + 21);
		solo.sleep(300);
		screenArray = StageActivity.stageListener.getPixels(0, 0, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(redBrightnessPixel, screenArray, -21, -21, screenWidth,
				screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(redBrightnessPixel, screenArray, -21, -21 + 27, screenWidth,
				screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 1, -2, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 40, -2, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 1, -41, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(greenPixel, screenArray, 40, -41, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(bluePixel, screenArray, 21, 21, screenWidth, screenHeight);

		solo.clickOnScreen((screenWidth / 2) - 50, (screenHeight / 2) - 50);
		solo.sleep(300);
		screenArray = StageActivity.stageListener.getPixels(0, 0, screenWidth, screenHeight);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(blackBrightnessPixel, screenArray, -54, 55, screenWidth,
				screenHeight);
		assertTrue("Just for FileTest", true);
	}

	private void createProject() {
		Values.SCREEN_HEIGHT = screenHeight;
		Values.SCREEN_WIDTH = screenWidth;

		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		// yellow Sprite
		Sprite yellowSprite = new Sprite("yellowSprite");
		StartScript yellowStartScript = new StartScript(yellowSprite);
		SetCostumeBrick yellowCostumeBrick = new SetCostumeBrick(yellowSprite);
		CostumeData yellowCostumeData = new CostumeData();
		String yellowImageName = "yellow_image.bmp";

		yellowCostumeData.setCostumeName(yellowImageName);

		yellowSprite.getCostumeDataList().add(yellowCostumeData);

		yellowCostumeBrick.setCostume(yellowCostumeData);
		yellowStartScript.addBrick(yellowCostumeBrick);
		yellowStartScript.addBrick(new PlaceAtBrick(yellowSprite, -21, 21));

		yellowSprite.addScript(yellowStartScript);

		WhenScript yellowWhenScript = new WhenScript(yellowSprite);
		SetGhostEffectBrick yellowSetGhostEffectBrick = new SetGhostEffectBrick(yellowSprite, 100d);
		yellowWhenScript.addBrick(yellowSetGhostEffectBrick);

		yellowSprite.addScript(yellowWhenScript);

		// blue Sprite
		Sprite blueSprite = new Sprite("blueSprite");
		StartScript blueStartScript = new StartScript(blueSprite);
		SetCostumeBrick blueCostumeBrick = new SetCostumeBrick(blueSprite);
		CostumeData blueCostumeData = new CostumeData();
		String blueImageName = "blue_image.bmp";

		blueCostumeData.setCostumeName(blueImageName);

		blueSprite.getCostumeDataList().add(blueCostumeData);

		blueCostumeBrick.setCostume(blueCostumeData);
		blueStartScript.addBrick(blueCostumeBrick);
		blueStartScript.addBrick(new PlaceAtBrick(blueSprite, 21, 21));

		blueSprite.addScript(blueStartScript);

		WhenScript blueWhenScript = new WhenScript(blueSprite);
		SetSizeToBrick blueSetSizeToBrick = new SetSizeToBrick(blueSprite, 200d);
		blueWhenScript.addBrick(blueSetSizeToBrick);

		blueSprite.addScript(blueWhenScript);

		// green Sprite
		Sprite greenSprite = new Sprite("greenSprite");
		StartScript greenStartScript = new StartScript(greenSprite);
		SetCostumeBrick greenCostumeBrick = new SetCostumeBrick(greenSprite);
		CostumeData greenCostumeData = new CostumeData();
		String greenImageName = "green_image.bmp";

		greenCostumeData.setCostumeName(greenImageName);

		greenSprite.getCostumeDataList().add(greenCostumeData);

		greenCostumeBrick.setCostume(greenCostumeData);
		greenStartScript.addBrick(greenCostumeBrick);
		greenStartScript.addBrick(new PlaceAtBrick(greenSprite, 21, -21));

		greenSprite.addScript(greenStartScript);

		WhenScript greenWhenScript = new WhenScript(greenSprite);
		ComeToFrontBrick greenComeToFrontBrick = new ComeToFrontBrick(greenSprite);
		greenWhenScript.addBrick(greenComeToFrontBrick);

		greenSprite.addScript(greenWhenScript);

		// red Sprite
		Sprite redSprite = new Sprite("redSprite");
		StartScript redStartScript = new StartScript(redSprite);
		SetCostumeBrick redCostumeBrick = new SetCostumeBrick(redSprite);
		CostumeData redCostumeData = new CostumeData();
		String redImageName = "red_image.bmp";

		redCostumeData.setCostumeName(redImageName);

		redSprite.getCostumeDataList().add(redCostumeData);

		redCostumeBrick.setCostume(redCostumeData);
		redStartScript.addBrick(redCostumeBrick);
		redStartScript.addBrick(new PlaceAtBrick(redSprite, -21, -21));

		redSprite.addScript(redStartScript);

		WhenScript redWhenScript = new WhenScript(redSprite);
		ComeToFrontBrick redComeToFrontBrick = new ComeToFrontBrick(redSprite);
		SetBrightnessBrick redSetBrightnessBrick = new SetBrightnessBrick(redSprite, 50d);
		TurnLeftBrick redTurnLeftBrick = new TurnLeftBrick(redSprite, 45d);
		redWhenScript.addBrick(redComeToFrontBrick);
		redWhenScript.addBrick(redSetBrightnessBrick);
		redWhenScript.addBrick(redTurnLeftBrick);

		redSprite.addScript(redWhenScript);

		// black Sprite
		Sprite blackSprite = new Sprite("blackSprite");
		StartScript blackStartScript = new StartScript(blackSprite);
		SetCostumeBrick blackCostumeBrick = new SetCostumeBrick(blackSprite);
		CostumeData blackCostumeData = new CostumeData();
		String blackImageName = "black_image.bmp";

		blackCostumeData.setCostumeName(blackImageName);

		blackSprite.getCostumeDataList().add(blackCostumeData);

		blackCostumeBrick.setCostume(blackCostumeData);
		blackStartScript.addBrick(blackCostumeBrick);
		blackStartScript.addBrick(new PlaceAtBrick(blackSprite, -50, 50));

		blackSprite.addScript(blackStartScript);

		WhenScript blackWhenScript = new WhenScript(blackSprite);
		ComeToFrontBrick blackComeToFrontBrick = new ComeToFrontBrick(blackSprite);
		SetBrightnessBrick blackSetBrightnessBrick = new SetBrightnessBrick(blackSprite, 150d);
		blackWhenScript.addBrick(blackComeToFrontBrick);
		blackWhenScript.addBrick(blackSetBrightnessBrick);

		blackSprite.addScript(blackWhenScript);

		project.addSprite(blackSprite);
		project.addSprite(yellowSprite);
		project.addSprite(redSprite);
		project.addSprite(greenSprite);
		project.addSprite(blueSprite);

		StorageHandler.getInstance().saveProject(project);

		File yellowImageFile = UiTestUtils.saveFileToProject(project.getName(), yellowImageName,
				at.tugraz.ist.catroid.uitest.R.raw.yellow_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		File greenImageFile = UiTestUtils.saveFileToProject(project.getName(), greenImageName,
				at.tugraz.ist.catroid.uitest.R.raw.green_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		File blueImageFile = UiTestUtils.saveFileToProject(project.getName(), blueImageName,
				at.tugraz.ist.catroid.uitest.R.raw.blue_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		File redImageFile = UiTestUtils.saveFileToProject(project.getName(), redImageName,
				at.tugraz.ist.catroid.uitest.R.raw.red_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		File blackImageFile = UiTestUtils.saveFileToProject(project.getName(), blackImageName,
				at.tugraz.ist.catroid.uitest.R.raw.black_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		yellowCostumeData.setCostumeFilename(yellowImageFile.getName());
		greenCostumeData.setCostumeFilename(greenImageFile.getName());
		blueCostumeData.setCostumeFilename(blueImageFile.getName());
		redCostumeData.setCostumeFilename(redImageFile.getName());
		blackCostumeData.setCostumeFilename(blackImageFile.getName());

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);
	}
}
