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

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.UserListBrick;
import org.catrobat.catroid.content.bricks.UserVariableBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class BackPackScriptController {

	public static final String TAG = BackPackScriptController.class.getSimpleName();

	private static final BackPackScriptController INSTANCE = new BackPackScriptController();

	private BackPackScriptController() {
	}

	public static BackPackScriptController getInstance() {
		return INSTANCE;
	}

	public List<Script> backpack(String groupName, List<Brick> checkedBricks, boolean addToHiddenBackpack, Sprite
			backpackedSprite) {
		Iterator<Brick> iterator = checkedBricks.iterator();
		List<Script> scriptsToAdd = new ArrayList<>();
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		while (iterator.hasNext()) {
			Brick currentBrick = iterator.next();
			if (currentBrick instanceof ScriptBrick) {
				//TODO: Userbricks in backpack currently not supported
				Script scriptToAdd = ((ScriptBrick) currentBrick).getScriptSafe().copyScriptForSprite(
						ProjectManager.getInstance().getCurrentSprite(), null);
				for (Brick brickOfScript : scriptToAdd.getBrickList()) {
					if (brickOfScript instanceof SetLookBrick) {
						SetLookBrick brick = (SetLookBrick) brickOfScript;
						LookData backPackedLookData = LookController.getInstance().backPackLook(brick.getLook(), true);
						brick.setLook(backPackedLookData);
						if (backpackedSprite != null && !backpackedSprite.getLookDataList().contains(backPackedLookData)) {
							backpackedSprite.getLookDataList().add(backPackedLookData);
						}
					} else if (brickOfScript instanceof PlaySoundBrick) {
						PlaySoundBrick brick = (PlaySoundBrick) brickOfScript;
						SoundInfo backPackedSoundInfo = SoundController.getInstance().backPackSound(brick.getSound(), true);
						brick.setSoundInfo(backPackedSoundInfo);
						if (backpackedSprite != null && !backpackedSprite.getSoundList().contains(backPackedSoundInfo)) {
							backpackedSprite.getSoundList().add(backPackedSoundInfo);
						}
					} else if (brickOfScript instanceof UserVariableBrick) {
						UserVariableBrick brick = (UserVariableBrick) brickOfScript;
						Integer type = currentProject.getDataContainer()
								.getTypeOfUserVariable(brick.getUserVariable().getName(), ProjectManager
										.getInstance().getCurrentSprite());
						brick.setBackPackedData(currentProject, brick.getUserVariable(), type);
					} else if (brickOfScript instanceof UserListBrick) {
						UserListBrick brick = (UserListBrick) brickOfScript;
						Integer type = currentProject.getDataContainer()
								.getTypeOfUserList(brick.getUserList().getName(), ProjectManager
										.getInstance().getCurrentSprite());
						brick.setBackPackedData(currentProject, brick.getUserList(), type);
					} else if (brickOfScript instanceof PointToBrick) {
						Sprite spriteToRestore = ProjectManager.getInstance().getCurrentSprite();
						PointToBrick brick = (PointToBrick) brickOfScript;
						Sprite backPackedSprite = BackPackSpriteController.getInstance().backpack(brick.getPointedObject(), true);
						brick.setPointedObject(backPackedSprite);
						ProjectManager.getInstance().setCurrentSprite(spriteToRestore);
					}
				}
				scriptsToAdd.add(scriptToAdd);
			}
		}
		if (!scriptsToAdd.isEmpty()) {
			if (addToHiddenBackpack) {
				BackPackListManager.getInstance().addScriptToHiddenBackpack(groupName, scriptsToAdd);
			} else {
				BackPackListManager.getInstance().addScriptToBackPack(groupName, scriptsToAdd);
			}
		}
		return scriptsToAdd;
	}

	public void unpack(String selectedScriptGroupBackPack, boolean deleteUnpackedItems, boolean
			handleInsertFromScriptBackPack, Activity activity, boolean fromHiddenBackPack) {
		List<Script> scriptsInGroup;
		if (fromHiddenBackPack) {
			scriptsInGroup = BackPackListManager.getInstance().getHiddenBackpackedScripts().get(selectedScriptGroupBackPack);
		} else {
			scriptsInGroup = BackPackListManager.getInstance().getBackPackedScripts().get(selectedScriptGroupBackPack);
		}

		if (scriptsInGroup == null) {
			Log.d(TAG, "Attempted to unpack a not existing (maybe previously deleted) script group");
			return;
		}
		Integer numberOfBricks = 0;

		for (Script backPackedScript : scriptsInGroup) {
			//TODO: userbricks currently not supported in backpack
			Script newScript = backPackedScript.copyScriptForSprite(ProjectManager.getInstance().getCurrentSprite(), null);
			handleBackPackedBricksWithAdditionalData(newScript, deleteUnpackedItems);

			ProjectManager.getInstance().getCurrentSprite().addScript(newScript);
			numberOfBricks += backPackedScript.getBrickList().size();

			if (deleteUnpackedItems) {
				if (fromHiddenBackPack) {
					BackPackListManager.getInstance().removeItemFromScriptHiddenBackpack(selectedScriptGroupBackPack);
				} else {
					BackPackListManager.getInstance().removeItemFromScriptBackPack(selectedScriptGroupBackPack);
				}
			}
			if (handleInsertFromScriptBackPack) {
				String textForUnPacking = activity.getResources().getQuantityString(R.plurals
						.unpacking_items_plural, 1);
				ToastUtil.showSuccess(activity, selectedScriptGroupBackPack + " " + textForUnPacking);

				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(activity);
				sharedPreferences.edit().putInt(Constants.NUMBER_OF_BRICKS_INSERTED_FROM_BACKPACK, numberOfBricks).commit();
				((BackPackActivity) activity)
						.returnToScriptActivity(ScriptActivity.FRAGMENT_SCRIPTS);
			}
		}
	}

	private void handleBackPackedBricksWithAdditionalData(Script newScript, boolean deleteUnpackedItems) {
		for (Brick brickOfScript : newScript.getBrickList()) {
			if (brickOfScript instanceof SetLookBrick) {
				SetLookBrick brick = (SetLookBrick) brickOfScript;
				LookData newLookData = LookController.getInstance().unpack(brick.getLook(), deleteUnpackedItems, true);
				if (newLookData != null) {
					brick.setLook(newLookData);
				}
			} else if (brickOfScript instanceof PlaySoundBrick) {
				PlaySoundBrick brick = (PlaySoundBrick) brickOfScript;
				SoundInfo newSoundInfo = SoundController.getInstance().unpack(brick.getSound(), deleteUnpackedItems, true);
				if (newSoundInfo != null) {
					brick.setSoundInfo(newSoundInfo);
				}
			} else if (brickOfScript instanceof UserVariableBrick) {
				UserVariableBrick brick = (UserVariableBrick) brickOfScript;
				ProjectManager projectManager = ProjectManager.getInstance();
				UserVariableBrick.BackPackedData backPackedData = brick.getBackPackedData();
				if (brick.getUserVariable() == null) {
					brick.setUserVariable(backPackedData.userVariable);
				}

				DataContainer dataContainer = projectManager.getCurrentProject().getDataContainer();
				switch (backPackedData.userVariableType) {
					case DataContainer.USER_VARIABLE_SPRITE:
						dataContainer.addSpriteUserVariable(brick.getUserVariable().getName());
						break;
					case DataContainer.USER_VARIABLE_PROJECT:
						if (dataContainer.findUserVariable(brick.getUserVariable().getName(),
								dataContainer.getProjectVariables()) == null) {
							dataContainer.addProjectUserVariable(brick.getUserVariable().getName());
						}
						break;
					case DataContainer.USER_VARIABLE_USERBRICK:
						//TODO: Userbricks currently not supported in backpack
						break;
				}
			} else if (brickOfScript instanceof UserListBrick) {
				UserListBrick brick = (UserListBrick) brickOfScript;
				ProjectManager projectManager = ProjectManager.getInstance();
				UserListBrick.BackPackedData backPackedData = brick.getBackPackedData();
				if (brick.getUserList() == null) {
					brick.setUserList(backPackedData.userList);
				}

				DataContainer dataContainer = projectManager.getCurrentProject().getDataContainer();
				switch (backPackedData.userListType) {
					case DataContainer.USER_LIST_SPRITE:
						dataContainer.addSpriteUserList(brick.getUserList().getName());
						break;
					case DataContainer.USER_LIST_PROJECT:
						if (dataContainer.findUserList(brick.getUserList().getName(),
								dataContainer.getProjectLists()) == null) {
							dataContainer.addProjectUserList(brick.getUserList().getName());
						}
						break;
				}
			} else if (brickOfScript instanceof PointToBrick) {
				PointToBrick brick = (PointToBrick) brickOfScript;
				Sprite unpackedPointToSprite = BackPackSpriteController.getInstance().unpack(brick.getPointedObject(),
						deleteUnpackedItems, true, true);
				brick.setPointedObject(unpackedPointToSprite);
			}
		}
	}
}
