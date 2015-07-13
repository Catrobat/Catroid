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
package org.catrobat.catroid.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.fragment.ProjectsListFragment;

import java.io.File;
import java.io.IOException;

public class CopyProjectTask extends AsyncTask<String, Long, Boolean> {

	private ProjectsListFragment parentFragment;
	private String newName;

	public CopyProjectTask(ProjectsListFragment parentActivity) {
		this.parentFragment = parentActivity;
	}

	@Override
	protected Boolean doInBackground(String... projectNameArray) {
		String newProjectName = projectNameArray[0];
		newName = newProjectName;
		int notificationId = StatusBarNotificationManager.getInstance().createCopyNotification(
				parentFragment.getActivity(), newProjectName);
		String oldProjectName = projectNameArray[1];

		try {
			File oldProjectRootDirectory = new File(Utils.buildProjectPath(oldProjectName));
			File newProjectRootDirectory = new File(Utils.buildProjectPath(newProjectName));

			copyDirectory(newProjectRootDirectory, oldProjectRootDirectory);

			Project copiedProject = StorageHandler.getInstance().loadProject(newProjectName);
			copiedProject.setName(newProjectName);
			StorageHandler.getInstance().saveProject(copiedProject);
		} catch (IOException exception) {
			UtilFile.deleteDirectory(new File(Utils.buildProjectPath(newProjectName)));
			Log.e("CATROID", "Error while copying project, destroy newly created directories.", exception);
			StatusBarNotificationManager.getInstance().cancelNotification(notificationId);

			return false;
		}
		StatusBarNotificationManager.getInstance().showOrUpdateNotification(notificationId, 100);
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		//quickfix: if fragment is not attached an instrumentation fault occurs
		//return if fragment is detached
		if (!parentFragment.isAdded()) {
			return;
			//parentFragment.onAttach(parentActivity);
		}

		if (!result) {
			Utils.showErrorDialog(parentFragment.getActivity(), R.string.error_copy_project);
			return;
		}

		ToastUtil.showSuccess(parentFragment.getActivity(),
				parentFragment.getString(R.string.project_name) + " " + newName + " "
						+ parentFragment.getString(R.string.copy_project_finished));
		parentFragment.onCopyProject();
	}

	private void copyDirectory(File destinationFile, File sourceFile) throws IOException {
		if (sourceFile.isDirectory()) {

			destinationFile.mkdirs();
			for (String subDirectoryName : sourceFile.list()) {
				copyDirectory(new File(destinationFile, subDirectoryName), new File(sourceFile, subDirectoryName));
			}
		} else {
			UtilFile.copyFile(destinationFile, sourceFile);
		}
	}
}
