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
package org.catrobat.catroid.merge;

import android.content.Context;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.ui.recyclerview.dialog.ReplaceExistingProjectDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;

import androidx.annotation.Nullable;

public class NewProjectNameTextWatcher<T extends Nameable> extends TextInputDialog.TextWatcher {

	@Nullable
	@Override
	public String validateInput(String input, Context context) {
		String error = null;

		if (input.isEmpty()) {
			return context.getString(R.string.name_empty);
		}

		input = input.trim();

		if (input.isEmpty()) {
			error = context.getString(R.string.name_consists_of_spaces_only);
		} else if (ReplaceExistingProjectDialogFragment.projectExistsInDirectory(input)) {
			error = context.getString(R.string.name_already_exists);
		}

		return error;
	}
}
