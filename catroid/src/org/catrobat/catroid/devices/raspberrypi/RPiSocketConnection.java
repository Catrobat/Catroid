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
package org.catrobat.catroid.devices.raspberrypi;

import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.BroadcastAction;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class RPiSocketConnection {

	private static final String TAG = AsyncRPiTaskRunner.class.getSimpleName();
	
	private Socket clientSocket;
	private String rpiVersion;
	private String host;

	private boolean isConnected;
	private OutputStream outToServer;
	private DataOutputStream outStream;
	private BufferedReader reader;
	private ArrayList<Integer> availableGPIOs;
	private int interruptReceiverPort;
	private Thread receiverThread;

	public RPiSocketConnection() {
	}

	public void connect(String host, int port) throws Exception {
		if (isConnected) {
			disconnect();
		}

		this.host = host;
		clientSocket = new Socket();
		clientSocket.connect(new InetSocketAddress(host, port), 2000);

		outToServer = clientSocket.getOutputStream();
		outStream = new DataOutputStream(outToServer);
		reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		String hello = reader.readLine();

		if (hello.startsWith("quit")) {
			throw new NoConnectionException("Server refused to accept our connection!");
		} else if (hello.startsWith("hello")) {
			isConnected = true;

			respondVersion();
			readServerPort();

			receiverThread = new Thread(new RPiSocketReceiver());
			receiverThread.start();
		}
	}

	public void disconnect() throws IOException {
		if (!isConnected) {
			return;
		}

		try {
			processCommand("quit");
		} catch (NoConnectionException e) {
			Log.d(TAG, "Error during quit, this should not happen!");
		}

		isConnected = false;
		clientSocket.close();
		receiverThread.interrupt();
	}

	private void respondVersion() throws Exception {
		String receivedLine = processCommand("rev");
		rpiVersion = receivedLine.split(" ")[1];
		availableGPIOs = RaspberryPiService.getInstance().getGpioList(rpiVersion);
	}

	private void readServerPort() throws Exception {
		String receivedLine = processCommand("serverport");
		interruptReceiverPort = Integer.parseInt(receivedLine.split(" ")[1]);
	}

	private String processCommand(String command) throws IOException, NoConnectionException {
		if (!isConnected) {
			throw new NoConnectionException("No active connection!");
		}

		Log.d(TAG, "Sending:  " + command);

		outStream.write(command.getBytes());
		String receivedLine = reader.readLine();

		Log.d(TAG, "Received: " + receivedLine);

		if (receivedLine == null || !receivedLine.startsWith(command.split(" ")[0])) {
			throw new IOException("Error with response");
		}

		return receivedLine;
	}

	private void callEvent(String broadcastMessage) {
		Sprite dummySenderSprite = new Sprite();
		dummySenderSprite.setName("raspi_interrupt_dummy");
		BroadcastAction action = ExtendedActions.broadcast(dummySenderSprite, broadcastMessage);
		action.act(0);
	}

	public void setPin(int pin, boolean value) throws NoConnectionException, IOException, NoGpioException {
		if (!isConnected) {
			throw new NoConnectionException("No active connection!");
		}

		if (!availableGPIOs.contains(pin)) {
			throw new NoGpioException("Pin out of range on this model!");
		}

		short valueShort = (short) (value ? 1 : 0);

		String setRequestMessage = "set " + pin + " " + valueShort;
		String receivedLine = processCommand(setRequestMessage);
		String[] tokens = receivedLine.split(" ");

		if (tokens.length != 3) {
			throw new IOException("setRequest: Error with response");
		}
	}

	public boolean getPin(int pin) throws NoConnectionException, IOException, NoGpioException {
		if (!availableGPIOs.contains(pin)) {
			throw new NoGpioException("Pin out of range on this model!");
		}

		String readRequestMsg = "read " + pin;
		String receivedLine = processCommand(readRequestMsg);
		String[] tokens = receivedLine.split(" ");

		if (tokens.length != 3) {
			throw new IOException("readRequest: Error with response");
		}

		if (tokens[2].equals("1")) {
			return true;
		} else if (tokens[2].equals("0")) {
			return false;
		} else {
			throw new IOException("readRequest: Error with response");
		}
	}

	public void activatePinInterrupt(int pin) throws NoConnectionException, IOException, NoGpioException {
		if (!availableGPIOs.contains(pin)) {
			throw new NoGpioException("Pin out of range on this model!");
		}

		String readRequestMsg = "readint " + pin;
		String receivedLine = processCommand(readRequestMsg);
		String[] tokens = receivedLine.split(" ");

		if (tokens.length != 3) {
			throw new IOException("readRequest: Error with response");
		}
	}

	public void setPWM(int pin, double frequencyInHz, double dutyCycleInPercent) throws NoConnectionException,
			IOException, NoGpioException {
		if (!availableGPIOs.contains(pin)) {
			throw new NoGpioException("Pin out of range on this model!");
		}

		String pwmRequestMessage = "pwm " + pin + " " + frequencyInHz + " " + dutyCycleInPercent;
		String receivedLine = processCommand(pwmRequestMessage);

		if (!pwmRequestMessage.equals(receivedLine)) {
			throw new IOException("pwmRequest: Error with response");
		}
	}

	private class RPiSocketReceiver implements Runnable {

		@Override
		public void run() {
			Socket receiverSocket = null;
			try {
				receiverSocket = new Socket(host, interruptReceiverPort);
				BufferedReader receiverReader = new BufferedReader(new InputStreamReader(receiverSocket.getInputStream()));
				while (!Thread.interrupted()) {
					String receivedLine = receiverReader.readLine();
					if (receivedLine == null) {
						break;
					}

					Log.d(TAG, "Interrupt: " + receivedLine);

					callEvent(Constants.RASPI_BROADCAST_PREFIX + receivedLine);
				}
				receiverSocket.close();
				Log.d(TAG, "RPiSocketReceiver closed");
			} catch (IOException e) {
				Log.e(TAG, "Exception " + e);
			}
		}
	}

	public String getVersion() throws NoConnectionException {
		if (!isConnected) {
			throw new NoConnectionException("No active connection!");
		}

		return rpiVersion;
	}

	public class NoGpioException extends Exception {

		private static final long serialVersionUID = 1L;

		public NoGpioException(String msg) {
			super(msg);
		}
	}

	public class NoConnectionException extends Exception {

		private static final long serialVersionUID = 1L;

		public NoConnectionException(String msg) {
			super(msg);
		}
	}

	public boolean isConnected() {
		return isConnected;
	}
}
