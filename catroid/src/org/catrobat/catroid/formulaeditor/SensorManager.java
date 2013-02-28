package org.catrobat.catroid.formulaeditor;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;

public class SensorManager implements SensorManagerInterface {
	android.hardware.SensorManager sensorManager;

	public SensorManager(android.hardware.SensorManager sensorManager) {
		this.sensorManager = sensorManager;
	}

	@Override
	public void unregisterListener(SensorEventListener listener) {
		this.sensorManager.unregisterListener(listener);

	}

	@Override
	public boolean registerListener(SensorEventListener listener, Sensor sensor, int rate) {
		return this.sensorManager.registerListener(listener, sensor, rate);
	}

	public Sensor getDefaultSensor(int type) {
		return this.sensorManager.getDefaultSensor(type);
	}

}
