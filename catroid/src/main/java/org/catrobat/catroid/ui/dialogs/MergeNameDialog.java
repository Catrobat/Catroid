/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.catrobat.catroid.R;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.merge.MergeTask;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

public class MergeNameDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_merge_name";

	private static final String TAG = MergeNameDialog.class.getSimpleName();

	private MergeTask task;

	private EditText nameEditText;
	private Dialog newProjectDialog;

	public MergeNameDialog(MergeTask task) {
		this.task = task;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_merge_name, null);

		nameEditText = (EditText) dialogView.findViewById(R.id.project_name_edittext);

		nameEditText.setText("");

		newProjectDialog = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(R.string.new_project_dialog_title)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		newProjectDialog.setCanceledOnTouchOutside(true);
		newProjectDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		newProjectDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		newProjectDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				if (getActivity() == null) {
					Log.e(TAG, "onShow() Activity was null!");
					return;
				}

				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT);

				((AlertDialog) newProjectDialog).getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
				nameEditText.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						if (nameEditText.length() == 0) {
							((AlertDialog) newProjectDialog).getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
						} else {
							((AlertDialog) newProjectDialog).getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
						}
					}
				});

				Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						handleOkButtonClick();
					}
				});
			}
		});
		return newProjectDialog;
	}

	protected void handleOkButtonClick() {
		String mergeName = nameEditText.getText().toString();
		if (StorageHandler.getInstance().projectExists(mergeName)) {
			Utils.showErrorDialog(getActivity(), R.string.error_project_exists);
		} else {
			if (task.mergeProjects(mergeName)) {
				String msg = task.getFirstProject().getName() + " " + getActivity().getString(R.string.merge_info) + " "
						+ "" + 	task.getSecondProject().getName() + "!";
				ToastUtil.showSuccess(getActivity(), msg);
			} else {
				ToastUtil.showError(getActivity(), R.string.error_merge);
			}
			this.dismiss();
		}
	}
}
