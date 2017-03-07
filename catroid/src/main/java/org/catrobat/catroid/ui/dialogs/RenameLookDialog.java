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
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.utils.Utils;

public class RenameLookDialog extends TextDialog {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_rename_look";
	public static final String EXTRA_NEW_LOOK_NAME = "new_look_name";

	public RenameLookDialog(int title, int inputLabel, String previousText) {
		super(title, inputLabel, previousText, false);
	}

	@Override
	protected boolean handlePositiveButtonClick() {
		String newLookName = input.getText().toString().trim();

		if (newLookName.equals(previousText)) {
			return true;
		}

		boolean newNameConsistsOfSpacesOnly = newLookName.isEmpty();

		if (newNameConsistsOfSpacesOnly) {
			input.setError(getString(R.string.name_consists_of_spaces_only));
			return false;
		}

		LookData lookData = new LookData();
		lookData.setLookName(newLookName);
		newLookName = Utils.getUniqueLookName(lookData, false);

		Intent intent = new Intent(ScriptActivity.ACTION_LOOK_RENAMED);
		intent.putExtra(EXTRA_NEW_LOOK_NAME, newLookName);
		getActivity().sendBroadcast(intent);
		return true;
	}

	@Override
	protected void handleNegativeButtonClick() {
	}
}
