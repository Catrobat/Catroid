/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
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

import org.catrobat.catroid.R;
<<<<<<< HEAD
import org.catrobat.catroid.LegoNXT.LegoNXT;
=======
>>>>>>> refs/remotes/origin/master
import org.catrobat.catroid.content.Sprite;
<<<<<<< HEAD
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
=======
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.ui.ScriptActivity;
>>>>>>> refs/remotes/origin/master

<<<<<<< HEAD
=======
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
>>>>>>> refs/remotes/origin/master
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
<<<<<<< HEAD
=======

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
>>>>>>> refs/remotes/origin/master

public class LegoNxtMotorTurnAngleBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;

	public static enum Motor {
		MOTOR_A, MOTOR_B, MOTOR_C, MOTOR_A_C
	}

	private Sprite sprite;
	private String motor;
	private transient Motor motorEnum;
<<<<<<< HEAD
	private Formula degrees;
	private static final int NO_DELAY = 0;
=======
	private int degrees;
>>>>>>> refs/remotes/origin/master

	private transient EditText editSpeed;

	protected Object readResolve() {
		if (motor != null) {
			motorEnum = Motor.valueOf(motor);
		}
		return this;
	}

	public LegoNxtMotorTurnAngleBrick(Sprite sprite, Motor motor, int degrees) {
		this.sprite = sprite;
		this.motorEnum = motor;
		this.motor = motorEnum.name();

		this.degrees = new Formula(degrees);
	}

	public LegoNxtMotorTurnAngleBrick(Sprite sprite, Motor motor, Formula degreesFormula) {
		this.sprite = sprite;
		this.motorEnum = motor;
		this.motor = motorEnum.name();

		this.degrees = degreesFormula;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT;
<<<<<<< HEAD
	}

	@Override
	public void execute() {
		int temp_angle = degrees.interpretInteger(sprite);
		int direction = 1;
		if (temp_angle < 0) {
			direction = -1;
			temp_angle = temp_angle + (-2 * temp_angle);
		}

		if (motorEnum.equals(Motor.MOTOR_A_C)) {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, Motor.MOTOR_A.ordinal(), -1 * direction * 30, temp_angle);
			LegoNXT.sendBTCMotorMessage(NO_DELAY, Motor.MOTOR_C.ordinal(), direction * 30, temp_angle);
		} else {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, motorEnum.ordinal(), direction * 30, temp_angle);
		}

=======
>>>>>>> refs/remotes/origin/master
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_nxt_motor_turn_angle, null);
	}

	@Override
	public Brick clone() {
		return new LegoNxtMotorTurnAngleBrick(getSprite(), motorEnum, degrees);
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter adapter) {
		View brickView = View.inflate(context, R.layout.brick_nxt_motor_turn_angle, null);

		TextView textSpeed = (TextView) brickView.findViewById(R.id.motor_turn_angle_text_view);
		editSpeed = (EditText) brickView.findViewById(R.id.motor_turn_angle_edit_text);
		degrees.setTextFieldId(R.id.motor_turn_angle_edit_text);
		degrees.refreshTextField(brickView);

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

		return brickView;
	}

<<<<<<< HEAD
	@Override
	public void onClick(View view) {
		FormulaEditorFragment.showFragment(view, this, degrees);
=======
	@SuppressLint("ValidFragment")
	private class EditNxtMotorTurnAngleBrickDialog extends DialogFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setRetainInstance(true);
		}

		@Override
		public void onDestroyView() {
			if (getDialog() != null && getRetainInstance()) {
				getDialog().setOnDismissListener(null);
			}
			super.onDestroyView();
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final EditText input = new EditText(getActivity());
			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
			//final EditIntegerDialog test = new EditIntegerDialog(context, input, angle, false);
			input.setText(degrees + "");

			Dialog dialog = new AlertDialog.Builder(getActivity()).setView(input).setTitle("Choose and edit direction")
					.setSingleChoiceItems(R.array.fancy_directions_chooser, -1, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int item) {

							switch (item) {
								case 0:
									input.setText("45");
									break;
								case 1:
									input.setText("90");
									break;
								case 2:
									input.setText("-45");
									break;
								case 3:
									input.setText("-90");
									break;
								case 4:
									input.setText("180");
									break;
								case 5:
									input.setText("360");
									break;
							}

							//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
						}
					}).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (input.getText().toString().equals("")) {
								input.setText("0");
							}
							editX.setText(input.getText().toString());
							degrees = Integer.parseInt(input.getText().toString());
						}
					}).setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).create();

			dialog.setOnShowListener(new OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
							Context.INPUT_METHOD_SERVICE);
					inputManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
				}
			});

			return dialog;
		}
>>>>>>> refs/remotes/origin/master
	}

	@Override
	public SequenceAction addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.legoNxtMotorTurnAngle(motorEnum, degrees));
		return null;
	}
}
