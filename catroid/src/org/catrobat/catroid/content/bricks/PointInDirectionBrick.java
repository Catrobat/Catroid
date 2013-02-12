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
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ScriptActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PointInDirectionBrick implements Brick, View.OnClickListener {

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
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		PointInDirectionBrick copyBrick = (PointInDirectionBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter adapter) {
		View view = View.inflate(context, R.layout.brick_point_in_direction, null);

		TextView setAngleTextView = (TextView) view.findViewById(R.id.brick_point_in_direction_prototype_text_view);
		setAngleEditText = (EditText) view.findViewById(R.id.brick_point_in_direction_edit_text);

		setAngleEditText.setText(String.valueOf(degrees));

		setAngleTextView.setVisibility(View.GONE);
		setAngleEditText.setVisibility(View.VISIBLE);

		setAngleEditText.setOnClickListener(this);

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
	public void onClick(View view) {
		ScriptActivity activity = (ScriptActivity) view.getContext();
		EditPointInDirectionBrickDialog editDialog = new EditPointInDirectionBrickDialog();
		editDialog.show(activity.getSupportFragmentManager(), "dialog_point_in_direction_brick");
	}

	private class EditPointInDirectionBrickDialog extends DialogFragment {

		private EditText input;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_text_dialog, null);
			input = (EditText) dialogView.findViewById(R.id.dialog_text_EditText);

			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED
					| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			input.setText(degrees + "");

			input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						getDialog().getWindow().setSoftInputMode(
								WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					}
				}
			});

			Dialog dialog = new AlertDialog.Builder(getActivity())
					.setView(dialogView)
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
							}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dismiss();
						}
					}).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							boolean okButtonResult = handleOkButton();
							onOkButtonHandled();
							if (okButtonResult) {
								dismiss();
							}
						}
					}).create();

			dialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
						boolean okButtonResult = handleOkButton();
						onOkButtonHandled();
						if (okButtonResult) {
							dismiss();
						}
						return okButtonResult;
					}

					return false;
				}
			});

			dialog.setCanceledOnTouchOutside(true);
			dialog.setOnShowListener(new OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					Button buttonPositive = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
					buttonPositive.setEnabled(getPositiveButtonEnabled());

					InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
							Context.INPUT_METHOD_SERVICE);
					inputManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);

					initTextChangedListener();
				}
			});

			return dialog;
		}

		protected boolean handleOkButton() {
			try {
				degrees = Double.parseDouble(input.getText().toString());
				setAngleEditText.setText(input.getText().toString());
			} catch (NumberFormatException exception) {
				Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				return false;
			}

			return true;
		}

		protected void onOkButtonHandled() {
			getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
		}

		protected TextWatcher getInputTextChangedListener(final Button buttonPositive) {
			return new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (s.length() == 0) {
						buttonPositive.setEnabled(false);
					} else {
						buttonPositive.setEnabled(true);
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			};
		}

		protected boolean getPositiveButtonEnabled() {
			if (input.getText().toString().length() == 0) {
				return false;
			}

			return true;
		}

		private void initTextChangedListener() {
			final Button buttonPositive = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
			input.addTextChangedListener(getInputTextChangedListener(buttonPositive));
		}
	}
}
