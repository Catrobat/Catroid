/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.LegoNXT.LegoNXT;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.catrobat.catroid.R;

public class LegoNxtMotorActionBrick implements Brick, OnSeekBarChangeListener, OnClickListener {
	private static final long serialVersionUID = 1L;

	public static enum Motor {
		MOTOR_A, MOTOR_B, MOTOR_C, MOTOR_A_C
	}

	public LegoNxtMotorActionBrick() {

	}

	private Sprite sprite;
	private String motor;
	private transient Motor motorEnum;
	private int speed;

	private static final int NO_DELAY = 0;
	private static final int MIN_SPEED = -100;
	private static final int MAX_SPEED = 100;

	private transient EditText editSpeed;
	private transient SeekBar speedBar;

	protected Object readResolve() {
		if (motor != null) {
			motorEnum = Motor.valueOf(motor);
		}
		return this;
	}

	public LegoNxtMotorActionBrick(Sprite sprite, Motor motor, int speed) {
		this.sprite = sprite;
		this.motorEnum = motor;
		this.motor = motorEnum.name();
		this.speed = speed;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT;
	}

	@Override
	public void execute() {

		if (motorEnum.equals(Motor.MOTOR_A_C)) {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, Motor.MOTOR_A.ordinal(), speed, 0);
			LegoNXT.sendBTCMotorMessage(NO_DELAY, Motor.MOTOR_C.ordinal(), speed, 0);
		} else {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, motorEnum.ordinal(), speed, 0);
		}
		//LegoNXT.sendBTCMotorMessage((int) (duration * 1000), motor, 0, 0);
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.brick_nxt_motor_action, null);
		SeekBar noClick = (SeekBar) view.findViewById(R.id.seekBarSpeedMotorAction);
		noClick.setEnabled(false);
		return view;
	}

	@Override
	public Brick clone() {
		return new LegoNxtMotorActionBrick(getSprite(), motorEnum, speed);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		View brickView = View.inflate(context, R.layout.brick_nxt_motor_action, null);

		TextView textSpeed = (TextView) brickView.findViewById(R.id.motor_action_speed_text_view);
		editSpeed = (EditText) brickView.findViewById(R.id.motor_action_speed_edit_text);
		editSpeed.setText(String.valueOf(speed));

		textSpeed.setVisibility(View.GONE);
		editSpeed.setVisibility(View.VISIBLE);

		editSpeed.setOnClickListener(this);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context, R.array.nxt_motor_chooser,
				android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner motorSpinner = (Spinner) brickView.findViewById(R.id.motor_spinner);
		motorSpinner.setClickable(true);
		motorSpinner.setEnabled(true);
		motorSpinner.setAdapter(motorAdapter);
		motorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				motorEnum = Motor.values()[position];
				motor = motorEnum.name();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});

		motorSpinner.setSelection(motorEnum.ordinal());

		speedBar = (SeekBar) brickView.findViewById(R.id.seekBarSpeedMotorAction);
		speedBar.setOnSeekBarChangeListener(this);
		speedBar.setMax(MAX_SPEED * 2);
		speedBar.setEnabled(true);
		speedToSeekBarVal();

		Button speedDown = (Button) brickView.findViewById(R.id.speed_down_btn);
		speedDown.setOnClickListener(new OnClickListener() {
			@Override
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
			@Override
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

	@Override
	public void onProgressChanged(SeekBar speedBar, int progress, boolean fromUser) {
		if (!fromUser) {
			if (progress == 0) {//Robotium fromUser=false
				return;
			}
		}

		if (progress != (speed + 100)) {
			seekbarValToSpeed();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar speedBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar speedBar) {
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

	@Override
	public void onClick(View view) {
		ScriptActivity activity = (ScriptActivity) view.getContext();

		BrickTextDialog editDialog = new BrickTextDialog() {
			@Override
			protected void initialize() {
				input.setText(String.valueOf(speed));
				input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
				input.setSelectAllOnFocus(true);
			}

			@Override
			protected boolean handleOkButton() {
				try {
					int newSpeed = Integer.parseInt(input.getText().toString());
					if (newSpeed > MAX_SPEED) {
						newSpeed = MAX_SPEED;
						Toast.makeText(getActivity(), R.string.number_to_big, Toast.LENGTH_SHORT).show();
					} else if (newSpeed < MIN_SPEED) {
						newSpeed = MIN_SPEED;
						Toast.makeText(getActivity(), R.string.number_to_small, Toast.LENGTH_SHORT).show();
					}
					speed = newSpeed;
					speedToSeekBarVal();
				} catch (NumberFormatException exception) {
					Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		};

		editDialog.show(activity.getSupportFragmentManager(), "dialog_nxt_moto_action_brick");
	}
}
