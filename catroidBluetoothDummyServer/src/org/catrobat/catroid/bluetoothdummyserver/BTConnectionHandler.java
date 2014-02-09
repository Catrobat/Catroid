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

	private static final String SERVERDUMMYMULTIPLAYER = "multiplayer";
	private static final String SETASCLIENT = "setasclient";
	private static final String SETASSERVER = "setasserver";
	private static final String COMMANDSETVARIABLE = "setvariable;";
	private static final String CONNECTIONSTRINGBEGIN = "btspp://localhost:";
	private static final String BTNAMEANDAUTHENTICATION = ";name=BT Dummy Server;authenticate=false;encrypt=false;";
	private static final String MAGIC_PACKET = "AEIOU";
	private static final String CLOSECONNECTION = "closethisconnection";
	private static final String SERVERDUMMYROBOTALBERT = "albert";

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

			if (receivedMessage[0].equals(SERVERDUMMYMULTIPLAYER)) {
				uuid = new UUID(receivedMessage[2], false);

				if (receivedMessage[1].equals(SETASCLIENT)) {
					multiplayerDummyClient();
					btTestConnectionRead(inputStream);
				} else if (receivedMessage[1].equals(SETASSERVER)) {
					multiplayerDummyServer();
					btTestConnectionRead(inputStream);
				} else {
					System.err.println("Incorrect message for Multiplayer-Server");
					return;
				}
			} else if (receivedMessage[0].equals(SERVERDUMMYROBOTALBERT)) {
				System.out.println("Albert message detected");
				uuid = new UUID(receivedMessage[1], false);
				multiplayerDummyServer();
				//btRobotAlbertSensorThread.start();
				stopAlbertSensorThread = false;
				btTestConnectionRead(inputStream);
			}
			System.out.println("end");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void multiplayerDummyServer() throws IOException {
		String connectionstring = CONNECTIONSTRINGBEGIN + uuid + BTNAMEANDAUTHENTICATION;
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
				if (receivedMessage.startsWith(COMMANDSETVARIABLE)) {
					outputStream.write(readBuffer, COMMANDSETVARIABLE.length(),
							readedBytes - COMMANDSETVARIABLE.length());
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
				outputStream.write(CLOSECONNECTION.getBytes());
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
		buffer[2] = (byte) 52;
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

		//boolean error = false;
		try {
			//if ((outputStream != null)/* && (error == false) */) {
			System.out.println("Sending Robot-Albert-Sensor-Message");
			outputStream.write(buffer);
			outputStream.flush();
			System.out.println("End of sending Robot-Albert-Sensor-Message");
			//}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//error = true;
			//outputStreamProgram = null;
			e.printStackTrace();
		}

	}

	private final Thread btRobotAlbertSensorThread = new Thread() {

		@Override
		public void run() {
			//System.out.println("[btrobotAlbertSensorThread] begin");
			try {
				while ((outputStreamProgram != null) && (stopAlbertSensorThread == false)) {

					Thread.sleep(500);

					System.out.println("btRobotAlbertSensorThread: Sending sensor message begin");
					robotAlbertSendDistanceValues(outputStreamProgram);
					System.out.println("btRobotAlbertSensorThread: Sending sensor message end");

					/*
					 * for (int i = 0; i < 10000; i++) {
					 * for (int j = 0; j < 5000; j++) {
					 * ;
					 * }
					 * }
					 */
				}
				System.out.println("btRobotAlbertSensorThread: temp = null !!!");

			} catch (Exception e) {
				// TODO: handle exception
				//outputStreamProgram = null;
				System.out.println("btRobotAlbertSensorThread: Exception occured: " + e.getMessage());
			}

			//System.out.println("[btrobotAlbertSensorThread] close");
		}

	};

}
