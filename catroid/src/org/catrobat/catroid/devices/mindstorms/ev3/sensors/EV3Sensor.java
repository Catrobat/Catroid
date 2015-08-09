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
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandParamByteCode;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandOpCode;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandType;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3Reply;

import java.util.Locale;

public abstract class EV3Sensor implements MindstormsSensor {

	public enum Sensor {
		NO_SENSOR,
		TOUCH,
		COLOR,
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

		EV3Command command = new EV3Command(connection.getCommandCounter(), EV3CommandType.DIRECT_COMMAND_REPLY,
				1, 0, EV3CommandOpCode.OP_INPUT_READ_SI);
		connection.incCommandCounter();

		int chainLayer = 0;
		int type = 0;
		int tempMode = 0;

		chainLayer = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
				| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
				| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & (byte) chainLayer)
				| EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();

		command.append((byte) chainLayer);

		int port = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
				| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
				| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & (byte) this.port)
				| EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();

		command.append((byte) port);

		type = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
				| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
				| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & (byte) type)
				| EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();

		command.append((byte) type); // don't change type

		tempMode = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
				| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
				| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & mode.getByte())
				| EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();

		command.append((byte) tempMode);

		int sampels = 0;
		sampels = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
				| EV3CommandParamByteCode.PARAM_TYPE_VARIABLE.getByte()
				| EV3CommandParamByteCode.PARAM_VARIABLE_SCOPE_GLOBAL.getByte()
				| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & (byte) sampels)
				| EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();

		command.append((byte) sampels); // request 0 samples

		try {
			connection.sendAndReceive(command);
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	protected void initialize() {
		if (connection != null && connection.isConnected()) {

			setMode(sensorMode);

			EV3Command command = new EV3Command(connection.getCommandCounter(), EV3CommandType.DIRECT_COMMAND_REPLY,
					1, 0, EV3CommandOpCode.OP_INPUT_DEVICE);
			connection.incCommandCounter();

			command.append(EV3CommandByteCode.INPUT_DEVICE_READY_RAW.getByte());

			int chainLayer = 0;
			int type = 0;
			int mode = 1;
			int returnValue = 1;

			chainLayer = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
					| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
					| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & (byte) chainLayer)
					| EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();

			command.append((byte) chainLayer);

			int port = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
					| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
					| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & (byte) this.port)
					| EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();

			command.append((byte) port);

			type = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
					| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
					| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & (byte) type)
					| EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();

			command.append((byte) type); // don't change type

			mode = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
					| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
					| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & (byte) mode)
					| EV3CommandParamByteCode.PARAM_SHORT_SIGN_NEGATIVE.getByte();

			command.append((byte) mode); // don't change mode

			returnValue = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
					| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
					| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & (byte) returnValue)
					| EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();

			command.append((byte) returnValue); // request 1 return value

			// reserve byte for return value
			int index = 0;
			index = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
					| EV3CommandParamByteCode.PARAM_TYPE_VARIABLE.getByte()
					| EV3CommandParamByteCode.PARAM_VARIABLE_SCOPE_GLOBAL.getByte()
					| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & (byte) index)
					| EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();

			command.append((byte) index);

			try {
				connection.sendAndReceive(command);
				hasInit = true;
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
			int type = 0;
			int mode = 1;

			chainLayer = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
					| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
					| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & (byte) chainLayer)
					| EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();

			command.append((byte) chainLayer);

			int port = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
					| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
					| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & (byte) this.port)
					| EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();

			command.append((byte) port);

			type = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
					| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
					| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & (byte) type)
					| EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();

			command.append((byte) type); // don't change type

			mode = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
					| EV3CommandParamByteCode.PARAM_TYPE_CONSTANT.getByte()
					| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & (byte) mode)
					| EV3CommandParamByteCode.PARAM_SHORT_SIGN_NEGATIVE.getByte();

			command.append((byte) mode); // don't change mode

			// reserve byte for return value
			int index = 0;
			index = EV3CommandParamByteCode.PARAM_FORMAT_SHORT.getByte()
					| EV3CommandParamByteCode.PARAM_TYPE_VARIABLE.getByte()
					| EV3CommandParamByteCode.PARAM_VARIABLE_SCOPE_GLOBAL.getByte()
					| (EV3CommandParamByteCode.PARAM_SHORT_MAX.getByte() & (byte) index)
					| EV3CommandParamByteCode.PARAM_SHORT_SIGN_POSITIVE.getByte();

			command.append((byte) index);

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
