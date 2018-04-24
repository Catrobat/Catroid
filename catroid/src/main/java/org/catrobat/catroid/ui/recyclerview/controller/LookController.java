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
import org.catrobat.catroid.common.LookData;
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

public class LookController {

	private UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();

	public LookData copy(LookData lookToCopy, Scene srcScene, Scene dstScene, Sprite dstSprite) throws IOException {
		String name = uniqueNameProvider.getUniqueName(lookToCopy.getName(), getScope(dstSprite.getLookList()));

		File srcDir = getImageDir(srcScene);
		File dstDir = getImageDir(dstScene);

		String fileName = StorageHandler.copyFileToDirectory(new File(srcDir, lookToCopy.getFileName()), dstDir).getName();
		return new LookData(name, fileName);
	}

	LookData findOrCopy(LookData lookToCopy, Scene srcScene, Scene dstScene, Sprite dstSprite) throws IOException {
		File lookFileToCopy = new File(getImageDir(srcScene), lookToCopy.getFileName());
		for (LookData look : dstSprite.getLookList()) {
			File lookFile = new File(getImageDir(dstScene), look.getFileName());

			if (compareByChecksum(lookFile, lookFileToCopy)) {
				return look;
			}
		}
		LookData copiedLook = copy(lookToCopy, srcScene, dstScene, dstSprite);
		dstSprite.getLookList().add(copiedLook);
		return copiedLook;
	}

	public void delete(LookData lookToDelete, Scene scrScene) throws IOException {
		StorageHandler.deleteFile(new File(getImageDir(scrScene), lookToDelete.getFileName()));
	}

	public void deleteFromBackpack(LookData lookToDelete) throws IOException {
		StorageHandler.deleteFile(new File(Constants.BACKPACK_IMAGE_DIRECTORY, lookToDelete.getFileName()));
	}

	public LookData pack(LookData lookToPack, Scene srcScene) throws IOException {
		String name = uniqueNameProvider.getUniqueName(
				lookToPack.getName(), getScope(BackPackListManager.getInstance().getBackPackedLooks()));

		String fileName = StorageHandler.copyFileToDirectory(
				new File(getImageDir(srcScene), lookToPack.getFileName()),
				Constants.BACKPACK_IMAGE_DIRECTORY).getName();

		LookData look = new LookData(name, fileName);
		look.isBackpackLookData = true;
		return look;
	}

	LookData packForSprite(LookData lookToPack, Sprite dstSprite) throws IOException {
		for (LookData look : dstSprite.getLookList()) {
			if (compareByChecksum(look.getFile(), lookToPack.getFile())) {
				return look;
			}
		}
		String fileName = StorageHandler.copyFileToDirectory(lookToPack.getFile(), Constants.BACKPACK_IMAGE_DIRECTORY).getName();
		LookData look = new LookData(lookToPack.getName(), fileName);
		look.isBackpackLookData = true;

		dstSprite.getLookList().add(look);
		return look;
	}

	public LookData unpack(LookData lookToUnpack, Scene dstScene, Sprite dstSprite) throws IOException {
		String name = uniqueNameProvider.getUniqueName(lookToUnpack.getName(), getScope(dstSprite.getLookList()));
		String fileName = StorageHandler.copyFileToDirectory(lookToUnpack.getFile(), getImageDir(dstScene)).getName();
		return new LookData(name, fileName);
	}

	LookData unpackForSprite(LookData lookToUnpack, Scene dstScene, Sprite dstSprite) throws IOException {
		for (LookData look : dstSprite.getLookList()) {
			File lookFile = new File(getImageDir(dstScene), look.getFileName());

			if (compareByChecksum(lookFile, lookToUnpack.getFile())) {
				return look;
			}
		}
		LookData lookData = unpack(lookToUnpack, dstScene, dstSprite);
		dstSprite.getLookList().add(lookData);
		return lookData;
	}

	private Set<String> getScope(List<LookData> items) {
		Set<String> scope = new HashSet<>();
		for (LookData item : items) {
			scope.add(item.getName());
		}
		return scope;
	}

	private File getImageDir(Scene scene) {
		return new File(scene.getDirectory(), Constants.IMAGE_DIRECTORY);
	}

	private boolean compareByChecksum(File file1, File file2) {
		String checksum1 = Utils.md5Checksum(file1);
		String checksum2 = Utils.md5Checksum(file2);

		return checksum1.equals(checksum2);
	}
}
