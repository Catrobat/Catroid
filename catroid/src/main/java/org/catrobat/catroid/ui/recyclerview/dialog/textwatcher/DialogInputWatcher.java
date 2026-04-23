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

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

public class DialogInputWatcher implements TextWatcher {

	private TextInputLayout inputLayout;
	private Button positiveButton;
	private boolean allowEmptyInput;

	public DialogInputWatcher(TextInputLayout inputLayout, Button positiveButton, boolean allowEmptyInput) {
		this.inputLayout = inputLayout;
		this.positiveButton = positiveButton;
		this.allowEmptyInput = allowEmptyInput;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		inputLayout.setError(null);
		if (!allowEmptyInput) {
			positiveButton.setEnabled(s.length() > 0);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
