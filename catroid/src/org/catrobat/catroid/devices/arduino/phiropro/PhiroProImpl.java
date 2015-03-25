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

package org.catrobat.catroid.devices.arduino.phiropro;

import android.util.Log;

import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper;
import org.catrobat.catroid.devices.arduino.common.firmata.Firmata;
import org.catrobat.catroid.devices.arduino.common.firmata.message.AnalogMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.Message;
import org.catrobat.catroid.devices.arduino.common.firmata.message.ReportFirmwareVersionMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.SetPinModeMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.StreamingSerialAdapter;

import java.io.IOException;
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

	private static final int PIN_LEFT_MOTOR_BACKWARD = 10;
	private static final int PIN_LEFT_MOTOR_FORWARD = 11;

	private static final int PIN_RIGHT_MOTOR_FORWARD = 12;
	private static final int PIN_RIGHT_MOTOR_BACKWARD = 13;

	private static final int MIN_PWM_PIN = 3;
	private static final int MAX_PWM_PIN = 13;

	private BluetoothConnection btConnection;
	private Firmata firmata;
	private boolean isInitialized = false;

	private PhiroProListener phiroProListener;


	@Override
	public void playTone(int selected_tone, int duration) {
		sendAnalogFirmataMessage(PIN_SPEAKER_OUT,
				BytesHelper.DECODE_BYTE(BytesHelper.LSB(selected_tone), BytesHelper.LSB(duration)) );
	}

	@Override
	public void moveLeftMotorForward(int speed) {
		sendAnalogFirmataMessage(PIN_LEFT_MOTOR_FORWARD, speed);
	}

	@Override
	public void moveLeftMotorBackward(int speed) {
		sendAnalogFirmataMessage(PIN_LEFT_MOTOR_BACKWARD, speed);
	}

	@Override
	public void moveRightMotorForward(int speed) {
		sendAnalogFirmataMessage(PIN_RIGHT_MOTOR_FORWARD, speed);
	}

	@Override
	public void moveRightMotorBackward(int speed) {
		sendAnalogFirmataMessage(PIN_RIGHT_MOTOR_BACKWARD, speed);
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
				this.stopAllMovements();
				firmata.getSerial().stop();
				firmata.clearListeners();
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
	}

	@Override
	public void start() {
		if (isInitialized == false) {
			initialise();
		}
	}

	@Override
	public void pause() {
		stopAllMovements();
	}

	@Override
	public void destroy() {

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
