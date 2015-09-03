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
package org.catrobat.catroid.test.formulaeditor;

import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.test.InstrumentationTestCase;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
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
import org.catrobat.catroid.test.utils.SimulatedSensorManager;
import org.catrobat.catroid.test.utils.SimulatedSoundRecorder;
import org.catrobat.catroid.uitest.annotation.Device;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class ParserTestSensors extends InstrumentationTestCase {

	private Project project;
	private Sprite firstSprite;
	Script startScript1;
	private float delta = 0.001f;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		//For initialization
		SensorLoudness.getSensorLoudness();
		SensorLoudness loudnessSensor = (SensorLoudness) Reflection.getPrivateField(SensorLoudness.class, "instance");
		SimulatedSoundRecorder simSoundRec = new SimulatedSoundRecorder("/dev/null");
		Reflection.setPrivateField(loudnessSensor, "recorder", simSoundRec);
	}

	@Override
	public void tearDown() throws Exception {
		SensorHandler.stopSensorListeners();
		Reflection.setPrivateField(SensorHandler.class, "instance", null);
		Reflection.setPrivateField(SensorLoudness.class, "instance", null);
		super.tearDown();
	}

	public void testSensorManagerNotInitialized() {
		SensorHandler.registerListener(null);
		SensorHandler.unregisterListener(null);
		SensorHandler.startSensorListener(getInstrumentation().getTargetContext());
		assertEquals("SensorHandler not initialized value error", 0d,
				SensorHandler.getSensorValue(Sensors.X_ACCELERATION));
	}

	public void testSensorHandlerWithLookSensorValue() {
		SensorHandler.startSensorListener(getInstrumentation().getContext());
		assertEquals("SensorHandler returned wrong value when Sensor is not found in List", 0d,
				SensorHandler.getSensorValue(Sensors.OBJECT_BRIGHTNESS));
		SensorHandler.stopSensorListeners();
	}

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
			Log.e("SensorTest", method.getName());
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

		assertEquals(
				"Unexpected sensor value for face x position (= in portrait mode, the central x coordinate of the face if the camera input is projected fullscreen to the display (range: 0 to screen width) )",
				expectedFaceXPosition, interpretFormula(formula8), delta);

		assertEquals(
				"Unexpected sensor value for face y position (= in portrait mode, the central y coordinate of the face if the camera input is projected fullscreen to the display (range: 0 to screen height) )",
				expectedFaceYPosition, interpretFormula(formula9), delta);

		SensorHandler.stopSensorListeners();
	}

	@Device
	public void testSensors() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		Formula formula = createFormulaWithSensor(Sensors.X_ACCELERATION);
		ChangeSizeByNBrick xAccelerationBrick = new ChangeSizeByNBrick(formula);
		startScript1.addBrick(xAccelerationBrick);

		Formula formula1 = createFormulaWithSensor(Sensors.Y_ACCELERATION);
		ChangeSizeByNBrick yAccelerationBrick = new ChangeSizeByNBrick(formula1);
		startScript1.addBrick(yAccelerationBrick);

		Formula formula2 = createFormulaWithSensor(Sensors.Z_ACCELERATION);
		ChangeSizeByNBrick zAccelerationBrick = new ChangeSizeByNBrick(formula2);
		startScript1.addBrick(zAccelerationBrick);

		Formula formula3 = createFormulaWithSensor(Sensors.COMPASS_DIRECTION);
		ChangeSizeByNBrick compassDirectionBrick = new ChangeSizeByNBrick(formula3);
		startScript1.addBrick(compassDirectionBrick);

		Formula formula4 = createFormulaWithSensor(Sensors.X_INCLINATION);
		ChangeSizeByNBrick xInclincationBrick = new ChangeSizeByNBrick(formula4);
		startScript1.addBrick(xInclincationBrick);

		Formula formula5 = createFormulaWithSensor(Sensors.Y_INCLINATION);
		ChangeSizeByNBrick yInclinationBrick = new ChangeSizeByNBrick(formula5);
		startScript1.addBrick(yInclinationBrick);

		Formula formula6 = createFormulaWithSensor(Sensors.LOUDNESS);
		ChangeSizeByNBrick loudnessBrick = new ChangeSizeByNBrick(formula6);
		startScript1.addBrick(loudnessBrick);

		//For initialization
		SensorHandler.startSensorListener(getInstrumentation().getTargetContext());
		SensorHandler.stopSensorListeners();

		SensorHandler sensorHandler = (SensorHandler) Reflection.getPrivateField(SensorHandler.class, "instance");
		SimulatedSensorManager simulatedSensorManager = new SimulatedSensorManager();
		Reflection.setPrivateField(sensorHandler, "sensorManager", simulatedSensorManager);

		Sensor accelerometerSensor = (Sensor) Reflection.getPrivateField(sensorHandler,
				"linearAccelerationSensor");
		Sensor rotationVectorSensor = (Sensor) Reflection.getPrivateField(sensorHandler, "rotationVectorSensor");
		SensorHandler.startSensorListener(getInstrumentation().getTargetContext());

		long startTime = System.currentTimeMillis();

		while (simulatedSensorManager.getLatestSensorEvent(accelerometerSensor) == null
				|| simulatedSensorManager.getLatestSensorEvent(rotationVectorSensor) == null
				|| !checkValidRotationValues(simulatedSensorManager.getLatestSensorEvent(rotationVectorSensor))
				|| simulatedSensorManager.getLatestCustomSensorEvent(Sensors.LOUDNESS) == null) {

			simulatedSensorManager.sendGeneratedSensorValues();

			if (startTime < System.currentTimeMillis() - 10000) {
				fail("SensorEvent generation Timeout. Check Sensor Simulation!");
			}
		}

		float expectedLoudness = (Float) Reflection.getPrivateField(sensorHandler, "loudness");

		float expectedXAcceleration = (Float) Reflection.getPrivateField(sensorHandler, "linearAccelerationX");
		float expectedYAcceleration = (Float) Reflection.getPrivateField(sensorHandler, "linearAccelerationY");
		float expectedZAcceleration = (Float) Reflection.getPrivateField(sensorHandler, "linearAccelerationZ");

		float[] rotationMatrix = new float[16];
		float[] rotationVector = (float[]) Reflection.getPrivateField(sensorHandler, "rotationVector");
		float[] orientations = new float[3];

		android.hardware.SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector);
		android.hardware.SensorManager.getOrientation(rotationMatrix, orientations);

		double expectedCompassDirection = (double) orientations[0] * SensorHandler.RADIAN_TO_DEGREE_CONST * -1f;
		double expectedXInclination = (double) orientations[2] * SensorHandler.RADIAN_TO_DEGREE_CONST * -1f;
		double expectedYInclination;

		float xInclinationUsedToExtendRangeOfRoll = orientations[2] * SensorHandler.RADIAN_TO_DEGREE_CONST * -1f;

		Double sensorValue = (double) orientations[1];

		if (Math.abs(xInclinationUsedToExtendRangeOfRoll) <= 90f) {
			expectedYInclination = sensorValue * SensorHandler.RADIAN_TO_DEGREE_CONST * -1f;
		} else {
			float uncorrectedYInclination = sensorValue.floatValue() * SensorHandler.RADIAN_TO_DEGREE_CONST * -1f;

			if (uncorrectedYInclination > 0f) {
				expectedYInclination = 180f - uncorrectedYInclination;
			} else {
				expectedYInclination = -180f - uncorrectedYInclination;
			}
		}

		assertEquals(
				"Unexpected sensor value for acceleration in x direction(= in portrait mode, from left to right side of screen surface, in m/s^2)",
				expectedXAcceleration, interpretFormula(formula), delta);
		assertEquals(
				"Unexpected sensor value for acceleration in y direction(= in portrait mode, from bottom to upper side of screen surface, in m/s^2)",
				expectedYAcceleration, interpretFormula(formula1), delta);
		assertEquals(
				"Unexpected sensor value for acceleration in z direction(= in portrait mode, from screen surface orthogonally upwards away from screen, in m/s^2)",
				expectedZAcceleration, interpretFormula(formula2), delta);
		assertEquals(
				"Unexpected sensor value for compass direction (= in portrait mode, deviation of screen-down-to-up-side (= positive y axis direction) from magnetic north in degrees, with z axis (pointing to sky) serving as rotation axis; positive direction = counter-clockwise turn seen from above; this is the angle between magnetic north and the device's y axis as it is displayed on a compass. For example, if the device's y axis points towards the magnetic north this value is 0, and if the device's y axis is pointing south this value is approaching 180 or -180. When the y axis is pointing west this value is 90 and when it is pointing east this value is -90)",
				expectedCompassDirection, interpretFormula(formula3), delta);
		assertEquals(
				"Unexpected sensor value for x inclination (= in portrait mode, deviation from screen-left-to-right-side (= x axis direction) horizontal inclination (range: -180 to +180 degrees; flat = 0); increasing values of x inclination = right border of screen pulled towards user, left border away = positive side of x axis gets lifted up)",
				expectedXInclination, interpretFormula(formula4), delta);
		assertEquals(
				"Unexpected sensor value for y inclination (= in portrait mode, deviation from screen-down-to-up-side (= y axis direction) horizontal inclination (range: -180 to +180 degrees; flat = 0); increasing values of y inclination = upper border of screen pulled towards user, lower border away = positive side of y axis gets lifted up)",
				expectedYInclination, interpretFormula(formula5), delta);
		assertEquals("Unexpected sensor value for loudness", expectedLoudness, interpretFormula(formula6),
				delta);

		SensorHandler.stopSensorListeners();
	}

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

	private boolean checkValidRotationValues(SensorEvent sensorEvent) {
		float[] rotationMatrix = new float[16];
		float[] rotationVector = new float[3];
		float[] orientations = new float[3];

		rotationVector[0] = sensorEvent.values[0];
		rotationVector[1] = sensorEvent.values[1];
		rotationVector[2] = sensorEvent.values[2];

		android.hardware.SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector);
		android.hardware.SensorManager.getOrientation(rotationMatrix, orientations);

		for (float orientation : orientations) {
			if (Float.compare(orientation, Float.NaN) == 0) {
				return false;
			}
		}
		return true;
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
		firstSprite = new Sprite("zwoosh");
		startScript1 = new StartScript();
		Brick changeBrick = new ChangeSizeByNBrick(10);
		firstSprite.addScript(startScript1);
		startScript1.addBrick(changeBrick);
		project.addSprite(firstSprite);
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
