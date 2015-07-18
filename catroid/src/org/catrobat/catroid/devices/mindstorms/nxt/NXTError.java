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
package org.catrobat.catroid.devices.mindstorms.nxt;

import android.util.SparseArray;

import org.catrobat.catroid.devices.mindstorms.MindstormsReply;

public final class NXTError {
	private NXTError() {
	}

	public static final String TAG = NXTError.class.getSimpleName();

	public static void checkForError(MindstormsReply reply, int expectedLength) {
		if (reply.hasError()) {
			throw new NXTException(ErrorCode.getTypeByValue(reply.getStatusByte()),
					CommandByte.getTypeByValue(reply.getCommandByte()));
		}

		if (reply.getLength() != expectedLength) {
			throw new NXTException(ErrorCode.WrongNumberOfBytes,
					CommandByte.getTypeByValue(reply.getCommandByte()));
		}
	}

	public enum ErrorCode {
		WrongNumberOfBytes(0x78), UnknownErrorCode(0x79), I2CTimeOut(0x80), NoMoreHandles(0x81), NoSpace(0x82), NoMoreFiles(0x83),
		EndOfFileExpected(0x84), EndOfFile(0x85), NotALinearFile(0x86), FileNotFound(0x87),
		HandleAlreadyClosed(0x88), NoLinearSpace(0x89), UndefinedFileError(0x8a), FileBusy(0x8b),
		NoWriteBuffers(0x8c), AppendNotPossible(0x8d), FileIsFull(0x8e), FileAlreadyExists(0x8f),
		ModuleNotFound(0x90), OutOfBoundary(0x91), IllegalFileName(0x92), IllegalHandle(0x93),
		PendingCommunication(0x20), MailboxQueueEmpty(0x40), RequestFailed(0xbd), UnknownCommand(0xbe),
		InsanePacket(0xbf), DataOutOfRange(0xc0), CommunicationBusError(0xdd), BufferFull(0xde),
		InvalidChannel(0xdf), ChannelBusy(0xe0), NoActiveProgram(0xec), IllegalSize(0xed),
		InvalidMailboxQueue(0xee), InvalidField(0xef), BadIO(0xf0), OutOfMemory(0xfb), BadArguments(0xff);

		private final int errorCodeValue;
		private static final SparseArray<ErrorCode> LOOKUP = new SparseArray<ErrorCode>();
		static {
			for (ErrorCode c : ErrorCode.values()) {
				LOOKUP.put(c.errorCodeValue, c);
			}
		}
		private ErrorCode(int errorCodeValue) {
			this.errorCodeValue = errorCodeValue;
		}

		public byte getByte() {
			return (byte) errorCodeValue;
		}

		public static ErrorCode getTypeByValue(byte value) {
			return LOOKUP.get(value & 0xFF);
		}
	}
}

