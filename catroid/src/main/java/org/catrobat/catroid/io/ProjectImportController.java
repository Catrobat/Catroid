/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.io;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import org.catrobat.catroid.io.asynctask.ProjectImportTask;
import org.catrobat.catroid.io.asynctask.ProjectUnzipAndImportTask;

import java.io.File;
import java.io.IOException;

import static org.catrobat.catroid.common.Constants.CACHED_PROJECT_ZIP_FILE_NAME;
import static org.catrobat.catroid.common.Constants.CACHE_DIR;

public class ProjectImportController {
	public static final String TAG = ProjectImportController.class.getSimpleName();

	private ContentResolver contentResolver;

	private ProjectImportFinishedListener projectImportFinishedListener;

	private ProjectUnzipAndImportTask.ProjectUnzipAndImportListener projectUnzipAndImportListener =
			new ProjectUnzipAndImportTask.ProjectUnzipAndImportListener() {
				@Override
				public void onImportFinished(boolean success) {
					projectImportFinishedListener.notifyImportFinished(success);
				}
			};

	private ProjectImportTask.ProjectImportListener projectImportListener =
			new ProjectImportTask.ProjectImportListener() {
				@Override
				public void onImportFinished(boolean success) {
					projectImportFinishedListener.notifyImportFinished(success);
				}
			};

	public ProjectImportController(ContentResolver contentResolver,
			ProjectImportFinishedListener finishedListener) {
		this.projectImportFinishedListener = finishedListener;
		this.contentResolver = contentResolver;
	}

	public void startImportOfProject(Uri uriToImport) {
		try {
			startImportFromUri(uriToImport);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
			projectImportFinishedListener.notifyImportFinished(false);
		}
	}

	private void startImportFromUri(Uri uriToImport) throws IOException {
		if (uriToImport == null) {
			throw new IllegalArgumentException("Cannot resolve project to import.");
		}

		if (!uriToImport.getScheme().equals("file")) {
			throw new IllegalArgumentException("importProject has to be called with a file uri. (not a content uri");
		}

		File cacheFile = new File(CACHE_DIR, CACHED_PROJECT_ZIP_FILE_NAME);
		if (cacheFile.exists()) {
			cacheFile.delete();
		}

		File src = new File(uriToImport.getPath());
		if (src.isDirectory()) {
			new ProjectImportTask().setListener(projectImportListener).execute(src);
			return;
		}

		File projectFile = StorageOperations.copyUriToDir(contentResolver, uriToImport, CACHE_DIR,
							CACHED_PROJECT_ZIP_FILE_NAME);
		new ProjectUnzipAndImportTask().setListener(projectUnzipAndImportListener).execute(projectFile);
	}

	public interface ProjectImportFinishedListener {
		void notifyImportFinished(boolean success);
	}
}
