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

package org.catrobat.catroid.devices.mindstorms;

import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseArray;

import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.Constants.LegoSensorType;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3Command;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandType;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;
import org.catrobat.catroid.devices.mindstorms.nxt.Command;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandType;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorMode;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorType;
import org.catrobat.catroid.utils.PausableScheduledThreadPoolExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.EV3_SENSORS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.NXT_SENSORS;

public class LegoSensorService implements CatroidService, SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String TAG = LegoSensorService.class.getSimpleName();
	private static final int SENSOR_UPDATER_THREAD_COUNT = 2;

	private @LegoSensorType int sensorType;
	private SensorRegistry sensorRegistry;
	private LegoSensorFactory sensorFactory;
	private SharedPreferences preferences;
	private PausableScheduledThreadPoolExecutor sensorScheduler;

	private List<OnSensorChangedListener> sensorChangedListeners = new ArrayList<>();

	public LegoSensorService(@LegoSensorType int sensorType, MindstormsConnection connection, SharedPreferences preferences) {
		if (sensorType != Constants.NXT && sensorType != Constants.EV3) {
			throw new IllegalArgumentException("Trying to construct LegoSensorService with invalid sensorType!");
		}

		this.sensorType = sensorType;
		this.preferences = preferences;
		this.sensorRegistry = new SensorRegistry();
		this.sensorFactory = new LegoSensorFactory(connection);
		this.sensorScheduler = new PausableScheduledThreadPoolExecutor(SENSOR_UPDATER_THREAD_COUNT);

		this.preferences.registerOnSharedPreferenceChangeListener(this);
		sensorScheduler.pause();
	}

	public LegoSensor createSensor(@Constants.LegoPort int port) {
		if (port < Constants.PORT_1 || port > Constants.PORT_4) {
			throw new IllegalArgumentException("Trying to create sensor with invalid port number!");
		}

		Enum sensor = getSensorByPort(port);
		if (sensor == EV3Sensor.Sensor.NO_SENSOR || sensor == NXTSensor.Sensor.NO_SENSOR) {
			sensorRegistry.remove(port);
			return null;
		}

		LegoSensor result = sensorFactory.create(sensor, port);
		sensorRegistry.add(result);
		return result;
	}

	private Enum getSensorByPort(@Constants.LegoPort int port) {
		String sensorCode;
		switch (sensorType) {
			case Constants.NXT:
				sensorCode = preferences.getString(NXT_SENSORS[port], null);
				return NXTSensor.Sensor.getSensorFromSensorCode(sensorCode);

			case Constants.EV3:
				sensorCode = preferences.getString(EV3_SENSORS[port], null);
				return EV3Sensor.Sensor.getSensorFromSensorCode(sensorCode);

			default:
				throw new IllegalArgumentException();
		}
	}

	public void pauseSensorUpdate() {
		sensorScheduler.pause();
	}

	public void resumeSensorUpdate() {
		sensorScheduler.resume();
	}

	public void destroy() {
		sensorScheduler.shutdown();
		preferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	public void deactivateAllSensors(MindstormsConnection connection) {
		if (sensorType == Constants.NXT) {
			deactivateAllNxtSensors(connection);
		} else if (sensorType == Constants.EV3) {
			deactivateAllEv3Sensors(connection);
		}
	}

	private void deactivateAllNxtSensors(MindstormsConnection connection) {
		for (int port = 0; port < 4; port++) {
			Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.SET_INPUT_MODE, false);
			command.append((byte) port);
			command.append(NXTSensorType.NO_SENSOR.getByte());
			command.append(NXTSensorMode.RAW.getByte());

			try {
				connection.send(command);
			} catch (MindstormsException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	private void deactivateAllEv3Sensors(MindstormsConnection connection) {
		EV3Command command = new EV3Command(connection.getCommandCounter(), EV3CommandType.DIRECT_COMMAND_NO_REPLY, 0, 0,
				EV3CommandByte.EV3CommandOpCode.OP_INPUT_DEVICE);
		connection.incCommandCounter();

		command.append(EV3CommandByte.EV3CommandByteCode.INPUT_DEVICE_STOP_ALL.getByte());

		int chainLayer = -1; // all chain-layers
		command.append(EV3CommandByte.EV3CommandParamFormat.PARAM_FORMAT_SHORT, chainLayer);

		try {
			connection.send(command);
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void registerOnSensorChangedListener(OnSensorChangedListener listener) {
		sensorChangedListeners.add(listener);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String preference) {
		List<String> sensorSettingKeys = new ArrayList<>();
		sensorSettingKeys.addAll(Arrays.asList(EV3_SENSORS));
		sensorSettingKeys.addAll(Arrays.asList(NXT_SENSORS));

		if (sensorSettingKeys.contains(preference)) {
			for (OnSensorChangedListener listener : sensorChangedListeners) {
				if (listener != null) {
					listener.onSensorChanged();
				}
			}
		}
	}

	public interface OnSensorChangedListener {
		void onSensorChanged();
	}

	private class SensorRegistry {
		private SparseArray<ScheduledFuture> registeredSensors = new SparseArray<>();
		private static final int INITIAL_DELAY = 500;

		public synchronized void add(final LegoSensor sensor) {
			remove(sensor.getConnectedPort());
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					sensor.updateLastSensorValue();
				}
			};
			ScheduledFuture updateSchedule = sensorScheduler.scheduleWithFixedDelay(runnable, INITIAL_DELAY,
					sensor.getUpdateInterval(), TimeUnit.MILLISECONDS);

			registeredSensors.put(sensor.getConnectedPort(), updateSchedule);
		}

		public synchronized void remove(@Constants.LegoPort int port) {
			ScheduledFuture updateSchedule = registeredSensors.get(port);
			if (updateSchedule != null) {
				updateSchedule.cancel(false);
			}
			registeredSensors.remove(port);
		}
	}
}
