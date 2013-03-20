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
package org.catrobat.catroid.uitest.stage;

import java.io.File;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.Values;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetGhostEffectBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class SingleExecutionWhenBrickTest extends ActivityInstrumentationTestCase2<StageActivity> {
	private static final int SCREEN_WIDTH = 480;
	private static final int SCREEN_HEIGHT = 800;

	private Solo solo;
	private Project projectWhenBrick;
	Sprite yellowSprite;
	Sprite greenSprite;
	WhenScript yellowWhenScript;
	BroadcastScript greenBroadcastScript;
	String broadcastMessage = "broadcastMessage";

	public SingleExecutionWhenBrickTest() {
		super(StageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		createProjectWhenBrick();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testWaitBrickWhenTapped() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(500);
		for (int i = 1; i <= 10; ++i) {
			solo.sleep(100);
			assertEquals("Look has wrong AlphaValue.", (float) 1.0, yellowSprite.look.getAlphaValue());
			solo.clickOnScreen((SCREEN_WIDTH / 2), (SCREEN_HEIGHT / 2));
		}
		solo.sleep(100);
		assertEquals("Look has wrong AlphaValue.", (float) 1.0, yellowSprite.look.getAlphaValue());
		solo.sleep(2000);
		assertEquals("Look has wrong AlphaValue.", (float) 0.5, yellowSprite.look.getAlphaValue());
	}

	public void testWaitBrickBroadcast() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(500);
		for (int i = 1; i <= 10; ++i) {
			solo.sleep(1000);
			assertEquals("Look has wrong AlphaValue.", (float) 1.0, greenSprite.look.getAlphaValue());
			solo.clickOnScreen((SCREEN_WIDTH / 2) + 100, (SCREEN_HEIGHT / 2));
		}
		solo.sleep(1000);
		assertEquals("Look has wrong AlphaValue.", (float) 1.0, greenSprite.look.getAlphaValue());
		solo.sleep(2000);
		assertEquals("Look has wrong AlphaValue.", (float) 0, greenSprite.look.getAlphaValue());
	}

	private void createProjectWhenBrick() {
		Values.SCREEN_HEIGHT = SCREEN_HEIGHT;
		Values.SCREEN_WIDTH = SCREEN_WIDTH;

		projectWhenBrick = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		// yellow Sprite
		yellowSprite = new Sprite("yellowSprite");

		StartScript yellowStartScript = new StartScript(yellowSprite);
		SetLookBrick yellowLookBrick = new SetLookBrick(yellowSprite);
		SetSizeToBrick yellowSetSizeToBrick = new SetSizeToBrick(yellowSprite, 200d);
		LookData yellowLookData = new LookData();
		String yellowImageName = "yellow_image.bmp";
		yellowLookData.setLookName(yellowImageName);
		yellowSprite.getLookDataList().add(yellowLookData);
		yellowLookBrick.setLook(yellowLookData);
		yellowStartScript.addBrick(yellowLookBrick);
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
		SetLookBrick blueLookBrick = new SetLookBrick(blueSprite);
		SetSizeToBrick blueSetSizeToBrick = new SetSizeToBrick(blueSprite, 200d);
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(blueSprite);
		broadcastWaitBrick.setSelectedMessage(broadcastMessage);
		LookData blueLookData = new LookData();
		String blueImageName = "blue_image.bmp";

		blueLookData.setLookName(blueImageName);

		blueSprite.getLookDataList().add(blueLookData);

		blueLookBrick.setLook(blueLookData);
		blueStartScript.addBrick(blueLookBrick);
		blueStartScript.addBrick(blueSetSizeToBrick);
		blueStartScript.addBrick(new PlaceAtBrick(blueSprite, 100, 0));

		blueSprite.addScript(blueStartScript);

		WhenScript blueWhenScript = new WhenScript(blueSprite);
		blueWhenScript.addBrick(broadcastWaitBrick);
		blueSprite.addScript(blueWhenScript);

		// green Sprite
		greenSprite = new Sprite("greenSprite");
		StartScript greenStartScript = new StartScript(greenSprite);
		SetLookBrick greenLookBrick = new SetLookBrick(greenSprite);
		SetSizeToBrick greenSetSizeToBrick = new SetSizeToBrick(greenSprite, 200d);
		LookData greenLookData = new LookData();
		String greenImageName = "green_image.bmp";

		greenLookData.setLookName(greenImageName);

		greenSprite.getLookDataList().add(greenLookData);

		greenLookBrick.setLook(greenLookData);
		greenStartScript.addBrick(greenLookBrick);
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
				org.catrobat.catroid.uitest.R.raw.yellow_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);

		File blueImageFile = UiTestUtils.saveFileToProject(projectWhenBrick.getName(), blueImageName,
				org.catrobat.catroid.uitest.R.raw.blue_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);

		File greenImageFile = UiTestUtils.saveFileToProject(projectWhenBrick.getName(), greenImageName,
				org.catrobat.catroid.uitest.R.raw.green_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);

		yellowLookData.setLookFilename(yellowImageFile.getName());

		blueLookData.setLookFilename(blueImageFile.getName());

		greenLookData.setLookFilename(greenImageFile.getName());
		StorageHandler.getInstance().saveProject(projectWhenBrick);
		ProjectManager.getInstance().setProject(projectWhenBrick);
	}
}
