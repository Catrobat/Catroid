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
package at.tugraz.ist.catroid.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;

public class EditDoubleDialog extends EditDialog implements OnClickListener {
	private double value;
	private double min = Double.MIN_VALUE;
	private double max = Double.MAX_VALUE;
	private boolean signed = false;

	public EditDoubleDialog(Context context, EditText referencedEditText, double value) {
		super(context, referencedEditText);
		this.value = value;
	}

	public EditDoubleDialog(Context context, EditText referencedEditText, double value, double min, double max) {
		super(context, referencedEditText);
		this.value = value;
		this.min = min;
		this.max = max;
	}

	public EditDoubleDialog(Context context, EditText referencedEditText, double value, boolean signed) {
		super(context, referencedEditText);
		this.value = value;
		this.signed = signed;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		editText.setText(String.valueOf(value));

		if (signed) {
			editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
					| InputType.TYPE_NUMBER_FLAG_SIGNED);
		} else {
			editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}

		okButton.setOnClickListener(this);
		this.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					try {
						value = Double.parseDouble(editText.getText().toString());
						if (value < min) {
							value = min;
							Toast.makeText(context, R.string.number_to_small, Toast.LENGTH_SHORT).show();
							return false;
						} else if (value > max) {
							value = max;
							Toast.makeText(context, R.string.number_to_big, Toast.LENGTH_SHORT).show();
							return false;
						}
						dismiss();
					} catch (NumberFormatException e) {
						Toast.makeText(context, R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
					}
					return true;
				}
				return false;
			}
		});
	}

	public double getValue() {
		return value;
	}

	public void onClick(View v) {

		if (v.getId() == R.id.dialog_edit_dialog_edit_text) {
			editText.selectAll();
		} else if (v.getId() == referencedEditText.getId()) {
			show();
		} else {
			try {
				value = Double.parseDouble(editText.getText().toString());
				if (value < min) {
					value = min;
					Toast.makeText(context, R.string.number_to_small, Toast.LENGTH_SHORT).show();
				} else if (value > max) {
					value = max;
					Toast.makeText(context, R.string.number_to_big, Toast.LENGTH_SHORT).show();
				}
				dismiss();
			} catch (NumberFormatException e) {
				Toast.makeText(context, R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public int getRefernecedEditTextId() {
		return referencedEditText.getId();
	}
}
