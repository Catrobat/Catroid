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
import android.util.Pair;

import org.catrobat.catroid.R;
import org.catrobat.catroid.io.ZipArchiver;
import org.catrobat.catroid.utils.StatusBarNotificationManager;

import java.io.File;
import java.io.IOException;

import static org.catrobat.catroid.common.Constants.CATROBAT_EXTENSION;
import static org.catrobat.catroid.common.Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public class ProjectExportTask extends AsyncTask<Pair<String, Integer>, Void, Void> {

	@SafeVarargs
	public static void task(Pair<String, Integer>... programNames) {
		for (Pair<String, Integer> programName : programNames) {
			exportProjectToExternalStorage(programName.first, programName.second);
		}
	}

	private static void exportProjectToExternalStorage(String projectName, int notificationID) {
		File projectFile = new File(DEFAULT_ROOT_DIRECTORY, projectName);
		File externalProjectZip = new File(EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY, projectName + CATROBAT_EXTENSION);

		EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY.mkdirs();
		if (!EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY.isDirectory()) {
			return;
		}

		if (externalProjectZip.exists()) {
			externalProjectZip.delete();
		}

		ZipArchiver zipArchiver = new ZipArchiver();
		try {
			zipArchiver.zip(externalProjectZip, projectFile.listFiles());
			StatusBarNotificationManager.getInstance().showOrUpdateNotification(notificationID, 100);
		} catch (IOException e) {
			StatusBarNotificationManager.getInstance().abortProgressNotificationWithMessage(notificationID,
					R.string.save_project_to_external_storage_io_exception_message);
		}
	}

	@SafeVarargs
	@Override
	protected final Void doInBackground(Pair<String, Integer>... programNames) {
		task(programNames);
		return null;
	}
}
