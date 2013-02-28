package org.catrobat.catroid.uitest.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.catrobat.catroid.formulaeditor.SensorManagerInterface;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class SimulatedSensorManager implements SensorManagerInterface {

	public class Pair<L, R> {
		private L l;
		private R r;

		public Pair(L l, R r) {
			this.l = l;
			this.r = r;
		}

		public L getL() {
			return l;
		}

		public R getR() {
			return r;
		}
	};

	List<Pair<SensorEventListener, Sensor>> listeners;
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

	public SimulatedSensorManager() {
		listeners = new ArrayList<Pair<SensorEventListener, Sensor>>();

		simulationThread = new Thread(new Runnable() {
			public void run() {
				while (simulationThreadRunning) {
					try {
						Thread.sleep(timeOutInMilliSeconds);
						sendGeneratedSensorValues();
					} catch (InterruptedException e) {
					}
				}
			}
		});
		simulationThreadRunning = false;
	}

	public synchronized void startSimulation() {
		if (!simulationThread.isAlive()) {
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

	public synchronized void sendGeneratedSensorValues() {
		for (Pair<SensorEventListener, Sensor> pair : listeners) {
			SensorEventListener sensorEventListener = pair.getL();
			Sensor sensor = pair.getR();

			SensorEvent sensorEvent = null;
			try {
				Constructor<SensorEvent> constructor = SensorEvent.class.getDeclaredConstructor(int.class);
				constructor.setAccessible(true);
				sensorEvent = constructor.newInstance(3);

			} catch (NoSuchMethodException e) {
			} catch (IllegalArgumentException e) {
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
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
					sensorEventListener.onSensorChanged(sensorEvent);
					break;
			}

		}
	}

	public Sensor getDefaultSensor(int typeLinearAcceleration) {
		return null;
	}
}
