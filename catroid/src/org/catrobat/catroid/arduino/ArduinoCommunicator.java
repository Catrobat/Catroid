/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.arduino;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

public abstract class ArduinoCommunicator extends Thread {

	public static final int DISCONNECT = 1;
	public static final int DISPLAY_TOAST = 2;
	public static final int STATE_CONNECTED = 3;
	public static final int STATE_CONNECTERROR = 4;
	public static final int STATE_CONNECTERROR_PAIRING = 5;
	public static final int STATE_RECEIVEERROR = 6;
	public static final int STATE_SENDERROR = 7;
	public static final int RECEIVED_MESSAGE = 8;
	public static final int PAUSED_MESSAGE = 9;

	public static final int SET_DIGITAL_PIN_VALUE_COMMAND = 100;
	public static final int GET_DIGITAL_PIN_VALUE_COMMAND = 101;

	protected boolean isConnected = false;
	protected Handler uiHandler;

	protected Resources resources;

	protected ArduinoControlCommands commands = new ArduinoControlCommands();
	protected ArduinoIncomingPinData sensors = ArduinoIncomingPinData.getInstance();

	public ArduinoCommunicator(Handler uiHandler, Resources resources) {
		this.uiHandler = uiHandler;
		this.resources = resources;
	}

	public Handler getHandler() {
		return myHandler;
	}

	public abstract void stopSensors();

	/**
	 * @return The current status of the connection
	 */
	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * Creates the connection, waits for incoming messages and dispatches them. The thread will be terminated
	 * on closing of the connection.
	 */
	@Override
	public abstract void run();

	/**
	 * Create a bluetooth connection with SerialPortServiceClass_UUID
	 * 
	 * @see <a href=
	 *      "http://lejos.sourceforge.net/forum/viewtopic.php?t=1991&highlight=android"
	 *      />
	 *      On error the method either sends a message to it's owner or creates an exception in the
	 *      case of no message handler.
	 */
	public abstract void createConnection() throws IOException;

	/**
	 * Closes the bluetooth connection. On error the method either sends a message
	 * to it's owner or creates an exception in the case of no message handler.
	 */
	public abstract void destroyConnection() throws IOException;

	/**
	 * Sends a message on the opened OutputStream
	 * 
	 * @param message
	 *            , the message as a byte array
	 */
	public abstract void sendMessage(byte[] message) throws IOException;

	/**
	 * Receives a message on the opened InputStream
	 * 
	 * @return the message
	 */

	public abstract byte[] receiveMessage() throws IOException, Exception;

	/**
	 * Sends a message on the opened OutputStream. In case of
	 * an error the state is sent to the handler.
	 * 
	 * @param message
	 *            , the message as a byte array
	 */

	protected void sendState(int message) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("message", message);
		sendBundle(myBundle);
	}

	protected void sendBundle(Bundle myBundle) {
		Message myMessage = myHandler.obtainMessage();
		myMessage.setData(myBundle);
		uiHandler.sendMessage(myMessage);
	}

	protected void sendToast(String toastText) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("message", DISPLAY_TOAST);
		myBundle.putString("toastText", toastText);
		sendBundle(myBundle);
	}

	protected synchronized void resetArduinoBoard() {
		sendCommandMessage(commands.resetArduino());
	}

	protected synchronized void pauseArduinoBoard() {
		sendCommandMessage(commands.pauseArduino());
	}

	protected synchronized void sendCommandMessage(byte[] commandMessage) {
		try {
			sendMessage(commandMessage);
		} catch (IOException e) {
			sendState(STATE_SENDERROR);
		}
	}

	// receive messages from the UI
	@SuppressLint("HandlerLeak")
	final Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			byte[] commandMessage;

			int pinLowerByte = message.getData().getInt("pinLowerByte");
			int pinHigherByte = message.getData().getInt("pinHigherByte");
			int value = message.getData().getInt("value");

			switch (message.what) {
				case SET_DIGITAL_PIN_VALUE_COMMAND:
					//set pin number + value
					commands.setPinNumberLowerByte(pinLowerByte);
					commands.setPinNumberHigherByte(pinHigherByte);
					commands.setPinValue(value);
					//get the buffer in the correct form, rdy to send
					commandMessage = commands.getCommandMessage();
					sendCommandMessage(commandMessage);
					break;
				case GET_DIGITAL_PIN_VALUE_COMMAND: //can be deleted
					//set pin number + value
					commands.setPinNumberLowerByte(pinLowerByte);
					commands.setPinNumberHigherByte(pinHigherByte);
					commands.setPinValue(value);
					commandMessage = commands.getCommandMessage();
					sendCommandMessage(commandMessage);
					break;
				default:
					Log.d("ArduinoCommunicator", "handleMessage: Default !!!!!!!!!!!!!!!");
					break;
			}
		}

	};
}
