/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.dialogs;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;

import android.os.Bundle;

public class SetDescriptionDialog extends MultiLineTextDialog {

	private static final String BUNDLE_ARGUMENTS_OLD_PROJECT_DESCRIPTION = "old_project_description";
	public static final String DIALOG_FRAGMENT_TAG = "dialog_set_description";

	private OnUpdateProjectDescriptionListener onUpdateProjectDescriptionListener;

	private ProjectManager projectManager;
	private String projectToChangeName;

	public static SetDescriptionDialog newInstance(String projectToChangeName) {
		SetDescriptionDialog dialog = new SetDescriptionDialog();

		Bundle arguments = new Bundle();
		arguments.putString(BUNDLE_ARGUMENTS_OLD_PROJECT_DESCRIPTION, projectToChangeName);
		dialog.setArguments(arguments);

		return dialog;
	}

	public void setOnUpdateProjectDescriptionListener(OnUpdateProjectDescriptionListener listener) {
		onUpdateProjectDescriptionListener = listener;
	}

	@Override
	protected void initialize() {
		projectManager = ProjectManager.getInstance();
		projectToChangeName = getArguments().getString(BUNDLE_ARGUMENTS_OLD_PROJECT_DESCRIPTION);
		String currentProjectName = projectManager.getCurrentProject().getName();

		if (projectToChangeName.equalsIgnoreCase(currentProjectName)) {
			input.setText(projectManager.getCurrentProject().getDescription());
		} else {

			projectManager.loadProject(projectToChangeName, getActivity(), false); //TODO: check something
			input.setText(projectManager.getCurrentProject().getDescription());
			projectManager.loadProject(currentProjectName, getActivity(), false);

		}
	}

	@Override
	protected boolean handleOkButton() {
		String description = (input.getText().toString());
		String currentProjectName = projectManager.getCurrentProject().getName();

		if (projectToChangeName.equalsIgnoreCase(currentProjectName)) {
			setDescription(description);
			updateProjectDescriptionListener();
			dismiss();
			return false;
		}

		projectManager.loadProject(projectToChangeName, getActivity(), false);
		setDescription(description);
		projectManager.loadProject(currentProjectName, getActivity(), false);

		updateProjectDescriptionListener();
		dismiss();
		return true;
	}

	@Override
	protected String getTitle() {
		return getString(R.string.description);
	}

	@Override
	protected String getHint() {
		return null;
	}

	private void setDescription(String description) {
		projectManager.getCurrentProject().setDescription(description);
		projectManager.saveProject();
	}

	private void updateProjectDescriptionListener() {
		if (onUpdateProjectDescriptionListener != null) {
			onUpdateProjectDescriptionListener.onUpdateProjectDescription();
		}
	}

	public interface OnUpdateProjectDescriptionListener {

		public void onUpdateProjectDescription();

	}
}
