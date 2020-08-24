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
package org.catrobat.catroid;

import android.content.Context;
import android.util.Log;

import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.dagger.EagerSingleton;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ProjectManager implements EagerSingleton {

	private static ProjectManager instance;
	private static final String TAG = ProjectManager.class.getSimpleName();

	private Project project;
	private Scene currentlyEditedScene;
	private Scene currentlyPlayingScene;
	private Scene startScene;
	private Sprite currentSprite;

	private Context applicationContext;

	public ProjectManager(Context applicationContext) {
		if (instance != null) {
			throw new RuntimeException("ProjectManager should be instantiated only once");
		}
		this.applicationContext = applicationContext;
		instance = this;
	}

	/**
	 * Replaced with dependency injection
	 *
	 * @deprecated use dependency injection with Dagger instead.
	 */
	@Deprecated
	public static ProjectManager getInstance() {
		return instance;
	}

	@SuppressWarnings({"unused", "WeakerAccess"})
	public boolean initializeDefaultProject() {
		return initializeDefaultProject(applicationContext);
	}

	/**
	 * @deprecated use {@link #initializeDefaultProject()} without Context instead.
	 */
	@Deprecated
	public boolean initializeDefaultProject(Context context) {
		try {
			project = DefaultProjectHandler.createAndSaveDefaultProject(context);
			currentSprite = null;
			currentlyEditedScene = project.getDefaultScene();
			currentlyPlayingScene = currentlyEditedScene;
			return true;
		} catch (IOException ioException) {
			Log.e(TAG, "Cannot initialize default project.", ioException);
			return false;
		}
	}

	@SuppressWarnings({"unused"})
	public void createNewEmptyProject(String name, boolean landscapeMode, boolean castEnabled) throws IOException {
		createNewEmptyProject(name, applicationContext, landscapeMode, castEnabled);
	}

	/**
	 * @deprecated use {@link #createNewEmptyProject(String, boolean, boolean)} ()} without Context instead.
	 */
	@Deprecated
	public void createNewEmptyProject(String name, Context context, boolean landscapeMode, boolean castEnabled) throws IOException {
		project = DefaultProjectHandler.createAndSaveEmptyProject(name, context, landscapeMode, castEnabled);
		currentSprite = null;
		currentlyEditedScene = project.getDefaultScene();
		currentlyPlayingScene = currentlyEditedScene;
	}

	@SuppressWarnings({"unused"})
	public void createNewExampleProject(String name, DefaultProjectHandler.ProjectCreatorType projectCreatorType, boolean landscapeMode) throws IOException {
		createNewExampleProject(name, applicationContext, projectCreatorType, landscapeMode);
	}

	/**
	 * @deprecated use {@link #createNewExampleProject(String, DefaultProjectHandler.ProjectCreatorType, boolean)} ()} without Context instead.
	 */
	@Deprecated
	public void createNewExampleProject(String name, Context context, DefaultProjectHandler.ProjectCreatorType projectCreatorType, boolean landscapeMode) throws IOException {
		DefaultProjectHandler.getInstance().setDefaultProjectCreator(projectCreatorType);
		project = DefaultProjectHandler.createAndSaveDefaultProject(name, context, landscapeMode);
		currentSprite = null;
		currentlyEditedScene = project.getDefaultScene();
		currentlyPlayingScene = currentlyEditedScene;
	}

	public Project getCurrentProject() {
		return project;
	}

	public Scene getCurrentlyPlayingScene() {
		if (currentlyPlayingScene == null) {
			currentlyPlayingScene = getCurrentlyEditedScene();
		}
		return currentlyPlayingScene;
	}

	public void setCurrentlyPlayingScene(Scene scene) {
		currentlyPlayingScene = scene;
	}

	public Scene getStartScene() {
		if (startScene == null) {
			startScene = getCurrentlyEditedScene();
		}
		return startScene;
	}

	public void setStartScene(Scene scene) {
		startScene = scene;
	}

	public Scene getCurrentlyEditedScene() {
		if (currentlyEditedScene == null && project != null) {
			currentlyEditedScene = project.getDefaultScene();
		}
		return currentlyEditedScene;
	}

	public boolean isCurrentProjectLandscapeMode() {
		int virtualScreenWidth = getCurrentProject().getXmlHeader().virtualScreenWidth;
		int virtualScreenHeight = getCurrentProject().getXmlHeader().virtualScreenHeight;

		return virtualScreenWidth > virtualScreenHeight;
	}

	public void setCurrentProject(Project project) {
		currentSprite = null;

		this.project = project;

		if (project != null && !project.getSceneList().isEmpty()) {
			currentlyEditedScene = project.getDefaultScene();
			currentlyPlayingScene = currentlyEditedScene;
		}
	}

	public Sprite getCurrentSprite() {
		return currentSprite;
	}

	public void setCurrentSprite(Sprite sprite) {
		currentSprite = sprite;
	}

	public void setCurrentlyEditedScene(Scene scene) {
		currentlyEditedScene = scene;
		currentlyPlayingScene = scene;
	}

	public List<UserVariable> getGlobalVariableConflicts(Project project1, Project project2) {
		List<UserVariable> project1GlobalVars = project1.getUserVariables();
		List<UserVariable> project2GlobalVars = project2.getUserVariables();
		List<UserVariable> conflicts = new ArrayList<>();
		for (UserVariable project1GlobalVar : project1GlobalVars) {
			for (UserVariable project2GlobalVar : project2GlobalVars) {
				if (project1GlobalVar.getName().equals(project2GlobalVar.getName()) && !project1GlobalVar.getValue()
						.equals(project2GlobalVar.getValue())) {
					conflicts.add(project1GlobalVar);
				}
			}
		}
		return conflicts;
	}

	public List<UserList> getGlobalListConflicts(Project project1, Project project2) {
		List<UserList> project1GlobalLists = project1.getUserLists();
		List<UserList> project2GlobalLists = project2.getUserLists();
		List<UserList> conflicts = new ArrayList<>();
		for (UserList project1GlobalList : project1GlobalLists) {
			for (UserList project2GlobalList : project2GlobalLists) {
				if (project1GlobalList.getName().equals(project2GlobalList.getName()) && !project1GlobalList.getValue()
						.equals(project2GlobalList.getValue())) {
					conflicts.add(project1GlobalList);
				}
			}
		}
		return conflicts;
	}
}
