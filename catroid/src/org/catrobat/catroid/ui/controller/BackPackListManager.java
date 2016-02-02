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

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.adapter.LookBaseAdapter;
import org.catrobat.catroid.ui.adapter.SoundBaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class BackPackListManager {
	private static final BackPackListManager INSTANCE = new BackPackListManager();

	private static SoundBaseAdapter currentSoundAdapter;
	private static LookBaseAdapter currentLookAdapter;

	private static List<SoundInfo> backpackedSounds = new CopyOnWriteArrayList<>();
	private static List<LookData> backpackedLooks = new CopyOnWriteArrayList<>();
	private static HashMap<String, List<Script>> backpackedScripts = new HashMap<>();
	private static List<Sprite> backpackedSprites = new CopyOnWriteArrayList<>();

	private static List<SoundInfo> hiddenBackpackedSounds = new CopyOnWriteArrayList<>();
	private static List<LookData> hiddenBackpackedLooks = new CopyOnWriteArrayList<>();
	private static HashMap<String, List<Script>> hiddenBackpackedScripts = new HashMap<>();
	private static List<Sprite> hiddenBackpackedSprites = new CopyOnWriteArrayList<>();

	private static boolean backpackFlag;

	private BackPackListManager() {
	}

	public static BackPackListManager getInstance() {
		return INSTANCE;
	}

	public void addLookToBackPack(LookData lookData) {
		backpackedLooks.add(lookData);
	}

	public List<LookData> getBackPackedLooks() {
		return backpackedLooks;
	}

	public void clearBackPackScripts() {
		backpackedScripts.clear();
	}

	public void removeItemFromScriptBackPack(String scriptGroup) {
		backpackedScripts.remove(scriptGroup);
	}

	public ArrayList<String> getBackPackedScriptGroups() {
		return new ArrayList<>(backpackedScripts.keySet());
	}

	public void addScriptToBackPack(String scriptGroup, List<Script> scripts) {
		backpackedScripts.put(scriptGroup, scripts);
	}

	public HashMap<String, List<Script>> getBackPackedScripts() {
		return backpackedScripts;
	}

	public void clearBackPackLooks() {
		backpackedLooks.clear();
	}

	public void removeItemFromLookBackPack(LookData lookData) {
		backpackedLooks.remove(lookData);
	}

	public List<SoundInfo> getBackPackedSounds() {
		return backpackedSounds;
	}

	public void clearBackPackSounds() {
		backpackedSounds.clear();
	}

	public void addSoundToBackPack(SoundInfo soundInfo) {
		backpackedSounds.add(soundInfo);
	}

	public void removeItemFromSoundBackPack(SoundInfo currentSoundInfo) {
		backpackedSounds.remove(currentSoundInfo);
	}

	public List<Sprite> getBackPackedSprites() {
		return backpackedSprites;
	}

	public void clearBackPackSprites() {
		backpackedSprites.clear();
	}

	public void addSpriteToBackPack(Sprite sprite) {
		backpackedSprites.add(sprite);
	}

	public void removeItemFromSpriteBackPack(Sprite sprite) {
		backpackedSprites.remove(sprite);
	}

	public List<LookData> getHiddenBackpackedLooks() {
		return hiddenBackpackedLooks;
	}

	public void removeItemFromScriptHiddenBackpack(String scriptGroup) {
		hiddenBackpackedScripts.remove(scriptGroup);
	}

	public void addScriptToHiddenBackpack(String scriptGroup, List<Script> scripts) {
		hiddenBackpackedScripts.put(scriptGroup, scripts);
	}

	public HashMap<String, List<Script>> getHiddenBackpackedScripts() {
		return hiddenBackpackedScripts;
	}

	public void removeItemFromLookHiddenBackpack(LookData lookData) {
		hiddenBackpackedLooks.remove(lookData);
	}

	public List<SoundInfo> getHiddenBackpackedSounds() {
		return hiddenBackpackedSounds;
	}

	public void addSoundToHiddenBackpack(SoundInfo soundInfo) {
		hiddenBackpackedSounds.add(soundInfo);
	}

	public void removeItemFromSoundHiddenBackpack(SoundInfo currentSoundInfo) {
		hiddenBackpackedSounds.remove(currentSoundInfo);
	}

	public List<Sprite> getHiddenBackpackedSprites() {
		return hiddenBackpackedSprites;
	}

	public void addSpriteToHiddenBackpack(Sprite sprite) {
		hiddenBackpackedSprites.add(sprite);
	}

	public void removeItemFromSpriteHiddenBackpack(Sprite sprite) {
		hiddenBackpackedSprites.remove(sprite);
	}

	public boolean backPackedSoundsContain(SoundInfo soundInfo) {
		List<SoundInfo> backPackedSounds = getAllBackPackedSounds();
		for (SoundInfo backPackedSound : backPackedSounds) {
			if (backPackedSound.equals(soundInfo)) {
				return true;
			}
		}
		return false;
	}

	public boolean backPackedLooksContain(LookData lookData) {
		List<LookData> backPackedLooks = getAllBackPackedLooks();
		for (LookData backPackedLook : backPackedLooks) {
			if (backPackedLook.equals(lookData)) {
				return true;
			}
		}
		return false;
	}

	public boolean backPackedSpritesContains(Sprite sprite) {
		List<Sprite> backPackedSprites = getAllBackPackedSprites();
		for (Sprite backPackedSprite : backPackedSprites) {
			if (backPackedSprite.equals(sprite)) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<String> getAllBackPackedScriptGroups() {
		ArrayList<String> allScriptGroups = new ArrayList<>();
		allScriptGroups.addAll(new ArrayList<>(backpackedScripts.keySet()));
		allScriptGroups.addAll(new ArrayList<>(backpackedScripts.keySet()));
		return allScriptGroups;
	}

	public List<LookData> getAllBackPackedLooks() {
		List<LookData> allLooks = new ArrayList<>();
		allLooks.addAll(backpackedLooks);
		allLooks.addAll(hiddenBackpackedLooks);
		return allLooks;
	}

	public List<SoundInfo> getAllBackPackedSounds() {
		List<SoundInfo> allSounds = new ArrayList<>();
		allSounds.addAll(backpackedSounds);
		allSounds.addAll(hiddenBackpackedSounds);
		return allSounds;
	}

	public List<Sprite> getAllBackPackedSprites() {
		List<Sprite> allSprites = new ArrayList<>();
		allSprites.addAll(backpackedSprites);
		allSprites.addAll(hiddenBackpackedSprites);
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

	public void setBackPackFlag(boolean currentBackpackFlag) {
		backpackFlag = currentBackpackFlag;
	}

	public boolean isBackpackFlag() {
		return backpackFlag;
	}

	public void addLookToHiddenBackPack(LookData newLookData) {
		hiddenBackpackedLooks.add(newLookData);
	}
}
