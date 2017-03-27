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

package org.catrobat.catroid.test.devices.arduino;

import android.test.AndroidTestCase;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.firmata.FirmataMessage;
import org.catrobat.catroid.common.firmata.FirmataUtils;
import org.catrobat.catroid.devices.arduino.Arduino;
import org.catrobat.catroid.devices.arduino.ArduinoImpl;

import java.util.Random;

import name.antonsmirnov.firmata.message.SetPinModeMessage;
import name.antonsmirnov.firmata.writer.AnalogMessageWriter;
import name.antonsmirnov.firmata.writer.DigitalMessageWriter;
import name.antonsmirnov.firmata.writer.ReportAnalogPinMessageWriter;
import name.antonsmirnov.firmata.writer.SetPinModeMessageWriter;

public class ArduinoImplTest extends AndroidTestCase {

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

	public void testSetDigitalArduinoPinIndividually() {
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

	public void testSetDigitalArduinoPinInterleavedOnPort() {
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

	public void testSetDigitalArduinoPinInterleavedBetweenPorts() {
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

	public void testSetAnalogArduinoPin() {
		arduino.initialise();
		doTestFirmataInitialization();

		int pin;
		int value;
		for (int i = 0; i < ArduinoImpl.NUMBER_OF_ANALOG_PINS; i++) {
			pin = i;
			value = 0;
			arduino.setAnalogArduinoPin(pin, value);
			testAnalog(value, pin);
			value = 100;
			arduino.setAnalogArduinoPin(pin, value);
			testAnalog(value, pin);
			value = 1;
			arduino.setAnalogArduinoPin(pin, value);
			testAnalog(value, pin);
			value = 99;
			arduino.setAnalogArduinoPin(pin, value);
			testAnalog(value, pin);
			value = 54;
			arduino.setAnalogArduinoPin(pin, value);
			testAnalog(value, pin);
			value = 42;
			arduino.setAnalogArduinoPin(pin, value);
			testAnalog(value, pin);
		}
	}

	public void testSetAnalogArduinoPinRandomly() {
		arduino.initialise();
		doTestFirmataInitialization();

		int pin;
		int value;
		Random r = new Random();
		for (int i = 0; i < 50; i++) {
			pin = r.nextInt(ArduinoImpl.NUMBER_OF_ANALOG_PINS);
			value = r.nextInt(101);
			arduino.setAnalogArduinoPin(pin, value);
			testAnalog(value, pin);
		}
	}

	public void testGetDigitalArduinoPin() {
		arduino.initialise();
		doTestFirmataInitialization();

		//TODO
	}

	public void testGetAnalogArduinoPin() {
		arduino.initialise();
		doTestFirmataInitialization();

		//TODO
	}

	private void doTestFirmataInitialization() {
		for (int pin : ArduinoImpl.PWM_PINS) {
			FirmataMessage m = firmataUtils.getSetPinModeMessage();

			assertEquals("Wrong Command, SET_PIN_MODE command expected", SET_PIN_MODE_COMMAND, m.getCommand());
			assertEquals("Wrong pin used to set pin mode", pin, m.getPin());
			assertEquals("Wrong pin mode is used", PWM_MODE, m.getData());
		}

		testReportAnalogPin(true);
	}

	private void testReportAnalogPin(boolean enable) {
		for (int i = MIN_ANALOG_SENSOR_PIN; i <= MAX_ANALOG_SENSOR_PIN; i++) {
			FirmataMessage m = firmataUtils.getReportAnalogPinMessage();

			assertEquals("Wrong Command, REPORT_ANALOG_PIN command expected", REPORT_ANALOG_PIN_COMMAND, m.getCommand());
			assertEquals("Wrong pin used to set pin to be reported", i, m.getPin());
			assertEquals("Wrong enable bit used", enable ? 1 : 0, m.getData());
		}
	}

	private void testDigital(int portValue, int pin) {
		FirmataMessage m = firmataUtils.getSetPinModeMessage();
		assertEquals("Wrong Command, SET_PIN_MODE command expected", SET_PIN_MODE_COMMAND, m.getCommand());
		assertEquals("Wrong pin used to set pin mode", pin, m.getPin());
		assertEquals("Wrong pin mode is used", OUTPUT_MODE, m.getData());

		m = firmataUtils.getDigitalMessageData();
		assertEquals("Wrong command, DIGITAL_MESSAGE command expected",
				DIGITAL_MESSAGE_COMMAND, m.getCommand());
		assertEquals("Wrong port", ArduinoImpl.getPortFromPin(pin), m.getPin());
		assertEquals("Wrong port value", portValue, m.getData());
	}

	private void testAnalog(int percent, int pin) {
		FirmataMessage m = firmataUtils.getSetPinModeMessage();
		assertEquals("Wrong Command, SET_PIN_MODE command expected", SET_PIN_MODE_COMMAND, m.getCommand());
		assertEquals("Wrong pin used to set pin mode", pin, m.getPin());
		assertEquals("Wrong pin mode is used", PWM_MODE, m.getData());

		m = firmataUtils.getAnalogMessageData();
		assertEquals("Wrong command, ANALOG_MESSAGE command expected",
				ANALOG_MESSAGE_COMMAND, m.getCommand());
		assertEquals("Wrong pin", pin, m.getPin());
		assertEquals("Wrong value", percentToValue(percent), m.getData());
	}

	private int percentToValue(int percent) {
		if (percent <= 0) {
			return 0;
		}
		if (percent >= 100) {
			return 255;
		}

		return (int) (percent * 2.55);
	}
}
