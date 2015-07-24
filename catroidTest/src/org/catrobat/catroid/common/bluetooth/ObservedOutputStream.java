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
import java.io.OutputStream;

class ObservedOutputStream extends OutputStream {

	private final OutputStream outputStream;
	private final BluetoothLogger logger;

	ObservedOutputStream(OutputStream outputStream, BluetoothLogger logger) {
		this.outputStream = outputStream;
		this.logger = logger;
	}

	@Override
	public void write(byte[] buffer) throws IOException {
		outputStream.write(buffer);
		logger.logSentData(buffer);
	}

	@Override
	public void write(byte[] buffer, int offset, int count) throws IOException {
		outputStream.write(buffer, offset, count);
		logger.logSentData(BluetoothTestUtils.getSubArray(buffer, offset, count));
	}

	@Override
	public void write(int i) throws IOException {
		outputStream.write(i);
		logger.logSentData(BluetoothTestUtils.intToByteArray(i));
	}

	@Override
	public void close() throws IOException {
		super.close();
		outputStream.close();
	}
}
