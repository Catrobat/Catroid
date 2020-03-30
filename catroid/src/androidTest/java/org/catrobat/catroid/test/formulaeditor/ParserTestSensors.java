/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

import android.Manifest;
import android.graphics.Point;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.camera.CameraManager;
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
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.SensorLoudness;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.soundrecorder.SoundRecorder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import androidx.test.annotation.UiThreadTest;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ParserTestSensors {

	@Rule
	public GrantPermissionRule runtimePermissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);

	private Project project;
	private Sprite firstSprite;
	private Script startScript1;
	private float delta = 0.001f;

	@Before
	@UiThreadTest
	public void setUp() throws Exception {
		createProject();
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
	}

	@After
	@UiThreadTest
	public void tearDown() throws Exception {
		SensorHandler.stopSensorListeners();
	}

	@Test
	@UiThreadTest
	public void testSensorManagerNotInitialized() {
		SensorHandler.registerListener(null);
		SensorHandler.unregisterListener(null);
		SensorHandler.startSensorListener(ApplicationProvider.getApplicationContext());

		assertEquals(0d, Math.abs((Double) SensorHandler.getSensorValue(Sensors.X_ACCELERATION)));
	}

	@Test
	@UiThreadTest
	public void testSensorHandlerWithLookSensorValue() {
		SensorHandler.startSensorListener(ApplicationProvider.getApplicationContext());
		assertEquals(0d, SensorHandler.getSensorValue(Sensors.OBJECT_BRIGHTNESS));
		SensorHandler.stopSensorListeners();
	}

	@Test
	@UiThreadTest
	public void testFaceDetection() throws Exception {
		CameraManager.makeInstance();
		SensorHandler.startSensorListener(ApplicationProvider.getApplicationContext());
		FaceDetector faceDetector = FaceDetectionHandler.getFaceDetector();

		assertNotNull(faceDetector);

		assertEquals(0d, SensorHandler.getSensorValue(Sensors.FACE_DETECTED));
		assertEquals(0d, SensorHandler.getSensorValue(Sensors.FACE_SIZE));

		faceDetector.callOnFaceDetected(true);

		int expectedFaceSize = (int) (Math.random() * 100);
		int exampleScreenWidth = 320;
		int exampleScreenHeight = 480;
		int expectedFaceXPosition = (int) (-exampleScreenWidth / 2 + (Math.random() * exampleScreenWidth));
		int expectedFaceYPosition = (int) (-exampleScreenHeight / 2 + (Math.random() * exampleScreenHeight));

		faceDetector.callOnFaceDetected(new Point(expectedFaceXPosition, expectedFaceYPosition), expectedFaceSize);

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

		assertEquals(1d, formula6.interpretFloat(firstSprite), delta);

		assertEquals(expectedFaceSize, formula7.interpretFloat(firstSprite), delta);

		assertEquals(expectedFaceXPosition, formula8.interpretFloat(firstSprite), delta);

		assertEquals(expectedFaceYPosition, -formula9.interpretFloat(firstSprite), delta);

		SensorHandler.stopSensorListeners();
	}

	@Test
	@UiThreadTest
	public void testMicRelease() throws IOException {
		SensorLoudness loudnessSensor = new SensorLoudness();
		SoundRecorder soundRecorder = Mockito.mock(SoundRecorder.class);
		loudnessSensor.setSoundRecorder(soundRecorder);
		InOrder inOrder = inOrder(soundRecorder);

		when(soundRecorder.isRecording()).thenReturn(false);
		SensorHandler.getInstance(ApplicationProvider.getApplicationContext()).setSensorLoudness(loudnessSensor);
		SensorHandler.startSensorListener(ApplicationProvider.getApplicationContext());
		inOrder.verify(soundRecorder).start();

		when(soundRecorder.isRecording()).thenReturn(true);
		SensorHandler.stopSensorListeners();
		inOrder.verify(soundRecorder).stop();
	}

	private Formula createFormulaWithSensor(Sensors sensor) {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.SENSOR, sensor.name()));
		InternFormulaParser internFormulaParser = new InternFormulaParser(internTokenList);
		FormulaElement root = internFormulaParser.parseFormula();
		return new Formula(root);
	}

	private void createProject() {
		this.project = new Project(ApplicationProvider.getApplicationContext(), "testProject");
		firstSprite = new SingleSprite("zwoosh");
		startScript1 = new StartScript();
		firstSprite.addScript(startScript1);
		Brick changeBrick = new ChangeSizeByNBrick(10);
		startScript1.addBrick(changeBrick);
		project.getDefaultScene().addSprite(firstSprite);
	}
}
