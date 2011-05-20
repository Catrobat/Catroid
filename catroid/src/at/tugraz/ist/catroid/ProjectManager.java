/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.Utils;

public class ProjectManager {

	private Sprite currentSprite;
	private Project project;
	private static ProjectManager instance;
	private Script currentScript;
	// used in uiTests
	private int serverProjectId;

	public FileChecksumContainer fileChecksumContainer;

	private ProjectManager() {
		fileChecksumContainer = new FileChecksumContainer();
	}

	public static ProjectManager getInstance() {
		if (instance == null) {
			instance = new ProjectManager();
		}
		return instance;
	}

	public boolean loadProject(String projectName, Context context, boolean errorMessage) {
		try {
			fileChecksumContainer = new FileChecksumContainer();
			project = StorageHandler.getInstance().loadProject(projectName);
			if (project == null) {
				project = StorageHandler.getInstance().createDefaultProject(context);
				if (errorMessage) {
					Utils.displayErrorMessage(context, context.getString(R.string.error_load_project));
					return false;
				}
			}
			currentSprite = null;
			currentScript = null;
			return true;
		} catch (Exception e) {
			Utils.displayErrorMessage(context, context.getString(R.string.error_load_project));
			return false;
		}
	}

	public boolean initializeDefaultProject(Context context) {
		try {
			fileChecksumContainer = new FileChecksumContainer();
			project = StorageHandler.getInstance().createDefaultProject(context);
			currentSprite = null;
			currentScript = null;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Utils.displayErrorMessage(context, context.getString(R.string.error_load_project));
			return false;
		}
	}

	public void saveProject(Context context) {
		if (project == null) {
			return;
		}
		StorageHandler.getInstance().saveProject(project);
	}

	public void deleteCurrentProject(Context context) {

		StorageHandler.getInstance().deleteProject(project);

		project = null;
	}

	public void resetProject(Context context) throws NameNotFoundException {
		project = new Project(context, project.getName());
		currentSprite = null;
		currentScript = null;
	}

	public void addSprite(Sprite sprite) {
		project.addSprite(sprite);
	}

	public void addScript(Script script) {
		currentSprite.getScriptList().add(script);
	}

	public Sprite getCurrentSprite() {
		return currentSprite;
	}

	public Project getCurrentProject() {
		return project;
	}

	public Script getCurrentScript() {
		return currentScript;
	}

	public void initializeNewProject(String projectName, Context context) {
		project = new Project(context, projectName);
		fileChecksumContainer = new FileChecksumContainer();
		currentSprite = null;
		currentScript = null;
		saveProject(context);
	}

	/**
	 * @return false if project doesn't contain the new sprite, true otherwise
	 */
	public void setCurrentSprite(Sprite sprite) {
		currentSprite = sprite;
	}

	/**
	 * @return false if currentSprite doesn't contain the new script, true
	 *         otherwise
	 */
	public boolean setCurrentScript(Script script) {
		if (script == null) {
			currentScript = null;
			return true;
		}
		if (currentSprite.getScriptList().contains(script)) {
			currentScript = script;
			return true;
		}
		return false;
	}

	public void setProject(Project project) {
		currentScript = null;
		currentSprite = null;

		this.project = project;
	}

	public boolean spriteExists(String spriteName) {
		for (Sprite tempSprite : project.getSpriteList()) {
			if (tempSprite.getName().equalsIgnoreCase(spriteName)) {
				return true;
			}
		}
		return false;
	}

	public boolean renameProject(String newProjectName, Context context) {

		if (StorageHandler.getInstance().projectExists(newProjectName)) {
			Utils.displayErrorMessage(context, context.getString(R.string.error_project_exists));
			return false;
		}

		File oldProjectDirectory = new File(Consts.DEFAULT_ROOT + "/" + project.getName());
		File oldProjectFile = new File(Consts.DEFAULT_ROOT + "/" + project.getName() + "/" + project.getName()
				+ Consts.PROJECT_EXTENTION);

		File newProjectDirectory = new File(Consts.DEFAULT_ROOT + "/" + newProjectName);
		File newProjectFile = new File(Consts.DEFAULT_ROOT + "/" + project.getName() + "/" + newProjectName
				+ Consts.PROJECT_EXTENTION);

		project.setName(newProjectName);

		boolean fileRenamed = oldProjectFile.renameTo(newProjectFile);
		boolean directoryRenamed = oldProjectDirectory.renameTo(newProjectDirectory);

		return (directoryRenamed && fileRenamed);
	}

	public void setServerProjectId(int serverProjectId) {
		this.serverProjectId = serverProjectId;
	}

	public int getServerProjectId() {
		return serverProjectId;
	}

	public int getCurrentSpritePosition() {
		return project.getSpriteList().indexOf(currentSprite);
	}

	public int getCurrentScriptPosition() {
		int currentSpritePos = this.getCurrentSpritePosition();
		if (currentSpritePos == -1) {
			return -1;
		}

		return project.getSpriteList().get(currentSpritePos).getScriptList().indexOf(currentScript);
	}

	public boolean setCurrentSpriteWithPosition(int position) {

		if (position >= project.getSpriteList().size() || position < 0) {
			return false;
		}

		currentSprite = project.getSpriteList().get(position);
		return true;

	}

	public boolean setCurrentScriptWithPosition(int position) {
		int currentSpritePos = this.getCurrentSpritePosition();
		if (currentSpritePos == -1) {
			return false;
		}

		if (position >= project.getSpriteList().get(currentSpritePos).getScriptList().size() || position < 0) {
			return false;
		}

		currentScript = project.getSpriteList().get(this.getCurrentSpritePosition()).getScriptList().get(position);

		return true;

	}

}