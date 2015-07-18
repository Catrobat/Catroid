/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

public class NewStringDialog extends SherlockDialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = NewStringDialog.class.getSimpleName();
	private EditText newStringEditText;

	public NewStringDialog() {
	}

	public static NewStringDialog newInstance() {
		return new NewStringDialog();
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		final ViewGroup root = (ViewGroup) getSherlockActivity().getSupportFragmentManager().findFragmentByTag(
				FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG).getView().getRootView();
		final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_formulaeditor_string, root, false);

		Dialog newStringDialog = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(R.string.formula_editor_new_string_name)
				.setNegativeButton(R.string.cancel_button, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).setPositiveButton(R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handleOkButton();
					}
				}).create();
		newStringEditText = (EditText) dialogView.findViewById(R.id.formula_editor_string_name_edit_text);
		newStringDialog.setCanceledOnTouchOutside(true);
		newStringDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		newStringDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(newStringEditText, InputMethodManager.SHOW_IMPLICIT);
			}
		});
		return newStringDialog;
	}

	private void handleOkButton() {
		String stringName = newStringEditText.getText().toString();

		FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getSherlockActivity().getSupportFragmentManager()
				.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		if (formulaEditor != null) {
			formulaEditor.addStringToActiveFormula(stringName);
			formulaEditor.updateButtonsOnKeyboardAndInvalidateOptionsMenu();
		}
	}
}
