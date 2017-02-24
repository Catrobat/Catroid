/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.TextSizeUtil;

public abstract class TextDialog extends DialogFragment {

	protected EditText input;

	protected int title;
	protected int inputLabel;
	protected String previousText;
	protected boolean allowEmptyInput;

	public TextDialog(int title, int inputLabel, String previousText, boolean allowEmptyInput) {
		this.title = title;
		this.inputLabel = inputLabel;
		this.previousText = previousText;
		this.allowEmptyInput = allowEmptyInput;
	}

	protected View inflateLayout() {
		final LayoutInflater inflater = getActivity().getLayoutInflater();
		return inflater.inflate(R.layout.dialog_text_input, null);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		View view = inflateLayout();

		builder.setTitle(title);
		builder.setView(view);

		final TextView inputLabelView = (TextView) view.findViewById(R.id.input_label);
		inputLabelView.setText(inputLabel);

		input = (EditText) view.findViewById(R.id.edit_text);
		input.setText(previousText);

		builder.setPositiveButton(R.string.ok, null);
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				onCancel(dialog);
			}
		});

		final AlertDialog alertDialog = builder.create();
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				showKeyboard();
				Button buttonPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				buttonPositive.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (handlePositiveButtonClick()) {
							dismiss();
						}
					}
				});
				if (!allowEmptyInput) {
					input.addTextChangedListener(getInputTextWatcher(buttonPositive));
				}
			}
		});

		TextSizeUtil.enlargeViewGroup((ViewGroup) view.getRootView());

		return alertDialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		handleNegativeButtonClick();
		dismiss();
	}

	protected abstract boolean handlePositiveButtonClick();

	protected abstract void handleNegativeButtonClick();

	protected void showKeyboard() {
		if (input.requestFocus()) {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	protected TextWatcher getInputTextWatcher(final Button positiveButton) {
		return new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0) {
					positiveButton.setEnabled(false);
				} else {
					positiveButton.setEnabled(true);
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
}
