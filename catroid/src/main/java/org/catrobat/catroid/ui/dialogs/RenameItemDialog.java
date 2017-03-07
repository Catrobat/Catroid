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
package org.catrobat.catroid.ui.dialogs;

import org.catrobat.catroid.R;

public class RenameItemDialog extends TextDialog {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_rename";

	private RenameItemInterface renameItemInterface;

	public RenameItemDialog(int title, int inputLabel, String previousItemName, RenameItemInterface
			renameItemInterface) {
		super(title, inputLabel, previousItemName, false);
		this.renameItemInterface = renameItemInterface;
	}

	@Override
	protected boolean handlePositiveButtonClick() {
		String newName = input.getText().toString().trim();

		if (newName.equals(previousText)) {
			renameItemInterface.clearCheckedItems();
			return true;
		}

		boolean newNameConsistsOfSpacesOnly = newName.isEmpty();

		if (newNameConsistsOfSpacesOnly) {
			input.setError(getString(R.string.name_consists_of_spaces_only));
			return false;
		}

		if (renameItemInterface.itemNameExists(newName)) {
			input.setError(getString(R.string.name_already_exists));
			return false;
		} else {
			renameItemInterface.clearCheckedItems();
			renameItemInterface.renameItem(newName);
			return true;
		}
	}

	@Override
	protected void handleNegativeButtonClick() {
		renameItemInterface.clearCheckedItems();
	}

	public interface RenameItemInterface {

		void clearCheckedItems();
		boolean itemNameExists(String newItemName);
		void renameItem(String newItemName);
	}
}

