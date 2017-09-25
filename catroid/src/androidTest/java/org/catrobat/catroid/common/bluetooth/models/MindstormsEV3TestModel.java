/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandOpCode;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandType;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3SensorMode;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

public class MindstormsEV3TestModel implements DeviceModel {

	public static final byte SYSTEM_REPLY = 0x03;
	public static final byte SYSTEM_REPLY_ERROR = 0x05;
	private static final byte SUCCESS = 0x00;
	private static final byte UNKNOWN_ERROR = 0x0A;
	private static final byte HANDLE_TO_FILE = 1;
	private static final int MIN_BEGIN_DOWNLOAD_LENGTH = 25;
	private static final int MAX_MESSAGE_LENGTH = 65534;
	private static final int MIN_CONTINUE_DOWNLOAD_LENGTH = 6;

	public static final byte DIRECT_REPLY = 0x02;
	public static final byte DIRECT_REPLY_ERROR = 0x04;

	private boolean isRunning = true;
	private boolean replyRequired;

	private EV3Sensor.Sensor[] sensors = {EV3Sensor.Sensor.NO_SENSOR, EV3Sensor.Sensor.NO_SENSOR,
			EV3Sensor.Sensor.NO_SENSOR, EV3Sensor.Sensor.NO_SENSOR};
	private byte[] portSensorMode = {0, 0, 0, 0};
	private int[] portSensorValue = {255, 255, 255, 255};
	private boolean[] portSensorActive = {false, false, false, false};
	private int keepAliveTime = 0;
	private boolean keepAliveSet = false;
	private int fileLength = 0;
	private int payloadLength = 0;

	protected byte[] createResponseFromClientRequest(byte[] message) {

		byte[] msgNumber = {0, 0};
		msgNumber[0] = message[0];
		msgNumber[1] = message[1];

		byte commandType = message[2];
		byte commandOpCode;
		if (commandType == EV3CommandType.DIRECT_COMMAND_REPLY.getByte()
				|| commandType == EV3CommandType.DIRECT_COMMAND_NO_REPLY.getByte()) {
			commandOpCode = message[5];
		} else {
			commandOpCode = message[3];
		}

		EV3CommandOpCode opCode = EV3CommandOpCode.getOpCodeByValue(commandOpCode);

		switch (opCode) {

			case OP_UI_READ:
				return new byte[0];

			case OP_INPUT_DEVICE:
				return handleInputDeviceMessage(message, msgNumber);

			case OP_INPUT_READ:
				return handleInputReadMessage(message, msgNumber);

			case OP_INPUT_READ_SI:
				return handleInputReadSiMessage(message, msgNumber);

			case OP_KEEP_ALIVE:
				return handleKeepAliveMessage(message);

			case OP_BEGIN_DOWNLOAD:
				return handleBeginDownloadMessage(message, msgNumber);

			case OP_CONTINUE_DOWNLOAD:
				return handleContinueDownloadMessage(message, msgNumber);

			default:
				return handleUnknownMessage(msgNumber, commandType);
		}
	}

	private byte[] handleKeepAliveMessage(byte[] message) {
		replyRequired = false;

		int keepAliveTime = message[6];

		setKeepAliveTime(keepAliveTime);

		return new byte[0];
	}

	private byte[] getDownloadReply(boolean msgValid, byte[] msgNumber, byte opCode) {
		byte[] reply;
		if (msgValid) {
			reply = new byte[6];

			reply[0] = msgNumber[0];
			reply[1] = msgNumber[1];

			reply[2] = SYSTEM_REPLY;
			reply[3] = EV3CommandOpCode.OP_BEGIN_DOWNLOAD.getByte();

			reply[4] = SUCCESS;
			reply[5] = HANDLE_TO_FILE;
		} else {
			reply = new byte[5];

			reply[0] = msgNumber[0];
			reply[1] = msgNumber[1];

			reply[2] = SYSTEM_REPLY_ERROR;
			reply[3] = EV3CommandOpCode.getOpCodeByValue(opCode).getByte();

			reply[4] = UNKNOWN_ERROR;
		}
		return reply;
	}

	private byte[] handleBeginDownloadMessage(byte[] message, byte[] msgNumber) {
		boolean msgValid = true;

		if (message.length < MIN_BEGIN_DOWNLOAD_LENGTH || message.length > MAX_MESSAGE_LENGTH) {
			msgValid = false;
		} else {
			fileLength = message[4] & 0xFF | (message[5] & 0xFF) << 8
					| (message[6] & 0xFF) << 16 | (message[7] & 0xFF) << 24;
		}

		replyRequired = true;

		return getDownloadReply(msgValid, msgNumber, message[3]);
	}

	private byte[] handleContinueDownloadMessage(byte[] message, byte[] msgNumber) {
		boolean msgValid = true;

		if (message.length < MIN_CONTINUE_DOWNLOAD_LENGTH || message.length > MAX_MESSAGE_LENGTH
				|| message[4] != HANDLE_TO_FILE) {
			msgValid = false;
		} else {
			payloadLength += message.length - 5;
		}
		if (payloadLength > fileLength) {
			msgValid = false;
		}

		replyRequired = true;

		return getDownloadReply(msgValid, msgNumber, message[3]);
	}

	private byte[] handleInputDeviceMessage(byte[] message, byte[] messageNumber) {

		byte[] reply = null;

		int port = -1;
		boolean msgValid = true;

		if (message.length < 10) {
			msgValid = false;
		} else {
			port = message[8];
		}

		replyRequired = true;
		if (message[6] == EV3CommandByte.EV3CommandByteCode.INPUT_DEVICE_STOP_ALL.getByte()) {
			Arrays.fill(portSensorActive, false);
			replyRequired = false;
			return new byte[0];
		}

		if (msgValid && message[6] == EV3CommandByte.EV3CommandByteCode.INPUT_DEVICE_READY_RAW.getByte()) {
			portSensorActive[port] = true;
		}

		if (msgValid && (message[6] == EV3CommandByte.EV3CommandByteCode.INPUT_DEVICE_READY_RAW.getByte()
				|| message[6] == EV3CommandByte.EV3CommandByteCode.INPUT_DEVICE_GET_RAW.getByte())) {

			reply = new byte[4];

			reply[0] = messageNumber[0];
			reply[1] = messageNumber[1];

			reply[2] = DIRECT_REPLY;

			byte value = (byte) portSensorValue[port];

			reply[3] = value;
		} else {
			msgValid = false;
		}

		if (!msgValid) {

			reply = new byte[3];

			reply[0] = messageNumber[0];
			reply[1] = messageNumber[1];

			reply[2] = DIRECT_REPLY_ERROR;
		}
		return reply;
	}

	private byte[] handleInputReadMessage(byte[] message, byte[] messageNumber) {
		byte[] reply = null;
		replyRequired = true;

		boolean msgValid = true;
		int port = -1;

		if (!isMessageLengthValid(message, 11)) {
			msgValid = false;
		} else {
			port = message[7];
		}

		if (msgValid) {
			reply = new byte[4];

			reply[0] = messageNumber[0];
			reply[1] = messageNumber[1];

			reply[2] = DIRECT_REPLY;

			byte value = (byte) portSensorValue[port];

			reply[3] = value;
		} else {
			msgValid = false;
		}

		if (!msgValid) {

			reply = new byte[3];

			reply[0] = messageNumber[0];
			reply[1] = messageNumber[1];

			reply[2] = DIRECT_REPLY_ERROR;
		}
		return reply;
	}

	private byte[] handleInputReadSiMessage(byte[] message, byte[] messageNumber) {
		byte[] reply = null;
		replyRequired = true;

		boolean msgValid = true;
		int port = -1;

		if (!isMessageLengthValid(message, 11)) {
			msgValid = false;
		} else {
			port = message[7];
		}

		if (!msgValid) {

			reply = new byte[3];

			reply[0] = messageNumber[0];
			reply[1] = messageNumber[1];

			reply[2] = DIRECT_REPLY_ERROR;
		} else {
			portSensorMode[port] = message[9];

			reply = new byte[4];

			reply[0] = messageNumber[0];
			reply[1] = messageNumber[1];

			reply[2] = DIRECT_REPLY;

			byte value = (byte) portSensorValue[port];
			reply[3] = value;
		}
		return reply;
	}

	private byte[] handleUnknownMessage(byte[] messageNumber, byte commandType) {
		byte[] reply = null;
		replyRequired = true;

		if (commandType == EV3CommandType.DIRECT_COMMAND_REPLY.getByte()) {

			reply = new byte[3];

			reply[0] = messageNumber[0];
			reply[1] = messageNumber[1];

			reply[2] = DIRECT_REPLY_ERROR;
		}
		return reply;
	}

	private boolean isMessageLengthValid(byte[] message, int expectedMessageLength) {
		if (message.length != expectedMessageLength) {
			return false;
		}
		return true;
	}

	private byte[] getMessageLength(byte[] message) {

		byte[] messageLength = {
				(byte) (message.length & 0x00FF),
				(byte) ((message.length & 0xFF00) >> 8)
		};
		return messageLength;
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

		if (responseMessage == null || !replyRequired) {
			return;
		}

		outStream.write(getMessageLength(responseMessage));
		outStream.write(responseMessage);
		outStream.flush();
	}

	public void generateSensorValue(int port) {

		EV3Sensor.Sensor sensorType = sensors[port];

		Random sensorValueRandom = new Random();
		byte sensorMode = portSensorMode[port];

		switch (sensorType) {

			case NO_SENSOR:
				setSensorValue(port, 255);
				break;
			case TOUCH:
				setSensorValue(port, sensorValueRandom.nextInt(101));
				break;
			case COLOR:
				if (sensorMode == EV3SensorMode.MODE2.getByte()) {
					setSensorValue(port, sensorValueRandom.nextInt(8));
				} else {
					setSensorValue(port, 255);
				}
				break;
			case COLOR_REFLECT:
				if (sensorMode == EV3SensorMode.MODE0.getByte()) {
					setSensorValue(port, sensorValueRandom.nextInt(101));
				} else {
					setSensorValue(port, 255);
				}
				break;
			case COLOR_AMBIENT:
				if (sensorMode == EV3SensorMode.MODE1.getByte()) {
					setSensorValue(port, sensorValueRandom.nextInt(101));
				} else {
					setSensorValue(port, 255);
				}
				break;
			case INFRARED:
				if (sensorMode == EV3SensorMode.MODE0.getByte()) {
					setSensorValue(port, sensorValueRandom.nextInt(101));
				} else {
					setSensorValue(port, 255);
				}
				break;
			default:
				setSensorValue(port, 255);
				break;
		}
	}

	public void setSensorValue(int port, int value) {
		portSensorValue[port] = value;
	}

	public boolean isSensorActive(int port) {
		return portSensorActive[port];
	}

	public void setSensorType(int port, EV3Sensor.Sensor sensorType) {
		sensors[port] = sensorType;
	}

	public void setKeepAliveTime(int keepAliveTimeValue) {
		keepAliveTime = keepAliveTimeValue;
		keepAliveSet = true;
	}

	public int getKeepAliveTime() {
		return keepAliveTime;
	}

	public boolean isKeepAliveSet() {
		return keepAliveSet;
	}

	public boolean payloadLengthEqualsFileLength() {
		return payloadLength == fileLength;
	}
}
