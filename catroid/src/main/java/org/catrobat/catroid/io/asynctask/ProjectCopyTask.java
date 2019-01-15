/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.io.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public class ProjectCopyTask extends AsyncTask<String, Void, Boolean> {

	public static final String TAG = ProjectCopyTask.class.getSimpleName();

	private WeakReference<Context> weakContextReference;
	private WeakReference<ProjectCopyListener> weakListenerReference;

	public ProjectCopyTask(Context context, ProjectCopyListener listener) {
		weakContextReference = new WeakReference<>(context);
		weakListenerReference = new WeakReference<>(listener);
	}

	@Override
	protected Boolean doInBackground(String... strings) {
		Context context = weakContextReference.get();
		if (context == null) {
			return false;
		}

		File projectToCopyDir = new File(DEFAULT_ROOT_DIRECTORY, strings[0]);
		File projectDir = new File(DEFAULT_ROOT_DIRECTORY, strings[1]);

		try {
			StorageOperations.copyDir(projectToCopyDir, projectDir);
			Project project = XstreamSerializer.getInstance().loadProject(strings[1], context);
			project.setName(strings[1]);
			XstreamSerializer.getInstance().saveProject(project);
			return true;
		} catch (IOException | LoadingProjectException e) {
			Log.e(TAG, "Something went wrong while copying project: " + strings[0]
					+ " trying to delete folder."
					+ Log.getStackTraceString(e));
			try {
				StorageOperations.deleteDir(projectDir);
			} catch (IOException deleteException) {
				Log.e(TAG, "Cannot delete folder: " + projectDir, deleteException);
			}
			Log.e(TAG, "Deleted folder, returning ..");
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean success) {
		ProjectCopyListener listener = weakListenerReference.get();
		if (listener != null) {
			listener.onCopyFinished(success);
		}
	}

	public interface ProjectCopyListener {

		void onCopyFinished(boolean success);
	}
}
