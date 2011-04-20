/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class RenameSpriteDialog extends Dialog {
	protected ProjectActivity projectActivity;

	public RenameSpriteDialog(ProjectActivity projectActivity) {
		super(projectActivity);
		this.projectActivity = projectActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_rename);
		setTitle(R.string.rename_sprite_dialog);
		setCanceledOnTouchOutside(true);

		Button renameButton = (Button) findViewById(R.id.renameButton);
		renameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String spriteName = ((EditText) findViewById(R.id.renameEditText)).getText().toString();
				if (spriteName.equalsIgnoreCase(projectActivity.getSpriteToEdit().getName())) {
					dismiss();
					return;
				}
				if (spriteName != null && !spriteName.equalsIgnoreCase("")) {
					for (Sprite tempSprite : ProjectManager.getInstance().getCurrentProject().getSpriteList()) {
						if (tempSprite.getName().equalsIgnoreCase(spriteName)) {
							Utils.displayErrorMessage(projectActivity,
									projectActivity.getString(R.string.spritename_already_exists));
							return;
						}
					}
					projectActivity.getSpriteToEdit().setName(spriteName);
				} else {
					Utils.displayErrorMessage(projectActivity, projectActivity.getString(R.string.spritename_invalid));
					return;
				}
				dismiss();
			}
		});
	}
}