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

import org.catrobat.catroid.bluetooth.BTConnectable;
import org.catrobat.catroid.bluetooth.DeviceListActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author une
 * 
 */
public class RobotAlbert implements BTConnectable {

	private static final int REQUEST_CONNECT_DEVICE = 1000;

	private static RobotAlbertCommunicator myCommunicator;

	private boolean pairing;
	private static Handler btcHandler;
	private Handler recieverHandler;
	private Activity activity;

	//private static int TONE_COMMAND = 101;
	private static final int MOTOR_COMMAND = 102;
	private static final int BUZZER_COMMAND = 104;
	private static final int RGB_EYE_COMMAND = 105;
	private static final int FRONT_LED_COMMAND = 106;
	private static final int GET_DISTANCE_COMMAND = 107;

	public RobotAlbert(Activity activity, Handler recieverHandler) {
		this.activity = activity;
		this.recieverHandler = recieverHandler;
	}

	public void startBTCommunicator(String mac_address) {

		if (myCommunicator != null) {
			try {
				myCommunicator.destroyConnection();
			} catch (IOException e) {
			}
		}

		myCommunicator = new RobotAlbertBtCommunicator(this, recieverHandler, BluetoothAdapter.getDefaultAdapter(),
				activity.getResources());
		btcHandler = myCommunicator.getHandler();

		((RobotAlbertBtCommunicator) myCommunicator).setMACAddress(mac_address);
		myCommunicator.start();
	}

	/**
	 * Receive messages from the BTCommunicator
	 */

	public void destroyCommunicator() {

		if (myCommunicator != null) {
			//sendBTCMotorMessage(LegoNXTBtCommunicator.NO_DELAY, LegoNXTBtCommunicator.DISCONNECT, 0, 0);
			try {
				myCommunicator.stopAllMovement();
				myCommunicator.destroyConnection();
			} catch (IOException e) { // TODO Auto-generated method stub

				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myCommunicator = null;
		}
	}

	public void pauseCommunicator() {
		myCommunicator.stopAllMovement();
	}

	public static synchronized void sendBTCPlayToneMessage(int frequency, int duration) {
		/*
		 * Bundle myBundle = new Bundle();
		 * myBundle.putInt("frequency", frequency);
		 * myBundle.putInt("duration", duration);
		 * 
		 * Message myMessage = btcHandler.obtainMessage();
		 * myMessage.setData(myBundle);
		 * myMessage.what = TONE_COMMAND;
		 * 
		 * btcHandler.sendMessage(myMessage);
		 */

	}

	public static synchronized void sendBTCMotorMessage(int delay, int motor, int speed, int angle) {
		/*
		 * Bundle myBundle = new Bundle();
		 * myBundle.putInt("motor", motor);
		 * myBundle.putInt("speed", speed);
		 * myBundle.putInt("angle", angle);
		 * Message myMessage = btcHandler.obtainMessage();
		 * myMessage.setData(myBundle);
		 * myMessage.what = motor;
		 * 
		 * if (delay == 0) {
		 * 
		 * btcHandler.removeMessages(motor);
		 * btcHandler.sendMessage(myMessage);
		 * 
		 * } else {
		 * //btcHandler.removeMessages(motor);
		 * btcHandler.sendMessageDelayed(myMessage, delay);
		 * 
		 * }
		 */
	}

	public static synchronized void sendRobotAlbertMotorMessage(int motor, int speed) {
		Log.d("RobotAlbert", "sendRobotAlbertMotorMessage():Bundle");
		Bundle myBundle = new Bundle();
		myBundle.putInt("motor", motor);
		myBundle.putInt("speed", speed);
		Log.d("RobotAlbert", "1");
		Message myMessage = btcHandler.obtainMessage();
		Log.d("RobotAlbert", "2");
		myMessage.setData(myBundle);
		myMessage.what = MOTOR_COMMAND;
		Log.d("RobotAlbert", "sendRobotAlbertMotorMessage():btcHandler.sendMessage(...)");
		btcHandler.sendMessage(myMessage);
		Log.d("RobotAlbert", "sendRobotAlbertMotorMessage finished!");
		/*
		 * if (delay == 0) {
		 * 
		 * btcHandler.removeMessages(motor);
		 * btcHandler.sendMessage(myMessage);
		 * 
		 * } else {
		 * //btcHandler.removeMessages(motor);
		 * btcHandler.sendMessageDelayed(myMessage, delay);
		 * 
		 * }
		 */
	}

	public static synchronized void sendRobotAlbertBuzzerMessage(int buzzer) {
		Log.d("RobotAlbert", "sendRobotAlbert BuzzerMessage():Bundle");
		Bundle myBundle = new Bundle();
		myBundle.putInt("buzzer", buzzer);
		Log.d("RobotAlbert", "1");
		Message myMessage = btcHandler.obtainMessage();
		Log.d("RobotAlbert", "2");
		myMessage.setData(myBundle);
		myMessage.what = BUZZER_COMMAND;
		Log.d("RobotAlbert", "sendRobotAlbertBuzzerMessage():btcHandler.sendMessage(...)");
		btcHandler.sendMessage(myMessage);
		Log.d("RobotAlbert", "sendRobotAlbertBuzzerMessage finished!");
	}

	public static synchronized void sendRobotAlbertFrontLedMessage(int status) {
		Log.d("RobotAlbert", "sendRobotAlbert FrontLedMessage():Bundle");
		Bundle myBundle = new Bundle();
		myBundle.putInt("frontLED", status);
		Message myMessage = btcHandler.obtainMessage();
		myMessage.setData(myBundle);
		myMessage.what = FRONT_LED_COMMAND;
		btcHandler.sendMessage(myMessage);
	}

	public static synchronized void sendRobotAlbertRgbLedEyeMessage(int eye, int red, int green, int blue) {
		Log.d("RobotAlbert", "sendRobotAlbert RgbLedEyeMessage():Bundle");
		Bundle myBundle = new Bundle();
		myBundle.putInt("eye", eye);
		myBundle.putInt("red", red);
		myBundle.putInt("green", green);
		myBundle.putInt("blue", blue);
		Log.d("RobotAlbert", "1");
		Message myMessage = btcHandler.obtainMessage();
		Log.d("RobotAlbert", "2");
		myMessage.setData(myBundle);
		myMessage.what = RGB_EYE_COMMAND;
		Log.d("RobotAlbert", "sendRobotAlbertRgbLedEyeMessage():btcHandler.sendMessage(...)");
		btcHandler.sendMessage(myMessage);
		Log.d("RobotAlbert", "sendRobotAlbertRgbLedEyeMessage finished!");
	}

	public static int getRobotAlbertDistanceSensorLeftMessage() {
		int value = myCommunicator.sensors.getValueOfLeftDistanceSensor();
		return value;
	}

	public static int getRobotAlbertDistanceSensorRightMessage() {
		int value = myCommunicator.sensors.getValueOfRightDistanceSensor();
		return value;
	}

	public static Handler getBTCHandler() {
		return btcHandler;
	}

	@Override
	public boolean isPairing() {
		// TODO Auto-generated method stub
		return pairing;
	}

	public void connectRobotAlbert() {
		Intent serverIntent = new Intent(this.activity, DeviceListActivity.class);
		serverIntent.putExtra("RobotAlbert", true);
		activity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

	}
}
