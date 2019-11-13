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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.DeviceListAccessor;
import org.catrobat.catroid.io.DeviceVariableAccessor;

import java.io.File;
import java.lang.ref.WeakReference;

public class ProjectLoadTask extends AsyncTask<Void, Void, Boolean> {

	public static final String TAG = ProjectLoadTask.class.getSimpleName();

	private File projectDir;

	private WeakReference<Context> weakContextReference;
	private WeakReference<ProjectLoadListener> weakListenerReference;

	public ProjectLoadTask(Context context, ProjectLoadListener listener) {
		weakContextReference = new WeakReference<>(context);
		weakListenerReference = new WeakReference<>(listener);
	}

	public ProjectLoadTask(File projectDir, Context context) {
		this.projectDir = projectDir;
		weakContextReference = new WeakReference<>(context);
	}

	public ProjectLoadTask setListener(ProjectLoadListener listener) {
		weakListenerReference = new WeakReference<>(listener);
		return this;
	}

	public static boolean task(File projectDir, Context context) {
		try {
			ProjectManager.getInstance().loadProject(projectDir, context);
			Project project = ProjectManager.getInstance().getCurrentProject();
			new DeviceVariableAccessor(projectDir).cleanUpDeletedUserData(project);
			new DeviceListAccessor(projectDir).cleanUpDeletedUserData(project);
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Cannot load project in " + projectDir.getAbsolutePath(), e);
			return false;
		}
	}

	@Override
	protected Boolean doInBackground(Void... voids) {
		Context context = weakContextReference.get();
		if (context == null) {
			return false;
		}
		return task(projectDir, context);
	}

	@Override
	protected void onPostExecute(Boolean success) {
		if (weakListenerReference == null) {
			return;
		}
		ProjectLoadListener listener = weakListenerReference.get();
		if (listener != null) {
			listener.onLoadFinished(success);
		}
	}

	public interface ProjectLoadListener {

		void onLoadFinished(boolean success);
	}
}
