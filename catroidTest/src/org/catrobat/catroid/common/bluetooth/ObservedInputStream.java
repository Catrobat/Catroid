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
package org.catrobat.catroid.common.bluetooth;

import java.io.IOException;
import java.io.InputStream;

class ObservedInputStream extends InputStream {
	private final BluetoothLogger logger;
	private final InputStream inputStream;

	ObservedInputStream(InputStream inputStream, BluetoothLogger logger) {
		this.inputStream = inputStream;
		this.logger = logger;
	}

	@Override
	public int read() throws IOException {
		int readByte = inputStream.read();
		logger.logReceivedData(BluetoothTestUtils.intToByteArray(readByte));

		return readByte;
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		int numOfReadBytes = inputStream.read(buffer);
		logger.logReceivedData(buffer);
		return numOfReadBytes;
	}

	@Override
	public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
		int numOfReadBytes = inputStream.read(buffer, byteOffset, byteCount);
		logger.logReceivedData(BluetoothTestUtils.getSubArray(buffer, byteOffset, byteCount));
		return numOfReadBytes;
	}

	@Override
	public void close() throws IOException {
		super.close();
		inputStream.close();
	}
}
