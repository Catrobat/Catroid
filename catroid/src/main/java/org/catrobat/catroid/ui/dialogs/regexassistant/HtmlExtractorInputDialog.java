/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.ui.dialogs.regexassistant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;

import com.google.android.material.textfield.TextInputLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.ViewUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public final class HtmlExtractorInputDialog extends AlertDialog {
	private HtmlExtractorInputDialog(@NonNull Context context) {
		super(context);
	}

	public static class Builder extends androidx.appcompat.app.AlertDialog.Builder {

		@Nullable
		private String keywordHint;
		@Nullable
		private String keyword;
		@Nullable
		private String htmlHint;
		@Nullable
		private String htmlText;
		@Nullable
		private HtmlExtractorInputDialog.TextWatcher textWatcherKeyword;
		@Nullable
		private HtmlExtractorInputDialog.TextWatcherHtml textWatcherHtml;

		public Builder(@NonNull Context context) {
			super(context);
			setView(R.layout.dialog_regex_html_extractor);
		}

		public Builder setKeywordHint(@Nullable String keywordHint) {
			this.keywordHint = keywordHint;
			return this;
		}

		public Builder setKeywordHint(@StringRes int id) {
			setKeywordHint(getContext().getText(id).toString());
			return this;
		}

		public Builder setKeyword(@Nullable String keyword) {
			this.keyword = keyword;
			return this;
		}

		public Builder setHtmlHint(@Nullable String htmlHint) {
			this.htmlHint = htmlHint;
			return this;
		}

		public Builder setHtmlHint(@StringRes int id) {
			setHtmlHint(getContext().getText(id).toString());
			return this;
		}

		public Builder setHtmlText(@Nullable String htmlText) {
			this.htmlText = htmlText;
			return this;
		}

		public Builder setPositiveButton(String text,
				final HtmlExtractorInputDialog.OnClickListener listener) {
			setPositiveButton(text, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					TextInputLayout textInputLayout = ((Dialog) dialog).findViewById(R.id.input);
					TextInputLayout textInputLayout1 = ((Dialog) dialog).findViewById(R.id.input1);

					String text = textInputLayout.getEditText().getText().toString();
					String text1 = textInputLayout1.getEditText().getText().toString();

					listener.onPositiveButtonClick(dialog, text, text1);
				}
			});
			return this;
		}

		public Builder setPositiveButton(@StringRes int id,
				final HtmlExtractorInputDialog.OnClickListener listener) {
			return setPositiveButton(getContext().getText(id).toString(), listener);
		}

		@Override
		public androidx.appcompat.app.AlertDialog create() {
			final androidx.appcompat.app.AlertDialog alertDialog = super.create();

			alertDialog.setOnShowListener(dialog -> {
				TextInputLayout textInputLayout = alertDialog.findViewById(R.id.input);
				textInputLayout.setHint(keywordHint);
				textInputLayout.getEditText().setText(keyword);
				textInputLayout.getEditText().selectAll();

				TextInputLayout textInputLayout1 = alertDialog.findViewById(R.id.input1);
				textInputLayout1.setHint(htmlHint);
				textInputLayout1.getEditText().setText(htmlText);
				textInputLayout1.getEditText().selectAll();

				if (textWatcherKeyword != null) {
					textInputLayout.getEditText().addTextChangedListener(textWatcherKeyword);
					textWatcherKeyword.setInputLayout(textInputLayout);
					textWatcherKeyword.setAlertDialog(alertDialog);
					String error = textWatcherKeyword
							.validateInput(textInputLayout.getEditText().getText().toString(), getContext());

					alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setEnabled(error == null);
				}

				if (textWatcherHtml != null) {
					textInputLayout.getEditText().addTextChangedListener(textWatcherHtml);
					textWatcherHtml.setInputLayout(textInputLayout1);
					textWatcherHtml.setAlertDialog(alertDialog);
					String error = textWatcherHtml
							.validateInput(textInputLayout.getEditText().getText().toString(), getContext());

					alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setEnabled(error == null);
				}

				ViewUtils.showKeyboard(textInputLayout.getEditText());
			});

			return alertDialog;
		}
	}

	public abstract static class TextWatcher implements android.text.TextWatcher {

		private TextInputLayout inputLayout;
		private androidx.appcompat.app.AlertDialog alertDialog;

		private void setInputLayout(@NonNull TextInputLayout inputLayout) {
			this.inputLayout = inputLayout;
		}

		private void setAlertDialog(@NonNull androidx.appcompat.app.AlertDialog alertDialog) {
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
			alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setEnabled(error == null);
		}

		@Nullable
		public abstract String validateInput(String input, Context context);
	}

	public abstract static class TextWatcherHtml implements android.text.TextWatcher {

		private TextInputLayout inputLayout;
		private androidx.appcompat.app.AlertDialog alertDialog;

		private void setInputLayout(@NonNull TextInputLayout inputLayout) {
			this.inputLayout = inputLayout;
		}

		private void setAlertDialog(@NonNull androidx.appcompat.app.AlertDialog alertDialog) {
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
			alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setEnabled(error == null);
		}

		@Nullable
		public abstract String validateInput(String input, Context context);
	}

	public interface OnClickListener {

		void onPositiveButtonClick(DialogInterface dialog, String keywordInput,
					String htmlInput);
	}
}
