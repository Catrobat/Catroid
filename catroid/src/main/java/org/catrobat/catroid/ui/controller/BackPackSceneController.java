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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.fragment.SceneListFragment;
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
	private static final BackPackSceneController INSTANCE = new BackPackSceneController();

	private BackPackSceneController() {
	}

	public static BackPackSceneController getInstance() {
		return INSTANCE;
	}

	public boolean checkScenesReplaceInBackpack(List<Scene> currentSceneList) {
		for (Scene scene : currentSceneList) {
			if (checkSceneReplaceInBackpack(scene)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkSceneReplaceInBackpack(Scene currentScene) {
		return BackPackListManager.getInstance().backPackedScenesContains(currentScene, true);
	}

	public void showBackPackReplaceDialog(final List<Scene> currentSceneList, final SceneListFragment fragment) {
		final Context context = fragment.getActivity();
		Resources resources = context.getResources();
		String replaceSceneMessage;
		if (currentSceneList.size() == 1) {
			replaceSceneMessage = resources.getString(R.string.backpack_replace_scene, currentSceneList.get(0)
					.getName());
		} else {
			replaceSceneMessage = resources.getString(R.string.backpack_replace_scene_multiple);
		}

		AlertDialog dialog = new CustomAlertDialogBuilder(context)
				.setTitle(R.string.backpack)
				.setMessage(replaceSceneMessage)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						fragment.showProgressCircle();
						fragment.packScenes(currentSceneList);
					}
				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						fragment.clearCheckedItems();
						dialog.dismiss();
					}
				}).setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialogInterface) {
						fragment.clearCheckedItems();
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public boolean backpackScenes(List<Scene> scenes) {
		StorageHandler.getInstance().saveProject(ProjectManager.getInstance().getCurrentProject());
		ArrayList<Scene> hiddenScenes = new ArrayList<>();
		ArrayList<Scene> backPackedScenes = new ArrayList<>();
		Map<String, String> backPackedNameMap = new HashMap<>();

		for (Scene sceneToEdit : scenes) {
			String sceneName = sceneToEdit.getName();
			BackPackListManager.getInstance().removeItemFromSceneBackPackByName(sceneName, false);

			Scene backPackScene = backpack(sceneToEdit);
			if (backPackScene == null) {
				return false;
			}

			backPackedNameMap.put(sceneToEdit.getName(), backPackScene.getName());
			backPackedScenes.add(backPackScene);
			BackPackListManager.searchForHiddenScenes(backPackScene, hiddenScenes, false);
			BackPackListManager.getInstance().addSceneToBackPack(backPackScene);
		}

		for (Scene scene : hiddenScenes) {
			BackPackListManager.getInstance().removeItemFromSceneBackPackByName(scene.getName(), true);
			Scene hiddenBackPackScene = backpack(scene);
			if (hiddenBackPackScene == null) {
				return false;
			}
			backPackedNameMap.put(scene.getName(), hiddenBackPackScene.getName());
			BackPackListManager.getInstance().addSceneToHiddenBackpack(hiddenBackPackScene);
			backPackedScenes.add(hiddenBackPackScene);
		}
		correctTransitionAndStartSceneBricksAfterPacking(backPackedScenes, backPackedNameMap);
		return true;
	}

	public boolean backpackScene(Scene sceneToEdit) {
		ArrayList<Scene> scenes = new ArrayList<>();
		scenes.add(sceneToEdit);
		return backpackScenes(scenes);
	}

	public Scene backpack(Scene sceneToEdit) {
		File sourceScene = new File(Utils.buildScenePath(sceneToEdit.getProject().getName(), sceneToEdit.getName()));
		File targetScene = new File(Utils.buildBackpackScenePath(sceneToEdit.getName()));

		try {
			StorageHandler.copyDirectory(targetScene, sourceScene);
		} catch (IOException e) {
			Log.e(TAG, "Error while backpacking Scene Files !", e);
			UtilFile.deleteDirectory(targetScene);
			return null;
		}
		Scene clonedScene = sceneToEdit.clone();

		clonedScene.isBackPackScene = true;
		clonedScene.setProject(null);

		return clonedScene;
	}

	public List<Scene> unpackScenes(List<Scene> scenes) {
		ArrayList<Scene> unpackedScenes = new ArrayList<>();
		Map<String, String> unPackedNameMap = new HashMap<>();
		ArrayList<Scene> hiddenScenesToUnpack = new ArrayList<>();

		for (Scene selectedScene : scenes) {
			Scene unpackedScene = unpack(selectedScene);
			if (unpackedScene == null) {
				return null;
			}
			unpackedScene.setProject(ProjectManager.getInstance().getCurrentProject());
			unpackedScene.getDataContainer().setProject(ProjectManager.getInstance().getCurrentProject());
			unpackedScenes.add(unpackedScene);
			unPackedNameMap.put(selectedScene.getName(), unpackedScene.getName());

			BackPackListManager.searchForHiddenScenes(selectedScene, hiddenScenesToUnpack, true);
		}

		clearHiddenScenesToUnpackFromAlreadyUnpacked(hiddenScenesToUnpack, scenes);
		List<Scene> result = new ArrayList<>();
		result.addAll(unpackedScenes);

		Scene unpackedScene;
		for (Scene scene : hiddenScenesToUnpack) {
			unpackedScene = unpack(scene);
			if (unpackedScene == null) {
				return null;
			}
			unpackedScenes.add(unpackedScene);
			unPackedNameMap.put(scene.getName(), unpackedScene.getName());
			unpackedScene.setProject(ProjectManager.getInstance().getCurrentProject());
			unpackedScene.getDataContainer().setProject(ProjectManager.getInstance().getCurrentProject());
		}

		correctTransitionAndStartSceneBricksAfterPacking(unpackedScenes, unPackedNameMap);

		return result;
	}

	public Scene unpackScene(Scene selectedScene) {
		ArrayList<Scene> scenes = new ArrayList<>();
		scenes.add(selectedScene);
		List<Scene> result = unpackScenes(scenes);
		return result == null ? null : result.get(0);
	}

	public Scene unpack(Scene selectedScene) {
		if (selectedScene == null) {
			return null;
		}
		ProjectManager projectManager = ProjectManager.getInstance();
		String newName = Utils.getUniqueSceneName(selectedScene.getName(), false);

		File sourceScene = new File(Utils.buildBackpackScenePath(selectedScene.getName()));
		File targetScene = new File(Utils.buildScenePath(projectManager.getCurrentProject().getName(), newName));

		try {
			StorageHandler.copyDirectory(targetScene, sourceScene);
			UtilFile.deleteDirectory(new File(targetScene, Constants.PROJECTCODE_NAME));
		} catch (IOException e) {
			Log.e(TAG, "Error while unpacking Scene Files !", e);
			UtilFile.deleteDirectory(targetScene);
			return null;
		}
		Scene clonedScene = selectedScene.cloneForBackPack();
		if (clonedScene == null) {
			return null;
		}
		clonedScene.setSceneName(newName);
		clonedScene.isBackPackScene = false;
		clonedScene.setProject(projectManager.getCurrentProject());
		clonedScene.getDataContainer().setProject(projectManager.getCurrentProject());
		projectManager.addScene(clonedScene);
		projectManager.setCurrentScene(clonedScene);

		return clonedScene;
	}

	private void clearHiddenScenesToUnpackFromAlreadyUnpacked(ArrayList<Scene> hiddenScenes, List<Scene>
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

	private void correctTransitionAndStartSceneBricksAfterPacking(ArrayList<Scene> scenesToCorrect, Map<String, String> visibleHiddenMap) {
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
