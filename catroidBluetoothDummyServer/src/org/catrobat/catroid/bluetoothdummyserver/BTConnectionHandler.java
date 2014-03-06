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
package org.catrobat.catroid.bluetoothdummyserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class BTConnectionHandler implements Runnable {
	private StreamConnection btTestConnection = null;
	private StreamConnection btProgramConnection = null;
	private UUID uuid = null;

	private static final String SERVER_DUMMY_MULTIPLAYER = "multiplayer";
	private static final String SET_AS_CLIENT = "setasclient";
	private static final String SET_AS_SERVER = "setasserver";
	private static final String COMMAND_SET_VARIABLE = "setvariable;";
	private static final String CONNECTION_STRING_BEGIN = "btspp://localhost:";
	private static final String BT_NAME_AND_AUTHENTICATION = ";name=BT Dummy Server;authenticate=false;encrypt=false;";
	private static final String MAGIC_PACKET = "AEIOU";
	private static final String CLOSE_CONNECTION = "closethisconnection";
	private static final String SERVER_DUMMY_ROBOT_ALBERT = "albert";

	private OutputStream outputStreamProgram = null;
	private boolean stopAlbertSensorThread = true;

	public BTConnectionHandler(StreamConnection connection) {
		this.btTestConnection = connection;
	}

	@Override
	public void run() {
		try {
			System.out.println("BTConnectionHandler started");
			InputStream inputStream = btTestConnection.openInputStream();
			byte[] readBuffer = new byte[1024];
			System.out.println("Before reading Bytes");
			int readedBytes = inputStream.read(readBuffer);
			System.out.println("bytes read=" + readedBytes);
			String[] receivedMessage = (new String(readBuffer, 0, readedBytes, "ASCII")).split(";");
			System.out.println("receivedMessage[0]:" + receivedMessage[0]);

			if (receivedMessage[0].equals(SERVER_DUMMY_MULTIPLAYER)) {
				uuid = new UUID(receivedMessage[2], false);

				if (receivedMessage[1].equals(SET_AS_CLIENT)) {
					multiplayerDummyClient();
					btTestConnectionRead(inputStream);
				} else if (receivedMessage[1].equals(SET_AS_SERVER)) {
					multiplayerDummyServer();
					btTestConnectionRead(inputStream);
				} else {
					System.err.println("Incorrect message for Multiplayer-Server");
					return;
				}
			} else if (receivedMessage[0].equals(SERVER_DUMMY_ROBOT_ALBERT)) {
				System.out.println("Albert message detected");
				uuid = new UUID(receivedMessage[1], false);
				multiplayerDummyServer();
				stopAlbertSensorThread = false;
				btTestConnectionRead(inputStream);
			}
			System.out.println("end");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void multiplayerDummyServer() throws IOException {
		String connectionstring = CONNECTION_STRING_BEGIN + uuid + BT_NAME_AND_AUTHENTICATION;
		System.out.println("[SERVER] Waiting for incoming connection...  UUID: " + uuid);
		StreamConnectionNotifier stream_conn_notifier = (StreamConnectionNotifier) Connector.open(connectionstring);
		System.out.println("[Server] Notifier done");
		btProgramConnection = stream_conn_notifier.acceptAndOpen();
		stream_conn_notifier.close();
		stream_conn_notifier = null;
		System.out.println("[SERVER] Client Connected...");
	}

	private void multiplayerDummyClient() throws IOException {
		System.out.println("[Client] Try to Connect...  UUID: " + uuid);

		DiscoveryAgent discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
		String connectionstring = discoveryAgent.selectService(uuid, ServiceRecord.AUTHENTICATE_ENCRYPT, false);
		btProgramConnection = (StreamConnection) Connector.open(connectionstring);
		System.out.println("[CLIENT] Connected to Server...");
	}

	private void btTestConnectionRead(InputStream inputStream) {
		btProgramConnectionRead.start();

		try {
			OutputStream outputStream = btProgramConnection.openOutputStream();

			byte[] buffer = new byte[64];
			ByteBuffer.wrap(buffer).put(MAGIC_PACKET.getBytes());
			ByteBuffer.wrap(buffer).putInt(MAGIC_PACKET.length(), Integer.MAX_VALUE);
			outputStream.write(buffer, 0, MAGIC_PACKET.length() + Integer.SIZE);
			outputStream.flush();

			if (stopAlbertSensorThread == false) {
				outputStreamProgram = outputStream;
				btRobotAlbertSensorThread.start();
			}

			byte[] readBuffer = new byte[1024];
			int readedBytes;
			while (true) {
				readedBytes = inputStream.read(readBuffer);
				if (readedBytes < 0) {
					inputStream.close();
					btProgramConnectionRead.interrupt();
					break;
				}

				String receivedMessage = new String(readBuffer, 0, readedBytes, "ASCII");
				if (receivedMessage.startsWith(COMMAND_SET_VARIABLE)) {
					outputStream.write(readBuffer, COMMAND_SET_VARIABLE.length(),
							readedBytes - COMMAND_SET_VARIABLE.length());
					outputStream.flush();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[btTestConnectionRead] close");
	}

	private final Thread btProgramConnectionRead = new Thread() {

		@Override
		public void run() {
			OutputStream outputStream = null;
			try {
				InputStream inputStream = btProgramConnection.openInputStream();
				outputStream = btTestConnection.openOutputStream();
				byte[] buffer = new byte[1024];
				int readedbytes;

				while (true) {
					readedbytes = inputStream.read(buffer);
					if (readedbytes < 0) {
						break;
					}
					outputStream.write(buffer, 0, readedbytes);
					outputStream.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			closeTestConnection(outputStream);
			System.out.println("[btProgrammConnectionRead] close");
		}

		private void closeTestConnection(OutputStream outputStream) {
			try {
				stopAlbertSensorThread = true;
				outputStream.write(CLOSE_CONNECTION.getBytes());
				outputStream.flush();
				outputStreamProgram = null;
				btTestConnection.close();
				btProgramConnection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	private void robotAlbertSendDistanceValues(OutputStream outputStream) {

		byte[] buffer = new byte[52];
		buffer[0] = (byte) 0xAA;
		buffer[1] = (byte) 0x55;
		buffer[2] = (byte) 50;
		buffer[3] = (byte) 6;
		buffer[13] = (byte) 50;
		buffer[14] = (byte) 50;
		buffer[15] = (byte) 50;
		buffer[16] = (byte) 50;
		buffer[17] = (byte) 50;
		buffer[18] = (byte) 50;
		buffer[19] = (byte) 50;
		buffer[20] = (byte) 50;
		buffer[50] = (byte) 0x0D;
		buffer[51] = (byte) 0x0A;

		try {
			System.out.println("Sending Robot-Albert-Sensor-Message");
			outputStream.write(buffer);
			outputStream.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private final Thread btRobotAlbertSensorThread = new Thread() {

		@Override
		public void run() {
			try {
				while ((outputStreamProgram != null) && (stopAlbertSensorThread == false)) {

					Thread.sleep(500);

					robotAlbertSendDistanceValues(outputStreamProgram);
				}
				System.out.println("btRobotAlbertSensorThread: temp = null !!!");

			} catch (Exception e) {
				System.out.println("btRobotAlbertSensorThread: Exception occured: " + e.getMessage());
			}
		}

	};

}
