/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.devices.mindstorms.nxt.sensors;

import android.util.Log;

import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.devices.mindstorms.nxt.Command;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandType;
import org.catrobat.catroid.devices.mindstorms.nxt.NXTError;
import org.catrobat.catroid.devices.mindstorms.nxt.NXTException;
import org.catrobat.catroid.devices.mindstorms.nxt.NXTReply;
import org.catrobat.catroid.utils.Stopwatch;

import androidx.annotation.VisibleForTesting;

public abstract class NXTI2CSensor extends NXTSensor {

	private byte address;
	private int pendingCommunicationErrorWaitTime;
	private final int requestTimeout = 500; //in MS

	private static final byte BYTES_READ_BYTE = 3;

	private static final String TAG = NXTI2CSensor.class.getSimpleName();

	public NXTI2CSensor(byte sensorAddress, NXTSensorType sensorType, MindstormsConnection connection) {
		super(3, sensorType, NXTSensorMode.RAW, connection);
		address = sensorAddress;
		pendingCommunicationErrorWaitTime = 30;
	}

	public byte getI2CAddress() {
		return address;
	}

	@Override
	protected void initialize() throws MindstormsException {
		super.initialize();
		readRegister(0x00, 0x01);
	}

	protected void writeRegister(byte register, byte data, boolean reply) throws MindstormsException {
		if (!hasInit) {
			initialize();
		}
		byte[] command = {address, register, data};
		write(command, (byte) 0, reply);
	}

	protected byte[] readRegister(int register, int rxLength) throws MindstormsException {
		if (!hasInit) {
			initialize();
		}
		byte[] command = {address, (byte) register};
		return writeAndRead(command, (byte) rxLength);
	}

	private void waitForBytes(byte numberOfBytes) throws MindstormsException {
		Stopwatch stopWatch = new Stopwatch();
		byte bytesRead = 0;
		stopWatch.start();
		do {
			bytesRead = tryGetNumberOfBytesAreReadyToRead();
		} while (bytesRead != numberOfBytes && stopWatch.getElapsedMilliseconds() < requestTimeout);

		if (stopWatch.getElapsedMilliseconds() > requestTimeout) {
			throw new NXTException("RequestTimeout while waiting on bytes Ready, waited " + stopWatch.getElapsedMilliseconds() + "ms");
		}
	}

	private byte tryGetNumberOfBytesAreReadyToRead() throws MindstormsException {
		try {
			return getNumberOfBytesAreReadyToRead();
		} catch (NXTException e) {
			if (e.getError() == NXTError.ErrorCode.PendingCommunication) {
				Log.e(TAG, "Pending Coummunication Error occured, wait for " + pendingCommunicationErrorWaitTime + "ms and try again.");
				wait(pendingCommunicationErrorWaitTime);
				return 0;
			}

			throw e;
		}
	}

	protected byte[] writeAndRead(byte[] data, byte rxLength) throws MindstormsException {
		write(data, rxLength, false);
		waitForBytes(rxLength);
		return read();
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public void write(byte[] txData, byte rxLength, boolean reply) throws MindstormsException {
		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.LS_WRITE, reply);
		command.append((byte) port);
		command.append((byte) txData.length);
		command.append(rxLength);
		command.append(txData);

		if (reply) {
			NXTReply brickReply = new NXTReply(connection.sendAndReceive(command));
			NXTError.checkForError(brickReply, 5);
		} else {
			connection.send(command);
		}
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public byte[] read() throws MindstormsException {
		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.LS_READ, true);
		command.append((byte) port);
		NXTReply reply = new NXTReply(connection.sendAndReceive(command));
		NXTError.checkForError(reply, 20);
		byte size = reply.getByte(BYTES_READ_BYTE);
		return reply.getData(4, size);
	}

	@VisibleForTesting
	public byte getNumberOfBytesAreReadyToRead() throws MindstormsException {
		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.LS_GET_STATUS, true);
		command.append((byte) port);
		NXTReply reply = new NXTReply(connection.sendAndReceive(command));
		NXTError.checkForError(reply, 4);
		return reply.getByte(BYTES_READ_BYTE);
	}

	protected void wait(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException interruptedException) {
			Log.w(TAG, "Shouldn't be interrupted", interruptedException);
		}
	}
}
