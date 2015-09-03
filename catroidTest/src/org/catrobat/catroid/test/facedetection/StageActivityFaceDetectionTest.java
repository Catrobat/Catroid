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

package org.catrobat.catroid.test.facedetection;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.utils.BaseActivityUnitTestCase;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class StageActivityFaceDetectionTest extends BaseActivityUnitTestCase<StageActivity> {

	public StageActivityFaceDetectionTest() {
		super(StageActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createProjectFaceDetection();
	}

	public void testFaceDetectionInStageActivityLifecycle() throws InterruptedException {
		assertTrue("Face detection did not start!", FaceDetectionHandler.startFaceDetection(getActivity()));

		Activity dial = startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:42"))
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), null, null);
		Reflection.setPrivateField(StageListener.class, getActivity().stageListener, "sprites", new ArrayList<Sprite>());

		getInstrumentation().callActivityOnPause(dial);
		assertFalse("Face detection should be stopped when other application is started!",
				FaceDetectionHandler.isFaceDetectionRunning());

		getInstrumentation().callActivityOnResume(dial);
		assertTrue("Face detection was not restarted when coming back to stage!",
				FaceDetectionHandler.isFaceDetectionRunning());

		getActivity().pause();
		assertFalse("Face detection should be stopped on pause!",
				FaceDetectionHandler.isFaceDetectionRunning());

		getActivity().resume();
		assertTrue("Face detection was not restarted on resume!",
				FaceDetectionHandler.isFaceDetectionRunning());
	}

	private void createProjectFaceDetection() {
		ScreenValues.SCREEN_HEIGHT = 480;
		ScreenValues.SCREEN_WIDTH = 800;

		Project projectFaceDetection = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("In your FACE!");
		StartScript startScript = new StartScript();
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(new Formula(new FormulaElement(FormulaElement.ElementType.SENSOR,
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
