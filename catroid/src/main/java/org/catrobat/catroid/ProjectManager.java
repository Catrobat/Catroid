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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.common.DefaultProjectHandler.ProjectCreatorType;
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
import org.catrobat.catroid.content.bricks.Brick.BrickField;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.dagger.EagerSingleton;
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
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.VisibleForTesting;

import static org.catrobat.catroid.common.Constants.CURRENT_CATROBAT_LANGUAGE_VERSION;
import static org.catrobat.catroid.common.Constants.PERMISSIONS_FILE_NAME;
import static org.catrobat.catroid.common.Constants.PREF_PROJECTNAME_KEY;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public final class ProjectManager implements EagerSingleton {

	private static ProjectManager instance;
	private static final String TAG = ProjectManager.class.getSimpleName();
	private final XstreamSerializer xstreamSerializer;
	private final DefaultProjectHandler defaultProjectHandler;
	private static Pattern urlWhitelistPattern;

	private Project project;
	private Scene currentlyEditedScene;
	private Scene currentlyPlayingScene;
	private Scene startScene;
	private Sprite currentSprite;

	private Context applicationContext;

	public ProjectManager(Context applicationContext, XstreamSerializer xstreamSerializer,
			DefaultProjectHandler defaultProjectHandler) {
		if (instance != null) {
			throw new RuntimeException("For the time being this class should be instantiated only "
					+ "once");
		}
		this.applicationContext = applicationContext;
		this.xstreamSerializer = xstreamSerializer;
		this.defaultProjectHandler = defaultProjectHandler;
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

	public void loadProject(File projectDir) throws ProjectException {
		Context context = applicationContext;
		Project previousProject = project;

		try {
			project = xstreamSerializer.loadProject(projectDir, context);
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
			restorePreviousProject(previousProject);
			throw new LoadingProjectException(context.getString(R.string.error_load_project));
		}

		if (project.getCatrobatLanguageVersion() > CURRENT_CATROBAT_LANGUAGE_VERSION) {
			restorePreviousProject(previousProject);
			throw new OutdatedVersionProjectException(context.getString(R.string.error_outdated_version));
		}
		if (project.getCatrobatLanguageVersion() < 0.9f && project.getCatrobatLanguageVersion() != 0.8f) {
			restorePreviousProject(previousProject);
			throw new CompatibilityProjectException(context.getString(R.string.error_project_compatibility));
		}

		if (project.getCatrobatLanguageVersion() <= 0.91f) {
			project.setScreenMode(ScreenModes.STRETCH);
		}
		if (project.getCatrobatLanguageVersion() <= 0.992f) {
			ProjectManager.updateCollisionFormulasTo993(project);
		}
		if (project.getCatrobatLanguageVersion() <= 0.993f) {
			ProjectManager.updateSetPenColorFormulasTo994(project);
		}
		if (project.getCatrobatLanguageVersion() <= 0.994f) {
			ProjectManager.updateArduinoValuesTo995(project);
		}
		if (project.getCatrobatLanguageVersion() <= 0.995f) {
			ProjectManager.updateCollisionScriptsTo996(project);
		}
		if (project.getCatrobatLanguageVersion() <= 0.999f) {
			ProjectManager.makeShallowCopiesDeepAgain(project);
		}
		if (project.getCatrobatLanguageVersion() <= 0.9993f) {
			ProjectManager.updateScriptsToTreeStructure(project);
		}
		if (project.getCatrobatLanguageVersion() <= 0.99992f) {
			removePermissionsFile(project);
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

		if (resourcesSet.contains(Brick.JUMPING_SUMO)) {
			SettingsFragment.setJumpingSumoSharedPreferenceEnabled(context, true);
		}

		if (resourcesSet.contains(Brick.BLUETOOTH_SENSORS_ARDUINO)) {
			SettingsFragment.setArduinoSharedPreferenceEnabled(context, true);
		}

		currentlyPlayingScene = project.getDefaultScene();
		currentSprite = null;
	}

	/**
	 * @deprecated use {@link #loadProject(File projectDir)} without Context instead.
	 */
	@Deprecated
	public void loadProject(File projectDir, Context context) throws ProjectException {
		loadProject(projectDir);
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
				background.setName(localizedBackgroundName);
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

	public static synchronized boolean checkIfURLIsWhitelisted(String url) {
		if (urlWhitelistPattern == null) {
			try {
				initializeURLWhitelistPattern();
			} catch (IOException | JSONException | NullPointerException e) {
				Log.e(TAG, "Cannot read URL whitelist", e);
				return false;
			}
		}
		return urlWhitelistPattern.matcher(url).matches();
	}

	private static void initializeURLWhitelistPattern() throws IOException, JSONException, NullPointerException {
		InputStream stream = Utils.getInputStreamFromAsset(instance.applicationContext, Constants.URL_WHITELIST_JSON_FILE_NAME);
		JSONObject whiteList = Utils.getJsonObjectFromInputStream(stream);
		JSONArray domains = whiteList.getJSONArray(Constants.URL_WHITELIST_JSON_ARRAY_NAME);

		StringBuilder trustedDomains = new StringBuilder("(");
		for (int i = 0; i < domains.length(); i++) {
			trustedDomains.append(domains.getString(i));

			if (i < domains.length() - 1) {
				trustedDomains.append('|');
			}
		}
		trustedDomains.append(')');

		urlWhitelistPattern = Pattern.compile("https?://([a-zA-Z0-9-]+\\.)*"
				+ trustedDomains.toString().replaceAll("\\.", "\\\\.")
				+ "(:[0-9]{1,5})?(/.*)?");
	}

	@VisibleForTesting
	public static void resetURLWhitelistPattern() {
		urlWhitelistPattern = null;
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
							spcBrick.replaceFormulaBrickField(BrickField.PHIRO_LIGHT_RED, BrickField.PEN_COLOR_RED);
							spcBrick.replaceFormulaBrickField(BrickField.PHIRO_LIGHT_GREEN, BrickField.PEN_COLOR_GREEN);
							spcBrick.replaceFormulaBrickField(BrickField.PHIRO_LIGHT_BLUE, BrickField.PEN_COLOR_BLUE);
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

	public String getCurrentProjectName() {
		if (getCurrentProject() == null) {

			if (FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).size() == 0) {
				initializeDefaultProject();
			}

			SharedPreferences sharedPreferences =
					PreferenceManager.getDefaultSharedPreferences(applicationContext);
			String currentProjectName = sharedPreferences.getString(Constants.PREF_PROJECTNAME_KEY, null);
			if (currentProjectName == null
					|| !FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).contains(currentProjectName)) {
				currentProjectName = FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).get(0);
			}
			return currentProjectName;
		}
		return getCurrentProject().getName();
	}

	public void saveCurrentProjectName() {
		if (getCurrentProject() == null) {
			PreferenceManager.getDefaultSharedPreferences(applicationContext)
					.edit()
					.remove(PREF_PROJECTNAME_KEY)
					.apply();
		} else {
			PreferenceManager.getDefaultSharedPreferences(applicationContext)
					.edit()
					.putString(PREF_PROJECTNAME_KEY, getCurrentProject().getName())
					.apply();
		}
	}

	@SuppressWarnings({"WeakerAccess"})
	public boolean initializeDefaultProject() {
		try {
			project = defaultProjectHandler.createAndSaveDefaultProject();
			currentSprite = null;
			currentlyEditedScene = project.getDefaultScene();
			currentlyPlayingScene = currentlyEditedScene;
			return true;
		} catch (IOException ioException) {
			Log.e(TAG, "Cannot initialize default project.", ioException);
			return false;
		}
	}

	/**
	 * @deprecated use {@link #initializeDefaultProject()} without Context instead.
	 */
	@Deprecated
	public boolean initializeDefaultProject(Context context) {
		return initializeDefaultProject();
	}

	@SuppressWarnings({"WeakerAccess"})
	public void createNewEmptyProject(String name, boolean landscapeMode, boolean castEnabled) throws IOException {
		project = defaultProjectHandler.createAndSaveEmptyProject(name, landscapeMode, castEnabled);
		currentSprite = null;
		currentlyEditedScene = project.getDefaultScene();
		currentlyPlayingScene = currentlyEditedScene;
	}

	/**
	 * @deprecated use {@link #createNewEmptyProject(String, boolean, boolean)} ()} without Context instead.
	 */
	@Deprecated
	public void createNewEmptyProject(String name, Context context, boolean landscapeMode, boolean castEnabled) throws IOException {
		createNewEmptyProject(name, landscapeMode, castEnabled);
	}

	@SuppressWarnings({"WeakerAccess"})
	public void createNewExampleProject(String name, ProjectCreatorType projectCreatorType, boolean landscapeMode) throws IOException {
		defaultProjectHandler.setDefaultProjectCreator(projectCreatorType);
		project = defaultProjectHandler.createAndSaveDefaultProject(name, landscapeMode);
		currentSprite = null;
		currentlyEditedScene = project.getDefaultScene();
		currentlyPlayingScene = currentlyEditedScene;
	}

	/**
	 * @deprecated use {@link #createNewExampleProject(String, ProjectCreatorType, boolean)} ()} without Context instead.
	 */
	@Deprecated
	public void createNewExampleProject(String name, Context context, ProjectCreatorType projectCreatorType, boolean landscapeMode) throws IOException {
		createNewExampleProject(name, projectCreatorType, landscapeMode);
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
		currentlyEditedScene = scene;
		currentlyPlayingScene = scene;
	}
}
