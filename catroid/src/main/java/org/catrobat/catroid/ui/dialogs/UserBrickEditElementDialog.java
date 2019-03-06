/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import org.catrobat.catroid.R;

import java.util.ArrayList;
import java.util.List;

public class UserBrickEditElementDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_new_text_catroid";

	private static CharSequence text;
	private static boolean editMode;
	private static int stringResourceOfTitle;
	private static int stringResourceOfHintText;

	public UserBrickEditElementDialog() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean isRestoringPreviouslyDestroyedActivity = savedInstanceState != null;
		if (isRestoringPreviouslyDestroyedActivity) {
			dismiss();
		}
	}

	public interface DialogListener {
		void onFinishDialog(CharSequence text, boolean editMode);
	}

	private List<DialogListener> listenerList = new ArrayList<UserBrickEditElementDialog.DialogListener>();

	public static void setTitle(int stringResource) {
		stringResourceOfTitle = stringResource;
	}

	public static void setText(CharSequence sequence) {
		text = sequence;
	}

	public static void setHintText(int stringResource) {
		stringResourceOfHintText = stringResource;
	}

	public static void setEditMode(boolean mode) {
		editMode = mode;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		finishDialog(null);
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		final View dialogView = View.inflate(getActivity(), R.layout.dialog_brick_editor_edit_element, null);

		EditText textField = (EditText) dialogView.findViewById(R.id.dialog_brick_editor_edit_element_edit_text);
		textField.setText(text);
		textField.setSelection(text.length());

		final Dialog dialogNewVariable = new AlertDialog.Builder(getActivity()).setView(dialogView)
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

		dialogNewVariable.setCanceledOnTouchOutside(true);

		dialogNewVariable.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				handleOnShow(dialogNewVariable);
			}
		});

		return dialogNewVariable;
	}

	public void addDialogListener(DialogListener newVariableDialogListener) {
		listenerList.add(newVariableDialogListener);
	}

	private void finishDialog(CharSequence text) {
		for (DialogListener newVariableDialogListener : listenerList) {
			newVariableDialogListener.onFinishDialog(text, editMode);
		}
	}

	private void handleOkButton(View dialogView) {
		EditText elementTextEditText = (EditText) dialogView
				.findViewById(R.id.dialog_brick_editor_edit_element_edit_text);

		CharSequence elementText = elementTextEditText.getText();
		finishDialog(elementText);
	}

	private void handleOnShow(final Dialog dialogNewVariable) {
		EditText dialogEditText = (EditText) dialogNewVariable
				.findViewById(R.id.dialog_brick_editor_edit_element_edit_text);

		dialogEditText.selectAll();
		dialogEditText.setHint(stringResourceOfHintText);
	}
}
