/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import android.text.Editable;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.ViewUtils;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.InputWatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public final class TextInputDialog extends AlertDialog {

	private TextInputDialog(@NonNull Context context) {
		super(context);
	}

	public static class Builder extends AlertDialog.Builder {

		@Nullable
		private String hint;
		@Nullable
		private String text;
		@Nullable
		private InputWatcher.TextWatcher textWatcher;

		public Builder(@NonNull Context context) {
			super(context);
			setView(R.layout.dialog_text_input);
		}

		public Builder setHint(String hint) {
			this.hint = hint;
			return this;
		}

		public Builder setText(String text) {
			this.text = text;
			return this;
		}

		public Builder setTextWatcher(InputWatcher.TextWatcher textWatcher) {
			this.textWatcher = textWatcher;
			return this;
		}

		public Builder setPositiveButton(String buttonText, final OnClickListener listener) {
			setPositiveButton(buttonText, (DialogInterface.OnClickListener) (dialog, which) -> {
				TextInputLayout textInputLayout = ((Dialog) dialog).findViewById(R.id.input);
				String text = textInputLayout.getEditText().getText().toString();
				listener.onPositiveButtonClick(dialog, text);
			});
			return this;
		}

		@Override
		public AlertDialog create() {
			final AlertDialog alertDialog = super.create();

			alertDialog.setOnShowListener(dialog -> {
				TextInputLayout textInputLayout = alertDialog.findViewById(R.id.input);
				EditText editText = textInputLayout.getEditText();
				textInputLayout.setHint(hint);

				if (textWatcher != null) {
					textInputLayout.getEditText().addTextChangedListener(textWatcher);
					textWatcher.setInputLayout(textInputLayout);
					textWatcher.setButton(alertDialog.getButton(AlertDialog.BUTTON_POSITIVE));
					textWatcher.setContext(getContext());
				}

				editText.setText(text);
				editText.selectAll();
				ViewUtils.showKeyboard(editText);
			});
			return alertDialog;
		}
	}

	public abstract static class TextWatcher implements android.text.TextWatcher {

		private TextInputLayout inputLayout;
		private AlertDialog alertDialog;

		private void setInputLayout(@NonNull TextInputLayout inputLayout) {
			this.inputLayout = inputLayout;
		}

		private void setAlertDialog(@NonNull AlertDialog alertDialog) {
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

	public interface OnClickListener {

		void onPositiveButtonClick(DialogInterface dialog, String textInput);
	}
}
