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
package org.catrobat.catroid.io;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import org.catrobat.catroid.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Connection extends Thread {
	public static enum connectionState {
		UNDEFINED, CONNECTED, UNCONNECTED
	};

	private String ip;
	private Socket client;
	private OutputStream output;
	private ObjectOutputStream objectOutput;
	private InputStream input;
	private ObjectInputStream objectInput;
	private ArrayList<Command> commandList;
	private connectionState state;
	private PcConnectionManager connectionManager;
	private Connection thisThread;
	private final int port = 63000;
	private String serverName;
	private final int socketTimeout = 1000;

	public Connection(String ip, PcConnectionManager connect, String serverName) {
		this.ip = ip;
		this.serverName = serverName;
		client = null;
		state = connectionState.UNDEFINED;
		connectionManager = connect;
		thisThread = this;
		Thread.currentThread().setName("connection");
	}

	@Override
	public void run() {
		initialize();
		while (thisThread == this) {

			if (commandList.size() > 0) {
				sendCommand();
				waitForResponse();
			} else {
				try {
					Thread.sleep(1L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void initialize() {
		output = null;
		objectOutput = null;
		commandList = new ArrayList<Command>();
		try {
			if (ip != null) {
				client = new Socket();
				client.connect(new InetSocketAddress(ip, port), socketTimeout);
				client.setSoTimeout(1000);
			} else {
				state = connectionState.UNCONNECTED;
				PcConnectionManager.getInstance(null).setConnectionAlreadySetUp(false);
				stopThread();
				return;
			}
		} catch (UnknownHostException e1) {
			Log.w("Connection", "" + ip + " unknown.");
			state = connectionState.UNCONNECTED;
			PcConnectionManager.getInstance(null).setConnectionAlreadySetUp(false);
			return;
		} catch (IOException e1) {
			Log.w("Connection", "Connection can't be established.");
			state = connectionState.UNCONNECTED;
			PcConnectionManager.getInstance(null).setConnectionAlreadySetUp(false);
			return;
		}
		state = connectionState.CONNECTED;
		try {
			output = client.getOutputStream();
			objectOutput = new ObjectOutputStream(output);
		} catch (IOException e2) {
			Log.w("Connection", "Connection to " + ip + " broke.");
			state = connectionState.UNCONNECTED;
			PcConnectionManager.getInstance(null).setConnectionAlreadySetUp(false);
			stopThread();
			return;
		}
		try {
			input = client.getInputStream();
			objectInput = new ObjectInputStream(input);
		} catch (IOException e) {
			Log.w("Connection", "Connection to " + ip + " broke.");
			state = connectionState.UNCONNECTED;
			PcConnectionManager.getInstance(null).setConnectionAlreadySetUp(false);
			stopThread();
			return;
		}
		PcConnectionManager.getInstance(null).setConnection(this);
	}

	public connectionState getConnectionState() {
		return state;
	}

	public void sendCommand() {
		Command actualCommand = commandList.get(0);
		try {
			objectOutput.writeObject(actualCommand);
		} catch (IOException e1) {
			Log.w("Connection", "Connection to " + ip + " broke.");
			PcConnectionManager.getInstance(null).setConnectionAlreadySetUp(false);
			showErrorDialog();
		}
		commandList.remove(actualCommand);
	}

	public void waitForResponse() {
		Confirmation confirmation = null;
		try {
			confirmation = (Confirmation) objectInput.readObject();
			Log.v("Connection", "Answer from server is: " + confirmation.getConfirmationState());
		} catch (OptionalDataException e) {
			Log.w("Connection", "Input was not an object. Was of primitive type.");
			PcConnectionManager.getInstance(null).setConnectionAlreadySetUp(false);
			showErrorDialog();
		} catch (ClassNotFoundException e) {
			Log.w("Connection", "Illegal class object was sent.");
			PcConnectionManager.getInstance(null).setConnectionAlreadySetUp(false);
			showErrorDialog();
		} catch (IOException e) {
			Log.w("Connection", "Connection to " + ip + " broke.");
			PcConnectionManager.getInstance(null).setConnectionAlreadySetUp(false);
			showErrorDialog();
		}
	}

	public void showErrorDialog() {
		connectionManager.getStageActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {

				String errorMessage = connectionManager.getStageActivity().getString(R.string.connection_broke_1)
						+ serverName + connectionManager.getStageActivity().getString(R.string.connection_broke_2);
				AlertDialog.Builder builder = new AlertDialog.Builder(connectionManager.getStageActivity());
				builder.setMessage(errorMessage).setCancelable(false)
						.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								connectionManager.setConnectionAlreadySetUp(false);
								connectionManager.getStageActivity().exit();
								stopThread();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}

	public void stopThread() {
		thisThread = null;
		try {
			if (output != null) {
				output.close();
			}
			if (objectOutput != null) {
				objectOutput.close();
			}
			if (input != null) {
				input.close();
			}
			if (objectInput != null) {
				objectInput.close();
			}
			if (client != null) {
				client.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		PcConnectionManager.getInstance(null).setConnection(null);
	}

	public void addCommand(Command command) {
		commandList.add(command);
	}
}