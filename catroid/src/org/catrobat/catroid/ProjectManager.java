/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.io.LoadProjectTask;
import org.catrobat.catroid.io.LoadProjectTask.OnLoadProjectCompleteListener;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.transfers.CheckTokenTask;
import org.catrobat.catroid.transfers.CheckTokenTask.OnCheckTokenCompleteListener;
import org.catrobat.catroid.ui.dialogs.LoginRegisterDialog;
import org.catrobat.catroid.ui.dialogs.UploadProjectDialog;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public final class ProjectManager implements OnLoadProjectCompleteListener, OnCheckTokenCompleteListener {
	private static final ProjectManager INSTANCE = new ProjectManager();
	private static final String TAG = ProjectManager.class.getSimpleName();

	private Project project;
	private Script currentScript;
	private Sprite currentSprite;
	private UserBrick currentUserBrick;
	private boolean asynchronTask = true;

	private FileChecksumContainer fileChecksumContainer = new FileChecksumContainer();

	private ProjectManager() {
	}

	public static ProjectManager getInstance() {
		return INSTANCE;
	}

	public void uploadProject(String projectName, FragmentActivity fragmentActivity) {
		if (getCurrentProject() == null || !getCurrentProject().getName().equals(projectName)) {
			LoadProjectTask loadProjectTask = new LoadProjectTask(fragmentActivity, projectName, false, false);
			loadProjectTask.setOnLoadProjectCompleteListener(this);
			loadProjectTask.execute();
		}
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(fragmentActivity);
		String token = preferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		String username = preferences.getString(Constants.USERNAME, Constants.NO_USERNAME);

		if (token.equals(Constants.NO_TOKEN) || token.length() != ServerCalls.TOKEN_LENGTH
				|| token.equals(ServerCalls.TOKEN_CODE_INVALID)) {
			showLoginRegisterDialog(fragmentActivity);
		} else {
			CheckTokenTask checkTokenTask = new CheckTokenTask(fragmentActivity, token, username);
			checkTokenTask.setOnCheckTokenCompleteListener(this);
			checkTokenTask.execute();
		}
	}

	public void loadProject(String projectName, Context context) throws LoadingProjectException,
			OutdatedVersionProjectException, CompatibilityProjectException {
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
					} catch (IOException ioException) {
						Log.e(TAG, "Cannot load project.", ioException);
						throw new LoadingProjectException(context.getString(R.string.error_load_project));
					}
				}
			}
			throw new LoadingProjectException(context.getString(R.string.error_load_project));
		} else if (project.getCatrobatLanguageVersion() > Constants.CURRENT_CATROBAT_LANGUAGE_VERSION) {
			project = oldProject;
			throw new OutdatedVersionProjectException(context.getString(R.string.error_outdated_pocketcode_version));
		} else {
			if (project.getCatrobatLanguageVersion() == 0.8f) {
				//TODO insert in every "When project starts" script list a "show" brick
				project.setCatrobatLanguageVersion(0.9f);
			}
			if (project.getCatrobatLanguageVersion() == 0.9f) {
				project.setCatrobatLanguageVersion(0.91f);
				//no convertion needed - only change to white background
			}
			if (project.getCatrobatLanguageVersion() == 0.91f) {
				project.setCatrobatLanguageVersion(0.92f);
				project.setScreenMode(ScreenModes.STRETCH);
				checkNestingBrickReferences(false);
			}
			if (project.getCatrobatLanguageVersion() == 0.92f) {
				project.setCatrobatLanguageVersion(0.93f);
			}

			//insert further conversions here

			checkNestingBrickReferences(true);
			if (project.getCatrobatLanguageVersion() == Constants.CURRENT_CATROBAT_LANGUAGE_VERSION) {
				//project seems to be converted now and can be loaded
				localizeBackgroundSprite(context);
			} else {
				//project cannot be converted
				project = oldProject;
				throw new CompatibilityProjectException(context.getString(R.string.error_project_compatability));
			}
		}

	}

	private void localizeBackgroundSprite(Context context) {
		// Set generic localized name on background sprite and move it to the back.
		if (project.getSpriteList().size() > 0) {
			project.getSpriteList().get(0).setName(context.getString(R.string.background));
			project.getSpriteList().get(0).look.setZIndex(0);
		}
		MessageContainer.clearBackup();
		currentSprite = null;
		currentScript = null;
		Utils.saveToPreferences(context, Constants.PREF_PROJECTNAME_KEY, project.getName());
	}

	public boolean cancelLoadProject() {
		return StorageHandler.getInstance().cancelLoadProject();
	}

	public boolean canLoadProject(String projectName) {
		return StorageHandler.getInstance().loadProject(projectName) != null;
	}

	public void saveProject() {
		if (project == null) {
			return;
		}

		if (asynchronTask) {
			SaveProjectAsynchronousTask saveTask = new SaveProjectAsynchronousTask();
			saveTask.execute();
		} else {
			StorageHandler.getInstance().saveProject(project);
		}
	}

	public boolean initializeDefaultProject(Context context) {
		try {
			fileChecksumContainer = new FileChecksumContainer();
			project = StandardProjectHandler.createAndSaveStandardProject(context);

			currentSprite = null;
			currentScript = null;
			return true;
		} catch (IOException ioException) {
			Log.e(TAG, "Cannot initialize default project.", ioException);
			Utils.showErrorDialog(context, R.string.error_load_project);
			return false;
		}
	}

	public boolean initializeDroneProject(Context context) {
		try {
			fileChecksumContainer = new FileChecksumContainer();
			project = StandardProjectHandler.createAndSaveStandardDroneProject(context);

			currentSprite = null;
			currentScript = null;
			return true;
		} catch (IOException ioException) {
			Log.e(TAG, "Cannot initialize default project.", ioException);
			Utils.showErrorDialog(context, R.string.error_load_project);
			return false;
		}
	}

	public void initializeNewProject(String projectName, Context context, boolean empty)
			throws IllegalArgumentException, IOException {
		fileChecksumContainer = new FileChecksumContainer();

		if (empty) {
			project = StandardProjectHandler.createAndSaveEmptyProject(projectName, context);
		} else {
			project = StandardProjectHandler.createAndSaveStandardProject(projectName, context);
		}

		currentSprite = null;
		currentScript = null;
	}

	public Project getCurrentProject() {
		return project;
	}

	public void setProject(Project project) {
		currentScript = null;
		currentSprite = null;

		this.project = project;
	}

	@Deprecated
	public void deleteCurrentProject(Context context) throws IllegalArgumentException, IOException {
		deleteProject(project.getName(), context);
	}


	public void deleteProject(String projectName, Context context) throws IllegalArgumentException, IOException {
		Log.d(TAG, "deleteProject " + projectName);
		if (StorageHandler.getInstance().projectExists(projectName)) {
			StorageHandler.getInstance().deleteProject(projectName);
		}

		if (project != null && project.getName().equals(projectName)) {
			Log.d(TAG, "deleteProject(): project instance set to null");

			project = null;

			if (context != null) {
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
				String currentProjectName = sharedPreferences.getString(Constants.PREF_PROJECTNAME_KEY, "notFound");
				if (!currentProjectName.equals("notFound")) {
					Utils.removeFromPreferences(context, Constants.PREF_PROJECTNAME_KEY);
				}
			}
		}
	}

	public boolean renameProject(String newProjectName, Context context) {
		if (StorageHandler.getInstance().projectExists(newProjectName)) {
			Utils.showErrorDialog(context, R.string.error_project_exists);
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
			saveProject();
		}

		if (!directoryRenamed) {
			Utils.showErrorDialog(context, R.string.error_rename_project);
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

	public UserBrick getCurrentUserBrick() {
		return currentUserBrick;
	}

	public void setCurrentUserBrick(UserBrick brick) {
		currentUserBrick = brick;
	}

	public void addSprite(Sprite sprite) {
		project.addSprite(sprite);
	}

	public void addScript(Script script) {
		currentSprite.addScript(script);
	}

	public boolean spriteExists(String spriteName) {
		for (Sprite sprite : project.getSpriteList()) {
			if (sprite.getName().equalsIgnoreCase(spriteName)) {
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

	public FileChecksumContainer getFileChecksumContainer() {
		return this.fileChecksumContainer;
	}

	public void setFileChecksumContainer(FileChecksumContainer fileChecksumContainer) {
		this.fileChecksumContainer = fileChecksumContainer;
	}

	@Override
	public void onTokenNotValid(FragmentActivity fragmentActivity) {
		showLoginRegisterDialog(fragmentActivity);
	}

	@Override
	public void onCheckTokenSuccess(FragmentActivity fragmentActivity) {
		UploadProjectDialog uploadProjectDialog = new UploadProjectDialog();
		uploadProjectDialog.show(fragmentActivity.getSupportFragmentManager(), UploadProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	private void showLoginRegisterDialog(FragmentActivity fragmentActivity) {
		LoginRegisterDialog loginRegisterDialog = new LoginRegisterDialog();
		loginRegisterDialog.show(fragmentActivity.getSupportFragmentManager(), LoginRegisterDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void onLoadProjectSuccess(boolean startProjectActivity) {

	}

	@Override
	public void onLoadProjectFailure() {

	}

	public void checkNestingBrickReferences(boolean assumeWrong) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		if (currentProject != null) {
			for (Sprite currentSprite : currentProject.getSpriteList()) {
				int numberOfScripts = currentSprite.getNumberOfScripts();
				for (int pos = 0; pos < numberOfScripts; pos++) {
					Script script = currentSprite.getScript(pos);
					boolean scriptCorrect = true;
					if (assumeWrong) {
						scriptCorrect = false;
					}
					for (Brick currentBrick : script.getBrickList()) {
						if (!scriptCorrect) {
							continue;
						}
						scriptCorrect = checkReferencesOfCurrentBrick(currentBrick);
					}
					if (!scriptCorrect) {
						correctAllNestedReferences(script);
					}
				}
			}
		}
	}

	private boolean checkReferencesOfCurrentBrick(Brick currentBrick) {
		if (currentBrick instanceof IfLogicBeginBrick) {
			IfLogicElseBrick elseBrick = ((IfLogicBeginBrick) currentBrick).getIfElseBrick();
			IfLogicEndBrick endBrick = ((IfLogicBeginBrick) currentBrick).getIfEndBrick();
			if (elseBrick == null || endBrick == null || elseBrick.getIfBeginBrick() == null
					|| elseBrick.getIfEndBrick() == null || endBrick.getIfBeginBrick() == null
					|| endBrick.getIfElseBrick() == null
					|| !elseBrick.getIfBeginBrick().equals(currentBrick)
					|| !elseBrick.getIfEndBrick().equals(endBrick)
					|| !endBrick.getIfBeginBrick().equals(currentBrick)
					|| !endBrick.getIfElseBrick().equals(elseBrick)) {
				Log.d("REFERENCE ERROR!!", "Brick has wrong reference:" + currentSprite + " "
						+ currentBrick);
				return false;
			}
		} else if (currentBrick instanceof LoopBeginBrick) {
			LoopEndBrick endBrick = ((LoopBeginBrick) currentBrick).getLoopEndBrick();
			if (endBrick == null || endBrick.getLoopBeginBrick() == null
					|| !endBrick.getLoopBeginBrick().equals(currentBrick)) {
				Log.d("REFERENCE ERROR!!", "Brick has wrong reference:" + currentSprite + " "
						+ currentBrick);
				return false;
			}
		}
		return true;
	}

	private void correctAllNestedReferences(Script script) {
		ArrayList<IfLogicBeginBrick> ifBeginList = new ArrayList<IfLogicBeginBrick>();
		ArrayList<LoopBeginBrick> loopBeginList = new ArrayList<LoopBeginBrick>();
		for (Brick currentBrick : script.getBrickList()) {
			if (currentBrick instanceof IfLogicBeginBrick) {
				ifBeginList.add((IfLogicBeginBrick) currentBrick);
			} else if (currentBrick instanceof LoopBeginBrick) {
				loopBeginList.add((LoopBeginBrick) currentBrick);
			} else if (currentBrick instanceof LoopEndBrick) {
				LoopBeginBrick loopBeginBrick = loopBeginList.get(loopBeginList.size() - 1);
				loopBeginBrick.setLoopEndBrick((LoopEndBrick) currentBrick);
				((LoopEndBrick) currentBrick).setLoopBeginBrick(loopBeginBrick);
				loopBeginList.remove(loopBeginBrick);
			} else if (currentBrick instanceof IfLogicElseBrick) {
				IfLogicBeginBrick ifBeginBrick = ifBeginList.get(ifBeginList.size() - 1);
				ifBeginBrick.setIfElseBrick((IfLogicElseBrick) currentBrick);
				((IfLogicElseBrick) currentBrick).setIfBeginBrick(ifBeginBrick);
			} else if (currentBrick instanceof IfLogicEndBrick) {
				IfLogicBeginBrick ifBeginBrick = ifBeginList.get(ifBeginList.size() - 1);
				IfLogicElseBrick elseBrick = ifBeginBrick.getIfElseBrick();
				ifBeginBrick.setIfEndBrick((IfLogicEndBrick) currentBrick);
				elseBrick.setIfEndBrick((IfLogicEndBrick) currentBrick);
				((IfLogicEndBrick) currentBrick).setIfBeginBrick(ifBeginBrick);
				((IfLogicEndBrick) currentBrick).setIfElseBrick(elseBrick);
				ifBeginList.remove(ifBeginBrick);
			}
		}
	}

	private class SaveProjectAsynchronousTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			StorageHandler.getInstance().saveProject(project);
			return null;
		}
	}
}
