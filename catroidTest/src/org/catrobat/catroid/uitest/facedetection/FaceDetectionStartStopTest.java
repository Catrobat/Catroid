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
package org.catrobat.catroid.uitest.facedetection;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class FaceDetectionStartStopTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int SCREEN_WIDTH = 480;
	private static final int SCREEN_HEIGHT = 800;
	private static final int SLEEP_TIME = 1200;

	public FaceDetectionStartStopTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		SettingsActivity.setFaceDetectionSharedPreferenceEnabled(
				this.getInstrumentation().getTargetContext(), true);
		createProjectFaceDetection();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(SLEEP_TIME);
	}

	@Override
	protected void tearDown() throws Exception {
		SettingsActivity.setFaceDetectionSharedPreferenceEnabled(
				this.getInstrumentation().getTargetContext(), false);
		super.tearDown();
	}

	public void testGoingBack() {
		assertTrue("Face detection was not started", FaceDetectionHandler.isFaceDetectionRunning());

		solo.goBackToActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(SLEEP_TIME);
		assertFalse("Face detection should be stopped when leaving to main menu",
				FaceDetectionHandler.isFaceDetectionRunning());
		solo.sleep(SLEEP_TIME);
	}

	public void testStageMenu() throws Exception {
		assertTrue("Face detection was not started", FaceDetectionHandler.isFaceDetectionRunning());

		solo.sleep(SLEEP_TIME);
		solo.goBack();
		solo.sleep(SLEEP_TIME);
		assertFalse("Face detection should be paused in stage dialog", FaceDetectionHandler.isFaceDetectionRunning());
		solo.clickOnButton(solo.getString(R.string.stage_dialog_resume));
		solo.sleep(SLEEP_TIME);
		assertTrue("Face detection was not resumed when leaving stage dialog",
				FaceDetectionHandler.isFaceDetectionRunning());
		solo.sleep(SLEEP_TIME);
		solo.goBack();
		solo.sleep(SLEEP_TIME);
		assertFalse("Face detection should be paused in stage dialog", FaceDetectionHandler.isFaceDetectionRunning());
		solo.clickOnButton(solo.getString(R.string.stage_dialog_restart));
		solo.sleep(SLEEP_TIME);
		assertTrue("Face detection was not started when restarting stage",
				FaceDetectionHandler.isFaceDetectionRunning());

		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.stage_dialog_back));
		solo.sleep(SLEEP_TIME);
		assertFalse("Face detection is running when leaving stage", FaceDetectionHandler.isFaceDetectionRunning());
		solo.sleep(SLEEP_TIME);
	}

	private void createProjectFaceDetection() {
		ScreenValues.SCREEN_HEIGHT = SCREEN_HEIGHT;
		ScreenValues.SCREEN_WIDTH = SCREEN_WIDTH;

		Project projectFaceDetection = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("fdSprite");
		StartScript startScript = new StartScript();
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(new Formula(new FormulaElement(ElementType.SENSOR,
				Sensors.FACE_SIZE.name(), null)));
		startScript.addBrick(setSizeToBrick);
		sprite.addScript(startScript);
		projectFaceDetection.addSprite(sprite);

		ProjectManager.getInstance().setFileChecksumContainer(new FileChecksumContainer());
		ProjectManager.getInstance().setProject(projectFaceDetection);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(startScript);
		StorageHandler.getInstance().saveProject(projectFaceDetection);
	}
}
