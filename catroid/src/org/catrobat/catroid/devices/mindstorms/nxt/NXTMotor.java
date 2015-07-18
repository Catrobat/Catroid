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
package org.catrobat.catroid.devices.mindstorms.nxt;

import android.util.Log;

import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.devices.mindstorms.MindstormsMotor;

public class NXTMotor implements MindstormsMotor {

	private static final String TAG = NXTMotor.class.getSimpleName();

	private int port;
	private MindstormsConnection connection;

	public NXTMotor(int port, MindstormsConnection connection) {
		this.port = port;
		this.connection = connection;
	}

	@Override
	public void stop() {
		OutputState state = new OutputState();
		state.setSpeed(0);
		state.mode = MotorMode.BREAK | MotorMode.ON | MotorMode.REGULATED;
		state.regulation = MotorRegulation.SPEED;
		state.turnRatio = 100;
		state.runState = MotorRunState.RUNNING;
		state.tachoLimit = 0;
		setOutputState(state, false);
	}

	private void setOutputState(OutputState state, boolean reply) {
		try {
			trySetOutputState(state, reply);
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	private void trySetOutputState(OutputState state, boolean reply) {
		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.SET_OUTPUT_STATE, false);
		command.append((byte) port);
		command.append(state.getSpeed());
		command.append(state.mode);
		command.append(state.regulation.getByte());
		command.append(state.turnRatio);
		command.append(state.runState.getByte());
		command.append(state.tachoLimit);
		command.append((byte) 0x00);

		if (reply) {
			connection.sendAndReceive(command);
		} else {
			connection.send(command);
		}
	}

	@Override
	public void move(int speed) {
		move(speed, 0, false);
	}

	@Override
	public void move(int speed, int degrees) {
		move(speed, degrees, false);
	}

	@Override
	public void move(int speed, int degrees, boolean reply) {
		OutputState state = new OutputState();
		state.setSpeed(speed);
		state.mode = MotorMode.BREAK | MotorMode.ON | MotorMode.REGULATED;
		state.regulation = MotorRegulation.SPEED;
		state.turnRatio = 100;
		state.runState = MotorRunState.RUNNING;
		state.tachoLimit = degrees;
		setOutputState(state, reply);
	}

	private static class OutputState {

		private byte speed;
		public byte mode;
		public MotorRegulation regulation;
		public byte turnRatio;
		public MotorRunState runState;
		public int tachoLimit; //Current limit on a movement in progress, if any

		public void setSpeed(int speed) {
			if (speed > 100) {
				this.speed = (byte) 100;
			} else if (speed < -100) {
				this.speed = (byte) -100;
			} else if (turnRatio > 100) {
				turnRatio = (byte) 100;
			} else if (turnRatio < -100) {
				this.turnRatio = (byte) 100;
			} else {
				this.speed = (byte) speed;
			}
		}

		public byte getSpeed() {
			return this.speed;
		}
	}

	public static class MotorMode {
		public static final byte ON = 0x01;
		public static final byte BREAK = 0x02;
		public static final byte REGULATED = 0x04;
	}

	public enum MotorRegulation {
		IDLE(0x00), SPEED(0x01), SYNC(0x02);

		private int motorRegulationValue;

		private MotorRegulation(int motorRegulationValue) {
			this.motorRegulationValue = motorRegulationValue;
		}

		public byte getByte() {
			return (byte) motorRegulationValue;
		}
	}

	public enum MotorRunState {
		IDLE(0x00), RAMP_UP(0x10), RUNNING(0x20), RAMP_DOWN(0x40);

		private int motorRunStateValue;

		private MotorRunState(int motorRunStateValue) {
			this.motorRunStateValue = motorRunStateValue;
		}

		public byte getByte() {
			return (byte) motorRunStateValue;
		}
	}
}
