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

package org.catrobat.catroid.test.utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.formulaeditor.SensorManagerInterface;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimulatedSensorManager implements SensorManagerInterface {
	private static final String TAG = SimulatedSensorManager.class.getSimpleName();

	public class Pair<L, R> {
		private L firstEntry;
		private R secondEntry;

		public Pair(L l, R r) {
			this.firstEntry = l;
			this.secondEntry = r;
		}

		public L getL() {
			return firstEntry;
		}

		public R getR() {
			return secondEntry;
		}
	}

	List<Pair<SensorEventListener, Sensor>> listeners;
	List<Pair<SensorCustomEventListener, Sensors>> customListeners;
	Thread simulationThread;
	boolean simulationThreadRunning;
	int timeOutInMilliSeconds = 100;

	// units of m/s^2
	float linearAccelationMinimumX = -10;
	float linearAccelerationMaximumX = 10;

	float linearAccelationMinimumY = -10;
	float linearAccelerationMaximumY = 10;

	float linearAccelationMinimumZ = -10;
	float linearAccelerationMaximumZ = 10;

	float rotationMinimumX = (float) -Math.PI * 0.5f;
	float rotationMaximumX = (float) Math.PI * 0.5f;

	float rotationMinimumY = (float) -Math.PI * 0.5f;
	float rotationMaximumY = (float) Math.PI * 0.5f;

	float rotationMinimumZ = (float) -Math.PI * 0.5f;
	float rotationMaximumZ = (float) Math.PI * 0.5f;

	float rotationAngleMinimum = 0;
	float rotationAngleMaximum = (float) (2 * Math.PI);

	private List<SensorEvent> sentSensorEvents;
	private List<SensorCustomEvent> sentSensorCustomEvents;

	private long lastExecution = 0;

	public SimulatedSensorManager() {
		sentSensorEvents = new ArrayList<SensorEvent>();
		sentSensorCustomEvents = new ArrayList<SensorCustomEvent>();
		listeners = new ArrayList<Pair<SensorEventListener, Sensor>>();
		customListeners = new ArrayList<Pair<SensorCustomEventListener, Sensors>>();
		createSimulationThread();
	}

	private void createSimulationThread() {
		simulationThread = new Thread(new Runnable() {
			public void run() {
				while (simulationThreadRunning) {
					lastExecution = System.currentTimeMillis();
					while (System.currentTimeMillis() < lastExecution + timeOutInMilliSeconds) {
						Thread.yield();
					}

					sendGeneratedSensorValues();
				}
			}
		});
		simulationThreadRunning = false;
	}

	public synchronized void startSimulation() {
		if (!simulationThreadRunning && !simulationThread.isAlive()) {
			simulationThreadRunning = true;
			simulationThread.start();
		}
	}

	public synchronized void stopSimulation() {
		simulationThreadRunning = false;
	}

	public synchronized void unregisterListener(SensorEventListener listener) {
		listeners.remove(listener);

		Iterator<Pair<SensorEventListener, Sensor>> iterator = listeners.iterator();

		while (iterator.hasNext()) {
			Pair<SensorEventListener, Sensor> pair = iterator.next();
			if (pair.getL() == listener) {
				iterator.remove();
			}
		}
	}

	public synchronized boolean registerListener(SensorEventListener listener, Sensor sensor, int rate) {
		listeners.add(new Pair<SensorEventListener, Sensor>(listener, sensor));
		return false;
	}

	public synchronized void unregisterListener(SensorCustomEventListener listener) {
		customListeners.remove(listener);

		Iterator<Pair<SensorCustomEventListener, Sensors>> iterator = customListeners.iterator();

		while (iterator.hasNext()) {
			Pair<SensorCustomEventListener, Sensors> pair = iterator.next();
			if (pair.getL() == listener) {
				iterator.remove();
			}
		}
	}

	public synchronized boolean registerListener(SensorCustomEventListener listener, Sensors sensor) {
		customListeners.add(new Pair<SensorCustomEventListener, Sensors>(listener, sensor));
		return false;
	}

	public synchronized void sendGeneratedSensorValues() {
		for (Pair<SensorEventListener, Sensor> pair : listeners) {
			SensorEventListener sensorEventListener = pair.getL();
			Sensor sensor = pair.getR();

			SensorEvent sensorEvent = null;
			try {
				Constructor<SensorEvent> constructor = SensorEvent.class.getDeclaredConstructor(int.class);
				constructor.setAccessible(true);
				sensorEvent = constructor.newInstance(3);
			} catch (NoSuchMethodException | IllegalArgumentException | InstantiationException
					| IllegalAccessException | InvocationTargetException ex) {
				Log.e(TAG, "Sleep was interrupted.", ex);
			}

			if (sensorEvent == null) {
				return;
			}

			switch (sensor.getType()) {
				case Sensor.TYPE_LINEAR_ACCELERATION:
					sensorEvent.sensor = sensor;

					float[] sensorValues = new float[3];

					sensorValues[0] = (float) (linearAccelationMinimumX + Math.random()
							* (linearAccelerationMaximumX - linearAccelationMinimumX));
					sensorValues[1] = (float) (linearAccelationMinimumY + Math.random()
							* (linearAccelerationMaximumY - linearAccelationMinimumY));
					sensorValues[2] = (float) (linearAccelationMinimumZ + Math.random()
							* (linearAccelerationMaximumZ - linearAccelationMinimumZ));

					Reflection.setPrivateField(SensorEvent.class, sensorEvent, "values", sensorValues);

					sentSensorEvents.add(0, sensorEvent);
					sentSensorEvents = sentSensorEvents.subList(0,
							sentSensorEvents.size() < 50 ? sentSensorEvents.size() : 50);
					sensorEventListener.onSensorChanged(sensorEvent);
					break;
				case Sensor.TYPE_ROTATION_VECTOR:
					sensorEvent.sensor = sensor;

					sensorValues = new float[3];
					sensorValues[0] = (float) (rotationMinimumX + Math.random() * (rotationMaximumX - rotationMinimumX));
					sensorValues[1] = (float) (rotationMinimumY + Math.random() * (rotationMaximumY - rotationMinimumY));
					sensorValues[2] = (float) (rotationMinimumZ + Math.random() * (rotationMaximumZ - rotationMinimumZ));

					sensorValues[0] *= Math.sin((rotationAngleMinimum + Math.random()
							* (rotationAngleMaximum - rotationAngleMinimum)) * 0.5f);
					sensorValues[1] *= Math.sin((rotationAngleMinimum + Math.random()
							* (rotationAngleMaximum - rotationAngleMinimum)) * 0.5f);
					sensorValues[2] *= Math.sin((rotationAngleMinimum + Math.random()
							* (rotationAngleMaximum - rotationAngleMinimum)) * 0.5f);

					Reflection.setPrivateField(SensorEvent.class, sensorEvent, "values", sensorValues);
					sentSensorEvents.add(0, sensorEvent);
					sentSensorEvents = sentSensorEvents.subList(0,
							sentSensorEvents.size() < 50 ? sentSensorEvents.size() : 50);
					sensorEventListener.onSensorChanged(sensorEvent);
					break;
			}
		}
		for (Pair<SensorCustomEventListener, Sensors> pair : customListeners) {
			SensorCustomEventListener sensorEventListener = pair.getL();
			Sensors sensor = pair.getR();
			float[] values = new float[1];

			switch (sensor) {
				case LOUDNESS:
					values[0] = (float) Math.random() * 100;
					break;
				default:
					break;
			}

			SensorCustomEvent sensorEvent = new SensorCustomEvent(sensor, values);

			sentSensorCustomEvents.add(0, sensorEvent);
			sentSensorCustomEvents = sentSensorCustomEvents.subList(0,
					sentSensorCustomEvents.size() < 50 ? sentSensorCustomEvents.size() : 50);
			sensorEventListener.onCustomSensorChanged(sensorEvent);
		}
	}

	public Sensor getDefaultSensor(int typeLinearAcceleration) {
		return null;
	}

	public synchronized SensorEvent getLatestSensorEvent(Sensor sensor) {
		Iterator<SensorEvent> iterator = sentSensorEvents.iterator();

		while (iterator.hasNext()) {
			SensorEvent sensorEvent = iterator.next();
			if (sensorEvent.sensor == sensor) {
				return sensorEvent;
			}
		}
		return null;
	}

	public synchronized SensorCustomEvent getLatestCustomSensorEvent(Sensors sensor) {
		Iterator<SensorCustomEvent> iterator = sentSensorCustomEvents.iterator();

		while (iterator.hasNext()) {
			SensorCustomEvent sensorEvent = iterator.next();
			if (sensorEvent.sensor == sensor) {
				return sensorEvent;
			}
		}
		return null;
	}
}
