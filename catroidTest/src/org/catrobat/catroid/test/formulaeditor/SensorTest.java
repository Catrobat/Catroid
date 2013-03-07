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
package org.catrobat.catroid.test.formulaeditor;

import java.util.LinkedList;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.SimulatedSensorManager;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.test.InstrumentationTestCase;

public class SensorTest extends InstrumentationTestCase {

	private Project project;
	private Sprite firstSprite;
	private Brick changeBrick;
	Script startScript1;
	private float delta = 0.001f;

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		SensorHandler.stopSensorListeners();
		Reflection.setPrivateField(SensorHandler.class, "instance", null);
		super.tearDown();
	}

	public void testSensors() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		createProject();

		Formula formula = createFormulaWithSensor(Sensors.X_ACCELERATION);
		ChangeSizeByNBrick xBrick = new ChangeSizeByNBrick(firstSprite, formula);
		startScript1.addBrick(xBrick);

		Formula formula1 = createFormulaWithSensor(Sensors.Y_ACCELERATION);
		ChangeSizeByNBrick yBrick = new ChangeSizeByNBrick(firstSprite, formula1);
		startScript1.addBrick(yBrick);

		Formula formula2 = createFormulaWithSensor(Sensors.Z_ACCELERATION);
		ChangeSizeByNBrick zBrick = new ChangeSizeByNBrick(firstSprite, formula2);
		startScript1.addBrick(zBrick);

		Formula formula3 = createFormulaWithSensor(Sensors.Z_ORIENTATION);
		ChangeSizeByNBrick azimuthBrick = new ChangeSizeByNBrick(firstSprite, formula3);
		startScript1.addBrick(azimuthBrick);

		Formula formula4 = createFormulaWithSensor(Sensors.X_ORIENTATION);
		ChangeSizeByNBrick pitchBrick = new ChangeSizeByNBrick(firstSprite, formula4);
		startScript1.addBrick(pitchBrick);

		Formula formula5 = createFormulaWithSensor(Sensors.Y_ORIENTATION);
		ChangeSizeByNBrick rollBrick = new ChangeSizeByNBrick(firstSprite, formula5);
		startScript1.addBrick(rollBrick);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

		//For initialization

		SensorHandler.startSensorListener(getInstrumentation().getTargetContext());

		SensorHandler.stopSensorListeners();

		SensorHandler sensorHandler = (SensorHandler) Reflection.getPrivateField(SensorHandler.class, "instance");
		SimulatedSensorManager sensorManager = new SimulatedSensorManager();
		Reflection.setPrivateField(sensorHandler, "sensorManager", sensorManager);

		Sensor accelerometerSensor = (Sensor) Reflection.getPrivateField(sensorHandler, "accelerometerSensor");
		Sensor rotationVectorSensor = (Sensor) Reflection.getPrivateField(sensorHandler, "rotationVectorSensor");
		SensorHandler.startSensorListener(getInstrumentation().getTargetContext());

		long startTime = System.currentTimeMillis();

		while (sensorManager.getLatestSensorEvent(accelerometerSensor) == null
				|| sensorManager.getLatestSensorEvent(rotationVectorSensor) == null
				|| checkValidRotationValues(sensorManager.getLatestSensorEvent(rotationVectorSensor)) == false) {

			sensorManager.sendGeneratedSensorValues();

			if (startTime < System.currentTimeMillis() - 10000) {
				fail("SensorEvent generation Timeout. Check Sensor Simulation!");
			}
		}

		float expectedX = (Float) Reflection.getPrivateField(sensorHandler, "linearAcceleartionX");
		float expectedY = (Float) Reflection.getPrivateField(sensorHandler, "linearAcceleartionY");
		float expectedZ = (Float) Reflection.getPrivateField(sensorHandler, "linearAcceleartionZ");

		float[] rotationMatrix = new float[16];
		float[] rotationVector = (float[]) Reflection.getPrivateField(sensorHandler, "rotationVector");
		float[] orientations = new float[3];

		SensorHandler.getRotationMatrixFromVector(rotationMatrix, rotationVector);
		android.hardware.SensorManager.getOrientation(rotationMatrix, orientations);

		double expectedOrientationZ = Double.valueOf(orientations[0]) * SensorHandler.radianToDegreeConst;
		double expectedOrientationX = Double.valueOf(orientations[1]) * SensorHandler.radianToDegreeConst;
		double expectedOrientationY = Double.valueOf(orientations[2]) * SensorHandler.radianToDegreeConst;

		assertEquals("Sensor value is wrong", expectedX, formula.interpretFloat(firstSprite), delta);
		assertEquals("Sensor value is wrong", expectedY, formula1.interpretFloat(firstSprite), delta);
		assertEquals("Sensor value is wrong", expectedZ, formula2.interpretFloat(firstSprite), delta);

		assertEquals("Sensor value is wrong", expectedOrientationZ, formula3.interpretFloat(firstSprite), delta);
		assertEquals("Sensor value is wrong", expectedOrientationX, formula4.interpretFloat(firstSprite), delta);
		assertEquals("Sensor value is wrong", expectedOrientationY, formula5.interpretFloat(firstSprite), delta);

		SensorHandler.stopSensorListeners();

	}

	private boolean checkValidRotationValues(SensorEvent sensorEvent) {
		float[] rotationMatrix = new float[16];
		float[] rotationVector = new float[3];
		float[] orientations = new float[3];

		rotationVector[0] = sensorEvent.values[0];
		rotationVector[1] = sensorEvent.values[1];
		rotationVector[2] = sensorEvent.values[2];

		SensorHandler.getRotationMatrixFromVector(rotationMatrix, rotationVector);
		android.hardware.SensorManager.getOrientation(rotationMatrix, orientations);

		for (float orientation : orientations) {
			if (Float.compare(orientation, Float.NaN) == 0) {
				return false;
			}

		}

		return true;

	}

	//	private int tryMock(String method, Formula formula, int expectedResult) {
	//		Input mock = PowerMock.createPartialMock(Input.class, method);
	//		//		SensorHandler.setSensorSourceForNextCall(mock); 
	//
	//		try {
	//			PowerMock.expectPrivate(mock, method).andReturn(expectedResult);
	//			PowerMock.replayAll();
	//			return formula.interpretInteger();
	//
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//		return -1;
	//
	//	}

	private Formula createFormulaWithSensor(Sensors sensor) {

		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.SENSOR, sensor.name()));
		InternFormulaParser internFormulaParser = new InternFormulaParser(internTokenList);
		FormulaElement root = internFormulaParser.parseFormula();
		Formula formula = new Formula(root);

		return formula;
	}

	private void createProject() {
		this.project = new Project(null, "testProject");
		firstSprite = new Sprite("zwoosh");
		startScript1 = new StartScript(firstSprite);
		changeBrick = new ChangeSizeByNBrick(firstSprite, 10);
		firstSprite.addScript(startScript1);
		startScript1.addBrick(changeBrick);
		project.addSprite(firstSprite);

	}
}
