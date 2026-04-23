/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.firmata.FirmataMessage;
import org.catrobat.catroid.common.firmata.FirmataUtils;
import org.catrobat.catroid.devices.arduino.Arduino;
import org.catrobat.catroid.devices.arduino.ArduinoImpl;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import name.antonsmirnov.firmata.message.SetPinModeMessage;
import name.antonsmirnov.firmata.writer.AnalogMessageWriter;
import name.antonsmirnov.firmata.writer.DigitalMessageWriter;
import name.antonsmirnov.firmata.writer.ReportAnalogPinMessageWriter;
import name.antonsmirnov.firmata.writer.SetPinModeMessageWriter;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ArduinoImplTest {

	private Arduino arduino;
	private ConnectionDataLogger logger;
	private FirmataUtils firmataUtils;

	private static final int MIN_ANALOG_SENSOR_PIN = 0;
	private static final int MAX_ANALOG_SENSOR_PIN = 5;

	private static final int OUTPUT_MODE = SetPinModeMessage.PIN_MODE.OUTPUT.getMode();
	private static final int PWM_MODE = SetPinModeMessage.PIN_MODE.PWM.getMode();

	private static final int DIGITAL_MESSAGE_COMMAND = DigitalMessageWriter.COMMAND;
	private static final int ANALOG_MESSAGE_COMMAND = AnalogMessageWriter.COMMAND;
	private static final int REPORT_ANALOG_PIN_COMMAND = ReportAnalogPinMessageWriter.COMMAND;
	private static final int SET_PIN_MODE_COMMAND = SetPinModeMessageWriter.COMMAND;

	private static final int MAX_ANALOG_VALUE_FIRMATA = (1 << 14) - 1;

	@Before
	public void setUp() throws Exception {

		arduino = new ArduinoImpl();
		logger = ConnectionDataLogger.createLocalConnectionLogger();
		firmataUtils = new FirmataUtils(logger);
		arduino.setConnection(logger.getConnectionProxy());
	}

	@After
	public void tearDown() throws Exception {
		arduino.disconnect();
		logger.disconnectAndDestroy();
	}

	@Test
	public void testSetDigitalArduinoPinIndividually() throws MindstormsException {
		arduino.initialise();
		doTestFirmataInitialization();

		// test set and clear of each individual pin
		for (int i = 0; i < ArduinoImpl.NUMBER_OF_DIGITAL_PINS; i++) {
			arduino.setDigitalArduinoPin(i, 1);
			testDigital(1 << ArduinoImpl.getIndexOfPinOnPort(i), i);
			arduino.setDigitalArduinoPin(i, 0);
			testDigital(0x00, i);
		}
	}

	@Test
	public void testSetDigitalArduinoPinInterleavedOnPort() throws MindstormsException {
		arduino.initialise();
		doTestFirmataInitialization();

		// test interleaved set and clear of pins of a port
		int pin;
		for (int i = 0; i < ArduinoImpl.NUMBER_OF_DIGITAL_PORTS; i++) {
			int offset = i * ArduinoImpl.PINS_IN_A_PORT;
			pin = offset + 0;
			arduino.setDigitalArduinoPin(pin, 1);
			testDigital(0x01, pin);
			arduino.setDigitalArduinoPin(pin, 0);
			testDigital(0x00, pin);
			pin = offset + 2;
			arduino.setDigitalArduinoPin(pin, 1);
			testDigital(0x04, pin);
			pin = offset + 1;
			arduino.setDigitalArduinoPin(pin, 1);
			testDigital(0x06, pin);
			pin = offset + 5;
			arduino.setDigitalArduinoPin(pin, 1);
			testDigital(0x26, pin);
			pin = offset + 4;
			arduino.setDigitalArduinoPin(pin, 1);
			testDigital(0x36, pin);
			pin = offset + 2;
			arduino.setDigitalArduinoPin(pin, 0);
			testDigital(0x32, pin);
			pin = offset + 3;
			arduino.setDigitalArduinoPin(pin, 1);
			testDigital(0x3A, pin);

			if (ArduinoImpl.NUMBER_OF_DIGITAL_PINS > offset + 7) {
				pin = offset + 6;
				arduino.setDigitalArduinoPin(pin, 1);
				testDigital(0x7A, pin);
				pin = offset + 7;
				arduino.setDigitalArduinoPin(pin, 1);
				testDigital(0xFA, pin);
				pin = offset + 6;
				arduino.setDigitalArduinoPin(pin, 0);
				testDigital(0xBA, pin);
				pin = offset + 4;
				arduino.setDigitalArduinoPin(pin, 0);
				testDigital(0xAA, pin);
				pin = offset + 5;
				arduino.setDigitalArduinoPin(pin, 0);
				testDigital(0x8A, pin);
			}
		}
		for (int i = 0; i < ArduinoImpl.NUMBER_OF_DIGITAL_PORTS; i++) {
			int offset = i * ArduinoImpl.PINS_IN_A_PORT;
			arduino.setDigitalArduinoPin(offset + 0, 1);
			if (ArduinoImpl.NUMBER_OF_DIGITAL_PINS > offset + 7) {
				testDigital(0x8B, offset);
			} else {
				testDigital(0x3B, offset);
			}
		}
	}

	@Test
	public void testSetDigitalArduinoPinInterleavedBetweenPorts() throws MindstormsException {
		arduino.initialise();
		doTestFirmataInitialization();

		// test interleaved set and clear of pins of different ports
		arduino.setDigitalArduinoPin(7, 1);
		testDigital(0x80, 7);
		arduino.setDigitalArduinoPin(13, 1);
		testDigital(0x20, 13);
		arduino.setDigitalArduinoPin(7, 0);
		testDigital(0x00, 7);
		arduino.setDigitalArduinoPin(8, 1);
		testDigital(0x21, 8);
	}

	@Test
	public void testSetAnalogArduinoPin() throws MindstormsException {
		arduino.initialise();
		doTestFirmataInitialization();

		int pin = 0;
		for (int i = 0; i < ArduinoImpl.NUMBER_OF_ANALOG_PINS; i++) {
			pin = i;
			testAnalog(pin, 0);
			testAnalog(pin, 1);
			testAnalog(pin, 99);
			testAnalog(pin, 100);
			testAnalog(pin, 101);
			testAnalog(pin, 255);
			testAnalog(pin, 256);
			testAnalog(pin, 1024);
			testAnalog(pin, MAX_ANALOG_VALUE_FIRMATA);
		}

		testAnalogOutOfRange(pin, MAX_ANALOG_VALUE_FIRMATA + 1, 0);
		testAnalogOutOfRange(pin, -1, MAX_ANALOG_VALUE_FIRMATA);
	}

	@Test
	public void testGetDigitalArduinoPin() throws MindstormsException {
		arduino.initialise();
		doTestFirmataInitialization();

		//TODO
	}

	@Test
	public void testGetAnalogArduinoPin() throws MindstormsException {
		arduino.initialise();
		doTestFirmataInitialization();

		//TODO
	}

	private void doTestFirmataInitialization() {
		for (int pin : ArduinoImpl.PWM_PINS) {
			FirmataMessage m = firmataUtils.getSetPinModeMessage();

			assertEquals(SET_PIN_MODE_COMMAND, m.getCommand());
			assertEquals(pin, m.getPin());
			assertEquals(PWM_MODE, m.getData());
		}

		testReportAnalogPin(true);
	}

	private void testReportAnalogPin(boolean enable) {
		for (int i = MIN_ANALOG_SENSOR_PIN; i <= MAX_ANALOG_SENSOR_PIN; i++) {
			FirmataMessage m = firmataUtils.getReportAnalogPinMessage();

			assertEquals(REPORT_ANALOG_PIN_COMMAND, m.getCommand());
			assertEquals(i, m.getPin());
			assertEquals(enable ? 1 : 0, m.getData());
		}
	}

	private void testDigital(int portValue, int pin) {
		FirmataMessage m = firmataUtils.getSetPinModeMessage();
		assertEquals(SET_PIN_MODE_COMMAND, m.getCommand());
		assertEquals(pin, m.getPin());
		assertEquals(OUTPUT_MODE, m.getData());

		m = firmataUtils.getDigitalMessageData();
		assertEquals(DIGITAL_MESSAGE_COMMAND, m.getCommand());
		assertEquals(ArduinoImpl.getPortFromPin(pin), m.getPin());
		assertEquals(portValue, m.getData());
	}

	private void checkAnalog(int pin, int expectedValue) {
		FirmataMessage m = firmataUtils.getSetPinModeMessage();
		assertEquals(SET_PIN_MODE_COMMAND, m.getCommand());
		assertEquals(pin, m.getPin());
		assertEquals(PWM_MODE, m.getData());

		m = firmataUtils.getAnalogMessageData();
		assertEquals(ANALOG_MESSAGE_COMMAND, m.getCommand());
		assertEquals(pin, m.getPin());
		assertEquals(expectedValue, m.getData());
	}

	private void testAnalog(int pin, int value) {
		arduino.setAnalogArduinoPin(pin, value);
		checkAnalog(pin, value);
	}

	private void testAnalogOutOfRange(int pin, int value, int expectedValue) {
		arduino.setAnalogArduinoPin(pin, value);
		checkAnalog(pin, expectedValue);
	}
}
