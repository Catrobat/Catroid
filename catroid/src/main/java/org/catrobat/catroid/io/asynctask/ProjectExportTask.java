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

import org.catrobat.catroid.R;
import org.catrobat.catroid.io.ZipArchiver;
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager;

import java.io.File;
import java.io.IOException;

import static org.catrobat.catroid.common.Constants.CATROBAT_EXTENSION;
import static org.catrobat.catroid.common.Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY;

public class ProjectExportTask extends AsyncTask<Void, Void, Void> {

	private static final String TAG = ProjectExportTask.class.getSimpleName();

	private final StatusBarNotificationManager statusBarNotificationManager;
	private File projectDir;
	private int notificationId;

	public ProjectExportTask(StatusBarNotificationManager statusBarNotificationManager, File projectDir, int notificationId) {
		this.statusBarNotificationManager = statusBarNotificationManager;
		this.projectDir = projectDir;
		this.notificationId = notificationId;
	}

	private void task(File projectDir, int notificationId) {
		exportProjectToExternalStorage(projectDir, notificationId);
	}

	private void exportProjectToExternalStorage(File projectDir, int notificationId) {
		File projectZip = new File(EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY, projectDir.getName() + CATROBAT_EXTENSION);

		EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY.mkdirs();
		if (!EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY.isDirectory()) {
			return;
		}

		if (projectZip.exists()) {
			projectZip.delete();
		}

		try {
			new ZipArchiver()
					.zip(projectZip, projectDir.listFiles());
			statusBarNotificationManager.showOrUpdateNotification(notificationId, 100);
		} catch (IOException e) {
			Log.e(TAG, "Cannot create archive.", e);
			StatusBarNotificationManager.getInstance().abortProgressNotificationWithMessage(notificationId,
					R.string.save_project_to_external_storage_io_exception_message);
		}
	}

	@Override
	protected final Void doInBackground(Void... voids) {
		task(projectDir, notificationId);
		return null;
	}
}
