/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

package org.catrobat.catroid.devices.arduino.phiropro;

import android.util.Log;

import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper;
import org.catrobat.catroid.devices.arduino.common.firmata.Firmata;
import org.catrobat.catroid.devices.arduino.common.firmata.message.AnalogMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.I2cRequestMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.Message;
import org.catrobat.catroid.devices.arduino.common.firmata.message.ReportAnalogCapabilityMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.ReportAnalogPinMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.ReportFirmwareVersionMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.SetPinModeMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.SysexByteMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.SysexMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.StreamingSerialAdapter;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class PhiroProImpl implements PhiroPro {

	private static final UUID PHIRO_PRO_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String TAG = PhiroProImpl.class.getSimpleName();

	private static final int PIN_SPEAKER_OUT = 3;

	private static final int PIN_RGB_RED_LEFT = 4;
	private static final int PIN_RGB_GREEN_LEFT = 5;
	private static final int PIN_RGB_BLUE_LEFT = 6;

	private static final int PIN_RGB_RED_RIGHT = 7;
	private static final int PIN_RGB_GREEN_RIGHT = 8;
	private static final int PIN_RGB_BLUE_RIGHT = 9;

	//private static final int PIN_LEFT_MOTOR_BACKWARD = 10;
	//private static final int PIN_LEFT_MOTOR_FORWARD = 11;

	private static final int PIN_LEFT_MOTOR_BACKWARD = 11;
	private static final int PIN_LEFT_MOTOR_FORWARD = 10;

	private static final int PIN_RIGHT_MOTOR_FORWARD = 12;
	private static final int PIN_RIGHT_MOTOR_BACKWARD = 13;

	private static final int MIN_PWM_PIN = 3;
	private static final int MAX_PWM_PIN = 13;

	public static final int PIN_SENSOR_SIDE_RIGHT = 0;
	public static final int PIN_SENSOR_FRONT_RIGHT = 1;
	public static final int PIN_SENSOR_BOTTOM_RIGHT = 2;
	public static final int PIN_SENSOR_BOTTOM_LEFT = 3;
	public static final int PIN_SENSOR_FRONT_LEFT = 4;
	public static final int PIN_SENSOR_SIDE_LEFT = 5;

	private static final int MIN_SENSOR_PIN = 0;
	private static final int MAX_SENSOR_PIN = 5;

	private BluetoothConnection btConnection;
	private Firmata firmata;
	private boolean isInitialized = false;
	private boolean isReportingSensorData = false;

	private PhiroProListener phiroProListener;


	@Override
	public synchronized void playTone(int selectedTone, int durationInSeconds) {
		sendAnalogFirmataMessage(PIN_SPEAKER_OUT, selectedTone);
		/*
		byte[] data  = {
				(byte)0x0,
				(byte)0x3,
				(byte)(selectedTone & 0x7F),
				(byte)(selectedTone >> 7),
				(byte)(durationInSeconds & 0x7F),
				(byte)(durationInSeconds >> 7)};
		Message message = new SysexByteMessage(0x5F, data);
		sendFirmataMessage(message);
		*/
		if (currentStopPlayToneTask != null) {
			currentStopPlayToneTask.cancel();
		}
		currentStopPlayToneTask = new StopPlayToneTask();
		timer.schedule(currentStopPlayToneTask, durationInSeconds * 1000);
	}

	Timer timer = new Timer();
	TimerTask currentStopPlayToneTask = null;
	private class StopPlayToneTask extends TimerTask {
		@Override
		public void run() {
			sendAnalogFirmataMessage(PIN_SPEAKER_OUT, 0);
		}
	}

	@Override
	public void moveLeftMotorForward(int speedInPercent) {
		sendAnalogFirmataMessage(PIN_LEFT_MOTOR_FORWARD, percentToSpeed(speedInPercent));
	}

	@Override
	public void moveLeftMotorBackward(int speedInPercent) {
		sendAnalogFirmataMessage(PIN_LEFT_MOTOR_BACKWARD, percentToSpeed(speedInPercent));
	}

	@Override
	public void moveRightMotorForward(int speedInPercent) {
		sendAnalogFirmataMessage(PIN_RIGHT_MOTOR_FORWARD, percentToSpeed(speedInPercent));
	}

	@Override
	public void moveRightMotorBackward(int speedInPercent) {
		sendAnalogFirmataMessage(PIN_RIGHT_MOTOR_BACKWARD, percentToSpeed(speedInPercent));
	}

	@Override
	public void stopLeftMotor() {
		moveLeftMotorForward(0);
		moveLeftMotorBackward(0);
	}

	@Override
	public void stopRightMotor() {
		moveRightMotorForward(0);
		moveRightMotorBackward(0);
	}

	@Override
	public void stopAllMovements() {
		stopLeftMotor();
		stopRightMotor();
	}

	@Override
	public void setLeftRGBLightColor(int red, int green, int blue) {
		red = checkRBGValue(red);
		green = checkRBGValue(green);
		blue = checkRBGValue(blue);

		sendFirmataMessage(new AnalogMessage(PIN_RGB_RED_LEFT, red));
		sendFirmataMessage(new AnalogMessage(PIN_RGB_GREEN_LEFT, green));
		sendFirmataMessage(new AnalogMessage(PIN_RGB_BLUE_LEFT, blue));
	}

	@Override
	public void setRightRGBLightColor(int red, int green, int blue) {
		red = checkRBGValue(red);
		green = checkRBGValue(green);
		blue = checkRBGValue(blue);

		sendFirmataMessage(new AnalogMessage(PIN_RGB_RED_RIGHT, red));
		sendFirmataMessage(new AnalogMessage(PIN_RGB_GREEN_RIGHT, green));
		sendFirmataMessage(new AnalogMessage(PIN_RGB_BLUE_RIGHT, blue));
	}

	private int percentToSpeed(int percent) {
		if (percent <= 0) {
			return 0;
		}
		if (percent >= 100) {
			return 255;
		}

		return (int)(percent * 2.55);
	}

	private int checkRBGValue(int rgbValue) {
		if (rgbValue > 255) {
			return 255;
		}

		if (rgbValue < 0) {
			return 0;
		}

		return rgbValue;
	}

	@Override
	public String getName() {
		return "PhiroPro";
	}

	@Override
	public Class<? extends BluetoothDevice> getDeviceType() {
		return BluetoothDevice.PHIRO_PRO;
	}

	@Override
	public void setConnection(BluetoothConnection connection) {
		this.btConnection = connection;
	}

	@Override
	public void disconnect() {
		try {
			if (firmata != null) {
				resetPins();
				this.reportSensorData(false);
				firmata.clearListeners();
				firmata.getSerial().stop();
				firmata = null;
			}
		} catch (SerialException e) {
			Log.d(TAG, "Error stop phiro pro serial");
		}
	}

	@Override
	public boolean isAlive() {
		try {
			firmata.send(new ReportFirmwareVersionMessage());
			return true;
		} catch (SerialException e) {
			return false;
		}
	}

	public void reportFirmwareVersion() {
		try {
			firmata.send(new ReportFirmwareVersionMessage());
		} catch (SerialException e) {
			Log.d(TAG, "Firmata Serial error, cannot send message.");
		}
	}

	@Override
	public int getSensorValue(Sensors sensor) {
		switch (sensor) {
			case PHIRO_PRO_BOTTOM_LEFT:
				return phiroProListener.getBottomLeftSensor();
			case PHIRO_PRO_BOTTOM_RIGHT:
				return phiroProListener.getBottomRightSensor();
			case PHIRO_PRO_FRONT_LEFT:
				return phiroProListener.getFrontLeftSensor();
			case PHIRO_PRO_FRONT_RIGHT:
				return phiroProListener.getFrontRightSensor();
			case PHIRO_PRO_SIDE_LEFT:
				return phiroProListener.getSideLeftSensor();
			case PHIRO_PRO_SIDE_RIGHT:
				return phiroProListener.getSideRightSensor();
		}

		return 0;
	}

	public int getSensorValue(int numberFromBrick) {
		switch (numberFromBrick) {
			case 0:
				return phiroProListener.getSideRightSensor();
			case 1:
				return phiroProListener.getFrontRightSensor();
			case 2:
				return phiroProListener.getBottomRightSensor();
			case 3:
				return phiroProListener.getBottomLeftSensor();
			case 4:
				return phiroProListener.getFrontLeftSensor();
			case 5:
				return phiroProListener.getSideRightSensor();
		}

		return 0;
	}

	@Override
	public UUID getBluetoothDeviceUUID() {
		return PHIRO_PRO_UUID;
	}

	@Override
	public void initialise() {
		if (isInitialized) {
			return;
		}

		try {
			tryInitialize();
			isInitialized = true;
		} catch (SerialException e) {
			Log.d(TAG, "Error starting firmata serials");
		} catch (IOException e) {
			Log.d(TAG, "Error opening streams");
		}
	}

	private void tryInitialize() throws IOException, SerialException {
		ISerial serial = new StreamingSerialAdapter(btConnection.getInputStream(), btConnection.getOutputStream());

		firmata = new Firmata(serial);

		phiroProListener = new PhiroProListener();
		firmata.addListener(phiroProListener);

		firmata.getSerial().start();

		for (int pin = MIN_PWM_PIN; pin <= MAX_PWM_PIN; ++pin) {
			sendFirmataMessage(new SetPinModeMessage(pin, SetPinModeMessage.PIN_MODE.PWM.getMode()));
		}

		reportSensorData(true);
	}

	private void reportSensorData(boolean report) {
		if (isReportingSensorData == report) {
			return;
		}

		isReportingSensorData = report;

		for (int pin = MIN_SENSOR_PIN; pin <= MAX_SENSOR_PIN; ++pin) {
			// sendFirmataMessage(new SetPinModeMessage(? maybe 54 ?, SetPinModeMessage.PIN_MODE.ANALOG.getMode())); // --> not needed
			sendFirmataMessage(new ReportAnalogPinMessage(pin, report));
		}
	}

	private void resetPins() {
		stopAllMovements();
		setLeftRGBLightColor(0, 0, 0);
		setRightRGBLightColor(0, 0, 0);
		playTone(0, 0);
	}

	@Override
	public void start() {
		if (isInitialized == false) {
			initialise();
		}

		reportSensorData(true);
	}

	@Override
	public void pause() {
		stopAllMovements();
		reportSensorData(false);
	}

	@Override
	public void destroy() {
		resetPins();
	}

	private void sendAnalogFirmataMessage(int pin, int value) {
		sendFirmataMessage(new AnalogMessage(pin, value));
	}

	private void sendFirmataMessage(Message message) {
		try {
			firmata.send(message);
		} catch (SerialException e) {
			Log.d(TAG, "Firmata Serial error, cannot send message.");
		}
	}
}
