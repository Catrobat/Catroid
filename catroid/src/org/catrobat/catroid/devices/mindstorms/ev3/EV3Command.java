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

import org.catrobat.catroid.devices.mindstorms.MindstormsCommand;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandByteCode;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandOpCode;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandParamByteCode;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandParamFormat;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandVariableScope;

import java.io.ByteArrayOutputStream;

public class EV3Command implements MindstormsCommand {

	private ByteArrayOutputStream commandData = new ByteArrayOutputStream();

	public EV3Command(short commandCounter, EV3CommandType commandType, EV3CommandOpCode commandByte) {

		commandData.write((byte) (commandCounter & 0x00FF));
		commandData.write((byte) ((commandCounter & 0xFF00) >> 8));

		commandData.write(commandType.getByte());
		commandData.write(commandByte.getByte());
	}

	public EV3Command(short commandCounter, EV3CommandType commandType, int globalVars, int localVars, EV3CommandOpCode commandByte) {

		commandData.write((byte) (commandCounter & 0x00FF));
		commandData.write((byte) ((commandCounter & 0xFF00) >> 8));

		commandData.write(commandType.getByte());

		byte reservationByte1 = (byte) (globalVars & 0xFF);
		byte reservationByte2 = (byte) ((localVars << 2) | (globalVars >> 8));
		commandData.write(reservationByte1);
		commandData.write(reservationByte2);

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

	public void append(EV3CommandByteCode commandCode) {
		append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, commandCode.getByte());
	}

	public void append(EV3CommandVariableScope variableScope, int bytesToReserve) {

		int byteToAppend = EV3CommandParamFormat.PARAM_FORMAT_SHORT.getByte()
				| EV3CommandParamByteCode.PARAM_TYPE_VARIABLE.getByte()
				| variableScope.getByte()
				| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & bytesToReserve)
				| EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();

		append((byte) byteToAppend);
	}

	public void append(EV3CommandParamFormat paramFormat, int data) {
		int byteToAppend;

		if (paramFormat == EV3CommandParamFormat.PARAM_FORMAT_SHORT) {
			byteToAppend = EV3CommandParamFormat.PARAM_FORMAT_SHORT.getByte()
					| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
					| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & data);

			if (data >= 0) {
				byteToAppend |= EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();
			} else {
				byteToAppend |= EV3CommandParamByteCode.PARAM_SHORT_SIGN_NEGATIVE.getByte();
			}

			append((byte) byteToAppend);
		} else {
			int controlByte;

			if ((data >= 0 && data <= 0x7F) || (data < 0 && data <= 0xFF)) {

				controlByte = EV3CommandParamFormat.PARAM_FORMAT_LONG.getByte()
						| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
						| EV3CommandParamByteCode.PARAM_FOLLOW_ONE_BYTE.getByte();

				byteToAppend = data & 0xFF;

				append((byte) controlByte);
				append((byte) byteToAppend);
			} else {

				int secondByteToAppend;
				controlByte = EV3CommandParamFormat.PARAM_FORMAT_LONG.getByte()
						| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
						| EV3CommandParamByteCode.PARAM_FOLLOW_TWO_BYTE.getByte();

				byteToAppend = data & 0x00FF;
				secondByteToAppend = (data & 0xFF00) >> 8;

				append((byte) controlByte);
				append((byte) byteToAppend);
				append((byte) secondByteToAppend);
			}
		}
	}

	@Override
	public int getLength() {
		return commandData.size();
	}

	public byte[] getRawCommand() {
		return commandData.toByteArray();
	}

	public String toHexString(EV3Command command) {
		byte[] rawBytes = command.getRawCommand();
		String commandHexString = "0x";

		if (rawBytes.length == 0) {
			return "0";
		}

		for (int i = 0; i < rawBytes.length; i++) {
			commandHexString += Integer.toHexString(rawBytes[i] & 0xFF);
			commandHexString += "_";
		}

		return commandHexString;
	}
}
