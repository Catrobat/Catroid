/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.material.switchmaterial.SwitchMaterial;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.merge.NewProjectNameTextWatcher;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;

import androidx.fragment.app.DialogFragment;

import static org.catrobat.catroid.common.DefaultProjectHandler.ProjectCreatorType.PROJECT_CREATOR_CAST;
import static org.catrobat.catroid.common.DefaultProjectHandler.ProjectCreatorType.PROJECT_CREATOR_DEFAULT;

public class NewProjectDialogFragment extends DialogFragment {

	public static final String TAG = NewProjectDialogFragment.class.getSimpleName();

	private boolean exampleProject;
	private boolean castProject;
	private boolean landscape;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.dialog_new_project, null);

		final RadioGroup radioGroup = view.findViewById(R.id.radio_group);
		final SwitchMaterial exampleProjectSwitch = view.findViewById(R.id.example_project_switch);

		if (SettingsFragment.isCastSharedPreferenceEnabled(getActivity())) {
			view.findViewById(R.id.cast_radio_button).setVisibility(View.VISIBLE);
		}
		UniqueNameProvider uniqueNameProvider = new UniqueNameProvider() {
			@Override
			public boolean isUnique(String newName) {
				return (!ReplaceExistingProjectDialogFragment.projectExistsInDirectory(newName));
			}
		};
		TextInputDialog.Builder builder = new TextInputDialog.Builder(getContext())
				.setHint(getString(R.string.project_name_label))
				.setText(uniqueNameProvider.getUniqueName(getString(R.string.default_project_name), null))
				.setTextWatcher(new NewProjectNameTextWatcher<>())
				.setPositiveButton(getString(R.string.ok), (TextInputDialog.OnClickListener) (dialog, textInput) -> {
					exampleProject = exampleProjectSwitch.isChecked();

					switch (radioGroup.getCheckedRadioButtonId()) {
						case R.id.portrait_radio_button:
							landscape = false;
							break;
						case R.id.landscape_mode_radio_button:
							landscape = true;
							break;
						case R.id.cast_radio_button:
							castProject = true;
							break;
						default:
							throw new IllegalStateException(TAG + ": No radio button id match, check layout?");
					}

					createProject(textInput, landscape, exampleProject, castProject);
				});

		return builder
				.setView(view)
				.setNegativeButton(R.string.cancel, null)
				.create();
	}

	void createProject(String projectName, boolean landscape, boolean exampleProject,
			boolean castProject) {
		try {
			if (exampleProject) {
				if (castProject) {
					ProjectManager.getInstance()
							.createNewExampleProject(projectName, getContext(), PROJECT_CREATOR_CAST, false);
				} else {
					ProjectManager.getInstance()
							.createNewExampleProject(projectName, getContext(), PROJECT_CREATOR_DEFAULT, landscape);
				}
			} else {
				if (castProject) {
					ProjectManager.getInstance()
							.createNewEmptyProject(projectName, getContext(), false, true);
				} else {
					ProjectManager.getInstance()
							.createNewEmptyProject(projectName, getContext(), landscape, false);
				}
			}
			getActivity().startActivity(new Intent(getActivity(), ProjectActivity.class));
		} catch (IOException e) {
			ToastUtil.showError(getActivity(), R.string.error_new_project);
		}
	}
}
