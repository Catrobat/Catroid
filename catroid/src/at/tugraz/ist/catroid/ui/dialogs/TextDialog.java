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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;

/**
 * Simple dialog for entering text with ok and cancel button will not permit to
 * enter empty strings you have to implement the key listener in the subclass.
 */
public abstract class TextDialog extends DialogFragment {

	protected EditText input;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_text_dialog, null);
		input = (EditText) dialogView.findViewById(R.id.dialog_text_EditText);

		if (getHint() != null) {
			input.setHint(getHint());
		}

		input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					getDialog().getWindow()
							.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		initialize();

		Dialog dialog = new AlertDialog.Builder(getActivity()).setView(dialogView).setTitle(getTitle())
				.setNegativeButton(R.string.cancel_button, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				}).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
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

				setPositiveButtonClickCustomListener(dialog);

				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);

				initTextChangedListener();
			}
		});

		return dialog;
	}

	protected abstract void initialize();

	protected abstract boolean handleOkButton();

	protected abstract String getTitle();

	protected abstract String getHint();

	protected void onOkButtonHandled() {
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

	/**
	 * This method overrides standart AlertDialog's positive button click listener to prevent dialog dismissing.
	 */
	private void setPositiveButtonClickCustomListener(final DialogInterface dialog) {
		Button buttonPositive = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
		buttonPositive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean okButtonResult = handleOkButton();
				onOkButtonHandled();

				if (okButtonResult) {
					dismiss();
				}
			}
		});
	}
}
