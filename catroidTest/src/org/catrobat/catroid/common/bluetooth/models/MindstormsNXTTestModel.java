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
import org.catrobat.catroid.devices.mindstorms.nxt.NXTError;
import org.catrobat.catroid.devices.mindstorms.nxt.NXTReply;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class MindstormsNXTTestModel implements DeviceModel {
	private boolean isRunning = true;
	private static final byte SHOULD_REPLY = 0x0;
	private static final byte NO_ERROR = 0x0;
	private Random random = new Random(System.currentTimeMillis());
	private byte[] batteryValue = { getRandomByte(256), getRandomByte(256) };
	private byte[] keepAliveTime = { getRandomByte(256), getRandomByte(256), getRandomByte(256), getRandomByte(256) };

	private byte[] portSensorType = { 0, 0, 0, 0 };
	private byte[] portSensorMode = { 0, 0, 0, 0 };

	private byte[] sensorValue = { getRandomByte(256), getRandomByte(256) };

	private byte ultrasonicSensorBytesReady = 0;

	protected byte[] createResponseFromClientRequest(byte[] message) {
		byte commandType = message[0];
		byte commandByte = message[1];

		switch (CommandByte.getTypeByValue(commandByte)) {
			case SET_INPUT_MODE:
				return handleSetInputModeMessage(message, commandType);

			case GET_INPUT_VALUES:
				return handleGetInputValuesMessage(message, commandType);

			case RESET_INPUT_SCALED_VALUE:
				return handleResetInputScaledValueMessage(message, commandType);

			case LS_WRITE:
				return handleLsWriteMessage(message, commandType);

			case LS_GET_STATUS:
				return handleLsGetStatusMessage(message, commandType);

			case LS_READ:
				return handleLsReadMessage(message, commandType);

			case KEEP_ALIVE:
				return handleKeepAlive(message, commandType);

			case GET_BATTERY_LEVEL:
				return handleGetBatteryLevel(message, commandType);

			default:
				return handleUnknownMessage(commandType, commandByte);
		}
	}

	private byte[] handleSetInputModeMessage(byte[] message, byte commandType) {
		byte status;
		byte[] reply = null;

		status = checkMessageLength(message, 5);
		if (status == NXTReply.NO_ERROR) {
			byte port = message[2];
			byte sensorType = message[3];
			byte sensorMode = message[4];

			status = setSensorTypeAndMode(sensorType, sensorMode, port);
		}

		if (commandType == SHOULD_REPLY) {
			reply = new byte[3];
			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = CommandByte.SET_INPUT_MODE.getByte();
			reply[2] = status;
		}
		return reply;
	}

	private byte[] handleGetInputValuesMessage(byte[] message, byte commandType) {
		byte[] reply = null;
		byte status;
		status = checkMessageLength(message, 3);
		byte port = message[2];
		if (status == NXTReply.NO_ERROR) {
			status = checkMessagePort(port);
		}

		if (commandType == SHOULD_REPLY) {
			reply = new byte[16];

			final byte isValid = 1;
			final byte isCalibrated = 0;
			final byte notUsed = 0;
			final byte scaledValue0 = sensorValue[0];
			final byte scaledValue1 = sensorValue[1];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = CommandByte.GET_INPUT_VALUES.getByte();
			reply[2] = status;
			reply[3] = port;
			reply[4] = isValid;
			reply[5] = isCalibrated;
			reply[6] = portSensorType[port];
			reply[7] = portSensorMode[port];
			reply[8] = notUsed;
			reply[9] = notUsed;
			reply[10] = notUsed;
			reply[11] = notUsed;
			reply[12] = scaledValue0;
			reply[13] = scaledValue1;
			reply[14] = notUsed;
			reply[15] = notUsed;
		}
		return reply;
	}

	private byte[] handleResetInputScaledValueMessage(byte[] message, byte commandType) {
		byte[] reply = null;
		byte status = checkMessageLength(message, 3);
		if (status == NO_ERROR) {
			status = checkMessagePort(message[2]);
		}

		if (commandType == SHOULD_REPLY) {
			reply = new byte[3];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = CommandByte.RESET_INPUT_SCALED_VALUE.getByte();
			reply[2] = status;
		}
		return reply;
	}

	private byte[] handleLsWriteMessage(byte[] message, byte commandType) {

		byte[] reply = null;
		byte status = checkMessageLength(message, 7);
		if (status == NO_ERROR) {
			status = checkMessagePort(message[2]);
		}

		if (commandType == SHOULD_REPLY) {
			reply = new byte[3];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = CommandByte.LS_WRITE.getByte();
			reply[2] = status;
		}

		return reply;
	}

	private byte[] handleLsGetStatusMessage(byte[] message, byte commandType) {

		byte[] reply = null;
		byte status = checkMessageLength(message, 3);
		if (status == NO_ERROR) {
			status = checkMessagePort(message[2]);
		}

		if (commandType == SHOULD_REPLY) {
			reply = new byte[4];
			ultrasonicSensorBytesReady = getRandomByte(2);

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = CommandByte.LS_GET_STATUS.getByte();
			reply[2] = status;
			reply[3] = ultrasonicSensorBytesReady;
		}

		return reply;
	}

	private byte[] handleLsReadMessage(byte[] message, byte commandType) {

		byte[] reply = null;
		byte status = checkMessageLength(message, 3);
		if (status == NO_ERROR) {
			status = checkMessagePort(message[2]);
		}

		if (commandType == SHOULD_REPLY) {
			reply = new byte[20];

			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = CommandByte.LS_READ.getByte();
			reply[2] = status;
			reply[3] = ultrasonicSensorBytesReady;
			reply[4] = sensorValue[0];

			ultrasonicSensorBytesReady = 0;
		}

		return reply;
	}

	private byte[] handleKeepAlive(byte[] message, byte commandType) {
		byte[] reply = null;
		byte status = checkMessageLength(message, 2);

		if (commandType == SHOULD_REPLY) {
			reply = new byte[7];
			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = CommandByte.KEEP_ALIVE.getByte();
			reply[2] = status;
			reply[3] = keepAliveTime[0];
			reply[4] = keepAliveTime[1];
			reply[5] = keepAliveTime[2];
			reply[6] = keepAliveTime[3];
		}

		return reply;
	}

	private byte[] handleGetBatteryLevel(byte[] message, byte commandType) {

		byte[] reply = null;
		byte status = checkMessageLength(message, 2);

		if (commandType == SHOULD_REPLY) {
			reply = new byte[5];
			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = CommandByte.GET_BATTERY_LEVEL.getByte();
			reply[2] = status;
			reply[3] = batteryValue[0];
			reply[4] = batteryValue[1];
		}

		return reply;
	}

	private byte[] handleUnknownMessage(byte commandType, byte commandByte) {
		byte[] reply = null;
		if (commandType == SHOULD_REPLY) {
			reply = new byte[3];
			reply[0] = CommandType.REPLY_COMMAND.getByte();
			reply[1] = commandByte;
			reply[2] = NXTError.ErrorCode.UnknownCommand.getByte();
		}

		return reply;
	}

	private byte checkMessagePort(byte port) {

		if (port < 0 || port > 3) {
			return NXTError.ErrorCode.BadArguments.getByte();
		}

		return NXTReply.NO_ERROR;
	}

	private byte checkMessageLength(byte[] message, int expectedMessageLength) {
		if (message.length != expectedMessageLength) {
			return NXTError.ErrorCode.WrongNumberOfBytes.getByte();
		}

		return NXTReply.NO_ERROR;
	}

	private byte setSensorType(byte sensorType, byte port) {
		if (sensorType < 0x0 || sensorType > 0x0C) {
			return NXTError.ErrorCode.BadArguments.getByte();
		}

		portSensorType[port] = sensorType;

		return NXTReply.NO_ERROR;
	}

	private byte setSensorTypeAndMode(byte sensorType, byte sensorMode, byte port) {

		byte status = checkMessagePort(port);
		if (status != NO_ERROR) {
			return status;
		}

		status = setSensorType(sensorType, port);
		if (status != NO_ERROR) {
			return status;
		}

		return setSensorMode(sensorMode, port);
	}

	private byte setSensorMode(byte sensorMode, byte port) {

		switch (sensorMode) {
			case 0x00: // raw mode
			case 0x20: // bool mode
			case 0x40:
			case 0x60:
			case (byte) 0x80: // percent
			case (byte) 0xA0:
			case (byte) 0xC0:
			case (byte) 0xE0:
			case (byte) 0x1F:
				portSensorMode[port] = sensorMode;
				return NO_ERROR;
			default:
				return NXTError.ErrorCode.BadArguments.getByte();
		}
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

	public void setSensorValue(int value) {
		sensorValue[0] = (byte) (value & 0xff);
		sensorValue[1] = (byte) ((value >> 8) & 0xff);
	}

	public void setBatteryValue(int batteryValue) {

		this.batteryValue[0] = (byte) (batteryValue & 0xff);
		this.batteryValue[1] = (byte) ((batteryValue >> 8) & 0xff);
	}

	public void setKeepAliveTime(int keepAliveTimeValue) {
		keepAliveTime[0] = (byte) (keepAliveTimeValue & 0xff);
		keepAliveTime[1] = (byte) ((keepAliveTimeValue >> 8) & 0xff);
		keepAliveTime[2] = (byte) ((keepAliveTimeValue >> 16) & 0xff);
		keepAliveTime[3] = (byte) ((keepAliveTimeValue >> 24) & 0xff);
	}

	public byte getRandomByte(int maxExclusive) {
		return (byte) random.nextInt(maxExclusive);
	}
}
