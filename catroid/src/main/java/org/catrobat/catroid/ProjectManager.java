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
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.common.DefaultProjectHandler.ProjectCreatorType;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.LegoEV3Setting;
import org.catrobat.catroid.content.LegoNXTSetting;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Setting;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.backwardcompatibility.BrickTreeBuilder;
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.Brick.BrickField;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.physics.PhysicsCollision;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.catrobat.catroid.common.Constants.CURRENT_CATROBAT_LANGUAGE_VERSION;

public final class ProjectManager {

	private static final ProjectManager INSTANCE = new ProjectManager();
	private static final String TAG = ProjectManager.class.getSimpleName();

	private Project project;
	private Scene currentlyEditedScene;
	private Scene currentlyPlayingScene;
	private Scene startScene;
	private Sprite currentSprite;

	private ProjectManager() {
	}

	public static ProjectManager getInstance() {
		return INSTANCE;
	}

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
		if (project.getCatrobatLanguageVersion() == 0.92f) {
			project.setCatrobatLanguageVersion(0.93f);
		}
		if (project.getCatrobatLanguageVersion() == 0.93f) {
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
			project.setCatrobatLanguageVersion(0.992f);
		}
		if (project.getCatrobatLanguageVersion() == 0.992f) {
			updateCollisionFormulasTo993(project);
			project.setCatrobatLanguageVersion(0.993f);
		}
		if (project.getCatrobatLanguageVersion() == 0.993f) {
			updateSetPenColorFormulasTo994(project);
			project.setCatrobatLanguageVersion(0.994f);
		}
		if (project.getCatrobatLanguageVersion() == 0.994f) {
			updateArduinoValuesTo995(project);
			project.setCatrobatLanguageVersion(0.995f);
		}
		if (project.getCatrobatLanguageVersion() == 0.995f) {
			updateCollisionScriptsTo996(project);
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
			makeShallowCopiesDeepAgain(project);
			project.setCatrobatLanguageVersion(0.9991f);
		}
		if (project.getCatrobatLanguageVersion() == 0.9991f) {
			project.setCatrobatLanguageVersion(0.9992f);
			project.setCatrobatLanguageVersion(0.9992f);
		}
		if (project.getCatrobatLanguageVersion() == 0.9992f) {
			project.setCatrobatLanguageVersion(0.9993f);
		}
		if (project.getCatrobatLanguageVersion() == 0.9993f) {
			updateScriptsToTreeStructure(project);
			project.setCatrobatLanguageVersion(0.9994f);
		}
		if (project.getCatrobatLanguageVersion() == 0.9994f) {
			project.setCatrobatLanguageVersion(CURRENT_CATROBAT_LANGUAGE_VERSION);
		}

		if (project.getCatrobatLanguageVersion() != CURRENT_CATROBAT_LANGUAGE_VERSION) {
			restorePreviousProject(previousProject);
			throw new CompatibilityProjectException(context.getString(R.string.error_project_compatibility));
		}

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

	private void restorePreviousProject(Project previousProject) {
		project = previousProject;
		if (previousProject != null) {
			currentlyPlayingScene = project.getDefaultScene();
		}
	}

	private static void makeShallowCopiesDeepAgain(Project project) {
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


	private void localizeBackgroundSprite(Context context) {

		List<Scene> allScenes = getCurrentProject().getSceneList();
		for (Scene scene: allScenes) {
			if (scene.getSpriteList().size() > 0) {
				scene.getSpriteList().get(0).setName(context.getString(R.string.background));
				scene.getSpriteList().get(0).look.setZIndex(0);
			}
		}

		if (currentlyEditedScene.getSpriteList().size() > 0) {
			currentlyEditedScene.getSpriteList().get(0).setName(context.getString(R.string.background));
			currentlyEditedScene.getSpriteList().get(0).look.setZIndex(0);
    }
  }

	private static void initializeScripts(Project project) {
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Script script : sprite.getScriptList()) {
					script.setParents();

					if (script instanceof CollisionScript) {
						((CollisionScript) script).updateSpriteToCollideWith(scene);
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

	@VisibleForTesting
	public static void updateCollisionFormulasTo993(Project project) {
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Script script : sprite.getScriptList()) {
					Brick scriptBrick = script.getScriptBrick();
					if (scriptBrick instanceof FormulaBrick) {
						FormulaBrick formulaBrick = (FormulaBrick) scriptBrick;
						for (Formula formula : formulaBrick.getFormulas()) {
							formula.updateCollisionFormulasToVersion();
						}
					}
					for (Brick brick : script.getBrickList()) {
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

	private static void updateSetPenColorFormulasTo994(Project project) {
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

	private static void updateArduinoValuesTo995(Project project) {
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

	private static void updateCollisionScriptsTo996(Project project) {
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Script script : sprite.getScriptList()) {
					if (script instanceof CollisionScript) {
						CollisionScript collisionScript = (CollisionScript) script;
						String[] spriteNames = collisionScript.getSpriteToCollideWithName().split(PhysicsCollision.COLLISION_MESSAGE_CONNECTOR);
						String spriteToCollideWith = spriteNames[0];
						if (spriteNames[0].equals(sprite.getName())) {
							spriteToCollideWith = spriteNames[1];
						}
						collisionScript.setSpriteToCollideWithName(spriteToCollideWith);
					}
				}
			}
		}
	}

	private static void updateScriptsToTreeStructure(Project project) {
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

	public void createNewEmptyProject(String name, Context context, boolean landscapeMode, boolean castEnabled) throws IOException {
		project = DefaultProjectHandler.createAndSaveEmptyProject(name, context, landscapeMode, castEnabled);
		currentSprite = null;
		currentlyEditedScene = project.getDefaultScene();
		currentlyPlayingScene = currentlyEditedScene;
	}

	public void createNewExampleProject(String name, Context context, ProjectCreatorType projectCreatorType, boolean landscapeMode) throws IOException {
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
