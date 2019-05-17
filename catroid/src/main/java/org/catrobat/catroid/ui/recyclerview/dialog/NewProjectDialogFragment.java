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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.RadioGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoServiceWrapper;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.IOException;

import static org.catrobat.catroid.common.DefaultProjectHandler.ProjectCreatorType.PROJECT_CREATOR_DRONE;
import static org.catrobat.catroid.common.DefaultProjectHandler.ProjectCreatorType.PROJECT_CREATOR_JUMPING_SUMO;

public class NewProjectDialogFragment extends DialogFragment {

	public static final String TAG = NewProjectDialogFragment.class.getSimpleName();

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.dialog_new_project, null);

		final RadioGroup radioGroup = view.findViewById(R.id.radio_group);

		if (SettingsFragment.isDroneSharedPreferenceEnabled(getContext())) {
			view.findViewById(R.id.project_default_drone_radio_button).setVisibility(View.VISIBLE);
		}

		if (JumpingSumoServiceWrapper.isJumpingSumoSharedPreferenceEnabled()) {
			view.findViewById(R.id.project_default_jumping_sumo_radio_button).setVisibility(View.VISIBLE);
		}

		final TextInputDialog.TextWatcher textWatcher = new TextInputDialog.TextWatcher() {

			@Nullable
			@Override
			public String validateInput(String input, Context context) {
				String error = null;

				if (input.isEmpty()) {
					return context.getString(R.string.name_empty);
				}

				input = input.trim();

				if (input.isEmpty()) {
					error = context.getString(R.string.name_consists_of_spaces_only);
				} else if (Utils.checkIfProjectExistsOrIsDownloadingIgnoreCase(input)) {
					error = context.getString(R.string.name_already_exists);
				}

				return error;
			}
		};

		TextInputDialog.Builder builder = new TextInputDialog.Builder(getContext())
				.setHint(getString(R.string.project_name_label))
				.setTextWatcher(textWatcher)
				.setPositiveButton(getString(R.string.ok), (TextInputDialog.OnClickListener) (dialog, textInput) -> {
					switch (radioGroup.getCheckedRadioButtonId()) {
						case R.id.project_empty_radio_button:
							showOrientationDialog(textInput, true);
							break;
						case R.id.project_default_radio_button:
							showOrientationDialog(textInput, false);
							break;
						case R.id.project_default_drone_radio_button:
							createARDroneProject(textInput);
							break;
						case R.id.project_default_jumping_sumo_radio_button:
							createJumpingSumoProject(textInput);
							break;
						default:
							throw new IllegalStateException(TAG + ": No radio button id match, check layout?");
					}
				});

		return builder
				.setTitle(R.string.new_project_dialog_title)
				.setView(view)
				.setNegativeButton(R.string.cancel, null)
				.create();
	}

	void showOrientationDialog(String projectName, boolean createEmptyProject) {
		OrientationDialogFragment
				.newInstance(projectName, createEmptyProject)
				.show(getFragmentManager(), OrientationDialogFragment.TAG);
	}

	void createARDroneProject(String name) {
		try {
			ProjectManager.getInstance()
					.createNewExampleProject(name, getContext(), PROJECT_CREATOR_DRONE, false);
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			startActivity(intent);
		} catch (IOException e) {
			ToastUtil.showError(getActivity(), R.string.error_new_project);
		}
	}

	void createJumpingSumoProject(String name) {
		try {
			ProjectManager.getInstance()
					.createNewExampleProject(name, getContext(), PROJECT_CREATOR_JUMPING_SUMO, false);
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			startActivity(intent);
		} catch (IOException e) {
			ToastUtil.showError(getActivity(), R.string.error_new_project);
		}
	}
}
