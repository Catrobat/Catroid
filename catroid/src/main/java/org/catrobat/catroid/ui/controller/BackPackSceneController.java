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
package org.catrobat.catroid.ui.controller;

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BackPackSceneController {

	public static final String TAG = BackPackSceneController.class.getSimpleName();

	private static ArrayList<Scene> backPackedScenes = new ArrayList<>();
	private  static Map<String, String> backPackedNameMap = new HashMap<>();

	private static ArrayList<Scene> unpackedScenes = new ArrayList<>();
	private static Map<String, String> unPackedNameMap = new HashMap<>();


	public static boolean existsInBackPack(List<Scene> sceneList) {
		for (Scene scene : sceneList) {
			if (existsInBackPack(scene)) {
				return true;
			}
		}
		return false;
	}

	public static boolean existsInBackPack(Scene scene) {
		return BackPackListManager.getInstance().backPackedScenesContains(scene, true);
	}

	public static boolean backpack(List<Scene> scenes) {
		StorageHandler.getInstance().saveProject(ProjectManager.getInstance().getCurrentProject());
		ArrayList<Scene> hiddenScenes = new ArrayList<>();

		for (Scene scene : scenes) {
			String sceneName = scene.getName();
			BackPackListManager.getInstance().removeItemFromSceneBackPackByName(sceneName, false);

			if (!backpack(scene, false)) {
				return false;
			}

			BackPackListManager.searchForHiddenScenes(scene, hiddenScenes, false);
		}

		for (Scene scene : hiddenScenes) {
			BackPackListManager.getInstance().removeItemFromSceneBackPackByName(scene.getName(), true);
			if (!backpack(scene, true)) {
				return false;
			}

		}

		correctTransitionAndStartSceneBricksAfterPacking(backPackedScenes, backPackedNameMap);
		return true;
	}

	private static boolean backpack(Scene scene, boolean hidden) {
		File sourceScene = new File(Utils.buildScenePath(scene.getProject().getName(), scene.getName()));
		File targetScene = new File(Utils.buildBackpackScenePath(scene.getName()));

		try {
			StorageHandler.copyDirectory(targetScene, sourceScene);
		} catch (IOException e) {
			Log.e(TAG, "Error while backpacking Scene Files!", e);
			UtilFile.deleteDirectory(targetScene);
			return false;
		}
		Scene clonedScene = scene.clone();

		clonedScene.isBackPackScene = true;
		clonedScene.setProject(null);

		backPackedNameMap.put(scene.getName(), clonedScene.getName());
		backPackedScenes.add(clonedScene);

		if (hidden) {
			BackPackListManager.getInstance().addSceneToHiddenBackpack(clonedScene);
		}  else {
			BackPackListManager.getInstance().addSceneToBackPack(clonedScene);
		}

		return true;
	}

	public static boolean unpack(List<Scene> scenes) {
		ArrayList<Scene> hiddenScenesToUnpack = new ArrayList<>();

		for (Scene scene : scenes) {
			if (!unpack(scene)) {
				return false;
			}

			BackPackListManager.searchForHiddenScenes(scene, hiddenScenesToUnpack, true);
		}

		clearHiddenScenesToUnpackFromAlreadyUnpacked(hiddenScenesToUnpack, scenes);
		List<Scene> result = new ArrayList<>();
		result.addAll(unpackedScenes);

		for (Scene scene : hiddenScenesToUnpack) {
			if (!unpack(scene)) {
				return false;
			}
		}

		correctTransitionAndStartSceneBricksAfterPacking(unpackedScenes, unPackedNameMap);

		return true;
	}

	public static boolean unpack(Scene scene) {
		ProjectManager projectManager = ProjectManager.getInstance();
		String newName = Utils.getUniqueSceneName(scene.getName(), false);

		File sourceScene = new File(Utils.buildBackpackScenePath(scene.getName()));
		File targetScene = new File(Utils.buildScenePath(projectManager.getCurrentProject().getName(), newName));

		try {
			StorageHandler.copyDirectory(targetScene, sourceScene);
			UtilFile.deleteDirectory(new File(targetScene, Constants.PROJECTCODE_NAME));
		} catch (IOException e) {
			UtilFile.deleteDirectory(targetScene);
			return false;
		}
		Scene clonedScene = scene.cloneForBackPack();
		if (clonedScene == null) {
			return false;
		}

		clonedScene.setSceneName(newName);
		clonedScene.isBackPackScene = false;
		clonedScene.setProject(projectManager.getCurrentProject());
		clonedScene.getDataContainer().setProject(projectManager.getCurrentProject());
		projectManager.addScene(clonedScene);
		projectManager.setCurrentScene(clonedScene);

		clonedScene.setProject(ProjectManager.getInstance().getCurrentProject());
		clonedScene.getDataContainer().setProject(ProjectManager.getInstance().getCurrentProject());
		unpackedScenes.add(clonedScene);
		unPackedNameMap.put(scene.getName(), clonedScene.getName());

		return true;
	}

	private static void clearHiddenScenesToUnpackFromAlreadyUnpacked(ArrayList<Scene> hiddenScenes, List<Scene>
			alreadyUnpacked) {
		ArrayList<Scene> toRemove = new ArrayList<>();
		for (Scene hiddenScene : hiddenScenes) {
			for (Scene alreadyUnpackedScene : alreadyUnpacked) {
				if (hiddenScene.getName().equals(alreadyUnpackedScene.getName())) {
					toRemove.add(hiddenScene);
				}
			}
		}
		hiddenScenes.removeAll(toRemove);
	}

	private static void correctTransitionAndStartSceneBricksAfterPacking(ArrayList<Scene> scenesToCorrect, Map<String,
			String> visibleHiddenMap) {
		for (Scene scene : scenesToCorrect) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Brick brick : sprite.getListWithAllBricks()) {
					if (brick instanceof SceneTransitionBrick) {
						SceneTransitionBrick trans = (SceneTransitionBrick) brick;
						trans.setSceneForTransition(visibleHiddenMap.get(trans.getSceneForTransition()));
					}
					if (brick instanceof SceneStartBrick) {
						SceneStartBrick start = (SceneStartBrick) brick;
						start.setSceneToStart(visibleHiddenMap.get(start.getSceneToStart()));
					}
				}
			}
		}
	}
}
