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
import android.util.Log;

import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.ZipArchiver;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import static org.catrobat.catroid.common.Constants.CACHE_DIR;

public class ProjectUnzipAndImportTask extends AsyncTask<File, Boolean, Boolean> {

	public static final String TAG = ProjectUnzipAndImportTask.class.getSimpleName();

	private WeakReference<ProjectUnzipAndImportListener> weakListenerReference;

	public ProjectUnzipAndImportTask setListener(ProjectUnzipAndImportListener projectUnzipAndImportListener) {
		weakListenerReference = new WeakReference<>(projectUnzipAndImportListener);
		return this;
	}

	public static boolean task(File... files) {
		boolean success = true;
		for (File projectDir : files) {
			success = success && unzipAndImportProject(projectDir);
		}
		return success;
	}

	private static boolean unzipAndImportProject(File projectDir) {
		try {
			File cachedProjectDir = new File(CACHE_DIR, StorageOperations.getSanitizedFileName(projectDir.getName()));
			if (cachedProjectDir.isDirectory()) {
				StorageOperations.deleteDir(cachedProjectDir);
			}
			new ZipArchiver().unzip(projectDir, cachedProjectDir);
			return ProjectImportTask.task(cachedProjectDir);
		} catch (IOException e) {
			Log.e(TAG, "Cannot unzip project " + projectDir.getName(), e);
			return false;
		}
	}

	@Override
	protected Boolean doInBackground(File... files) {
		return task(files);
	}

	@Override
	protected void onPostExecute(Boolean success) {
		if (weakListenerReference == null) {
			return;
		}
		ProjectUnzipAndImportListener listener = weakListenerReference.get();
		if (listener != null) {
			listener.onImportFinished(success);
		}
	}

	public interface ProjectUnzipAndImportListener {

		void onImportFinished(boolean success);
	}
}
