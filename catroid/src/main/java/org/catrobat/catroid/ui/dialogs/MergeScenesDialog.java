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
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;

import java.util.List;

public class MergeScenesDialog extends DialogFragment {

	public static final String TAG = MergeScenesDialog.class.getSimpleName();

	private MergeScenesInterface mergeScenesInterface;

	private EditText input;
	private Spinner firstScene;
	private Spinner secondScene;

	private List<String> scenes;

	public MergeScenesDialog(MergeScenesInterface mergeScenesInterface) {
		this.mergeScenesInterface = mergeScenesInterface;
	}

	protected View inflateLayout() {
		final LayoutInflater inflater = getActivity().getLayoutInflater();
		return inflater.inflate(R.layout.dialog_merge_scenes, null);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		View view = inflateLayout();
		builder.setView(view);

		input = (EditText) view.findViewById(R.id.scene_merge_name_edittext);
		firstScene = (Spinner) view.findViewById(R.id.merge_scene_spinner_first);
		secondScene = (Spinner) view.findViewById(R.id.merge_scene_spinner_second);
		scenes = ProjectManager.getInstance().getCurrentProject().getSceneOrder();

		ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, scenes);

		firstScene.setAdapter(adapter);
		secondScene.setAdapter(adapter);
		firstScene.setSelection(0);
		secondScene.setSelection(1);

		builder.setPositiveButton(R.string.ok, null);
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				onCancel(dialog);
			}
		});

		final AlertDialog alertDialog = builder.create();
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				showKeyboard();
				Button buttonPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				buttonPositive.setEnabled(false);
				buttonPositive.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!hasError()) {
							handlePositiveButtonClick();
							dismiss();
						}
					}
				});
				input.addTextChangedListener(getInputTextWatcher(buttonPositive));
			}
		});

		return alertDialog;
	}

	protected boolean hasError() {
		String first = firstScene.getSelectedItem().toString();
		String second = secondScene.getSelectedItem().toString();
		String result = input.getText().toString();

		if (first.equals(second)) {
			input.setError(getString(R.string.error_merge_with_self_scene));
			return true;
		}

		if (scenes.contains(result)) {
			input.setError(getString(R.string.name_already_exists));
			return true;
		}
		return false;
	}

	protected TextWatcher getInputTextWatcher(final Button positiveButton) {
		return new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0) {
					positiveButton.setEnabled(false);
				} else {
					positiveButton.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};
	}

	protected void showKeyboard() {
		if (input.requestFocus()) {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	private void handlePositiveButtonClick() {
		String first = firstScene.getSelectedItem().toString();
		String second = secondScene.getSelectedItem().toString();
		String result = input.getText().toString();

		mergeScenesInterface.mergeScenes(first, second, result);
	}

	public interface MergeScenesInterface {
		void mergeScenes(String firstScene, String secondScene, String resultName);
	}
}
