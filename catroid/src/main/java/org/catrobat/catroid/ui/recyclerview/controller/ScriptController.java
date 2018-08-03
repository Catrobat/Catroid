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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.UserListBrick;
import org.catrobat.catroid.content.bricks.UserVariableBrick;
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.controller.BackpackListManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScriptController {

	public static final String TAG = ScriptController.class.getSimpleName();

	private LookController lookController = new LookController();
	private SoundController soundController = new SoundController();

	public Script copy(Script scriptToCopy, Scene dstScene, Sprite dstSprite) throws IOException,
			CloneNotSupportedException {

		Script script = scriptToCopy.clone();

		for (Brick brick : script.getBrickList()) {
			if (brick instanceof SetLookBrick) {
				((SetLookBrick) brick).setLook(lookController
						.findOrCopy(((SetLookBrick) brick).getLook(), dstScene, dstSprite));
			}

			if (brick instanceof WhenBackgroundChangesBrick) {
				((WhenBackgroundChangesBrick) brick).setLook(lookController
						.findOrCopy(((WhenBackgroundChangesBrick) brick).getLook(), dstScene, dstSprite));
			}

			if (brick instanceof PlaySoundBrick) {
				((PlaySoundBrick) brick).setSound(soundController
						.findOrCopy(((PlaySoundBrick) brick).getSound(), dstScene, dstSprite));
			}

			if (brick instanceof PlaySoundAndWaitBrick) {
				((PlaySoundAndWaitBrick) brick).setSound(soundController
						.findOrCopy(((PlaySoundAndWaitBrick) brick).getSound(), dstScene, dstSprite));
			}

			if (brick instanceof UserVariableBrick) {
				UserVariable previousUserVar = ((UserVariableBrick) brick).getUserVariable();
				((UserVariableBrick) brick).setUserVariable(dstScene.getDataContainer()
						.getUserVariable(dstSprite, previousUserVar.getName()));
			}

			if (brick instanceof UserListBrick) {
				UserList previousUserList = ((UserListBrick) brick).getUserList();
				((UserListBrick) brick).setUserList(dstScene.getDataContainer()
						.getUserList(dstSprite, previousUserList.getName()));
			}
		}

		return script;
	}

	public void pack(String groupName, List<Brick> bricksToPack) throws CloneNotSupportedException {
		List<Script> scriptsToPack = new ArrayList<>();

		for (Brick brick : bricksToPack) {
			if (brick instanceof ScriptBrick) {
				Script scriptToPack = ((ScriptBrick) brick).getScript();
				scriptsToPack.add(scriptToPack.clone());
			}
		}

		BackpackListManager.getInstance().addScriptToBackPack(groupName, scriptsToPack);
		BackpackListManager.getInstance().saveBackpack();
	}

	void packForSprite(Script scriptToPack, Sprite dstSprite) throws IOException, CloneNotSupportedException {
		Script script = scriptToPack.clone();

		for (Brick brick : script.getBrickList()) {
			if (brick instanceof SetLookBrick) {
				((SetLookBrick) brick).setLook(lookController
						.packForSprite(((SetLookBrick) brick).getLook(), dstSprite));
			}

			if (brick instanceof WhenBackgroundChangesBrick) {
				((WhenBackgroundChangesBrick) brick).setLook(lookController
						.packForSprite(((WhenBackgroundChangesBrick) brick).getLook(), dstSprite));
			}

			if (brick instanceof PlaySoundBrick) {
				((PlaySoundBrick) brick).setSound(soundController
						.packForSprite(((PlaySoundBrick) brick).getSound(), dstSprite));
			}

			if (brick instanceof PlaySoundAndWaitBrick) {
				((PlaySoundAndWaitBrick) brick).setSound(soundController
						.packForSprite(((PlaySoundAndWaitBrick) brick).getSound(), dstSprite));
			}
		}

		dstSprite.getScriptList().add(script);
	}

	public void unpack(Script scriptToUnpack, Sprite dstSprite) throws CloneNotSupportedException {
		Script script = scriptToUnpack.clone();

		for (Brick brick : script.getBrickList()) {
			if (ProjectManager.getInstance().getCurrentProject().isCastProject()
					&& CastManager.unsupportedBricks.contains(brick.getClass())) {
				Log.e(TAG, "CANNOT insert bricks into ChromeCast project");
				return;
			}
		}

		dstSprite.getScriptList().add(script);
	}

	void unpackForSprite(Script scriptToUnpack, Scene dstScene, Sprite dstSprite) throws IOException,
			CloneNotSupportedException {
		Script script = scriptToUnpack.clone();

		for (Brick brick : script.getBrickList()) {
			if (ProjectManager.getInstance().getCurrentProject().isCastProject()
					&& CastManager.unsupportedBricks.contains(brick.getClass())) {
				Log.e(TAG, "CANNOT insert bricks into ChromeCast project");
				return;
			}

			if (brick instanceof SetLookBrick) {
				((SetLookBrick) brick)
						.setLook(lookController
								.unpackForSprite(((SetLookBrick) brick).getLook(),
										dstScene,
										dstSprite));
			}

			if (brick instanceof WhenBackgroundChangesBrick) {
				((WhenBackgroundChangesBrick) brick)
						.setLook(lookController
								.unpackForSprite(((WhenBackgroundChangesBrick) brick).getLook(),
										dstScene,
										dstSprite));
			}

			if (brick instanceof PlaySoundBrick) {
				((PlaySoundBrick) brick)
						.setSound(soundController
								.unpackForSprite(((PlaySoundBrick) brick).getSound(),
										dstScene,
										dstSprite));
			}

			if (brick instanceof PlaySoundAndWaitBrick) {
				((PlaySoundAndWaitBrick) brick)
						.setSound(soundController
								.unpackForSprite(((PlaySoundAndWaitBrick) brick).getSound(),
										dstScene,
										dstSprite));
			}
		}

		dstSprite.getScriptList().add(script);
	}
}
