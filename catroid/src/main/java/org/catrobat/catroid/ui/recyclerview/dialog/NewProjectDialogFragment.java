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
package org.catrobat.catroid.ui.recyclerview.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.drone.ardrone.DroneServiceWrapper;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoServiceWrapper;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DialogInputWatcher;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.IOException;

public class NewProjectDialogFragment extends DialogFragment {

	public static final String TAG = NewProjectDialogFragment.class.getSimpleName();

	private TextInputLayout inputLayout;
	private RadioGroup radioGroup;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.dialog_new_project, null);

		inputLayout = view.findViewById(R.id.input);
		inputLayout.setHint(getActivity().getString(R.string.project_name_label));

		radioGroup = view.findViewById(R.id.radio_group);

		if (DroneServiceWrapper.isDroneSharedPreferenceEnabled()) {
			view.findViewById(R.id.project_default_drone_radio_button).setVisibility(View.VISIBLE);
		}

		if (JumpingSumoServiceWrapper.isJumpingSumoSharedPreferenceEnabled()) {
			view.findViewById(R.id.project_default_jumping_sumo_radio_button).setVisibility(View.VISIBLE);
		}

		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.new_project_dialog_title)
				.setView(view)
				.setPositiveButton(R.string.ok, null)
				.setNegativeButton(R.string.cancel, null)
				.create();

		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button buttonPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				buttonPositive.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (onPositiveButtonClick()) {
							dismiss();
						}
					}
				});
				buttonPositive.setEnabled(!inputLayout.getEditText().getText().toString().isEmpty());
				DialogInputWatcher inputWatcher = new DialogInputWatcher(inputLayout, buttonPositive, false);
				inputLayout.getEditText().addTextChangedListener(inputWatcher);
			}
		});
		inputLayout.getEditText().setOnFocusChangeListener(new OpenSoftkeyboardRightAway(alertDialog));
		return alertDialog;
	}

	private boolean onPositiveButtonClick() {
		String name = inputLayout.getEditText().getText().toString().trim();

		if (name.isEmpty()) {
			inputLayout.setError(getString(R.string.name_consists_of_spaces_only));
			return false;
		}

		if (Utils.checkIfProjectExistsOrIsDownloadingIgnoreCase(name)) {
			inputLayout.setError(getString(R.string.name_already_exists));
			return false;
		} else {
			switch (radioGroup.getCheckedRadioButtonId()) {
				case R.id.project_empty_radio_button:
					showOrientationDialog(name, true);
					break;
				case R.id.project_default_radio_button:
					showOrientationDialog(name, false);
					break;
				case R.id.project_default_drone_radio_button:
					createDroneProject(name, false);
					break;
				case R.id.project_default_jumping_sumo_radio_button:
					createDroneProject(name, true);
					break;
				default:
					throw new IllegalStateException(TAG + ": Cannot find RadioButton.");
			}
			return true;
		}
	}

	private void showOrientationDialog(String name, boolean createEmptyProject) {
		OrientationDialogFragment dialog = new OrientationDialogFragment(name, createEmptyProject);
		dialog.show(getFragmentManager(), OrientationDialogFragment.TAG);
	}

	private void createDroneProject(String name, boolean jumpingSumo) {
		try {
			ProjectManager.getInstance()
					.initializeNewProject(name, getActivity(), false, true, false, false, jumpingSumo);
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			getActivity().startActivity(intent);
		} catch (IOException e) {
			ToastUtil.showError(getActivity(), R.string.error_new_project);
		}
	}
}
