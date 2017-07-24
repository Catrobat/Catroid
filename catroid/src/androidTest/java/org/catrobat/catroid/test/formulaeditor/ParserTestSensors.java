/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.test.formulaeditor;

import android.graphics.Point;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.facedetection.FaceDetector;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.SensorLoudness;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.Reflection.ParameterList;
import org.catrobat.catroid.test.utils.SimulatedSoundRecorder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ParserTestSensors {
	@Rule
	public UiThreadTestRule uiThreadTestRule = new UiThreadTestRule();

	private static final String TAG = ParserTestSensors.class.getSimpleName();

	private Project project;
	private Sprite firstSprite;
	Script startScript1;
	private float delta = 0.001f;

	@Before
	public void setUp() throws Exception {
		createProject();
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		//For initialization
		SensorLoudness.getSensorLoudness();
		SensorLoudness loudnessSensor = (SensorLoudness) Reflection.getPrivateField(SensorLoudness.class, "instance");
		SimulatedSoundRecorder simSoundRec = new SimulatedSoundRecorder("/dev/null");
		Reflection.setPrivateField(loudnessSensor, "recorder", simSoundRec);
	}

	@After
	public void tearDown() throws Exception {
		SensorHandler.stopSensorListeners();
		Reflection.setPrivateField(SensorHandler.class, "instance", null);
		Reflection.setPrivateField(SensorLoudness.class, "instance", null);
	}

	@Test
	@UiThreadTest
	public void testSensorManagerNotInitialized() {
		SensorHandler.registerListener(null);
		SensorHandler.unregisterListener(null);
		SensorHandler.startSensorListener(getInstrumentation().getTargetContext());

		if (ProjectManager.getInstance().isCurrentProjectLandscapeMode()) {
			assertEquals("SensorHandler not initialized value error", 0d,
					Math.abs((Double) SensorHandler.getSensorValue(Sensors.Y_ACCELERATION)));
		} else {
			assertEquals("SensorHandler not initialized value error", 0d,
					Math.abs((Double) SensorHandler.getSensorValue(Sensors.X_ACCELERATION)));
		}
	}

	@Test
	@UiThreadTest
	public void testSensorHandlerWithLookSensorValue() {
		SensorHandler.startSensorListener(getInstrumentation().getTargetContext());
		assertEquals("SensorHandler returned wrong value when Sensor is not found in List", 0d,
				SensorHandler.getSensorValue(Sensors.OBJECT_BRIGHTNESS));
		SensorHandler.stopSensorListeners();
	}

	@Test
	@UiThreadTest
	public void testFaceDetection() {
		SensorHandler.startSensorListener(getInstrumentation().getTargetContext());
		FaceDetector faceDetector = (FaceDetector) Reflection.getPrivateField(FaceDetectionHandler.class,
				"faceDetector");

		assertNotNull("SensorHandler was not registered as a listener at FaceDetectionHandler", faceDetector);

		assertEquals("Face detection status initial value error", 0d,
				SensorHandler.getSensorValue(Sensors.FACE_DETECTED));
		assertEquals("Face detection size initial value error", 0d, SensorHandler.getSensorValue(Sensors.FACE_SIZE));

		Method[] methods = faceDetector.getClass().getSuperclass().getDeclaredMethods();
		for (Method method : methods) {
			Log.e(TAG, method.getName());
		}

		ParameterList parameters = new ParameterList(Boolean.TRUE);
		Reflection.invokeMethod(faceDetector.getClass().getSuperclass(), faceDetector, "onFaceDetected", parameters);

		int expectedFaceSize = (int) (Math.random() * 100);
		int exampleScreenWidth = 320;
		int exampleScreenHeight = 480;
		int expectedFaceXPosition = (int) (-exampleScreenWidth / 2 + (Math.random() * exampleScreenWidth));
		int expectedFaceYPosition = (int) (-exampleScreenHeight / 2 + (Math.random() * exampleScreenHeight));

		parameters = new ParameterList(new Point(expectedFaceXPosition, expectedFaceYPosition),
				expectedFaceSize);
		Reflection.invokeMethod(faceDetector.getClass().getSuperclass(), faceDetector, "onFaceDetected", parameters);

		Formula formula6 = createFormulaWithSensor(Sensors.FACE_DETECTED);
		ChangeSizeByNBrick faceDetectionStatusBrick = new ChangeSizeByNBrick(formula6);
		startScript1.addBrick(faceDetectionStatusBrick);

		Formula formula7 = createFormulaWithSensor(Sensors.FACE_SIZE);
		ChangeSizeByNBrick faceSizeBrick = new ChangeSizeByNBrick(formula7);
		startScript1.addBrick(faceSizeBrick);

		Formula formula8 = createFormulaWithSensor(Sensors.FACE_X_POSITION);
		ChangeSizeByNBrick faceXPositionBrick = new ChangeSizeByNBrick(formula8);
		startScript1.addBrick(faceXPositionBrick);

		Formula formula9 = createFormulaWithSensor(Sensors.FACE_Y_POSITION);
		ChangeSizeByNBrick faceYPositionBrick = new ChangeSizeByNBrick(formula9);
		startScript1.addBrick(faceYPositionBrick);

		assertEquals("Unexpected sensor value for face detection status (= 1 if face detected, 0 otherwise)", 1d,
				interpretFormula(formula6));

		assertEquals(
				"Unexpected sensor value for face size (= width of the face (range: 0 to 100, where at 100 the face fills half the width of the cameras view)",
				expectedFaceSize, interpretFormula(formula7), delta);

		if (ProjectManager.getInstance().isCurrentProjectLandscapeMode()) {
			assertEquals(
					"Unexpected sensor value for face x position (= in portrait mode, the central x coordinate of the face if the camera input is projected fullscreen to the display (range: 0 to screen width) )",
					expectedFaceXPosition, interpretFormula(formula9), delta);

			assertEquals(
					"Unexpected sensor value for face y position (= in portrait mode, the central y coordinate of the face if the camera input is projected fullscreen to the display (range: 0 to screen height) )",
					expectedFaceYPosition, -interpretFormula(formula8), delta);
		} else {
			assertEquals(
					"Unexpected sensor value for face x position (= in portrait mode, the central x coordinate of the face if the camera input is projected fullscreen to the display (range: 0 to screen width) )",
					expectedFaceXPosition, interpretFormula(formula8), delta);

			assertEquals(
					"Unexpected sensor value for face y position (= in portrait mode, the central y coordinate of the face if the camera input is projected fullscreen to the display (range: 0 to screen height) )",
					expectedFaceYPosition, -interpretFormula(formula9), delta);
		}
		SensorHandler.stopSensorListeners();
	}

	@Test
	@UiThreadTest
	public void testMicRelease() {

		SensorLoudness.getSensorLoudness();
		SensorLoudness loudnessSensor = (SensorLoudness) Reflection.getPrivateField(SensorLoudness.class, "instance");
		SimulatedSoundRecorder simulatedSoundRecorder = new SimulatedSoundRecorder("/dev/null");
		Reflection.setPrivateField(loudnessSensor, "recorder", simulatedSoundRecorder);

		SensorHandler.startSensorListener(getInstrumentation().getTargetContext());
		assertEquals("LoudnessSensor dit not start recording, isRecording()", true, simulatedSoundRecorder.isRecording());
		SensorHandler.stopSensorListeners();
		assertEquals("LoudnessSensor did not stop recording, isRecording()", false, simulatedSoundRecorder.isRecording());
	}

	private Formula createFormulaWithSensor(Sensors sensor) {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.SENSOR, sensor.name()));
		InternFormulaParser internFormulaParser = new InternFormulaParser(internTokenList);
		FormulaElement root = internFormulaParser.parseFormula();
		return new Formula(root);
	}

	private void createProject() {
		this.project = new Project(null, "testProject");
		firstSprite = new SingleSprite("zwoosh");
		startScript1 = new StartScript();
		Brick changeBrick = new ChangeSizeByNBrick(10);
		firstSprite.addScript(startScript1);
		startScript1.addBrick(changeBrick);
		project.getDefaultScene().addSprite(firstSprite);
	}

	private Double interpretFormula(Formula formula) {
		try {
			return formula.interpretDouble(firstSprite);
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for Formula failed.", interpretationException);
		}
		return Double.NaN;
	}
}
