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

import android.annotation.TargetApi;
import android.os.Build;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
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

public class FaceDetectionResourcesTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int SCREEN_WIDTH = 480;
	private static final int SCREEN_HEIGHT = 800;
	private static final int SLEEP_TIME = 300;

	private Project project;
	Sprite sprite;

	public FaceDetectionResourcesTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		SettingsActivity.setFaceDetectionSharedPreferenceEnabled(
				this.getInstrumentation().getTargetContext(), true);
	}

	@Override
	protected void tearDown() throws Exception {
		SettingsActivity.setFaceDetectionSharedPreferenceEnabled(
				this.getInstrumentation().getTargetContext(), false);
		super.tearDown();
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void testResourceNeeded() throws Exception {
		createProject(true);
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(SLEEP_TIME);
		assertTrue("Face detection was not started although it is needed as a resource",
				FaceDetectionHandler.isFaceDetectionRunning());
		solo.goBackToActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(SLEEP_TIME);
		assertFalse("Face detection was not stopped", FaceDetectionHandler.isFaceDetectionRunning());
		solo.goBackToActivity(MainMenuActivity.class.getSimpleName());
		solo.waitForActivity(StageActivity.class.getSimpleName());
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void testResourceNotNeeded() throws Exception {
		createProject(false);
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(SLEEP_TIME);
		assertFalse("Face detection was started although it is not needed as a resource",
				FaceDetectionHandler.isFaceDetectionRunning());
		solo.goBackToActivity(MainMenuActivity.class.getSimpleName());
		solo.waitForActivity(StageActivity.class.getSimpleName());
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void testResourceChanged() throws Exception {
		createProject(true);
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(SLEEP_TIME * 5);
		assertTrue("Face detection was not started although it is needed as a resource",
				FaceDetectionHandler.isFaceDetectionRunning());
		solo.goBackToActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(SLEEP_TIME * 5);
		assertFalse("Face detection was not stopped", FaceDetectionHandler.isFaceDetectionRunning());
		createProject(false);
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(SLEEP_TIME * 5);
		assertFalse("Face detection was resumed although it is not needed anymore"
						+ " (if testResourceNotNeeded succeeds: FaceDetectionHandler.reset might be missing)",
				FaceDetectionHandler.isFaceDetectionRunning());
		solo.goBackToActivity(MainMenuActivity.class.getSimpleName());
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(SLEEP_TIME);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void createProject(boolean faceDetection) {
		ScreenValues.SCREEN_HEIGHT = SCREEN_HEIGHT;
		ScreenValues.SCREEN_WIDTH = SCREEN_WIDTH;

		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		sprite = new Sprite("aSprite");

		StartScript startScript = new StartScript();
		FormulaElement faceDetectionFormulaElement = new FormulaElement(ElementType.SENSOR, Sensors.FACE_SIZE.name(),
				null);
		FormulaElement nonFaceDetectionFormulaElement = new FormulaElement(ElementType.NUMBER, "42", null);
		MoveNStepsBrick moveBrick = new MoveNStepsBrick(new Formula(faceDetection ? faceDetectionFormulaElement
				: nonFaceDetectionFormulaElement));
		startScript.addBrick(moveBrick);
		sprite.addScript(startScript);

		project.addSprite(sprite);

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);
	}
}

