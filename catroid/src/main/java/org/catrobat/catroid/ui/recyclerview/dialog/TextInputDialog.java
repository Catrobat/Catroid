/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;

import org.catrobat.catroid.R;

public final class TextInputDialog extends AlertDialog {

	private TextInputDialog(@NonNull Context context) {
		super(context);
	}

	public static class Builder extends AlertDialog.Builder {

		@Nullable
		private CharSequence hint;
		@Nullable
		private CharSequence text;

		@Nullable
		private DialogTextWatcher dialogTextWatcher;

		public Builder(@NonNull Context context) {
			super(context);
			setView(R.layout.dialog_text_input);
		}

		public Builder setHint(CharSequence hint) {
			this.hint = hint;
			return this;
		}

		public Builder setText(CharSequence text) {
			this.text = text;
			return this;
		}

		public Builder setDialogTextWatcher(DialogTextWatcher dialogTextWatcher) {
			this.dialogTextWatcher = dialogTextWatcher;
			return this;
		}

		public Builder setPositiveButton(CharSequence text, final OnTextDialogClickListener listener) {
			setPositiveButton(text, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					TextInputLayout textInputLayout = ((Dialog) dialog).findViewById(R.id.input);
					String text = textInputLayout.getEditText().getText().toString().trim();
					listener.onPositiveButtonClick(text);
				}
			});
			return this;
		}

		@Override
		public AlertDialog create() {
			final AlertDialog alertDialog = super.create();

			alertDialog.setOnShowListener(new OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					TextInputLayout textInputLayout = ((Dialog) dialog).findViewById(R.id.input);
					textInputLayout.setHint(hint);
					textInputLayout.getEditText().setText(text);
					if (dialogTextWatcher != null) {
						textInputLayout.getEditText().addTextChangedListener(dialogTextWatcher);
						dialogTextWatcher.setInputLayout(textInputLayout);
						dialogTextWatcher.setAlertDialog(alertDialog);
					}
				}
			});
			return alertDialog;
		}
	}

	public abstract static class DialogTextWatcher implements TextWatcher {

		private TextInputLayout inputLayout;
		private AlertDialog alertDialog;

		public void setInputLayout(@NonNull TextInputLayout inputLayout) {
			this.inputLayout = inputLayout;
		}

		public void setAlertDialog(@NonNull AlertDialog alertDialog) {
			this.alertDialog = alertDialog;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			String input = s.toString();

			String error = validateInput(input, alertDialog.getContext());
			inputLayout.setError(error);
			alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(error == null);
		}

		@Nullable
		public abstract String validateInput(String input, Context context);
	}

	public interface OnTextDialogClickListener {

		void onPositiveButtonClick(String text);
	}
}
