/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public final class BackPackSpriteController {
	private static final BackPackSpriteController INSTANCE = new BackPackSpriteController();

	private OnBackpackSpriteCompleteListener onBackpackSpriteCompleteListener;

	private BackPackSpriteController() {
	}

	public static BackPackSpriteController getInstance() {
		return INSTANCE;
	}

	public boolean checkSpriteReplaceInBackpack(List<Sprite> currentSpriteList) {
		for (Sprite sprite : currentSpriteList) {
			if (checkSpriteReplaceInBackpack(sprite)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkSpriteReplaceInBackpack(Sprite currentSprite) {
		return BackPackListManager.getInstance().backPackedSpritesContains(currentSprite, true);
	}

	public void showBackPackReplaceDialog(final List<Sprite> currentSpriteList, final Context context) {
		Resources resources = context.getResources();
		String replaceLookMessage = resources.getString(R.string.backpack_replace_object_multiple);

		AlertDialog dialog = new CustomAlertDialogBuilder(context)
				.setTitle(R.string.backpack)
				.setMessage(replaceLookMessage)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						for (Sprite currentSprite : currentSpriteList) {
							backpackVisibleSprite(currentSprite);
						}
						onBackpackSpriteCompleteListener.onBackpackSpriteComplete(true);
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onBackpackSpriteCompleteListener.onBackpackSpriteComplete(false);
						dialog.dismiss();
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public void showBackPackReplaceDialog(final Sprite currentSprite, final Context context) {
		Resources resources = context.getResources();
		String replaceLookMessage = resources.getString(R.string.backpack_replace_object, currentSprite.getName());

		AlertDialog dialog = new CustomAlertDialogBuilder(context)
				.setTitle(R.string.backpack)
				.setMessage(replaceLookMessage)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						backpackVisibleSprite(currentSprite);
						onBackpackSpriteCompleteListener.onBackpackSpriteComplete(true);
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public void backpackVisibleSprite(Sprite spriteToEdit) {
		String spriteName = spriteToEdit.getName();
		BackPackListManager.getInstance().removeItemFromSpriteBackPackByName(spriteName);

		Sprite backPackSprite = backpack(spriteToEdit);
		BackPackListManager.getInstance().addSpriteToBackPack(backPackSprite);
	}

	public Sprite backpackHiddenSprite(Sprite spriteToEdit) {
		if (BackPackListManager.getInstance().backPackedSpritesContains(spriteToEdit, false)) {
			return spriteToEdit;
		}
		Sprite backPackSprite = backpack(spriteToEdit);
		BackPackListManager.getInstance().addSpriteToHiddenBackpack(backPackSprite);
		return backPackSprite;
	}

	public Sprite backpack(Sprite spriteToEdit) {
		ProjectManager.getInstance().setCurrentSprite(spriteToEdit);

		Sprite backPackSprite = spriteToEdit.cloneForBackPack();

		String newSpriteName = Utils.getUniqueSpriteName(spriteToEdit);
		backPackSprite.setName(newSpriteName);
		backPackSprite.isBackpackObject = true;

		for (LookData lookData : spriteToEdit.getLookDataList()) {
			if (!lookDataIsUsedInScript(lookData, ProjectManager.getInstance().getCurrentSprite())) {
				backPackSprite.getLookDataList().add(LookController.getInstance().backPackHiddenLook(lookData));
			}
		}
		for (SoundInfo soundInfo : spriteToEdit.getSoundList()) {
			if (!soundInfoIsUsedInScript(soundInfo, ProjectManager.getInstance().getCurrentSprite())) {
				backPackSprite.getSoundList().add(SoundController.getInstance().backPackHiddenSound(soundInfo));
			}
		}
		List<Script> backPackedScripts = BackPackScriptController.getInstance().backpack(backPackSprite.getName(),
				spriteToEdit.getListWithAllBricks(), true, spriteToEdit);

		if (backPackedScripts != null && !backPackedScripts.isEmpty()) {
			backPackSprite.getScriptList().addAll(backPackedScripts);
		}
		return backPackSprite;
	}

	public Sprite unpack(Sprite selectedSprite, boolean delete, boolean keepCurrentSprite, boolean
			fromHiddenBackPack, boolean asBackground) {

		if (fromHiddenBackPack && ProjectManager.getInstance().getCurrentScene().containsSprite(selectedSprite)) {
			return selectedSprite;
		}

		Sprite unpackedSprite = selectedSprite.cloneForBackPack();
		String newSpriteName = Utils.getUniqueSpriteName(selectedSprite);
		unpackedSprite.setName(newSpriteName);

		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		ProjectManager.getInstance().setCurrentSprite(unpackedSprite);

		for (LookData lookData : selectedSprite.getLookDataList()) {
			if (!lookDataIsUsedInScript(lookData, selectedSprite)) {
				LookController.getInstance().unpack(lookData, delete, true);
			}
		}
		for (SoundInfo soundInfo : selectedSprite.getSoundList()) {
			if (!soundInfoIsUsedInScript(soundInfo, selectedSprite)) {
				SoundController.getInstance().unpack(soundInfo, delete, true);
			}
		}

		BackPackScriptController.getInstance().unpack(selectedSprite.getName(), delete, false, null, true);
		selectedSprite.setUserAndVariableBrickReferences(unpackedSprite, unpackedSprite.getUserBrickList());

		if (asBackground) {
			ProjectManager.getInstance().getCurrentScene().replaceBackgroundSprite(unpackedSprite);
		} else {
			ProjectManager.getInstance().addSprite(unpackedSprite);
		}

		if (keepCurrentSprite) {
			ProjectManager.getInstance().setCurrentSprite(currentSprite);
		} else {
			ProjectManager.getInstance().setCurrentSprite(unpackedSprite);
		}

		if (delete) {
			if (fromHiddenBackPack) {
				BackPackListManager.getInstance().removeItemFromSpriteHiddenBackpack(selectedSprite);
			} else {
				BackPackListManager.getInstance().removeItemFromSpriteBackPack(selectedSprite);
			}
		}
		return unpackedSprite;
	}

	private boolean lookDataIsUsedInScript(LookData lookData, Sprite sprite) {
		for (Brick brick : sprite.getListWithAllBricks()) {
			if (brick instanceof SetLookBrick && ((SetLookBrick) brick).getLook().equals(lookData)) {
				return true;
			}
		}
		return false;
	}

	private boolean soundInfoIsUsedInScript(SoundInfo soundInfo, Sprite sprite) {
		for (Brick brick : sprite.getListWithAllBricks()) {
			if (brick instanceof PlaySoundBrick && ((PlaySoundBrick) brick).getSound().equals(soundInfo)) {
				return true;
			}
		}
		return false;
	}

	public void setOnBackpackSpriteCompleteListener(OnBackpackSpriteCompleteListener listener) {
		onBackpackSpriteCompleteListener = listener;
	}

	public interface OnBackpackSpriteCompleteListener {
		void onBackpackSpriteComplete(boolean startBackpackActivity);
	}
}
