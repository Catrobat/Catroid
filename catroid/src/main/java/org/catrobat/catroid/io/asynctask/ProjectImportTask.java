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
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public class ProjectImportTask extends AsyncTask<File, Boolean, Boolean> {

	private WeakReference<Context> weakContextReference;
	private WeakReference<ProjectImportListener> weakListenerReference;

	public ProjectImportTask(Context context, ProjectImportListener projectImportListener) {
		weakContextReference = new WeakReference<>(context);
		weakListenerReference = new WeakReference<>(projectImportListener);
	}

	private boolean importProject(File projectDir, Context context) {
		if (new File(projectDir, CODE_XML_FILE_NAME).exists()) {

			File dstDir = new File(DEFAULT_ROOT_DIRECTORY, projectDir.getName());

			List<String> fileNames = new ArrayList<>();
			for (File dir : DEFAULT_ROOT_DIRECTORY.listFiles()) {
				fileNames.add(dir.getName());
			}

			if (dstDir.exists()) {
				File newDstDir = new File(DEFAULT_ROOT_DIRECTORY,
						new UniqueNameProvider().getUniqueName(projectDir.getName(), fileNames));
				try {
					StorageOperations.copyDir(projectDir, newDstDir);
					ProjectManager.renameProject(dstDir.getName(), newDstDir.getName(), context);
					return true;
				} catch (IOException e) {
					Log.e(getClass().getSimpleName(), "Cannot rename project: "
							+ newDstDir.getName() + ", deleting dir.", e);
					if (newDstDir.exists()) {
						try {
							StorageOperations.deleteDir(newDstDir);
						} catch (IOException deleteException) {
							Log.e(getClass().getSimpleName(), "Cannot delete "
									+ newDstDir.getAbsolutePath(), deleteException);
						}
					}
					return false;
				}
			} else {
				try {
					StorageOperations.copyDir(projectDir, dstDir);
					return true;
				} catch (IOException e) {
					if (dstDir.exists()) {
						try {
							StorageOperations.deleteDir(dstDir);
						} catch (IOException deleteException) {
							Log.e(getClass().getSimpleName(), "Cannot delete "
									+ dstDir.getAbsolutePath(), deleteException);
						}
					}
					return false;
				}
			}
		}
		return false;
	}

	@Override
	protected Boolean doInBackground(File... files) {
		Context context = weakContextReference.get();
		if (context == null) {
			return false;
		}

		boolean success = true;
		for (File projectDir : files) {
			success = success && importProject(projectDir, context);
		}
		return success;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		ProjectImportListener listener = weakListenerReference.get();
		if (listener != null) {
			listener.onImportFinished(success);
		}
	}

	public interface ProjectImportListener {

		void onImportFinished(boolean success);
	}
}
