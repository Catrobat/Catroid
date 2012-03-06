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

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class RenameSpriteDialog extends TextDialog {

	public RenameSpriteDialog(ProjectActivity projectActivity) {
		super(projectActivity, projectActivity.getString(R.string.rename_sprite_dialog), projectActivity
				.getString(R.string.new_sprite_dialog_default_sprite_name));
		initKeyListener();
	}

	public void handleOkButton() {
		String newSpriteName = (input.getText().toString()).trim();
		String oldSpriteName = ((ProjectActivity) activity).getSpriteToEdit().getName();

		if (projectManager.spriteExists(newSpriteName) && !newSpriteName.equalsIgnoreCase(oldSpriteName)) {
			Utils.displayErrorMessage(activity, activity.getString(R.string.spritename_already_exists));
			return;
		}

		if (newSpriteName.equalsIgnoreCase(((ProjectActivity) activity).getSpriteToEdit().getName())) {
			dialog.cancel();
			return;
		}
		if (newSpriteName != null && !newSpriteName.equalsIgnoreCase("")) {
			((ProjectActivity) activity).getSpriteToEdit().setName(newSpriteName);
		} else {
			Utils.displayErrorMessage(activity, activity.getString(R.string.spritename_invalid));
			return;
		}
		dialog.cancel();
	}

	private void initKeyListener() {
		dialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					String newSpriteName = (input.getText().toString()).trim();
					String oldSpriteName = ((ProjectActivity) activity).getSpriteToEdit().getName();
					if (projectManager.spriteExists(newSpriteName) && !newSpriteName.equalsIgnoreCase(oldSpriteName)) {
						Utils.displayErrorMessage(activity, activity.getString(R.string.spritename_already_exists));
					} else {
						handleOkButton();
						return true;
					}
				}
				return false;
			}
		});

		buttonPositive.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handleOkButton();
			}
		});

		buttonNegative.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				activity.dismissDialog(ProjectActivity.DIALOG_RENAME_SPRITE);
			}
		});
	}
}
