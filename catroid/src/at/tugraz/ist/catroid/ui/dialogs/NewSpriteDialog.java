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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class NewSpriteDialog {
	protected ProjectActivity projectActivity;
	private EditText input;
	private Button buttonPositive;
	public Dialog newSpriteDialog;
	private ProjectManager projectManager;

	public NewSpriteDialog(ProjectActivity projectActivity) {
		this.projectActivity = projectActivity;
		projectManager = ProjectManager.getInstance();
	}

	public Dialog createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(projectActivity);
		builder.setTitle(R.string.new_sprite_dialog_title);

		LayoutInflater inflater = (LayoutInflater) projectActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_new_sprite, null);

		input = (EditText) view.findViewById(R.id.dialog_new_sprite_editText);
		input.setHint(projectActivity.getString(R.string.new_sprite_dialog_default_sprite_name));

		buttonPositive = (Button) view.findViewById(R.id.dialog_new_sprite_ok_button);

		builder.setView(view);

		initKeyListener(builder);

		newSpriteDialog = builder.create();
		newSpriteDialog.setCanceledOnTouchOutside(true);

		initAlertDialogListener(newSpriteDialog);

		return newSpriteDialog;
	}

	public void handleOkButton() {

		String spriteName = input.getText().toString();

		if (spriteName == null || spriteName.equalsIgnoreCase("")) {
			Utils.displayErrorMessage(projectActivity, projectActivity.getString(R.string.spritename_invalid));
			return;
		}

		if (projectManager.spriteExists(spriteName)) {
			Utils.displayErrorMessage(projectActivity, projectActivity.getString(R.string.spritename_already_exists));
			return;
		}
		Sprite sprite = new Sprite(spriteName);
		projectManager.addSprite(sprite);
		((ArrayAdapter<?>) projectActivity.getListAdapter()).notifyDataSetChanged();

		input.setText(null);
		newSpriteDialog.dismiss();
	}

	private void initKeyListener(AlertDialog.Builder builder) {
		builder.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					String newSpriteName = (input.getText().toString()).trim();
					if (projectManager.spriteExists(newSpriteName)) {
						Utils.displayErrorMessage(projectActivity,
								projectActivity.getString(R.string.spritename_already_exists));
					} else {
						handleOkButton();
						return true;
					}
				}
				return false;
			}
		});
	}

	private void initAlertDialogListener(Dialog dialog) {

		dialog.setOnShowListener(new OnShowListener() {
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) projectActivity
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
			}
		});

		input.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0 || (s.length() == 1 && s.charAt(0) == '.')) {
					buttonPositive.setEnabled(false);
				} else {
					buttonPositive.setEnabled(true);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
	}
}
