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

package org.catrobat.catroid.devices.mindstorms.ev3;

import android.content.Context;
import android.util.Log;

import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnectionImpl;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;

import java.util.UUID;

public class LegoEV3Impl implements LegoEV3 {

	private static final UUID LEGO_EV3_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String TAG = LegoEV3Impl.class.getSimpleName();

	private boolean isInitialized = false;

	private short commandCounter = 1;

	protected MindstormsConnection mindstormsConnection;
	protected Context context;

	private EV3Motor motorA;
	private EV3Motor motorB;
	private EV3Motor motorC;
	private EV3Motor motorD;

	//private EV3Sensor sensor1;
	//private EV3Sensor sensor2;
	//private EV3Sensor sensor4;

	public LegoEV3Impl(Context applicationContext) {
		this.context = applicationContext;
	}

	public short getCommandCounter() {
		return commandCounter;
	}

	public void incCommandCounter() {
		commandCounter++;
	}

	@Override
	public String getName() {
		return "Lego Mindstorms EV3";
	}

	@Override
	public Class<? extends BluetoothDevice> getDeviceType() {
		return BluetoothDevice.LEGO_EV3;
	}

	@Override
	public void playTone(int frequencyInHz, int durationInMs, int volumeInPercent) {

		if (durationInMs <= 0) {
			return;
		}

		//different sources suggest different min./max. values
		if (frequencyInHz > 10000) {
			frequencyInHz = 10000;
		} else if (frequencyInHz < 250) {
			frequencyInHz = 250;
		} else if (volumeInPercent > 100) {
			volumeInPercent = 100;
		}

		EV3Command command = new EV3Command(getCommandCounter(), EV3CommandType.DIRECT_COMMAND_NO_REPLY, 0, 0, EV3CommandOpCode.OP_SOUND);
		incCommandCounter();

		command.append((byte) 0x01); //cmd Play_TONE. TODO: enum?

		//Don't know why this is handled as long Param-Format not as short (source example 4.2.5 Lego EV3 Communication Developer Kit)
		command.append((byte) (EV3CommandParamByteCode.PARAM_FORMAT_LONG.getByte() | EV3CommandParamByteCode.PARAM_FOLLOW_ONE_BYTE.getByte()));
		command.append((byte) (volumeInPercent & 0xFF));

		command.append((byte) (EV3CommandParamByteCode.PARAM_FORMAT_LONG.getByte() | EV3CommandParamByteCode.PARAM_FOLLOW_TWO_BYTE.getByte()));
		command.append((byte) (frequencyInHz & 0x00FF));
		command.append((byte) ((frequencyInHz & 0xFF00) >> 8));

		command.append((byte) (EV3CommandParamByteCode.PARAM_FORMAT_LONG.getByte() | EV3CommandParamByteCode.PARAM_FOLLOW_TWO_BYTE.getByte()));
		command.append((byte) (durationInMs & 0x00FF));
		command.append((byte) ((durationInMs & 0xFF00) >> 8));

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

		EV3Command command = new EV3Command(getCommandCounter(), EV3CommandType.DIRECT_COMMAND_REPLY, 0, 0, EV3CommandOpCode.OP_UI_READ);
		incCommandCounter();

		command.append((byte) 0x01); //cmd get_vBatt TODO: enum?
	}

	public void moveMotorStepsSpeed(byte outputField, int chainLayer, int speed, int step1Tacho, int step2Tacho, int step3Tacho, boolean brake) {

		EV3Command command = new EV3Command(getCommandCounter(), EV3CommandType.DIRECT_COMMAND_NO_REPLY, 0, 0, EV3CommandOpCode.OP_OUTPUT_STEP_SPEED);
		incCommandCounter();

		command.append((byte) chainLayer);

		command.append(outputField);

		command.append((byte) (EV3CommandParamByteCode.PARAM_FORMAT_LONG.getByte() | EV3CommandParamByteCode.PARAM_FOLLOW_ONE_BYTE.getByte()));
		command.append((byte) (speed & 0xFF));

		command.append((byte) (EV3CommandParamByteCode.PARAM_FORMAT_LONG.getByte() | EV3CommandParamByteCode.PARAM_FOLLOW_TWO_BYTE.getByte()));
		command.append((byte) (step1Tacho & 0x00FF));
		command.append((byte) ((step1Tacho & 0xFF00) >> 8));

		command.append((byte) (EV3CommandParamByteCode.PARAM_FORMAT_LONG.getByte() | EV3CommandParamByteCode.PARAM_FOLLOW_TWO_BYTE.getByte()));
		command.append((byte) (step2Tacho & 0x00FF));
		command.append((byte) ((step2Tacho & 0xFF00) >> 8));

		command.append((byte) (EV3CommandParamByteCode.PARAM_FORMAT_LONG.getByte() | EV3CommandParamByteCode.PARAM_FOLLOW_TWO_BYTE.getByte()));
		command.append((byte) (step3Tacho & 0x00FF));
		command.append((byte) ((step3Tacho & 0xFF00) >> 8));

		// I don't know why this parameter is just appended without control-byte in between... source : example from Lego Ev3 Communication Dev Kit 4.2.2
		command.append((byte) (brake ? 0x01 : 0x00));

		try {
			mindstormsConnection.send(command);
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void moveMotorTime(byte outputField, int chainLayer, int power, int step1TimeInMs, int step2TimeInMs, int step3TimeInMs, boolean brake) {

		EV3Command command = new EV3Command(getCommandCounter(), EV3CommandType.DIRECT_COMMAND_NO_REPLY, 0, 0, EV3CommandOpCode.OP_OUTPUT_TIME_POWER);
		incCommandCounter();

		command.append((byte) chainLayer);

		command.append(outputField);

		command.append((byte) (EV3CommandParamByteCode.PARAM_FORMAT_LONG.getByte() | EV3CommandParamByteCode.PARAM_FOLLOW_ONE_BYTE.getByte()));
		command.append((byte) (power & 0xFF));

		command.append((byte) (EV3CommandParamByteCode.PARAM_FORMAT_LONG.getByte() | EV3CommandParamByteCode.PARAM_FOLLOW_TWO_BYTE.getByte()));
		command.append((byte) (step1TimeInMs & 0x00FF));
		command.append((byte) ((step1TimeInMs & 0xFF00) >> 8));

		command.append((byte) (EV3CommandParamByteCode.PARAM_FORMAT_LONG.getByte() | EV3CommandParamByteCode.PARAM_FOLLOW_TWO_BYTE.getByte()));
		command.append((byte) (step2TimeInMs & 0x00FF));
		command.append((byte) ((step2TimeInMs & 0xFF00) >> 8));

		command.append((byte) (EV3CommandParamByteCode.PARAM_FORMAT_LONG.getByte() | EV3CommandParamByteCode.PARAM_FOLLOW_TWO_BYTE.getByte()));
		command.append((byte) (step3TimeInMs & 0x00FF));
		command.append((byte) ((step3TimeInMs & 0xFF00) >> 8));

		// I don't know why this parameter is just appended without control-byte in between... source : example from Lego Ev3 Communication Dev Kit 4.2.2
		command.append((byte) (brake ? 0x01 : 0x00));

		try {
			mindstormsConnection.send(command);
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void stopMotor(byte outputField, int chainLayer, boolean brake) {

		EV3Command command = new EV3Command(getCommandCounter(), EV3CommandType.DIRECT_COMMAND_NO_REPLY, 0, 0, EV3CommandOpCode.OP_OUTPUT_STOP);
		incCommandCounter();

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

		EV3Command command = new EV3Command(getCommandCounter(), EV3CommandType.DIRECT_COMMAND_NO_REPLY, 0, 0, EV3CommandOpCode.OP_UI_WRITE);
		incCommandCounter();

		command.append((byte) 0x1B); //cmd LED TODO: enum?

		command.append((byte) (EV3CommandParamByteCode.PARAM_FORMAT_LONG.getByte() | EV3CommandParamByteCode.PARAM_FOLLOW_ONE_BYTE.getByte()));

		command.append((byte) (ledStatus & 0xFF));

		try {
			mindstormsConnection.send(command);
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public synchronized void initialise() {

		if (isInitialized) {
			return;
		}

		mindstormsConnection.init();

		motorA = new EV3Motor(0);
		motorB = new EV3Motor(1);
		motorC = new EV3Motor(2);
		motorD = new EV3Motor(3);

		// TODO: Sensor Init
		//assignSensorsToPorts();

		isInitialized = true;
	}

	@Override
	public void start() {
		initialise();
		//sensorService.resumeSensorUpdate();
	}

	@Override
	public void pause() {
		//stopAllMovements();
		//sensorService.pauseSensorUpdate();
	}

	@Override
	public void destroy() {
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
