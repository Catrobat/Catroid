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

import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.utils.FileMetaDataExtractor;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;

public class ProjectRenameTask extends AsyncTask<Void, Void, Boolean> {

	public static final String TAG = ProjectRenameTask.class.getSimpleName();

	private File projectDir;
	private String dstName;

	private WeakReference<ProjectRenameListener> weakListenerReference;

	public ProjectRenameTask(File projectDir, String dstName) {
		this.projectDir = projectDir;
		this.dstName = dstName;
	}

	public ProjectRenameTask setListener(ProjectRenameListener listener) {
		weakListenerReference = new WeakReference<>(listener);
		return this;
	}

	public static File task(File projectDir, String dstName) throws IOException {
		File dstDir = new File(projectDir.getParent(), FileMetaDataExtractor.encodeSpecialCharsForFileSystem(dstName));

		if (projectDir.renameTo(dstDir)
				&& XstreamSerializer.renameProject(new File(dstDir, CODE_XML_FILE_NAME), dstName)) {
			return dstDir;
		} else {
			throw new IOException("Cannot rename project directory " + projectDir.getAbsolutePath() + " to " + dstName);
		}
	}

	@Override
	protected Boolean doInBackground(Void... voids) {
		try {
			task(projectDir, dstName);
			return true;
		} catch (IOException e) {
			Log.e(TAG, "Creating renamed directory failed!", e);
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean success) {
		if (weakListenerReference == null) {
			return;
		}
		ProjectRenameListener listener = weakListenerReference.get();
		if (listener != null) {
			listener.onRenameFinished(success);
		}
	}

	public interface ProjectRenameListener {

		void onRenameFinished(boolean success);
	}
}
