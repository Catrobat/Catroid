/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetTransparencyBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;

public class SingleExecutionWhenBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private Project projectWhenBrick;
	Sprite yellowSprite;
	Sprite greenSprite;
	WhenScript yellowWhenScript;
	BroadcastScript greenBroadcastScript;
	String broadcastMessage = "broadcastMessage";

	public SingleExecutionWhenBrickTest() {
		super(MainMenuActivity.class);
	}

	public void testWaitBrickWhenTapped() {

		createProjectWhenBrick();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(500);

		for (int i = 1; i <= 10; ++i) {
			solo.sleep(100);
			assertEquals("Look has wrong AlphaValue.", 0f,
					yellowSprite.look.getTransparencyInUserInterfaceDimensionUnit());
			UiTestUtils.clickOnStageCoordinates(solo, 0, 0, ScreenValues.SCREEN_WIDTH, ScreenValues.SCREEN_HEIGHT);
		}
		solo.sleep(100);
		assertEquals("Look has wrong AlphaValue.", 0f, yellowSprite.look.getTransparencyInUserInterfaceDimensionUnit());
		checkAlphaWithTimeout(5000, yellowSprite, 50f);
	}

	public void testWaitBrickBroadcast() {

		createProjectWhenBrick();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(500);

		for (int i = 1; i <= 10; ++i) {
			solo.sleep(1000);
			assertEquals("Look has wrong AlphaValue.", 0f,
					greenSprite.look.getTransparencyInUserInterfaceDimensionUnit());
			UiTestUtils.clickOnStageCoordinates(solo, 100, 200, ScreenValues.SCREEN_WIDTH, ScreenValues
					.SCREEN_HEIGHT);
		}
		solo.sleep(1000);
		assertEquals("Look has wrong AlphaValue.", 0f, greenSprite.look.getTransparencyInUserInterfaceDimensionUnit());
		checkAlphaWithTimeout(5000, greenSprite, 100f);
	}

	public void testWaitBrickWhenStretched() {

		createProjectWhenBrick();
		ScreenValues.SCREEN_HEIGHT /= 2;
		ScreenValues.SCREEN_WIDTH /= 2;
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(500);

		for (int i = 1; i <= 10; ++i) {
			solo.sleep(1000);
			assertEquals("Look has wrong AlphaValue.", 0f,
					greenSprite.look.getTransparencyInUserInterfaceDimensionUnit());
			UiTestUtils.clickOnStageCoordinates(solo, 100, 200, ScreenValues.SCREEN_WIDTH * 2, ScreenValues
					.SCREEN_HEIGHT * 2);
		}
		solo.sleep(1000);
		assertEquals("Look has wrong AlphaValue.", 0f, greenSprite.look.getTransparencyInUserInterfaceDimensionUnit());
		checkAlphaWithTimeout(5000, greenSprite, 100f);
	}

	private void checkAlphaWithTimeout(int timeout, Sprite sprite, float value2) {
		int waitTime = 0;

		while (waitTime <= timeout) {
			if ((sprite.look.getTransparencyInUserInterfaceDimensionUnit() == value2)) {
				break;
			}
			solo.sleep(200);
			waitTime += 200;
		}
		assertEquals("Look has wrong AlphaValue.", value2, sprite.look.getTransparencyInUserInterfaceDimensionUnit());
	}

	private void createProjectWhenBrick() {

		projectWhenBrick = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		// yellow Sprite
		yellowSprite = new Sprite("yellowSprite");

		StartScript yellowStartScript = new StartScript();
		SetLookBrick yellowLookBrick = new SetLookBrick();
		SetSizeToBrick yellowSetSizeToBrick = new SetSizeToBrick(200d);
		LookData yellowLookData = new LookData();
		String yellowImageName = "yellow_image.bmp";
		yellowLookData.setLookName(yellowImageName);
		yellowSprite.getLookDataList().add(yellowLookData);
		yellowLookBrick.setLook(yellowLookData);
		yellowStartScript.addBrick(yellowLookBrick);
		yellowStartScript.addBrick(yellowSetSizeToBrick);
		yellowSprite.addScript(yellowStartScript);

		yellowWhenScript = new WhenScript();
		WaitBrick yellowWaitBrick = new WaitBrick(2000);
		SetTransparencyBrick yellowSetTransparencyBrick = new SetTransparencyBrick(50d);
		yellowWhenScript.addBrick(yellowWaitBrick);
		yellowWhenScript.addBrick(yellowSetTransparencyBrick);
		yellowSprite.addScript(yellowWhenScript);

		// blue Sprite
		Sprite blueSprite = new Sprite("blueSprite");
		StartScript blueStartScript = new StartScript();
		SetLookBrick blueLookBrick = new SetLookBrick();
		SetSizeToBrick blueSetSizeToBrick = new SetSizeToBrick(200d);
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(broadcastMessage);
		LookData blueLookData = new LookData();
		String blueImageName = "blue_image.bmp";

		blueLookData.setLookName(blueImageName);

		blueSprite.getLookDataList().add(blueLookData);

		blueLookBrick.setLook(blueLookData);
		blueStartScript.addBrick(blueLookBrick);
		blueStartScript.addBrick(blueSetSizeToBrick);
		blueStartScript.addBrick(new PlaceAtBrick(100, 200));

		blueSprite.addScript(blueStartScript);

		WhenScript blueWhenScript = new WhenScript();
		blueWhenScript.addBrick(broadcastWaitBrick);
		blueSprite.addScript(blueWhenScript);

		// green Sprite
		greenSprite = new Sprite("greenSprite");
		StartScript greenStartScript = new StartScript();
		SetLookBrick greenLookBrick = new SetLookBrick();
		SetSizeToBrick greenSetSizeToBrick = new SetSizeToBrick(200d);
		LookData greenLookData = new LookData();
		String greenImageName = "green_image.bmp";

		greenLookData.setLookName(greenImageName);

		greenSprite.getLookDataList().add(greenLookData);

		greenLookBrick.setLook(greenLookData);
		greenStartScript.addBrick(greenLookBrick);
		greenStartScript.addBrick(greenSetSizeToBrick);
		greenStartScript.addBrick(new PlaceAtBrick(-100, 200));

		greenSprite.addScript(greenStartScript);

		greenBroadcastScript = new BroadcastScript(broadcastMessage);
		WaitBrick waitBrick = new WaitBrick(2000);

		SetTransparencyBrick greenSetTransparencyBrick2 = new SetTransparencyBrick(100d);
		greenBroadcastScript.addBrick(waitBrick);
		greenBroadcastScript.addBrick(greenSetTransparencyBrick2);
		greenSprite.addScript(greenBroadcastScript);

		projectWhenBrick.addSprite(yellowSprite);

		projectWhenBrick.addSprite(blueSprite);

		projectWhenBrick.addSprite(greenSprite);

		StorageHandler.getInstance().saveProject(projectWhenBrick);

		File yellowImageFile = UiTestUtils.saveFileToProject(projectWhenBrick.getName(), yellowImageName,
				org.catrobat.catroid.test.R.raw.yellow_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);

		File blueImageFile = UiTestUtils.saveFileToProject(projectWhenBrick.getName(), blueImageName,
				org.catrobat.catroid.test.R.raw.blue_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);

		File greenImageFile = UiTestUtils.saveFileToProject(projectWhenBrick.getName(), greenImageName,
				org.catrobat.catroid.test.R.raw.green_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);

		yellowLookData.setLookFilename(yellowImageFile.getName());

		blueLookData.setLookFilename(blueImageFile.getName());

		greenLookData.setLookFilename(greenImageFile.getName());
		StorageHandler.getInstance().saveProject(projectWhenBrick);
		ProjectManager.getInstance().setProject(projectWhenBrick);
	}
}
