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
package org.catrobat.catroid.bluetoothtestserver;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;

public class BTClientHandler extends Thread
{
	private static final String TAG = BTClientHandler.class.getName();

	public static final byte DIRECT_COMMAND_REPLY = 0;
	public static final byte SYSTEM_COMMAND_REPLY = 1;
	public static final byte REPLY_COMMAND = 2;
	public static final byte DIRECT_COMMAND_NOREPLY = -128;
	public static final byte SYSTEM_COMMAND_NOREPLY = -127;
	public static final byte SET_OUTPUT_STATE = 4;
	public static final byte SET_INPUT_MODE = 5;
	public static final byte GET_OUTPUT_STATE = 6;
	public static final byte GET_INPUT_VALUES = 7;
	private StreamConnection connection;

	public BTClientHandler(StreamConnection connection)
	{
		this.connection = connection;
	}

	public void run() {
		String client = "null";
		try {
			RemoteDevice dev = RemoteDevice.getRemoteDevice(this.connection);
			client = dev.getFriendlyName(true);
			BTServer.writeMessage("Remote device address: " + dev.getBluetoothAddress() + "\n");
			BTServer.writeMessage("Remote device name: " + client + "\n");

			InputStream inStream = this.connection.openInputStream();
			BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));

			OutputStream outStream = this.connection.openOutputStream();
			PrintWriter pWriter = new PrintWriter(new OutputStreamWriter(outStream));

			char[] lastMessage = (char[])null;
			int messageLength = 0;
			int firstLenByte = 0;
			int secondLenByte = 0;

			while ((firstLenByte = bReader.read()) != -1) {
				secondLenByte = bReader.read() << 8;
				messageLength = firstLenByte + secondLenByte;
				BTServer.writeMessage("Received message, length (byte): " + messageLength + "\n");
				char[] buf = new char[messageLength];
				byte[] reply = (byte[])null;
				bReader.read(buf);

				if ((messageLength == 3) && (buf[0] == DIRECT_COMMAND_REPLY) && (buf[1] == '\006')) {
					reply = getLegoNXTReplyMessage(lastMessage);
				}

				if (buf[0] == DIRECT_COMMAND_REPLY) {
					BTServer.writeMessage("Reply message:\n");
					for (int i = 0; i < reply.length; i++) {
						BTServer.writeMessage("Byte" + i + ": " + reply[i] + " ");
					}
					BTServer.writeMessage("\nSending reply message \n");
					outStream.write(reply.length);
					outStream.write(0);
					outStream.write(reply);
					outStream.flush();
				}
				lastMessage = buf;
			}

			pWriter.close();
			bReader.close();
			this.connection.close();
		}
		catch (IOException ioException) {
			Log.e(TAG, "IOException!", ioException);
		}
		BTServer.writeMessage("Client " + client + " disconnected!\n");
	}

	public byte[] getLegoNXTReplyMessage(char[] lastMessage)
	{
		byte[] reply = new byte[32];
		reply[0] = REPLY_COMMAND;
		reply[1] = 6;
		reply[3] = (byte)lastMessage[2];
		reply[2] = 0;
		reply[4] = (byte)lastMessage[3];
		reply[5] = (byte)lastMessage[4];
		reply[6] = (byte)lastMessage[5];
		reply[7] = (byte)lastMessage[6];
		reply[9] = (byte)lastMessage[8];
		reply[10] = (byte)lastMessage[9];
		reply[11] = (byte)lastMessage[10];
		reply[12] = (byte)lastMessage[11];
		return reply;
	}
}
