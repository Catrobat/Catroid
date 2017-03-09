/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;

import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3Command;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandOpCode;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandType;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.PausableScheduledThreadPoolExecutor;
import org.catrobat.catroid.utils.Stopwatch;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EV3SensorService implements CatroidService, SharedPreferences.OnSharedPreferenceChangeListener {

	private SensorRegistry sensorRegistry;
	private EV3SensorFactory sensorFactory;

	private SharedPreferences preferences;
	private Context context;

	private PausableScheduledThreadPoolExecutor sensorScheduler;

	private static final String TAG = EV3SensorService.class.getSimpleName();
	private static final int SENSOR_UPDATER_THREAD_COUNT = 2;

	public EV3SensorService(Context context, MindstormsConnection connection) {
		this.context = context;

		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		preferences.registerOnSharedPreferenceChangeListener(this);

		sensorRegistry = new SensorRegistry();
		sensorFactory = new EV3SensorFactory(connection);

		sensorScheduler = new PausableScheduledThreadPoolExecutor(SENSOR_UPDATER_THREAD_COUNT);
		sensorScheduler.pause();
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

	public EV3Sensor createSensor1() {
		EV3Sensor.Sensor sensor = SettingsActivity.getLegoMindstormsEV3SensorMapping(context, SettingsActivity
				.EV3_SENSOR_1);
		return createSensor(sensor, 0);
	}

	public EV3Sensor createSensor2() {
		EV3Sensor.Sensor sensor = SettingsActivity.getLegoMindstormsEV3SensorMapping(context, SettingsActivity
				.EV3_SENSOR_2);
		return createSensor(sensor, 1);
	}

	public EV3Sensor createSensor3() {
		EV3Sensor.Sensor sensor = SettingsActivity.getLegoMindstormsEV3SensorMapping(context, SettingsActivity
				.EV3_SENSOR_3);
		return createSensor(sensor, 2);
	}

	public EV3Sensor createSensor4() {
		EV3Sensor.Sensor sensor = SettingsActivity.getLegoMindstormsEV3SensorMapping(context, SettingsActivity
				.EV3_SENSOR_4);
		return createSensor(sensor, 3);
	}

	private EV3Sensor createSensor(EV3Sensor.Sensor sensorType, int port) {
		if (sensorType == EV3Sensor.Sensor.NO_SENSOR) {
			sensorRegistry.remove(port);
			return null;
		}
		EV3Sensor sensor = sensorFactory.create(sensorType, port);
		sensorRegistry.add(sensor);

		return sensor;
	}

	public void deactivateAllSensors(MindstormsConnection connection) {

		EV3Command command = new EV3Command(connection.getCommandCounter(), EV3CommandType.DIRECT_COMMAND_NO_REPLY, 0, 0,
				EV3CommandOpCode.OP_INPUT_DEVICE);
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

	List<OnSensorChangedListener> sensorChangedListeners = new LinkedList<OnSensorChangedListener>();

	public void registerOnSensorChangedListener(OnSensorChangedListener listener) {
		sensorChangedListeners.add(listener);
	}

	private boolean isChangedPreferenceASensorPreference(String preference) {
		return (preference.equals(SettingsActivity.EV3_SENSOR_1)
				|| preference.equals(SettingsActivity.EV3_SENSOR_2)
				|| preference.equals(SettingsActivity.EV3_SENSOR_3)
				|| preference.equals(SettingsActivity.EV3_SENSOR_4));
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String preference) {

		if (!isChangedPreferenceASensorPreference(preference)) {
			return;
		}

		for (OnSensorChangedListener listener : sensorChangedListeners) {
			if (listener != null) {
				listener.onSensorChanged();
			}
		}
	}

	public interface OnSensorChangedListener {
		void onSensorChanged();
	}

	private static class SensorValueUpdater implements Runnable {
		private EV3Sensor sensor;

		public SensorValueUpdater(EV3Sensor sensor) {
			this.sensor = sensor;
		}

		@Override
		public void run() {
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			sensor.updateLastSensorValue();
		}
	}

	private class SensorRegistry {

		private class SensorTuple {

			public ScheduledFuture scheduledFuture;
			public EV3Sensor sensor;

			public SensorTuple(ScheduledFuture scheduledFuture, EV3Sensor sensor) {
				this.scheduledFuture = scheduledFuture;
				this.sensor = sensor;
			}
		}

		private SparseArray<SensorTuple> registeredSensors = new SparseArray<SensorTuple>();
		private static final int INITIAL_DELAY = 500;

		public synchronized void add(EV3Sensor sensor) {
			remove(sensor.getConnectedPort());
			ScheduledFuture scheduledFuture = sensorScheduler.scheduleWithFixedDelay(new SensorValueUpdater(sensor),
					INITIAL_DELAY, sensor.getUpdateInterval(), TimeUnit.MILLISECONDS);

			registeredSensors.put(sensor.getConnectedPort(), new SensorTuple(scheduledFuture, sensor));
		}

		public synchronized void remove(EV3Sensor sensor) {
			int port = sensor.getConnectedPort();
			remove(port);
		}

		public synchronized void remove(int port) {
			SensorTuple tuple = registeredSensors.get(port);
			if (tuple != null) {
				tuple.scheduledFuture.cancel(false);
			}
			registeredSensors.remove(port);
		}
	}
}
