/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import android.os.Bundle;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;

public class SetDescriptionDialog extends TextDialog {

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
			input.setText(projectManager.getCurrentProject().description);
		} else {
			projectManager.loadProject(projectToChangeName, getActivity(), false); //TODO: check something
			input.setText(projectManager.getCurrentProject().description);
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
		projectManager.getCurrentProject().description = description;
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
