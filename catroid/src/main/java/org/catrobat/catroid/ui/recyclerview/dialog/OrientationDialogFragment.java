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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import static org.catrobat.catroid.common.DefaultProjectHandler.ProjectCreatorType.PROJECT_CREATOR_CAST;
import static org.catrobat.catroid.common.DefaultProjectHandler.ProjectCreatorType.PROJECT_CREATOR_DEFAULT;

public class OrientationDialogFragment extends DialogFragment {

	public static final String TAG = OrientationDialogFragment.class.getSimpleName();
	public static final String BUNDLE_KEY_PROJECT_NAME = "projectName";
	public static final String BUNDLE_KEY_CREATE_EMPTY_PROJECT = "createEmptyProject";

	private String projectName;
	private boolean createEmptyProject;

	public static OrientationDialogFragment newInstance(String projectName, boolean createEmptyProject) {
		OrientationDialogFragment dialog = new OrientationDialogFragment();

		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_KEY_PROJECT_NAME, projectName);
		bundle.putBoolean(BUNDLE_KEY_CREATE_EMPTY_PROJECT, createEmptyProject);
		dialog.setArguments(bundle);

		return dialog;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() == null) {
			return;
		}

		projectName = getArguments().getString(BUNDLE_KEY_PROJECT_NAME);
		createEmptyProject = getArguments().getBoolean(BUNDLE_KEY_CREATE_EMPTY_PROJECT);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.dialog_orientation, null);

		final RadioGroup radioGroup = view.findViewById(R.id.radio_group);

		int title = R.string.project_orientation_title;

		if (SettingsFragment.isCastSharedPreferenceEnabled(getActivity())) {
			title = R.string.project_select_screen_title;
			view.findViewById(R.id.cast).setVisibility(View.VISIBLE);
		}

		return new AlertDialog.Builder(getContext())
				.setTitle(title)
				.setView(view)
				.setPositiveButton(R.string.ok, (dialog, which) -> {
					switch (radioGroup.getCheckedRadioButtonId()) {
						case R.id.portrait:
							createProject(false);
							break;
						case R.id.landscape_mode:
							createProject(true);
							break;
						case R.id.cast:
							createCastProject();
							break;
						default:
							throw new IllegalStateException(TAG + ": No radio button id match, check layout?");
					}
				})
				.setNegativeButton(R.string.cancel, null)
				.create();
	}

	void createProject(boolean landscape) {
		try {
			if (createEmptyProject) {
				ProjectManager.getInstance()
						.createNewEmptyProject(projectName, getContext(), landscape, false);
			} else {
				ProjectManager.getInstance()
						.createNewExampleProject(projectName, getContext(), PROJECT_CREATOR_DEFAULT, landscape);
			}
			getActivity().startActivity(new Intent(getActivity(), ProjectActivity.class));
		} catch (IOException e) {
			ToastUtil.showError(getActivity(), R.string.error_new_project);
		}
	}

	void createCastProject() {
		try {
			if (createEmptyProject) {
				ProjectManager.getInstance()
						.createNewEmptyProject(projectName, getContext(), false, true);
			} else {
				ProjectManager.getInstance()
						.createNewExampleProject(projectName, getContext(), PROJECT_CREATOR_CAST, false);
			}
			getActivity().startActivity(new Intent(getActivity(), ProjectActivity.class));
		} catch (IOException e) {
			ToastUtil.showError(getActivity(), R.string.error_new_project);
		}
	}
}
