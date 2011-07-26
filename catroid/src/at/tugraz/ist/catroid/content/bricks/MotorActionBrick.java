/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.LegoNXT.LegoNXTBtCommunicator;
import at.tugraz.ist.catroid.content.Sprite;

public class MotorActionBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private Handler btcHandler;
	private int motor;
	private int speed;
	private int angle;

	public MotorActionBrick(Sprite sprite, int motor, int speed, int angle) {
		this.sprite = sprite;
		this.motor = motor;
		this.speed = speed;
		this.angle = angle;

	}

	public void execute() {
		if (btcHandler == null) {
			btcHandler = LegoNXT.getBTCHandler();
		}

		//sendBTCmessage(LegoNXTBtCommunicator.NO_DELAY, motor, 50, 180);
		//sendBTCmessage(1000, LegoNXTBtCommunicator.MOTOR_A, -75, 0);
		//sendBTCmessage(1000, motor, 0, 0);
		LegoNXT.sendBTCmessage(LegoNXTBtCommunicator.NO_DELAY, LegoNXTBtCommunicator.MOTOR_A, 75 * 1, 0);
		LegoNXT.sendBTCmessage(500, LegoNXTBtCommunicator.MOTOR_A, -75 * 1, 0);
		LegoNXT.sendBTCmessage(1000, LegoNXTBtCommunicator.MOTOR_A, 0, 0);
		//LegoNXT.sendBTCmessage(LegoNXTBtCommunicator.NO_DELAY, LegoNXTBtCommunicator.MOTOR_A, 75 * 1, 0);
		//LegoNXT.sendBTCmessage(500, LegoNXTBtCommunicator.MOTOR_A, -75 * 1, 0);
		//LegoNXT.sendBTCmessage(1000, LegoNXTBtCommunicator.MOTOR_A, 0, 0);

	}
		
	
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SE
RVICE);
		return inflater.inflate(R.layout.construction_brick_motor_action, null);
	}

	@Override
	public Brick clone() {
		return new MotorActionBrick(getSprite());
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.toolbox_brick_motor_action, null);
	}

}