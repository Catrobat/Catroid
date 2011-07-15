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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.costumeData;
import at.tugraz.ist.catroid.ui.CostumeActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class RenameCostumeDialog extends Dialog {
	protected CostumeActivity costumeActivity;
	private EditText renameName;
	int length;
	String last;

	public RenameCostumeDialog(CostumeActivity costumeActivity) {
		super(costumeActivity);
		this.costumeActivity = costumeActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		renameName = null;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_rename);
		setTitle(R.string.rename_costume_dialog);
		setCanceledOnTouchOutside(true);
		length = costumeActivity.getCostumeToEdit().getCostumeName().length();
		last = costumeActivity.getCostumeToEdit().getCostumeName().substring(length - 6, length);
		renameName = (EditText) findViewById(R.id.rename_edit_text);
		renameName.setText(costumeActivity.getCostumeToEdit().getCostumeName().substring(0, length - 6));

		Button renameButton = (Button) findViewById(R.id.rename_button);
		renameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String costumeName = ((EditText) findViewById(R.id.rename_edit_text)).getText().toString();
				if (costumeName.equalsIgnoreCase(costumeActivity.getCostumeToEdit().getCostumeName()
						.substring(0, length - 5))) {
					dismiss();
					((EditText) findViewById(R.id.rename_edit_text)).setText(null);
					return;
				}
				if (costumeName != null && !costumeName.equalsIgnoreCase("")) {
					for (costumeData tempCostume : ProjectManager.getInstance().getCurrentSprite().getCostumeList()) {
						if (tempCostume.getCostumeName().equalsIgnoreCase(costumeName)) {
							Utils.displayErrorMessage(costumeActivity,
									costumeActivity.getString(R.string.costumename_already_exists));
							return;
						}
					}
					costumeActivity.getCostumeToEdit().setCostumeName(costumeName + last);
				} else {
					Utils.displayErrorMessage(costumeActivity, costumeActivity.getString(R.string.costumename_invalid));
					return;
				}
				dismiss();
				((EditText) findViewById(R.id.rename_edit_text)).setText(null);
			}
		});

		this.setOnShowListener(new OnShowListener() {
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(renameName, InputMethodManager.SHOW_IMPLICIT);
			}
		});

		this.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
						case KeyEvent.KEYCODE_ENTER: {
							String costumeName = ((EditText) findViewById(R.id.rename_edit_text)).getText().toString();
							if (costumeName.equalsIgnoreCase(costumeActivity.getCostumeToEdit().getCostumeName()
									.substring(0, length - 5))) {
								dismiss();
								((EditText) findViewById(R.id.rename_edit_text)).setText(null);
								return true;
							}
							if (costumeName != null && !costumeName.equalsIgnoreCase("")) {
								for (costumeData tempCostume : ProjectManager.getInstance().getCurrentSprite()
										.getCostumeList()) {
									if (tempCostume.getCostumeName().equalsIgnoreCase(costumeName)) {
										Utils.displayErrorMessage(costumeActivity,
												costumeActivity.getString(R.string.costumename_already_exists));
										return true;
									}
								}
								costumeActivity.getCostumeToEdit().setCostumeName(costumeName + last);
							} else {
								Utils.displayErrorMessage(costumeActivity,
										costumeActivity.getString(R.string.costumename_invalid));
								return true;
							}
							dismiss();
							((EditText) findViewById(R.id.rename_edit_text)).setText(null);
							return true;
						}
						default: {
							break;
						}
					}
				}
				return false;
			}
		});
	}
}