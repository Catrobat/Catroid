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

import java.io.IOException;
import java.util.ArrayList;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class is for talking to a LEGO NXT robot via bluetooth.
 * The communciation to the robot is done via LCP (LEGO communication protocol).
 * Objects of this class can either be run as standalone thread or controlled
 * by the owners, i.e. calling the send/recive methods by themselves.
 */
public abstract class RobotAlbertCommunicator extends Thread {
	public static final int MOTOR_LEFT = 0;
	public static final int MOTOR_RIGHT = 1;
	public static final int MOTOR_BOTH = 2;

	public static final int EYE_LEFT = 0;
	public static final int EYE_RIGHT = 1;
	public static final int EYE_BOTH = 2;
	/*
	 * public static final int MOTOR_B_ACTION = 40;
	 * public static final int MOTOR_RESET = 10;
	 * public static final int DO_BEEP = 51;
	 * public static final int DO_ACTION = 52;
	 * public static final int READ_MOTOR_STATE = 60;
	 * public static final int GET_FIRMWARE_VERSION = 70;
	 */
	public static final int DISCONNECT = 99;

	public static final int DISPLAY_TOAST = 1000;
	public static final int STATE_CONNECTED = 1001;
	public static final int STATE_CONNECTERROR = 1002;
	public static final int STATE_CONNECTERROR_PAIRING = 1022;
	public static final int MOTOR_STATE = 1003;
	public static final int STATE_RECEIVEERROR = 1004;
	public static final int STATE_SENDERROR = 1005;
	//public static final int FIRMWARE_VERSION = 1006;
	/*
	 * public static final int FIND_FILES = 1007;
	 * public static final int START_PROGRAM = 1008;
	 * public static final int STOP_PROGRAM = 1009;
	 * public static final int GET_PROGRAM_NAME = 1010;
	 * public static final int PROGRAM_NAME = 1011;
	 * public static final int SAY_TEXT = 1030;
	 * public static final int VIBRATE_PHONE = 1031;
	 */
	public static final int RECEIVED_MESSAGE = 1111;

	//public static final int NO_DELAY = 0;
	//public static final int GENERAL_COMMAND = 100;
	public static final int MOTOR_COMMAND = 102;
	public static final int MOTOR_RESET_COMMAND = 103;
	public static final int BUZZER_COMMAND = 104;
	private static final int RGB_EYE_COMMAND = 105;
	private static final int FRONT_LED_COMMAND = 106;
	//public static final int TONE_COMMAND = 101;

	protected boolean connected = false;
	protected Handler uiHandler;
	private static boolean requestConfirmFromDevice = false;

	protected static ArrayList<byte[]> receivedMessages = new ArrayList<byte[]>();
	protected byte[] returnMessage;

	protected Resources mResources;

	private ControlCommands commands = new ControlCommands();

	public RobotAlbertCommunicator(Handler uiHandler, Resources resources) {
		this.uiHandler = uiHandler;
		this.mResources = resources;
	}

	public static ArrayList<byte[]> getReceivedMessageList() {
		return receivedMessages;
	}

	public static void enableRequestConfirmFromDevice(boolean cfd) {
		requestConfirmFromDevice = cfd;
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

	public abstract byte[] receiveMessage() throws IOException;

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

	protected void sendMessageAndState(byte[] message) {

		try {
			sendMessage(message);
		} catch (IOException e) {
			sendState(STATE_SENDERROR);
		}
	}

	protected void sendState(int message, byte[] data) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("message", message);
		myBundle.putByteArray("received_message", data);
		sendBundle(myBundle);
	}

	protected void sendBundle(Bundle myBundle) {
		Message myMessage = myHandler.obtainMessage();
		myMessage.setData(myBundle);
		uiHandler.sendMessage(myMessage);
	}

	protected void dispatchMessage(byte[] message) {

		//Log.i("bt", "Received response, length: " + message.length);
		//		for (int i = 0; i < message.length; i++) {
		//			Log.i("bt", " " + (0x000000FF & message[i]));
		//		}

		/*
		 * switch (message[1]) {
		 * 
		 * case LCPMessage.SET_OUTPUT_STATE:
		 * //sendState(RECEIVED_MESSAGE, message);
		 * analyzeMessageSetOutputState(message);
		 * break;
		 * 
		 * case LCPMessage.GET_OUTPUT_STATE:
		 * //sendState(RECEIVED_MESSAGE, message);
		 * receivedMessages.add(message);
		 * analyzeMessageGetOutputState(message);
		 * break;
		 * default:
		 * Log.i("bt", "Unknown Message received by LegoNXTCommunicator over bluetooth " + message.length);
		 * receivedMessages.add(message);
		 * break;
		 * }
		 */
	}

	protected void analyzeMessageSetOutputState(byte[] message) {
		//change command byte0 to DIRECT_COMMAND_REPLY to use!
		/*
		 * Log.i("bt", "Direct command executed: " + (int) message[0]);
		 * Log.i("bt", "executed Command was: " + (int) message[1]);
		 * Log.i("bt", "Status: " + (int) message[2]);
		 * Log.i("bt", "Length: " + message.length);
		 */

	}

	protected void analyzeMessageGetOutputState(byte[] message) {

		Log.d("!!!!", "Why are u there?");
		//See Lego NXT Docu or LCPMessage class for info on numbers!
		Log.i("bt", "Message Length: " + message.length);
		Log.i("bt", "GetOutputState executed: " + (int) message[0]);
		//		Log.i("bt", "----- executed Command:  " + (int) message[1]);
		//		Log.i("bt", "Status: " + (int) message[2]);
		//		Log.i("bt", "Used Motor: " + (int) message[3]);
		//		Log.i("bt", "Used Power: " + (int) message[4]);
		//Log.i("bt", "Mode: " + (int) message[5]);
		//Log.i("bt", "Regulation: " + (int) message[6]);
		//Log.i("bt", "Turn Ratio: " + (int) message[7]);
		//Log.i("bt", "Run State: " + (int) message[8]);

		//		int tacholimit = (0x000000FF & message[9]); //unsigned types would be too smart for java, sorry no chance mate!
		//		tacholimit += ((0x000000FF & message[10]) << 8);
		//		tacholimit += ((0x000000FF & message[11]) << 16);
		//		tacholimit += ((0x000000FF & message[12]) << 24);

		//Log.i("bt", "Tacholimit " + tacholimit);
		/*
		 * int tachocount = message[13];
		 * tachocount += (message[14] << 8);
		 * tachocount += (message[15] << 16);
		 * tachocount += (message[16] << 24);
		 * 
		 * Log.i("bt", "Tachocount " + tachocount);
		 */
	}

	protected void doBeep(int frequency, int duration) {
		/*
		 * byte[] message = LCPMessage.getBeepMessage(frequency, duration);
		 * sendMessageAndState(message);
		 * waitSomeTime(20);
		 */
	}

	protected void waitSomeTime(int millis) {
		try {
			Thread.sleep(millis);

		} catch (InterruptedException e) {
		}
	}

	protected void sendToast(String toastText) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("message", DISPLAY_TOAST);
		myBundle.putString("toastText", toastText);
		sendBundle(myBundle);
	}

	protected synchronized void moveMotor(int motor, int speed, int end) {
		/*
		 * byte[] message = LCPMessage.getMotorMessage(motor, speed, end);
		 * sendMessageAndState(message);
		 * //Log.i("bto", "Motor " + motor + " speed " + speed);
		 * 
		 * if (requestConfirmFromDevice) {
		 * byte[] test = LCPMessage.getOutputStateMessage(motor);
		 * sendMessageAndState(test);
		 * }
		 */
	}

	protected synchronized void resetRobotAlbert() {
		commands.resetRobotAlbert();
		sendCommandMessage(commands.getCommandMessage());
	}

	protected synchronized void sendCommandMessage(byte[] command_message) {
		/*
		 * byte[] buffer = new byte[22];
		 * buffer[0] = (byte) 0xAA;
		 * buffer[1] = (byte) 0x55;
		 * buffer[2] = (byte) 20;
		 * buffer[3] = (byte) 6;
		 * buffer[4] = (byte) 0x11;
		 * buffer[5] = (byte) 0;
		 * buffer[6] = (byte) 0;
		 * buffer[7] = (byte) 0xFF;
		 * buffer[8] = (byte) 100; //Left motor
		 * buffer[9] = (byte) 100; //Right motor
		 * buffer[10] = (byte) 0; //Buzzer
		 * buffer[11] = (byte) 0; //Left LED Red
		 * buffer[12] = (byte) 0; //Left LED Green
		 * buffer[13] = (byte) 0; //Left LED Blue
		 * buffer[14] = (byte) 0; //Right LED Red
		 * buffer[15] = (byte) 0; //Right LED Green
		 * buffer[16] = (byte) 0; //Right LED Blue
		 * buffer[17] = (byte) 0; //Front-LED 0...1
		 * buffer[18] = (byte) 0; //Reserved
		 * buffer[19] = (byte) 0; //Body-LED 0...255
		 * buffer[20] = (byte) 0x0D;
		 * buffer[21] = (byte) 0x0A;
		 */
		try {
			sendMessage(command_message);
		} catch (IOException e) {
			sendState(STATE_SENDERROR);
		}
	}

	// receive messages from the UI
	final Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {

			/*
			 * switch (myMessage.what) {
			 * case TONE_COMMAND:
			 * doBeep(myMessage.getData().getInt("frequency"), myMessage.getData().getInt("duration"));
			 * break;
			 * case DISCONNECT:
			 * break;
			 * default:
			 * 
			 * int motor;
			 * int speed;
			 * int angle;
			 * motor = myMessage.getData().getInt("motor");
			 * speed = myMessage.getData().getInt("speed");
			 * angle = myMessage.getData().getInt("angle");
			 * moveMotor(motor, speed, angle);
			 * 
			 * Log.d("Albert", "Handler:albertMoveForward()");
			 * albertMoveForward();
			 * 
			 * break;
			 * 
			 * }
			 */
			byte[] command_message;
			switch (message.what) {
				case MOTOR_COMMAND:
					Log.d("Albert", "create command-message");
					int motor = message.getData().getInt("motor");
					int speed = message.getData().getInt("speed");
					switch (motor) {
						case MOTOR_LEFT:
							Log.d("Albert", "set speed of left-motor:" + speed);
							commands.setSpeedOfLeftMotor(speed);
							break;
						case MOTOR_RIGHT:
							Log.d("Albert", "set speed of right-motor:" + speed);
							commands.setSpeedOfRightMotor(speed);
							break;
						case MOTOR_BOTH:
							Log.d("Albert", "set speed of both-motors:" + speed);
							commands.setSpeedOfLeftMotor(speed);
							commands.setSpeedOfRightMotor(speed);
							break;
						default:
							Log.d("Albert", "Handler: ERROR: default-Motor !!!!!!!!!!!!!!!");
					}
					command_message = commands.getCommandMessage();
					Log.d("Albert", "buffer[5]=" + command_message[5] + " (sendFrameNo)");
					Log.d("Albert", "buffer[2]=" + command_message[2]);
					Log.d("Albert", "buffer[3]=" + command_message[3]);
					Log.d("Albert", "buffer[7]=" + command_message[7]);
					Log.d("Albert", "buffer[8]=" + command_message[8]);
					Log.d("Albert", "buffer[9]=" + command_message[9]);
					Log.d("Albert", "buffer[21]=" + command_message[21]);
					sendCommandMessage(command_message);
					break;
				case MOTOR_RESET_COMMAND:
					//int motor = MOTOR_BOTH;
					Log.d("RobotAlbertC.Handler", "MotorResetCommand received");
					commands.setSpeedOfLeftMotor(0);
					commands.setSpeedOfRightMotor(0);
					commands.setBuzzer(0);
					commands.setLeftEye(255, 255, 255);
					commands.setRightEye(255, 255, 255);
					command_message = commands.getCommandMessage();
					Log.d("Albert", "buffer[5]=" + command_message[5] + " (sendFrameNo)");
					Log.d("Albert", "buffer[2]=" + command_message[2]);
					Log.d("Albert", "buffer[3]=" + command_message[3]);
					Log.d("Albert", "buffer[7]=" + command_message[7]);
					Log.d("Albert", "buffer[8]=" + command_message[8]);
					Log.d("Albert", "buffer[9]=" + command_message[9]);
					Log.d("Albert", "buffer[10]=" + command_message[10]);
					Log.d("Albert", "buffer[21]=" + command_message[21]);
					sendCommandMessage(command_message);
					break;
				case BUZZER_COMMAND:
					Log.d("RobotAlbertC.Handler", "BuzzerCommand received");
					int buzzer = message.getData().getInt("buzzer");
					commands.setBuzzer(buzzer);
					command_message = commands.getCommandMessage();
					Log.d("Albert", "buffer[5]=" + command_message[5] + " (sendFrameNo)");
					Log.d("Albert", "buffer[2]=" + command_message[2]);
					Log.d("Albert", "buffer[3]=" + command_message[3]);
					Log.d("Albert", "buffer[7]=" + command_message[7]);
					Log.d("Albert", "buffer[8]=" + command_message[8]);
					Log.d("Albert", "buffer[9]=" + command_message[9]);
					Log.d("Albert", "buffer[10]=" + command_message[10]);
					Log.d("Albert", "buffer[21]=" + command_message[21]);
					sendCommandMessage(command_message);
					break;
				case FRONT_LED_COMMAND:
					Log.d("RobotAlbertC.Handler", "FrontLEDCommand received");
					int status = message.getData().getInt("frontLED");
					commands.setFrontLed(status);
					command_message = commands.getCommandMessage();
					Log.d("Albert", "buffer[5]=" + command_message[5] + " (sendFrameNo)");
					Log.d("Albert", "buffer[17]=" + command_message[17]);
					sendCommandMessage(command_message);
					break;
				case RGB_EYE_COMMAND:
					Log.d("Albert", "create command-message");
					int eye = message.getData().getInt("eye");
					int red = message.getData().getInt("red");
					int green = message.getData().getInt("green");
					int blue = message.getData().getInt("blue");
					switch (eye) {
						case EYE_LEFT:
							Log.d("Albert", "set left-eye:");
							commands.setLeftEye(red, green, blue);
							break;
						case EYE_RIGHT:
							Log.d("Albert", "set right-eye:");
							commands.setRightEye(red, green, blue);
							break;
						case EYE_BOTH:
							Log.d("Albert", "set both-eyes:");
							commands.setLeftEye(red, green, blue);
							commands.setRightEye(red, green, blue);
							break;
						default:
							Log.d("Albert", "Handler: ERROR: default-Motor !!!!!!!!!!!!!!!");
					}
					command_message = commands.getCommandMessage();
					Log.d("Albert", "buffer[5]=" + command_message[5] + " (sendFrameNo)");
					Log.d("Albert", "buffer[11]=" + command_message[11]);
					Log.d("Albert", "buffer[12]=" + command_message[12]);
					Log.d("Albert", "buffer[13]=" + command_message[13]);
					Log.d("Albert", "buffer[14]=" + command_message[14]);
					Log.d("Albert", "buffer[15]=" + command_message[15]);
					Log.d("Albert", "buffer[16]=" + command_message[16]);
					sendCommandMessage(command_message);
					break;
				default:
					Log.d("RobotAlbertCommunicator", "handleMessage: Default !!!!!!!!!!!!!!!");
					break;
			}
			Log.d("Albert", "Handler:albertMoveForward()");
		}

	};
}
