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

package org.catrobat.catroid.test.devices.phiro;

import com.google.common.base.Stopwatch;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.firmata.FirmataMessage;
import org.catrobat.catroid.common.firmata.FirmataUtils;
import org.catrobat.catroid.devices.arduino.phiro.Phiro;
import org.catrobat.catroid.devices.arduino.phiro.PhiroImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PhiroImplTest {

	private Phiro phiro;
	private ConnectionDataLogger logger;
	private FirmataUtils firmataUtils;

	private static final int PIN_SPEAKER_OUT = 3;

	private static final int PIN_RGB_RED_RIGHT = 4;
	private static final int PIN_RGB_GREEN_RIGHT = 5;
	private static final int PIN_RGB_BLUE_RIGHT = 6;

	private static final int PIN_RGB_RED_LEFT = 7;
	private static final int PIN_RGB_GREEN_LEFT = 8;
	private static final int PIN_RGB_BLUE_LEFT = 9;

	private static final int PIN_LEFT_MOTOR_SPEED = 10;
	private static final int PIN_LEFT_MOTOR_FORWARD_BACKWARD = 11;

	private static final int PIN_RIGHT_MOTOR_SPEED = 12;

	private static final int MIN_PWM_PIN = 2;
	private static final int MAX_PWM_PIN = 13;

	private static final int MIN_SENSOR_PIN = 0;
	private static final int MAX_SENSOR_PIN = 5;

	private static final int PWM_MODE = 3;

	private static final int SET_PIN_MODE_COMMAND = 0xF4;
	private static final int REPORT_ANALOG_PIN_COMMAND = 0xC0;
	private static final int ANALOG_MESSAGE_COMMAND = 0xE0;

	@Before
	public void setUp() throws Exception {

		phiro = new PhiroImpl();
		logger = ConnectionDataLogger.createLocalConnectionLogger();
		firmataUtils = new FirmataUtils(logger);
		phiro.setConnection(logger.getConnectionProxy());
		phiro.initialise();
	}

	@After
	public void tearDown() throws Exception {
		phiro.disconnect();
		logger.disconnectAndDestroy();
	}

	private static final int SPEED_IN_PERCENT = 42;

	@Test
	public void testMoveLeftMotorForward() {
		doTestFirmataInitialization();

		phiro.moveLeftMotorForward(SPEED_IN_PERCENT);
		testSpeed(SPEED_IN_PERCENT, PIN_LEFT_MOTOR_SPEED);
	}

	@Test
	public void testMoveLeftMotorBackward() {
		doTestFirmataInitialization();

		phiro.moveLeftMotorBackward(SPEED_IN_PERCENT);
		testSpeed(SPEED_IN_PERCENT, PIN_LEFT_MOTOR_SPEED);
	}

	@Test
	public void testMoveRightMotorForward() {
		doTestFirmataInitialization();

		phiro.moveRightMotorForward(SPEED_IN_PERCENT);
		testSpeed(SPEED_IN_PERCENT, PIN_RIGHT_MOTOR_SPEED);
	}

	@Test
	public void testMoveRightMotorBackward() {
		doTestFirmataInitialization();

		phiro.moveRightMotorBackward(SPEED_IN_PERCENT);
		testSpeed(SPEED_IN_PERCENT, PIN_RIGHT_MOTOR_SPEED);
	}

	@Test
	public void testStopLeftMotor() {
		doTestFirmataInitialization();

		phiro.stopLeftMotor();
		testSpeed(0, PIN_LEFT_MOTOR_SPEED);
		testSpeed(0, PIN_LEFT_MOTOR_FORWARD_BACKWARD);
	}

	@Test
	public void testStopRightMotor() {
		doTestFirmataInitialization();

		phiro.stopRightMotor();
		testSpeed(0, PIN_RIGHT_MOTOR_SPEED);
	}

	@Test
	public void testStopAllMovements() {
		doTestFirmataInitialization();

		phiro.stopAllMovements();
		testSpeed(0, PIN_LEFT_MOTOR_SPEED);
		testSpeed(0, PIN_LEFT_MOTOR_FORWARD_BACKWARD);
		testSpeed(0, PIN_RIGHT_MOTOR_SPEED);
	}

	@Test
	public void testSetLeftRGBLightColor() {
		doTestFirmataInitialization();

		int red = 242;
		int green = 0;
		int blue = 3;

		phiro.setLeftRGBLightColor(red, green, blue);
		testLight(red, PIN_RGB_RED_LEFT);
		testLight(green, PIN_RGB_GREEN_LEFT);
		testLight(blue, PIN_RGB_BLUE_LEFT);
	}

	@Test
	public void testSetRightRGBLightColor() {
		doTestFirmataInitialization();

		int red = 242;
		int green = 1;
		int blue = 3;

		phiro.setRightRGBLightColor(red, green, blue);
		testLight(red, PIN_RGB_RED_RIGHT);
		testLight(green, PIN_RGB_GREEN_RIGHT);
		testLight(blue, PIN_RGB_BLUE_RIGHT);
	}

	@Test
	public void testPlayTone() throws InterruptedException {
		doTestFirmataInitialization();

		int tone = 294;
		int durationInSeconds = 1;

		phiro.playTone(tone, durationInSeconds);

		FirmataMessage m = firmataUtils.getAnalogMessageData();

		assertEquals(ANALOG_MESSAGE_COMMAND, m.getCommand());
		assertEquals(PIN_SPEAKER_OUT, m.getPin());
		assertEquals(tone, m.getData());

		Stopwatch stopwatch = Stopwatch.createStarted();
		while (stopwatch.elapsed(TimeUnit.SECONDS) < durationInSeconds) {
			assertEquals(0, logger.getSentMessages(0).size());
			Thread.sleep(durationInSeconds * 100);
		}

		m = firmataUtils.getAnalogMessageData();

		assertEquals(ANALOG_MESSAGE_COMMAND, m.getCommand());
		assertEquals(PIN_SPEAKER_OUT, m.getPin());
		assertEquals(0, m.getData());
	}

	private void doTestFirmataInitialization() {
		for (int i = MIN_PWM_PIN; i <= MAX_PWM_PIN; ++i) {
			FirmataMessage m = firmataUtils.getSetPinModeMessage();

			assertEquals(SET_PIN_MODE_COMMAND, m.getCommand());
			assertEquals(i, m.getPin());
			assertEquals(PWM_MODE, m.getData());
		}

		testReportAnalogPin(true);
	}

	private void testReportAnalogPin(boolean enable) {
		for (int i = MIN_SENSOR_PIN; i <= MAX_SENSOR_PIN; ++i) {
			FirmataMessage m = firmataUtils.getReportAnalogPinMessage();

			assertEquals(REPORT_ANALOG_PIN_COMMAND, m.getCommand());
			assertEquals(i, m.getPin());
			assertEquals(enable ? 1 : 0, m.getData());
		}
	}

	private void testSpeed(int speedInPercent, int pin) {
		int speed = percentToSpeed(speedInPercent);

		FirmataMessage m = firmataUtils.getAnalogMessageData();

		assertEquals(ANALOG_MESSAGE_COMMAND, m.getCommand());
		assertEquals(pin, m.getPin());
		assertEquals(speed, m.getData());
	}

	private void testLight(int color, int pin) {
		FirmataMessage m = firmataUtils.getAnalogMessageData();

		assertEquals(ANALOG_MESSAGE_COMMAND, m.getCommand());
		assertEquals(pin, m.getPin());
		assertEquals(color, m.getData());
	}

	private int percentToSpeed(int percent) {
		if (percent <= 0) {
			return 0;
		}
		if (percent >= 100) {
			return 255;
		}

		return (int) (percent * 2.55);
	}
}
