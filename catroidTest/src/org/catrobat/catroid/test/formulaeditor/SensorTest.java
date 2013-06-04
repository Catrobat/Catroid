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

	public void testSensors() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		createProject();

		Formula formula = createFormulaWithSensor(Sensors.X_ACCELERATION);
		ChangeSizeByNBrick xAccelerationBrick = new ChangeSizeByNBrick(firstSprite, formula);
		startScript1.addBrick(xAccelerationBrick);

		Formula formula1 = createFormulaWithSensor(Sensors.Y_ACCELERATION);
		ChangeSizeByNBrick yAccelerationBrick = new ChangeSizeByNBrick(firstSprite, formula1);
		startScript1.addBrick(yAccelerationBrick);

		Formula formula2 = createFormulaWithSensor(Sensors.Z_ACCELERATION);
		ChangeSizeByNBrick zAccelerationBrick = new ChangeSizeByNBrick(firstSprite, formula2);
		startScript1.addBrick(zAccelerationBrick);

		Formula formula3 = createFormulaWithSensor(Sensors.COMPASS_DIRECTION);
		ChangeSizeByNBrick compassDirectionBrick = new ChangeSizeByNBrick(firstSprite, formula3);
		startScript1.addBrick(compassDirectionBrick);

		Formula formula4 = createFormulaWithSensor(Sensors.X_INCLINATION);
		ChangeSizeByNBrick xInclincationBrick = new ChangeSizeByNBrick(firstSprite, formula4);
		startScript1.addBrick(xInclincationBrick);

		Formula formula5 = createFormulaWithSensor(Sensors.Y_INCLINATION);
		ChangeSizeByNBrick yInclinationBrick = new ChangeSizeByNBrick(firstSprite, formula5);
		startScript1.addBrick(yInclinationBrick);

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

		float expectedXAcceleration = (Float) Reflection.getPrivateField(sensorHandler, "linearAcceleartionX");
		float expectedYAcceleration = (Float) Reflection.getPrivateField(sensorHandler, "linearAcceleartionY");
		float expectedZAcceleration = (Float) Reflection.getPrivateField(sensorHandler, "linearAcceleartionZ");

		float[] rotationMatrix = new float[16];
		float[] rotationVector = (float[]) Reflection.getPrivateField(sensorHandler, "rotationVector");
		float[] orientations = new float[3];

		SensorHandler.getRotationMatrixFromVector(rotationMatrix, rotationVector);
		android.hardware.SensorManager.getOrientation(rotationMatrix, orientations);

		double expectedCompassDirection = Double.valueOf(orientations[0]) * SensorHandler.radianToDegreeConst * -1f;
		double expectedXInclination = Double.valueOf(orientations[2]) * SensorHandler.radianToDegreeConst * -1f;
		double expectedYInclination = Double.valueOf(orientations[1]) * SensorHandler.radianToDegreeConst * -1f;

		assertEquals(
				"Unexpected sensor value for acceleration in x direction(= in portrait mode, from left to right side of screen surface, in m/s^2)",
				expectedXAcceleration, formula.interpretDouble(firstSprite), delta);

		assertEquals(
				"Unexpected sensor value for acceleration in y direction(= in portrait mode, from bottom to upper side of screen surface, in m/s^2)",
				expectedYAcceleration, formula1.interpretDouble(firstSprite), delta);

		assertEquals(
				"Unexpected sensor value for acceleration in z direction(= in portrait mode, from screen surface orthogonally upwards away from screen, in m/s^2)",
				expectedZAcceleration, formula2.interpretDouble(firstSprite), delta);

		assertEquals(
				"Unexpected sensor value for compass direction (= in portrait mode, deviation of screen-down-to-up-side (= positive y axis direction) from magnetic north in degrees, with z axis (pointing to sky) serving as rotation axis; positive direction = counter-clockwise turn seen from above; this is the angle between magnetic north and the device's y axis as it is displayed on a compass. For example, if the device's y axis points towards the magnetic north this value is 0, and if the device's y axis is pointing south this value is approaching 180 or -180. When the y axis is pointing west this value is 90 and when it is pointing east this value is -90)",
				expectedCompassDirection, formula3.interpretDouble(firstSprite), delta);

		assertEquals(
				"Unexpected sensor value for x inclination (= in portrait mode, deviation from screen-left-to-right-side (= x axis direction) horizontal inclination (range: -180 to +180 degrees; flat = 0); increasing values of x inclination = right border of screen pulled towards user, left border away = positive side of x axis gets lifted up)",
				expectedXInclination, formula4.interpretDouble(firstSprite), delta);

		assertEquals(
				"Unexpected sensor value for y inclination (= in portrait mode, deviation from screen-down-to-up-side (= y axis direction) horizontal inclination (range: -180 to +180 degrees; flat = 0); increasing values of y inclination = upper border of screen pulled towards user, lower border away = positive side of y axis gets lifted up)",
				expectedYInclination, formula5.interpretDouble(firstSprite), delta);

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
