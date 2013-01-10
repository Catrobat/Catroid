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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ScriptTabActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PointInDirectionBrick implements Brick, OnItemSelectedListener {

	private static final long serialVersionUID = 1L;

	public PointInDirectionBrick() {

	}

	public static enum Direction {
		DIRECTION_RIGHT(90), DIRECTION_LEFT(-90), DIRECTION_UP(0), DIRECTION_DOWN(180);

		private double directionDegrees;

		private Direction(double degrees) {
			directionDegrees = degrees;
		}

		public double getDegrees() {
			return directionDegrees;
		}
	}

	private Sprite sprite;
	private double degrees;

	private transient Direction direction;
	private transient EditText setAngleEditText;

	protected Object readResolve() {
		for (Direction direction : Direction.values()) {
			if (Math.abs(direction.getDegrees() - degrees) < 0.1) {
				this.direction = direction;
				break;
			}
		}
		return this;
	}

	public PointInDirectionBrick(Sprite sprite, Direction direction) {
		this.sprite = sprite;
		this.direction = direction;
		this.degrees = direction.getDegrees();
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		double degreeOffset = 90.0;
		sprite.costume.rotation = (float) (-degrees + degreeOffset);
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter adapter) {
		View view = View.inflate(context, R.layout.brick_point_in_direction, null);

		TextView setAngleTextView = (TextView) view.findViewById(R.id.brick_point_in_direction_prototype_text_view);
		setAngleEditText = (EditText) view.findViewById(R.id.brick_point_in_direction_edit_text);
		Button setAngleButton = (Button) view.findViewById(R.id.brick_point_in_direction_button_set_angle);

		setAngleEditText.setText(String.valueOf(degrees));

		setAngleTextView.setVisibility(View.GONE);
		setAngleEditText.setVisibility(View.VISIBLE);

		setAngleButton.setClickable(true);
		setAngleButton.setEnabled(true);
		setAngleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ScriptTabActivity activity = (ScriptTabActivity) context;
				EditPointInDirectionBrickDialog dialog = new EditPointInDirectionBrickDialog();
				dialog.show(activity.getSupportFragmentManager(), "dialog_point_in_direction_brick");
			}
		});

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_point_in_direction, null);
	}

	@Override
	public Brick clone() {
		return new PointInDirectionBrick(getSprite(), direction);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		direction = Direction.values()[position];
		degrees = direction.getDegrees();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	private class EditPointInDirectionBrickDialog extends DialogFragment {

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
			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED
					| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			input.setText(degrees + "");

			Dialog dialog = new AlertDialog.Builder(getActivity())
					.setView(input)
					.setTitle(R.string.brick_point_in_direction_choose_direction)
					.setSingleChoiceItems(R.array.point_in_direction_strings, -1,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int item) {

									String[] pointInDirectionValues = getResources().getStringArray(
											R.array.point_in_direction_values);

									switch (item) {
										case 0:
											input.setText(pointInDirectionValues[0]);
											break;
										case 1:
											input.setText(pointInDirectionValues[1]);
											break;
										case 2:
											input.setText(pointInDirectionValues[2]);
											break;
										case 3:
											input.setText(pointInDirectionValues[3]);
											break;
									}
								}
							}).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (input.getText().toString().equals("")) {
								input.setText("0");
							}
							setAngleEditText.setText(input.getText().toString());
							degrees = Double.parseDouble(input.getText().toString());
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
	}
}
