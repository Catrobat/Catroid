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
package org.catrobat.catroid.devices.mindstorms.nxt;

import org.catrobat.catroid.devices.mindstorms.MindstormsCommand;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class Command implements MindstormsCommand {

	private static final String TAG = Command.class.getSimpleName();

	private ByteArrayOutputStream commandData = new ByteArrayOutputStream();

	public Command(CommandType commandType, CommandByte commandByte, boolean reply) {

		if (reply) {
			commandData.write(commandType.getByte());
		} else {
			commandData.write((byte) (commandType.getByte() | 0x80));
		}
		commandData.write(commandByte.getByte());
	}

	public void append(byte data) {
		commandData.write(data);
	}

	public void append(byte[] data) {
		commandData.write(data, 0, data.length);
	}

	public void append(int data) {
		append((byte) (0xFF & data));
		append((byte) (0xFF & (data >> 8)));
		append((byte) (0xFF & (data >> 16)));
		append((byte) (0xFF & (data >> 24)));
	}

	@Override
	public int getLength() {
		return commandData.size();
	}

	public byte[] getRawCommand() {
		return commandData.toByteArray();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Command)) {
			return false;
		}

		Command command = (Command) obj;
		return Arrays.equals(commandData.toByteArray(), command.commandData.toByteArray());
	}

	@Override
	public int hashCode() {
		return super.hashCode() * TAG.hashCode();
	}
}
