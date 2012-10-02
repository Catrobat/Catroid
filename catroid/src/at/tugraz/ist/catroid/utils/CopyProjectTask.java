/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.utils;

import java.io.File;
import java.io.IOException;

import android.os.AsyncTask;
import android.util.Log;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.io.StorageHandler;

public class CopyProjectTask extends AsyncTask<String, Void, Boolean> {

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(String... projectNameArray) {
		String newProjectName = projectNameArray[0];
		String oldProjectName = projectNameArray[1];
		if (isCancelled()) {
			return false;
		}

		try {
			//TODO: OldProjectName
			File oldProjectRootDirectory = new File(Utils.buildProjectPath(oldProjectName));
			File newProjectRootDirectory = new File(Utils.buildProjectPath(newProjectName));

			copyDirectory(newProjectRootDirectory, oldProjectRootDirectory);
			if (isCancelled()) {
				return false;
			}

			Project copiedProject = StorageHandler.getInstance().loadProject(newProjectName);
			copiedProject.setName(newProjectName);
			StorageHandler.getInstance().saveProject(copiedProject);

		} catch (IOException exception) {
			UtilFile.deleteDirectory(new File(Utils.buildProjectPath(newProjectName)));
			Log.e("CATROID", "Error while copying project, destroy newly created directories.", exception);
			return false;
		}

		return true;
	}

	private void copyDirectory(File destinationFile, File sourceFile) throws IOException {
		if (isCancelled()) {
			throw new IOException();
		}
		if (sourceFile.isDirectory()) {

			destinationFile.mkdirs();
			for (String subDirectoryName : sourceFile.list()) {
				copyDirectory(new File(destinationFile, subDirectoryName), new File(sourceFile, subDirectoryName));
			}
		} else {
			UtilFile.copyFile(destinationFile, sourceFile, null);
		}
	}
}
