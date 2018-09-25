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
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.common.FlavoredConstants.OLD_EXTERNAL_STORAGE_ROOT_DIRECTORY;
import static org.catrobat.catroid.common.FlavoredConstants.POCKET_CODE_EXTERNAL_STORAGE_FOLDER_NAME;
import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG;
import static org.catrobat.catroid.io.StorageOperations.transferData;

public class ImportProjectsFromExternalStorage implements DialogInterface.OnClickListener {
	public static final String TAG = ImportProjectsFromExternalStorage.class.getSimpleName();
	private static final String POCKET_CODE_INTERNAL_STORAGE_FOLDER_NAME = "files";
	private static final String BACKPACK_FOLDER_NAME = "backpack";
	private Activity activity;

	public ImportProjectsFromExternalStorage(Activity mActivity) {
		activity = mActivity;
	}

	private static File copyFolderRecursive(File srcDir, File dstDir) throws IOException {
		UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();
		List<String> scope = new ArrayList<>();
		if (!srcDir.exists()) {
			throw new FileNotFoundException("Directory: " + srcDir.getAbsolutePath() + " does not exist.");
		}

		if (!srcDir.isDirectory()) {
			throw new IOException(srcDir.getAbsolutePath() + " is not a directory.");
		}
		if (!dstDir.exists()) {
			dstDir.mkdir();
		}

		if (!dstDir.isDirectory()) {
			throw new IOException("Cannot create directory: " + dstDir.getAbsolutePath());
		}

		for (File srcFile : srcDir.listFiles()) {
			String srcFileName = srcFile.getName();
			if (srcFile.isDirectory()) {
				boolean fileExists = directoryContainsFile(srcFile, dstDir, srcFileName, scope);

				if (fileExists) {
					String newFileName = uniqueNameProvider.getUniqueName(srcFileName, scope);
					copyFolderRecursive(srcFile, new File(dstDir, newFileName));
				} else {
					copyFolderRecursive(srcFile, new File(dstDir, srcFileName));
				}
			} else {
				transferData(srcFile, new File(dstDir, srcFileName));
			}
		}

		return dstDir;
	}

	private static boolean directoryContainsFile(File srcFile, File dstDir, String srcFileName, List<String> scope) {
		boolean fileExists = false;
		for (File dstFile : dstDir.listFiles()) {
			if (dstFile.getName().equals(srcFileName)
					&& dstFile.getParent().endsWith(POCKET_CODE_INTERNAL_STORAGE_FOLDER_NAME)
					&& srcFile.getParent().endsWith(POCKET_CODE_EXTERNAL_STORAGE_FOLDER_NAME)
					&& !srcFileName.equals(BACKPACK_FOLDER_NAME)) {
				fileExists = true;
				scope.add(srcFileName);
				break;
			}
		}
		return fileExists;
	}

	private static boolean copyProjects() {
		// delete tmp folders before start copying
		File tmpDir = new File(OLD_EXTERNAL_STORAGE_ROOT_DIRECTORY, "/tmp");

		if (tmpDir.exists()) {
			try {
				StorageOperations.deleteDir(tmpDir);
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		// copy projects to internal storage
		try {
			copyFolderRecursive(OLD_EXTERNAL_STORAGE_ROOT_DIRECTORY, FlavoredConstants.DEFAULT_ROOT_DIRECTORY);
			return true;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}

	private static boolean deleteOldExternalPocketCodeFolder() {
		try {
			StorageOperations.deleteDir(OLD_EXTERNAL_STORAGE_ROOT_DIRECTORY);
			return true;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}

	public void importOldPocketCodePrograms(Context context) {
		if (OLD_EXTERNAL_STORAGE_ROOT_DIRECTORY.exists()) {
			showImportProjectsDialog(activity);
		}
	}

	private AlertDialog.Builder createImportProjectsDialog(Activity activity) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
		alertDialog.setTitle(R.string.import_dialog_title)
				.setCancelable(false)
				.setMessage(R.string.import_dialog_message)
				.setPositiveButton(R.string.import_dialog_move_btn, this)
				.setNeutralButton(R.string.import_dialog_copy_btn, this);
		return alertDialog;
	}

	private void showImportProjectsDialog(Activity activity) {
		AlertDialog.Builder alertDialog = createImportProjectsDialog(activity);
		boolean showDialog = PreferenceManager
				.getDefaultSharedPreferences(activity).getBoolean(SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG, true);
		if (showDialog) {
			alertDialog.show();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				CopyProjectsTask moveProjectsTask = new CopyProjectsTask(activity, true);
				moveProjectsTask.execute();
				doNotShowDialogAgain();
				break;
			case DialogInterface.BUTTON_NEUTRAL:
				CopyProjectsTask copyProjectsTask = new CopyProjectsTask(activity, false);
				copyProjectsTask.execute();
				doNotShowDialogAgain();
				break;
		}
	}

	private void doNotShowDialogAgain() {
		PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG, false).apply();
	}

	private class CopyProjectsTask extends AsyncTask<String, Boolean, Boolean> {
		WeakReference<Activity> weakActivity;
		WeakReference<ProgressDialog> progressDialogWeakReference;
		private boolean deleteFromExStorage;
		private boolean copySuccessful;

		CopyProjectsTask(Activity mActivity, boolean deleteFromExternalStorage) {
			weakActivity = new WeakReference<Activity>(mActivity);
			this.deleteFromExStorage = deleteFromExternalStorage;
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
			copySuccessful = ImportProjectsFromExternalStorage.copyProjects();
			if (copySuccessful && deleteFromExStorage) {
				ImportProjectsFromExternalStorage.deleteOldExternalPocketCodeFolder();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			super.onPostExecute(aBoolean);
			if (progressDialogWeakReference.get() != null && progressDialogWeakReference.get().isShowing()) {
				progressDialogWeakReference.get().dismiss();

				if (!copySuccessful) {
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
}
