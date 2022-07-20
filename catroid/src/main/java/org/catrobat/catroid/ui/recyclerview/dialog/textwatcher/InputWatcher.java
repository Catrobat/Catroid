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

package org.catrobat.catroid.ui.recyclerview.dialog.textwatcher;

import android.content.Context;
import android.text.Editable;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

import org.catrobat.catroid.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class InputWatcher {
	public static class TextWatcher implements android.text.TextWatcher {
		protected TextInputLayout inputLayout;
		protected Button button;
		protected Context context;
		protected List<String> scope = new ArrayList<>();

		public void setScope(List<String> scope) {
			this.scope = scope;
		}

		public void setButton(Button button) {
			this.button = button;
		}

		public void setContext(Context context) {
			this.context = context;
		}

		public void setInputLayout(@NonNull TextInputLayout inputLayout) {
			this.inputLayout = inputLayout;
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
			String error = validateInput(input, context);
			inputLayout.setError(error);
			button.setEnabled(error == null);
		}

		@Nullable
		public String validateInput(String input, Context context) {
			String error = null;
			if (input.isEmpty()) {
				return context.getString(R.string.name_empty);
			}

			input = input.trim();

			if (input.isEmpty()) {
				error = context.getString(R.string.name_consists_of_spaces_only);
			} else if (!isNameUnique(input)) {
				error = context.getString(R.string.name_already_exists);
			}

			return error;
		}

		protected boolean isNameUnique(String name) {
			return !scope.contains(name);
		}
	}
}
