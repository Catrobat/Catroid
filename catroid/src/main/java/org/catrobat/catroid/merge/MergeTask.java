/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

package org.catrobat.catroid.merge;

import android.app.Activity;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.ui.adapter.ProjectListAdapter;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MergeTask {

	private Activity activity;
	private Scene mergeResultScene = null;
	private Scene firstScene = null;
	private Scene secondScene = null;
	private Scene current = null;
	private Project firstProject = null;
	private Project secondProject = null;
	private Project mergedProject = null;
	private ProjectListAdapter adapter = null;
	private boolean addScene = false;
	private String oldSceneName = null;

	public MergeTask(Project firstProject, Project secondProject, Activity activity, ProjectListAdapter
			adapter, boolean addScene) {
		if (addScene) {
			this.firstProject = firstProject.getProjectLists().size() == 1 ? secondProject : firstProject;
			this.secondProject = firstProject.getProjectLists().size() == 1 ? firstProject : secondProject;
		} else {
			this.firstProject = firstProject;
			this.secondProject = secondProject;
		}
		this.activity = activity;
		this.adapter = adapter;
		this.addScene = addScene;
	}

	public MergeTask(Scene firstScene, Scene secondScene, Activity activity) {
		this.firstScene = firstScene;
		this.secondScene = secondScene;
		this.activity = activity;
		this.mergedProject = ProjectManager.getInstance().getCurrentProject();
	}

	public boolean mergeProjects(String mergeProjectName) {
		boolean result = true;
		ArrayList<String> handledScenes = new ArrayList<>();

		if (mergedProject != null) {
			return false;
		}

		mergedProject = new Project(activity, mergeProjectName);
		mergedProject.removeScene(mergedProject.getDefaultScene());

		createHeader();

		if (addScene) {
			Scene scene = secondProject.getDefaultScene();
			oldSceneName = scene.getName();
			result = scene.rename(Utils.getUniqueSceneName(scene.getName(), firstProject, secondProject), activity, false);
		}

		if (firstProject.getSceneList().size() == 1 && secondProject.getSceneList().size() == 1) {
			result &= secondProject.getDefaultScene().rename(firstProject.getDefaultScene().getName(), activity, false);
		}

		for (Scene firstScene : firstProject.getSceneList()) {
			for (Scene secondScene : secondProject.getSceneList()) {
				if (firstScene.getName().equals(secondScene.getName())) {
					this.firstScene = firstScene;
					this.secondScene = secondScene;
					result &= mergeScenes(firstScene.getName());
					handledScenes.add(firstScene.getName());
					handledScenes.add(secondScene.getName());
				}
			}
		}

		for (Scene scene : firstProject.getSceneList()) {
			if (!handledScenes.contains(scene.getName())) {
				addSceneToProject(scene, mergedProject);
			}
		}

		for (Scene scene : secondProject.getSceneList()) {
			if (!handledScenes.contains(scene.getName())) {
				addSceneToProject(scene, mergedProject);
			}
		}

		if (oldSceneName != null) {
			Scene scene;
			scene = secondProject.getDefaultScene();
			result &= scene.rename(oldSceneName, activity, false);
		}

		mergedProject = StorageHandler.getInstance().loadProject(mergedProject.getName(), activity);
		ProjectManager.getInstance().setCurrentProject(mergedProject);

		if (adapter != null) {
			File projectCodeFile = new File(Utils.buildPath(Utils.buildProjectPath(mergeProjectName),
					Constants.PROJECTCODE_NAME));
			adapter.insert(new ProjectData(mergeProjectName, projectCodeFile.lastModified()), 0);
			adapter.notifyDataSetChanged();

			String msg = firstProject.getName() + " " + activity.getString(R.string.merge_info) + " " + secondProject.getName() + "!";
			ToastUtil.showSuccess(activity, msg);
		}

		return result;
	}

	public boolean mergeScenes(String mergeResultName) {
		if (mergeResultScene != null) {
			return false;
		}

		mergeResultScene = new Scene(activity, mergeResultName, mergedProject);

		createSpritesAllScenes();
		copySpriteScriptsAllScenes();

		if (!ConflictHelper.checkMergeConflict(activity, mergeResultScene)) {
			return false;
		}

		mergedProject.addScene(mergeResultScene);
		StorageHandler.getInstance().saveProject(mergedProject);

		// copy all images and sounds from first scene into merge scene
		StorageHandler.copyAllFiles(firstScene.getSceneImageDirectoryPath(),
				mergeResultScene.getSceneImageDirectoryPath());
		StorageHandler.copyAllFiles(firstScene.getSceneSoundDirectoryPath(),
				mergeResultScene.getSceneSoundDirectoryPath());

		// copy all images and sounds from second scene into merge scene
		StorageHandler.copyAllFiles(secondScene.getSceneImageDirectoryPath(),
				mergeResultScene.getSceneImageDirectoryPath());
		StorageHandler.copyAllFiles(secondScene.getSceneSoundDirectoryPath(),
				mergeResultScene.getSceneSoundDirectoryPath());

		File automaticScreenshotDestination = new File(firstScene.getSceneDirectoryPath(),
				StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME);
		File automaticScreenshotSource = new File(mergeResultScene.getSceneDirectoryPath(),
				StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME);
		File manualScreenshotDestination = new File(firstScene.getSceneDirectoryPath(),
				StageListener.SCREENSHOT_MANUAL_FILE_NAME);
		File manualScreenshotSource = new File(mergeResultScene.getSceneDirectoryPath(),
				StageListener.SCREENSHOT_MANUAL_FILE_NAME);

		if (automaticScreenshotSource.exists()) {
			StorageHandler.copyFile(automaticScreenshotDestination.getAbsolutePath(),
					automaticScreenshotSource.getAbsolutePath(), null);
		}
		if (manualScreenshotSource.exists()) {
			StorageHandler.copyFile(manualScreenshotDestination.getAbsolutePath(),
					manualScreenshotSource.getAbsolutePath(), null);
		}

		return true;
	}

	private boolean addSceneToProject(Scene scene, Project targetProject) {
		String srcSceneImageDirectoryPath = scene.getSceneImageDirectoryPath();
		String srcSceneSoundDirectoryPath = scene.getSceneSoundDirectoryPath();

		Scene copiedScene = scene.clone();
		copiedScene.setProject(targetProject);
		copiedScene.getDataContainer().setProject(targetProject);
		targetProject.addScene(copiedScene);

		String dstSceneImageDirectoryPath = copiedScene.getSceneImageDirectoryPath();
		String dstSceneSoundDirectoryPath = copiedScene.getSceneSoundDirectoryPath();

		StorageHandler.getInstance().saveProject(mergedProject);

		boolean result = StorageHandler.copyAllFiles(srcSceneImageDirectoryPath, dstSceneImageDirectoryPath);
		result &= StorageHandler.copyAllFiles(srcSceneSoundDirectoryPath, dstSceneSoundDirectoryPath);

		File automaticScreenshotDestination = new File(copiedScene.getSceneDirectoryPath(),
				StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME);
		File automaticScreenshotSource = new File(scene.getSceneDirectoryPath(),
				StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME);
		File manualScreenshotDestination = new File(copiedScene.getSceneDirectoryPath(),
				StageListener.SCREENSHOT_MANUAL_FILE_NAME);
		File manualScreenshotSource = new File(scene.getSceneDirectoryPath(),
				StageListener.SCREENSHOT_MANUAL_FILE_NAME);

		if (automaticScreenshotSource.exists()) {
			StorageHandler.copyFile(automaticScreenshotSource.getAbsolutePath(),
					automaticScreenshotDestination.getAbsolutePath(), null);
		}
		if (manualScreenshotSource.exists()) {
			StorageHandler.copyFile(manualScreenshotSource.getAbsolutePath(),
					manualScreenshotDestination.getAbsolutePath(), null);
		}

		return result;
	}

	private void createHeader() {
		XmlHeader mainHeader;
		XmlHeader subHeader;
		XmlHeader mergeHeader = mergedProject.getXmlHeader();

		int firstProjectSpriteCount = 0;
		int secondProjectSpriteCount = 0;

		for (Scene scene : firstProject.getSceneList()) {
			firstProjectSpriteCount += scene.getSpriteList().size();
		}

		for (Scene scene : secondProject.getSceneList()) {
			secondProjectSpriteCount += scene.getSpriteList().size();
		}

		if (firstProjectSpriteCount < 2 && secondProjectSpriteCount > 1) {
			mainHeader = secondProject.getXmlHeader();
			subHeader = firstProject.getXmlHeader();
		} else {
			mainHeader = firstProject.getXmlHeader();
			subHeader = secondProject.getXmlHeader();
		}
		mergeHeader.setVirtualScreenWidth(mainHeader.virtualScreenWidth);
		mergeHeader.setVirtualScreenHeight(mainHeader.virtualScreenHeight);

		mergeHeader.setRemixParentsUrlString(Utils.generateRemixUrlsStringForMergedProgram(mainHeader, subHeader));
		mergedProject.setXmlHeader(mergeHeader);
	}

	private void createSpritesAllScenes() {
		current = firstScene;
		createSprites(firstScene);
		current = secondScene;
		createSprites(secondScene);
	}

	private void createSprites(Scene scene) {
		for (Sprite sprite : scene.getSpriteList()) {
			if (!mergeResultScene.containsSpriteBySpriteName(sprite.getName())) {
				mergeResultScene.addSprite(sprite);
			}
		}
	}

	private void copySpriteScriptsAllScenes() {
		current = firstScene;
		copySpriteScripts(firstScene);
		current = secondScene;
		copySpriteScripts(secondScene);
		mergeResultScene.removeUnusedBroadcastMessages();
	}

	private void copySpriteScripts(Scene scene) {
		ReferenceHelper helper = new ReferenceHelper(mergeResultScene, scene);

		for (Sprite sprite : scene.getSpriteList()) {
			checkScripts(sprite);
			addSoundsAndLooks(sprite);
		}
		mergeResultScene = helper.updateAllReferences();
	}

	private void checkScripts(Sprite sprite) {
		Sprite spriteInto = mergeResultScene.getSpriteBySpriteName(sprite.getName());

		for (Script fromScript : sprite.getScriptList()) {
			boolean equal = false;
			for (Script intoScript : spriteInto.getScriptList()) {
				equal |= isEqualScript(intoScript, fromScript);
			}
			if (spriteInto.getScriptList().size() == 0 || !equal) {
				mergeResultScene.getSpriteBySpriteName(sprite.getName()).addScript(fromScript);
			}
		}
	}

	private void addSoundsAndLooks(Sprite sprite) {
		Sprite spriteInto = mergeResultScene.getSpriteBySpriteName(sprite.getName());

		for (LookData look : sprite.getLookDataList()) {
			if (!spriteInto.existLookDataByName(look) && !spriteInto.existLookDataByFileName(look)) {
				spriteInto.getLookDataList().add(look.clone());
			} else if (spriteInto.existLookDataByName(look) && !spriteInto.existLookDataByFileName(look)) {
				for (int i = 2; i < 100; i++) {
					look.setName(look.getName() + "_" + i);
					if (!spriteInto.existLookDataByName(look)) {
						break;
					}
				}
				spriteInto.getLookDataList().add(look.clone());
			}
		}

		for (SoundInfo sound : sprite.getSoundList()) {
			if (!spriteInto.existSoundInfoByName(sound) || !spriteInto.existSoundInfoByFileName(sound)) {
				spriteInto.getSoundList().add(sound.clone());
			}
		}
	}

	private boolean isEqualScript(Script intoProjectScript, Script fromProjectScript) {
		List<Brick> intoProjectBrickList = intoProjectScript.getBrickList();
		List<Brick> fromProjectBrickList = fromProjectScript.getBrickList();

		int counter = 0;
		int numberOfEqualBricks = (intoProjectBrickList.size() < fromProjectBrickList.size()) ? intoProjectBrickList.size() : fromProjectBrickList.size();

		for (int i = 0; i < numberOfEqualBricks; i++) {
			Brick intoProjectBrick = intoProjectBrickList.get(i);
			Brick fromProjectBrick = fromProjectBrickList.get(i);

			if (intoProjectBrick.isEqualBrick(fromProjectBrick, mergeResultScene, current)) {
				counter++;
			}
		}

		if (counter != numberOfEqualBricks) {
			return false;
		}
		return true;
	}
}
