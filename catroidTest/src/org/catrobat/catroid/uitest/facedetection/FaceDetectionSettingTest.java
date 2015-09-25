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
import android.view.View;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.camera.CameraManager;
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

import java.util.ArrayList;

public class FaceDetectionSettingTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int SCREEN_WIDTH = 480;
	private static final int SCREEN_HEIGHT = 800;

	private Project project;
	Sprite sprite;

	public FaceDetectionSettingTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createCameraProject();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		solo.sleep(300);
	}

	@Override
	protected void tearDown() throws Exception {
		SettingsActivity.setFaceDetectionSharedPreferenceEnabled(
				this.getInstrumentation().getTargetContext(), false);
		super.tearDown();
	}

	public void testReadFaceDetectionPreference() {
		setFaceDetectionPreference(false);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(1000);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(1000);

		assertTrue("Face Detection should be started if needed, although it is disabled in settings",
				FaceDetectionHandler.isFaceDetectionRunning());

		solo.goBackToActivity(MainMenuActivity.class.getSimpleName());
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		setFaceDetectionPreference(true);

		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());

		assertTrue("Face Detection was not started although enabled in settings",
				FaceDetectionHandler.isFaceDetectionRunning());

		solo.goBackToActivity(MainMenuActivity.class.getSimpleName());
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
	}

	public void testSetFaceDetectionPreference() {
		solo.clickOnMenuItem(solo.getString(R.string.settings));

		String preferenceTitle = solo.getString(R.string.preference_title_use_face_detection);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);
		solo.sleep(300);

		assertTrue("Face Detection Preference was not properly written to shared preferences",
				readFaceDetectionPreference());

		solo.clickOnText(preferenceTitle);
		solo.sleep(300);

		assertFalse("Face Detection Preference was not properly written to shared preferences",
				readFaceDetectionPreference());
	}

	public void testCameraSettingsList() {
		solo.clickOnMenuItem(solo.getString(R.string.settings));

		String cameraSettingsTitle = solo.getString(R.string.preference_title_select_camera);
		solo.waitForText(cameraSettingsTitle);
		solo.clickOnText(cameraSettingsTitle);
		solo.sleep(300);

		boolean listFound = false;
		ArrayList<View> views = solo.getCurrentViews();
		for (View view : views) {
			if (view instanceof ListView) {
				listFound = true;
				ListView listView = (ListView) view;
				assertEquals("Number of camera choices does not match camera count", Camera.getNumberOfCameras(),
						listView.getChildCount());
				break;
			}
		}
		assertTrue("No list with camera choices available in Settings", listFound);
	}

	public void testSetCameraSettings() {
		assertTrue("Device must have at least 2 cameras for this test", Camera.getNumberOfCameras() >= 2);

		solo.clickOnMenuItem(solo.getString(R.string.settings));

		String cameraSettingsTitle = solo.getString(R.string.preference_title_select_camera);
		solo.waitForText(cameraSettingsTitle);
		solo.clickOnText(cameraSettingsTitle);
		solo.sleep(300);

		String backCamera = solo.getString(R.string.camera_facing_back);
		String frontCamera = solo.getString(R.string.camera_facing_front);
		solo.waitForText(backCamera);
		solo.clickOnText(backCamera);

		solo.waitForText(cameraSettingsTitle);
		int id = readCameraIDPreference();
		solo.clickOnText(cameraSettingsTitle);
		solo.waitForText(frontCamera);
		solo.clickOnText(frontCamera);
		solo.waitForText(cameraSettingsTitle);

		int otherID = readCameraIDPreference();
		assertTrue("Selected camera id was not written to preferences", otherID != id);
	}

	public void testReadCameraSettings() {
		SettingsActivity.setFaceDetectionSharedPreferenceEnabled(
				getInstrumentation().getTargetContext(), true);

		assertTrue("Device must have at least 2 cameras for this test", Camera.getNumberOfCameras() >= 2);

		setCameraIDPreference("1");

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(1000);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(1000);

		assertEquals("CameraManager did not properly read camera id", 1, CameraManager.getInstance().getCameraID());

		solo.goBackToActivity(MainMenuActivity.class.getSimpleName());
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		setCameraIDPreference("0");

		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());

		assertEquals("CameraManager did not properly update camera id", 0, CameraManager.getInstance().getCameraID());

		solo.goBackToActivity(MainMenuActivity.class.getSimpleName());
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
	}

	private int readCameraIDPreference() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String key = solo.getString(R.string.preference_key_select_camera);
		assertTrue("There is no camera id in shared preferences", preferences.contains(key));
		String idAsString = preferences.getString(key, "0");
		int id = 0;
		try {
			id = Integer.parseInt(idAsString);
		} catch (NumberFormatException exc) {
			fail("Camera id must be integer");
		}
		return id;
	}

	private boolean readFaceDetectionPreference() {
		return SettingsActivity.isFaceDetectionPreferenceEnabled(
				getInstrumentation().getTargetContext());
	}

	private void setCameraIDPreference(String id) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		preferences.edit().putString(solo.getString(R.string.preference_key_select_camera), id).commit();
	}

	private void setFaceDetectionPreference(boolean useFaceDetection) {
		SettingsActivity.setFaceDetectionSharedPreferenceEnabled(
				getInstrumentation().getTargetContext(), useFaceDetection);
	}

	private void createCameraProject() {
		ScreenValues.SCREEN_HEIGHT = SCREEN_HEIGHT;
		ScreenValues.SCREEN_WIDTH = SCREEN_WIDTH;

		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		sprite = new Sprite("testSprite");

		StartScript startScript = new StartScript();
		MoveNStepsBrick moveBrick = new MoveNStepsBrick(new Formula(new FormulaElement(ElementType.SENSOR,
				Sensors.FACE_SIZE.name(), null)));
		startScript.addBrick(moveBrick);
		sprite.addScript(startScript);

		project.addSprite(sprite);

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);
	}
}
