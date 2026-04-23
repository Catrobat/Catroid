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

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.devices.mindstorms.LegoSensor;
import org.catrobat.catroid.devices.mindstorms.LegoSensorService;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnectionImpl;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.util.UUID;

public class LegoNXTImpl implements LegoNXT, LegoSensorService.OnSensorChangedListener {

	private static final UUID LEGO_NXT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String TAG = LegoNXTImpl.class.getSimpleName();

	protected MindstormsConnection mindstormsConnection;
	protected Context context;

	private boolean isInitialized = false;

	private NXTMotor motorA;
	private NXTMotor motorB;
	private NXTMotor motorC;

	private LegoSensor sensor1;
	private LegoSensor sensor2;
	private LegoSensor sensor3;
	private LegoSensor sensor4;

	private LegoSensorService sensorService;

	public LegoNXTImpl(Context applicationContext) {
		this.context = applicationContext;
	}

	@Override
	public String getName() {
		return "Lego NXT";
	}

	@Override
	public Class<? extends BluetoothDevice> getDeviceType() {
		return BluetoothDevice.LEGO_NXT;
	}

	@Override
	public void setConnection(BluetoothConnection btConnection) {
		this.mindstormsConnection = new MindstormsConnectionImpl(btConnection);
	}

	@Override
	public UUID getBluetoothDeviceUUID() {
		return LEGO_NXT_UUID;
	}

	@Override
	public void disconnect() {
		if (mindstormsConnection.isConnected()) {
			this.stopAllMovements();

			if (sensorService != null) {
				sensorService.deactivateAllSensors(mindstormsConnection);
				sensorService.destroy();
			}
			mindstormsConnection.disconnect();
		}
	}

	@Override
	public boolean isAlive() {
		try {
			tryGetKeepAliveTime();
			return true;
		} catch (MindstormsException e) {
			return false;
		}
	}

	@Override
	public void playTone(int frequencyInHz, int durationInMs) {

		if (durationInMs <= 0) {
			return;
		}

		if (frequencyInHz > 14000) {
			frequencyInHz = 14000;
		} else if (frequencyInHz < 200) {
			frequencyInHz = 200;
		}

		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.PLAY_TONE, false);
		command.append((byte) (frequencyInHz & 0x00FF));
		command.append((byte) ((frequencyInHz & 0xFF00) >> 8));
		command.append((byte) (durationInMs & 0x00FF));
		command.append((byte) ((durationInMs & 0xFF00) >> 8));

		try {
			mindstormsConnection.send(command);
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public int getKeepAliveTime() {
		try {
			return tryGetKeepAliveTime();
		} catch (NXTException e) {
			return -1;
		} catch (MindstormsException e) {
			return -1;
		}
	}

	private int tryGetKeepAliveTime() throws MindstormsException {
		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.KEEP_ALIVE, true);

		byte[] alive = mindstormsConnection.sendAndReceive(command);

		NXTReply reply = new NXTReply(mindstormsConnection.sendAndReceive(command));
		NXTError.checkForError(reply, 7);

		byte[] aliveTimeToInt = new byte[4];
		aliveTimeToInt[0] = alive[3];
		aliveTimeToInt[1] = alive[4];
		aliveTimeToInt[2] = alive[5];
		aliveTimeToInt[3] = alive[6];

		int aliveTime = java.nio.ByteBuffer.wrap(aliveTimeToInt).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
		return aliveTime;
	}

	@Override
	public int getBatteryLevel() {
		try {
			return tryGetBatteryLevel();
		} catch (NXTException e) {
			return -1;
		} catch (MindstormsException e) {
			return -1;
		}
	}

	private int tryGetBatteryLevel() throws MindstormsException {
		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.GET_BATTERY_LEVEL, true);

		NXTReply reply = new NXTReply(mindstormsConnection.sendAndReceive(command));
		NXTError.checkForError(reply, 5);

		byte[] batByte = mindstormsConnection.sendAndReceive(command);
		byte[] batValues = new byte[2];
		batValues[0] = batByte[3];
		batValues[1] = batByte[4];

		int millivolt = java.nio.ByteBuffer.wrap(batValues).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort();

		return millivolt;
	}

	@Override
	public NXTMotor getMotorA() {
		return motorA;
	}

	@Override
	public NXTMotor getMotorB() {
		return motorB;
	}

	@Override
	public NXTMotor getMotorC() {
		return motorC;
	}

	@Override
	public void stopAllMovements() {
		motorA.stop();
		motorB.stop();
		motorC.stop();
	}

	@Override
	public synchronized float getSensorValue(Sensors sensor) {

		switch (sensor) {
			case NXT_SENSOR_1:
				return sensor1 != null ? sensor1.getLastSensorValue() : 0;
			case NXT_SENSOR_2:
				return sensor2 != null ? sensor2.getLastSensorValue() : 0;
			case NXT_SENSOR_3:
				return sensor3 != null ? sensor3.getLastSensorValue() : 0;
			case NXT_SENSOR_4:
				return sensor4 != null ? sensor4.getLastSensorValue() : 0;
		}

		return -1;
	}

	@Override
	public LegoSensor getSensor1() {
		return sensor1;
	}

	@Override
	public LegoSensor getSensor2() {
		return sensor2;
	}

	@Override
	public LegoSensor getSensor3() {
		return sensor3;
	}

	@Override
	public LegoSensor getSensor4() {
		return sensor4;
	}

	@Override
	public void onSensorChanged() {
		assignSensorsToPorts();
	}

	@Override
	public synchronized void initialise() {

		if (isInitialized) {
			return;
		}

		try {
			mindstormsConnection.init();
		} catch (MindstormsException e) {
			isInitialized = false;
			return;
		}

		motorA = new NXTMotor(0, mindstormsConnection);
		motorB = new NXTMotor(1, mindstormsConnection);
		motorC = new NXTMotor(2, mindstormsConnection);

		assignSensorsToPorts();

		isInitialized = true;
	}

	private synchronized void assignSensorsToPorts() {
		if (sensorService == null) {
			sensorService = new LegoSensorService(Constants.NXT, mindstormsConnection,
					PreferenceManager.getDefaultSharedPreferences(context));
			sensorService.registerOnSensorChangedListener(this);
		}

		sensor1 = sensorService.createSensor(Constants.PORT_1);
		sensor2 = sensorService.createSensor(Constants.PORT_2);
		sensor3 = sensorService.createSensor(Constants.PORT_3);
		sensor4 = sensorService.createSensor(Constants.PORT_4);
	}

	@Override
	public void start() {
		initialise();

		assignSensorsToPorts();

		sensorService.resumeSensorUpdate();
	}

	@Override
	public void pause() {
		stopAllMovements();
		sensorService.pauseSensorUpdate();
	}

	@Override
	public void destroy() {
		sensorService.deactivateAllSensors(mindstormsConnection);
	}
}
