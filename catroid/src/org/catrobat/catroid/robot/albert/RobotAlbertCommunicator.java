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
 *    
 *    This file incorporates work covered by the following copyright and  
 *    permission notice: 
 *    
 *		   	Copyright 2010 Guenther Hoelzl, Shawn Brown
 *
 *		   	This file is part of MINDdroid.
 *
 * 		  	MINDdroid is free software: you can redistribute it and/or modify
 * 		  	it under the terms of the GNU Affero General Public License as
 * 		  	published by the Free Software Foundation, either version 3 of the
 *   		License, or (at your option) any later version.
 *
 *   		MINDdroid is distributed in the hope that it will be useful,
 *   		but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   		GNU Affero General Public License for more details.
 *
 *   		You should have received a copy of the GNU Affero General Public License
 *   		along with MINDdroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.robot.albert;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

//This code is based on the nxt-implementation
public abstract class RobotAlbertCommunicator extends Thread {
	public static final int MOTOR_LEFT = 0;
	public static final int MOTOR_RIGHT = 1;
	public static final int MOTOR_BOTH = 2;

	public static final int EYE_LEFT = 0;
	public static final int EYE_RIGHT = 1;
	public static final int EYE_BOTH = 2;

	public static final int DISCONNECT = 99;

	public static final int DISPLAY_TOAST = 1000;
	public static final int STATE_CONNECTED = 1001;
	public static final int STATE_CONNECTERROR = 1002;
	public static final int STATE_CONNECTERROR_PAIRING = 1022;
	public static final int MOTOR_STATE = 1003;
	public static final int STATE_RECEIVEERROR = 1004;
	public static final int STATE_SENDERROR = 1005;
	public static final int RECEIVED_MESSAGE = 1111;

	public static final int MOTOR_COMMAND = 102;
	public static final int MOTOR_RESET_COMMAND = 103;
	public static final int BUZZER_COMMAND = 104;
	public static final int RGB_EYE_COMMAND = 105;
	public static final int FRONT_LED_COMMAND = 106;

	protected boolean connected = false;
	protected Handler uiHandler;

	protected static ArrayList<byte[]> receivedMessages = new ArrayList<byte[]>();
	protected byte[] returnMessage;

	protected Resources resources;

	protected ControlCommands commands = new ControlCommands();
	protected SensorData sensors = SensorData.getInstance();

	public RobotAlbertCommunicator(Handler uiHandler, Resources resources) {
		this.uiHandler = uiHandler;
		this.resources = resources;
	}

	public static ArrayList<byte[]> getReceivedMessageList() {
		return receivedMessages;
	}

	public Handler getHandler() {
		return myHandler;
	}

	public byte[] getReturnMessage() {
		return returnMessage;
	}

	/**
	 * @return The current status of the connection
	 */
	public boolean isConnected() {
		return connected;
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

	public abstract void stopAllMovement();

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

	protected synchronized void resetRobotAlbert() {
		commands.resetRobotAlbert();
		sendCommandMessage(commands.getCommandMessage());
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
			switch (message.what) {
				case MOTOR_COMMAND:
					int motor = message.getData().getInt("motor");
					int speed = message.getData().getInt("speed");
					switch (motor) {
						case MOTOR_LEFT:
							commands.setSpeedOfLeftMotor(speed);
							break;
						case MOTOR_RIGHT:
							commands.setSpeedOfRightMotor(speed);
							break;
						case MOTOR_BOTH:
							commands.setSpeedOfLeftMotor(speed);
							commands.setSpeedOfRightMotor(speed);
							break;
						default:
							Log.d("Albert", "Handler: ERROR: default-Motor !!!!!!!!!!!!!!!");
					}
					commandMessage = commands.getCommandMessage();
					sendCommandMessage(commandMessage);
					break;
				case MOTOR_RESET_COMMAND:
					commands.setSpeedOfLeftMotor(0);
					commands.setSpeedOfRightMotor(0);
					commands.setBuzzer(0);
					commands.setLeftEye(255, 255, 255);
					commands.setRightEye(255, 255, 255);
					commandMessage = commands.getCommandMessage();
					sendCommandMessage(commandMessage);
					break;
				case BUZZER_COMMAND:
					int buzzer = message.getData().getInt("buzzer");
					commands.setBuzzer(buzzer);
					commandMessage = commands.getCommandMessage();
					sendCommandMessage(commandMessage);
					break;
				case FRONT_LED_COMMAND:
					int status = message.getData().getInt("frontLED");
					commands.setFrontLed(status);
					commandMessage = commands.getCommandMessage();
					sendCommandMessage(commandMessage);
					break;
				case RGB_EYE_COMMAND:
					Log.d("Albert", "create command-message");
					int eye = message.getData().getInt("eye");
					int red = message.getData().getInt("red");
					int green = message.getData().getInt("green");
					int blue = message.getData().getInt("blue");
					switch (eye) {
						case EYE_LEFT:
							commands.setLeftEye(red, green, blue);
							break;
						case EYE_RIGHT:
							commands.setRightEye(red, green, blue);
							break;
						case EYE_BOTH:
							commands.setLeftEye(red, green, blue);
							commands.setRightEye(red, green, blue);
							break;
						default:
							Log.d("Albert", "Handler: ERROR: default-Motor !!!!!!!!!!!!!!!");
					}
					commandMessage = commands.getCommandMessage();
					sendCommandMessage(commandMessage);
					break;

				default:
					Log.d("RobotAlbertCommunicator", "handleMessage: Default !!!!!!!!!!!!!!!");
					break;
			}
		}

	};
}
