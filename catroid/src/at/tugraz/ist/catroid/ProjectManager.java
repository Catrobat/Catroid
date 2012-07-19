/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.common.MessageContainer;
import at.tugraz.ist.catroid.common.StandardProjectHandler;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.Utils;

public class ProjectManager {

	private Project mProject;
	private Script mCurrentScript;
	private Sprite mCurrentSprite;
	private static final ProjectManager INSTANCE = new ProjectManager();

	public FileChecksumContainer fileChecksumContainer;
	public MessageContainer messageContainer;

	private ProjectManager() {
		fileChecksumContainer = new FileChecksumContainer();
		messageContainer = new MessageContainer();
	}

	public static ProjectManager getInstance() {
		return INSTANCE;
	}

	public boolean loadProject(String projectName, Context context, boolean errorMessage) {
		try {
			fileChecksumContainer = new FileChecksumContainer();
			messageContainer = new MessageContainer();

			mProject = StorageHandler.getInstance().loadProject(projectName);

			if (errorMessage && mProject.getCatroidVersionCode() > Utils.getVersionCode(context)) {
				Utils.displayErrorMessage(context, context.getString(R.string.error_project_compatability));
				return false;
				// TODO show dialog to download latest catroid version instead
			}

			if (mProject == null) {
				mProject = Utils.findValidProject();
				if (mProject == null) {
					mProject = StandardProjectHandler.createAndSaveStandardProject(context);
				}

				if (errorMessage) {
					Utils.displayErrorMessage(context, context.getString(R.string.error_load_project));
					return false;
				}
			}
			// adapt name of background sprite to the current language and place
			// on lowest layer
			mProject.getSpriteList().get(0).setName(context.getString(R.string.background));
			mProject.getSpriteList().get(0).costume.zPosition = Integer.MIN_VALUE;

			mCurrentSprite = null;
			mCurrentScript = null;

			Utils.saveToPreferences(context, Constants.PREF_PROJECTNAME_KEY, mProject.getName());

			return true;
		} catch (Exception e) {
			Utils.displayErrorMessage(context, context.getString(R.string.error_load_project));
			return false;
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

	public void saveProject() {
		if (mProject == null) {
			return;
		}
		StorageHandler.getInstance().saveProject(mProject);
	}

	public boolean initializeDefaultProject(Context context) {
		try {
			fileChecksumContainer = new FileChecksumContainer();
			messageContainer = new MessageContainer();
			mProject = StandardProjectHandler.createAndSaveStandardProject(context);
			mCurrentSprite = null;
			mCurrentScript = null;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Utils.displayErrorMessage(context, context.getString(R.string.error_load_project));
			return false;
		}
	}

	public void initializeNewProject(String projectName, Context context) throws IOException {
		fileChecksumContainer = new FileChecksumContainer();
		messageContainer = new MessageContainer();
		mProject = StandardProjectHandler.createAndSaveStandardProject(projectName, context);

		mCurrentSprite = null;
		mCurrentScript = null;
		saveProject();
	}

	public Project getCurrentProject() {
		return mProject;
	}

	public void setProject(Project project) {
		mCurrentScript = null;
		mCurrentSprite = null;

		this.mProject = project;
	}

	public void deleteCurrentProject() {
		StorageHandler.getInstance().deleteProject(mProject);

		mProject = null;
	}

	public boolean renameProject(String newProjectName, Context context) {
		if (StorageHandler.getInstance().projectExists(newProjectName)) {
			Utils.displayErrorMessage(context, context.getString(R.string.error_project_exists));
			return false;
		}

		String oldProjectPath = Utils.buildProjectPath(mProject.getName());
		File oldProjectDirectory = new File(oldProjectPath);

		String newProjectPath = Utils.buildProjectPath(newProjectName);
		File newProjectDirectory = new File(newProjectPath);

		mProject.setName(newProjectName);

		boolean directoryRenamed = oldProjectDirectory.renameTo(newProjectDirectory);

		if (directoryRenamed) {
			this.saveProject();
		}

		return (directoryRenamed);
	}

	public Sprite getCurrentSprite() {
		return mCurrentSprite;
	}

	public void setCurrentSprite(Sprite sprite) {
		mCurrentSprite = sprite;
	}

	public Script getCurrentScript() {
		return mCurrentScript;
	}

	public void setCurrentScript(Script script) {
		if (script == null) {
			mCurrentScript = null;
		} else if (mCurrentSprite.getScriptIndex(script) != -1) {
			mCurrentScript = script;
		}
	}

	public void addSprite(Sprite sprite) {
		mProject.addSprite(sprite);
	}

	public void addScript(Script script) {
		mCurrentSprite.addScript(script);
	}

	public boolean spriteExists(String spriteName) {
		for (Sprite tempSprite : mProject.getSpriteList()) {
			if (tempSprite.getName().equalsIgnoreCase(spriteName)) {
				return true;
			}
		}
		return false;
	}

	public int getCurrentSpritePosition() {
		return mProject.getSpriteList().indexOf(mCurrentSprite);
	}

	public int getCurrentScriptPosition() {
		int currentSpritePosition = this.getCurrentSpritePosition();
		if (currentSpritePosition == -1) {
			return -1;
		}

		return mProject.getSpriteList().get(currentSpritePosition).getScriptIndex(mCurrentScript);
	}

	public boolean setCurrentSpriteWithPosition(int position) {
		if (position >= mProject.getSpriteList().size() || position < 0) {
			return false;
		}

		mCurrentSprite = mProject.getSpriteList().get(position);
		return true;
	}

	public boolean setCurrentScriptWithPosition(int position) {
		int currentSpritePosition = this.getCurrentSpritePosition();
		if (currentSpritePosition == -1) {
			return false;
		}

		if (position >= mProject.getSpriteList().get(currentSpritePosition).getNumberOfScripts() || position < 0) {
			return false;
		}

		mCurrentScript = mProject.getSpriteList().get(this.getCurrentSpritePosition()).getScript(position);

		return true;
	}

}
