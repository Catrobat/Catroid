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

import android.os.AsyncTask;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.XstreamSerializer;

import java.lang.ref.WeakReference;

public class ProjectSaveTask extends AsyncTask<Void, Void, Boolean> {

	private Project project;

	private WeakReference<ProjectSaveListener> weakListenerReference;

	public ProjectSaveTask(Project project) {
		this.project = project;
	}

	public ProjectSaveTask setListener(ProjectSaveListener listener) {
		weakListenerReference = new WeakReference<>(listener);
		return this;
	}

	public static boolean task(Project project) {
		return XstreamSerializer.getInstance().saveProject(project);
	}

	@Override
	protected Boolean doInBackground(Void... voids) {
		return task(project);
	}

	@Override
	protected void onPostExecute(Boolean success) {
		if (weakListenerReference == null) {
			return;
		}
		ProjectSaveListener listener = weakListenerReference.get();
		if (listener != null) {
			listener.onSaveProjectComplete(success);
		}
	}

	public interface ProjectSaveListener {

		void onSaveProjectComplete(boolean success);
	}
}
