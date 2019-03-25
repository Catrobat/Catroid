/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
import android.support.annotation.Nullable;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;

import java.util.List;

public class RenameItemTextWatcher<T extends Nameable> extends TextInputDialog.TextWatcher {

	private T item;
	private List<T> scope;

	public RenameItemTextWatcher(T item, List<T> scope) {
		this.item = item;
		this.scope = scope;
	}

	private boolean isNameUnique(String name) {
		for (Nameable item : scope) {
			if (item.getName().equals(name)) {
				return false;
			}
		}
		return true;
	}

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
		} else if (!input.equals(item.getName()) && !isNameUnique(input)) {
			error = context.getString(R.string.name_already_exists);
		}

		return error;
	}
}
