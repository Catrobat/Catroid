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

import org.catrobat.catroid.R;

public class RenameItemDialog extends TextDialog {

	public static final String TAG = RenameItemDialog.class.getSimpleName();

	private RenameItemInterface renameItemInterface;

	public RenameItemDialog(int title, int label, String text, RenameItemInterface renameItemInterface) {
		super(title, label, text, false);
		this.renameItemInterface = renameItemInterface;
	}

	@Override
	protected boolean handlePositiveButtonClick() {
		String name = inputLayout.getEditText().getText().toString().trim();

		if (name.isEmpty()) {
			inputLayout.setError(getString(R.string.name_consists_of_spaces_only));
			return false;
		}

		if (renameItemInterface.isNameUnique(name) || name.equals(text)) {
			renameItemInterface.renameItem(name);
			return true;
		} else {
			inputLayout.setError(getString(R.string.name_already_exists));
			return false;
		}
	}

	@Override
	protected void handleNegativeButtonClick() {
		renameItemInterface.renameItem(text);
	}

	public interface RenameItemInterface {

		boolean isNameUnique(String name);

		void renameItem(String name);
	}
}
