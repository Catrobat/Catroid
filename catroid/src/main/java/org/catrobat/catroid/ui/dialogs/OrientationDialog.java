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
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.dialogs.NewProjectDialog.LoadNewProjectInterface;
import org.catrobat.catroid.utils.ToastUtil;

public class OrientationDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_orientation_project";

	private static final String TAG = OrientationDialog.class.getSimpleName();

	private String newName;
	private boolean createEmptyProject;
	private LoadNewProjectInterface loadNewProjectInterface;

	public OrientationDialog(String newName, boolean createEmptyProject, LoadNewProjectInterface loadNewProjectInterface) {
		this.newName = newName;
		this.createEmptyProject = createEmptyProject;
		this.loadNewProjectInterface = loadNewProjectInterface;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_orientation_new_project, null);

		RadioButton landscapeButton = (RadioButton) dialogView.findViewById(R.id.landscape_mode);
		final boolean createLandscapeProject = landscapeButton.isChecked();

		builder.setView(dialogView);
		builder.setTitle(R.string.project_orientation_title);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				createNewProject(createEmptyProject, createLandscapeProject);
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		return builder.create();
	}

	private void createNewProject(boolean createEmptyProject, boolean createLandscapeProject) {
		try {
			ProjectManager.getInstance().initializeNewProject(newName, getActivity(), createEmptyProject, false,
					createLandscapeProject);
			loadNewProjectInterface.loadNewProject(newName);
		} catch (Exception e) {
			Log.e(TAG, "Could not create Program");
			ToastUtil.showError(getActivity(), R.string.error_new_project);
		}
	}
}
