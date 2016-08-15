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
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserBrickParameter;
import org.catrobat.catroid.content.bricks.UserListBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrickElement;
import org.catrobat.catroid.content.bricks.UserVariableBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserVariable;
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
			spriteToBackpack) {
		Iterator<Brick> iterator = checkedBricks.iterator();
		List<Script> scriptsToAdd = new ArrayList<>();
		while (iterator.hasNext()) {
			Brick currentBrick = iterator.next();
			if (currentBrick instanceof ScriptBrick) {
				Script scriptToAdd = ((ScriptBrick) currentBrick).getScriptSafe().copyScriptForSprite(
						ProjectManager.getInstance().getCurrentSprite());
				ProjectManager.getInstance().checkCurrentScript(scriptToAdd, false);
				for (Brick brickOfScript : scriptToAdd.getBrickList()) {
					brickOfScript.storeDataForBackPack(spriteToBackpack);
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
			Script newScript = backPackedScript.copyScriptForSprite(ProjectManager.getInstance().getCurrentSprite());
			for (Brick brickOfScript : newScript.getBrickList()) {
				handleBackPackedBricksWithAdditionalData(brickOfScript, deleteUnpackedItems);
			}

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

	public void handleBackPackedBricksWithAdditionalData(Brick brickOfScript, boolean deleteUnpackedItems) {
		if (brickOfScript instanceof SetLookBrick) {
			handleLookBrickUnpacking(brickOfScript, deleteUnpackedItems);
		} else if (brickOfScript instanceof PlaySoundBrick) {
			handleSoundBrickUnpacking(brickOfScript, deleteUnpackedItems);
		} else if (brickOfScript instanceof UserVariableBrick) {
			handleVariableBrickUnpacking(brickOfScript);
		} else if (brickOfScript instanceof UserListBrick) {
			handleVariableListUnpacking(brickOfScript);
		} else if (brickOfScript instanceof PointToBrick) {
			handlePointToBrickUnpacking(brickOfScript, deleteUnpackedItems);
		} else if (brickOfScript instanceof UserBrick) {
			handleUserBrickUnpacking(brickOfScript, deleteUnpackedItems);
		}
	}

	private void handleLookBrickUnpacking(Brick brickOfScript, boolean deleteUnpackedItems) {
		SetLookBrick brick = (SetLookBrick) brickOfScript;
		LookData newLookData = LookController.getInstance().unpack(brick.getLook(), deleteUnpackedItems, true);
		if (newLookData != null) {
			brick.setLook(newLookData);
		}
	}

	private void handleSoundBrickUnpacking(Brick brickOfScript, boolean deleteUnpackedItems) {
		PlaySoundBrick brick = (PlaySoundBrick) brickOfScript;
		SoundInfo newSoundInfo = SoundController.getInstance().unpack(brick.getSound(), deleteUnpackedItems, true);
		if (newSoundInfo != null) {
			brick.setSoundInfo(newSoundInfo);
		}
	}

	private void handleVariableBrickUnpacking(Brick brickOfScript) {
		UserVariableBrick brick = (UserVariableBrick) brickOfScript;
		ProjectManager projectManager = ProjectManager.getInstance();
		UserVariableBrick.BackPackedData backPackedData = brick.getBackPackedData();
		if (brick.getUserVariable() == null) {
			brick.setUserVariable(backPackedData.userVariable);
		}

		DataContainer dataContainer = projectManager.getCurrentScene().getDataContainer();
		UserVariable variable = null;
		switch (backPackedData.userVariableType) {
			case DataContainer.USER_VARIABLE_SPRITE:
				Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
				variable = dataContainer.addSpriteVariableIfDoesNotExist(brick.getUserVariable().getName(), currentSprite);
				break;
			case DataContainer.USER_VARIABLE_PROJECT:
				if (dataContainer.findUserVariable(brick.getUserVariable().getName(),
						dataContainer.getProjectVariables()) == null) {
					variable = dataContainer.addProjectUserVariable(brick.getUserVariable().getName());
				}
				break;
			case DataContainer.USER_VARIABLE_USERBRICK:
				UserBrick userBrick = ProjectManager.getInstance().getCurrentUserBrick();
				UserVariable userVariable = brick.getUserVariable();
				if (userVariable != null) {
					variable = dataContainer.addUserBrickVariableToUserBrickIfNotExists(userBrick, userVariable.getName(),
							userVariable.getValue());
				}
				break;
		}
		brick.setUserVariable(variable);
	}

	private void handleVariableListUnpacking(Brick brickOfScript) {
		UserListBrick brick = (UserListBrick) brickOfScript;
		ProjectManager projectManager = ProjectManager.getInstance();
		UserListBrick.BackPackedData backPackedData = brick.getBackPackedData();
		if (brick.getUserList() == null) {
			brick.setUserList(backPackedData.userList);
		}

		DataContainer dataContainer = projectManager.getCurrentScene().getDataContainer();
		switch (backPackedData.userListType) {
			case DataContainer.USER_LIST_SPRITE:
				Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
				dataContainer.addSpriteListIfDoesNotExist(brick.getUserList().getName(), currentSprite);
				break;
			case DataContainer.USER_LIST_PROJECT:
				if (dataContainer.findUserList(brick.getUserList().getName(),
						dataContainer.getProjectLists()) == null) {
					dataContainer.addProjectUserList(brick.getUserList().getName());
				}
				break;
		}
	}

	private void handlePointToBrickUnpacking(Brick brickOfScript, boolean deleteUnpackedItems) {
		PointToBrick brick = (PointToBrick) brickOfScript;
		Sprite unpackedPointToSprite = BackPackSpriteController.getInstance().unpack(brick.getPointedObject(),
				deleteUnpackedItems, true, true, false);
		brick.setPointedObject(unpackedPointToSprite);
	}

	public void handleUserBrickUnpacking(Brick brickOfScript, boolean deleteUnpackedItems) {
		UserBrick brick = (UserBrick) brickOfScript;
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		UserBrick clonedPrototypeUserBrick = (UserBrick) brick.clone();
		clonedPrototypeUserBrick.updateUserBrickParametersAndVariables();
		currentSprite.addUserBrick(clonedPrototypeUserBrick);

		for (Brick brickOfUserScript : brick.getDefinitionBrick().getUserScript().getBrickList()) {
			handleBackPackedBricksWithAdditionalData(brickOfUserScript, deleteUnpackedItems);
		}

		setUserBrickElementReferences(brick);
		brick.updateUserBrickParametersAndVariables();
	}

	private void setUserBrickElementReferences(UserBrick brick) {
		UserScriptDefinitionBrick definitionBrick = brick.getDefinitionBrick();
		List<UserScriptDefinitionBrickElement> userScriptDefinitionBrickElementList = definitionBrick.getUserScriptDefinitionBrickElements();
		Iterator<UserBrickParameter> userBrickParameterIterator = brick.getUserBrickParameters().iterator();

		for (UserScriptDefinitionBrickElement element : userScriptDefinitionBrickElementList) {
			if (element.isVariable() && userBrickParameterIterator.hasNext()) {
				UserBrickParameter parameter = userBrickParameterIterator.next();
				parameter.setElement(element);
			}
		}
	}
}
