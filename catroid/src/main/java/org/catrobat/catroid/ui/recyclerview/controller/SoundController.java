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

	private UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();

	public SoundInfo copy(SoundInfo soundToCopy, Scene srcScene, Scene dstScene, Sprite dstSprite) throws IOException {
		String name = uniqueNameProvider.getUniqueName(soundToCopy.getName(), getScope(dstSprite.getSoundList()));

		File srcDir = getSoundDir(srcScene);
		File dstDir = getSoundDir(dstScene);

		String fileName = StorageHandler.copyFileToDirectory(new File(srcDir, soundToCopy.getFileName()), dstDir).getName();
		return new SoundInfo(name, fileName);
	}

	SoundInfo findOrCopy(SoundInfo soundToCopy, Scene srcScene, Scene dstScene, Sprite dstSprite) throws IOException {
		File soundFileToCopy = new File(getSoundDir(srcScene), soundToCopy.getFileName());
		for (SoundInfo sound : dstSprite.getSoundList()) {
			File soundFile = new File(getSoundDir(dstScene), sound.getFileName());

			if (compareByChecksum(soundFile, soundFileToCopy)) {
				return sound;
			}
		}
		SoundInfo copiedSound = copy(soundToCopy, srcScene, dstScene, dstSprite);
		dstSprite.getSoundList().add(copiedSound);
		return copiedSound;
	}

	public void delete(SoundInfo soundToDelete, Scene srcScene) throws IOException {
		StorageHandler.deleteFile(new File(getSoundDir(srcScene), soundToDelete.getFileName()));
	}

	public void deleteFromBackpack(SoundInfo soundToDelete) throws IOException {
		StorageHandler.deleteFile(new File(Constants.BACKPACK_SOUND_DIRECTORY, soundToDelete.getFileName()));
	}

	public SoundInfo pack(SoundInfo soundToPack, Scene srcScene) throws IOException {
		String name = uniqueNameProvider.getUniqueName(
				soundToPack.getName(), getScope(BackPackListManager.getInstance().getBackPackedSounds()));

		String fileName = StorageHandler.copyFileToDirectory(
				new File(getSoundDir(srcScene), soundToPack.getFileName()),
				Constants.BACKPACK_SOUND_DIRECTORY).getName();

		SoundInfo sound = new SoundInfo(name, fileName);
		sound.isBackpackSoundInfo = true;
		return sound;
	}

	SoundInfo packForSprite(SoundInfo soundToPack, Sprite dstSprite) throws IOException {
		for (SoundInfo sound : dstSprite.getSoundList()) {
			if (compareByChecksum(sound.getFile(), soundToPack.getFile())) {
				return sound;
			}
		}
		String fileName = StorageHandler.copyFileToDirectory(soundToPack.getFile(), Constants.BACKPACK_SOUND_DIRECTORY).getName();
		SoundInfo sound = new SoundInfo(soundToPack.getName(), fileName);
		sound.isBackpackSoundInfo = true;

		dstSprite.getSoundList().add(sound);
		return sound;
	}

	public SoundInfo unpack(SoundInfo soundToUnpack, Scene dstScene, Sprite dstSprite) throws IOException {
		String name = uniqueNameProvider.getUniqueName(soundToUnpack.getName(), getScope(dstSprite.getSoundList()));
		String fileName = StorageHandler.copyFileToDirectory(soundToUnpack.getFile(), getSoundDir(dstScene)).getName();
		return new SoundInfo(name, fileName);
	}

	SoundInfo unpackForSprite(SoundInfo soundToUnpack, Scene dstScene, Sprite dstSprite) throws IOException {
		for (SoundInfo sound : dstSprite.getSoundList()) {
			File soundFile = new File(getSoundDir(dstScene), sound.getFileName());

			if (compareByChecksum(soundFile, soundToUnpack.getFile())) {
				return sound;
			}
		}
		SoundInfo soundInfo = unpack(soundToUnpack, dstScene, dstSprite);
		dstSprite.getSoundList().add(soundInfo);
		return soundInfo;
	}

	private Set<String> getScope(List<SoundInfo> items) {
		Set<String> scope = new HashSet<>();
		for (SoundInfo item : items) {
			scope.add(item.getName());
		}
		return scope;
	}

	private File getSoundDir(Scene scene) {
		return new File(scene.getDirectory(), Constants.SOUND_DIRECTORY);
	}

	private boolean compareByChecksum(File file1, File file2) {
		String checksum1 = Utils.md5Checksum(file1);
		String checksum2 = Utils.md5Checksum(file2);

		return checksum1.equals(checksum2);
	}
}
