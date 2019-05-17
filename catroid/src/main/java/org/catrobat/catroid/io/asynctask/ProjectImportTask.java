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

import org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.utils.FileMetaDataExtractor;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public class ProjectImportTask extends AsyncTask<File, Boolean, Boolean> {

	public static final String TAG = ProjectImportTask.class.getSimpleName();

	private WeakReference<ProjectImportListener> weakListenerReference;

	public ProjectImportTask setListener(ProjectImportListener listener) {
		this.weakListenerReference = new WeakReference<>(listener);
		return this;
	}

	public static boolean task(File... files) {
		boolean success = true;
		for (File projectDir : files) {
			success = success && importProject(projectDir);
		}
		return success;
	}

	private static boolean importProject(File projectDir) {
		File xmlFile = new File(projectDir, CODE_XML_FILE_NAME);
		if (!xmlFile.exists()) {
			Log.e(TAG, "No xml file found for project " + projectDir.getName());
			return false;
		}
		String projectName;
		try {
			projectName = new ProjectMetaDataParser(xmlFile).getProjectMetaData().getName();
		} catch (IOException e) {
			Log.d(TAG, "Cannot extract projectName from xml", e);
			return false;
		}

		if (projectName == null) {
			return false;
		}

		projectName = new UniqueNameProvider()
				.getUniqueName(projectName, FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY));

		File dstDir = new File(DEFAULT_ROOT_DIRECTORY,
				FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName));

		try {
			StorageOperations
					.copyDir(projectDir, dstDir);
			XstreamSerializer
					.renameProject(new File(dstDir, CODE_XML_FILE_NAME), projectName);
			return true;
		} catch (IOException e) {
			Log.e(TAG, "Something went wrong while importing project " + projectDir.getName(), e);
			if (dstDir.isDirectory()) {
				Log.e(TAG, "Folder exists, trying to delete folder.");
				try {
					StorageOperations.deleteDir(projectDir);
				} catch (IOException deleteException) {
					Log.e(TAG, "Cannot delete folder " + projectDir, deleteException);
				}
			}
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
		ProjectImportListener listener = weakListenerReference.get();
		if (listener != null) {
			listener.onImportFinished(success);
		}
	}

	public interface ProjectImportListener {

		void onImportFinished(boolean success);
	}
}
