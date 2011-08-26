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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.dialogs.EditIntegerDialog;

public class NXTMotorActionBrick implements Brick, OnDismissListener, OnItemSelectedListener, OnSeekBarChangeListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private transient Handler btcHandler;
	private int motor;
	private int speed;
	private double duration;
	private static final int MOTOR_A = 0;
	private static final int MOTOR_B = 1;
	private static final int MOTOR_C = 2;
	private static final int MOTOR_A_C = 3;
	private static final int NO_DELAY = 0;
	private static final int MIN_SPEED = -100;
	private static final int MAX_SPEED = 100;

	private transient EditText editSpeed;
	private transient SeekBar speedBar;
	private transient EditIntegerDialog dialogSpeed;

	public NXTMotorActionBrick(Sprite sprite, int motor, int speed, double duration) {
		this.sprite = sprite;
		this.motor = motor;
		this.speed = speed;
		this.duration = duration;

	}

	public void execute() {
		if (btcHandler == null) {
			btcHandler = LegoNXT.getBTCHandler();
		}
		if (motor == MOTOR_A_C) {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, MOTOR_A, speed, 0);
			LegoNXT.sendBTCMotorMessage(NO_DELAY, MOTOR_C, speed, 0);
		} else {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, motor, speed, 0);
		}
		//LegoNXT.sendBTCMotorMessage((int) (duration * 1000), motor, 0, 0);

	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.toolbox_brick_nxt_motor_action, null);
	}

	@Override
	public Brick clone() {
		return new NXTMotorActionBrick(getSprite(), motor, speed, duration);
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_nxt_motor_action, null);

		//		EditText editDuration = (EditText) brickView.findViewById(R.id.motor_action_duration_edit_text);
		//		editDuration.setText(String.valueOf(duration));
		//		EditDoubleDialog dialogDuration = new EditDoubleDialog(context, editDuration, duration, MIN_DURATION,
		//				MAX_DURATION);
		//		dialogDuration.setOnDismissListener(this);
		//		dialogDuration.setOnCancelListener((OnCancelListener) context);
		//		editDuration.setOnClickListener(dialogDuration);

		editSpeed = (EditText) brickView.findViewById(R.id.motor_action_speed_edit_text);
		editSpeed.setText(String.valueOf(speed));
		dialogSpeed = new EditIntegerDialog(context, editSpeed, speed, true, MIN_SPEED, MAX_SPEED);
		dialogSpeed.setOnDismissListener(this);
		dialogSpeed.setOnCancelListener((OnCancelListener) context);
		editSpeed.setOnClickListener(dialogSpeed);

		Spinner motorSpinner = (Spinner) brickView.findViewById(R.id.motor_spinner);
		motorSpinner.setOnItemSelectedListener(this);
		motorSpinner.setSelection(motor);

		speedBar = (SeekBar) brickView.findViewById(R.id.seekBarSpeedMotorAction);
		speedBar.setOnSeekBarChangeListener(this);
		speedBar.setMax(MAX_SPEED * 2);
		speedBar.setEnabled(true);
		speedToSeekBarVal();

		Button speedDown = (Button) brickView.findViewById(R.id.speed_down_btn);
		speedDown.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (speed <= -100) {
					return;
				}

				speed--;
				speedToSeekBarVal();
				editSpeed.setText(String.valueOf(speed));
			}
		});

		Button speedUp = (Button) brickView.findViewById(R.id.speed_up_btn);
		speedUp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (speed >= 100) {
					return;
				}

				speed++;
				speedToSeekBarVal();
				editSpeed.setText(String.valueOf(speed));
			}
		});

		return brickView;
	}

	public void onProgressChanged(SeekBar speedBar, int progress, boolean fromUser) {
		if (progress != (speed + 100)) {
			seekbarValToSpeed();
			if (dialogSpeed != null) {
				dialogSpeed.setValue(progress - 100);
			}
		}

	}

	public void onStartTrackingTouch(SeekBar speedBar) {

	}

	public void onStopTrackingTouch(SeekBar speedBar) {

	}

	public void onDismiss(DialogInterface dialog) {
		if (dialog instanceof EditIntegerDialog) {
			EditIntegerDialog inputDialog = (EditIntegerDialog) dialog;
			if (inputDialog.getRefernecedEditTextId() == R.id.motor_action_speed_edit_text) {
				speed = inputDialog.getValue();
				speedToSeekBarVal();
			}
		}
		//		else if (dialog instanceof EditDoubleDialog) {
		//			EditDoubleDialog inputDialog = (EditDoubleDialog) dialog;
		//			duration = inputDialog.getValue();}
		else {
			throw new RuntimeException("Received illegal id from EditText in MotorActionBrick");
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
			case 3:
				motor = MOTOR_A_C;
				break;
		}
	}

	private void seekbarValToSpeed() {
		speed = speedBar.getProgress() - 100;
		editSpeed.setText(String.valueOf(speed));
	}

	private void speedToSeekBarVal() {
		speedBar.setProgress(speed + 100);
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

}