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

import android.os.AsyncTask;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Backpack;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.adapter.LookBaseAdapter;
import org.catrobat.catroid.ui.adapter.SoundBaseAdapter;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class BackPackListManager {
	private static final BackPackListManager INSTANCE = new BackPackListManager();

	private static Backpack backpack;
	private SoundBaseAdapter currentSoundAdapter;
	private LookBaseAdapter currentLookAdapter;

	public static BackPackListManager getInstance() {
		if (backpack == null) {
			backpack = new Backpack();
		}
		return INSTANCE;
	}

	void addLookToBackPack(LookData lookData) {
		getBackpack().backpackedLooks.add(lookData);
	}

	public List<LookData> getBackPackedLooks() {
		return getBackpack().backpackedLooks;
	}

	public void clearBackPackScripts() {
		getBackpack().backpackedScripts.clear();
		getBackpack().hiddenBackpackedScripts.clear();
	}

	public void removeItemFromScriptBackPack(String scriptGroup) {
		getBackpack().backpackedScripts.remove(scriptGroup);
	}

	public ArrayList<String> getBackPackedScriptGroups() {
		return new ArrayList<>(getBackpack().backpackedScripts.keySet());
	}

	void addScriptToBackPack(String scriptGroup, List<Script> scripts) {
		getBackpack().backpackedScripts.put(scriptGroup, scripts);
	}

	public HashMap<String, List<Script>> getBackPackedScripts() {
		return getBackpack().backpackedScripts;
	}

	public HashMap<String, List<Script>> getAllBackPackedScripts() {
		HashMap<String, List<Script>> allScripts = new HashMap<>();
		allScripts.putAll(getBackpack().backpackedScripts);
		allScripts.putAll(getBackpack().hiddenBackpackedScripts);
		return allScripts;
	}

	public void clearBackPackUserBricks() {
		getBackpack().backpackedUserBricks.clear();
	}

	public void removeItemFromUserBrickBackPack(String userBrickGroup) {
		getBackpack().backpackedUserBricks.remove(userBrickGroup);
	}

	public ArrayList<String> getBackPackedUserBrickGroups() {
		return new ArrayList<>(getBackpack().backpackedUserBricks.keySet());
	}

	void addUserBrickToBackPack(String userBrickGroup, List<UserBrick> userBricks) {
		getBackpack().backpackedUserBricks.put(userBrickGroup, userBricks);
	}

	public HashMap<String, List<UserBrick>> getBackPackedUserBricks() {
		return getBackpack().backpackedUserBricks;
	}

	public void clearBackPackLooks() {
		getBackpack().backpackedLooks.clear();
		getBackpack().hiddenBackpackedLooks.clear();
	}

	public void removeItemFromLookBackPack(LookData lookData) {
		getBackpack().backpackedLooks.remove(lookData);
	}

	void removeItemFromLookBackPackByLookName(String name) {
		for (LookData lookData : getBackpack().backpackedLooks) {
			if (lookData.getLookName().equals(name)) {
				getBackpack().backpackedLooks.remove(lookData);
			}
		}
	}

	public List<SoundInfo> getBackPackedSounds() {
		return getBackpack().backpackedSounds;
	}

	public void clearBackPackSounds() {
		getBackpack().backpackedSounds.clear();
		getBackpack().hiddenBackpackedSounds.clear();
	}

	void addSoundToBackPack(SoundInfo soundInfo) {
		getBackpack().backpackedSounds.add(soundInfo);
	}

	public void removeItemFromSoundBackPack(SoundInfo currentSoundInfo) {
		getBackpack().backpackedSounds.remove(currentSoundInfo);
	}

	void removeItemFromSoundBackPackBySoundTitle(String title) {
		for (SoundInfo soundInfo : getBackpack().backpackedSounds) {
			if (soundInfo.getTitle().equals(title)) {
				getBackpack().backpackedSounds.remove(soundInfo);
			}
		}
	}

	public List<Scene> getAllBackpackedScenes() {
		List<Scene> result = new ArrayList<>();
		result.addAll(getBackpack().backpackedScenes);
		result.addAll(getBackpack().hiddenBackpackedScenes);
		return result;
	}

	public List<Scene> getBackPackedScenes() {
		return getBackpack().backpackedScenes;
	}

	private List<Scene> getHiddenBackPackedScenes() {
		return getBackpack().hiddenBackpackedScenes;
	}

	public Scene getHiddenSceneByName(String name) {
		for (Scene scene : getBackpack().hiddenBackpackedScenes) {
			if (scene.getName().equals(name)) {
				return scene;
			}
		}
		return null;
	}

	public void clearBackPackScenes() {
		getBackpack().backpackedScenes.clear();
	}

	void addSceneToBackPack(Scene scene) {
		getBackpack().backpackedScenes.add(scene);
	}

	void addSceneToHiddenBackpack(Scene scene) {
		getBackpack().hiddenBackpackedScenes.add(scene);
	}

	public void removeItemFromSceneBackPackByName(String title, boolean hidden) {
		List<Scene> toRemove = new ArrayList<>();
		for (Scene scene : getBackpack().backpackedScenes) {
			if (scene.getName().equals(title)) {
				toRemove.add(scene);
				UtilFile.deleteDirectory(new File(Utils.buildBackpackScenePath(scene.getName())));
			}
		}
		(hidden ? getHiddenBackPackedScenes() : getBackPackedScenes()).removeAll(toRemove);
	}

	public List<Sprite> getBackPackedSprites() {
		return getBackpack().backpackedSprites;
	}

	public void clearBackPackSprites() {
		getBackpack().backpackedSprites.clear();
		getBackpack().hiddenBackpackedSprites.clear();
	}

	void addSpriteToBackPack(Sprite sprite) {
		getBackpack().backpackedSprites.add(sprite);
	}

	public void removeItemFromSpriteBackPack(Sprite sprite) {
		getBackpack().backpackedSprites.remove(sprite);
	}

	void removeItemFromSpriteBackPackByName(String name) {
		List<Sprite> sprites = getBackpack().backpackedSprites;
		for (int spritePosition = 0; spritePosition < sprites.size(); spritePosition++) {
			Sprite sprite = getBackpack().backpackedSprites.get(spritePosition);
			if (sprite.getName().equals(name)) {
				getBackpack().backpackedSprites.remove(sprite);
			}
		}
	}

	public List<LookData> getHiddenBackpackedLooks() {
		return getBackpack().hiddenBackpackedLooks;
	}

	void removeItemFromScriptHiddenBackpack(String scriptGroup) {
		getBackpack().hiddenBackpackedScripts.remove(scriptGroup);
	}

	void addScriptToHiddenBackpack(String scriptGroup, List<Script> scripts) {
		getBackpack().hiddenBackpackedScripts.put(scriptGroup, scripts);
	}

	public HashMap<String, List<Script>> getHiddenBackpackedScripts() {
		return getBackpack().hiddenBackpackedScripts;
	}

	public void removeItemFromLookHiddenBackpack(LookData lookData) {
		getBackpack().hiddenBackpackedLooks.remove(lookData);
	}

	public List<SoundInfo> getHiddenBackpackedSounds() {
		return getBackpack().hiddenBackpackedSounds;
	}

	void addSoundToHiddenBackpack(SoundInfo soundInfo) {
		getBackpack().hiddenBackpackedSounds.add(soundInfo);
	}

	public void removeItemFromSoundHiddenBackpack(SoundInfo currentSoundInfo) {
		getBackpack().hiddenBackpackedSounds.remove(currentSoundInfo);
	}

	public List<Sprite> getHiddenBackpackedSprites() {
		return getBackpack().hiddenBackpackedSprites;
	}

	void addSpriteToHiddenBackpack(Sprite sprite) {
		getBackpack().hiddenBackpackedSprites.add(sprite);
	}

	void removeItemFromSpriteHiddenBackpack(Sprite sprite) {
		getBackpack().hiddenBackpackedSprites.remove(sprite);
	}

	boolean backPackedSoundsContain(SoundInfo soundInfo, boolean onlyVisible) {
		List<SoundInfo> backPackedSounds = onlyVisible ? getBackPackedSounds() : getAllBackPackedSounds();
		for (SoundInfo backPackedSound : backPackedSounds) {
			if (backPackedSound.equals(soundInfo)) {
				return true;
			}
		}
		return false;
	}

	boolean backPackedLooksContain(LookData lookData, boolean onlyVisible) {
		List<LookData> backPackedLooks = onlyVisible ? getBackPackedLooks() : getAllBackPackedLooks();
		for (LookData backPackedLook : backPackedLooks) {
			if (backPackedLook.equals(lookData)) {
				return true;
			}
		}
		return false;
	}

	boolean backPackedSpritesContains(Sprite sprite, boolean onlyVisible) {
		List<Sprite> backPackedSprites = onlyVisible ? getBackPackedSprites() : getAllBackPackedSprites();
		for (Sprite backPackedSprite : backPackedSprites) {
			if (backPackedSprite.equals(sprite)) {
				return true;
			}
		}
		return false;
	}

	public boolean backPackedScenesContains(Scene scene, boolean onlyVisible) {
		List<Scene> toSearch = onlyVisible ? getBackPackedScenes() : getHiddenBackPackedScenes();
		for (Scene backPackedScene : toSearch) {
			if (backPackedScene.getName().equals(scene.getName())) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<String> getAllBackPackedScriptGroups() {
		ArrayList<String> allScriptGroups = new ArrayList<>();
		allScriptGroups.addAll(new ArrayList<>(getBackpack().backpackedScripts.keySet()));
		allScriptGroups.addAll(new ArrayList<>(getBackpack().backpackedScripts.keySet()));
		return allScriptGroups;
	}

	public List<LookData> getAllBackPackedLooks() {
		List<LookData> allLooks = new ArrayList<>();
		allLooks.addAll(getBackpack().backpackedLooks);
		allLooks.addAll(getBackpack().hiddenBackpackedLooks);
		return allLooks;
	}

	public List<SoundInfo> getAllBackPackedSounds() {
		List<SoundInfo> allSounds = new ArrayList<>();
		allSounds.addAll(getBackpack().backpackedSounds);
		allSounds.addAll(getBackpack().hiddenBackpackedSounds);
		return allSounds;
	}

	public List<Sprite> getAllBackPackedSprites() {
		List<Sprite> allSprites = new ArrayList<>();
		allSprites.addAll(getBackpack().backpackedSprites);
		allSprites.addAll(getBackpack().hiddenBackpackedSprites);
		return allSprites;
	}

	SoundBaseAdapter getCurrentSoundAdapter() {
		return currentSoundAdapter;
	}

	public void setCurrentSoundAdapter(SoundBaseAdapter currentSoundAdapter) {
		this.currentSoundAdapter = currentSoundAdapter;
	}

	LookBaseAdapter getCurrentLookAdapter() {
		return currentLookAdapter;
	}

	public void setCurrentLookAdapter(LookBaseAdapter currentLookAdapter) {
		this.currentLookAdapter = currentLookAdapter;
	}

	void addLookToHiddenBackPack(LookData newLookData) {
		getBackpack().hiddenBackpackedLooks.add(newLookData);
	}

	public boolean isBackpackEmpty() {
		return getAllBackPackedLooks().isEmpty() && getAllBackPackedScriptGroups().isEmpty()
				&& getAllBackPackedSounds().isEmpty() && getAllBackPackedSprites().isEmpty();
	}

	public void saveBackpack() {
		SaveBackpackAsynchronousTask saveTask = new SaveBackpackAsynchronousTask();
		saveTask.execute();
	}

	public void loadBackpack() {
		LoadBackpackAsynchronousTask loadTask = new LoadBackpackAsynchronousTask();
		loadTask.execute();
	}

	public Backpack getBackpack() {
		if (backpack == null) {
			backpack = new Backpack();
		}
		return backpack;
	}

	public static void searchForHiddenScenes(Scene sceneToSearch, ArrayList<Scene> foundScenes, boolean inBackpack) {
		for (Sprite sprite : sceneToSearch.getSpriteList()) {
			for (Brick brick : sprite.getListWithAllBricks()) {
				if (brick instanceof SceneTransitionBrick) {
					Scene transitionScene;
					if (inBackpack) {
						transitionScene = BackPackListManager.getInstance().getHiddenSceneByName(((SceneTransitionBrick) brick).getSceneForTransition());
						if (transitionScene == null) {
							continue;
						}
					} else {
						transitionScene = ProjectManager.getInstance().getCurrentProject().getSceneByName(((SceneTransitionBrick) brick).getSceneForTransition());
						if (transitionScene == null) {
							continue;
						}
					}
					if (!foundScenes.contains(transitionScene)) {
						foundScenes.add(transitionScene);
						searchForHiddenScenes(transitionScene, foundScenes, inBackpack);
					}
				}
				if (brick instanceof SceneStartBrick) {
					Scene startScene;
					if (inBackpack) {
						startScene = BackPackListManager.getInstance().getHiddenSceneByName(((SceneStartBrick) brick).getSceneToStart());
						if (startScene == null) {
							continue;
						}
					} else {
						startScene = ProjectManager.getInstance().getCurrentProject().getSceneByName(((SceneStartBrick) brick).getSceneToStart());
						if (startScene == null) {
							continue;
						}
					}
					if (!foundScenes.contains(startScene)) {
						foundScenes.add(startScene);
						searchForHiddenScenes(startScene, foundScenes, inBackpack);
					}
				}
			}
		}
	}

	private class SaveBackpackAsynchronousTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			StorageHandler.getInstance().saveBackpack(getBackpack());
			return null;
		}
	}

	private class LoadBackpackAsynchronousTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			backpack = StorageHandler.getInstance().loadBackpack();
			setBackPackFlags();
			ProjectManager.getInstance().checkNestingBrickReferences(false, true);
			return null;
		}

		private void setBackPackFlags() {
			for (LookData lookData : getAllBackPackedLooks()) {
				lookData.isBackpackLookData = true;
			}
			for (SoundInfo soundInfo : getAllBackPackedSounds()) {
				soundInfo.setBackpackSoundInfo(true);
			}
			for (Sprite sprite : getAllBackPackedSprites()) {
				sprite.isBackpackObject = true;
				for (LookData lookData : sprite.getLookDataList()) {
					lookData.isBackpackLookData = true;
				}
				for (SoundInfo soundInfo : sprite.getSoundList()) {
					soundInfo.setBackpackSoundInfo(true);
				}
			}
			for (Scene scene : getBackPackedScenes()) {
				scene.isBackPackScene = true;
			}
		}
	}
}
