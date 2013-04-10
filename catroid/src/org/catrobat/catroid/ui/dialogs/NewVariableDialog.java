/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.dialogs;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.fragment.FormulaEditorVariableListFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class NewVariableDialog extends SherlockDialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_new_variable_catroid";

	public interface NewVariableDialogListener {
		void onFinishNewVariableDialog();
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		final View dialogView = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_formula_editor_variable_name, null);

		final Dialog dialogNewVariable = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(R.string.formula_editor_variable_dialog_title)
				.setNegativeButton(R.string.cancel_button, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).setPositiveButton(R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handleOkButton(dialogView);
					}
				}).create();

		dialogNewVariable.setCanceledOnTouchOutside(true);

		dialogNewVariable.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				handleOnShow(dialogNewVariable);
			}
		});

		return dialogNewVariable;
	}

	private void handleOkButton(View dialogView) {
		EditText variableNameEditText = (EditText) dialogView
				.findViewById(R.id.dialog_formula_editor_variable_name_edit_text);
		RadioButton localVariable = (RadioButton) dialogView
				.findViewById(R.id.dialog_formula_editor_variable_name_local_variable_radio_button);
		RadioButton globalVariable = (RadioButton) dialogView
				.findViewById(R.id.dialog_formula_editor_variable_name_global_variable_radio_button);

		NewVariableDialogListener newVariableDialogListener = (NewVariableDialogListener) getSherlockActivity()
				.getSupportFragmentManager().findFragmentByTag(FormulaEditorVariableListFragment.VARIABLE_TAG);

		String variableName = variableNameEditText.getText().toString();
		if (globalVariable.isChecked()) {
			if (ProjectManager.getInstance().getCurrentProject().getUserVariables()
					.getUserVariable(variableName, ProjectManager.getInstance().getCurrentSprite()) != null) {

				Toast.makeText(getActivity(), R.string.formula_editor_existing_user_variable, Toast.LENGTH_LONG).show();

			} else {
				ProjectManager.getInstance().getCurrentProject().getUserVariables()
						.addProjectUserVariable(variableName, 0.0);
				newVariableDialogListener.onFinishNewVariableDialog();
			}
		} else if (localVariable.isChecked()) {
			ProjectManager.getInstance().getCurrentProject().getUserVariables()
					.addSpriteUserVariable(variableName, 0.0);
			newVariableDialogListener.onFinishNewVariableDialog();
		}
	}

	private void handleOnShow(final Dialog dialogNewVariable) {
		final Button positiveButton = ((AlertDialog) dialogNewVariable).getButton(AlertDialog.BUTTON_POSITIVE);
		positiveButton.setEnabled(false);

		EditText dialogEditText = (EditText) dialogNewVariable
				.findViewById(R.id.dialog_formula_editor_variable_name_edit_text);

		InputMethodManager inputMethodManager = (InputMethodManager) getSherlockActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(dialogEditText, InputMethodManager.SHOW_IMPLICIT);

		dialogEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable editable) {

				String variableName = editable.toString();
				if (ProjectManager.getInstance().getCurrentProject().getUserVariables()
						.getUserVariable(variableName, ProjectManager.getInstance().getCurrentSprite()) != null) {

					Toast.makeText(getActivity(), R.string.formula_editor_existing_user_variable, Toast.LENGTH_SHORT)
							.show();

					positiveButton.setEnabled(false);
				} else {
					positiveButton.setEnabled(true);
				}

				if (editable.length() == 0) {
					positiveButton.setEnabled(false);
				}
			}
		});
	}
}
