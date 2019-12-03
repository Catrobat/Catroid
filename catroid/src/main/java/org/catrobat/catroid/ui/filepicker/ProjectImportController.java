/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

package org.catrobat.catroid.ui.filepicker;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.asynctask.ProjectImportTask;
import org.catrobat.catroid.io.asynctask.ProjectUnzipAndImportTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.catrobat.catroid.common.Constants.CACHE_DIR;

public class ProjectImportController {

	private static final String CATROBAT_FILE_SUFFIX = ".catrobat";
	private static final String ZIP_FILE_SUFFIX = ".zip";
	public static final String TAG = ProjectImportController.class.getSimpleName();

	private ContentResolver contentResolver;

	private ArrayList<File> filesForUnzipAndImportTask;
	boolean hasUnzipAndImportTaskFinished;
	private ArrayList<File> filesForImportTask;
	boolean hasImportTaskFinished;

	private ProjectImportFinishedListener projectImportInterface;

	private ProjectUnzipAndImportTask.ProjectUnzipAndImportListener projectUnzipAndImportListener =
			new ProjectUnzipAndImportTask.ProjectUnzipAndImportListener() {
				@Override
				public void onImportFinished(boolean success) {
					hasUnzipAndImportTaskFinished = true;

					cleanUpCacheDirectory();

					if (hasImportTaskFinished && hasUnzipAndImportTaskFinished) {
						projectImportInterface.notifyActivityFinished(success);
					}
				}
			};

	private ProjectImportTask.ProjectImportListener projectImportListener =
			new ProjectImportTask.ProjectImportListener() {
				@Override
				public void onImportFinished(boolean success) {
					hasImportTaskFinished = true;

					if (hasImportTaskFinished && hasUnzipAndImportTaskFinished) {
						projectImportInterface.notifyActivityFinished(success);
					}
				}
			};

	public ProjectImportController(ContentResolver contentResolver,
			ProjectImportFinishedListener finishedListener) {
		this.projectImportInterface = finishedListener;
		this.contentResolver = contentResolver;
		filesForImportTask = new ArrayList<>();
		filesForUnzipAndImportTask = new ArrayList<>();

		hasUnzipAndImportTaskFinished = true;
		hasImportTaskFinished = true;
	}

	public void startImportOfProject(Uri uriToImport) {
		ArrayList<Uri> urisToImport = new ArrayList<>();
		urisToImport.add(uriToImport);
		startImportOfProjects(urisToImport);
	}

	public void startImportOfProjects(ArrayList<Uri> urisToImport) {
		try {
			prepareFilesForImport(urisToImport);

			if (!filesForImportTask.isEmpty()) {
				File[] filesToImport =
						filesForImportTask.toArray(new File[filesForImportTask.size()]);
				new ProjectImportTask()
						.setListener(projectImportListener)
						.execute(filesToImport);
			}

			if (!filesForUnzipAndImportTask.isEmpty()) {
				File[] filesToUnzipAndImport =
						filesForUnzipAndImportTask.toArray(new File[filesForUnzipAndImportTask.size()]);
				new ProjectUnzipAndImportTask()
						.setListener(projectUnzipAndImportListener)
						.execute(filesToUnzipAndImport);
			}
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
	}

	private void prepareFilesForImport(ArrayList<Uri> urisToImport) throws IOException {
		for (Uri uri : urisToImport) {
			if (!uri.getScheme().equals("file")) {
				throw new IllegalArgumentException("importProject has to be called with a file "
						+ "uri. (not a content uri");
			}

			File src = new File(uri.getPath());
			if (src.isDirectory()) {
				filesForImportTask.add(src);
				hasImportTaskFinished = false;
			} else {
				String fileName = uri.getLastPathSegment();
				fileName = fileName.replace(CATROBAT_FILE_SUFFIX, ZIP_FILE_SUFFIX);
				File projectFile = StorageOperations.copyUriToDir(contentResolver, uri,
						CACHE_DIR, fileName);
				filesForUnzipAndImportTask.add(projectFile);
				hasUnzipAndImportTaskFinished = false;
			}
		}
	}

	private void cleanUpCacheDirectory() {
		for (File cachedFile : filesForUnzipAndImportTask) {
			if (cachedFile.exists()) {
				cachedFile.delete();
			}
		}
	}

	public interface ProjectImportFinishedListener {
		void notifyActivityFinished(boolean success);
	}
}
