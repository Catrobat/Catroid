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
import java.util.Arrays;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.TurnLeftBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class TouchAxisTest extends ActivityInstrumentationTestCase2<StageActivity> {
	private Solo solo;

	public TouchAxisTest() {
		super(StageActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	// This prevents regression of https://github.com/Catrobat/Catroid/issues/3
	public void testYAxis() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(500);

		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, 100);

		byte[] blackPixel = { (byte) 0, (byte) 0, (byte) 0, (byte) 255 };
		byte[] screenPixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH / 2, 100, 1, 1);

		assertTrue("Pixels didn't match! Touch area is off!", Arrays.equals(blackPixel, screenPixel));
	}

	private void createProject() {
		Values.SCREEN_HEIGHT = 800;
		Values.SCREEN_WIDTH = 480;

		Project testProject = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		final String alphaTestImageName = "alpha_test_image.png";

		Sprite touchSprite = new Sprite("touchSprite");
		Script startScript = new StartScript(touchSprite);
		SetCostumeBrick setAlphaCostumeBrick = new SetCostumeBrick(touchSprite);

		CostumeData touchCostumeData = new CostumeData();
		touchCostumeData.setCostumeName(alphaTestImageName);
		touchSprite.getCostumeDataList().add(touchCostumeData);

		setAlphaCostumeBrick.setCostume(touchCostumeData);

		startScript.addBrick(setAlphaCostumeBrick);
		touchSprite.addScript(startScript);

		WhenScript touchWhenScript = new WhenScript(touchSprite);
		TurnLeftBrick turnLeftBrick = new TurnLeftBrick(touchSprite, 180.0);
		touchWhenScript.addBrick(turnLeftBrick);

		touchSprite.addScript(touchWhenScript);

		testProject.addSprite(touchSprite);

		StorageHandler.getInstance().saveProject(testProject);

		File alphaTestImage = UiTestUtils.saveFileToProject(testProject.getName(), alphaTestImageName,
				at.tugraz.ist.catroid.uitest.R.raw.alpha_test_image, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		touchCostumeData.setCostumeFilename(alphaTestImage.getName());

		StorageHandler.getInstance().saveProject(testProject);
		ProjectManager.getInstance().setProject(testProject);
	}
}
