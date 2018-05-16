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
import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.PathBuilder;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class ProjectManager {

	private static final ProjectManager INSTANCE = new ProjectManager();
	private static final String TAG = ProjectManager.class.getSimpleName();

	private Project project;
	private Scene currentScene;
	private Scene sceneToPlay;
	private Scene startScene;
	private Script currentScript;
	private Sprite currentSprite;
	private UserBrick currentUserBrick;

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
			//TODO insert in every "When project starts" script list a "showDialog" brick
			project.setCatrobatLanguageVersion(0.9f);
		}
		if (project.getCatrobatLanguageVersion() == 0.9f) {
			project.setCatrobatLanguageVersion(0.91f);
			//no convertion needed - only change to white background
		}
		if (project.getCatrobatLanguageVersion() == 0.91f) {
			project.setCatrobatLanguageVersion(0.92f);
			project.setScreenMode(ScreenModes.STRETCH);
			checkNestingBrickReferences(false, false);
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
			project.setCatrobatLanguageVersion(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION);
		}

		//insert further conversions here

		makeShallowCopiesDeepAgain(project);
		checkNestingBrickReferences(true, false);

		if (project.getCatrobatLanguageVersion() == Constants.CURRENT_CATROBAT_LANGUAGE_VERSION) {
			localizeBackgroundSprite(context);
		} else {
			restorePreviousProject(previousProject);
			throw new CompatibilityProjectException(context.getString(R.string.error_project_compatability));
		}

		project.loadLegoNXTSettingsFromProject(context);
		project.loadLegoEV3SettingsFromProject(context);

		int resources = project.getRequiredResources();

		if ((resources & Brick.BLUETOOTH_PHIRO) > 0) {
			SettingsFragment.setPhiroSharedPreferenceEnabled(context, true);
		}

		if ((resources & Brick.JUMPING_SUMO) > 0) {
			SettingsFragment.setJumpingSumoSharedPreferenceEnabled(context, true);
		}

		if ((resources & Brick.BLUETOOTH_SENSORS_ARDUINO) > 0) {
			SettingsFragment.setArduinoSharedPreferenceEnabled(context, true);
		}

		sceneToPlay = project.getDefaultScene();
	}

	private void restorePreviousProject(Project previousProject) {
		project = previousProject;
		if (previousProject != null) {
			sceneToPlay = project.getDefaultScene();
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
		// Set generic localized name on background sprite and move it to the back.
		if (currentScene == null) {
			return;
		}
		if (currentScene.getSpriteList().size() > 0) {
			currentScene.getSpriteList().get(0).setName(context.getString(R.string.background));
			currentScene.getSpriteList().get(0).look.setZIndex(0);
		}
		currentSprite = null;
		currentScript = null;
		Utils.saveToPreferences(context, Constants.PREF_PROJECTNAME_KEY, project.getName());
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
			currentScript = null;
			currentScene = project.getDefaultScene();
			sceneToPlay = currentScene;
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
		currentScript = null;
		currentScene = project.getDefaultScene();
		sceneToPlay = currentScene;
	}

	public Project getCurrentProject() {
		return project;
	}

	public Scene getSceneToPlay() {
		if (sceneToPlay == null) {
			sceneToPlay = getCurrentScene();
		}
		return sceneToPlay;
	}

	public void setSceneToPlay(Scene scene) {
		sceneToPlay = scene;
	}

	public Scene getStartScene() {
		if (startScene == null) {
			startScene = getCurrentScene();
		}
		return startScene;
	}

	public void setStartScene(Scene scene) {
		startScene = scene;
	}

	public Scene getCurrentScene() {
		if (currentScene == null) {
			currentScene = project.getDefaultScene();
		}
		return currentScene;
	}

	public boolean isCurrentProjectLandscapeMode() {
		int virtualScreenWidth = getCurrentProject().getXmlHeader().virtualScreenWidth;
		int virtualScreenHeight = getCurrentProject().getXmlHeader().virtualScreenHeight;

		return virtualScreenWidth > virtualScreenHeight;
	}

	public void setProject(Project project) {
		currentScript = null;
		currentSprite = null;

		this.project = project;
		if (project != null) {
			currentScene = project.getDefaultScene();
			sceneToPlay = currentScene;
		}
	}

	public void setCurrentProject(Project project) {
		this.project = project;
	}

	public boolean renameProject(String newProjectName, Context context) {
		if (XstreamSerializer.getInstance().projectExists(newProjectName)) {
			return false;
		}

		String oldProjectPath = PathBuilder.buildProjectPath(project.getName());
		File oldProjectDirectory = new File(oldProjectPath);

		String newProjectPath = PathBuilder.buildProjectPath(newProjectName);
		File newProjectDirectory = new File(newProjectPath);

		boolean directoryRenamed;

		if (oldProjectPath.equalsIgnoreCase(newProjectPath)) {
			String tmpProjectPath = PathBuilder.buildProjectPath(createTemporaryDirectoryName(newProjectName));
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
			saveProject(context);
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

	public void setCurrentScene(Scene scene) {
		this.currentScene = scene;
		sceneToPlay = scene;
	}

	public void setCurrentScript(Script script) {
		if (script == null) {
			currentScript = null;
		} else if (currentSprite.getScriptIndex(script) != -1) {
			currentScript = script;
		}
	}

	public UserBrick getCurrentUserBrick() {
		return currentUserBrick;
	}

	public void setCurrentUserBrick(UserBrick brick) {
		currentUserBrick = brick;
	}

	public int getCurrentSpritePosition() {
		return getCurrentScene().getSpriteList().indexOf(currentSprite);
	}

	private String createTemporaryDirectoryName(String projectDirectoryName) {
		String temporaryDirectorySuffix = "_tmp";
		String temporaryDirectoryName = projectDirectoryName + temporaryDirectorySuffix;
		int suffixCounter = 0;
		while (Utils.checkIfProjectExistsOrIsDownloadingIgnoreCase(temporaryDirectoryName)) {
			temporaryDirectoryName = projectDirectoryName + temporaryDirectorySuffix + suffixCounter;
			suffixCounter++;
		}
		return temporaryDirectoryName;
	}

//	private void triggerFacebookTokenRefreshOnServer(Activity activity) {
//		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
//		sharedPreferences.edit().putBoolean(Constants.FACEBOOK_TOKEN_REFRESH_NEEDED, true);
//		FacebookExchangeTokenTask facebookExchangeTokenTask = new FacebookExchangeTokenTask(activity,
//				AccessToken.getCurrentAccessToken().getToken(),
//				sharedPreferences.getString(Constants.FACEBOOK_EMAIL, Constants.NO_FACEBOOK_EMAIL),
//				sharedPreferences.getString(Constants.FACEBOOK_USERNAME, Constants.NO_FACEBOOK_USERNAME),
//				sharedPreferences.getString(Constants.FACEBOOK_ID, Constants.NO_FACEBOOK_ID),
//				sharedPreferences.getString(Constants.FACEBOOK_LOCALE, Constants.NO_FACEBOOK_LOCALE)
//		);
//		facebookExchangeTokenTask.setOnFacebookExchangeTokenCompleteListener(this);
//		facebookExchangeTokenTask.execute();
//	}

	public boolean checkNestingBrickReferences(boolean assumeWrong, boolean inBackPack) {
		boolean projectCorrect = true;
		if (inBackPack) {

			List<Sprite> spritesToCheck = BackpackListManager.getInstance().getBackpackedSprites();

			HashMap<String, List<Script>> backPackedScripts = BackpackListManager.getInstance().getBackpackedScripts();
			for (String scriptGroup : backPackedScripts.keySet()) {
				List<Script> scriptListToCheck = backPackedScripts.get(scriptGroup);
				for (Script scriptToCheck : scriptListToCheck) {
					checkCurrentScript(scriptToCheck, assumeWrong);
				}
			}

			for (Sprite currentSprite : spritesToCheck) {
				if (!checkCurrentSprite(currentSprite, assumeWrong)) {
					projectCorrect = false;
				}
			}
		} else {
			for (Scene scene : project.getSceneList()) {
				if (ProjectManager.getInstance().getCurrentProject() == null) {
					return false;
				}

				for (Sprite currentSprite : scene.getSpriteList()) {
					if (!checkCurrentSprite(currentSprite, assumeWrong)) {
						projectCorrect = false;
					}
				}
			}
		}
		return projectCorrect;
	}

	public boolean checkCurrentSprite(Sprite currentSprite, boolean assumeWrong) {
		boolean spriteCorrect = true;
		int numberOfScripts = currentSprite.getNumberOfScripts();
		for (int pos = 0; pos < numberOfScripts; pos++) {
			Script script = currentSprite.getScript(pos);
			if (!checkCurrentScript(script, assumeWrong)) {
				spriteCorrect = false;
			}
		}
		return spriteCorrect;
	}

	private boolean checkCurrentScript(Script script, boolean assumeWrong) {
		boolean scriptCorrect = true;
		if (assumeWrong) {
			scriptCorrect = false;
		}
		for (Brick currentBrick : script.getBrickList()) {
			if (!scriptCorrect) {
				break;
			}
			scriptCorrect = checkReferencesOfCurrentBrick(currentBrick);
		}
		if (!scriptCorrect) {
			correctAllNestedReferences(script);
		}
		return scriptCorrect;
	}

	private boolean checkReferencesOfCurrentBrick(Brick currentBrick) {
		if (currentBrick instanceof IfThenLogicBeginBrick) {
			IfThenLogicEndBrick endBrick = ((IfThenLogicBeginBrick) currentBrick).getIfThenEndBrick();
			if (endBrick == null || endBrick.getIfBeginBrick() == null
					|| !endBrick.getIfBeginBrick().equals(currentBrick)) {
				Log.d(TAG, "Brick has wrong reference:" + currentSprite + " "
						+ currentBrick);
				return false;
			}
		} else if (currentBrick instanceof IfLogicBeginBrick) {
			IfLogicElseBrick elseBrick = ((IfLogicBeginBrick) currentBrick).getIfElseBrick();
			IfLogicEndBrick endBrick = ((IfLogicBeginBrick) currentBrick).getIfEndBrick();
			if (elseBrick == null || endBrick == null || elseBrick.getIfBeginBrick() == null
					|| elseBrick.getIfEndBrick() == null || endBrick.getIfBeginBrick() == null
					|| endBrick.getIfElseBrick() == null
					|| !elseBrick.getIfBeginBrick().equals(currentBrick)
					|| !elseBrick.getIfEndBrick().equals(endBrick)
					|| !endBrick.getIfBeginBrick().equals(currentBrick)
					|| !endBrick.getIfElseBrick().equals(elseBrick)) {
				Log.d(TAG, "Brick has wrong reference:" + currentSprite + " "
						+ currentBrick);
				return false;
			}
		} else if (currentBrick instanceof LoopBeginBrick) {
			LoopEndBrick endBrick = ((LoopBeginBrick) currentBrick).getLoopEndBrick();
			if (endBrick == null || endBrick.getLoopBeginBrick() == null
					|| !endBrick.getLoopBeginBrick().equals(currentBrick)) {
				Log.d(TAG, "Brick has wrong reference:" + currentSprite + " "
						+ currentBrick);
				return false;
			}
		}
		return true;
	}

	private void correctAllNestedReferences(Script script) {
		ArrayList<IfLogicBeginBrick> ifBeginList = new ArrayList<>();
		ArrayList<IfThenLogicBeginBrick> ifThenBeginList = new ArrayList<>();
		ArrayList<Brick> loopBeginList = new ArrayList<>();
		ArrayList<Brick> bricksWithInvalidReferences = new ArrayList<>();

		for (Brick currentBrick : script.getBrickList()) {
			if (currentBrick instanceof IfThenLogicBeginBrick) {
				ifThenBeginList.add((IfThenLogicBeginBrick) currentBrick);
			} else if (currentBrick instanceof IfLogicBeginBrick) {
				ifBeginList.add((IfLogicBeginBrick) currentBrick);
			} else if (currentBrick instanceof LoopBeginBrick) {
				loopBeginList.add(currentBrick);
			} else if (currentBrick instanceof LoopEndBrick) {
				if (loopBeginList.isEmpty()) {
					Log.e(TAG, "Removing LoopEndBrick without reference to a LoopBeginBrick");
					bricksWithInvalidReferences.add(currentBrick);
					continue;
				}
				LoopBeginBrick loopBeginBrick = (LoopBeginBrick) loopBeginList.get(loopBeginList.size() - 1);
				loopBeginBrick.setLoopEndBrick((LoopEndBrick) currentBrick);
				((LoopEndBrick) currentBrick).setLoopBeginBrick(loopBeginBrick);
				loopBeginList.remove(loopBeginBrick);
			} else if (currentBrick instanceof IfLogicElseBrick) {
				if (ifBeginList.isEmpty()) {
					Log.e(TAG, "Removing IfLogicElseBrick without reference to an IfBeginBrick");
					bricksWithInvalidReferences.add(currentBrick);
					continue;
				}
				IfLogicBeginBrick ifBeginBrick = ifBeginList.get(ifBeginList.size() - 1);
				ifBeginBrick.setIfElseBrick((IfLogicElseBrick) currentBrick);
				((IfLogicElseBrick) currentBrick).setIfBeginBrick(ifBeginBrick);
			} else if (currentBrick instanceof IfThenLogicEndBrick) {
				if (ifThenBeginList.isEmpty()) {
					Log.e(TAG, "Removing IfThenLogicEndBrick without reference to an IfBeginBrick");
					bricksWithInvalidReferences.add(currentBrick);
					continue;
				}
				IfThenLogicBeginBrick ifBeginBrick = ifThenBeginList.get(ifThenBeginList.size() - 1);
				ifBeginBrick.setIfThenEndBrick((IfThenLogicEndBrick) currentBrick);
				((IfThenLogicEndBrick) currentBrick).setIfThenBeginBrick(ifBeginBrick);
				ifThenBeginList.remove(ifBeginBrick);
			} else if (currentBrick instanceof IfLogicEndBrick) {
				if (ifBeginList.isEmpty()) {
					Log.e(TAG, "Removing IfLogicEndBrick without reference to an IfBeginBrick");
					bricksWithInvalidReferences.add(currentBrick);
					continue;
				}
				IfLogicBeginBrick ifBeginBrick = ifBeginList.get(ifBeginList.size() - 1);
				IfLogicElseBrick elseBrick = ifBeginBrick.getIfElseBrick();
				ifBeginBrick.setIfEndBrick((IfLogicEndBrick) currentBrick);
				elseBrick.setIfEndBrick((IfLogicEndBrick) currentBrick);
				((IfLogicEndBrick) currentBrick).setIfBeginBrick(ifBeginBrick);
				((IfLogicEndBrick) currentBrick).setIfElseBrick(elseBrick);
				ifBeginList.remove(ifBeginBrick);
			}
		}

		bricksWithInvalidReferences.addAll(ifBeginList);
		bricksWithInvalidReferences.addAll(ifThenBeginList);
		bricksWithInvalidReferences.addAll(loopBeginList);

		for (Brick brick : bricksWithInvalidReferences) {
			script.removeBrick(brick);
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
