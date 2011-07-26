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

public class MotorTurnAngleBrick implements Brick, OnDismissListener, OnItemSelectedListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private Handler btcHandler;
	private int motor;
	private int speed;
	private int angle;
	private static final int MOTOR_A = 0;
	private static final int MOTOR_B = 1;
	private static final int MOTOR_C = 2;
	private static final int NO_DELAY = 0;

	public MotorTurnAngleBrick(Sprite sprite, int motor, int speed, int angle) {
		this.sprite = sprite;
		this.motor = motor;
		this.speed = speed;
		this.angle = angle;

	}

	public void execute() {
		if (btcHandler == null) {
			btcHandler = LegoNXT.getBTCHandler();
		}

		LegoNXT.sendBTCmessage(NO_DELAY, motor, speed, angle);

	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.toolbox_brick_motor_turn_angle, null);
	}

	@Override
	public Brick clone() {
		return new MotorTurnAngleBrick(getSprite(), motor, speed, angle);
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_motor_turn_angle, null);

		EditText editX = (EditText) brickView.findViewById(R.id.motor_turn_angle_duration_edit_text);
		editX.setText(String.valueOf(angle));
		EditIntegerDialog dialogX = new EditIntegerDialog(context, editX, angle, true);
		dialogX.setOnDismissListener(this);
		dialogX.setOnCancelListener((OnCancelListener) context);
		editX.setOnClickListener(dialogX);

		EditText editY = (EditText) brickView.findViewById(R.id.motor_turn_angle_speed_edit_text);
		editY.setText(String.valueOf(speed));
		EditIntegerDialog dialogY = new EditIntegerDialog(context, editY, speed, true);
		dialogY.setOnDismissListener(this);
		dialogY.setOnCancelListener((OnCancelListener) context);
		editY.setOnClickListener(dialogY);

		Spinner motorSpinner = (Spinner) brickView.findViewById(R.id.motor_spinner);
		motorSpinner.setOnItemSelectedListener(this);
		motorSpinner.setSelection(motor);

		//return inflater.inflate(R.layout.toolbox_brick_motor_action, null);
		return brickView;
	}

	public void onDismiss(DialogInterface dialog) {
		if (dialog instanceof EditIntegerDialog) {
			EditIntegerDialog inputDialog = (EditIntegerDialog) dialog;
			if (inputDialog.getRefernecedEditTextId() == R.id.motor_turn_angle_speed_edit_text) {
				speed = inputDialog.getValue();
			} else if (inputDialog.getRefernecedEditTextId() == R.id.motor_turn_angle_duration_edit_text) {
				angle = inputDialog.getValue();
			} else {
				throw new RuntimeException("Received illegal id from EditText: "
						+ inputDialog.getRefernecedEditTextId());
			}
		}

		dialog.cancel();
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
		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

}