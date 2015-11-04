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
package org.catrobat.catroid.devices.arduino;

import android.util.Log;

import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;

import java.io.IOException;
import java.util.UUID;

import name.antonsmirnov.firmata.Firmata;
import name.antonsmirnov.firmata.message.AnalogMessage;
import name.antonsmirnov.firmata.message.DigitalMessage;
import name.antonsmirnov.firmata.message.Message;
import name.antonsmirnov.firmata.message.ReportAnalogPinMessage;
import name.antonsmirnov.firmata.message.ReportDigitalPortMessage;
import name.antonsmirnov.firmata.message.ReportFirmwareVersionMessage;
import name.antonsmirnov.firmata.message.SetPinModeMessage;
import name.antonsmirnov.firmata.serial.ISerial;
import name.antonsmirnov.firmata.serial.SerialException;
import name.antonsmirnov.firmata.serial.StreamingSerialAdapter;

public class ArduinoImpl implements Arduino {

	public static final int PIN_ANALOG_0 = 0;
	public static final int PIN_ANALOG_1 = 1;
	public static final int PIN_ANALOG_2 = 2;
	public static final int PIN_ANALOG_3 = 3;
	public static final int PIN_ANALOG_4 = 4;
	public static final int PIN_ANALOG_5 = 5;

	public static final int PORT_DIGITAL_0 = 0;
	public static final int PORT_DIGITAL_1 = 1;

	private static final int MIN_PWM_PIN_GROUP_1 = 3;
	private static final int MAX_PWM_PIN_GROUP_1 = 3;
	private static final int MIN_PWM_PIN_GROUP_2 = 5;
	private static final int MAX_PWM_PIN_GROUP_2 = 6;
	private static final int MIN_PWM_PIN_GROUP_3 = 9;
	private static final int MAX_PWM_PIN_GROUP_3 = 11;

	private static final int MIN_ANALOG_SENSOR_PIN = 0;
	private static final int MAX_ANALOG_SENSOR_PIN = 5;

	private static final UUID ARDUINO_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String TAG = ArduinoImpl.class.getSimpleName();

	private Firmata firmata;
	private boolean isReportingSensorData = false;
	private boolean isInitialized = false;

	private ArduinoListener arduinoListener;
	private BluetoothConnection btConnection;

	private int castValue(int value) {
		if (value <= 0) {
			return 0;
		}
		if (value >= 100) {
			return 255;
		}

		return (int) (value * 2.55);
	}

	@Override
	public String getName() {
		return "ARDUINO";
	}

	@Override
	public void setConnection(BluetoothConnection connection) {
		this.btConnection = connection;
	}

	@Override
	public Class<? extends BluetoothDevice> getDeviceType() {
		return BluetoothDevice.ARDUINO;
	}

	@Override
	public void disconnect() {

		if (firmata == null) {
			return;
		}

		try {
			this.reportSensorData(false);
			firmata.clearListeners();
			firmata.getSerial().stop();
			isInitialized = false;
			firmata = null;
		} catch (SerialException e) {
			Log.d(TAG, "Error stop Arduino serial");
		}
	}

	@Override
	public boolean isAlive() {

		if (firmata == null) {
			return false;
		}

		try {
			firmata.send(new ReportFirmwareVersionMessage());
			return true;
		} catch (SerialException e) {
			return false;
		}
	}

	public void reportFirmwareVersion() {
		if (firmata == null) {
			return;
		}

		try {
			firmata.send(new ReportFirmwareVersionMessage());
		} catch (SerialException e) {
			Log.d(TAG, "Firmata Serial error, cannot send message.");
		}
	}

	@Override
	public UUID getBluetoothDeviceUUID() {
		return ARDUINO_UUID;
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

		arduinoListener = new ArduinoListener();
		firmata.addListener(arduinoListener);

		firmata.getSerial().start();

		for (int pin = MIN_PWM_PIN_GROUP_1; pin <= MAX_PWM_PIN_GROUP_1; ++pin) {
			sendFirmataMessage(new SetPinModeMessage(pin, SetPinModeMessage.PIN_MODE.PWM.getMode()));
		}
		for (int pin = MIN_PWM_PIN_GROUP_2; pin <= MAX_PWM_PIN_GROUP_2; ++pin) {
			sendFirmataMessage(new SetPinModeMessage(pin, SetPinModeMessage.PIN_MODE.PWM.getMode()));
		}
		for (int pin = MIN_PWM_PIN_GROUP_3; pin <= MAX_PWM_PIN_GROUP_3; ++pin) {
			sendFirmataMessage(new SetPinModeMessage(pin, SetPinModeMessage.PIN_MODE.PWM.getMode()));
		}
		reportSensorData(true);
	}

	private void reportSensorData(boolean report) {
		if (isReportingSensorData == report) {
			return;
		}

		isReportingSensorData = report;

		for (int i = MIN_ANALOG_SENSOR_PIN; i <= MAX_ANALOG_SENSOR_PIN; i++) {
			sendFirmataMessage(new ReportAnalogPinMessage(i, report));
		}
	}

	@Override
	public void start() {
		if (!isInitialized) {
			initialise();
		}
		reportSensorData(true);
	}

	@Override
	public void pause() { }

	@Override
	public void destroy() {
		reportSensorData(false);
	}

	@Override
	public void setAnalogArduinoPin(int pin, int value) {
		sendAnalogFirmataMessage(pin, value);
	}

	@Override
	public void setDigitalArduinoPin(int digitalPinNumber, int pinValue) {
		int digitalPort = 0;
		double value;
		if (digitalPinNumber < 8) {
			if (pinValue > 0) {
				value = Math.pow(2, (double) digitalPinNumber);
				sendDigitalFirmataMessage(digitalPort, digitalPinNumber, (int) value);
				arduinoListener.setPortValue(digitalPinNumber, 1);
			} else {
				sendDigitalFirmataMessage(digitalPort, digitalPinNumber, 0);
				arduinoListener.setPortValue(digitalPinNumber, 0);
			}
		} else {
			digitalPort = 1;
			if (pinValue > 0) {
				value = Math.pow(2, (double) digitalPinNumber - 8);
				sendDigitalFirmataMessage(digitalPort, digitalPinNumber, (int) value);
				arduinoListener.setPortValue(digitalPinNumber, 1);
			} else {
				sendDigitalFirmataMessage(digitalPort, digitalPinNumber, 0);
				arduinoListener.setPortValue(digitalPinNumber, 0);
			}
		}
	}

	@Override
	public double getDigitalArduinoPin(int digitalPinNumber) {
		sendFirmataMessage(new SetPinModeMessage(digitalPinNumber, SetPinModeMessage.PIN_MODE.INPUT.getMode()));
		int port;

		if (digitalPinNumber >= 8) {
			port = 1;
		} else {
			port = 0;
		}

		sendFirmataMessage(new ReportDigitalPortMessage(port, true));

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Log.d(TAG, "Error Arduino sensor thread sleep()");
		}

		double result = arduinoListener.getPortValue(digitalPinNumber);

		sendFirmataMessage(new ReportDigitalPortMessage(port, false));

		return result;
	}

	@Override
	public double getAnalogArduinoPin(int analogPinNumber) {
		switch (analogPinNumber) {
			case 0:
				return arduinoListener.getAnalogPin0();
			case 1:
				return arduinoListener.getAnalogPin1();
			case 2:
				return arduinoListener.getAnalogPin2();
			case 3:
				return arduinoListener.getAnalogPin3();
			case 4:
				return arduinoListener.getAnalogPin4();
			case 5:
				return arduinoListener.getAnalogPin5();
		}
		return 0;
	}

	private void sendAnalogFirmataMessage(int pin, int value) {
		sendFirmataMessage(new AnalogMessage(pin, castValue(value)));
	}

	private void sendDigitalFirmataMessage(int port, int pin, int value) {
		sendFirmataMessage(new SetPinModeMessage(pin, SetPinModeMessage.PIN_MODE.OUTPUT.getMode()));
		sendFirmataMessage(new DigitalMessage(port, value));
	}

	private void sendFirmataMessage(Message message) {
		if (firmata == null) {
			return;
		}

		try {
			firmata.send(message);
		} catch (SerialException e) {
			Log.d(TAG, "Firmata Serial error, cannot send message.");
		}
	}
}
