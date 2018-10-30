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

package org.catrobat.catroid.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.FlavoredConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;

import static org.catrobat.catroid.common.FlavoredConstants.EXTERNAL_STORAGE_ROOT_DIRECTORY;
import static org.catrobat.catroid.io.StorageOperations.copyDir;
import static org.catrobat.catroid.io.StorageOperations.deleteDir;
import static org.catrobat.catroid.io.StorageOperations.getUniqueFile;

public class CopyProjectsTask extends AsyncTask<String, Boolean, Boolean> {

	public static final String TAG = CopyProjectsTask.class.getSimpleName();
	private WeakReference<Activity> weakActivity;
	private WeakReference<ProgressDialog> progressDialogWeakReference;
	private boolean deleteFromExStorage;

	CopyProjectsTask(Activity mActivity, boolean deleteFromExternalStorage) {
		weakActivity = new WeakReference<>(mActivity);
		this.deleteFromExStorage = deleteFromExternalStorage;
	}

	private void copyAllProjects() throws IOException {
		if (!EXTERNAL_STORAGE_ROOT_DIRECTORY.exists()
				|| !EXTERNAL_STORAGE_ROOT_DIRECTORY.isDirectory()) {
			throw new FileNotFoundException("External storage dir does not exist.");
		}

		File tmpDir = new File(EXTERNAL_STORAGE_ROOT_DIRECTORY, "/tmp");
		if (tmpDir.exists()) {
			deleteDir(tmpDir);
		}

		for (File project : EXTERNAL_STORAGE_ROOT_DIRECTORY.listFiles()) {
			if (project.isDirectory()) {
				copyDir(project, getUniqueFile(project.getName(), FlavoredConstants.DEFAULT_ROOT_DIRECTORY));
			}
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (weakActivity.get() == null) {
			return;
		}
		String title = weakActivity.get().getString(R.string.please_wait);
		String message = weakActivity.get().getString(R.string.copying);
		progressDialogWeakReference = new WeakReference<>(ProgressDialog.show(weakActivity.get(), title, message));
	}

	@Override
	protected Boolean doInBackground(String... strings) {
		try {
			copyAllProjects();
			if (deleteFromExStorage) {
				deleteDir(EXTERNAL_STORAGE_ROOT_DIRECTORY);
			}
			return true;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);
		if (progressDialogWeakReference.get() != null && progressDialogWeakReference.get().isShowing()) {
			progressDialogWeakReference.get().dismiss();

			if (!success) {
				ToastUtil.showError(weakActivity.get(), R.string.error_during_copying_projects_toast);
			} else {
				if (deleteFromExStorage) {
					ToastUtil.showSuccess(weakActivity.get(), R.string.projects_successful_moved_toast);
				} else {
					ToastUtil.showSuccess(weakActivity.get(), R.string.projects_successful_copied_toast);
				}
			}
		}
	}
}
