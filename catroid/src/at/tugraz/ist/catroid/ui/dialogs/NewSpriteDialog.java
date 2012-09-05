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
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class NewSpriteDialog extends TextDialog {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_new_sprite";

	@Override
	protected void initialize() {
	}

	@Override
	protected boolean handleOkButton() {
		String newSpriteName = (input.getText().toString()).trim();
		ProjectManager projectManager = ProjectManager.getInstance();

		if (projectManager.spriteExists(newSpriteName)) {
			Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.spritename_already_exists));
			return false;
		}

		if (newSpriteName == null || newSpriteName.equalsIgnoreCase("")) {
			Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.spritename_invalid));
			return false;
		}

		if (projectManager.spriteExists(newSpriteName)) {
			Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.spritename_already_exists));
			return false;
		}

		Sprite sprite = new Sprite(newSpriteName);
		projectManager.addSprite(sprite);

		getActivity().sendBroadcast(new Intent(ScriptTabActivity.ACTION_SPRITES_LIST_CHANGED));

		return true;
	}

	@Override
	protected String getTitle() {
		return getString(R.string.new_sprite_dialog_title);
	}

	@Override
	protected String getHint() {
		return getString(R.string.new_sprite_dialog_default_sprite_name);
	}
}
