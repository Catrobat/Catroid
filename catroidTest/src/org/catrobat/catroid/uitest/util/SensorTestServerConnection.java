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
package org.catrobat.catroid.uitest.util;

import android.util.Log;

import junit.framework.AssertionFailedError;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@SuppressWarnings("AvoidUsingHardCodedIP")
public final class SensorTestServerConnection {

	private static final String TAG = SensorTestServerConnection.class.getSimpleName();

	// fields to provide ethernet connection to the arduino server
	private static Socket clientSocket = null;
	private static DataOutputStream sendToServer;
	private static BufferedReader receiveFromServer;

	// Enter the right IP address and port number to connect and request sensor values.
	// PMD DISABLE AvoidUsingHardCodedIP FOR 1 LINES
	private static final String ARDUINO_SERVER_IP = "129.27.202.103"; //NOPMD
	private static final int SERVER_PORT = 6789;

	private static final int GET_VIBRATION_VALUE_ID = 1;
	private static final int GET_LIGHT_VALUE_ID = 2;
	private static final int CALIBRATE_VIBRATION_SENSOR_ID = 3;

	public static final int SET_LED_ON_VALUE = 1;
	public static final int SET_LED_OFF_VALUE = 0;

	public static final int SET_VIBRATION_ON_VALUE = 1;
	public static final int SET_VIBRATION_OFF_VALUE = 0;

	public static final int NETWORK_DELAY_MS = 500;

	private SensorTestServerConnection() {
	}

	public static void connectToArduinoServer() throws IOException {
		Log.d(TAG, "Trying to connect to server...");

		clientSocket = new Socket(ARDUINO_SERVER_IP, SERVER_PORT);
		clientSocket.setKeepAlive(true);

		Log.d(TAG, "Connected to: " + ARDUINO_SERVER_IP + " on port " + SERVER_PORT);
		sendToServer = new DataOutputStream(clientSocket.getOutputStream());
		receiveFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	public static void closeConnection() throws IOException {
		if (clientSocket != null) {
			clientSocket.close();
		}
		clientSocket = null;
		sendToServer = null;
		receiveFromServer = null;
	}

	public static void checkLightSensorValue(int expected) {

		char expectedChar;
		String assertString;
		String response;
		if (expected == SET_LED_ON_VALUE) {
			expectedChar = '1';
			assertString = "Error: LED is turned off!";
		} else {
			expectedChar = '0';
			assertString = "Error: LED is turned on!";
		}
		try {
			connectToArduinoServer();
			Thread.sleep(NETWORK_DELAY_MS);
			Log.d(TAG, "requesting sensor value: ");
			sendToServer.writeByte(Integer.toHexString(GET_LIGHT_VALUE_ID).charAt(0));
			sendToServer.flush();
			Thread.sleep(NETWORK_DELAY_MS);
			response = receiveFromServer.readLine();
			Log.d(TAG, "response received! " + response);
			clientSocket.close();

			assertFalse("Wrong Command!", response.contains("ERROR"));
			assertTrue("Wrong data received!", response.contains("LIGHT_END"));
			assertTrue(assertString, response.charAt(0) == expectedChar);
		} catch (IOException ioException) {
			throw new AssertionFailedError("Data exchange failed! Check server connection!");
		} catch (InterruptedException e) {
			Log.w(TAG, "InterruptedException", e);
		}
	}

	public static void checkVibrationSensorValue(int expected) {

		char expectedChar;
		String assertString;
		String response;
		if (expected == SET_VIBRATION_ON_VALUE) {
			expectedChar = '1';
			assertString = "Error: Vibrator is turned off!";
		} else {
			expectedChar = '0';
			assertString = "Error: Vibrator is turned on!";
		}
		try {
			connectToArduinoServer();
			Thread.sleep(NETWORK_DELAY_MS);
			Log.d(TAG, "requesting sensor value: ");
			sendToServer.writeByte(Integer.toHexString(GET_VIBRATION_VALUE_ID).charAt(0));
			sendToServer.flush();
			Thread.sleep(NETWORK_DELAY_MS);
			response = receiveFromServer.readLine();
			Log.d(TAG, "response received! " + response);
			clientSocket.close();

			assertFalse("Wrong Command!", response.contains("ERROR"));
			assertTrue("Wrong data received!", response.contains("VIBRATION_END"));
			assertTrue(assertString, response.charAt(0) == expectedChar);
		} catch (IOException ioException) {
			throw new AssertionFailedError("Data exchange failed! Check server connection!");
		} catch (InterruptedException e) {
			Log.w(TAG, "InterruptedException", e);
		}
	}

	public static void calibrateVibrationSensor() {
		String response;
		try {
			connectToArduinoServer();
			Thread.sleep(NETWORK_DELAY_MS);
			Log.d(TAG, "requesting sensor value: ");
			sendToServer.writeByte(Integer.toHexString(CALIBRATE_VIBRATION_SENSOR_ID).charAt(0));
			sendToServer.flush();
			Thread.sleep(NETWORK_DELAY_MS);
			response = receiveFromServer.readLine();
			Log.d(TAG, "response received! " + response);
			clientSocket.close();
		} catch (IOException ioException) {
			throw new AssertionFailedError("Data exchange failed! Check server connection!");
		} catch (InterruptedException e) {
			Log.w(TAG, "InterruptedException", e);
		}
	}
}
