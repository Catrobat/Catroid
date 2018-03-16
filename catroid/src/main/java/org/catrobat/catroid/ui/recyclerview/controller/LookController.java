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

	private static final String BACKPACK_DIRECTORY = Utils.buildPath(
			Constants.DEFAULT_ROOT,
			Constants.BACKPACK_DIRECTORY,
			Constants.BACKPACK_IMAGE_DIRECTORY);

	private UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();

	public LookData copy(LookData lookToCopy, Scene srcScene, Scene dstScene, Sprite dstSprite) throws IOException {
		String name = uniqueNameProvider.getUniqueName(lookToCopy.getName(), getScope(dstSprite.getLookList()));

		String srcDir = getImageDirPath(srcScene);
		String dstDir = getImageDirPath(dstScene);

		String fileName = StorageHandler.copyFile(Utils.buildPath(srcDir, lookToCopy.getFileName()), dstDir).getName();

		LookData look = new LookData(name, fileName);
		dstSprite.getLookList().add(look);
		return look;
	}

	LookData findOrCopy(LookData lookToCopy, Scene srcScene, Scene dstScene, Sprite dstSprite) throws IOException {
		String lookToCopyPath = Utils.buildPath(getImageDirPath(srcScene), lookToCopy.getFileName());

		for (LookData look : dstSprite.getLookList()) {
			String lookPath = Utils.buildPath(getImageDirPath(dstScene), look.getFileName());

			if (compareByChecksum(lookPath, lookToCopyPath)) {
				return look;
			}
		}
		return copy(lookToCopy, srcScene, dstScene, dstSprite);
	}

	public void delete(LookData lookToDelete, Scene scrScene) throws IOException {
		StorageHandler.deleteFile(Utils.buildPath(getImageDirPath(scrScene), lookToDelete.getFileName()));
	}

	public void deleteFromBackpack(LookData lookToDelete) throws IOException {
		StorageHandler.deleteFile(Utils.buildPath(BACKPACK_DIRECTORY, lookToDelete.getFileName()));
	}

	public void pack(LookData lookToPack, Scene srcScene) throws IOException {
		String name = uniqueNameProvider.getUniqueName(
				lookToPack.getName(), getScope(BackPackListManager.getInstance().getBackPackedLooks()));

		String fileName = StorageHandler.copyFile(
				Utils.buildPath(getImageDirPath(srcScene), lookToPack.getFileName()), BACKPACK_DIRECTORY).getName();

		LookData look = new LookData(name, fileName);
		look.isBackpackLookData = true;

		BackPackListManager.getInstance().getBackPackedLooks().add(look);
		BackPackListManager.getInstance().saveBackpack();
	}

	LookData packForSprite(LookData lookToPack, Sprite dstSprite) throws IOException {
		for (LookData look : dstSprite.getLookList()) {
			if (compareByChecksum(look.getAbsolutePath(), lookToPack.getAbsolutePath())) {
				return look;
			}
		}

		String fileName = StorageHandler.copyFile(lookToPack.getAbsolutePath(), BACKPACK_DIRECTORY).getName();
		LookData look = new LookData(lookToPack.getName(), fileName);
		look.isBackpackLookData = true;

		dstSprite.getLookList().add(look);
		return look;
	}

	public LookData unpack(LookData lookToUnpack, Scene dstScene, Sprite dstSprite) throws IOException {
		String name = uniqueNameProvider.getUniqueName(lookToUnpack.getName(), getScope(dstSprite.getLookList()));
		String fileName = StorageHandler.copyFile(lookToUnpack.getAbsolutePath(), getImageDirPath(dstScene)).getName();

		LookData look = new LookData(name, fileName);
		dstSprite.getLookList().add(look);
		return look;
	}

	LookData unpackForSprite(LookData lookToUnpack, Scene dstScene, Sprite dstSprite) throws IOException {
		for (LookData look : dstSprite.getLookList()) {
			String lookPath = Utils.buildPath(getImageDirPath(dstScene), look.getFileName());

			if (compareByChecksum(lookPath, lookToUnpack.getAbsolutePath())) {
				return look;
			}
		}
		return unpack(lookToUnpack, dstScene, dstSprite);
	}

	private Set<String> getScope(List<LookData> items) {
		Set<String> scope = new HashSet<>();
		for (LookData item : items) {
			scope.add(item.getName());
		}
		return scope;
	}

	private String getImageDirPath(Scene scene) {
		return Utils.buildPath(scene.getPath(), Constants.IMAGE_DIRECTORY);
	}

	private boolean compareByChecksum(String filePath1, String filePath2) {
		String checksum1 = Utils.md5Checksum(new File(filePath1));
		String checksum2 = Utils.md5Checksum(new File(filePath2));

		return checksum1.equals(checksum2);
	}
}
