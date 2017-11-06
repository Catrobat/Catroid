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

package org.catrobat.catroid.ui.recyclerview.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.utils.Utils;

import java.io.IOException;

public class ProjectCopyTask extends AsyncTask<String, Void, Boolean> {

	public static final String TAG = ProjectCopyTask.class.getSimpleName();

	private Context context;
	private ProjectCopyListener listener;

	public ProjectCopyTask(Context context, ProjectCopyListener listener) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String projectToCopyPath = Utils.buildProjectPath(params[0]);
		String projectPath = Utils.buildProjectPath(params[1]);

		try {
			StorageHandler.copyDir(projectToCopyPath, projectPath);
			Project project = StorageHandler.getInstance().loadProject(params[1], context);
			project.setName(params[1]);
			StorageHandler.getInstance().saveProject(project);
			return true;
		} catch (IOException | LoadingProjectException loadingException) {
			Log.e(TAG, "Something went wrong while copying project: " + params[0]
					+ " trying to delete folder."
					+ Log.getStackTraceString(loadingException));
			try {
				StorageHandler.deleteDir(projectPath);
			} catch (IOException deletionIOException) {
				Log.e(TAG, "Could not delete folder:" + Log.getStackTraceString(deletionIOException));
			}
			Log.e(TAG, "Deleted folder, returning ..");
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean success) {
		listener.onCopyFinished(success);
	}

	public interface ProjectCopyListener {

		void onCopyFinished(boolean success);
	}
}
