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
import java.util.Arrays;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class TouchAxisTest extends ActivityInstrumentationTestCase2<StageActivity> {
	private Solo solo;

	public TouchAxisTest() {
		super(StageActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;

	}

	// This prevents regression of https://github.com/Catrobat/Catroid/issues/3
	public void testYAxis() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);

		solo.clickOnScreen(ScreenValues.SCREEN_WIDTH / 2, 100);
		solo.sleep(500);

		byte[] blackPixel = { (byte) 0, (byte) 0, (byte) 0, (byte) 255 };
		byte[] screenPixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH / 2, 100, 1, 1);

		assertTrue("Pixels didn't match! Touch area is off!", Arrays.equals(blackPixel, screenPixel));
	}

	private void createProject() {
		ScreenValues.SCREEN_HEIGHT = 800;
		ScreenValues.SCREEN_WIDTH = 480;

		Project testProject = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		final String alphaTestImageName = "alpha_test_image.png";

		Sprite touchSprite = new Sprite("touchSprite");
		Script startScript = new StartScript(touchSprite);
		SetLookBrick setAlphaLookBrick = new SetLookBrick(touchSprite);

		LookData touchLookData = new LookData();
		touchLookData.setLookName(alphaTestImageName);
		touchSprite.getLookDataList().add(touchLookData);

		setAlphaLookBrick.setLook(touchLookData);

		startScript.addBrick(setAlphaLookBrick);
		touchSprite.addScript(startScript);

		WhenScript touchWhenScript = new WhenScript(touchSprite);
		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(touchSprite, 180.0);
		touchWhenScript.addBrick(turnLeftBrick);

		touchSprite.addScript(touchWhenScript);

		testProject.addSprite(touchSprite);

		StorageHandler.getInstance().saveProject(testProject);

		File alphaTestImage = UiTestUtils.saveFileToProject(testProject.getName(), alphaTestImageName,
				org.catrobat.catroid.uitest.R.raw.alpha_test_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		touchLookData.setLookFilename(alphaTestImage.getName());

		StorageHandler.getInstance().saveProject(testProject);
		ProjectManager.getInstance().setProject(testProject);
	}
}
