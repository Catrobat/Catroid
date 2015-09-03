/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import android.os.Bundle;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.utils.Utils;

public class RenameProjectDialog extends TextDialog {

	private static final String BUNDLE_ARGUMENTS_OLD_PROJECT_NAME = "old_project_name";
	public static final String DIALOG_FRAGMENT_TAG = "dialog_rename_project";

	private OnProjectRenameListener onProjectRenameListener;

	private String oldProjectName;

	public static RenameProjectDialog newInstance(String oldProjectName) {
		RenameProjectDialog dialog = new RenameProjectDialog();

		Bundle arguments = new Bundle();
		arguments.putString(BUNDLE_ARGUMENTS_OLD_PROJECT_NAME, oldProjectName);
		dialog.setArguments(arguments);

		return dialog;
	}

	public void setOnProjectRenameListener(OnProjectRenameListener listener) {
		onProjectRenameListener = listener;
	}

	@Override
	protected void initialize() {
		oldProjectName = getArguments().getString(BUNDLE_ARGUMENTS_OLD_PROJECT_NAME);
		input.setText(oldProjectName);
		inputTitle.setText(R.string.new_project_name);
	}

	@Override
	protected boolean handleOkButton() {
		String newProjectName = input.getText().toString().trim();

		if (newProjectName.equalsIgnoreCase("")) {
			Utils.showErrorDialog(getActivity(), R.string.notification_invalid_text_entered);
			return false;
		} else if (Utils.checkIfProjectExistsOrIsDownloadingIgnoreCase(newProjectName)
				&& !oldProjectName.equalsIgnoreCase(newProjectName)) {
			Utils.showErrorDialog(getActivity(), R.string.error_project_exists);
			return false;
		}

		if (newProjectName.equals(oldProjectName)) {
			dismiss();
			return false;
		}

		if (newProjectName != null && !newProjectName.equalsIgnoreCase("")) {
			ProjectManager projectManager = ProjectManager.getInstance();
			String currentProjectName = projectManager.getCurrentProject().getName();

			// check if is current project
			boolean isCurrentProject = false;

			if (oldProjectName.equalsIgnoreCase(currentProjectName)) {
				projectManager.renameProject(newProjectName, getActivity());

				isCurrentProject = true;
				Utils.saveToPreferences(getActivity(), Constants.PREF_PROJECTNAME_KEY, newProjectName);
			} else {
				try {
					projectManager.loadProject(oldProjectName, getActivity());
					projectManager.renameProject(newProjectName, getActivity());
					projectManager.loadProject(currentProjectName, getActivity());
				} catch (ProjectException projectException) {
					Log.e(DIALOG_FRAGMENT_TAG, "Renaming an incompatible project isn't possible", projectException);
					Utils.showErrorDialog(getActivity(), R.string.error_rename_incompatible_project);
					dismiss();
					return false;
				}
			}

			if (onProjectRenameListener != null) {
				onProjectRenameListener.onProjectRename(isCurrentProject);
			}
		} else {
			Utils.showErrorDialog(getActivity(), R.string.notification_invalid_text_entered);
			return false;
		}

		return true;
	}

	@Override
	protected String getTitle() {
		return getString(R.string.rename_project);
	}

	@Override
	protected String getHint() {
		return null;
	}

	public interface OnProjectRenameListener {

		void onProjectRename(boolean isCurrentProject);
	}
}
