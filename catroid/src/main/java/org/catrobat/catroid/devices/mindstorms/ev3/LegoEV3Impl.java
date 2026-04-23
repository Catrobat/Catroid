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

package org.catrobat.catroid.devices.mindstorms.ev3;

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
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandByteCode;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandOpCode;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandParamFormat;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.util.UUID;

public class LegoEV3Impl implements LegoEV3, LegoSensorService.OnSensorChangedListener {

	private static final UUID LEGO_EV3_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String TAG = LegoEV3Impl.class.getSimpleName();

	private static final int KEEP_ALIVE_TIME = 5;

	private static final int NUMBER_VOLUME_LEVELS = 13;
	private static final int VOLUME_LEVEL_INCR = 8;
	private static final int FREQ_MAX_VAL = 10000;
	private static final int FREQ_MIN_VAL = 250;

	private boolean isInitialized = false;

	protected MindstormsConnection mindstormsConnection;
	protected Context context;

	private EV3Motor motorA;
	private EV3Motor motorB;
	private EV3Motor motorC;
	private EV3Motor motorD;

	private LegoSensor sensor1;
	private LegoSensor sensor2;
	private LegoSensor sensor3;
	private LegoSensor sensor4;

	private LegoSensorService sensorService;

	public LegoEV3Impl(Context applicationContext) {
		this.context = applicationContext;
	}

	@Override
	public String getName() {
		return "Lego EV3";
	}

	@Override
	public Class<? extends BluetoothDevice> getDeviceType() {
		return BluetoothDevice.LEGO_EV3;
	}

	@Override
	public void playTone(int frequencyInHz, int durationInMs, int volumeInPercent) {

		if (volumeInPercent > 100) {
			volumeInPercent = 100;
		} else if (volumeInPercent < 0) {
			volumeInPercent = 0;
		}

		if (durationInMs <= 0 | volumeInPercent == 0) {
			return;
		}

		if (frequencyInHz > FREQ_MAX_VAL) {
			frequencyInHz = FREQ_MAX_VAL;
		} else if (frequencyInHz < FREQ_MIN_VAL) {
			frequencyInHz = FREQ_MIN_VAL;
		}

		int volumeLevel = NUMBER_VOLUME_LEVELS;
		for (int volLevel = 0; volLevel < NUMBER_VOLUME_LEVELS; volLevel++) {
			if (volumeInPercent > (volLevel * VOLUME_LEVEL_INCR)) {
				volumeLevel = volLevel + 1;
			}
		}

		EV3Command command = new EV3Command(mindstormsConnection.getCommandCounter(), EV3CommandType.DIRECT_COMMAND_NO_REPLY, 0, 0, EV3CommandOpCode.OP_SOUND);
		mindstormsConnection.incCommandCounter();

		command.append(EV3CommandByteCode.SOUND_PLAY_TONE);
		command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, volumeLevel);
		command.append(EV3CommandParamFormat.PARAM_FORMAT_LONG, frequencyInHz);
		command.append(EV3CommandParamFormat.PARAM_FORMAT_LONG, durationInMs);

		try {
			mindstormsConnection.send(command);
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public EV3Motor getMotorA() {
		return motorA;
	}

	@Override
	public EV3Motor getMotorB() {
		return motorB;
	}

	@Override
	public EV3Motor getMotorC() {
		return motorC;
	}

	@Override
	public EV3Motor getMotorD() {
		return motorD;
	}

	@Override
	public void onSensorChanged() {
		assignSensorsToPorts();
	}

	@Override
	public void setConnection(BluetoothConnection btConnection) {
		this.mindstormsConnection = new MindstormsConnectionImpl(btConnection);
	}

	@Override
	public boolean isAlive() {
		try {
			sendKeepAlive();
			return true;
		} catch (MindstormsException e) {
			return false;
		}
	}

	private void sendKeepAlive() throws MindstormsException {

		EV3Command command = new EV3Command(mindstormsConnection.getCommandCounter(), EV3CommandType.DIRECT_COMMAND_NO_REPLY,
				0, 0, EV3CommandOpCode.OP_KEEP_ALIVE);
		mindstormsConnection.incCommandCounter();

		command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, KEEP_ALIVE_TIME);

		try {
			mindstormsConnection.send(command);
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void moveMotorStepsSpeed(byte outputField, int chainLayer, int speed, int step1Tacho, int step2Tacho, int step3Tacho, boolean brake) {

		EV3Command command = new EV3Command(mindstormsConnection.getCommandCounter(), EV3CommandType.DIRECT_COMMAND_NO_REPLY, 0, 0, EV3CommandOpCode.OP_OUTPUT_STEP_SPEED);
		mindstormsConnection.incCommandCounter();

		command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, chainLayer);
		command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, outputField);

		command.append(EV3CommandParamFormat.PARAM_FORMAT_LONG, speed);

		command.append(EV3CommandParamFormat.PARAM_FORMAT_LONG, step1Tacho);
		command.append(EV3CommandParamFormat.PARAM_FORMAT_LONG, step2Tacho);
		command.append(EV3CommandParamFormat.PARAM_FORMAT_LONG, step3Tacho);

		command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, (brake ? 0x01 : 0x00));

		try {
			mindstormsConnection.send(command);
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void moveMotorSpeed(byte outputField, int chainLayer, int speed) {

		EV3Command setSpeedCommand = new EV3Command(mindstormsConnection.getCommandCounter(), EV3CommandType.DIRECT_COMMAND_NO_REPLY, 0, 0, EV3CommandOpCode.OP_OUTPUT_SPEED);
		mindstormsConnection.incCommandCounter();

		setSpeedCommand.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, chainLayer);
		setSpeedCommand.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, outputField);
		setSpeedCommand.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, speed);

		EV3Command startMotorCommand = new EV3Command(mindstormsConnection.getCommandCounter(), EV3CommandType
				.DIRECT_COMMAND_NO_REPLY, 0, 0, EV3CommandOpCode.OP_OUTPUT_START);
		mindstormsConnection.incCommandCounter();

		startMotorCommand.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, chainLayer);
		startMotorCommand.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, outputField);

		try {
			mindstormsConnection.send(setSpeedCommand);
			mindstormsConnection.send(startMotorCommand);
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void stopMotor(byte outputField, int chainLayer, boolean brake) {

		EV3Command command = new EV3Command(mindstormsConnection.getCommandCounter(), EV3CommandType.DIRECT_COMMAND_NO_REPLY, 0, 0, EV3CommandOpCode.OP_OUTPUT_STOP);
		mindstormsConnection.incCommandCounter();

		command.append((byte) chainLayer);

		command.append(outputField);

		command.append((byte) (brake ? 0x01 : 0x00));

		try {
			mindstormsConnection.send(command);
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void setLed(int ledStatus) {

		EV3Command command = new EV3Command(mindstormsConnection.getCommandCounter(), EV3CommandType.DIRECT_COMMAND_NO_REPLY, 0, 0, EV3CommandOpCode.OP_UI_WRITE);
		mindstormsConnection.incCommandCounter();

		command.append(EV3CommandByteCode.UI_WRITE_LED);

		command.append(EV3CommandParamFormat.PARAM_FORMAT_LONG, ledStatus);

		try {
			mindstormsConnection.send(command);
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public float getSensorValue(Sensors sensor) {
		switch (sensor) {
			case EV3_SENSOR_1:
				return sensor1 != null ? sensor1.getLastSensorValue() : 0;
			case EV3_SENSOR_2:
				return sensor2 != null ? sensor2.getLastSensorValue() : 0;
			case EV3_SENSOR_3:
				return sensor3 != null ? sensor3.getLastSensorValue() : 0;
			case EV3_SENSOR_4:
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
	public synchronized void initialise() throws MindstormsException {

		if (isInitialized) {
			return;
		}

		mindstormsConnection.init();

		motorA = new EV3Motor(0);
		motorB = new EV3Motor(1);
		motorC = new EV3Motor(2);
		motorD = new EV3Motor(3);

		assignSensorsToPorts();

		isInitialized = true;
	}

	private synchronized void assignSensorsToPorts() {
		if (sensorService == null) {
			sensorService = new LegoSensorService(Constants.EV3, mindstormsConnection,
					PreferenceManager.getDefaultSharedPreferences(context));
			sensorService.registerOnSensorChangedListener(this);
		}

		sensor1 = sensorService.createSensor(Constants.PORT_1);
		sensor2 = sensorService.createSensor(Constants.PORT_2);
		sensor3 = sensorService.createSensor(Constants.PORT_3);
		sensor4 = sensorService.createSensor(Constants.PORT_4);
	}

	@Override
	public void start() throws MindstormsException {
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

	@Override
	public void stopAllMovements() {

		stopMotor((byte) 0x0F, 0, true);
	}

	@Override
	public void disconnect() {
		if (mindstormsConnection.isConnected()) {
			this.stopAllMovements();
			//sensorService.destroy();
			mindstormsConnection.disconnect();
		}
	}

	@Override
	public UUID getBluetoothDeviceUUID() {
		return LEGO_EV3_UUID;
	}
}
