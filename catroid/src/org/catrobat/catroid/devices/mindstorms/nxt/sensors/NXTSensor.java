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
package org.catrobat.catroid.devices.mindstorms.nxt.sensors;

import android.util.Log;

import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.devices.mindstorms.MindstormsSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.Command;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandType;
import org.catrobat.catroid.devices.mindstorms.nxt.NXTError;
import org.catrobat.catroid.devices.mindstorms.nxt.NXTReply;

import java.util.Locale;

public abstract class NXTSensor implements MindstormsSensor {

	public enum Sensor {
		NO_SENSOR,
		TOUCH,
		SOUND,
		LIGHT_INACTIVE,
		LIGHT_ACTIVE,
		ULTRASONIC;

		public static String[] getSensorCodes() {
			String[] valueStrings = new String[values().length];

			for (int i = 0; i < values().length; ++i) {
				valueStrings[i] = values()[i].name();
			}

			return valueStrings;
		}

		public String getSensorCode() {
			return getSensorCode(this);
		}

		public static String getSensorCode(NXTSensor.Sensor sensor) {
			return sensor.name();
		}

		public static NXTSensor.Sensor getSensorFromSensorCode(String sensorCode) {
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
	protected final NXTSensorType sensorType;
	protected final NXTSensorMode sensorMode;
	protected final int updateInterval = 250;

	protected final MindstormsConnection connection;

	protected boolean hasInit;
	protected int lastValidValue = 0;

	public static final String TAG = NXTSensor.class.getSimpleName();

	public NXTSensor(int port, NXTSensorType sensorType, NXTSensorMode sensorMode, MindstormsConnection connection) {
		this.port = port;
		this.sensorType = sensorType;
		this.sensorMode = sensorMode;

		this.connection = connection;
	}

	protected void updateTypeAndMode() {

		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.SET_INPUT_MODE, true);
		command.append((byte) port);
		command.append(sensorType.getByte());
		command.append(sensorMode.getByte());

		NXTReply reply = new NXTReply(connection.sendAndReceive(command));
		NXTError.checkForError(reply, 3);
	}

	protected int getScaledValue() {
		return getSensorReadings().scaled;
	}

	protected int getRawValue() {
		return getSensorReadings().raw;
	}

	protected int getNormalizedValue() {
		return getSensorReadings().normalized;
	}

	public SensorReadings getSensorReadings() {
		if (!hasInit) {
			initialize();
		}

		SensorReadings sensorReadings = new SensorReadings();
		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.GET_INPUT_VALUES, true);
		command.append((byte) port);
		NXTReply reply = new NXTReply(connection.sendAndReceive(command));
		NXTError.checkForError(reply, 16);

		sensorReadings.raw = reply.getShort(8);
		sensorReadings.normalized = reply.getShort(10);
		sensorReadings.scaled = reply.getShort(12);
		return sensorReadings;
	}

	protected void initialize() {
		if (connection != null && connection.isConnected()) {
			updateTypeAndMode();
			try {
				Thread.sleep(100);
				resetScaledValue();
				Thread.sleep(100);
				updateTypeAndMode();
				hasInit = true;
			} catch (InterruptedException e) {
				hasInit = false;
			}
		} else {
			hasInit = false;
		}
	}

	protected void resetScaledValue() {
		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.RESET_INPUT_SCALED_VALUE, false);
		command.append((byte) port);
		connection.send(command);
	}

	private static class SensorReadings {
		public int raw;
		public int normalized;
		public int scaled;
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
