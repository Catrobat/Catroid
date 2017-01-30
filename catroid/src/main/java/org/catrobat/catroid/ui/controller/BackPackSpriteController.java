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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public final class BackPackSpriteController {

	public static final String TAG = BackPackSpriteController.class.getSimpleName();

	public static boolean existsInBackPack(List<Sprite> spriteList) {
		for (Sprite sprite : spriteList) {
			if (existsInBackPack(sprite)) {
				return true;
			}
		}
		return false;
	}

	public static boolean existsInBackPack(Sprite sprite) {
		return BackPackListManager.getInstance().backPackedSpritesContains(sprite, true);
	}

	public static boolean backpack(List<Sprite> spriteList, boolean visible) {
		for (Sprite sprite : spriteList) {
			if(!backpack(sprite, visible)) {
				return false;
			}
		}
		return true;
	}

	public static boolean backpack(Sprite sprite, boolean visible) {
		if (visible) {
			return backpackVisible(sprite) != null;
		} else {
			return backpackHidden(sprite) != null;
		}
	}

	public static Sprite backpackVisible(Sprite sprite) {
		BackPackListManager.getInstance().removeItemFromSpriteBackPackByName(sprite.getName());
		Sprite backpackedSprite = copyToBackpack(sprite);
		BackPackListManager.getInstance().addSpriteToBackPack(backpackedSprite);
		return backpackedSprite;
	}

	public static Sprite backpackHidden(Sprite sprite) {
		if (BackPackListManager.getInstance().backPackedSpritesContains(sprite, false)) {
			BackPackListManager.getInstance().addSpriteToHiddenBackpack(sprite);
			return sprite;
		}
		Sprite backpackedSprite = copyToBackpack(sprite);
		BackPackListManager.getInstance().addSpriteToHiddenBackpack(backpackedSprite);
		return backpackedSprite;
	}

	public static Sprite copyToBackpack(Sprite sprite) {
		ProjectManager.getInstance().setCurrentSprite(sprite);
		Sprite backPackSprite = sprite.cloneForBackPack();

		String newSpriteName = Utils.getUniqueSpriteName(sprite);
		backPackSprite.setName(newSpriteName);
		backPackSprite.isBackpackObject = true;

		for (LookData lookData : sprite.getLookDataList()) {
			if (!lookDataIsUsedInScript(lookData, ProjectManager.getInstance().getCurrentSprite())) {
				backPackSprite.getLookDataList().add(OldLookController.getInstance().backPackHiddenLook(lookData));
			}
		}
		for (SoundInfo soundInfo : sprite.getSoundList()) {
			if (!soundInfoIsUsedInScript(soundInfo, ProjectManager.getInstance().getCurrentSprite())) {
				backPackSprite.getSoundList().add(OldSoundController.getInstance().backPackHiddenSound(soundInfo));
			}
		}
		List<Script> backPackedScripts = BackPackScriptController.getInstance().backpack(backPackSprite.getName(),
				sprite.getListWithAllBricks(), true, sprite);

		if (backPackedScripts != null && !backPackedScripts.isEmpty()) {
			backPackSprite.getScriptList().addAll(backPackedScripts);
		}

		return backPackSprite;
	}

	public static boolean unpack(Sprite sprite, boolean visible, boolean asBackground) {
		return visible ? unpackVisible(sprite, asBackground) != null : unpackHidden(sprite, asBackground) != null;
	}

	public static Sprite unpackVisible(Sprite sprite, boolean asBackground) {
		Sprite unpackedSprite = sprite.cloneForBackPack();
		String newSpriteName = Utils.getUniqueSpriteName(sprite);
		unpackedSprite.setName(newSpriteName);

		ProjectManager.getInstance().setCurrentSprite(unpackedSprite);

		for (LookData lookData : sprite.getLookDataList()) {
			if (!lookDataIsUsedInScript(lookData, sprite)) {
				OldLookController.getInstance().unpack(lookData, true, true);
			}
		}
		for (SoundInfo soundInfo : sprite.getSoundList()) {
			if (!soundInfoIsUsedInScript(soundInfo, sprite)) {
				OldSoundController.getInstance().unpack(soundInfo, false, true);
			}
		}

		BackPackScriptController.getInstance().unpack(sprite.getName(), false, false, null, true);
		sprite.setUserAndVariableBrickReferences(unpackedSprite, unpackedSprite.getUserBrickList());

		if (asBackground) {
			ProjectManager.getInstance().getCurrentScene().replaceBackgroundSprite(unpackedSprite);
		} else {
			ProjectManager.getInstance().addSprite(unpackedSprite);
		}

		return unpackedSprite;
	}

	public static Sprite unpackHidden(Sprite sprite, boolean asBackground) {
		if (ProjectManager.getInstance().getCurrentScene().containsSprite(sprite)) {
			return sprite;
		}
		return unpackVisible(sprite, asBackground);
	}

	private static boolean lookDataIsUsedInScript(LookData lookData, Sprite sprite) {
		for (Brick brick : sprite.getListWithAllBricks()) {
			if (brick instanceof SetLookBrick && ((SetLookBrick) brick).getLook().equals(lookData)) {
				return true;
			}
		}
		return false;
	}

	private static boolean soundInfoIsUsedInScript(SoundInfo soundInfo, Sprite sprite) {
		for (Brick brick : sprite.getListWithAllBricks()) {
			if (brick instanceof PlaySoundBrick && ((PlaySoundBrick) brick).getSound().equals(soundInfo)) {
				return true;
			}
		}
		return false;
	}
}
