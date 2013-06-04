/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid;

import java.io.File;
import java.io.IOException;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.utils.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ProjectManager {

	private Project project;
	private Script currentScript;
	private Sprite currentSprite;
	public static final ProjectManager INSTANCE = new ProjectManager();

	private FileChecksumContainer fileChecksumContainer;

	private ProjectManager() {
		fileChecksumContainer = new FileChecksumContainer();
	}

	public static ProjectManager getInstance() {
		return INSTANCE;
	}

	public boolean loadProject(String projectName, Context context, boolean errorMessage) {
		fileChecksumContainer = new FileChecksumContainer();
		Project oldProject = project;
		MessageContainer.createBackup();
		project = StorageHandler.getInstance().loadProject(projectName);

		if (project == null) {
			if (oldProject != null) {
				project = oldProject;
				MessageContainer.restoreBackup();
			} else {
				project = Utils.findValidProject();
				if (project == null) {
					try {
						project = StandardProjectHandler.createAndSaveStandardProject(context);
						MessageContainer.clearBackup();
					} catch (IOException e) {
						if (errorMessage) {
							Utils.showErrorDialog(context, context.getString(R.string.error_load_project));
						}
						Log.e("CATROID", "Cannot load project.", e);
						return false;
					}
				}
			}
			if (errorMessage) {
				Utils.showErrorDialog(context, context.getString(R.string.error_load_project));
			}
			return false;
		} else if (!Utils.isApplicationDebuggable(context)
				&& (project.getCatrobatLanguageVersion() > Constants.SUPPORTED_CATROBAT_LANGUAGE_VERSION)) {
			project = oldProject;
			if (errorMessage) {
				Utils.showErrorDialog(context, context.getString(R.string.error_project_compatability));
				// TODO show dialog to download latest catroid version instead
			}
			return false;
		} else if (!Utils.isApplicationDebuggable(context)
				&& (project.getCatrobatLanguageVersion() < Constants.SUPPORTED_CATROBAT_LANGUAGE_VERSION)) {
			project = oldProject;
			if (errorMessage) {
				Utils.showErrorDialog(context, context.getString(R.string.error_project_compatability));
				// TODO show dialog to convert project to a supported version
			}
			return false;
		} else {
			// Set generic localized name on background sprite and move it to the back.
			if (project.getSpriteList().size() > 0) {
				project.getSpriteList().get(0).setName(context.getString(R.string.background));
				project.getSpriteList().get(0).look.setZIndex(0);
			}
			MessageContainer.clearBackup();
			currentSprite = null;
			currentScript = null;
			Utils.saveToPreferences(context, Constants.PREF_PROJECTNAME_KEY, project.getName());
			return true;
		}
	}

	public boolean canLoadProject(String projectName) {
		Project project = StorageHandler.getInstance().loadProject(projectName);
		if (project == null) {
			return false;
		} else {
			return true;
		}
	}

	public boolean saveProject() {
		if (project == null) {
			return false;
		}

		project.removeUnusedBroadcastMessages();

		SaveProjectAsynchronousTask saveTask = new SaveProjectAsynchronousTask();
		saveTask.execute();
		return true;
	}

	public boolean initializeDefaultProject(Context context) {
		try {
			fileChecksumContainer = new FileChecksumContainer();
			project = StandardProjectHandler.createAndSaveStandardProject(context);
			currentSprite = null;
			currentScript = null;
			return true;
		} catch (Exception e) {
			Log.e("CATROID", "Cannot initialize default project.", e);
			Utils.showErrorDialog(context, context.getString(R.string.error_load_project));
			return false;
		}
	}

	public void initializeNewProject(String projectName, Context context) throws IOException {
		fileChecksumContainer = new FileChecksumContainer();
		project = StandardProjectHandler.createAndSaveStandardProject(projectName, context);

		currentSprite = null;
		currentScript = null;
		saveProject();
	}

	public Project getCurrentProject() {
		return project;
	}

	public void setProject(Project project) {
		currentScript = null;
		currentSprite = null;

		this.project = project;
	}

	public void deleteCurrentProject() {
		StorageHandler.getInstance().deleteProject(project);
		project = null;
	}

	public boolean renameProject(String newProjectName, Context context) {
		if (StorageHandler.getInstance().projectExistsCheckCase(newProjectName)) {
			Utils.showErrorDialog(context, context.getString(R.string.error_project_exists));
			return false;
		}

		String oldProjectPath = Utils.buildProjectPath(project.getName());
		File oldProjectDirectory = new File(oldProjectPath);

		String newProjectPath = Utils.buildProjectPath(newProjectName);
		File newProjectDirectory = new File(newProjectPath);

		boolean directoryRenamed = false;

		if (oldProjectPath.equalsIgnoreCase(newProjectPath)) {
			String tmpProjectPath = Utils.buildProjectPath(createTemporaryDirectoryName(newProjectName));
			File tmpProjectDirectory = new File(tmpProjectPath);
			directoryRenamed = oldProjectDirectory.renameTo(tmpProjectDirectory);
			if (directoryRenamed) {
				directoryRenamed = tmpProjectDirectory.renameTo(newProjectDirectory);
			}
		} else {
			directoryRenamed = oldProjectDirectory.renameTo(newProjectDirectory);
		}

		if (directoryRenamed) {
			project.setName(newProjectName);
			StorageHandler.getInstance().saveProject(project);
		}

		if (!directoryRenamed) {
			Utils.showErrorDialog(context, context.getString(R.string.error_rename_project));
		}

		return directoryRenamed;
	}

	public boolean renameProjectNameAndDescription(String newProjectName, String newProjectDescription, Context context) {
		if (StorageHandler.getInstance().projectExistsCheckCase(newProjectName)) {
			Utils.showErrorDialog(context, context.getString(R.string.error_project_exists));
			return false;
		}

		String oldProjectPath = Utils.buildProjectPath(project.getName());
		File oldProjectDirectory = new File(oldProjectPath);

		String newProjectPath = Utils.buildProjectPath(newProjectName);
		File newProjectDirectory = new File(newProjectPath);

		boolean directoryRenamed = false;

		if (oldProjectPath.equalsIgnoreCase(newProjectPath)) {
			String tmpProjectPath = Utils.buildProjectPath(createTemporaryDirectoryName(newProjectName));
			File tmpProjectDirectory = new File(tmpProjectPath);
			directoryRenamed = oldProjectDirectory.renameTo(tmpProjectDirectory);
			if (directoryRenamed) {
				directoryRenamed = tmpProjectDirectory.renameTo(newProjectDirectory);
			}
		} else {
			directoryRenamed = oldProjectDirectory.renameTo(newProjectDirectory);
		}

		if (directoryRenamed) {
			project.setName(newProjectName);
			project.setDescription(newProjectDescription);
			this.saveProject();
		}

		if (!directoryRenamed) {
			Utils.showErrorDialog(context, context.getString(R.string.error_rename_project));
		}

		return directoryRenamed;
	}

	public Sprite getCurrentSprite() {
		return currentSprite;
	}

	public void setCurrentSprite(Sprite sprite) {
		currentSprite = sprite;
	}

	public Script getCurrentScript() {
		return currentScript;
	}

	public void setCurrentScript(Script script) {
		if (script == null) {
			currentScript = null;
		} else if (currentSprite.getScriptIndex(script) != -1) {
			currentScript = script;
		}
	}

	public void addSprite(Sprite sprite) {
		project.addSprite(sprite);
	}

	public void addScript(Script script) {
		currentSprite.addScript(script);
	}

	public boolean spriteExists(String spriteName) {
		for (Sprite tempSprite : project.getSpriteList()) {
			if (tempSprite.getName().equalsIgnoreCase(spriteName)) {
				return true;
			}
		}
		return false;
	}

	public int getCurrentSpritePosition() {
		return project.getSpriteList().indexOf(currentSprite);
	}

	public int getCurrentScriptPosition() {
		int currentSpritePosition = this.getCurrentSpritePosition();
		if (currentSpritePosition == -1) {
			return -1;
		}

		return project.getSpriteList().get(currentSpritePosition).getScriptIndex(currentScript);
	}

	public boolean setCurrentSpriteWithPosition(int position) {
		if (position >= project.getSpriteList().size() || position < 0) {
			return false;
		}

		currentSprite = project.getSpriteList().get(position);
		return true;
	}

	public boolean setCurrentScriptWithPosition(int position) {
		int currentSpritePosition = this.getCurrentSpritePosition();
		if (currentSpritePosition == -1) {
			return false;
		}

		if (position >= project.getSpriteList().get(currentSpritePosition).getNumberOfScripts() || position < 0) {
			return false;
		}

		currentScript = project.getSpriteList().get(this.getCurrentSpritePosition()).getScript(position);

		return true;
	}

	private String createTemporaryDirectoryName(String projectDirectoryName) {
		String temporaryDirectorySuffix = "_tmp";
		String temporaryDirectoryName = projectDirectoryName + temporaryDirectorySuffix;
		int suffixCounter = 0;
		while (StorageHandler.getInstance().projectExistsIgnoreCase(temporaryDirectoryName)) {
			temporaryDirectoryName = projectDirectoryName + temporaryDirectorySuffix + suffixCounter;
			suffixCounter++;
		}
		return temporaryDirectoryName;
	}

	public FileChecksumContainer getFileChecksumContainer() {
		return this.fileChecksumContainer;
	}

	public void setFileChecksumContainer(FileChecksumContainer fileChecksumContainer) {
		this.fileChecksumContainer = fileChecksumContainer;
	}

	private class SaveProjectAsynchronousTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			StorageHandler.getInstance().saveProject(project);
			return null;
		}
	}
}
