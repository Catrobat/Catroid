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
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.LegoNXT.LegoNXTBtCommunicator;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.dialogs.EditIntegerDialog;

public class MotorActionBrick implements Brick, OnDismissListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private Handler btcHandler;
	private int motor;
	private int speed;
	private int duration;

	public MotorActionBrick(Sprite sprite, int motor, int speed, int duration) {
		this.sprite = sprite;
		this.motor = motor;
		this.speed = speed;
		this.duration = duration;

	}

	public void execute() {
		if (btcHandler == null) {
			btcHandler = LegoNXT.getBTCHandler();
		}

		LegoNXT.sendBTCmessage(LegoNXTBtCommunicator.NO_DELAY, motor, speed, 0);
		LegoNXT.sendBTCmessage(duration * 1000, motor, 0, 0);
		//LegoNXT.sendBTCmessage(LegoNXTBtCommunicator.NO_DELAY, LegoNXTBtCommunicator.MOTOR_A, 75 * 1, 0);
		//LegoNXT.sendBTCmessage(500, LegoNXTBtCommunicator.MOTOR_A, -75 * 1, 0);
		//LegoNXT.sendBTCmessage(1000, LegoNXTBtCommunicator.MOTOR_A, 0, 0);

	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.construction_brick_motor_action, null);
	}

	@Override
	public Brick clone() {
		return new MotorActionBrick(getSprite(), motor, speed, duration);
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_motor_action, null);

		EditText editX = (EditText) brickView.findViewById(R.id.motor_action_duration_edit_text);
		editX.setText(String.valueOf(duration));
		EditIntegerDialog dialogX = new EditIntegerDialog(context, editX, duration, true);
		dialogX.setOnDismissListener(this);
		dialogX.setOnCancelListener((OnCancelListener) context);
		editX.setOnClickListener(dialogX);

		EditText editY = (EditText) brickView.findViewById(R.id.motor_action_speed_edit_text);
		editY.setText(String.valueOf(speed));
		EditIntegerDialog dialogY = new EditIntegerDialog(context, editY, speed, true);
		dialogY.setOnDismissListener(this);
		dialogY.setOnCancelListener((OnCancelListener) context);
		editY.setOnClickListener(dialogY);

		//return inflater.inflate(R.layout.toolbox_brick_motor_action, null);
		return brickView;
	}

	public void onDismiss(DialogInterface dialog) {
		if (dialog instanceof EditIntegerDialog) {
			EditIntegerDialog inputDialog = (EditIntegerDialog) dialog;
			if (inputDialog.getRefernecedEditTextId() == R.id.construction_brick_glide_to_x_edit_text) {
				motor = inputDialog.getValue();
			} else if (inputDialog.getRefernecedEditTextId() == R.id.construction_brick_glide_to_y_edit_text) {
				speed = inputDialog.getValue();
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
				motor = 0;
				break;
			case 1:
				motor = 1;
				break;
			case 2:
				motor = 2;
				break;
		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

}