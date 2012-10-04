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

import android.app.ProgressDialog;
import android.os.Bundle;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.fragment.ProjectsListFragment;
import at.tugraz.ist.catroid.utils.CopyProjectTask;
import at.tugraz.ist.catroid.utils.Utils;

public class CopyProjectDialog extends TextDialog {

	private static final String BUNDLE_ARGUMENTS_OLD_PROJECT_NAME = "old_project_name";
	public static final String DIALOG_FRAGMENT_TAG = "dialog_copy_project";

	private String oldProjectName;
	private ProjectsListFragment parentFragment;
	ProgressDialog progressDialog;

	public CopyProjectDialog() {
	}

	public static CopyProjectDialog newInstance(String oldProjectName) {
		CopyProjectDialog dialog = new CopyProjectDialog();
		Bundle arguments = new Bundle();
		arguments.putString(BUNDLE_ARGUMENTS_OLD_PROJECT_NAME, oldProjectName);
		dialog.setArguments(arguments);
		return dialog;
	}

	public void setParentFragment(ProjectsListFragment parentFragment) {
		this.parentFragment = parentFragment;
	}

	@Override
	protected void initialize() {
		oldProjectName = getArguments().getString(BUNDLE_ARGUMENTS_OLD_PROJECT_NAME);
		input.setText(oldProjectName);
	}

	@Override
	protected boolean handleOkButton() {
		String newProjectName = (input.getText().toString()).trim();

		if (newProjectName.equalsIgnoreCase("")) {
			Utils.displayErrorMessageFragment(getActivity().getSupportFragmentManager(),
					getString(R.string.notification_invalid_text_entered));
			return false;
		} else if (StorageHandler.getInstance().projectExistsIgnoreCase(newProjectName)) {
			Utils.displayErrorMessageFragment(getActivity().getSupportFragmentManager(),
					getString(R.string.error_project_exists));
			return false;
		}

		if (newProjectName != null && !newProjectName.equalsIgnoreCase("")) {

			new CopyProjectTask(parentFragment).execute(newProjectName, oldProjectName);
			this.dismiss();

		} else {
			Utils.displayErrorMessageFragment(getActivity().getSupportFragmentManager(),
					getString(R.string.notification_invalid_text_entered));
			return false;
		}
		return false;
	}

	@Override
	protected String getHint() {
		return null;
	}

	public interface OnCopyProjectListener {

		public void onCopyProject(boolean orientationChangedWhileCopying);

	}

	@Override
	protected String getTitle() {
		return null;
	}
}
