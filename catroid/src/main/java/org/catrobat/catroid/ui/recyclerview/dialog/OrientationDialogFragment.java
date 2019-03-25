/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RadioGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;

public class OrientationDialogFragment extends DialogFragment {

	public static final String TAG = OrientationDialogFragment.class.getSimpleName();
	public static final String BUNDLE_KEY_PROJECT_NAME = "projectName";
	public static final String BUNDLE_KEY_CREATE_EMPTY_PROJECT = "createEmptyProject";

	public static OrientationDialogFragment newInstance(String projectName, boolean createEmptyProject) {
		OrientationDialogFragment dialog = new OrientationDialogFragment();

		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_KEY_PROJECT_NAME, projectName);
		bundle.putBoolean(BUNDLE_KEY_CREATE_EMPTY_PROJECT, createEmptyProject);
		dialog.setArguments(bundle);

		return dialog;
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
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (radioGroup.getCheckedRadioButtonId()) {
							case R.id.portrait:
								createProject(false, false);
								break;
							case R.id.landscape_mode:
								createProject(true, false);
								break;
							case R.id.cast:
								createProject(false, true);
								break;
							default:
								throw new IllegalStateException(TAG + ": No radio button id match, check layout?");
						}
					}
				})
				.setNegativeButton(R.string.cancel, null)
				.create();
	}

	void createProject(boolean createLandscapeProject, boolean createCastProject) {
		if (getArguments() == null) {
			return;
		}

		String projectName = getArguments().getString(BUNDLE_KEY_PROJECT_NAME);
		Boolean createEmptyProject = getArguments().getBoolean(BUNDLE_KEY_CREATE_EMPTY_PROJECT);

		if (projectName == null) {
			return;
		}

		try {
			ProjectManager.getInstance().initializeNewProject(projectName, getActivity(), createEmptyProject,
					false, createLandscapeProject, createCastProject, false);
		} catch (IOException e) {
			ToastUtil.showError(getActivity(), R.string.error_new_project);
		}

		Intent intent = new Intent(getActivity(), ProjectActivity.class);
		getActivity().startActivity(intent);
	}
}
