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
package org.catrobat.catroid.ui.recyclerview.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DialogInputWatcher;

public abstract class TextInputDialogFragment extends DialogFragment {

	protected TextInputLayout inputLayout;
	protected int title;
	protected int hint;
	protected String text;
	protected boolean allowEmptyInput;

	public TextInputDialogFragment(int title, int hint, @Nullable String text, boolean allowEmptyInput) {
		this.title = title;
		this.hint = hint;
		this.text = text;
		this.allowEmptyInput = allowEmptyInput;
	}

	protected View inflateView() {
		return View.inflate(getActivity(), R.layout.dialog_text_input, null);
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View view = inflateView();

		inputLayout = view.findViewById(R.id.input);
		inputLayout.setHint(getString(hint));
		inputLayout.getEditText().setText(text);

		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(title)
				.setView(view)
				.setPositiveButton(R.string.ok, null)
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						onCancel(dialog);
					}
				})
				.create();

		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button buttonPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				buttonPositive.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (onPositiveButtonClick()) {
							dismiss();
						}
					}
				});
				if (!allowEmptyInput) {
					buttonPositive.setEnabled(!inputLayout.getEditText().getText().toString().isEmpty());
				}
				DialogInputWatcher inputWatcher = new DialogInputWatcher(inputLayout, buttonPositive, allowEmptyInput);
				inputLayout.getEditText().addTextChangedListener(inputWatcher);
			}
		});

		return alertDialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		onNegativeButtonClick();
		dismiss();
	}

	protected abstract boolean onPositiveButtonClick();

	protected abstract void onNegativeButtonClick();
}
