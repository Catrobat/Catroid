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

import java.io.IOException;

import android.content.Intent;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class NewProjectDialog extends TextDialog {

	@Override
	protected void initialize() {
	}

	@Override
	protected boolean handleOkButton() {
		String projectName = (input.getText().toString().trim());

		if (projectName.length() == 0) {
			Utils.displayErrorMessage(getActivity(), getString(R.string.error_no_name_entered));
			return false;
		}

		if (StorageHandler.getInstance().projectExistsIgnoreCase(projectName)) {
			Utils.displayErrorMessage(getActivity(), getString(R.string.error_project_exists));
			return false;
		}

		try {
			ProjectManager.getInstance().initializeNewProject(projectName, getActivity());
		} catch (IOException e) {
			Utils.displayErrorMessage(getActivity(), getString(R.string.error_new_project));
			dismiss();
		}

		Utils.saveToPreferences(getActivity(), Constants.PREF_PROJECTNAME_KEY, projectName);
		Intent intent = new Intent(getActivity(), ProjectActivity.class);
		getActivity().startActivity(intent);

		return true;
	}

	@Override
	protected String getTitle() {
		return getString(R.string.new_project_dialog_title);
	}

	@Override
	protected String getHint() {
		return getString(R.string.new_project_dialog_hint);
	}
}
