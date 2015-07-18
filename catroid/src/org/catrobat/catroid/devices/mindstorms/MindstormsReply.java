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
package org.catrobat.catroid.devices.mindstorms;

public abstract class MindstormsReply {

	protected byte[] data;

	public MindstormsReply(byte[] data) {
		this.data = data.clone();
	}

	public abstract boolean hasError();

	public abstract byte getStatusByte();
	public abstract byte getCommandByte();

	public int getLength() {
		return data.length;
	}

	public byte[] getData() {
		return data.clone();
	}

	public byte[] getData(int offset, int length) {
		byte[] a = null;
		if (offset <= data.length - length) {
			a = new byte[length];
			for (int i = 0; i < length; i++) {
				a[i] = data[i + offset];
			}
		}

		return a;
	}

	public byte getByte(int number) {
		return data[number];
	}

	public int getShort(int offset) {
		int value = ((data[offset] & 0xFF) | (data[offset + 1] & 0xFF) << 8);

		return (short) value;
	}

	public int getInt(int offset) {
		int value = ((data[offset] & 0xFF)
				| (data[offset + 1] & 0xFF) << 8
				| (data[offset + 2] & 0xFF) << 16
				| (data[offset + 3] & 0xFF) << 24
		);

		return value;
	}
}
