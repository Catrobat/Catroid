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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.dialogs.EditDoubleDialog;
import at.tugraz.ist.catroid.ui.dialogs.EditIntegerDialog;

public class MotorActionBrickSlide implements Brick, OnDismissListener, OnItemSelectedListener, OnSeekBarChangeListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private transient Handler btcHandler;
	private int motor;
	private int speed;
	private double duration;
	private static final int MOTOR_A = 0;
	private static final int MOTOR_B = 1;
	private static final int MOTOR_C = 2;
	private static final int NO_DELAY = 0;
	private static final int MIN_SPEED = -100;
	private static final int MAX_SPEED = 100;
	private static final double MIN_DURATION = 0;
	private static final double MAX_DURATION = Double.MAX_VALUE;

	private transient EditText editSpeed;
	private transient SeekBar speedBar;
	private transient EditIntegerDialog dialogSpeed;

	public MotorActionBrickSlide(Sprite sprite, int motor, int speed, double duration) {
		this.sprite = sprite;
		this.motor = motor;
		this.speed = speed;
		this.duration = duration;

	}

	public void execute() {
		if (btcHandler == null) {
			btcHandler = LegoNXT.getBTCHandler();
		}

		LegoNXT.sendBTCmessage(NO_DELAY, motor, speed, 0);
		LegoNXT.sendBTCmessage((int) (duration * 1000), motor, 0, 0);

	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.toolbox_brick_motor_action_slide, null);
		//speedBar = (SeekBar) brickView.findViewById(R.id.seekBarSpeedMotorActionToolbox);
		//speedBar.setEnabled(false);
		return brickView;
	}

	@Override
	public Brick clone() {
		return new MotorActionBrickSlide(getSprite(), motor, speed, duration);
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_motor_action_slide, null);

		EditText editDuration = (EditText) brickView.findViewById(R.id.motor_action_duration_edit_text_slide);
		editDuration.setText(String.valueOf(duration));
		EditDoubleDialog dialogDuration = new EditDoubleDialog(context, editDuration, duration, MIN_DURATION,
				MAX_DURATION);
		dialogDuration.setOnDismissListener(this);
		dialogDuration.setOnCancelListener((OnCancelListener) context);
		editDuration.setOnClickListener(dialogDuration);

		editSpeed = (EditText) brickView.findViewById(R.id.motor_action_speed_edit_text_slide);
		editSpeed.setText(String.valueOf(speed));
		dialogSpeed = new EditIntegerDialog(context, editSpeed, speed, true, MIN_SPEED, MAX_SPEED);
		dialogSpeed.setOnDismissListener(this);
		dialogSpeed.setOnCancelListener((OnCancelListener) context);
		editSpeed.setOnClickListener(dialogSpeed);

		Spinner motorSpinner = (Spinner) brickView.findViewById(R.id.motor_spinner_slide);
		motorSpinner.setOnItemSelectedListener(this);
		motorSpinner.setSelection(motor);
		//return inflater.inflate(R.layout.toolbox_brick_motor_action, null);

		speedBar = (SeekBar) brickView.findViewById(R.id.seekBarSpeedMotorAction);
		speedBar.setOnSeekBarChangeListener(this);
		speedBar.setMax(MAX_SPEED * 2);
		speedBar.setEnabled(true);
		speedToSeekBarVal();
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
			if (inputDialog.getRefernecedEditTextId() == R.id.motor_action_speed_edit_text_slide) {
				speed = inputDialog.getValue();
				speedToSeekBarVal();
			}
		} else if (dialog instanceof EditDoubleDialog) {
			EditDoubleDialog inputDialog = (EditDoubleDialog) dialog;
			duration = inputDialog.getValue();
		} else {
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