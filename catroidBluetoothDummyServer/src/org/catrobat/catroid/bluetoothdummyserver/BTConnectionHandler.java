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

	public BTConnectionHandler(StreamConnection connection) {
		this.btTestConnection = connection;
	}

	@Override
	public void run() {
		try {
			InputStream inputStream = btTestConnection.openInputStream();
			byte[] readBuffer = new byte[1024];
			int readedBytes = inputStream.read(readBuffer);
			String[] receivedMessage = (new String(readBuffer, 0, readedBytes, "ASCII")).split(";");

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
				uuid = new UUID(receivedMessage[1], false);
				multiplayerDummyServer();
				btTestConnectionRead(inputStream);
			}

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
				outputStream.write(CLOSECONNECTION.getBytes());
				outputStream.flush();
				btTestConnection.close();
				btProgramConnection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

}
