/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.LegoEV3Setting;
import org.catrobat.catroid.content.LegoNXTSetting;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Setting;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenBounceOffScript;
import org.catrobat.catroid.content.backwardcompatibility.BrickTreeBuilder;
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexAndWaitBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.physics.PhysicsCollisionListener;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.VisibleForTesting;

import static org.catrobat.catroid.common.Constants.CURRENT_CATROBAT_LANGUAGE_VERSION;
import static org.catrobat.catroid.common.Constants.PERMISSIONS_FILE_NAME;

public final class ProjectManager {

	private static ProjectManager instance;
	private static final String TAG = ProjectManager.class.getSimpleName();

	private Project project;
	private Scene currentlyEditedScene;
	private Scene currentlyPlayingScene;
	private Scene startScene;
	private Sprite currentSprite;
	private HashMap<String, Boolean> downloadedProjects;
	private final String downloadedProjectsName = "downloaded_projects";

	private Context applicationContext;

	public Context getApplicationContext() {
		return applicationContext;
	}

	public ProjectManager(Context applicationContext) {
		this.applicationContext = applicationContext;
		if (instance == null) {
			instance = this;
			loadDownloadedProjects();
		}
	}

	/**
	 * Replaced with dependency injection
	 *
	 * @deprecated use dependency injection with koin instead.
	 */
	@Deprecated
	public static ProjectManager getInstance() {
		return instance;
	}

	public void loadProject(File projectDir) throws ProjectException {
		loadProject(projectDir, applicationContext);
	}

	/**
	 * @deprecated use {@link #loadProject(File projectDir)} without Context instead.
	 */
	@Deprecated
	public void loadProject(File projectDir, Context context) throws ProjectException {

		Project previousProject = project;

		try {
			project = XstreamSerializer.getInstance().loadProject(projectDir, context);
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
			restorePreviousProject(previousProject);
			throw new LoadingProjectException(context.getString(R.string.error_load_project));
		}

		if (project.getCatrobatLanguageVersion() > CURRENT_CATROBAT_LANGUAGE_VERSION) {
			restorePreviousProject(previousProject);
			throw new OutdatedVersionProjectException(context.getString(R.string.error_outdated_version));
		}
		if (project.getCatrobatLanguageVersion() < 0.9 && project.getCatrobatLanguageVersion() != 0.8) {
			restorePreviousProject(previousProject);
			throw new CompatibilityProjectException(context.getString(R.string.error_project_compatibility));
		}

		if (project.getCatrobatLanguageVersion() <= 0.91) {
			project.setScreenMode(ScreenModes.STRETCH);
		}
		if (project.getCatrobatLanguageVersion() <= 0.992) {
			ProjectManager.updateCollisionFormulasTo993(project);
		}
		if (project.getCatrobatLanguageVersion() <= 0.993) {
			ProjectManager.updateSetPenColorFormulasTo994(project);
		}
		if (project.getCatrobatLanguageVersion() <= 0.994) {
			ProjectManager.updateArduinoValuesTo995(project);
		}
		if (project.getCatrobatLanguageVersion() <= 0.995) {
			ProjectManager.updateCollisionScriptsTo996(project);
		}
		if (project.getCatrobatLanguageVersion() <= 0.999) {
			ProjectManager.makeShallowCopiesDeepAgain(project);
		}
		if (project.getCatrobatLanguageVersion() <= 0.9993) {
			ProjectManager.updateScriptsToTreeStructure(project);
		}
		if (project.getCatrobatLanguageVersion() <= 0.99992) {
			removePermissionsFile(project);
		}
		if (project.getCatrobatLanguageVersion() <= 0.9999995) {
			updateBackgroundIndexTo9999995(project);
		}
		if (project.getCatrobatLanguageVersion() <= 1.03) {
			ProjectManager.updateDirectionProperty(project);
		}
		project.setCatrobatLanguageVersion(CURRENT_CATROBAT_LANGUAGE_VERSION);

		localizeBackgroundSprites(project, context.getString(R.string.background));
		initializeScripts(project);

		loadLegoNXTSettingsFromProject(project, context);
		loadLegoEV3SettingsFromProject(project, context);

		Brick.ResourcesSet resourcesSet = project.getRequiredResources();

		if (resourcesSet.contains(Brick.BLUETOOTH_PHIRO)) {
			SettingsFragment.setPhiroSharedPreferenceEnabled(context, true);
		}

		if (resourcesSet.contains(Brick.BLUETOOTH_SENSORS_ARDUINO)) {
			SettingsFragment.setArduinoSharedPreferenceEnabled(context, true);
		}

		if (resourcesSet.contains(Brick.SPEECH_RECOGNITION)) {
			SettingsFragment.setAISpeechReconitionPreferenceEnabled(context, true);
		}

		if (resourcesSet.contains(Brick.FACE_DETECTION)) {
			SettingsFragment.setAIFaceDetectionPreferenceEnabled(context, true);
		}

		if (resourcesSet.contains(Brick.TEXT_TO_SPEECH)) {
			SettingsFragment.setAISpeechSynthetizationPreferenceEnabled(context, true);
		}

		if (resourcesSet.contains(Brick.TEXT_DETECTION)) {
			SettingsFragment.setAITextRecognitionPreferenceEnabled(context, true);
		}

		currentlyPlayingScene = project.getDefaultScene();
		currentSprite = null;
	}

	private void restorePreviousProject(Project previousProject) {
		project = previousProject;
		if (previousProject != null) {
			currentlyPlayingScene = project.getDefaultScene();
		}
	}

	@VisibleForTesting
	public static void makeShallowCopiesDeepAgain(Project project) {
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
							Log.e(TAG, "Cannot copy: " + lookData.getFile().getAbsolutePath()
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
							Log.e(TAG, "Cannot copy: " + soundInfo.getFile().getAbsolutePath()
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

	private void localizeBackgroundSprites(Project project, String localizedBackgroundName) {
		for (Scene scene : project.getSceneList()) {
			if (!scene.getSpriteList().isEmpty()) {
				Sprite background = scene.getSpriteList().get(0);
				background.renameSpriteAndUpdateCollisionFormulas(localizedBackgroundName, scene);
				background.look.setZIndex(0);
			}
		}
	}

	private static void initializeScripts(Project project) {
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Script script : sprite.getScriptList()) {
					script.setParents();

					if (script instanceof WhenBounceOffScript) {
						((WhenBounceOffScript) script).updateSpriteToCollideWith(scene);
					}
				}
			}
		}
	}

	private static void loadLegoNXTSettingsFromProject(Project project, Context context) {
		for (Setting setting : project.getSettings()) {
			if (setting instanceof LegoNXTSetting) {
				SettingsFragment.enableLegoMindstormsNXTBricks(context);
				SettingsFragment.setLegoMindstormsNXTSensorMapping(context, ((LegoNXTSetting) setting).getSensorMapping());
				return;
			}
		}
	}

	private static void loadLegoEV3SettingsFromProject(Project project, Context context) {
		for (Setting setting : project.getSettings()) {
			if (setting instanceof LegoEV3Setting) {
				SettingsFragment.enableLegoMindstormsEV3Bricks(context);
				SettingsFragment.setLegoMindstormsEV3SensorMapping(context, ((LegoEV3Setting) setting).getSensorMapping());
				return;
			}
		}
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

	public static ArrayList<Object> checkForVariablesConflicts(List<Object> globalVariables, List<Object> localVariables) {

		ArrayList<Object> conflicts = new ArrayList<>();
		for (Object localVar : localVariables) {
			if (globalVariables.contains(localVar)) {
				conflicts.add(localVar);
			}
		}
		return conflicts;
	}

	@VisibleForTesting
	public static void updateCollisionFormulasTo993(Project project) {
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Script script : sprite.getScriptList()) {
					List<Brick> flatList = new ArrayList();
					script.addToFlatList(flatList);
					for (Brick brick : flatList) {
						if (brick instanceof FormulaBrick) {
							FormulaBrick formulaBrick = (FormulaBrick) brick;
							for (Formula formula : formulaBrick.getFormulas()) {
								formula.updateCollisionFormulasToVersion();
							}
						}
					}
				}
			}
		}
	}

	@VisibleForTesting
	public static void updateSetPenColorFormulasTo994(Project project) {
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Script script : sprite.getScriptList()) {
					for (Brick brick : script.getBrickList()) {
						if (brick instanceof SetPenColorBrick) {
							SetPenColorBrick spcBrick = (SetPenColorBrick) brick;
							spcBrick.replaceFormulaBrickField(Brick.BrickField.PHIRO_LIGHT_RED, Brick.BrickField.PEN_COLOR_RED);
							spcBrick.replaceFormulaBrickField(Brick.BrickField.PHIRO_LIGHT_GREEN, Brick.BrickField.PEN_COLOR_GREEN);
							spcBrick.replaceFormulaBrickField(Brick.BrickField.PHIRO_LIGHT_BLUE, Brick.BrickField.PEN_COLOR_BLUE);
						}
					}
				}
			}
		}
	}

	@VisibleForTesting
	public static void updateArduinoValuesTo995(Project project) {
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Script script : sprite.getScriptList()) {
					for (Brick brick : script.getBrickList()) {
						if (brick instanceof ArduinoSendPWMValueBrick) {
							ArduinoSendPWMValueBrick spcBrick = (ArduinoSendPWMValueBrick) brick;
							spcBrick.updateArduinoValues994to995();
						}
					}
				}
			}
		}
	}

	@VisibleForTesting
	public static void updateCollisionScriptsTo996(Project project) {
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Script script : sprite.getScriptList()) {
					if (script instanceof WhenBounceOffScript) {
						WhenBounceOffScript bounceOffScript = (WhenBounceOffScript) script;
						String[] spriteNames =
								bounceOffScript.getSpriteToBounceOffName().split(PhysicsCollisionListener.COLLISION_MESSAGE_CONNECTOR);
						String spriteToCollideWith = spriteNames[0];
						if (spriteNames[0].equals(sprite.getName())) {
							spriteToCollideWith = spriteNames[1];
						}
						bounceOffScript.setSpriteToBounceOffName(spriteToCollideWith);
					}
				}
			}
		}
	}

	@VisibleForTesting
	public static void updateBackgroundIndexTo9999995(Project project) {
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Script script : sprite.getScriptList()) {
					for (Brick brick : script.getBrickList()) {
						if (brick instanceof SetBackgroundByIndexBrick) {
							FormulaBrick formulaBrick = (FormulaBrick) brick;
							formulaBrick.replaceFormulaBrickField(Brick.BrickField.LOOK_INDEX,
									Brick.BrickField.BACKGROUND_INDEX);
						} else if (brick instanceof SetBackgroundByIndexAndWaitBrick) {
							FormulaBrick formulaBrick = (FormulaBrick) brick;
							formulaBrick.replaceFormulaBrickField(Brick.BrickField.LOOK_INDEX,
									Brick.BrickField.BACKGROUND_WAIT_INDEX);
						}
					}
				}
			}
		}
	}

	@VisibleForTesting
	public static void updateScriptsToTreeStructure(Project project) {
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Script script : sprite.getScriptList()) {
					BrickTreeBuilder brickTreeBuilder = new BrickTreeBuilder();
					brickTreeBuilder.convertBricks(script.getBrickList());
					script.getBrickList().clear();
					script.getBrickList().addAll(brickTreeBuilder.toList());
				}
			}
		}
	}

	@VisibleForTesting
	public static void removePermissionsFile(Project project) {
		File permissionsFile = new File(project.getDirectory(), PERMISSIONS_FILE_NAME);
		if (permissionsFile.exists()) {
			permissionsFile.delete();
		}
	}

	@VisibleForTesting
	public static void updateDirectionProperty(Project project) {
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Script script : sprite.getScriptList()) {
					List<Brick> flatList = new ArrayList();
					script.addToFlatList(flatList);
					for (Brick brick : flatList) {
						if (brick instanceof FormulaBrick) {
							FormulaBrick formulaBrick = (FormulaBrick) brick;
							for (Formula formula : formulaBrick.getFormulas()) {
								formula.updateDirectionPropertyToVersion();
							}
						}
					}
				}
			}
		}
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

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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

	public boolean setCurrentSceneAndSprite(String sceneName, String spriteName) {
		for (Scene scene : project.getSceneList()) {
			if (scene.getName().equals(sceneName)) {
				setCurrentlyEditedScene(scene);
				for (Sprite sprite : scene.getSpriteList()) {
					if (sprite.getName().equals(spriteName)) {
						currentSprite = sprite;
						return true;
					}
				}
			}
		}
		return false;
	}

	public void setCurrentlyEditedScene(Scene scene) {
		currentlyEditedScene = scene;
		currentlyPlayingScene = scene;
	}

	public void addNewDownloadedProject(String projectName) {
		Boolean flag = downloadedProjects.get(projectName);
		if (flag == null || flag) {
			downloadedProjects.put(projectName, false);
			saveDownloadedProjects();
		}
	}

	public void resetChangedFlag(Project project) {
		String projectName = project.getName();
		Boolean isChanged = downloadedProjects.get(projectName);
		if (isChanged != null && isChanged) {
			downloadedProjects.put(projectName, false);
			saveDownloadedProjects();
		}
	}

	public boolean isChangedProject(Project project) {
		String projectName = project.getName();
		Boolean isChanged = downloadedProjects.get(projectName);
		if (isChanged == null) {
			return true;
		}
		return isChanged;
	}

	public void deleteDownloadedProjectInformation(String projectName) {
		downloadedProjects.remove(projectName);
	}

	public void changedProject(String projectName) {
		Boolean isChanged = downloadedProjects.get(projectName);
		if (isChanged != null && !isChanged) {
			downloadedProjects.put(projectName, true);
			saveDownloadedProjects();
		}
	}

	public void saveDownloadedProjects() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		Gson gson = new Gson();
		String json = gson.toJson(downloadedProjects);
		editor.putString(downloadedProjectsName, json);
		editor.apply();
	}

	public void loadDownloadedProjects() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
		Gson gson = new Gson();
		String json = null;
		if (sharedPreferences != null) {
			json = sharedPreferences.getString(downloadedProjectsName, null);
			if (json != null) {
				Type type = new TypeToken<HashMap<String, Boolean>>() {
				}.getType();
				downloadedProjects = gson.fromJson(json, type);
			} else {
				downloadedProjects = new HashMap<>();
			}
		}
	}

	public void moveChangedFlag(String source, String destination) {
		Boolean value = downloadedProjects.get(source);
		if (null != value) {
			downloadedProjects.remove(source);
			downloadedProjects.put(destination, true);
			saveDownloadedProjects();
		}
	}
}
