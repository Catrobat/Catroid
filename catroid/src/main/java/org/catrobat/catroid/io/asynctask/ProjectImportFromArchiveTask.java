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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.io.ZipArchiver;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public class ProjectImportFromArchiveTask extends AsyncTask<InputStream, Boolean, Boolean> {

	private WeakReference<Context> weakContextReference;
	private WeakReference<ProjectImportListener> weakListenerReference;

	public ProjectImportFromArchiveTask(Context context, ProjectImportListener projectImportListener) {
		weakContextReference = new WeakReference<>(context);
		weakListenerReference = new WeakReference<>(projectImportListener);
	}

	private File getUniqueProjectDir(String projectName, File parentDir) {
		File projectDir = new File(parentDir, projectName);
		if (projectDir.exists()) {
			List<String> fileNames = new ArrayList<>();
			for (File dir : parentDir.listFiles()) {
				fileNames.add(dir.getName());
			}
			return new File(parentDir,
					new UniqueNameProvider().getUniqueName(projectDir.getName(), fileNames));
		}
		return projectDir;
	}

	private boolean deleteIfExists(File file) {
		if (file.exists()) {
			try {
				if (file.isDirectory()) {
					StorageOperations.deleteDir(file);
				} else {
					StorageOperations.deleteFile(file);
				}
			} catch (IOException deleteException) {
				Log.e(getClass().getSimpleName(), "Cannot delete "
						+ file.getAbsolutePath(), deleteException);
				return false;
			}
		}
		return true;
	}

	private boolean importProject(InputStream archive, Context context) {
		File unzippedProject = getUniqueProjectDir("unzippedProject", DEFAULT_ROOT_DIRECTORY);

		try {
			new ZipArchiver().unzip(archive, unzippedProject);
		} catch (IOException unzipException) {
			Log.e(getClass().getSimpleName(), "Cannot unzip project", unzipException);
			return false;
		}
		if (new File(unzippedProject, CODE_XML_FILE_NAME).exists()) {
			try {
				XstreamSerializer serializer = XstreamSerializer.getInstance();
				String projectName = serializer.loadProject(unzippedProject.getName(), context).getName();
				File correctlyNamedProject = new File(DEFAULT_ROOT_DIRECTORY, projectName);
				if (correctlyNamedProject.exists()) {
					correctlyNamedProject = getUniqueProjectDir(projectName, DEFAULT_ROOT_DIRECTORY);
				}

				try {
					StorageOperations.renameFile(unzippedProject, correctlyNamedProject);
					Project project = serializer.loadProject(correctlyNamedProject.getName(), context);
					project.setName(correctlyNamedProject.getName());
					serializer.saveProject(project);
				} catch (IOException e) {
					Log.e(getClass().getSimpleName(), "Cannot rename project: "
							+ correctlyNamedProject.getName() + ", deleting dir.", e);
					deleteIfExists(correctlyNamedProject);
					return false;
				}
				return true;
			} catch (IOException | LoadingProjectException e) {
				Log.e(getClass().getSimpleName(), "Cannot deserialize project via Xstream: "
						+ unzippedProject.getAbsolutePath(), e);
			}
		}
		return false;
	}

	@Override
	protected Boolean doInBackground(InputStream... inputStreams) {
		Context context = weakContextReference.get();
		if (context == null) {
			return false;
		}

		boolean success = true;
		for (InputStream projectStream : inputStreams) {
			success = success && importProject(projectStream, context);
		}
		return success;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		ProjectImportListener listener = weakListenerReference.get();
		if (listener != null) {
			listener.onImportFinished(success);
		}
	}

	public interface ProjectImportListener {

		void onImportFinished(boolean success);
	}
}
