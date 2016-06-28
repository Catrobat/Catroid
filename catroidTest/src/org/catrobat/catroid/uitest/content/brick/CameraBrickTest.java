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

package org.catrobat.catroid.uitest.content.brick;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.CameraBrick;
import org.catrobat.catroid.content.bricks.ChooseCameraBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class CameraBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private String videoOn;
	private String videoOff;
	private String frontCamera;
	private String backCamera;

	private Project project;

	public CameraBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		videoOn = solo.getString(R.string.video_brick_camera_on);
		videoOff = solo.getString(R.string.video_brick_camera_off);
		frontCamera = solo.getString(R.string.choose_camera_front);
		backCamera = solo.getString(R.string.choose_camera_back);
	}

	@Device
	public void testAddVideoBrickOn() {
		createProject();

		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		startStageActivity();

		solo.sleep(2000);

		assertTrue("Video not started", CameraManager.getInstance().getState()
				!= CameraManager.CameraState.notUsed && CameraManager.getInstance().getState()
				!= CameraManager.CameraState.stopped);

		assertTrue("Standarcamera must be the front camera", CameraManager.getInstance().isCurrentCameraFacingFront());
	}

	@Device
	public void testAddVideoBrickOff() {

		createProject();

		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		solo.clickOnText(videoOn);
		solo.clickOnText(videoOff);

		solo.sleep(2000);

		assertTrue(videoOff + " is not selected in Spinner", solo.searchText(videoOff));

		startStageActivity();

		solo.sleep(2000);

		assertTrue("Video not started", CameraManager.getInstance().getState()
				== CameraManager.CameraState.notUsed || CameraManager.getInstance().getState()
				== CameraManager.CameraState.stopped);

		assertTrue("Standarcamera must be the front camera", CameraManager.getInstance().isCurrentCameraFacingFront());
	}

	@Device
	public void testAddVideoBrickOnWithChosenFrontCamera() {
		createProjectWithChooseBrick();

		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		startStageActivity();

		solo.sleep(2000);

		assertTrue("Video not started", CameraManager.getInstance().getState()
				!= CameraManager.CameraState.notUsed && CameraManager.getInstance().getState()
				!= CameraManager.CameraState.stopped);

		assertTrue("chosen camera must be the front camera", CameraManager.getInstance().isCurrentCameraFacingFront());
	}

	@Device
	public void testAddVideoBrickOnWithChosenBackCamera() {
		createProjectWithChooseBrick();

		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		solo.clickOnText(frontCamera);
		solo.clickOnText(backCamera);

		solo.sleep(2000);

		assertTrue(videoOn + " is not selected in Spinner", solo.searchText(videoOn));

		startStageActivity();

		solo.sleep(2000);

		assertTrue("Video not started", CameraManager.getInstance().getState()
				!= CameraManager.CameraState.notUsed && CameraManager.getInstance().getState()
				!= CameraManager.CameraState.stopped);

		assertTrue("chosen camera must be the back camera", CameraManager.getInstance().isCurrentCameraFacingBack());
	}

	@Device
	public void testAddVideoBrickOffWithChosenBackCamera() {
		createProjectWithChooseBrick();

		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		solo.clickOnText(frontCamera);
		solo.clickOnText(backCamera);

		solo.clickOnText(videoOn);
		solo.clickOnText(videoOff);

		solo.sleep(2000);

		assertTrue(videoOff + " is not selected in Spinner", solo.searchText(videoOff));
		startStageActivity();

		solo.sleep(2000);

		assertTrue("Video not started", CameraManager.getInstance().getState()
				== CameraManager.CameraState.notUsed || CameraManager.getInstance().getState()
				== CameraManager.CameraState.stopped);

		assertTrue("chosen camera must be the back camera", CameraManager.getInstance().isCurrentCameraFacingBack());
	}

	@Device
	public void testComplexVideoBehaviour() {
		createComplexVideoTest();

		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		startStageActivity();

		solo.sleep(1000);

		assertTrue("Video not started", CameraManager.getInstance().getState()
				!= CameraManager.CameraState.notUsed && CameraManager.getInstance().getState()
				!= CameraManager.CameraState.stopped);

		assertTrue("chosen camera must be the front camera", CameraManager.getInstance().isCurrentCameraFacingFront());

		solo.sleep(2000);

		assertTrue("chosen camera must be the back camera", CameraManager.getInstance().isCurrentCameraFacingBack());

		solo.sleep(2000);

		assertTrue("Video not started", CameraManager.getInstance().getState()
				== CameraManager.CameraState.notUsed);

		solo.sleep(2000);

		assertTrue("Video not started", CameraManager.getInstance().getState()
				!= CameraManager.CameraState.notUsed && CameraManager.getInstance().getState()
				!= CameraManager.CameraState.stopped);

		solo.sleep(2000);

		assertTrue("chosen camera must be the front camera", CameraManager.getInstance().isCurrentCameraFacingFront());

		solo.goBack();
		solo.goBack();

		solo.sleep(1000);

		assertTrue("Video not started", CameraManager.getInstance().getState()
				== CameraManager.CameraState.notUsed);
	}

	private void createProject() {
		ProjectManager projectManager = ProjectManager.getInstance();
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("background");
		Script testScript = new StartScript();

		CameraBrick cameraBrick = new CameraBrick();
		testScript.addBrick(cameraBrick);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
	}

	private void createProjectWithChooseBrick() {
		ProjectManager projectManager = ProjectManager.getInstance();
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("background");
		Script testScript = new StartScript();

		ChooseCameraBrick chooseBrick = new ChooseCameraBrick();
		testScript.addBrick(chooseBrick);

		CameraBrick cameraBrick = new CameraBrick();
		testScript.addBrick(cameraBrick);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
	}

	private void createComplexVideoTest() {
		ProjectManager projectManager = ProjectManager.getInstance();
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("background");
		Script testScript = new StartScript();

		CameraBrick cameraBrick1 = new CameraBrick(1);
		testScript.addBrick(cameraBrick1);

		WaitBrick wBrick1 = new WaitBrick(2000);
		testScript.addBrick(wBrick1);

		ChooseCameraBrick chooseBrick1 = new ChooseCameraBrick(0);
		testScript.addBrick(chooseBrick1);

		WaitBrick wBrick2 = new WaitBrick(2000);
		testScript.addBrick(wBrick2);

		CameraBrick cameraBrick2 = new CameraBrick(0);
		testScript.addBrick(cameraBrick2);

		WaitBrick wBrick3 = new WaitBrick(2000);
		testScript.addBrick(wBrick3);

		CameraBrick cameraBrick3 = new CameraBrick(1);
		testScript.addBrick(cameraBrick3);

		WaitBrick wBrick4 = new WaitBrick(2000);
		testScript.addBrick(wBrick4);

		ChooseCameraBrick chooseBrick2 = new ChooseCameraBrick(1);
		testScript.addBrick(chooseBrick2);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
	}

	private void startStageActivity() {
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);

		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(1000);
	}
}
