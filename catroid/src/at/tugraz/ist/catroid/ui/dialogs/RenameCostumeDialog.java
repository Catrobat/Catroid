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
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class RenameCostumeDialog extends DialogFragment {
	
	private static final String ARGS_OLD_COSTUME_NAME = "old_costume_name";
	public static final String EXTRA_NEW_COSTUME_NAME = "new_costume_name";
	
	private EditText input;
	
	public static RenameCostumeDialog newInstance(String oldCostumeName) {
		RenameCostumeDialog dialog = new RenameCostumeDialog();
		
		Bundle args = new Bundle();
		args.putString(ARGS_OLD_COSTUME_NAME, oldCostumeName);
		dialog.setArguments(args);
		
		return dialog;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final String oldCostumeName = getArguments().getString(ARGS_OLD_COSTUME_NAME);
		
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_rename_costume, null);
		input = (EditText) dialogView.findViewById(R.id.dialog_rename_costume_editText);
		input.setText(oldCostumeName);
		
		input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
		
		Dialog dialog = new AlertDialog.Builder(getActivity())
			.setView(dialogView)
			.setTitle(R.string.rename_costume_dialog)
			.setNegativeButton(R.string.cancel_button, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismiss();
				}
			})
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					handleRenameCostume(oldCostumeName);
				}
			}).create();
		
		dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				initAlertDialogListener();
			}
		});
		
		return dialog;
	}
	
	private void handleRenameCostume(String oldCostumeName) {
		String newCostumeName = (input.getText().toString()).trim();

		if (newCostumeName.equalsIgnoreCase(oldCostumeName)) {
			dismiss();
		}
		
		if (newCostumeName != null && !newCostumeName.equalsIgnoreCase("")) {
			newCostumeName = Utils.getUniqueCostumeName(newCostumeName);
		} else {
			Utils.displayErrorMessage(getActivity(), getString(R.string.costumename_invalid));
			dismiss();
		}
		
		Intent intent = new Intent(ScriptTabActivity.ACTION_COSTUME_RENAMED);
		intent.putExtra(EXTRA_NEW_COSTUME_NAME, newCostumeName);
		getActivity().sendBroadcast(intent);
	}
	
	private void initAlertDialogListener() {
		final Button buttonPositive = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
		
		input.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0 || (s.length() == 1 && s.charAt(0) == '.')) {
					Toast.makeText(getActivity(), 
							R.string.notification_invalid_text_entered, Toast.LENGTH_SHORT).show();
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
