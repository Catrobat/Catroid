/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.ui.fragment.UserBrickElementEditorFragment;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class UserBrickEditElementDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_new_text_catroid";

	private static String text;
	private static boolean editMode;
	private static int stringResourceOfTitle;
	private static int stringResourceOfHintText;
	private static ArrayList<String> takenVariables;
	private View fragmentView;
	private UserBrickElementEditorFragment userBrickElementEditorFragment;

	private static boolean isFunctionName;
	private static boolean isVariable;
	private static String originalFunctionName;

	public UserBrickEditElementDialog(View fragmentView) {
		super();
		this.fragmentView = fragmentView;
	}

	public interface DialogListener {
		void onFinishDialog(CharSequence text, boolean editMode);
	}

	private List<DialogListener> listenerList = new ArrayList<>();

	public static void setTitle(int stringResource) {
		stringResourceOfTitle = stringResource;
	}

	public static void setText(String name) {
		text = name;
	}

	public static void setFunctionName(boolean functionName) {
		isFunctionName = functionName;
		originalFunctionName = text;
	}

	public static void setIsVariable(boolean variable) {
		isVariable = variable;
	}

	public static void setHintText(int stringResource) {
		stringResourceOfHintText = stringResource;
	}

	public static void setTakenVariables(ArrayList<String> variables) {
		takenVariables = variables;
	}

	public static void setEditMode(boolean mode) {
		editMode = mode;
	}

	public void setUserBrickElementEditorFragment(UserBrickElementEditorFragment userBrickElementEditorFragment) {
		this.userBrickElementEditorFragment = userBrickElementEditorFragment;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		if (isNewVariable()) {
			UserScriptDefinitionBrick definitionBrick = ProjectManager.getInstance().getCurrentUserBrick().getDefinitionBrick();
			int numberOfElements = definitionBrick.getUserScriptDefinitionBrickElements().size();
			definitionBrick.removeDataAt(numberOfElements - 1, getActivity().getApplicationContext());
			userBrickElementEditorFragment.decreaseIndexOfCurrentlyEditedElement();
		}
		finishDialog(null);
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_brick_editor_edit_element,
				(ViewGroup) fragmentView, false);

		final EditText textField = (EditText) dialogView.findViewById(R.id.dialog_brick_editor_edit_element_edit_text);
		textField.setText(text);
		textField.setSelection(text.length());

		final Dialog dialogNewTextOrVariable = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(stringResourceOfTitle).setNegativeButton(R.string.cancel, new OnClickListener() {
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

		dialogNewTextOrVariable.setCanceledOnTouchOutside(true);

		dialogNewTextOrVariable.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				handleOnShow(dialogNewTextOrVariable, textField);
			}
		});

		return dialogNewTextOrVariable;
	}

	public void addDialogListener(DialogListener newVariableDialogListener) {
		listenerList.add(newVariableDialogListener);
	}

	private void finishDialog(String text) {
		for (DialogListener newVariableDialogListener : listenerList) {
			newVariableDialogListener.onFinishDialog(text, editMode);
		}
	}

	private void handleOkButton(View dialogView) {
		EditText elementEditText = (EditText) dialogView.findViewById(R.id.dialog_brick_editor_edit_element_edit_text);
		String elementText = elementEditText.getText().toString().trim();

		finishDialog(elementText);
	}

	private void handleOnShow(final Dialog dialogNewTextOrVariable, EditText textField) {
		final Button positiveButton = ((AlertDialog) dialogNewTextOrVariable).getButton(AlertDialog.BUTTON_POSITIVE);
		if (textField.getText().toString().isEmpty()) {
			positiveButton.setEnabled(false);
		}

		EditText dialogEditText = (EditText) dialogNewTextOrVariable
				.findViewById(R.id.dialog_brick_editor_edit_element_edit_text);

		dialogEditText.selectAll();
		dialogEditText.setHint(stringResourceOfHintText);

		InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
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
				String text = editable.toString().trim();
				positiveButton.setEnabled(true);

				if (text.length() == 0) {
					positiveButton.setEnabled(false);
				} else if (isFunctionName && !text.equals(originalFunctionName)
						&& ProjectManager.getInstance().getCurrentSprite().userBrickNameExists(text)) {
					ToastUtil.showError(getActivity(), R.string.user_brick_name_given);
					positiveButton.setEnabled(false);
				} else if (isVariable) {
					for (String takenName : takenVariables) {
						if (text.equals(takenName)) {
							positiveButton.setEnabled(false);
							ToastUtil.showError(getActivity(), R.string.formula_editor_existing_variable);
							break;
						}
					}
				}
			}
		});
	}

	private boolean isNewVariable() {
		return stringResourceOfTitle == R.string.add_variable;
	}
}
