/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
package org.catrobat.catroid.uitest.stage;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.conditional.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.conditional.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.conditional.SetGhostEffectBrick;
import org.catrobat.catroid.content.bricks.conditional.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.conditional.TurnLeftBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;

public class StageTestComplex extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int SCREEN_WIDTH = 480;
	private static final int SCREEN_HEIGHT = 800;

	private static final byte[] RED_PIXEL = { (byte) 237, 28, 36, (byte) 255 };
	private static final byte[] RED_BRIGHTNESS_PIXEL = { (byte) 109, 0, 0, (byte) 255 };
	private static final byte[] GREEN_PIXEL = { 34, (byte) 177, 76, (byte) 255 };
	private static final byte[] YELLOW_PIXEL = { (byte) 255, (byte) 242, 0, (byte) 255 };
	private static final byte[] BLUE_PIXEL = { 0, (byte) 162, (byte) 232, (byte) 255 };
	private static final byte[] WHITE_PIXEL = { (byte) 255, (byte) 255, (byte) 255, (byte) 255 };
	private static final byte[] BLACK_PIXEL = { (byte) 0, (byte) 0, (byte) 0, (byte) 255 };
	private static final byte[] BLACK_BRIGHTNESS_PIXEL = { (byte) -124, (byte) -124, (byte) -124, (byte) 255 };

	private Project project;

	public StageTestComplex() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
	}

	@Device
	public void testShowTexture() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(1400);
		byte[] screenArray = StageActivity.stageListener.getPixels(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		UiTestUtils.comparePixelArrayWithPixelScreenArrayWithTolerance(WHITE_PIXEL, screenArray, 0, 0, SCREEN_WIDTH,
				SCREEN_HEIGHT, 0);

		UiTestUtils
				.comparePixelArrayWithPixelScreenArray(RED_PIXEL, screenArray, -41, -41, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(RED_PIXEL, screenArray, -41, -2, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(RED_PIXEL, screenArray, -2, -41, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(RED_PIXEL, screenArray, -2, -2, SCREEN_WIDTH, SCREEN_HEIGHT);

		UiTestUtils.comparePixelArrayWithPixelScreenArray(GREEN_PIXEL, screenArray, 1, -2, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils
				.comparePixelArrayWithPixelScreenArray(GREEN_PIXEL, screenArray, 40, -2, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils
				.comparePixelArrayWithPixelScreenArray(GREEN_PIXEL, screenArray, 1, -41, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(GREEN_PIXEL, screenArray, 40, -41, SCREEN_WIDTH,
				SCREEN_HEIGHT);

		UiTestUtils.comparePixelArrayWithPixelScreenArray(YELLOW_PIXEL, screenArray, -21, 21, SCREEN_WIDTH,
				SCREEN_HEIGHT);

		UiTestUtils.comparePixelArrayWithPixelScreenArray(WHITE_PIXEL, screenArray, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		UiTestUtils.comparePixelArrayWithPixelScreenArray(BLACK_PIXEL, screenArray, -54, 55, SCREEN_WIDTH,
				SCREEN_HEIGHT);

		solo.clickOnScreen((SCREEN_WIDTH / 2) + 21, (SCREEN_HEIGHT / 2) - 21);
		solo.sleep(300);
		screenArray = StageActivity.stageListener.getPixels(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(BLUE_PIXEL, screenArray, 21, 21, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(BLUE_PIXEL, screenArray, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(BLUE_PIXEL, screenArray, 21 - 40, 21 - 40, SCREEN_WIDTH,
				SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(RED_PIXEL, screenArray, 21 - 41, 21 - 41, SCREEN_WIDTH,
				SCREEN_HEIGHT);

		solo.clickOnScreen((SCREEN_WIDTH / 2) - 21, (SCREEN_HEIGHT / 2) - 21);
		solo.sleep(300);
		screenArray = StageActivity.stageListener.getPixels(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(WHITE_PIXEL, screenArray, -31, 21, SCREEN_WIDTH,
				SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(BLUE_PIXEL, screenArray, 21, 21, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils
				.comparePixelArrayWithPixelScreenArray(RED_PIXEL, screenArray, -41, -41, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(GREEN_PIXEL, screenArray, 40, -41, SCREEN_WIDTH,
				SCREEN_HEIGHT);

		solo.clickOnScreen((SCREEN_WIDTH / 2) + 21, (SCREEN_HEIGHT / 2) + 21);
		solo.sleep(300);
		screenArray = StageActivity.stageListener.getPixels(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(GREEN_PIXEL, screenArray, 1, -2, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils
				.comparePixelArrayWithPixelScreenArray(GREEN_PIXEL, screenArray, 40, -2, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils
				.comparePixelArrayWithPixelScreenArray(GREEN_PIXEL, screenArray, 1, -41, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(GREEN_PIXEL, screenArray, 40, -41, SCREEN_WIDTH,
				SCREEN_HEIGHT);

		solo.clickOnScreen((SCREEN_WIDTH / 2) - 21, (SCREEN_HEIGHT / 2) + 21);
		solo.sleep(300);
		screenArray = StageActivity.stageListener.getPixels(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(RED_BRIGHTNESS_PIXEL, screenArray, -21, -21, SCREEN_WIDTH,
				SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(RED_BRIGHTNESS_PIXEL, screenArray, -21, -21 + 27,
				SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(GREEN_PIXEL, screenArray, 1, -2, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils
				.comparePixelArrayWithPixelScreenArray(GREEN_PIXEL, screenArray, 40, -2, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils
				.comparePixelArrayWithPixelScreenArray(GREEN_PIXEL, screenArray, 1, -41, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(GREEN_PIXEL, screenArray, 40, -41, SCREEN_WIDTH,
				SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(BLUE_PIXEL, screenArray, 21, 21, SCREEN_WIDTH, SCREEN_HEIGHT);

		solo.clickOnScreen((SCREEN_WIDTH / 2) - 50, (SCREEN_HEIGHT / 2) - 50);
		solo.sleep(300);
		screenArray = StageActivity.stageListener.getPixels(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(BLACK_BRIGHTNESS_PIXEL, screenArray, -54, 55, SCREEN_WIDTH,
				SCREEN_HEIGHT);

	}

	public void testBehaviourWithoutBricks() {
		Project project = ProjectManager.getInstance().getCurrentProject();
		assertNotNull("current project was null", project);

		Sprite blueSprite = project.getSpriteList().get(4);
		while (blueSprite.getNumberOfScripts() > 0) {
			blueSprite.removeScript(blueSprite.getScript(0));
		}

		assertEquals("there shouldn't be any script left", 0, blueSprite.getNumberOfScripts());
		assertEquals("there shouldn't be any script left", 0, blueSprite.getNumberOfBricks());
		StorageHandler.getInstance().loadProject(project.getName());

		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(1400);

		byte[] screenArray = StageActivity.stageListener.getPixels(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(BLUE_PIXEL, screenArray, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(BLUE_PIXEL, screenArray, -19, -19, SCREEN_WIDTH,
				SCREEN_HEIGHT);
		UiTestUtils
				.comparePixelArrayWithPixelScreenArray(BLUE_PIXEL, screenArray, -19, 19, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils
				.comparePixelArrayWithPixelScreenArray(BLUE_PIXEL, screenArray, 19, -19, SCREEN_WIDTH, SCREEN_HEIGHT);
		UiTestUtils.comparePixelArrayWithPixelScreenArray(BLUE_PIXEL, screenArray, 19, 19, SCREEN_WIDTH, SCREEN_HEIGHT);

		solo.sleep(2000);
	}

	private void createProject() {
		ScreenValues.SCREEN_HEIGHT = SCREEN_HEIGHT;
		ScreenValues.SCREEN_WIDTH = SCREEN_WIDTH;

		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		// yellow Sprite
		Sprite yellowSprite = new Sprite("yellowSprite");
		StartScript yellowStartScript = new StartScript();
		LookData yellowLookData = new LookData();
		String yellowImageName = "yellow_image.bmp";

		yellowLookData.setLookName(yellowImageName);

		yellowSprite.getLookDataList().add(yellowLookData);

		yellowStartScript.addBrick(new PlaceAtBrick(-21, 21));

		yellowSprite.addScript(yellowStartScript);

		WhenScript yellowWhenScript = new WhenScript();
		SetGhostEffectBrick yellowSetGhostEffectBrick = new SetGhostEffectBrick(100d);
		yellowWhenScript.addBrick(yellowSetGhostEffectBrick);

		yellowSprite.addScript(yellowWhenScript);

		// blue Sprite
		Sprite blueSprite = new Sprite("blueSprite");
		StartScript blueStartScript = new StartScript();
		LookData blueLookData = new LookData();
		String blueImageName = "blue_image.bmp";

		blueLookData.setLookName(blueImageName);

		blueSprite.getLookDataList().add(blueLookData);

		blueStartScript.addBrick(new PlaceAtBrick(21, 21));

		blueSprite.addScript(blueStartScript);

		WhenScript blueWhenScript = new WhenScript();
		SetSizeToBrick blueSetSizeToBrick = new SetSizeToBrick(200d);
		blueWhenScript.addBrick(blueSetSizeToBrick);

		blueSprite.addScript(blueWhenScript);

		// green Sprite
		Sprite greenSprite = new Sprite("greenSprite");
		StartScript greenStartScript = new StartScript();
		LookData greenLookData = new LookData();
		String greenImageName = "green_image.bmp";

		greenLookData.setLookName(greenImageName);

		greenSprite.getLookDataList().add(greenLookData);

		greenStartScript.addBrick(new PlaceAtBrick(21, -21));

		greenSprite.addScript(greenStartScript);

		WhenScript greenWhenScript = new WhenScript();
		ComeToFrontBrick greenComeToFrontBrick = new ComeToFrontBrick();
		greenWhenScript.addBrick(greenComeToFrontBrick);

		greenSprite.addScript(greenWhenScript);

		// red Sprite
		Sprite redSprite = new Sprite("redSprite");
		StartScript redStartScript = new StartScript();
		LookData redLookData = new LookData();
		String redImageName = "red_image.bmp";

		redLookData.setLookName(redImageName);

		redSprite.getLookDataList().add(redLookData);

		redStartScript.addBrick(new PlaceAtBrick(-21, -21));

		redSprite.addScript(redStartScript);

		WhenScript redWhenScript = new WhenScript();
		ComeToFrontBrick redComeToFrontBrick = new ComeToFrontBrick();
		SetBrightnessBrick redSetBrightnessBrick = new SetBrightnessBrick(50d);
		TurnLeftBrick redTurnLeftBrick = new TurnLeftBrick(45d);
		redWhenScript.addBrick(redComeToFrontBrick);
		redWhenScript.addBrick(redSetBrightnessBrick);
		redWhenScript.addBrick(redTurnLeftBrick);

		redSprite.addScript(redWhenScript);

		// black Sprite
		Sprite blackSprite = new Sprite("blackSprite");
		StartScript blackStartScript = new StartScript();
		LookData blackLookData = new LookData();
		String blackImageName = "black_image.bmp";

		blackLookData.setLookName(blackImageName);

		blackSprite.getLookDataList().add(blackLookData);

		blackStartScript.addBrick(new PlaceAtBrick(-50, 50));

		blackSprite.addScript(blackStartScript);

		WhenScript blackWhenScript = new WhenScript();
		ComeToFrontBrick blackComeToFrontBrick = new ComeToFrontBrick();
		SetBrightnessBrick blackSetBrightnessBrick = new SetBrightnessBrick(150d);
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
				org.catrobat.catroid.test.R.raw.yellow_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		File greenImageFile = UiTestUtils.saveFileToProject(project.getName(), greenImageName,
				org.catrobat.catroid.test.R.raw.green_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		File blueImageFile = UiTestUtils.saveFileToProject(project.getName(), blueImageName,
				org.catrobat.catroid.test.R.raw.blue_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		File redImageFile = UiTestUtils.saveFileToProject(project.getName(), redImageName,
				org.catrobat.catroid.test.R.raw.red_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		File blackImageFile = UiTestUtils.saveFileToProject(project.getName(), blackImageName,
				org.catrobat.catroid.test.R.raw.black_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		yellowLookData.setLookFilename(yellowImageFile.getName());
		greenLookData.setLookFilename(greenImageFile.getName());
		blueLookData.setLookFilename(blueImageFile.getName());
		redLookData.setLookFilename(redImageFile.getName());
		blackLookData.setLookFilename(blackImageFile.getName());

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);
	}
}
