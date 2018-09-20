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
import android.content.DialogInterface;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG;
import static org.catrobat.catroid.io.StorageOperations.transferData;

public class ImportProjectsFromExternalStorage implements DialogInterface.OnClickListener {
	public static final String TAG = ImportProjectsFromExternalStorage.class.getSimpleName();

	private final File POCKET_CODE_ROOT_DIRECTORY = new File(
			CatroidApplication.getAppContext().getFilesDir().getAbsolutePath() + "/Programs");
	private final File EXTERNAL_STORAGE_ROOT_DIRECTORY = new File(
			Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pocket Code");
	private Activity mActivity;

	public ImportProjectsFromExternalStorage(Activity activity) {
		mActivity = activity;
	}

	private static File copyProjectsFromExternalToInternalStorage(File srcDir, File dstDir) throws IOException {
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
				boolean fileExists = false;
				for (File dstFile : dstDir.listFiles()) {
					if (dstFile.getName().equals(srcFileName) && dstFile.getParent().endsWith("Programs") && srcFile.getParent().endsWith("Pocket Code") && !srcFileName.equals("backpack")) {
						fileExists = true;
						scope.add(srcFileName);
						break;
					}
				}
				if (fileExists) {
					String newFileName = uniqueNameProvider.getUniqueName(srcFileName, scope);
					copyProjectsFromExternalToInternalStorage(srcFile, new File(dstDir, newFileName));
				} else {
					copyProjectsFromExternalToInternalStorage(srcFile, new File(dstDir, srcFileName));
				}
			} else {
				transferData(srcFile, new File(dstDir, srcFileName));
			}
		}

		return dstDir;
	}

	public void importOldPocketCodePrograms() {
		if (FlavoredConstants.DEFAULT_ROOT_DIRECTORY.equals(POCKET_CODE_ROOT_DIRECTORY) && pocketCodeExternalStorageDirectoryExists()) {
			showImportProjectsDialog(mActivity);
		}
	}

	private boolean pocketCodeExternalStorageDirectoryExists() {
		return EXTERNAL_STORAGE_ROOT_DIRECTORY.exists();
	}

	private void showImportProjectsDialog(Activity activity) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
		alertDialog.setTitle(R.string.import_dialog_title)
				.setCancelable(false)
				.setMessage(R.string.import_dialog_message)
				.setPositiveButton(R.string.ok, this)
				.setNeutralButton(R.string.import_dialog_copy_only, this);

		boolean showDialog = PreferenceManager
				.getDefaultSharedPreferences(mActivity).getBoolean(SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG, true);
		if (showDialog) {
			alertDialog.show();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				copyProjects();
				try {
					StorageOperations.deleteDir(EXTERNAL_STORAGE_ROOT_DIRECTORY);
				} catch (IOException e) {
					e.printStackTrace();
				}
				doNotShowDialogAgain();
				break;
			case DialogInterface.BUTTON_NEUTRAL:
				copyProjects();
				doNotShowDialogAgain();
				break;
		}
	}

	private void copyProjects() {
		// delete tmp folders before start copying
		File tmpDir = new File(EXTERNAL_STORAGE_ROOT_DIRECTORY, "/tmp");
		if (tmpDir.exists()) {
			try {
				StorageOperations.deleteDir(tmpDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// copy projects to internal storage
		try {
			copyProjectsFromExternalToInternalStorage(EXTERNAL_STORAGE_ROOT_DIRECTORY, FlavoredConstants.DEFAULT_ROOT_DIRECTORY);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doNotShowDialogAgain() {
		PreferenceManager.getDefaultSharedPreferences(mActivity).edit().putBoolean(SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG, false).apply();
	}
}
