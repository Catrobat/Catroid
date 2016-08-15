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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.merge.MergeManager;
import org.catrobat.catroid.utils.Utils;

public class MergeSceneDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_merge_scene";

	private static final String TAG = MergeSceneDialog.class.getSimpleName();

	private EditText nameEditText;
	private Dialog mergeSceneDialog;
	private String firstSelectedScene;
	private String secondSelectedScene;

	public MergeSceneDialog() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_merge_scene, null);

		nameEditText = (EditText) dialogView.findViewById(R.id.scene_merge_name_edittext);
		Spinner spinnerFirstScene = (Spinner) dialogView.findViewById(R.id.merge_scene_spinner_first);
		Spinner spinnerSecondScene = (Spinner) dialogView.findViewById(R.id.merge_scene_spinner_second);

		nameEditText.setText("");
		ArrayAdapter<String> firstAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
				ProjectManager.getInstance().getCurrentProject().getSceneOrder());
		firstAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ArrayAdapter<String> secondAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
				ProjectManager.getInstance().getCurrentProject().getSceneOrder());
		secondAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerFirstScene.setAdapter(firstAdapter);
		spinnerSecondScene.setAdapter(secondAdapter);

		spinnerFirstScene.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				firstSelectedScene = (String) parent.getItemAtPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		spinnerSecondScene.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				secondSelectedScene = (String) parent.getItemAtPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		mergeSceneDialog = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		mergeSceneDialog.setCanceledOnTouchOutside(true);
		mergeSceneDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mergeSceneDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		mergeSceneDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				if (getActivity() == null) {
					Log.e(TAG, "onShow() Activity was null!");
					return;
				}

				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT);

				((AlertDialog) mergeSceneDialog).getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
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
							((AlertDialog) mergeSceneDialog).getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
						} else {
							((AlertDialog) mergeSceneDialog).getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
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
		return mergeSceneDialog;
	}

	protected void handleOkButtonClick() {
		if (ProjectManager.getInstance().getCurrentProject().getSceneOrder().contains(nameEditText.getText().toString().trim())) {
			Utils.showErrorDialog(getActivity(), R.string.error_scene_exists);
		} else if (MergeManager.mergeScene(firstSelectedScene, secondSelectedScene, nameEditText.getText().toString(),
				getActivity())) {
			dismiss();
		}
	}
}
