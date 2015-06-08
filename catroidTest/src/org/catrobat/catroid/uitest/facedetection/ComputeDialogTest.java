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

import android.content.SharedPreferences;
import android.hardware.Camera;
import android.preference.PreferenceManager;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.camera.CameraManager;
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
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class ComputeDialogTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int SCREEN_WIDTH = 480;
	private static final int SCREEN_HEIGHT = 800;
	private static final int SLEEP_TIME = 500;

	private Project projectFaceDetection;
	Sprite sprite;

	public ComputeDialogTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProjectFaceDetection();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		solo.sleep(SLEEP_TIME);
	}

	@Override
	protected void tearDown() throws Exception {
		SettingsActivity.setFaceDetectionSharedPreferenceEnabled(
				getInstrumentation().getTargetContext(), false);
		super.tearDown();
	}

	public void testFaceDetectionStart() {
		SettingsActivity.setFaceDetectionSharedPreferenceEnabled(
				getInstrumentation().getTargetContext(), true);

		assertFalse("Face detection should not be running in ScriptActivity",
				FaceDetectionHandler.isFaceDetectionRunning());
		solo.clickOnView(solo.getView(R.id.brick_set_size_to_edit_text));
		solo.sleep(SLEEP_TIME);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
		solo.sleep(SLEEP_TIME);
		assertTrue("Face detection was not started for compute dialog", FaceDetectionHandler.isFaceDetectionRunning());
		solo.goBack();
		solo.sleep(SLEEP_TIME);
		assertFalse("Face detection was not stopped when compute dialog was closed",
				FaceDetectionHandler.isFaceDetectionRunning());
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_0));
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
		solo.sleep(SLEEP_TIME);
		assertFalse("Face detection should not be started for compute dialog if it is not needed",
				FaceDetectionHandler.isFaceDetectionRunning());
		solo.goBack();
	}

	public void testCameraSetting() {
		SettingsActivity.setFaceDetectionSharedPreferenceEnabled(
				getInstrumentation().getTargetContext(), true);

		assertTrue("Device must have at least 2 cameras for this test", Camera.getNumberOfCameras() >= 2);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		preferences.edit().putString(solo.getString(R.string.preference_key_select_camera), "1").commit();

		solo.clickOnView(solo.getView(R.id.brick_set_size_to_edit_text));
		solo.sleep(SLEEP_TIME);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
		solo.sleep(SLEEP_TIME);
		assertEquals("CameraManager did not read camera id for compute dialog", 1, CameraManager.getInstance()
				.getCameraID());
		solo.goBack();
		preferences.edit().putString(solo.getString(R.string.preference_key_select_camera), "0").commit();
		solo.sleep(SLEEP_TIME);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_compute));
		solo.sleep(SLEEP_TIME);
		assertEquals("CameraManager did not read camera id for compute dialog", 0, CameraManager.getInstance()
				.getCameraID());
		solo.goBack();
	}

	private void createProjectFaceDetection() {
		ScreenValues.SCREEN_HEIGHT = SCREEN_HEIGHT;
		ScreenValues.SCREEN_WIDTH = SCREEN_WIDTH;

		projectFaceDetection = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		sprite = new Sprite("fdSprite");

		StartScript startScript = new StartScript();
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(new Formula(new FormulaElement(ElementType.SENSOR,
				Sensors.FACE_SIZE.name(), null)));
		startScript.addBrick(setSizeToBrick);
		sprite.addScript(startScript);

		projectFaceDetection.addSprite(sprite);

		StorageHandler.getInstance().saveProject(projectFaceDetection);
		ProjectManager.getInstance().setProject(projectFaceDetection);
	}
}
