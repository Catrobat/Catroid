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
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LookController {

	private UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();

	public LookData copy(LookData lookToCopy, Scene dstScene, Sprite dstSprite) throws IOException {
		String name = uniqueNameProvider.getUniqueName(lookToCopy.getName(), getScope(dstSprite.getLookList()));

		File dstDir = getImageDir(dstScene);
		File file = StorageOperations.copyFileToDir(lookToCopy.getFile(), dstDir);

		return new LookData(name, file);
	}

	LookData findOrCopy(LookData lookToCopy, Scene dstScene, Sprite dstSprite) throws IOException {
		for (LookData look : dstSprite.getLookList()) {
			if (look.getFile().equals(lookToCopy.getFile())) {
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
		String name = uniqueNameProvider.getUniqueName(
				lookToPack.getName(), getScope(BackpackListManager.getInstance().getBackpackedLooks()));

		File file = StorageOperations.copyFileToDir(lookToPack.getFile(), Constants.BACKPACK_IMAGE_DIRECTORY);

		return new LookData(name, file);
	}

	LookData packForSprite(LookData lookToPack, Sprite dstSprite) throws IOException {
		for (LookData look : dstSprite.getLookList()) {
			if (look.getFile().equals(lookToPack.getFile())) {
				return look;
			}
		}
		File file = StorageOperations.copyFileToDir(lookToPack.getFile(), Constants.BACKPACK_IMAGE_DIRECTORY);
		LookData look = new LookData(lookToPack.getName(), file);
		dstSprite.getLookList().add(look);

		return look;
	}

	public LookData unpack(LookData lookToUnpack, Scene dstScene, Sprite dstSprite) throws IOException {
		String name = uniqueNameProvider.getUniqueName(lookToUnpack.getName(), getScope(dstSprite.getLookList()));
		File file = StorageOperations.copyFileToDir(lookToUnpack.getFile(), getImageDir(dstScene));
		return new LookData(name, file);
	}

	LookData unpackForSprite(LookData lookToUnpack, Scene dstScene, Sprite dstSprite) throws IOException {
		for (LookData look : dstSprite.getLookList()) {
			if (look.getFile().equals(lookToUnpack.getFile())) {
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
		return new File(scene.getDirectory(), Constants.IMAGE_DIRECTORY_NAME);
	}
}
