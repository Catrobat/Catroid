/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import android.content.Intent;
import android.os.Bundle;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class RenameSpriteDialog extends TextDialog {

	private static final String BUNDLE_ARGUMENTS_OLD_SPRITE_NAME = "old_sprite_name";
	public static final String EXTRA_NEW_SPRITE_NAME = "new_sprite_name";
	public static final String DIALOG_FRAGMENT_TAG = "dialog_rename_sprite";

	private String oldSpriteName;

	public static RenameSpriteDialog newInstance(String oldSpriteName) {
		RenameSpriteDialog dialog = new RenameSpriteDialog();

		Bundle arguments = new Bundle();
		arguments.putString(BUNDLE_ARGUMENTS_OLD_SPRITE_NAME, oldSpriteName);
		dialog.setArguments(arguments);

		return dialog;
	}

	@Override
	protected void initialize() {
		oldSpriteName = getArguments().getString(BUNDLE_ARGUMENTS_OLD_SPRITE_NAME);
		input.setText(oldSpriteName);
	}

	@Override
	protected boolean handleOkButton() {
		String newSpriteName = (input.getText().toString()).trim();
		ProjectManager projectManager = ProjectManager.getInstance();

		if (projectManager.spriteExists(newSpriteName) && !newSpriteName.equalsIgnoreCase(oldSpriteName)) {
			Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.spritename_already_exists));
			return false;
		}

		if (newSpriteName.equals(oldSpriteName)) {
			dismiss();
			return false;
		}

		if (newSpriteName != null && !newSpriteName.equalsIgnoreCase("")) {
			Intent intent = new Intent(ScriptTabActivity.ACTION_SPRITE_RENAMED);
			intent.putExtra(EXTRA_NEW_SPRITE_NAME, newSpriteName);
			getActivity().sendBroadcast(intent);
		} else {
			Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.spritename_invalid));
			return false;
		}

		return true;
	}

	@Override
	protected String getTitle() {
		return getString(R.string.rename_sprite_dialog);
	}

	@Override
	protected String getHint() {
		return getString(R.string.new_sprite_dialog_default_sprite_name);
	}
}
