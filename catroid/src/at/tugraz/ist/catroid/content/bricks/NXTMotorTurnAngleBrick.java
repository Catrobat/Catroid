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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.utils.Utils;

public class NXTMotorTurnAngleBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	public static final int REQUIRED_RESSOURCES = BLUETOOTH_LEGO_NXT;

	private Sprite sprite;
	private Handler btcHandler;
	private int motor;
	private int angle;
	private static final int MOTOR_A = 0;
	private static final int MOTOR_B = 1;
	private static final int MOTOR_C = 2;
	private static final int MOTOR_A_C = 3;
	private static final int NO_DELAY = 0;

	private transient EditText editX;

	public NXTMotorTurnAngleBrick(Sprite sprite, int motor, int angle) {
		this.sprite = sprite;
		this.motor = motor;
		this.angle = angle;
	}

	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT;
	}

	public void execute() {
		if (btcHandler == null) {
			btcHandler = LegoNXT.getBTCHandler();
		}

		int temp_angle = angle;
		int direction = 1;
		if (angle < 0) {
			direction = -1;
			temp_angle = angle + (-2 * angle);
		}

		if (motor == MOTOR_A_C) {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, MOTOR_A, -1 * direction * 30, temp_angle);
			LegoNXT.sendBTCMotorMessage(NO_DELAY, MOTOR_C, direction * 30, temp_angle);
		} else {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, motor, direction * 30, temp_angle);
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
		return View.inflate(context, R.layout.toolbox_brick_nxt_motor_turn_angle, null);
	}

	@Override
	public Brick clone() {
		return new NXTMotorTurnAngleBrick(getSprite(), motor, angle);
	}

	public View getView(final Context context, int brickId, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_nxt_motor_turn_angle, null);

		editX = (EditText) brickView.findViewById(R.id.motor_turn_angle_edit_text);
		editX.setText(String.valueOf(angle));
		editX.setOnClickListener(this);

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

		Button directions = (Button) brickView.findViewById(R.id.directions_btn);
		directions.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				final EditText input = new EditText(context);
				input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
				//final EditIntegerDialog test = new EditIntegerDialog(context, input, angle, false);
				input.setText(angle + "");
				builder.setView(input);
				builder.setTitle("Choose and edit direction");
				builder.setSingleChoiceItems(R.array.fancy_directions_chooser, -1,
						new DialogInterface.OnClickListener() {
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
						});
				builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//						}
						if (input.getText().equals("")) {
							input.setText(0);
						}
						editX.setText(input.getText().toString());
						angle = Integer.parseInt(input.getText().toString());
						//dialogX.setValue(angle);
						//broadcastSpinner.setSelection(position);
					}
				});
				builder.setNegativeButton(context.getString(R.string.cancel_button),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});

				AlertDialog alertDialog = builder.create();
				alertDialog.setOnShowListener(new OnShowListener() {
					public void onShow(DialogInterface dialog) {
						InputMethodManager inputManager = (InputMethodManager) context
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						inputManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
					}
				});
				alertDialog.show();

			}
		});

		//return inflater.inflate(R.layout.toolbox_brick_motor_action, null);
		return brickView;
	}

	public void onClick(View view) {
		final Context context = view.getContext();

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		final EditText input = new EditText(context);
		input.setText(String.valueOf(angle));
		input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
		input.setSelectAllOnFocus(true);
		dialog.setView(input);
		dialog.setOnCancelListener((OnCancelListener) context);
		dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					angle = Integer.parseInt(input.getText().toString());
				} catch (NumberFormatException exception) {
					Toast.makeText(context, R.string.error_no_number_entered, Toast.LENGTH_SHORT);
				}
				dialog.cancel();
			}
		});
		dialog.setNeutralButton(context.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		AlertDialog finishedDialog = dialog.create();
		finishedDialog.setOnShowListener(Utils.getBrickDialogOnClickListener(context, input));

		finishedDialog.show();

	}

}
