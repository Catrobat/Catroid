/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import org.catrobat.catroid.common.Backpack;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.io.BackpackSerializer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.catrobat.catroid.common.Constants.BACKBACK_SCENES_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.BACKPACK_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.BACKPACK_IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.BACKPACK_JSON_FILE_NAME;
import static org.catrobat.catroid.common.Constants.BACKPACK_SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;

public final class BackpackListManager {

	private static BackpackListManager instance = null;
	public final File backpackDirectory;
	public final File backpackFile;
	public final File backpackSceneDirectory;
	public final File backpackSoundDirectory;
	public final File backpackImageDirectory;
	private final BackpackSerializer backpackSerializer;
	private Backpack backpack;

	public static BackpackListManager getInstance() {
		if (instance == null) {
			instance = new BackpackListManager();
		}
		return instance;
	}

	private BackpackListManager() {
		backpackDirectory = new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, BACKPACK_DIRECTORY_NAME);
		backpackFile = new File(backpackDirectory, BACKPACK_JSON_FILE_NAME);
		backpackSceneDirectory = new File(backpackDirectory, BACKBACK_SCENES_DIRECTORY_NAME);
		backpackSoundDirectory = new File(backpackDirectory, BACKPACK_SOUND_DIRECTORY_NAME);
		backpackImageDirectory = new File(backpackDirectory, BACKPACK_IMAGE_DIRECTORY_NAME);
		backpack = new Backpack();
		backpackSerializer = new BackpackSerializer(backpackFile);
		createBackpackDirectories();
	}

	private void createBackpackDirectories() {
		createDirectory(backpackDirectory);
		createDirectory(backpackSceneDirectory);
		createDirectory(backpackImageDirectory);
		createDirectory(backpackSoundDirectory);
	}

	private void createDirectory(File directory) {
		directory.mkdir();
		if (!directory.exists()) {
			throw new RuntimeException("Could not create directory: " + directory.getAbsolutePath());
		}
	}

	public Backpack getBackpack() {
		return backpack;
	}

	public void removeItemFromScriptBackPack(String scriptGroup) {
		getBackpack().backpackedScripts.remove(scriptGroup);
		getBackpack().backpackedUserVariables.remove(scriptGroup);
		getBackpack().backpackedUserLists.remove(scriptGroup);
		getBackpack().backpackedUserDefinedBricks.remove(scriptGroup);
	}

	public List<Scene> getScenes() {
		return getBackpack().backpackedScenes;
	}

	public List<Sprite> getSprites() {
		return getBackpack().backpackedSprites;
	}

	public void replaceBackpackedSprites(List<Sprite> list) {
		getBackpack().backpackedSprites.clear();
		getBackpack().backpackedSprites.addAll(list);
	}

	public void replaceBackpackedLooks(List<LookData> list) {
		getBackpack().backpackedLooks.clear();
		getBackpack().backpackedLooks.addAll(list);
	}

	public void replaceBackpackedSounds(List<SoundInfo> list) {
		getBackpack().backpackedSounds.clear();
		getBackpack().backpackedSounds.addAll(list);
	}

	public void replaceBackpackedScenes(List<Scene> list) {
		getBackpack().backpackedScenes.clear();
		getBackpack().backpackedScenes.addAll(list);
	}

	public void replaceBackpackedScripts(HashMap<String, List<Script>> map) {
		getBackpack().backpackedScripts.clear();
		getBackpack().backpackedScripts.putAll(map);
	}

	public List<String> getBackpackedScriptGroups() {
		return new ArrayList<>(getBackpack().backpackedScripts.keySet());
	}

	public HashMap<String, List<Script>> getBackpackedScripts() {
		return getBackpack().backpackedScripts;
	}

	public HashMap<String, List<UserDefinedBrick>> getBackpackedUserDefinedBricks() {
		return getBackpack().backpackedUserDefinedBricks;
	}

	public void addScriptToBackPack(String scriptGroup, List<Script> scripts) {
		getBackpack().backpackedScripts.put(scriptGroup, scripts);
	}

	public void addUserDefinedBrickToBackPack(String scriptGroup, List<UserDefinedBrick> userDefinedBricks) {
		getBackpack().backpackedUserDefinedBricks.put(scriptGroup, userDefinedBricks);
	}

	public List<LookData> getBackpackedLooks() {
		return getBackpack().backpackedLooks;
	}

	public List<SoundInfo> getBackpackedSounds() {
		return getBackpack().backpackedSounds;
	}

	public boolean isBackpackEmpty() {
		return getBackpackedLooks().isEmpty() && getBackpackedScriptGroups().isEmpty()
				&& getBackpackedSounds().isEmpty() && getSprites().isEmpty();
	}

	public void saveBackpack() {
		backpackSerializer.saveBackpack(getBackpack());
	}

	public void setBackpack(Backpack backpack) {
		this.backpack = backpack;
	}

	public void loadBackpack() {
		backpack = backpackSerializer.loadBackpack();

		for (Scene scene : getScenes()) {
			setSpriteFileReferences(scene.getSpriteList(), scene.getDirectory());
		}

		for (Sprite sprite : getSprites()) {
			setLookFileReferences(sprite.getLookList(),
					backpackImageDirectory);
			setSoundFileReferences(sprite.getSoundList(),
					backpackSoundDirectory);
		}

		setLookFileReferences(getBackpackedLooks(), backpackImageDirectory);
		setSoundFileReferences(getBackpackedSounds(), backpackSoundDirectory);

		for (Iterable<Script> scripts : getBackpackedScripts().values()) {
			for (Script script : scripts) {
				script.setParents();
			}
		}
	}

	private void setSpriteFileReferences(List<Sprite> sprites, File parentDir) {
		for (Sprite sprite : sprites) {
			setLookFileReferences(sprite.getLookList(), new File(parentDir, IMAGE_DIRECTORY_NAME));
			setSoundFileReferences(sprite.getSoundList(), new File(parentDir, SOUND_DIRECTORY_NAME));
		}
	}

	private void setLookFileReferences(List<LookData> looks, File parentDir) {
		for (Iterator<LookData> iterator = looks.iterator(); iterator.hasNext(); ) {
			LookData lookData = iterator.next();
			File lookFile = new File(parentDir, lookData.getXstreamFileName());

			if (lookFile.exists()) {
				lookData.setFile(lookFile);
			} else {
				iterator.remove();
			}
		}
	}

	private void setSoundFileReferences(List<SoundInfo> sounds, File parentDir) {
		for (Iterator<SoundInfo> iterator = sounds.iterator(); iterator.hasNext(); ) {
			SoundInfo soundInfo = iterator.next();
			File soundFile = new File(parentDir, soundInfo.getXstreamFileName());

			if (soundFile.exists()) {
				soundInfo.setFile(soundFile);
			} else {
				iterator.remove();
			}
		}
	}
}
