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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.drone.DroneServiceWrapper;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

public class NewProjectDialog extends TextDialog {

	public static final String TAG = NewProjectDialog.class.getSimpleName();
	public static final String DIALOG_FRAGMENT_TAG = "dialog_new_project";

	private LoadNewProjectInterface loadNewProjectInterface;
	private RadioButton emptyProjectButton;
	private RadioButton defaultDroneProjectButton;

	public NewProjectDialog(String defaultProjectName, LoadNewProjectInterface loadNewProjectInterface) {
		super(R.string.new_project_dialog_title, R.string.new_project_name, "", true, defaultProjectName);
		this.loadNewProjectInterface = loadNewProjectInterface;
	}

	@Override
	protected View inflateLayout() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_new_project, null);

		emptyProjectButton = (RadioButton) dialogView.findViewById(R.id.project_empty_radio_button);
		defaultDroneProjectButton = (RadioButton) dialogView.findViewById(R.id.project_default_drone_radio_button);

		if (DroneServiceWrapper.isDroneSharedPreferenceEnabled()) {
			defaultDroneProjectButton.setVisibility(View.VISIBLE);
		}

		return dialogView;
	}

	@Override
	protected boolean handlePositiveButtonClick() {
		String newName = input.getText().toString().trim();

		if (newName.isEmpty()) {
			newName = hint;
		}

		if (Utils.checkIfProjectExistsOrIsDownloadingIgnoreCase(newName)) {
			input.setError(getString(R.string.error_project_exists));
			return false;
		}

		if (defaultDroneProjectButton.isChecked()) {
			try {
				ProjectManager.getInstance().initializeNewProject(newName, getActivity(), false, true, true);
				loadNewProjectInterface.loadNewProject(newName);
			} catch (Exception e) {
				Log.e(TAG, "Could not create Program");
				ToastUtil.showError(getActivity(), R.string.error_new_project);
			}
			return true;
		}

		OrientationDialog dialog = new OrientationDialog(newName, emptyProjectButton.isChecked(), loadNewProjectInterface);
		dialog.show(getFragmentManager(), OrientationDialog.DIALOG_FRAGMENT_TAG);
		return true;
	}

	@Override
	protected void handleNegativeButtonClick() {
	}

	public interface LoadNewProjectInterface {

		void loadNewProject(String projectName);
	}
}
