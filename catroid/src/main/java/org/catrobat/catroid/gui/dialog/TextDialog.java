/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.gui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import org.catrobat.catroid.R;

public abstract class TextDialog extends DialogFragment {

	protected TextInputLayout input;

	protected int title;
	protected String defaultText;
	protected int hint;
	protected boolean allowEmptyInput;

	public TextDialog(int title, @Nullable String defaultText, @Nullable int hint, boolean allowEmptyInput) {
		this.title = title;
		this.defaultText = defaultText;
		this.hint = hint;
		this.allowEmptyInput = allowEmptyInput;
	}

	@SuppressLint("InflateParams")
	protected View inflateLayout() {
		final LayoutInflater inflater = getActivity().getLayoutInflater();
		return inflater.inflate(R.layout.dialog_text, null);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		View view = inflateLayout();

		builder.setTitle(title);
		builder.setView(view);

		input = (TextInputLayout) view.findViewById(R.id.input);

		input.getEditText().setText(defaultText);
		input.setHint(getActivity().getString(hint));

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

				input.getEditText().addTextChangedListener(getTextWatcher(buttonPositive));
			}
		});

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
		if (input.getEditText().requestFocus()) {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(input.getEditText(), InputMethodManager.SHOW_IMPLICIT);
		}
	}

	protected TextWatcher getTextWatcher(final Button positiveButton) {

		return new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				input.setError(null);

				if (allowEmptyInput) {
					return;
				}

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
