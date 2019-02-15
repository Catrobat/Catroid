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

import java.lang.ref.WeakReference;

public class ProjectRenameTask extends AsyncTask<String, Void, Boolean> {

	private WeakReference<Context> weakContextReference;
	private WeakReference<ProjectRenameListener> weakListenerReference;

	public ProjectRenameTask(Context context, ProjectRenameListener listener) {
		weakContextReference = new WeakReference<>(context);
		weakListenerReference = new WeakReference<>(listener);
	}

	@Override
	protected Boolean doInBackground(String... strings) {
		Context context = weakContextReference.get();
		if (context == null) {
			return false;
		}
		try {
			ProjectManager.renameProject(strings[0], strings[1], context);
			return true;
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "Cannot rename project " + strings[0] + " to " + strings[1], e);
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean success) {
		ProjectRenameListener listener = weakListenerReference.get();
		if (listener != null) {
			listener.onRenameFinished(success);
		}
	}

	public interface ProjectRenameListener {

		void onRenameFinished(boolean success);
	}
}
