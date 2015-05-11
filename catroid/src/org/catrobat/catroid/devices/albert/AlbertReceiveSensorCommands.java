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

package org.catrobat.catroid.devices.albert;

import java.util.Arrays;

public class AlbertReceiveSensorCommands {

	private byte[] buffer;

	private byte sendFrameNumber = 1;
	private int distanceLeft;
	private int distanceRight;

	public AlbertReceiveSensorCommands(int size) {
		this.buffer = new byte[size];
		buffer[0] = AlbertConnection.PACKET_HEADER_1;
		buffer[1] = AlbertConnection.PACKET_HEADER_2;
		buffer[2] = (byte) (size - 2);
		buffer[3] = AlbertConnection.COMMAND_SENSOR;
		buffer[4] = (byte) 0x11; // model
		buffer[5] = sendFrameNumber;
		buffer[6] = (byte) 0;
		buffer[7] = (byte) 0xFF;
		buffer[8] = (byte) 0;        // Battery high
		buffer[9] = (byte) 0;        // Battery low
		buffer[10] = (byte) 0;        // RSSI
		buffer[11] = (byte) 0;        // Front ALS High
		buffer[12] = (byte) 0;        // Front ALS Low
		buffer[13] = (byte) 0;        // Front IR right frame 0
		buffer[14] = (byte) 0;        // Front IR left frame 0
		buffer[15] = (byte) 0;        // Front IR right frame 1
		buffer[16] = (byte) 0;        // Front IR left frame 1
		buffer[17] = (byte) 0;        // Front IR right frame 2
		buffer[18] = (byte) 0;        // Front IR left frame 2
		buffer[19] = (byte) 0;        // Front IR right frame 3
		buffer[20] = (byte) 0;        // Front IR left frame 3
		buffer[size - 2] = (byte) 0x0D;    // TAIL
		buffer[size - 1] = (byte) 0x0A;    // TAIL
	}

	public void setDistanceLeft(int distance) {
		this.distanceLeft = distance;
	}

	public void setDistanceRight(int distance) {
		this.distanceRight = distance;
	}

	public byte[] getSensorCommandMessage() {
		buffer[5] = (byte) (sendFrameNumber + 1);
		sendFrameNumber = (byte) (sendFrameNumber + 2);
		this.buffer[13] = (byte) distanceRight;
		this.buffer[15] = (byte) distanceRight;
		this.buffer[17] = (byte) distanceRight;
		this.buffer[19] = (byte) distanceRight;
		this.buffer[14] = (byte) distanceLeft;
		this.buffer[16] = (byte) distanceLeft;
		this.buffer[18] = (byte) distanceLeft;
		this.buffer[20] = (byte) distanceLeft;
		return Arrays.copyOf(buffer, buffer.length);
	}


}
