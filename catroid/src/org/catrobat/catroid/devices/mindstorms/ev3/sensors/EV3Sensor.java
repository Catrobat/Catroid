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

package org.catrobat.catroid.devices.mindstorms.ev3.sensors;

import android.util.Log;

import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.devices.mindstorms.MindstormsSensor;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3Command;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandByteCode;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandOpCode;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandParamFormat;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandVariableScope;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandType;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3Reply;

import java.math.BigInteger;
import java.util.Locale;

public abstract class EV3Sensor implements MindstormsSensor {

	public enum Sensor {
		NO_SENSOR,
		TOUCH,
		COLOR,
		COLOR_REFLECT,
		COLOR_AMBIENT,
		INFRARED;

		public static String[] getSensorCodes() {
			String[] valueStrings = new String[values().length];

			for (int i = 0; i < values().length; i++) {
				valueStrings[i] = values()[i].name();
			}

			return valueStrings;
		}

		public String getSensorCode() {
			return getSensorCode(this);
		}

		public static String getSensorCode(EV3Sensor.Sensor sensor) {
			return sensor.name();
		}

		public static EV3Sensor.Sensor getSensorFromSensorCode(String sensorCode) {
			if (sensorCode == null) {
				return Sensor.NO_SENSOR;
			}

			try {
				return valueOf(sensorCode);
			} catch (IllegalArgumentException e) {
				return Sensor.NO_SENSOR;
			}
		}
	}

	protected final int port;
	protected final EV3SensorType sensorType;
	protected final EV3SensorMode sensorMode;
	protected final int updateInterval = 250;

	protected final MindstormsConnection connection;

	protected boolean hasInit;
	protected int lastValidValue = 0;

	public static final String TAG = EV3Sensor.class.getSimpleName();

	public EV3Sensor(int port, EV3SensorType sensorType, EV3SensorMode sensorMode, MindstormsConnection connection) {
		this.port = port;
		this.sensorType = sensorType;
		this.sensorMode = sensorMode;

		this.connection = connection;
	}

	protected void setMode(EV3SensorMode mode) {
		int commandCount = connection.getCommandCounter();

		EV3Command command = new EV3Command(connection.getCommandCounter(), EV3CommandType.DIRECT_COMMAND_REPLY,
				1, 0, EV3CommandOpCode.OP_INPUT_READ_SI);
		connection.incCommandCounter();

		int chainLayer = 0;
		int type = 0; // don't change type
		int samples = 0; // request 0 samples

		command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, chainLayer);
		command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, this.port);
		command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, type);
		command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, mode.getByte());
		command.append(EV3CommandVariableScope.PARAM_VARIABLE_SCOPE_GLOBAL, samples);

		try {
			EV3Reply reply = new EV3Reply(connection.sendAndReceive(command));

			if (!reply.isValid(commandCount)) {
				throw new MindstormsException("Reply not valid!");
			}
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	protected void initialize() {
		if (connection != null && connection.isConnected()) {

			setMode(sensorMode);
			int commandCount = connection.getCommandCounter();

			EV3Command command = new EV3Command(connection.getCommandCounter(), EV3CommandType.DIRECT_COMMAND_REPLY,
					1, 0, EV3CommandOpCode.OP_INPUT_DEVICE);
			connection.incCommandCounter();

			command.append(EV3CommandByteCode.INPUT_DEVICE_READY_RAW.getByte());

			int chainLayer = 0;
			int type = 0;  // don't change type
			int mode = -1; // don't change mode
			int returnValue = 1; // request 1 return value
			int returnValueIndex = 0;

			command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, chainLayer);
			command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, this.port);
			command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, type);
			command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, mode);
			command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, returnValue);
			command.append(EV3CommandVariableScope.PARAM_VARIABLE_SCOPE_GLOBAL, returnValueIndex);

			try {
				EV3Reply reply = new EV3Reply(connection.sendAndReceive(command));

				if (!reply.isValid(commandCount)) {
					throw new MindstormsException("Reply not valid!");
				} else {
					hasInit = true;
				}
			} catch (MindstormsException e) {
				hasInit = false;
				Log.e(TAG, e.getMessage());
			}
		} else {
			hasInit = false;
		}
	}

	public int getPercentValue() {
		int percentValue = 0;

		if (!hasInit) {
			initialize();
		} else {
			int commandCount = connection.getCommandCounter();

			EV3Command command = new EV3Command(connection.getCommandCounter(), EV3CommandType.DIRECT_COMMAND_REPLY,
					1, 0, EV3CommandOpCode.OP_INPUT_READ);
			connection.incCommandCounter();

			int chainLayer = 0;
			int type = 0;  // don't change type
			int mode = -1; // don't change mode
			int returnValueIndex = 0;

			command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, chainLayer);
			command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, this.port);
			command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, type);
			command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, mode);
			command.append(EV3CommandVariableScope.PARAM_VARIABLE_SCOPE_GLOBAL, returnValueIndex);

			try {
				EV3Reply reply = new EV3Reply(connection.sendAndReceive(command));

				if (!reply.isValid(commandCount)) {
					throw new MindstormsException("Reply not valid!");
				}

				percentValue = reply.getByte(3); // first 2 bytes(reply length) not saved
			} catch (MindstormsException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		return percentValue;
	}

	public int getRawValue() {
		int rawValue = 0;

		if (!hasInit) {
			initialize();
		} else {
			int commandCount = connection.getCommandCounter();

			EV3Command command = new EV3Command(connection.getCommandCounter(), EV3CommandType.DIRECT_COMMAND_REPLY,
					1, 0, EV3CommandOpCode.OP_INPUT_DEVICE);
			connection.incCommandCounter();

			int chainLayer = 0;
			int returnValueIndex = 0;

			command.append(EV3CommandByteCode.INPUT_DEVICE_GET_RAW);
			command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, chainLayer);
			command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, this.port);
			command.append(EV3CommandVariableScope.PARAM_VARIABLE_SCOPE_GLOBAL, returnValueIndex);

			try {
				EV3Reply reply = new EV3Reply(connection.sendAndReceive(command));

				if (!reply.isValid(commandCount)) {
					throw new MindstormsException("Reply not valid!");
				}

				int offset = 3;
				int replyLength = reply.getLength();
				byte[] valueBytes = reply.getData(offset, replyLength - offset);
				BigInteger intValue = new BigInteger(valueBytes);

				rawValue = intValue.intValue();
			} catch (MindstormsException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		return rawValue;
	}

	@Override
	public int getUpdateInterval() {
		return updateInterval;
	}

	@Override
	public void updateLastSensorValue() {
		try {
			lastValidValue = getValue();
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public int getLastSensorValue() {
		return lastValidValue;
	}

	@Override
	public String getName() {
		return String.format(Locale.getDefault(), "%s_%s_%d", TAG, sensorType.name(), port);
	}

	@Override
	public int getConnectedPort() {
		return port;
	}
}
