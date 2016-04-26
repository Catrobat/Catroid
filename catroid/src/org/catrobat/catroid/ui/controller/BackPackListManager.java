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
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.adapter.LookBaseAdapter;
import org.catrobat.catroid.ui.adapter.SoundBaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class BackPackListManager {
	private static final BackPackListManager INSTANCE = new BackPackListManager();

	private static Backpack backpack;
	private static SoundBaseAdapter currentSoundAdapter;
	private static LookBaseAdapter currentLookAdapter;

	public static BackPackListManager getInstance() {
		if (backpack == null) {
			backpack = new Backpack();
		}
		return INSTANCE;
	}

	public void addLookToBackPack(LookData lookData) {
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

	public void addScriptToBackPack(String scriptGroup, List<Script> scripts) {
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

	public void clearBackPackLooks() {
		getBackpack().backpackedLooks.clear();
		getBackpack().hiddenBackpackedLooks.clear();
	}

	public void removeItemFromLookBackPack(LookData lookData) {
		getBackpack().backpackedLooks.remove(lookData);
	}

	public void removeItemFromLookBackPackByLookName(String name) {
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

	public void addSoundToBackPack(SoundInfo soundInfo) {
		getBackpack().backpackedSounds.add(soundInfo);
	}

	public void removeItemFromSoundBackPack(SoundInfo currentSoundInfo) {
		getBackpack().backpackedSounds.remove(currentSoundInfo);
	}

	public void removeItemFromSoundBackPackBySoundTitle(String title) {
		for (SoundInfo soundInfo : getBackpack().backpackedSounds) {
			if (soundInfo.getTitle().equals(title)) {
				getBackpack().backpackedSounds.remove(soundInfo);
			}
		}
	}

	public List<Sprite> getBackPackedSprites() {
		return getBackpack().backpackedSprites;
	}

	public void clearBackPackSprites() {
		getBackpack().backpackedSprites.clear();
		getBackpack().hiddenBackpackedSprites.clear();
	}

	public void addSpriteToBackPack(Sprite sprite) {
		getBackpack().backpackedSprites.add(sprite);
	}

	public void removeItemFromSpriteBackPack(Sprite sprite) {
		getBackpack().backpackedSprites.remove(sprite);
	}

	public void removeItemFromSpriteBackPackByName(String name) {
		for (Sprite sprite : getBackpack().backpackedSprites) {
			if (sprite.getName().equals(name)) {
				getBackpack().backpackedSprites.remove(sprite);
			}
		}
	}

	public List<LookData> getHiddenBackpackedLooks() {
		return getBackpack().hiddenBackpackedLooks;
	}

	public void removeItemFromScriptHiddenBackpack(String scriptGroup) {
		getBackpack().hiddenBackpackedScripts.remove(scriptGroup);
	}

	public void addScriptToHiddenBackpack(String scriptGroup, List<Script> scripts) {
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

	public void addSoundToHiddenBackpack(SoundInfo soundInfo) {
		getBackpack().hiddenBackpackedSounds.add(soundInfo);
	}

	public void removeItemFromSoundHiddenBackpack(SoundInfo currentSoundInfo) {
		getBackpack().hiddenBackpackedSounds.remove(currentSoundInfo);
	}

	public List<Sprite> getHiddenBackpackedSprites() {
		return getBackpack().hiddenBackpackedSprites;
	}

	public void addSpriteToHiddenBackpack(Sprite sprite) {
		getBackpack().hiddenBackpackedSprites.add(sprite);
	}

	public void removeItemFromSpriteHiddenBackpack(Sprite sprite) {
		getBackpack().hiddenBackpackedSprites.remove(sprite);
	}

	public boolean backPackedSoundsContain(SoundInfo soundInfo, boolean onlyVisible) {
		List<SoundInfo> backPackedSounds = onlyVisible ? getBackPackedSounds() : getAllBackPackedSounds();
		for (SoundInfo backPackedSound : backPackedSounds) {
			if (backPackedSound.equals(soundInfo)) {
				return true;
			}
		}
		return false;
	}

	public boolean backPackedLooksContain(LookData lookData, boolean onlyVisible) {
		List<LookData> backPackedLooks = onlyVisible ? getBackPackedLooks() : getAllBackPackedLooks();
		for (LookData backPackedLook : backPackedLooks) {
			if (backPackedLook.equals(lookData)) {
				return true;
			}
		}
		return false;
	}

	public boolean backPackedSpritesContains(Sprite sprite, boolean onlyVisible) {
		List<Sprite> backPackedSprites = onlyVisible ? getBackPackedSprites() : getAllBackPackedSprites();
		for (Sprite backPackedSprite : backPackedSprites) {
			if (backPackedSprite.equals(sprite)) {
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

	public SoundBaseAdapter getCurrentSoundAdapter() {
		return currentSoundAdapter;
	}

	public void setCurrentSoundAdapter(SoundBaseAdapter adapter) {
		currentSoundAdapter = adapter;
	}

	public LookBaseAdapter getCurrentLookAdapter() {
		return currentLookAdapter;
	}

	public void setCurrentLookAdapter(LookBaseAdapter currentLookAdapter) {
		BackPackListManager.currentLookAdapter = currentLookAdapter;
	}

	public void addLookToHiddenBackPack(LookData newLookData) {
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
				soundInfo.isBackpackSoundInfo = true;
			}
			for (Sprite sprite : getAllBackPackedSprites()) {
				sprite.isBackpackObject = true;
				for (LookData lookData : sprite.getLookDataList()) {
					lookData.isBackpackLookData = true;
				}
				for (SoundInfo soundInfo : sprite.getSoundList()) {
					soundInfo.isBackpackSoundInfo = true;
				}
			}
		}
	}

	public Backpack getBackpack() {
		if (backpack == null) {
			backpack = new Backpack();
		}
		return backpack;
	}
}
