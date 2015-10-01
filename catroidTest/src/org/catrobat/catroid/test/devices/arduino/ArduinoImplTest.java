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

package org.catrobat.catroid.test.devices.arduino;

import android.test.AndroidTestCase;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.firmata.FirmataMessage;
import org.catrobat.catroid.common.firmata.FirmataUtils;
import org.catrobat.catroid.devices.arduino.Arduino;
import org.catrobat.catroid.devices.arduino.ArduinoImpl;

public class ArduinoImplTest extends AndroidTestCase {

	private Arduino arduino;
	private ConnectionDataLogger logger;
	private FirmataUtils firmataUtils;

	private static final int PWM_PIN_GROUP_1 = 3;
	private static final int PWM_PIN_GROUP_2_MIN = 5;
	private static final int PWM_PIN_GROUP_2_MAX = 6;
	private static final int PWM_PIN_GROUP_3_MIN = 9;
	private static final int PWM_PIN_GROUP_3_MAX = 11;

	private static final int MIN_ANALOG_SENSOR_PIN = 0;
	private static final int MAX_ANALOG_SENSOR_PIN = 5;

	private static final int PWM_MODE = 3;

	private static final int SET_PIN_MODE_COMMAND = 0xF4;
	private static final int REPORT_ANALOG_PIN_COMMAND = 0xC0;
	private static final int ANALOG_MESSAGE_COMMAND = 0xE0;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		arduino = new ArduinoImpl();
		logger = ConnectionDataLogger.createLocalConnectionLogger();
		firmataUtils = new FirmataUtils(logger);
		arduino.setConnection(logger.getConnectionProxy());
	}

	@Override
	protected void tearDown() throws Exception {
		arduino.disconnect();
		logger.disconnectAndDestroy();
		super.tearDown();
	}

	private static final int ANALOG_PIN_NUMBER = 3;
	private static final int HIGH = 255;
	private static final int LOW = 0;

	public void testSetPinHigh() {
		arduino.initialise();
		doTestFirmataInitialization();

		arduino.setAnalogArduinoPin(ANALOG_PIN_NUMBER, HIGH);
		testAnalogPinValue(HIGH, ANALOG_PIN_NUMBER);
	}

	public void testSetPinLow() {
		arduino.initialise();
		doTestFirmataInitialization();

		arduino.setAnalogArduinoPin(ANALOG_PIN_NUMBER, LOW);
		testAnalogPinValue(LOW, ANALOG_PIN_NUMBER);
	}

	private void doTestFirmataInitialization() {
		for (int i = PWM_PIN_GROUP_1; i <= PWM_PIN_GROUP_1; ++i) {
			FirmataMessage m = firmataUtils.getSetPinModeMessage();

			assertEquals("Wrong Command, SET_PIN_MODE command expected", SET_PIN_MODE_COMMAND, m.getCommand());
			assertEquals("Wrong pin used to set pin mode", i, m.getPin());
			assertEquals("Wrong pin mode is used", PWM_MODE, m.getData());
		}

		for (int i = PWM_PIN_GROUP_2_MIN; i <= PWM_PIN_GROUP_2_MAX; ++i) {
			FirmataMessage m = firmataUtils.getSetPinModeMessage();

			assertEquals("Wrong Command, SET_PIN_MODE command expected", SET_PIN_MODE_COMMAND, m.getCommand());
			assertEquals("Wrong pin used to set pin mode", i, m.getPin());
			assertEquals("Wrong pin mode is used", PWM_MODE, m.getData());
		}

		for (int i = PWM_PIN_GROUP_3_MIN; i <= PWM_PIN_GROUP_3_MAX; ++i) {
			FirmataMessage m = firmataUtils.getSetPinModeMessage();

			assertEquals("Wrong Command, SET_PIN_MODE command expected", SET_PIN_MODE_COMMAND, m.getCommand());
			assertEquals("Wrong pin used to set pin mode", i, m.getPin());
			assertEquals(
					"Wrong pin mode is used", PWM_MODE, m.getData());
		}
		testReportAnalogPin(true);
	}

	private void testReportAnalogPin(boolean enable) {
		for (int i = MIN_ANALOG_SENSOR_PIN; i <= MAX_ANALOG_SENSOR_PIN; ++i) {
			FirmataMessage m = firmataUtils.getReportAnalogPinMessage();

			assertEquals("Wrong Command, REPORT_ANALOG_PIN command expected", REPORT_ANALOG_PIN_COMMAND, m.getCommand());
			assertEquals("Wrong pin used to set pin mode", i, m.getPin());
			assertEquals("Wrong pin mode is used", enable ? 1 : 0, m.getData());
		}
	}

	private void testAnalogPinValue(int value, int pin) {
		FirmataMessage m = firmataUtils.getAnalogMesageData();

		assertEquals("Wrong command, ANALOG_MESSAGE command expected",
				ANALOG_MESSAGE_COMMAND, m.getCommand());
		assertEquals("Wrong lsb speed", pin, m.getPin());
		assertEquals("Wrong msb speed", value, m.getData());
	}
}
