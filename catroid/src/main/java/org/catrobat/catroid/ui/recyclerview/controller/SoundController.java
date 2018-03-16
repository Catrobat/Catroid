/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.controller;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SoundController {

	private static final String BACKPACK_DIRECTORY = Utils.buildPath(
			Constants.DEFAULT_ROOT,
			Constants.BACKPACK_DIRECTORY,
			Constants.BACKPACK_SOUND_DIRECTORY);

	private UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();

	public SoundInfo copy(SoundInfo soundToCopy, Scene srcScene, Scene dstScene, Sprite dstSprite) throws IOException {
		String name = uniqueNameProvider.getUniqueName(soundToCopy.getName(), getScope(dstSprite.getSoundList()));

		String srcDir = getSoundDirPath(srcScene);
		String dstDir = getSoundDirPath(dstScene);

		String fileName = StorageHandler.copyFile(
				Utils.buildPath(srcDir, soundToCopy.getFileName()), dstDir).getName();

		SoundInfo sound = new SoundInfo(name, fileName);
		dstSprite.getSoundList().add(sound);
		return sound;
	}

	SoundInfo findOrCopy(SoundInfo soundToCopy, Scene srcScene, Scene dstScene, Sprite dstSprite) throws IOException {
		String soundToCopyPath = Utils.buildPath(getSoundDirPath(srcScene), soundToCopy.getFileName());
		for (SoundInfo sound : dstSprite.getSoundList()) {
			String soundPath = Utils.buildPath(getSoundDirPath(dstScene), sound.getFileName());

			if (compareByChecksum(soundPath, soundToCopyPath)) {
				return sound;
			}
		}
		return copy(soundToCopy, srcScene, dstScene, dstSprite);
	}

	public void delete(SoundInfo soundToDelete, Scene srcScene) throws IOException {
		StorageHandler.deleteFile(Utils.buildPath(getSoundDirPath(srcScene), soundToDelete.getFileName()));
	}

	public void deleteFromBackpack(SoundInfo soundToDelete) throws IOException {
		StorageHandler.deleteFile(Utils.buildPath(BACKPACK_DIRECTORY, soundToDelete.getFileName()));
	}

	public void pack(SoundInfo soundToPack, Scene srcScene) throws IOException {
		String name = uniqueNameProvider.getUniqueName(
				soundToPack.getName(), getScope(BackPackListManager.getInstance().getBackPackedSounds()));

		String fileName = StorageHandler.copyFile(
				Utils.buildPath(getSoundDirPath(srcScene), soundToPack.getFileName()), BACKPACK_DIRECTORY).getName();

		SoundInfo sound = new SoundInfo(name, fileName);
		sound.isBackpackSoundInfo = true;

		BackPackListManager.getInstance().getBackPackedSounds().add(sound);
		BackPackListManager.getInstance().saveBackpack();
	}

	SoundInfo packForSprite(SoundInfo soundToPack, Sprite dstSprite) throws IOException {
		for (SoundInfo sound : dstSprite.getSoundList()) {
			if (compareByChecksum(sound.getAbsolutePath(), soundToPack.getAbsolutePath())) {
				return sound;
			}
		}

		String fileName = StorageHandler.copyFile(soundToPack.getAbsolutePath(), BACKPACK_DIRECTORY).getName();
		SoundInfo sound = new SoundInfo(soundToPack.getName(), fileName);
		sound.isBackpackSoundInfo = true;

		dstSprite.getSoundList().add(sound);
		return sound;
	}

	public SoundInfo unpack(SoundInfo soundToUnpack, Scene dstScene, Sprite dstSprite) throws IOException {
		String name = uniqueNameProvider.getUniqueName(soundToUnpack.getName(), getScope(dstSprite.getSoundList()));
		String fileName = StorageHandler.copyFile(soundToUnpack.getAbsolutePath(), getSoundDirPath(dstScene)).getName();

		SoundInfo sound = new SoundInfo(name, fileName);
		dstSprite.getSoundList().add(sound);
		return sound;
	}

	SoundInfo unpackForSprite(SoundInfo soundToUnpack, Scene dstScene, Sprite dstSprite) throws IOException {
		for (SoundInfo sound : dstSprite.getSoundList()) {
			String soundPath = Utils.buildPath(getSoundDirPath(dstScene), sound.getFileName());

			if (compareByChecksum(soundPath, soundToUnpack.getAbsolutePath())) {
				return sound;
			}
		}
		return unpack(soundToUnpack, dstScene, dstSprite);
	}

	private Set<String> getScope(List<SoundInfo> items) {
		Set<String> scope = new HashSet<>();
		for (SoundInfo item : items) {
			scope.add(item.getName());
		}
		return scope;
	}

	private String getSoundDirPath(Scene scene) {
		return Utils.buildPath(scene.getPath(), Constants.SOUND_DIRECTORY);
	}

	private boolean compareByChecksum(String filePath1, String filePath2) {
		String checksum1 = Utils.md5Checksum(new File(filePath1));
		String checksum2 = Utils.md5Checksum(new File(filePath2));

		return checksum1.equals(checksum2);
	}
}
