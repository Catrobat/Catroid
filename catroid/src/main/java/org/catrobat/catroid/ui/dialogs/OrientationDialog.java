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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.Utils;

import java.io.IOException;

public class OrientationDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_orientation_project";

	private static final String TAG = OrientationDialog.class.getSimpleName();

	private Dialog orientationDialog;
	private String projectName;
	private RadioButton landscapeMode;
	private RadioButton cast;
	private boolean createEmptyProject;
	private boolean createLandscapeProject = false;
	private boolean createCastProject = false;

	private boolean openedFromProjectList = false;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_orientation_new_project, null);
		boolean castEnabled = SettingsActivity.isCastSharedPreferenceEnabled(getActivity());
		int title = castEnabled ? R.string.project_select_screen_title : R.string.project_orientation_title;

		orientationDialog = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(title)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		orientationDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				if (getActivity() == null) {
					Log.e(TAG, "onShow() Activity was null!");
					return;
				}
				Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						handleOkButtonClick();
					}
				});
			}
		});

		landscapeMode = (RadioButton) dialogView.findViewById(R.id.landscape_mode);
		cast = (RadioButton) dialogView.findViewById(R.id.cast);

		if (castEnabled) {
			cast.setVisibility(View.VISIBLE);
		}

		return orientationDialog;
	}

	protected void handleOkButtonClick() {

		createLandscapeProject = landscapeMode.isChecked();
		createCastProject = cast.isChecked();

		try {
			ProjectManager.getInstance().initializeNewProject(projectName, getActivity(), createEmptyProject, false, createLandscapeProject, createCastProject);
		} catch (IllegalArgumentException illegalArgumentException) {
			Utils.showErrorDialog(getActivity(), R.string.error_project_exists);
			return;
		} catch (IOException ioException) {
			Utils.showErrorDialog(getActivity(), R.string.error_new_project);
			Log.e(TAG, Log.getStackTraceString(ioException));
			dismiss();
			return;
		}

		Intent intent = new Intent(getActivity(), ProjectActivity.class);

		intent.putExtra(Constants.PROJECTNAME_TO_LOAD, projectName);

		if (isOpenedFromProjectList()) {
			intent.putExtra(Constants.PROJECT_OPENED_FROM_PROJECTS_LIST, true);
		}

		getActivity().startActivity(intent);

		dismiss();
	}

	public boolean isOpenedFromProjectList() {
		return openedFromProjectList;
	}

	public void setOpenedFromProjectList(boolean openedFromProjectList) {
		this.openedFromProjectList = openedFromProjectList;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setCreateEmptyProject(boolean isChecked) {
		this.createEmptyProject = isChecked;
	}
}
