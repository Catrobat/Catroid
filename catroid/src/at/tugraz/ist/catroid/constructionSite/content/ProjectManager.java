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

package at.tugraz.ist.catroid.constructionSite.content;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.Utils;

public class ProjectManager {

	private Project project;
	private static ProjectManager instance;
	private ProjectValuesManager projectValuesManager;
	// used in uiTests
	private transient int serverProjectId;

	private ProjectManager() {
		projectValuesManager = new ProjectValuesManager();
	}

	public static ProjectManager getInstance() {
		if (instance == null) {
			instance = new ProjectManager();
		}
		return instance;
	}

	public boolean loadProject(String projectName, Context context, boolean errorMessage) {
		try {
			String oldProjectName = project.getName();
			int oldCurrentSpritePos = projectValuesManager.getCurrentSpritePosition();
			int oldCurrentScriptPos = projectValuesManager.getCurrentScriptPosition();

			project = StorageHandler.getInstance().loadProject(projectName);
			if (project == null) {
				project = StorageHandler.getInstance().createDefaultProject(context);
				projectValuesManager.setCurrentSprite(null);
				projectValuesManager.setCurrentScript(null);
				projectValuesManager.updateProjectValuesManager();
				if (errorMessage) {
					Utils.displayErrorMessage(context, context.getString(R.string.error_load_project));
					return false;
				}
				return true;
			}

			if (oldProjectName == projectName && oldCurrentScriptPos != -1 && oldCurrentSpritePos != -1) {
				projectValuesManager.setCurrentSpriteWithPosition(oldCurrentSpritePos);
				projectValuesManager.setCurrentScriptWithPosition(oldCurrentScriptPos);
				projectValuesManager.updateProjectValuesManager();
				return true;
			}

			projectValuesManager.setCurrentSprite(null);
			projectValuesManager.setCurrentScript(null);
			projectValuesManager.updateProjectValuesManager();

			return true;
		} catch (Exception e) {
			Utils.displayErrorMessage(context, context.getString(R.string.error_load_project));

		}
		return false;
	}

	public boolean initializeDefaultProject(Context context) {
		try {
			project = StorageHandler.getInstance().createDefaultProject(context);
			projectValuesManager.setCurrentSprite(null);
			projectValuesManager.setCurrentScript(null);
			projectValuesManager.updateProjectValuesManager();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Utils.displayErrorMessage(context, context.getString(R.string.error_load_project));
			return false;
		}
	}

	public void saveProject(Context context) {
		try {
			if (project == null) {
				return;
			}
			StorageHandler.getInstance().saveProject(project);
		} catch (IOException e) {
			Utils.displayErrorMessage(context, context.getString(R.string.error_save_project));
		}
	}

	//TODO: just for debugging
	public void deleteCurrentProject(Context context) {
		try {
			StorageHandler.getInstance().deleteProject(project);
		} catch (IOException e) {
			Utils.displayErrorMessage(context, context.getString(R.string.error_delete_project));
		}
		project = null;
	}

	//TODO: just for debugging
	public void resetProject(Context context) throws NameNotFoundException {
		project = new Project(context, project.getName());

		projectValuesManager.setCurrentSprite(null);
		projectValuesManager.setCurrentScript(null);
	}

	public void addSprite(Sprite sprite) {
		project.addSprite(sprite);
	}

	public void addScript(Script script) {
		projectValuesManager.getCurrentSprite().getScriptList().add(script);
	}

	public void addBrick(Brick brick) {
		projectValuesManager.getCurrentScript().addBrick(brick);
	}

	public void moveBrickUpInList(int position) {
		Script currentScript = projectValuesManager.getCurrentScript();
		if (position >= 0 && position < currentScript.getBrickList().size()) {
			currentScript.moveBrickBySteps(currentScript.getBrickList().get(position), -1);
		}
	}

	public void moveBrickDownInList(int position) {
		Script currentScript = projectValuesManager.getCurrentScript();
		if (position >= 0 && position < currentScript.getBrickList().size()) {
			currentScript.moveBrickBySteps(currentScript.getBrickList().get(position), 1);

		}
	}

	public Project getCurrentProject() {
		return project;
	}

	public void initializeNewProject(String projectName, Context context) {
		project = new Project(context, projectName);

		projectValuesManager.setCurrentSprite(null);
		projectValuesManager.setCurrentScript(null);
		saveProject(context);
	}

	//TODO: just for debugging
	public void setProject(Project project) {

		projectValuesManager.setCurrentSprite(null);
		projectValuesManager.setCurrentScript(null);

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
		try {
			if (StorageHandler.getInstance().projectExists(newProjectName)) {
				Utils.displayErrorMessage(context, context.getString(R.string.error_project_exists));
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		File oldProjectDirectory = new File(Consts.DEFAULT_ROOT + "/" + project.getName());
		File oldProjectFile = new File(Consts.DEFAULT_ROOT + "/" + project.getName() + "/" + project.getName()
				+ Consts.PROJECT_EXTENTION);

		File newProjectDirectory = new File(Consts.DEFAULT_ROOT + "/" + newProjectName);
		File newProjectFile = new File(Consts.DEFAULT_ROOT + "/" + project.getName() + "/" + newProjectName
				+ Consts.PROJECT_EXTENTION);

		try {
			String projectAsString = StorageHandler.getInstance().getProjectfileAsString(this.project.getName());
			StorageHandler.getInstance().overwriteSpfFile(project.getName(),
					projectAsString.replace(project.getName(), newProjectName));
			loadProject(project.getName(), context, false);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

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

	public ProjectValuesManager getProjectValuesManager() {
		return this.projectValuesManager;
	}

}