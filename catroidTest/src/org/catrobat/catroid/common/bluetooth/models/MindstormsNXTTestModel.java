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

package org.catrobat.catroid.common.bluetooth.models;

import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandType;
import org.catrobat.catroid.devices.mindstorms.nxt.NXTReply;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MindstormsNXTTestModel implements DeviceModel {

	private boolean isRunning = true;

	protected byte[] createResponseFromClientRequest(byte[] message) {
		byte[] reply;
		byte commandType = message[0];
		byte commandByte = message[1];

		if (commandType != 0x00) {
			return null;
		}

		if (commandByte == CommandByte.SET_INPUT_MODE.getByte()) {
			reply = new byte[3];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = commandByte;
			reply[2] = NXTReply.NO_ERROR;

		} else if (commandByte == CommandByte.GET_INPUT_VALUES.getByte()) {
			byte inputPort = message[2];
			reply = new byte[16];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = commandByte;
			reply[2] = NXTReply.NO_ERROR;
			reply[3] = inputPort;

		} else if (commandByte == CommandByte.LS_WRITE.getByte()) {
			reply = new byte[3];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = commandByte;
			reply[2] = NXTReply.NO_ERROR;

		} else if (commandByte == CommandByte.LS_GET_STATUS.getByte()) {
			reply = new byte[4];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = commandByte;
			reply[2] = NXTReply.NO_ERROR;
			reply[3] = 1;//Bytes Ready

		} else if (commandByte == CommandByte.LS_READ.getByte()) {
			reply = new byte[20];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = commandByte;
			reply[2] = NXTReply.NO_ERROR;
			reply[3] = 1;//Bytes Read

		} else if (commandByte == CommandByte.RESET_INPUT_SCALED_VALUE.getByte()) {
			reply = new byte[3];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = commandByte;
			reply[2] = NXTReply.NO_ERROR;

		} else {
			reply = null;
		}

		return reply;
	}

	@Override
	public void start(DataInputStream inStream, OutputStream outStream) throws IOException {
		byte[] messageLengthBuffer = new byte[2];

		while (isRunning) {
			inStream.readFully(messageLengthBuffer, 0, 2);
			int expectedMessageLength = ((messageLengthBuffer[0] & 0xFF) | (messageLengthBuffer[1] & 0xFF) << 8);
			handleClientMessage(expectedMessageLength, inStream, outStream);
		}
	}

	@Override
	public void stop() {
		isRunning = false;
	}

	private void handleClientMessage(int expectedMessageLength, DataInputStream inStream, OutputStream outStream) throws IOException {

		byte[] requestMessage = new byte[expectedMessageLength];

		inStream.readFully(requestMessage, 0, expectedMessageLength);

		byte[] responseMessage = createResponseFromClientRequest(requestMessage);

		if (responseMessage == null) {
			return;
		}

		outStream.write(getMessageLength(responseMessage));
		outStream.write(responseMessage);
		outStream.flush();
	}

	private byte[] getMessageLength(byte[] message) {

		byte[] messageLength = {
				(byte) (message.length & 0x00FF),
				(byte) ((message.length & 0xFF00) >> 8)
		};

		return messageLength;
	}
}