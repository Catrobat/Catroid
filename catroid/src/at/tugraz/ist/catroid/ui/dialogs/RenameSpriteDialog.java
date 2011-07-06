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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class RenameSpriteDialog {
	protected ProjectActivity projectActivity;
	private EditText input;

	public RenameSpriteDialog(ProjectActivity projectActivity) {
		this.projectActivity = projectActivity;
	}

	public Dialog createDialog(String dialogTitle) {
		AlertDialog.Builder builder = new AlertDialog.Builder(projectActivity);
		builder.setTitle(R.string.rename_sprite_dialog);

		input = new EditText(projectActivity);
		input.setText(projectActivity.getSpriteToEdit().getName());
		input.setSingleLine(true);
		builder.setView(input);

		initBuilder(builder);

		final AlertDialog alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(true);

		initAlertDialogListener(alertDialog);

		return alertDialog;
	}

	private void handleOkButton(DialogInterface dialog) {
		String newSpriteName = (input.getText().toString()).trim();

		if (newSpriteName.equalsIgnoreCase(projectActivity.getSpriteToEdit().getName())) {
			dialog.dismiss();
			return;
		}
		if (newSpriteName != null && !newSpriteName.equalsIgnoreCase("")) {
			projectActivity.getSpriteToEdit().setName(newSpriteName);
		} else {
			Utils.displayErrorMessage(projectActivity, projectActivity.getString(R.string.spritename_invalid));
			return;
		}
		dialog.dismiss();
	}

	private void initBuilder(AlertDialog.Builder builder) {
		builder.setPositiveButton(projectActivity.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				handleOkButton(dialog);
			}
		});

		builder.setNegativeButton(projectActivity.getString(R.string.cancel_button),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		builder.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					String newSpriteName = (input.getText().toString()).trim();
					if (spriteAlreadyExists(newSpriteName)) {
						return false;
					}
					handleOkButton(dialog);
					return false;
				}
				return false;
			}
		});
	}

	private void initAlertDialogListener(final AlertDialog alertDialog) {

		alertDialog.setOnShowListener(new OnShowListener() {
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) projectActivity
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
			}
		});

		input.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0 || (s.length() == 1 && s.charAt(0) == '.')) {
					Toast.makeText(projectActivity, R.string.notification_invalid_text_entered, Toast.LENGTH_SHORT)
							.show();
					alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
				} else {
					alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
				}

				if (spriteAlreadyExists(s.toString())
						&& !(s.toString()).equalsIgnoreCase(projectActivity.getSpriteToEdit().getName())) {
					alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
					Toast.makeText(projectActivity, R.string.spritename_already_exists, Toast.LENGTH_SHORT).show();
					return;
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
	}

	private boolean spriteAlreadyExists(String newSpriteName) {
		for (Sprite tempSprite : ProjectManager.getInstance().getCurrentProject().getSpriteList()) {
			if (tempSprite.getName().equalsIgnoreCase(newSpriteName.toString())) {
				return true;
			}
		}
		return false;
	}
}