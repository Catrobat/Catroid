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
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.ui.CostumeActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class RenameCostumeDialog {
	protected CostumeActivity costumeActivity;
	private EditText input;
	private Button buttonPositive;
	public Dialog renameDialog;

	public RenameCostumeDialog(CostumeActivity costumeActivity) {
		this.costumeActivity = costumeActivity;
	}

	public Dialog createDialog(CostumeData selectedCostumeInfo) {
		AlertDialog.Builder builder = new AlertDialog.Builder(costumeActivity);
		builder.setTitle(R.string.rename_costume_dialog);

		LayoutInflater inflater = (LayoutInflater) costumeActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_rename_costume, null);

		input = (EditText) view.findViewById(R.id.dialog_rename_costume_editText);
		input.setText(selectedCostumeInfo.getCostumeName());

		buttonPositive = (Button) view.findViewById(R.id.btn_rename_costume);

		builder.setView(view);

		initKeyListener(builder);

		renameDialog = builder.create();
		renameDialog.setCanceledOnTouchOutside(true);

		initAlertDialogListener(renameDialog);

		return renameDialog;
	}

	public void handleOkButton() {
		String newCostumeName = (input.getText().toString()).trim();
		String oldCostumeName = costumeActivity.selectedCostumeInfo.getCostumeName();

		if (newCostumeName.equalsIgnoreCase(oldCostumeName)) {
			renameDialog.cancel();
			return;
		}
		if (newCostumeName != null && !newCostumeName.equalsIgnoreCase("")) {
			costumeActivity.selectedCostumeInfo.setCostumeName(newCostumeName);
		} else {
			Utils.displayErrorMessage(costumeActivity, costumeActivity.getString(R.string.costumename_invalid));
			return;
		}
		renameDialog.cancel();
	}

	private void initKeyListener(AlertDialog.Builder builder) {
		builder.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					handleOkButton();
					return true;
				}
				return false;
			}
		});
	}

	private void initAlertDialogListener(Dialog dialog) {

		dialog.setOnShowListener(new OnShowListener() {
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) costumeActivity
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
				input.setText(costumeActivity.selectedCostumeInfo.getCostumeName());
			}
		});

		input.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0 || (s.length() == 1 && s.charAt(0) == '.')) {
					Toast.makeText(costumeActivity, R.string.notification_invalid_text_entered, Toast.LENGTH_SHORT)
							.show();
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
