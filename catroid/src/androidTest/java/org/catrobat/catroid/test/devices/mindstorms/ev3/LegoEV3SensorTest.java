/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.test.devices.mindstorms.ev3;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.bluetooth.models.MindstormsEV3TestModel;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnectionImpl;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3Impl;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3ColorSensor;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3InfraredSensor;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3SensorMode;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3TouchSensor;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.HiTechnicColorSensor;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.TemperatureSensor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LegoEV3SensorTest {

	private static final byte PORT_NR_0 = 0;
	private static final byte PORT_NR_1 = 1;
	private static final byte PORT_NR_2 = 2;
	private static final byte PORT_NR_3 = 3;

	private LegoEV3 ev3;
	private MindstormsEV3TestModel ev3TestModel;
	ConnectionDataLogger logger;
	private MindstormsConnection mindstormsConnection;

	@Before
	public void setUp() throws Exception {

		Context applicationContext = InstrumentationRegistry.getContext().getApplicationContext();

		ev3TestModel = new MindstormsEV3TestModel();

		ev3 = new LegoEV3Impl(applicationContext);
		logger = ConnectionDataLogger.createLocalConnectionLoggerWithDeviceModel(ev3TestModel);
		ev3.setConnection(logger.getConnectionProxy());

		this.mindstormsConnection = new MindstormsConnectionImpl(logger.getConnectionProxy());
		mindstormsConnection.init();
	}

	@After
	public void tearDown() throws Exception {
		ev3.disconnect();
		mindstormsConnection.disconnect();
		logger.disconnectAndDestroy();
	}

	@Test
	public void testEV3IRSensor() {

		ev3TestModel.setSensorType(PORT_NR_0, EV3Sensor.Sensor.INFRARED);
		EV3Sensor sensor = new EV3InfraredSensor(PORT_NR_0, mindstormsConnection);
		sensor.getValue(); // will initialize the sensor
		assertTrue(ev3TestModel.isSensorActive(PORT_NR_0));

		ev3TestModel.generateSensorValue(PORT_NR_0);
		int sensorValue = (int) sensor.getValue();
		assertFalse((sensorValue < 0 || sensorValue > 100));

		final int expectedSensorValue = 12;
		ev3TestModel.setSensorValue(PORT_NR_0, expectedSensorValue);
		sensorValue = (int) sensor.getValue();
		assertEquals(expectedSensorValue, sensorValue);
	}

	@Test
	public void testEV3ColorSensor() {

		ev3TestModel.setSensorType(PORT_NR_1, EV3Sensor.Sensor.COLOR);
		EV3Sensor sensor = new EV3ColorSensor(PORT_NR_1, mindstormsConnection, EV3SensorMode.MODE2);
		sensor.getValue(); // will initialize the sensor
		assertTrue(ev3TestModel.isSensorActive(PORT_NR_1));

		ev3TestModel.generateSensorValue(PORT_NR_1);
		int sensorValue = (int) sensor.getValue();
		assertFalse((sensorValue < 0 || sensorValue > 7));

		final int expectedSensorValue = 3;
		ev3TestModel.setSensorValue(PORT_NR_1, expectedSensorValue);
		sensorValue = (int) sensor.getValue();
		assertEquals(expectedSensorValue, sensorValue);
	}

	@Test
	public void testEV3ColorSensorAmbient() {

		ev3TestModel.setSensorType(PORT_NR_2, EV3Sensor.Sensor.COLOR_AMBIENT);
		EV3Sensor sensor = new EV3ColorSensor(PORT_NR_2, mindstormsConnection, EV3SensorMode.MODE1);
		sensor.getValue(); // will initialize the sensor
		assertTrue(ev3TestModel.isSensorActive(PORT_NR_2));

		ev3TestModel.generateSensorValue(PORT_NR_2);
		int sensorValue = (int) sensor.getValue();
		assertFalse((sensorValue < 0 || sensorValue > 100));

		final int expectedSensorValue = 33;
		ev3TestModel.setSensorValue(PORT_NR_2, expectedSensorValue);
		sensorValue = (int) sensor.getValue();
		assertEquals(expectedSensorValue, sensorValue);
	}

	@Test
	public void testEV3ColorSensorReflected() {

		ev3TestModel.setSensorType(PORT_NR_3, EV3Sensor.Sensor.COLOR_REFLECT);
		EV3Sensor sensor = new EV3ColorSensor(PORT_NR_3, mindstormsConnection, EV3SensorMode.MODE0);
		sensor.getValue(); // will initialize the sensor
		assertTrue(ev3TestModel.isSensorActive(PORT_NR_3));

		ev3TestModel.generateSensorValue(PORT_NR_3);
		int sensorValue = (int) sensor.getValue();
		assertFalse((sensorValue < 0 || sensorValue > 100));

		final int expectedSensorValue = 42;
		ev3TestModel.setSensorValue(PORT_NR_3, expectedSensorValue);
		sensorValue = (int) sensor.getValue();
		assertEquals(expectedSensorValue, sensorValue);
	}

	@Test
	public void testEV3TouchSensor() {

		ev3TestModel.setSensorType(PORT_NR_3, EV3Sensor.Sensor.TOUCH);
		EV3Sensor sensor = new EV3TouchSensor(PORT_NR_3, mindstormsConnection);
		sensor.getValue(); // will initialize the sensor
		assertTrue(ev3TestModel.isSensorActive(PORT_NR_3));

		ev3TestModel.generateSensorValue(PORT_NR_3);
		int sensorValue = (int) sensor.getValue();
		assertFalse((sensorValue < 0 || sensorValue > 1));

		final int expectedSensorValue = 1;
		final int touchPercentValue = 82;
		ev3TestModel.setSensorValue(PORT_NR_3, touchPercentValue);
		sensorValue = (int) sensor.getValue();
		assertEquals(expectedSensorValue, sensorValue);
	}

	@Test
	public void testEV3TemperatureSensor() {
		ev3TestModel.setSensorType(PORT_NR_2, EV3Sensor.Sensor.NXT_TEMPERATURE_C);
		EV3Sensor tempSensC = new TemperatureSensor(PORT_NR_2, mindstormsConnection, EV3SensorMode.MODE0);
		tempSensC.getValue(); // will initialize the sensor
		assertTrue(ev3TestModel.isSensorActive(PORT_NR_2));

		assertEquals(ev3TestModel.getSensormode(PORT_NR_2), EV3SensorMode.MODE0.getByte());

		ev3TestModel.setSensorType(PORT_NR_3, EV3Sensor.Sensor.NXT_TEMPERATURE_F);
		EV3Sensor tempSensF = new TemperatureSensor(PORT_NR_3, mindstormsConnection, EV3SensorMode.MODE1);
		tempSensF.getValue(); // will initialize the sensor
		assertTrue(ev3TestModel.isSensorActive(PORT_NR_3));

		assertEquals(ev3TestModel.getSensormode(PORT_NR_3), EV3SensorMode.MODE1.getByte());

		ev3TestModel.generateSensorValue(PORT_NR_2);
		float sensorValueC = tempSensC.getValue();
		assertFalse((sensorValueC < -550 || sensorValueC > 1280));

		ev3TestModel.generateSensorValue(PORT_NR_3);
		float sensorValueF = tempSensF.getValue();
		assertFalse((sensorValueF < -670 || sensorValueF > 2624));

		final float expectedSensorValueC = 27.53f;
		ev3TestModel.setSensorValue(PORT_NR_2, expectedSensorValueC);
		float sensorValue = tempSensC.getValue();
		assertEquals(expectedSensorValueC, sensorValue);

		final float expectedSensorValueF = 49.53f;
		ev3TestModel.setSensorValue(PORT_NR_3, expectedSensorValueF);
		sensorValue = tempSensF.getValue();
		assertEquals(expectedSensorValueF, sensorValue);
	}

	@Test
	public void testEV3hitecColorSensor() {
		ev3TestModel.setSensorType(PORT_NR_1, EV3Sensor.Sensor.HT_NXT_COLOR);
		EV3Sensor sensor = new HiTechnicColorSensor(PORT_NR_1, mindstormsConnection, EV3SensorMode.MODE0);
		sensor.getValue(); // will initialize the sensor
		assertTrue(ev3TestModel.isSensorActive(PORT_NR_1));

		ev3TestModel.generateSensorValue(PORT_NR_1);
		float sensorValue = sensor.getValue();
		assertFalse((sensorValue < 0 || sensorValue > 17));

		final float expectedSensorValue = 13;
		ev3TestModel.setSensorValue(PORT_NR_1, expectedSensorValue);
		sensorValue = sensor.getValue();
		assertEquals(expectedSensorValue, sensorValue);
	}
}
