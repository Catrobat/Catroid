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

import android.content.Intent;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.base.InputDialog;
import org.catrobat.catroid.utils.Utils;

public class RenameNfcTagDialog extends InputDialog {

	public static final String EXTRA_NEW_NFCTAG_TITLE = "new_nfctag_name";
	public static final String DIALOG_FRAGMENT_TAG = "dialog_rename_nfctag";

	public RenameNfcTagDialog(int title, int inputLabel, String previousText) {
		super(title, inputLabel, previousText, false);
	}

	@Override
	protected boolean handlePositiveButtonClick() {
		String newNfcTagName = input.getText().toString().trim();

		if (newNfcTagName.equals(previousText)) {
			return true;
		}

		boolean newNameConsistsOfSpacesOnly = newNfcTagName.isEmpty();

		if (newNameConsistsOfSpacesOnly) {
			input.setError(getString(R.string.name_consists_of_spaces_only));
			return false;
		}

		if (!newNfcTagName.equalsIgnoreCase(getString(R.string.brick_when_nfc_default_all))) {
			newNfcTagName = Utils.getUniqueNfcTagName(newNfcTagName);
		} else {
			input.setError(getString(R.string.nfctagname_invalid));
			return false;
		}

		Intent intent = new Intent(ScriptActivity.ACTION_NFCTAG_RENAMED);
		intent.putExtra(EXTRA_NEW_NFCTAG_TITLE, newNfcTagName);
		getActivity().sendBroadcast(intent);

		return true;
	}

	@Override
	protected void handleNegativeButtonClick() {
	}
}
