/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.utils.FileMetaDataExtractor;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;

public class ProjectCopyTask extends AsyncTask<Void, Void, Boolean> {

	public static final String TAG = ProjectCopyTask.class.getSimpleName();

	private File projectDir;
	private String destinationName;

	private WeakReference<ProjectCopyListener> weakListenerReference;

	public ProjectCopyTask(File projectDir, String destinationName) {
		this.projectDir = projectDir;
		this.destinationName = destinationName;
	}

	public ProjectCopyTask setListener(ProjectCopyListener listener) {
		weakListenerReference = new WeakReference<>(listener);
		return this;
	}

	public static boolean task(File sourceDir, String destinationName) {
		File destinationDir = new File(sourceDir.getParentFile(), FileMetaDataExtractor.encodeSpecialCharsForFileSystem(destinationName));

		try {
			StorageOperations.copyDir(sourceDir, destinationDir);
			XstreamSerializer.renameProject(new File(destinationDir, CODE_XML_FILE_NAME), destinationName);
			ProjectManager.getInstance().addNewDownloadedProject(destinationName);
			return true;
		} catch (IOException e) {
			Log.e(TAG, "Something went wrong while copying " + sourceDir.getAbsolutePath() + " to " + destinationName, e);
			if (destinationDir.isDirectory()) {
				Log.e(TAG, "Folder exists, trying to delete folder.");
				try {
					StorageOperations.deleteDir(destinationDir);
				} catch (IOException deleteException) {
					Log.e(TAG, "Cannot delete folder " + destinationName, deleteException);
				}
			}
			return false;
		}
	}

	@Override
	protected Boolean doInBackground(Void... voids) {
		return task(projectDir, destinationName);
	}

	@Override
	protected void onPostExecute(Boolean success) {
		if (weakListenerReference == null) {
			return;
		}
		ProjectCopyListener listener = weakListenerReference.get();
		if (listener != null) {
			listener.onCopyFinished(success);
		}
	}

	public interface ProjectCopyListener {

		void onCopyFinished(boolean success);
	}
}
