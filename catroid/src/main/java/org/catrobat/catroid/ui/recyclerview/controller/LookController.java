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

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;

public class LookController {

	private UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();

	public LookData copy(LookData lookToCopy, Scene dstScene, Sprite dstSprite) throws IOException {
		String name = uniqueNameProvider.getUniqueNameInNameables(lookToCopy.getName(), dstSprite.getLookList());

		File dstDir = getImageDir(dstScene);
		File file = StorageOperations.copyFileToDir(lookToCopy.getFile(), dstDir);

		return new LookData(name, file);
	}

	LookData findOrCopy(LookData lookToCopy, Scene dstScene, Sprite dstSprite) throws IOException {
		for (LookData look : dstSprite.getLookList()) {
			if (compareByChecksum(look.getFile(), lookToCopy.getFile())) {
				return look;
			}
		}
		LookData copiedLook = copy(lookToCopy, dstScene, dstSprite);
		dstSprite.getLookList().add(copiedLook);
		return copiedLook;
	}

	public void delete(LookData lookToDelete) throws IOException {
		StorageOperations.deleteFile(lookToDelete.getFile());
	}

	public LookData pack(LookData lookToPack) throws IOException {
		String name = uniqueNameProvider.getUniqueNameInNameables(
				lookToPack.getName(), BackpackListManager.getInstance().getBackpackedLooks());

		File file = StorageOperations.copyFileToDir(lookToPack.getFile(),
				BackpackListManager.getInstance().backpackImageDirectory);

		return new LookData(name, file);
	}

	LookData packForSprite(LookData lookToPack, Sprite dstSprite) throws IOException {
		for (LookData look : dstSprite.getLookList()) {
			if (compareByChecksum(look.getFile(), lookToPack.getFile())) {
				return look;
			}
		}
		File file = StorageOperations.copyFileToDir(lookToPack.getFile(), BackpackListManager.getInstance().backpackImageDirectory);
		LookData look = new LookData(lookToPack.getName(), file);
		dstSprite.getLookList().add(look);

		return look;
	}

	public LookData unpack(LookData lookToUnpack, Scene dstScene, Sprite dstSprite) throws IOException {
		String name = uniqueNameProvider.getUniqueNameInNameables(lookToUnpack.getName(), dstSprite.getLookList());
		File file = StorageOperations.copyFileToDir(lookToUnpack.getFile(), getImageDir(dstScene));
		return new LookData(name, file);
	}

	LookData unpackForSprite(LookData lookToUnpack, Scene dstScene, Sprite dstSprite) throws IOException {
		for (LookData look : dstSprite.getLookList()) {
			if (compareByChecksum(look.getFile(), lookToUnpack.getFile())) {
				return look;
			}
		}

		LookData lookData = unpack(lookToUnpack, dstScene, dstSprite);
		dstSprite.getLookList().add(lookData);
		return lookData;
	}

	private File getImageDir(Scene scene) {
		return new File(scene.getDirectory(), IMAGE_DIRECTORY_NAME);
	}

	private boolean compareByChecksum(File file1, File file2) {
		// The backpack uses this method and because the backpack does not correctly
		// de-serialize LookData references we can end up with a new LookData that has a null file reference
		// although we actually need one that already exists. so the workaround here (prevents a lot of crashes) is
		// to just hope that copying bricks with references to files always happens after the LookDatas are copied.
		if (file1 == null || file2 == null) {
			return true;
		}

		String checksum1 = Utils.md5Checksum(file1);
		String checksum2 = Utils.md5Checksum(file2);

		return checksum1.equals(checksum2);
	}
}
