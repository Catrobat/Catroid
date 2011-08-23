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
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.dialogs.EditIntegerDialog;

public class NXTMotorTurnAngleBrick implements Brick, OnDismissListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private Handler btcHandler;
	private int motor;
	private int angle;
	private int last_spinner_angle = 0;
	private int direction_spinner_pos = 0;
	private boolean inverse = false;
	private static final int MOTOR_A = 0;
	private static final int MOTOR_B = 1;
	private static final int MOTOR_C = 2;
	private static final int MOTOR_A_C = 3;
	private static final int NO_DELAY = 0;

	private transient EditIntegerDialog dialogX;
	private transient EditText editX;

	public NXTMotorTurnAngleBrick(Sprite sprite, int motor, int angle) {
		this.sprite = sprite;
		this.motor = motor;
		this.angle = angle;

	}

	public void execute() {
		if (btcHandler == null) {
			btcHandler = LegoNXT.getBTCHandler();
		}

		int direction = 1;
		if (angle < 0) {
			direction = -1;
			angle = angle + (-2 * angle);
		}

		if (motor == MOTOR_A_C) {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, MOTOR_A, -1 * direction * 30, angle);
			LegoNXT.sendBTCMotorMessage(NO_DELAY, MOTOR_C, direction * 30, angle);
		} else {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, motor, direction * 30, angle);
		}

		/*
		 * if (inverse == false) {
		 * LegoNXT.sendBTCMotorMessage(NO_DELAY, motor, 30, angle);
		 * } else {
		 * LegoNXT.sendBTCMotorMessage(NO_DELAY, motor, -30, angle);
		 * }
		 */

	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.toolbox_brick_nxt_motor_turn_angle, null);
	}

	@Override
	public Brick clone() {
		return new NXTMotorTurnAngleBrick(getSprite(), motor, angle);
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_nxt_motor_turn_angle, null);

		editX = (EditText) brickView.findViewById(R.id.motor_turn_angle_edit_text);
		editX.setText(String.valueOf(angle));
		dialogX = new EditIntegerDialog(context, editX, angle, true);
		dialogX.setOnDismissListener(this);
		dialogX.setOnCancelListener((OnCancelListener) context);
		editX.setOnClickListener(dialogX);

		Spinner motorSpinner = (Spinner) brickView.findViewById(R.id.motor_spinner);
		motorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
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
				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		motorSpinner.setSelection(motor);

		Spinner directionSpinner = (Spinner) brickView.findViewById(R.id.fancy_directions_dialog);
		directionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

				if (position == direction_spinner_pos) {
					return;
				}
				switch (position) {
					case 0:
						dialogX.setValue(45);
						angle = 45;
						break;
					case 1:
						dialogX.setValue(90);
						angle = 90;
						break;
					case 2:
						dialogX.setValue(-45);
						angle = -45;
						break;
					case 3:
						dialogX.setValue(-90);
						angle = -90;
						break;
					case 4:
						dialogX.setValue(180);
						angle = 180;
						break;
				}
				last_spinner_angle = angle;
				direction_spinner_pos = position;
				editX.setText(String.valueOf(angle));

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		directionSpinner.setSelection(direction_spinner_pos);

		//return inflater.inflate(R.layout.toolbox_brick_motor_action, null);
		return brickView;
	}

	public void onDismiss(DialogInterface dialog) {
		if (dialog instanceof EditIntegerDialog) {
			EditIntegerDialog inputDialog = (EditIntegerDialog) dialog;

			if (inputDialog.getRefernecedEditTextId() == R.id.motor_turn_angle_edit_text) {
				angle = inputDialog.getValue();
				editX.setText(String.valueOf(angle));
			} else {
				throw new RuntimeException("Received illegal id from EditText: "
						+ inputDialog.getRefernecedEditTextId());
			}
		}

		dialog.cancel();
	}

}