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

package org.catrobat.catroid.ui.filepicker;

import android.os.AsyncTask;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.common.Constants.BACKPACK_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.CATROBAT_EXTENSION;
import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.Constants.TMP_DIR_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.EXTERNAL_STORAGE_ROOT_DIRECTORY;

public class ListProjectFilesTask extends AsyncTask<File, Void, List<File>> {

	private WeakReference<OnListProjectFilesListener> weakListenerReference;

	public ListProjectFilesTask(OnListProjectFilesListener listener) {
		weakListenerReference = new WeakReference<>(listener);
	}

	public static List<File> task(File... startDir) {
		List<File> files = new ArrayList<>();
		for (File dir : startDir) {
			findProjectFiles(dir, files);
		}
		getAllProjectsFromPocketCodeFolder(files);
		return files;
	}

	private static void findProjectFiles(File dir, List<File> projectFiles) {
		// this check will prevent a future crash on android 11
		if (dir.canRead() && dir.listFiles() != null) {
			for (File file : dir.listFiles()) {
				if (file.isDirectory()) {
					findProjectFiles(file, projectFiles);
				}

				if (file.getName().endsWith(CATROBAT_EXTENSION)) {
					projectFiles.add(file);
				}
			}
		}
	}

	public static void getAllProjectsFromPocketCodeFolder(List<File> projectFiles) {
		if (EXTERNAL_STORAGE_ROOT_DIRECTORY.listFiles() == null) {
			return;
		}
		for (File dir : EXTERNAL_STORAGE_ROOT_DIRECTORY.listFiles()) {
			if (!dir.getName().equals(BACKPACK_DIRECTORY_NAME)
					&& !dir.getName().equals(TMP_DIR_NAME)
					&& dir.isDirectory()
					&& new File(dir, CODE_XML_FILE_NAME).exists()) {
				projectFiles.add(dir);
			}
		}
	}

	@Override
	protected List<File> doInBackground(File... startDir) {
		return task(startDir);
	}

	@Override
	protected void onPostExecute(List<File> files) {
		OnListProjectFilesListener listener = weakListenerReference.get();
		if (listener != null) {
			listener.onListProjectFilesComplete(files);
		}
	}

	public interface OnListProjectFilesListener {

		void onListProjectFilesComplete(List<File> files);
	}
}
