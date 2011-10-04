/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.content.Sprite;

public class NXTMotorStopBrick implements Brick, OnItemSelectedListener {
	private static final long serialVersionUID = 1L;
	public static final int REQUIRED_RESSOURCES = BLUETOOTH_LEGO_NXT;

	private Sprite sprite;
	private Handler btcHandler;
	private int motor;
	private static final int MOTOR_A = 0;
	private static final int MOTOR_B = 1;
	private static final int MOTOR_C = 2;
	private static final int MOTOR_A_C = 3;
	private static final int ALL_MOTORS = 4;
	private static final int NO_DELAY = 0;

	public NXTMotorStopBrick(Sprite sprite, int motor) {
		this.sprite = sprite;
		this.motor = motor;
	}

	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT;
	}

	public void execute() {
		if (btcHandler == null) {
			btcHandler = LegoNXT.getBTCHandler();
		}
		if (motor == ALL_MOTORS) {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, MOTOR_A, 0, 0);
			LegoNXT.sendBTCMotorMessage(NO_DELAY, MOTOR_B, 0, 0);
			LegoNXT.sendBTCMotorMessage(NO_DELAY, MOTOR_C, 0, 0);
		} else if (motor == MOTOR_A_C) {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, MOTOR_A, 0, 0);
			LegoNXT.sendBTCMotorMessage(NO_DELAY, MOTOR_C, 0, 0);
		} else {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, motor, 0, 0);
		}

	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_nxt_motor_stop, null);
	}

	@Override
	public Brick clone() {
		return new NXTMotorStopBrick(getSprite(), motor);
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_nxt_motor_stop, null);

		Spinner motorSpinner = (Spinner) brickView.findViewById(R.id.stop_motor_spinner);
		motorSpinner.setOnItemSelectedListener(this);
		motorSpinner.setSelection(motor);
		//return inflater.inflate(R.layout.toolbox_brick_motor_action, null);
		return brickView;
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		//String[] values = parent.getContext().getResources().getStringArray(R.array.nxt_motor_chooser);
		switch (position) {
			case 0:
				motor = MOTOR_A;
				break;
			case 1:
				motor = MOTOR_B;
				break;
			case 2:
				motor = MOTOR_C;
				break;
			case 3:
				motor = MOTOR_A_C;
				break;
			case 4:
				motor = ALL_MOTORS;
				break;
		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

}
