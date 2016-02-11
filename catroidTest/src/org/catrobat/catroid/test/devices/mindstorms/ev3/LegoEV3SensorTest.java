/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.test.AndroidTestCase;

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

public class LegoEV3SensorTest extends AndroidTestCase {

	private static final byte PORT_NR_0 = 0;
	private static final byte PORT_NR_1 = 1;
	private static final byte PORT_NR_2 = 2;
	private static final byte PORT_NR_3 = 3;

	private LegoEV3 ev3;
	private MindstormsEV3TestModel ev3TestModel;
	ConnectionDataLogger logger;
	private MindstormsConnection mindstormsConnection;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Context applicationContext = this.getContext().getApplicationContext();

		ev3TestModel = new MindstormsEV3TestModel();

		ev3 = new LegoEV3Impl(applicationContext);
		logger = ConnectionDataLogger.createLocalConnectionLoggerWithDeviceModel(ev3TestModel);
		ev3.setConnection(logger.getConnectionProxy());

		this.mindstormsConnection = new MindstormsConnectionImpl(logger.getConnectionProxy());
		mindstormsConnection.init();
	}

	@Override
	protected void tearDown() throws Exception {
		ev3.disconnect();
		mindstormsConnection.disconnect();
		logger.disconnectAndDestroy();
		super.tearDown();
	}

	public void testEV3IRSensor() {

		ev3TestModel.setSensorType(PORT_NR_0, EV3Sensor.Sensor.INFRARED);
		EV3Sensor sensor = new EV3InfraredSensor(PORT_NR_0, mindstormsConnection);
		sensor.getValue(); // will initialize the sensor
		assertTrue("Infrared Sensor was not initialized or deactivated", ev3TestModel.isSensorActive(PORT_NR_0));

		ev3TestModel.generateSensorValue(PORT_NR_0);
		int sensorValue = sensor.getValue();
		assertFalse("Infrared Sensor Value was not in range 0-100", (sensorValue < 0 || sensorValue > 100));

		final int expectedSensorValue = 12;
		ev3TestModel.setSensorValue(PORT_NR_0, expectedSensorValue);
		sensorValue = sensor.getValue();
		assertEquals("Received wrong IR sensor value", expectedSensorValue, sensorValue);
	}

	public void testEV3ColorSensor() {

		ev3TestModel.setSensorType(PORT_NR_1, EV3Sensor.Sensor.COLOR);
		EV3Sensor sensor = new EV3ColorSensor(PORT_NR_1, mindstormsConnection, EV3SensorMode.MODE2);
		sensor.getValue(); // will initialize the sensor
		assertTrue("Color Sensor was not initialized or deactivated", ev3TestModel.isSensorActive(PORT_NR_1));

		ev3TestModel.generateSensorValue(PORT_NR_1);
		int sensorValue = sensor.getValue();
		assertFalse("Color Sensor Value (Color Mode) not in range 0 - 7", (sensorValue < 0 || sensorValue > 7));

		final int expectedSensorValue = 3;
		ev3TestModel.setSensorValue(PORT_NR_1, expectedSensorValue);
		sensorValue = sensor.getValue();
		assertEquals("Received wrong color sensor value(color mode)", expectedSensorValue, sensorValue);
	}

	public void testEV3ColorSensorAmbient() {

		ev3TestModel.setSensorType(PORT_NR_2, EV3Sensor.Sensor.COLOR_AMBIENT);
		EV3Sensor sensor = new EV3ColorSensor(PORT_NR_2, mindstormsConnection, EV3SensorMode.MODE1);
		sensor.getValue(); // will initialize the sensor
		assertTrue("Color Ambient Sensor was not initialized or deactivated", ev3TestModel.isSensorActive(PORT_NR_2));

		ev3TestModel.generateSensorValue(PORT_NR_2);
		int sensorValue = sensor.getValue();
		assertFalse("Color Sensor Value(Ambient) was not in range 0-100", (sensorValue < 0 || sensorValue > 100));

		final int expectedSensorValue = 33;
		ev3TestModel.setSensorValue(PORT_NR_2, expectedSensorValue);
		sensorValue = sensor.getValue();
		assertEquals("Received wrong color sensor value(Ambient)", expectedSensorValue, sensorValue);
	}

	public void testEV3ColorSensorReflected() {

		ev3TestModel.setSensorType(PORT_NR_3, EV3Sensor.Sensor.COLOR_REFLECT);
		EV3Sensor sensor = new EV3ColorSensor(PORT_NR_3, mindstormsConnection, EV3SensorMode.MODE0);
		sensor.getValue(); // will initialize the sensor
		assertTrue("Color Reflected Sensor was not initialized or deactivated", ev3TestModel.isSensorActive(PORT_NR_3));

		ev3TestModel.generateSensorValue(PORT_NR_3);
		int sensorValue = sensor.getValue();
		assertFalse("Color Sensor Value(Reflected) was not in range 0-100", (sensorValue < 0 || sensorValue > 100));

		final int expectedSensorValue = 42;
		ev3TestModel.setSensorValue(PORT_NR_3, expectedSensorValue);
		sensorValue = sensor.getValue();
		assertEquals("Received wrong color sensor value(reflected)", expectedSensorValue, sensorValue);
	}

	public void testEV3TouchSensor() {

		ev3TestModel.setSensorType(PORT_NR_3, EV3Sensor.Sensor.TOUCH);
		EV3Sensor sensor = new EV3TouchSensor(PORT_NR_3, mindstormsConnection);
		sensor.getValue(); // will initialize the sensor
		assertTrue("Touch Sensor was not initialized or deactivated", ev3TestModel.isSensorActive(PORT_NR_3));

		ev3TestModel.generateSensorValue(PORT_NR_3);
		int sensorValue = sensor.getValue();
		assertFalse("Touch Sensor Value was not in range 0-1", (sensorValue < 0 || sensorValue > 1));

		final int expectedSensorValue = 1;
		final int touchPercentValue = 82;
		ev3TestModel.setSensorValue(PORT_NR_3, touchPercentValue);
		sensorValue = sensor.getValue();
		assertEquals("Received wrong touch sensor value", expectedSensorValue, sensorValue);
	}
}
