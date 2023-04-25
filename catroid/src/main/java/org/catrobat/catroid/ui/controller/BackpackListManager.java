/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public final class BackpackListManager {

	private static final BackpackListManager INSTANCE = new BackpackListManager();

	public final File backpackDirectory = new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, BACKPACK_DIRECTORY_NAME);
	public final File backpackFile = new File(backpackDirectory, BACKPACK_JSON_FILE_NAME);
	public final File backpackSceneDirectory = new File(backpackDirectory, BACKBACK_SCENES_DIRECTORY_NAME);
	public final File backpackSoundDirectory = new File(backpackDirectory, BACKPACK_SOUND_DIRECTORY_NAME);
	public final File backpackImageDirectory = new File(backpackDirectory, BACKPACK_IMAGE_DIRECTORY_NAME);

	private Backpack backpack = new Backpack();

	private BackpackSerializer backpackSerializer = new BackpackSerializer(backpackFile);

	public static BackpackListManager getInstance() {
		return INSTANCE;
	}

	private BackpackListManager() {
		createBackpackDirectories();
	}

	private void createBackpackDirectories() {
		DEFAULT_ROOT_DIRECTORY.mkdir();
		backpackDirectory.mkdir();
		backpackSceneDirectory.mkdir();
		backpackImageDirectory.mkdir();
		backpackSoundDirectory.mkdir();
	}

	public Backpack getBackpack() {
		return backpack;
	}

	public void removeItemFromScriptBackPack(String scriptGroup) {
		getBackpack().backpackedScripts.remove(scriptGroup);
	}

	public List<Scene> getScenes() {
		return getBackpack().backpackedScenes;
	}

	public List<Sprite> getSprites() {
		return getBackpack().backpackedSprites;
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
