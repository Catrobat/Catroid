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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.ScriptActivity;

public class RenameSpriteDialog extends TextDialog {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_rename_sprite";
	public static final String EXTRA_NEW_SPRITE_NAME = "new_sprite_name";

	public RenameSpriteDialog(int title, int inputLabel, String previousText) {
		super(title, inputLabel, previousText, false);
	}

	@Override
	protected boolean handlePositiveButtonClick() {
		String newSpriteName = input.getText().toString().trim();
		ProjectManager projectManager = ProjectManager.getInstance();

		if (newSpriteName.equals(previousText)) {
			return true;
		}

		boolean newNameConsistsOfSpacesOnly = newSpriteName.isEmpty();

		if (newNameConsistsOfSpacesOnly) {
			input.setError(getString(R.string.name_consists_of_spaces_only));
			return false;
		}

		if (projectManager.spriteExists(newSpriteName) && !newSpriteName.equalsIgnoreCase(previousText)) {
			input.setError(getString(R.string.spritename_already_exists));
			return false;
		}

		Intent intent = new Intent(ScriptActivity.ACTION_SPRITE_RENAMED);
		intent.putExtra(EXTRA_NEW_SPRITE_NAME, newSpriteName);
		getActivity().sendBroadcast(intent);

		return true;
	}

	@Override
	protected void handleNegativeButtonClick() {
	}
}
