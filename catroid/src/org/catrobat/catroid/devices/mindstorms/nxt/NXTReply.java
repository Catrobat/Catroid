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

import org.catrobat.catroid.devices.mindstorms.MindstormsReply;

public class NXTReply extends MindstormsReply {

	public static final byte NO_ERROR = 0X0;
	public static final int MIN_REPLY_MESSAGE_LENGTH = 3;

	public static final String TAG = NXTReply.class.getSimpleName();

	@Override
	public boolean hasError() {
		if (getStatusByte() == NO_ERROR) {
			return false;
		}
		return true;
	}

	@Override
	public byte getStatusByte() {
		return data[2];
	}

	@Override
	public byte getCommandByte() {
		return data[1];
	}

	public NXTReply(byte[] data) {
		super(data);

		if (data.length < MIN_REPLY_MESSAGE_LENGTH) {
			throw new NXTException("Invalid NXT Reply");
		}

		if (!CommandByte.isMember(data[1])) {
			throw new NXTException("Invalid NXT Reply");
		}

		if (data[0] != CommandType.REPLY_COMMAND.getByte()) {
			throw new NXTException("Invalid NXT Reply");
		}
	}
}
