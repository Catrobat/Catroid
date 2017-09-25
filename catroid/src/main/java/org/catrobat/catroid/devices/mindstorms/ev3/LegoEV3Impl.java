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

package org.catrobat.catroid.devices.mindstorms.ev3;

import android.content.Context;
import android.util.Log;

import com.google.common.io.ByteStreams;

import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnectionImpl;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.devices.mindstorms.MindstormsSensor;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandByteCode;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandOpCode;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandParamByteCode;
import org.catrobat.catroid.devices.mindstorms.ev3.EV3CommandByte.EV3CommandParamFormat;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3SensorService;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;

public class LegoEV3Impl implements LegoEV3, EV3SensorService.OnSensorChangedListener {

	private static final UUID LEGO_EV3_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String TAG = LegoEV3Impl.class.getSimpleName();

	private static final int KEEP_ALIVE_TIME = 5;

	private static final int NUMBER_VOLUME_LEVELS = 13;
	private static final int VOLUME_LEVEL_INCR = 8;

	private static final int MAX_BYTES_TO_SEND = 1000;
	private static byte[] legoEv3ShowImageProgram;

	private boolean isInitialized = false;

	protected MindstormsConnection mindstormsConnection;
	protected Context context;

	private EV3Motor motorA;
	private EV3Motor motorB;
	private EV3Motor motorC;
	private EV3Motor motorD;

	private EV3Sensor sensor1;
	private EV3Sensor sensor2;
	private EV3Sensor sensor3;
	private EV3Sensor sensor4;

	private EV3SensorService sensorService;

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

		//different sources suggest different min./max. values
		if (frequencyInHz > 10000) {
			frequencyInHz = 10000;
		} else if (frequencyInHz < 250) {
			frequencyInHz = 250;
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

	public byte[] getLegoEv3ShowImageProgram() {
		if (legoEv3ShowImageProgram != null) {
			return Arrays.copyOf(legoEv3ShowImageProgram, legoEv3ShowImageProgram.length);
		}
		try {
			InputStream inputStream = context.getResources().openRawResource(R.raw.lego_ev3_show_image_program);
			legoEv3ShowImageProgram = ByteStreams.toByteArray(inputStream);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		return Arrays.copyOf(legoEv3ShowImageProgram, legoEv3ShowImageProgram.length);
	}

	public String downloadFileToEv3(String fileName, byte[] content) {
		String folder = "../prjs/" + Utils.getCurrentProjectName(this.context) + "/";
		int bytesSent = 0;
		int bytesToSend = content.length;
		try {
			byte handleToFile = beginDownload(bytesToSend, (folder + fileName));
			while (bytesSent < bytesToSend) {
				if ((bytesToSend - bytesSent) >= MAX_BYTES_TO_SEND) {
					continueDownload(handleToFile, content, bytesSent, MAX_BYTES_TO_SEND);
					bytesSent += MAX_BYTES_TO_SEND;
				} else {
					continueDownload(handleToFile, content, bytesSent, (bytesToSend - bytesSent));
					bytesSent = bytesSent + (bytesToSend - bytesSent);
				}
			}
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
		return fileName;
	}

	private byte beginDownload(int fileLength, String fileName) {
		int commandCount = mindstormsConnection.getCommandCounter();

		EV3Command command = new EV3Command(mindstormsConnection.getCommandCounter(),
				EV3CommandType.SYSTEM_COMMAND_REPLY, EV3CommandOpCode.OP_BEGIN_DOWNLOAD);
		mindstormsConnection.incCommandCounter();
		command.append(fileLength);
		command.append(fileName.getBytes());
		command.append(EV3CommandByte.EV3CommandParamByteCode.PARAM_FOLLOW_TERMINATED.getByte());

		EV3Reply reply = new EV3Reply(mindstormsConnection.sendAndReceive(command));
		if (!reply.isValid(commandCount)) {
			throw new MindstormsException("Reply not valid or error occurred!");
		}
		return reply.getByte(reply.getLength() - 1);
	}

	private void continueDownload(byte handleToFile, byte[] payload, int offset, int length) {
		int commandCount = mindstormsConnection.getCommandCounter();

		EV3Command command = new EV3Command(mindstormsConnection.getCommandCounter(),
				EV3CommandType.SYSTEM_COMMAND_REPLY, EV3CommandOpCode.OP_CONTINUE_DOWNLOAD);
		mindstormsConnection.incCommandCounter();
		command.append(handleToFile);
		command.append(payload, offset, length);

		EV3Reply reply = new EV3Reply(mindstormsConnection.sendAndReceive(command));

		if (!reply.isValid(commandCount)) {
			throw new MindstormsException("Reply not valid or error occurred!");
		}
	}

	public void startProgram(String programName) {
		String folder = "../prjs/" + Utils.getCurrentProjectName(this.context) + "/";

		EV3Command command = new EV3Command(mindstormsConnection.getCommandCounter(), EV3CommandType.DIRECT_COMMAND_NO_REPLY, 8, 0, EV3CommandOpCode.OP_FILE);
		mindstormsConnection.incCommandCounter();

		command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, 8); // LC0(LOAD_IMAGE)
		command.append(EV3CommandParamByteCode.PARAM_FOLLOW_TWO_BYTE, 1); // LC2(USER_SLOT)
		command.append(folder + programName);
		command.append(EV3CommandByte.EV3CommandVariableScope.PARAM_VARIABLE_SCOPE_GLOBAL, 0);
		command.append(EV3CommandByte.EV3CommandVariableScope.PARAM_VARIABLE_SCOPE_GLOBAL, 4);
		command.append(EV3CommandOpCode.OP_PROGRAM_START.getByte());
		command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, 1); // LC0(USER_SLOT)
		command.append(EV3CommandByte.EV3CommandVariableScope.PARAM_VARIABLE_SCOPE_GLOBAL, 0);
		command.append(EV3CommandByte.EV3CommandVariableScope.PARAM_VARIABLE_SCOPE_GLOBAL, 4);
		command.append(EV3CommandParamFormat.PARAM_FORMAT_SHORT, 0);

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
	public int getSensorValue(Sensors sensor) {
		switch (sensor) {
			case EV3_SENSOR_1:
				if (getSensor1() == null) {
					return 0;
				}
				return getSensor1().getLastSensorValue();
			case EV3_SENSOR_2:
				if (getSensor2() == null) {
					return 0;
				}
				return getSensor2().getLastSensorValue();
			case EV3_SENSOR_3:
				if (getSensor3() == null) {
					return 0;
				}
				return getSensor3().getLastSensorValue();
			case EV3_SENSOR_4:
				if (getSensor4() == null) {
					return 0;
				}
				return getSensor4().getLastSensorValue();
		}

		return -1;
	}

	@Override
	public MindstormsSensor getSensor1() {
		return sensor1;
	}

	@Override
	public MindstormsSensor getSensor2() {
		return sensor2;
	}

	@Override
	public MindstormsSensor getSensor3() {
		return sensor3;
	}

	@Override
	public MindstormsSensor getSensor4() {
		return sensor4;
	}

	private EV3SensorService getSensorService() {
		if (sensorService == null) {
			sensorService = new EV3SensorService(context, mindstormsConnection);
			sensorService.registerOnSensorChangedListener(this);
		}

		return sensorService;
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

		assignSensorsToPorts();

		isInitialized = true;
	}

	private synchronized void assignSensorsToPorts() {
		EV3SensorService sensorService = getSensorService();

		sensor1 = sensorService.createSensor1();
		sensor2 = sensorService.createSensor2();
		sensor3 = sensorService.createSensor3();
		sensor4 = sensorService.createSensor4();
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
