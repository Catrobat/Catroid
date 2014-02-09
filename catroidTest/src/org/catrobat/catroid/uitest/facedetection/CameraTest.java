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
package org.catrobat.catroid.uitest.facedetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.camera.JpgPreviewCallback;
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
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class CameraTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int SCREEN_WIDTH = 480;
	private static final int SCREEN_HEIGHT = 800;
	private static final int MAX_FRAME_DELAY_IN_MS = 1000;

	private Project project;
	Sprite sprite;

	public CameraTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
	}

	@Device
	public void testJpgPreviewFrameCallback() {
		final int[] calls = new int[1];
		calls[0] = 0;
		JpgPreviewCallback callback = new JpgPreviewCallback() {
			public void onJpgPreviewFrame(byte[] jpgData) {
				calls[0]++;
				if (calls[0] == 1) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(jpgData, 0, jpgData.length);
					assertNotNull("Could not create bitmap from data - wrong format?", bitmap);
				}
			}
		};
		assertTrue("Face detection is not running (so the camera was probably no started either)",
				FaceDetectionHandler.isFaceDetectionRunning());
		CameraManager.getInstance().addOnJpgPreviewFrameCallback(callback);
		solo.sleep(MAX_FRAME_DELAY_IN_MS);
		assertTrue("Did not receive frame data from camera", calls[0] > 0);
		CameraManager.getInstance().removeOnJpgPreviewFrameCallback(callback);
	}

	private void createProject() {
		ScreenValues.SCREEN_HEIGHT = SCREEN_HEIGHT;
		ScreenValues.SCREEN_WIDTH = SCREEN_WIDTH;

		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		sprite = new Sprite("testSprite");

		StartScript startScript = new StartScript(sprite);
		MoveNStepsBrick moveBrick = new MoveNStepsBrick(sprite, new Formula(new FormulaElement(ElementType.SENSOR,
				Sensors.FACE_SIZE.name(), null)));
		startScript.addBrick(moveBrick);
		sprite.addScript(startScript);

		project.addSprite(sprite);

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);
	}
}
