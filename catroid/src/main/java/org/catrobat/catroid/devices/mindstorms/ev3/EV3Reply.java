/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

package org.catrobat.catroid.devices.mindstorms.ev3;

import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.devices.mindstorms.MindstormsReply;

public class EV3Reply extends MindstormsReply {

	public static final byte NO_ERROR = 0x02;
	public static final int MIN_REPLY_MESSAGE_LENGTH = 3;

	public static final String TAG = EV3Reply.class.getSimpleName();

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
		return 0x00;
	}

	public boolean isValid(int commandCounter) {
		if (data[0] == (byte) (commandCounter & 0x00FF) && data[1] == ((commandCounter & 0xFF00) >> 8) && !hasError()) {
			return true;
		}
		return false;
	}

	public EV3Reply(byte[] data) {
		super(data);

		if (data.length < MIN_REPLY_MESSAGE_LENGTH) {
			throw new MindstormsException("Invalid EV3 Reply");
		}
	}

	public String toHexString(EV3Reply reply) {
		byte[] rawBytes = reply.getData();
		String commandHexString = "0x";

		if (rawBytes.length == 0) {
			return "null";
		}

		for (int i = 0; i < rawBytes.length; i++) {
			commandHexString += Integer.toHexString(rawBytes[i] & 0xFF);
			commandHexString += "_";
		}

		return commandHexString;
	}
}
