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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.SoundActivity;
import at.tugraz.ist.catroid.ui.adapter.SoundAdapter;
import at.tugraz.ist.catroid.utils.Utils;

public class RenameSoundDialog {
	protected ScriptTabActivity scriptTabActivity;
	private EditText input;
	private Button buttonPositive;

	public RenameSoundDialog(ScriptTabActivity scriptTabActivity) {
		this.scriptTabActivity = scriptTabActivity;
	}

	public Dialog createDialog(SoundInfo soundInfoToEdit) {
		AlertDialog.Builder builder = new AlertDialog.Builder(scriptTabActivity);
		builder.setTitle(R.string.rename_sound_dialog);

		LayoutInflater inflater = (LayoutInflater) scriptTabActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_rename_sound, null);

		input = (EditText) view.findViewById(R.id.dialog_rename_sound_editText);
		input.setText(soundInfoToEdit.getTitle());

		buttonPositive = (Button) view.findViewById(R.id.btn_rename_sound);

		builder.setView(view);

		initKeyListener(builder);

		final Dialog renameDialog = builder.create();
		renameDialog.setCanceledOnTouchOutside(true);

		initAlertDialogListener(renameDialog);

		input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					renameDialog.getWindow().setSoftInputMode(
							WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		return renameDialog;
	}

	public void handleOkButton() {
		String newSoundTitle = (input.getText().toString()).trim();
		String oldSoundTitle = scriptTabActivity.selectedSoundInfo.getTitle();

		if (newSoundTitle.equalsIgnoreCase(oldSoundTitle)) {
			scriptTabActivity.dismissDialog(ScriptTabActivity.DIALOG_RENAME_SOUND);
			return;
		}
		if (newSoundTitle != null && !newSoundTitle.equalsIgnoreCase("")) {
			newSoundTitle = Utils.getUniqueSoundName(newSoundTitle);
			scriptTabActivity.selectedSoundInfo.setTitle(newSoundTitle);
			((SoundAdapter) ((SoundActivity) scriptTabActivity.getCurrentActivity()).getListAdapter())
					.notifyDataSetChanged(); //TODO: this is madness!
		} else {
			Utils.displayErrorMessage(scriptTabActivity, scriptTabActivity.getString(R.string.soundname_invalid));
			return;
		}
		scriptTabActivity.dismissDialog(ScriptTabActivity.DIALOG_RENAME_SOUND);
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

		input.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0 || (s.length() == 1 && s.charAt(0) == '.')) {
					Toast.makeText(scriptTabActivity, R.string.notification_invalid_text_entered, Toast.LENGTH_SHORT)
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
