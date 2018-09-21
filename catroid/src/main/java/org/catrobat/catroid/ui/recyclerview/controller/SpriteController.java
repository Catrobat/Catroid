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

import android.util.Log;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.fragment.SpriteFactory;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;

import java.io.IOException;

public class SpriteController {

	public static final String TAG = SpriteController.class.getSimpleName();

	private UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();
	private ScriptController scriptController = new ScriptController();
	private LookController lookController = new LookController();
	private SoundController soundController = new SoundController();

	public Sprite convert(Sprite spriteToConvert, Scene parentScene) {
		Sprite convertedSprite = spriteToConvert.convert();
		parentScene.getDataContainer().updateSpriteUserDataMapping(spriteToConvert, convertedSprite);
		return convertedSprite;
	}

	public Sprite copy(Sprite spriteToCopy, Scene srcScene, Scene dstScene) throws IOException {
		String name = uniqueNameProvider.getUniqueNameInNameables(spriteToCopy.getName(), dstScene.getSpriteList());
		Sprite sprite = new SpriteFactory().newInstance(spriteToCopy.getClass().getSimpleName(), name);

		for (LookData look : spriteToCopy.getLookList()) {
			sprite.getLookList().add(lookController.copy(look, dstScene, sprite));
		}

		for (SoundInfo sound : spriteToCopy.getSoundList()) {
			sprite.getSoundList().add(soundController.copy(sound, dstScene, sprite));
		}

		for (NfcTagData nfcTag : spriteToCopy.getNfcTagList()) {
			sprite.getNfcTagList().add(nfcTag.clone());
		}

		dstScene.getDataContainer().copySpriteUserData(spriteToCopy, srcScene.getDataContainer(), sprite);

		for (Script script : spriteToCopy.getScriptList()) {
			try {
				sprite.addScript(scriptController.copy(script, dstScene, sprite));
			} catch (CloneNotSupportedException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		return sprite;
	}

	public void delete(Sprite spriteToDelete) {
		for (LookData look : spriteToDelete.getLookList()) {
			try {
				lookController.delete(look);
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		for (SoundInfo sound : spriteToDelete.getSoundList()) {
			try {
				soundController.delete(sound);
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
	}

	public void delete(Sprite spriteToDelete, Scene srcScene) {
		if (spriteToDelete.isClone) {
			throw new IllegalStateException("You are deleting a clone: this means you also delete the files that are "
					+ "referenced by the original sprite because clones are shallow copies regarding files.");
		}
		delete(spriteToDelete);
		srcScene.getDataContainer().removeSpriteUserData(spriteToDelete);
	}

	public Sprite pack(Sprite spriteToPack) throws IOException {
		String name = uniqueNameProvider
				.getUniqueNameInNameables(spriteToPack.getName(), BackpackListManager.getInstance().getSprites());

		Sprite sprite = new Sprite(name);

		for (LookData look : spriteToPack.getLookList()) {
			lookController.packForSprite(look, sprite);
		}

		for (SoundInfo sound : spriteToPack.getSoundList()) {
			soundController.packForSprite(sound, sprite);
		}

		for (NfcTagData nfcTag : spriteToPack.getNfcTagList()) {
			sprite.getNfcTagList().add(nfcTag.clone());
		}

		for (Script script : spriteToPack.getScriptList()) {
			try {
				scriptController.packForSprite(script, sprite);
			} catch (CloneNotSupportedException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		return sprite;
	}

	public Sprite unpack(Sprite spriteToUnpack, Scene dstScene) throws IOException {
		String name = uniqueNameProvider.getUniqueNameInNameables(spriteToUnpack.getName(), dstScene.getSpriteList());
		Sprite sprite = new Sprite(name);

		for (LookData look : spriteToUnpack.getLookList()) {
			lookController.unpackForSprite(look, dstScene, sprite);
		}

		for (SoundInfo sound : spriteToUnpack.getSoundList()) {
			soundController.unpackForSprite(sound, dstScene, sprite);
		}

		for (NfcTagData nfcTag : spriteToUnpack.getNfcTagList()) {
			sprite.getNfcTagList().add(nfcTag.clone());
		}

		for (Script script : spriteToUnpack.getScriptList()) {
			try {
				scriptController.unpackForSprite(script, dstScene, sprite);
			} catch (CloneNotSupportedException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		return sprite;
	}
}
