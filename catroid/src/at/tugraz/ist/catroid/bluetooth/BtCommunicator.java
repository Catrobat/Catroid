/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.bluetooth;

import java.util.ArrayList;

import android.os.Handler;

public class BtCommunicator extends Thread {
	protected static ArrayList<byte[]> receivedMessages;
	protected static boolean requestConfirmFromDevice;
	protected Handler myHandler;
	protected byte[] returnMessage;
	protected String mMACaddress;
	protected boolean connected;

	public static ArrayList<byte[]> getReceivedMessageList() {
		return receivedMessages;
	}

	public static void enableRequestConfirmFromDevice() {
		requestConfirmFromDevice = true;
	}

	public Handler getHandler() {
		return myHandler;
	}

	public byte[] getReturnMessage() {
		return returnMessage;
	}

	public void setMACAddress(String mMACaddress) {
		this.mMACaddress = mMACaddress;
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
	public void run() {

		//create Your Connections

		while (connected) {
			//do what ever you have to do....
		}
	}
}
