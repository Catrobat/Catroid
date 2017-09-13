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
package org.catrobat.catroid.formulaeditor.datacontainer;

import android.content.Context;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.UserBrickScriptActivity;
import org.catrobat.catroid.ui.adapter.DataAdapter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DataContainer extends BaseDataContainer {

	public enum DataType implements Serializable {
		USER_VARIABLE_SPRITE,
		USER_VARIABLE_PROJECT,
		USER_VARIABLE_USERBRICK,
		USER_LIST_SPRITE,
		USER_LIST_PROJECT,
		USER_DATA_EMPTY
	}

	private transient Project project;

	private transient SpriteVariableBehaviour spriteVariableBehaviour = new SpriteVariableBehaviour(this);
	private transient SpriteListBehaviour spriteListBehaviour = new SpriteListBehaviour(this);
	private transient UserBrickVariableBehaviour userBrickVariableBehaviour = new UserBrickVariableBehaviour(this);
	private transient ProjectVariableBehaviour projectVariableBehaviour = new ProjectVariableBehaviour(this);
	private transient ProjectListBehaviour projectListBehaviour = new ProjectListBehaviour(this);

	private DataContainer() {
	}

	public DataContainer(Project project) {
		spriteVariables = new HashMap<>();
		spriteListOfLists = new HashMap<>();

		this.project = project;
	}

	//general methods
	public DataAdapter createDataAdapter(Context context, Sprite sprite) {
		List<UserVariable> userBrickVariables = new LinkedList<>();
		List<UserVariable> spriteVariables = getOrCreateVariableListForSprite(sprite);
		List<UserList> spriteUserList = getOrCreateUserListForSprite(sprite);
		return new DataAdapter(context, spriteUserList, getProjectLists(), spriteVariables, getProjectVariables(),
				userBrickVariables);
	}

	public DataAdapter createDataAdapter(Context context, UserBrick userBrick, Sprite sprite) {
		List<UserVariable> userBrickVariables;
		if (userBrick == null || !(context instanceof UserBrickScriptActivity)) {
			userBrickVariables = new LinkedList<>();
		} else {
			userBrickVariables = getOrCreateVariableListForUserBrick(userBrick);
		}
		List<UserVariable> spriteVariables = getOrCreateVariableListForSprite(sprite);
		List<UserList> spriteUserList = getOrCreateUserListForSprite(sprite);
		return new DataAdapter(context, spriteUserList, getProjectLists(), spriteVariables, getProjectVariables(), userBrickVariables);
	}

	public String getUniqueVariableName(Context context) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		String name = context.getResources().getString(R.string.new_user_brick_variable).trim();

		int variableCounter = 0;
		String originalName = name;
		name += " " + variableCounter;

		while (getUserVariable(currentSprite, name) != null) {
			variableCounter++;
			name = originalName + " " + variableCounter;
		}
		name = originalName + " " + variableCounter;

		return name;
	}

	public UserVariable getUserVariable(Sprite currentSprite, String name, UserBrick userBrick) {
		UserVariable variableToReturn = spriteVariableBehaviour.get(currentSprite, name);
		if (variableToReturn == null && userBrick != null) {
			variableToReturn = userBrickVariableBehaviour.get(userBrick, name);
		}
		if (variableToReturn == null) {
			variableToReturn = findProjectVariable(name);
		}
		return variableToReturn;
	}

	public boolean deleteUserVariableByName(String userVariableName) {
		UserVariable variableToDelete = getUserVariable(getCurrentSprite(), userVariableName, getCurrentUserBrick());
		return spriteVariableBehaviour.delete(getCurrentSprite(), userVariableName)
				|| userBrickVariableBehaviour.delete(getCurrentUserBrick(), userVariableName)
				|| getProjectVariables().remove(variableToDelete); //TODO
	}

	public boolean deleteUserListByName(String userListName) {
		UserList listToDelete = getUserList(getCurrentSprite(), userListName);
		return spriteListBehaviour.delete(getCurrentSprite(), userListName)
				|| getProjectLists().remove(listToDelete); //TODO
	}

	public void removeVariablesOfClones() {
		spriteVariableBehaviour.removeCloneData();
		spriteListBehaviour.removeCloneData();
	}

	public void removeVariableListForSprite(Sprite sprite) {
		spriteVariables.remove(sprite);
		spriteListOfLists.remove(sprite);

		for (UserBrick userBrick : sprite.getUserBrickList()) {
			userBrickVariables.remove(userBrick);
		}
	}

	public void resetAllDataObjects() {
		resetAllUserLists();
		resetAllUserVariables();
	}

	private void resetAllUserVariables() {
		resetUserVariables(getProjectVariables());

		for (Sprite currentSprite : spriteVariables.keySet()) {
			resetUserVariables(spriteVariables.get(currentSprite));
		}
	}

	//general UserVariable methods
	public UserVariable getUserVariable(Sprite sprite, String userVariableName) {
		return getUserVariable(sprite, userVariableName, getCurrentUserBrick());
	}

	public DataType getTypeOfUserVariable(String userVariableName, Sprite sprite) {
		UserVariable userVariable = findSpriteUserVariable(sprite, userVariableName);
		if (userVariable != null) {
			return DataType.USER_VARIABLE_SPRITE;
		}
		userVariable = findProjectVariable(userVariableName);
		if (userVariable != null) {
			return DataType.USER_VARIABLE_PROJECT;
		}
		UserBrick userBrick = getCurrentUserBrick();
		if (userBrick != null) {
			userVariable = findUserBrickVariable(userBrick, userVariableName);
			if (userVariable != null) {
				return DataType.USER_VARIABLE_USERBRICK;
			}
		}
		return DataType.USER_VARIABLE_PROJECT;
	}

	public void setSpriteVariablesForSupportContainer(SupportDataContainer container) {
		if (container.spriteVariables != null) {
			spriteVariables = container.spriteVariables;
		}
		if (container.userBrickVariables != null) {
			userBrickVariables = container.userBrickVariables;
		}
		if (container.spriteListOfLists != null) {
			spriteListOfLists = container.spriteListOfLists;
		}
	}

	//general UserList methods
	public UserList getUserList(Sprite currentSprite, String name) {
		UserList userList = spriteListBehaviour.get(currentSprite, name);
		if (userList == null) {
			userList = findProjectList(name);
		}
		return userList;
	}

	public UserList getUserList() {
		if (getProjectLists().size() > 0) {
			return getProjectLists().get(0);
		}

		for (Sprite currentSprite : spriteListOfLists.keySet()) {
			if (spriteListOfLists.get(currentSprite).size() > 0) {
				return spriteListOfLists.get(currentSprite).get(0);
			}
		}
		return null;
	}

	public DataType getTypeOfUserList(String userListName, Sprite sprite) {
		UserList userList = findSpriteUserList(sprite, userListName);
		if (userList != null) {
			return DataType.USER_LIST_SPRITE;
		}
		userList = findProjectList(userListName);
		if (userList != null) {
			return DataType.USER_LIST_PROJECT;
		}
		return DataType.USER_DATA_EMPTY;
	}

	private void resetAllUserLists() {
		resetUserLists(getProjectLists());

		for (Sprite currentSprite : spriteListOfLists.keySet()) {
			resetUserLists(spriteListOfLists.get(currentSprite));
		}
	}

	//sprite UserVariable methods
	public UserVariable addSpriteUserVariable(String userVariableName) {
		return spriteVariableBehaviour.add(getCurrentSprite(), userVariableName);
	}

	public UserVariable addSpriteUserVariableToSprite(Sprite sprite, String userVariableName) {
		return spriteVariableBehaviour.add(sprite, userVariableName);
	}

	public UserVariable addSpriteVariableIfDoesNotExist(Sprite sprite, String userVariableName) {
		return spriteVariableBehaviour.addIfNotExists(sprite, userVariableName);
	}

	public List<UserVariable> getOrCreateVariableListForSprite(Sprite sprite) {
		return spriteVariableBehaviour.getOrCreate(sprite);
	}

	public UserVariable findSpriteUserVariable(Sprite sprite, String name) {
		return spriteVariableBehaviour.find(sprite, name);
	}

	public boolean spriteVariableExists(Sprite sprite, UserVariable userVariable) {
		return spriteVariableBehaviour.exists(sprite, userVariable);
	}

	public boolean spriteVariableExistsByName(Sprite sprite, String variableName) {
		return spriteVariableBehaviour.exists(sprite, variableName);
	}

	public boolean variableExistsInAnySprite(List<Sprite> sprites, String variableName) {
		return spriteVariableBehaviour.existsAny(sprites, variableName);
	}

	public void cloneSpriteVariablesForScene(Scene scene, DataContainer original) {
		spriteVariables = spriteVariableBehaviour.cloneForScene(scene, original.getSpriteVariableMap());
	}

	public UserVariable renameSpriteUserVariable(String oldName, String newName) {
		return spriteVariableBehaviour.rename(getCurrentSprite(), oldName, newName);
	}

	public void cleanVariableListForSprite(Sprite sprite) {
		spriteVariableBehaviour.clean(sprite);
	}

	private void resetUserVariables(List<UserVariable> userVariableList) {
		spriteVariableBehaviour.reset(userVariableList);
	}

	//UserList methods
	public UserList addSpriteUserList(String userListName) {
		return spriteListBehaviour.add(getCurrentSprite(), userListName);
	}

	public UserList addSpriteUserListToSprite(Sprite sprite, String userListName) {
		return spriteListBehaviour.add(sprite, userListName);
	}

	public UserList addSpriteListIfDoesNotExist(Sprite sprite, String userListName) {
		return spriteListBehaviour.addIfNotExists(sprite, userListName);
	}

	public List<UserList> getOrCreateUserListForSprite(Sprite sprite) {
		return spriteListBehaviour.getOrCreate(sprite);
	}

	public UserList findSpriteUserList(Sprite sprite, String name) {
		return spriteListBehaviour.find(sprite, name);
	}

	public boolean existSpriteListByName(Sprite sprite, String listName) {
		return spriteListBehaviour.exists(sprite, listName);
	}

	public boolean existSpriteList(Sprite sprite, UserList list) {
		return spriteListBehaviour.exists(sprite, list);
	}

	public boolean existListInAnySprite(List<Sprite> sprites, String listName) {
		return spriteListBehaviour.existsAny(sprites, listName);
	}

	public void cloneSpriteListsForScene(Scene scene, DataContainer original) {
		spriteListOfLists = spriteListBehaviour.cloneForScene(scene, original.getSpriteListMap());
	}

	public UserList renameSpriteUserList(String oldName, String newName) {
		return spriteListBehaviour.rename(getCurrentSprite(), oldName, newName);
	}

	public void cleanUserListForSprite(Sprite sprite) {
		spriteListBehaviour.clean(sprite);
	}

	private void resetUserLists(List<UserList> userList) {
		spriteListBehaviour.reset(userList);
	}

	//methods for userbrick variables
	public UserVariable addUserBrickVariableToUserBrick(UserBrick userBrick, String userVariableName, Object value) {
		return userBrickVariableBehaviour.add(userBrick, userVariableName, value);
	}

	public UserVariable addUserBrickVariableToUserBrickIfNotExists(UserBrick userBrick, String userVariableName, Object userVariableValue) {
		return userBrickVariableBehaviour.addIfNotExists(userBrick, userVariableName, userVariableValue);
	}

	public List<UserVariable> getOrCreateVariableListForUserBrick(UserBrick userBrick) {
		return userBrickVariableBehaviour.getOrCreate(userBrick);
	}

	public boolean existUserVariableWithName(String name) {
		for (UserBrick userBrick : userBrickVariables.keySet()) {
			if (userBrickVariableBehaviour.exists(userBrick, name)) {
				return true;
			}
		}
		return false;
	}

	public UserVariable findUserBrickVariable(UserBrick userBrick, String name) {
		return userBrickVariableBehaviour.find(userBrick, name);
	}

	public void deleteUserVariableFromUserBrick(UserBrick userBrick, String userVariableName) {
		userBrickVariableBehaviour.delete(userBrick, userVariableName);
	}

	//methods for project variables
	public UserVariable addProjectUserVariable(String userVariableName) {
		return projectVariableBehaviour.add(userVariableName);
	}

	public UserVariable renameProjectUserVariable(String newName, String oldName) {
		return projectVariableBehaviour.rename(newName, oldName);
	}

	public boolean existProjectVariable(UserVariable variable) {
		return getProjectVariables().contains(variable);
	}

	public boolean existProjectVariableWithName(String name) {
		return projectVariableBehaviour.exists(name);
	}

	public UserVariable findProjectVariable(String name) {
		return projectVariableBehaviour.find(name);
	}

	//methods for project lists
	public UserList addProjectUserList(String userListName) {
		return projectListBehaviour.add(userListName);
	}

	public UserList renameProjectUserList(String newName, String oldName) {
		return projectListBehaviour.rename(newName, oldName);
	}

	public boolean existProjectList(UserList list) {
		return getProjectLists().contains(list);
	}

	public boolean existProjectListWithName(String name) {
		return projectListBehaviour.exists(name);
	}

	public UserList findProjectList(String name) {
		return projectListBehaviour.find(name);
	}

	//getters/setters
	public void setProject(Project project) {
		this.project = project;
	}

	public void setUserBrickVariables(UserBrick key, List<UserVariable> userVariables) {
		userBrickVariables.put(key, userVariables);
	}

	public Map<Sprite, List<UserVariable>> getSpriteVariableMap() {
		return spriteVariables;
	}

	public Map<Sprite, List<UserList>> getSpriteListMap() {
		return spriteListOfLists;
	}

	public Map<UserBrick, List<UserVariable>> getUserBrickVariableMap() {
		return userBrickVariables;
	}

	public List<UserVariable> getProjectVariables() {
		if (project == null) {
			project = ProjectManager.getInstance().getCurrentProject();
		}
		return project.getProjectVariables();
	}

	public List<UserList> getProjectLists() {
		if (project == null) {
			project = ProjectManager.getInstance().getCurrentProject();
		}
		return project.getProjectLists();
	}

	public List<UserList> getSpriteListOfLists(Sprite sprite) {
		return spriteListOfLists.get(sprite);
	}

	public UserBrick getCurrentUserBrick() {
		return ProjectManager.getInstance().getCurrentUserBrick();
	}

	public Sprite getCurrentSprite() {
		return ProjectManager.getInstance().getCurrentSprite();
	}

	public Project getCurrentProject() {
		return ProjectManager.getInstance().getCurrentProject();
	}
}
