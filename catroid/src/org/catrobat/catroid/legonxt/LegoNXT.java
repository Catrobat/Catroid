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
package org.catrobat.catroid.legonxt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.catrobat.catroid.bluetooth.BTConnectable;
import org.catrobat.catroid.bluetooth.DeviceListActivity;

import java.io.IOException;

public class LegoNXT implements BTConnectable {

	private static final int REQUEST_CONNECT_DEVICE = 1000;
	private static final int TONE_COMMAND = 101;
	private static final String TAG = LegoNXT.class.getSimpleName();
	private static Handler btcHandler;
	private LegoNXTCommunicator myNXTCommunicator;
	private boolean pairing;
	private Handler recieverHandler;
	private Activity activity;

	public LegoNXT(Activity activity, Handler recieverHandler) {
		this.activity = activity;
		this.recieverHandler = recieverHandler;
	}

	public static synchronized void sendBTCPlayToneMessage(int frequency, int duration) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("frequency", frequency);
		myBundle.putInt("duration", duration);

		Message myMessage = btcHandler.obtainMessage();
		myMessage.setData(myBundle);
		myMessage.what = TONE_COMMAND;

		btcHandler.sendMessage(myMessage);

	}

	public static synchronized void sendBTCMotorMessage(int delay, int motor, int speed, int angle) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("motor", motor);
		myBundle.putInt("speed", speed);
		myBundle.putInt("angle", angle);
		Message myMessage = btcHandler.obtainMessage();
		myMessage.setData(myBundle);
		myMessage.what = motor;

		if (delay == 0) {

			btcHandler.removeMessages(motor);
			btcHandler.sendMessage(myMessage);

		} else {
			//btcHandler.removeMessages(motor);
			btcHandler.sendMessageDelayed(myMessage, delay);

		}
	}

	public static Handler getBTCHandler() {
		return btcHandler;
	}

	public void startBTCommunicator(String macAddress) {

		if (myNXTCommunicator != null) {
			try {
				myNXTCommunicator.destroyNXTconnection();
			} catch (IOException ioException) {
				Log.e(TAG, Log.getStackTraceString(ioException));
			}
		}

		myNXTCommunicator = new LegoNXTBtCommunicator(recieverHandler, activity.getResources());
		btcHandler = myNXTCommunicator.getHandler();

		((LegoNXTBtCommunicator) myNXTCommunicator).setMACAddress(macAddress);
		myNXTCommunicator.start();
	}

	/**
	 * Receive messages from the BTCommunicator
	 */

	public void destroyCommunicator() {

		if (myNXTCommunicator != null) {
			//sendBTCMotorMessage(LegoNXTBtCommunicator.NO_DELAY, LegoNXTBtCommunicator.DISCONNECT, 0, 0);
			try {
				myNXTCommunicator.destroyNXTconnection();
			} catch (IOException ioException) { // TODO Auto-generated method stub
				Log.e(TAG, Log.getStackTraceString(ioException));
			}
			myNXTCommunicator = null;
		}
	}

	public void pauseCommunicator() {
		myNXTCommunicator.stopAllNXTMovement();
	}

	@Override
	public boolean isPairing() {
		// TODO Auto-generated method stub
		return pairing;
	}

	public void connectLegoNXT() {
		Intent serverIntent = new Intent(this.activity, DeviceListActivity.class);
		activity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

	}
}
