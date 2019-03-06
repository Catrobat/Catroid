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
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.recyclerview.controller.BrickController;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.catrobat.catroid.common.Constants.PREF_PROJECTNAME_KEY;

public final class ProjectManager {

	private static final ProjectManager INSTANCE = new ProjectManager();
	private static final String TAG = ProjectManager.class.getSimpleName();

	private Project project;
	private Scene currentlyEditedScene;
	private Scene currentlyPlayingScene;
	private Scene startScene;
	private Sprite currentSprite;

	private BrickController brickController = new BrickController();

	private ProjectManager() {
	}

	public static ProjectManager getInstance() {
		return INSTANCE;
	}

	public void loadProject(String projectName, Context context) throws LoadingProjectException,
			OutdatedVersionProjectException,
			CompatibilityProjectException {

		Project previousProject = project;

		try {
			project = XstreamSerializer.getInstance().loadProject(projectName, context);
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
			restorePreviousProject(previousProject);
			throw new LoadingProjectException(context.getString(R.string.error_load_project));
		}

		if (project.getCatrobatLanguageVersion() > Constants.CURRENT_CATROBAT_LANGUAGE_VERSION) {
			restorePreviousProject(previousProject);
			throw new OutdatedVersionProjectException(context.getString(R.string.error_outdated_version));
		}

		if (project.getCatrobatLanguageVersion() == 0.8f) {
			project.setCatrobatLanguageVersion(0.9f);
		}
		if (project.getCatrobatLanguageVersion() == 0.9f) {
			project.setCatrobatLanguageVersion(0.91f);
		}
		if (project.getCatrobatLanguageVersion() == 0.91f) {
			project.setCatrobatLanguageVersion(0.92f);
			project.setScreenMode(ScreenModes.STRETCH);
		}

		if (project.getCatrobatLanguageVersion() == 0.92f || project.getCatrobatLanguageVersion() == 0.93f) {
			//0.93 should be left out because it available unintentional for a day
			//raise language version here to 0.94
			project.setCatrobatLanguageVersion(0.94f);
		}
		if (project.getCatrobatLanguageVersion() == 0.94f) {
			project.setCatrobatLanguageVersion(0.95f);
		}
		if (project.getCatrobatLanguageVersion() == 0.95f) {
			project.setCatrobatLanguageVersion(0.96f);
		}
		if (project.getCatrobatLanguageVersion() == 0.96f) {
			project.setCatrobatLanguageVersion(0.97f);
		}
		if (project.getCatrobatLanguageVersion() == 0.97f) {
			project.setCatrobatLanguageVersion(0.98f);
		}
		if (project.getCatrobatLanguageVersion() == 0.98f) {
			project.setCatrobatLanguageVersion(0.99f);
		}
		if (project.getCatrobatLanguageVersion() == 0.99f) {
			project.setCatrobatLanguageVersion(0.991f);
		}
		if (project.getCatrobatLanguageVersion() == 0.991f) {
			//With the introduction of grouping there are several Sprite-classes
			//This is simply done in XStreamSpriteConverter
			project.setCatrobatLanguageVersion(0.992f);
		}
		if (project.getCatrobatLanguageVersion() == 0.992f) {
			project.updateCollisionFormulasToVersion(0.993f);
			project.setCatrobatLanguageVersion(0.993f);
		}
		if (project.getCatrobatLanguageVersion() == 0.993f) {
			project.updateSetPenColorFormulas();
			project.setCatrobatLanguageVersion(0.994f);
		}
		if (project.getCatrobatLanguageVersion() == 0.994f) {
			project.updateArduinoValues994to995();
			project.setCatrobatLanguageVersion(0.995f);
		}
		if (project.getCatrobatLanguageVersion() == 0.995f) {
			project.updateCollisionScripts();
			project.setCatrobatLanguageVersion(0.996f);
		}
		if (project.getCatrobatLanguageVersion() == 0.996f) {
			project.setCatrobatLanguageVersion(0.997f);
		}
		if (project.getCatrobatLanguageVersion() == 0.997f) {
			project.setCatrobatLanguageVersion(0.998f);
		}
		if (project.getCatrobatLanguageVersion() == 0.998f) {
			project.setCatrobatLanguageVersion(0.999f);
		}
		if (project.getCatrobatLanguageVersion() == 0.999f) {
			project.setCatrobatLanguageVersion(0.9991f);
		}
		if (project.getCatrobatLanguageVersion() == 0.9991f) {
			project.setCatrobatLanguageVersion(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION);
		}

		//insert further conversions here

		makeShallowCopiesDeepAgain(project);
		setControlBrickReferences(project);
		updateCollisionScriptsSpriteReference(project);

		if (project.getCatrobatLanguageVersion() == Constants.CURRENT_CATROBAT_LANGUAGE_VERSION) {
			localizeBackgroundSprite(context);
		} else {
			restorePreviousProject(previousProject);
			throw new CompatibilityProjectException(context.getString(R.string.error_project_compatibility));
		}

		project.loadLegoNXTSettingsFromProject(context);
		project.loadLegoEV3SettingsFromProject(context);

		Brick.ResourcesSet resourcesSet = project.getRequiredResources();

		if (resourcesSet.contains(Brick.BLUETOOTH_PHIRO)) {
			SettingsFragment.setPhiroSharedPreferenceEnabled(context, true);
		}

		if (resourcesSet.contains(Brick.JUMPING_SUMO)) {
			SettingsFragment.setJumpingSumoSharedPreferenceEnabled(context, true);
		}

		if (resourcesSet.contains(Brick.BLUETOOTH_SENSORS_ARDUINO)) {
			SettingsFragment.setArduinoSharedPreferenceEnabled(context, true);
		}

		currentlyPlayingScene = project.getDefaultScene();
	}

	private void restorePreviousProject(Project previousProject) {
		project = previousProject;
		if (previousProject != null) {
			currentlyPlayingScene = project.getDefaultScene();
		}
	}

	private void makeShallowCopiesDeepAgain(Project project) {
		for (Scene scene : project.getSceneList()) {

			List<String> fileNames = new ArrayList<>();

			for (Sprite sprite : scene.getSpriteList()) {
				for (Iterator<LookData> iterator = sprite.getLookList().iterator(); iterator.hasNext(); ) {
					LookData lookData = iterator.next();

					if (fileNames.contains(lookData.getFile().getName())) {
						try {
							lookData.setFile(StorageOperations.duplicateFile(lookData.getFile()));
						} catch (IOException e) {
							iterator.remove();
							Log.e(TAG, "Cannot not copy: " + lookData.getFile().getAbsolutePath()
									+ ", removing LookData " + lookData.getName() + " from "
									+ project.getName() + ", "
									+ scene.getName() + ", "
									+ sprite.getName() + ".");
						}
					}
					fileNames.add(lookData.getFile().getName());
				}

				for (Iterator<SoundInfo> iterator = sprite.getSoundList().iterator(); iterator.hasNext(); ) {
					SoundInfo soundInfo = iterator.next();

					if (fileNames.contains(soundInfo.getFile().getName())) {
						try {
							soundInfo.setFile(StorageOperations.duplicateFile(soundInfo.getFile()));
						} catch (IOException e) {
							iterator.remove();
							Log.e(TAG, "Cannot not copy: " + soundInfo.getFile().getAbsolutePath()
									+ ", removing SoundInfo " + soundInfo.getName() + " from "
									+ project.getName() + ", "
									+ scene.getName() + ", "
									+ sprite.getName() + ".");
						}
					}
					fileNames.add(soundInfo.getFile().getName());
				}
			}
		}
	}

	private void localizeBackgroundSprite(Context context) {
		if (currentlyEditedScene == null) {
			return;
		}
		if (currentlyEditedScene.getSpriteList().size() > 0) {
			currentlyEditedScene.getSpriteList().get(0).setName(context.getString(R.string.background));
			currentlyEditedScene.getSpriteList().get(0).look.setZIndex(0);
		}
		currentSprite = null;

		PreferenceManager.getDefaultSharedPreferences(context)
				.edit()
				.putString(PREF_PROJECTNAME_KEY, project.getName())
				.commit();
	}

	public void saveProject(Context context) {
		if (project == null) {
			return;
		}

		project.saveLegoNXTSettingsToProject(context);
		project.saveLegoEV3SettingsToProject(context);

		SaveProjectAsynchronousTask saveTask = new SaveProjectAsynchronousTask();
		saveTask.execute();
	}

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

	public void initializeNewProject(String projectName, Context context, boolean empty, boolean drone,
			boolean landscapeMode, boolean castEnabled, boolean jumpingSumo)
			throws IllegalArgumentException, IOException {

		if (empty) {
			project = DefaultProjectHandler.createAndSaveEmptyProject(projectName, context, landscapeMode, castEnabled);
		} else {
			if (drone) {
				DefaultProjectHandler.getInstance().setDefaultProjectCreator(DefaultProjectHandler.ProjectCreatorType
						.PROJECT_CREATOR_DRONE);
			} else if (castEnabled) {
				DefaultProjectHandler.getInstance().setDefaultProjectCreator(DefaultProjectHandler.ProjectCreatorType
						.PROJECT_CREATOR_CAST);
			} else if (jumpingSumo) {
				DefaultProjectHandler.getInstance().setDefaultProjectCreator(DefaultProjectHandler.ProjectCreatorType
						.PROJECT_CREATOR_JUMPING_SUMO);
			} else {
				DefaultProjectHandler.getInstance().setDefaultProjectCreator(DefaultProjectHandler.ProjectCreatorType
						.PROJECT_CREATOR_DEFAULT);
			}
			project = DefaultProjectHandler.createAndSaveDefaultProject(projectName, context, landscapeMode);
		}

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
		if (currentlyEditedScene == null) {
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
		this.currentlyEditedScene = scene;
		currentlyPlayingScene = scene;
	}

	public void setControlBrickReferences(Project project) {
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Script script : sprite.getScriptList()) {
					brickController.setControlBrickReferences(script.getBrickList());
				}
			}
		}
	}

	private void updateCollisionScriptsSpriteReference(Project project) {
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Script script : sprite.getScriptList()) {
					if (script instanceof CollisionScript) {
						CollisionScript collisionScript = (CollisionScript) script;
						collisionScript.updateSpriteToCollideWith(scene);
					}
				}
			}
		}
	}

	private class SaveProjectAsynchronousTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			XstreamSerializer.getInstance().saveProject(project);
			return null;
		}
	}
}
