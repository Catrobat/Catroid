/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import android.graphics.PointF;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.GroupItemSprite;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;
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

	public Sprite convert(Sprite spriteToConvert) {
		return spriteToConvert.convert();
	}

	public Sprite copy(Sprite spriteToCopy, Project destinationProject, Scene destinationScene) throws IOException {
		String name = uniqueNameProvider.getUniqueNameInNameables(spriteToCopy.getName(), destinationScene.getSpriteList());
		Sprite sprite = new SpriteFactory().newInstance(spriteToCopy.getClass().getSimpleName(), name);

		for (LookData look : spriteToCopy.getLookList()) {
			sprite.getLookList().add(lookController.copy(look, destinationScene, sprite));
		}

		for (SoundInfo sound : spriteToCopy.getSoundList()) {
			sprite.getSoundList().add(soundController.copy(sound, destinationScene, sprite));
		}

		for (NfcTagData nfcTag : spriteToCopy.getNfcTagList()) {
			sprite.getNfcTagList().add(nfcTag.clone());
		}

		for (UserVariable userVariable : spriteToCopy.getUserVariables()) {
			sprite.getUserVariables().add(new UserVariable(userVariable));
		}
		for (UserList userList : spriteToCopy.getUserLists()) {
			sprite.getUserLists().add(new UserList(userList));
		}

		for (Brick userDefinedBrick : spriteToCopy.getUserDefinedBrickList()) {
			sprite.getUserDefinedBrickList().add(new UserDefinedBrick((UserDefinedBrick) userDefinedBrick));
		}

		for (Script script : spriteToCopy.getScriptList()) {
			try {
				sprite.addScript(scriptController.copy(script, destinationProject, destinationScene, sprite));
			} catch (CloneNotSupportedException exception) {
				Log.e(TAG, Log.getStackTraceString(exception));
			}
		}

		if (spriteToCopy instanceof GroupItemSprite) {
			((GroupItemSprite) sprite).setCollapsed(((GroupItemSprite) spriteToCopy).isCollapsed());
		}

		return sprite;
	}

	public Sprite copyForCloneBrick(Sprite spriteToCopy) {
		Sprite sprite = new Sprite(spriteToCopy.getName() + "-c" + StageActivity
				.getAndIncrementNumberOfClonedSprites());
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();

		ScriptController scriptController = new ScriptController();

		sprite.isClone = true;
		sprite.setActionFactory(spriteToCopy.getActionFactory());

		for (LookData look : spriteToCopy.getLookList()) {
			sprite.getLookList().add(new LookData(look.getName(), look.getFile()));
		}

		sprite.getSoundList().addAll(spriteToCopy.getSoundList());
		sprite.getNfcTagList().addAll(spriteToCopy.getNfcTagList());

		for (UserVariable originalVariable : spriteToCopy.getUserVariables()) {
			UserVariable copyVariable = new UserVariable(originalVariable);
			copyVariable.setDeviceValueKey(originalVariable.getDeviceKey());
			sprite.getUserVariables().add(copyVariable);
		}

		for (UserList originalList : spriteToCopy.getUserLists()) {
			UserList copyList = new UserList(originalList);
			copyList.setDeviceListKey(originalList.getDeviceKey());
			sprite.getUserLists().add(new UserList(originalList));
		}

		for (Brick userDefinedBrick : spriteToCopy.getUserDefinedBrickList()) {
			sprite.getUserDefinedBrickList().add(new UserDefinedBrick((UserDefinedBrick) userDefinedBrick));
		}

		for (Script script : spriteToCopy.getScriptList()) {
			try {
				sprite.addScript(scriptController.copy(script, currentProject, currentScene, sprite));
			} catch (CloneNotSupportedException | IOException exception) {
				Log.e(TAG, Log.getStackTraceString(exception));
			}
		}

		sprite.resetSprite();
		int currentLookDataIndex = spriteToCopy.getLookList().indexOf(spriteToCopy.look.getLookData());
		if (currentLookDataIndex != -1) {
			sprite.look.setLookData(sprite.getLookList().get(currentLookDataIndex));
		}
		spriteToCopy.look.copyTo(sprite.look);

		if (spriteToCopy.penConfiguration.isPenDown()) {
			sprite.penConfiguration.setPenDown(true);
			sprite.penConfiguration.addQueue();
		}
		if(spriteToCopy.plot.isPlotting()){
			sprite.plot.resume();
			sprite.plot.startNewPlotLine();
		}
		return sprite;
	}

	public void delete(Sprite spriteToDelete) {
		if (spriteToDelete.isClone) {
			throw new IllegalStateException("You are deleting a clone: this means you also delete the files that are "
					+ "referenced by the original sprite because clones are shallow copies regarding files.");
		}

		for (LookData look : spriteToDelete.getLookList()) {
			try {
				lookController.delete(look);
			} catch (IOException exception) {
				Log.e(TAG, Log.getStackTraceString(exception));
			}
		}

		for (SoundInfo sound : spriteToDelete.getSoundList()) {
			try {
				soundController.delete(sound);
			} catch (IOException exception) {
				Log.e(TAG, Log.getStackTraceString(exception));
			}
		}

		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		if (currentScene != null) {
			currentScene.removeSpriteFromCloneBricks(spriteToDelete);
		}
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

		for (Brick brick: spriteToPack.getUserDefinedBrickList()) {
			try {
				sprite.addUserDefinedBrick((UserDefinedBrick) brick.clone());
			} catch (CloneNotSupportedException exception) {
				Log.e(TAG, Log.getStackTraceString(exception));
			}
		}

		for (Script script : spriteToPack.getScriptList()) {
			try {
				scriptController.packForSprite(spriteToPack, script, sprite);
			} catch (CloneNotSupportedException exception) {
				Log.e(TAG, Log.getStackTraceString(exception));
			}
		}

		return sprite;
	}

	public Sprite unpack(Sprite spriteToUnpack, Project destinationProject, Scene destinationScene) throws IOException {
		String name = uniqueNameProvider.getUniqueNameInNameables(spriteToUnpack.getName(), destinationScene.getSpriteList());
		Sprite sprite = new Sprite(name);

		for (LookData look : spriteToUnpack.getLookList()) {
			lookController.unpackForSprite(look, destinationScene, sprite);
		}

		for (SoundInfo sound : spriteToUnpack.getSoundList()) {
			soundController.unpackForSprite(sound, destinationScene, sprite);
		}

		for (NfcTagData nfcTag : spriteToUnpack.getNfcTagList()) {
			sprite.getNfcTagList().add(nfcTag.clone());
		}

		for (Brick brick: spriteToUnpack.getUserDefinedBrickList()) {
			try {
				sprite.addUserDefinedBrick((UserDefinedBrick) brick.clone());
			} catch (CloneNotSupportedException exception) {
				Log.e(TAG, Log.getStackTraceString(exception));
			}
		}

		for (Script script : spriteToUnpack.getScriptList()) {
			try {
				scriptController.unpackForSprite(spriteToUnpack.getName(), script, destinationProject, destinationScene, sprite);
			} catch (CloneNotSupportedException exception) {
				Log.e(TAG, Log.getStackTraceString(exception));
			}
		}

		return sprite;
	}
}
