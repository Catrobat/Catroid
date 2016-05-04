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
package org.catrobat.catroid.formulaeditor;

import android.content.Context;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrickElement;
import org.catrobat.catroid.ui.UserBrickScriptActivity;
import org.catrobat.catroid.ui.adapter.DataAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DataContainer implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final transient int USER_VARIABLE_SPRITE = 0;
	public static final transient int USER_VARIABLE_PROJECT = 1;
	public static final transient int USER_VARIABLE_USERBRICK = 2;
	public static final transient int USER_LIST_SPRITE = 4;
	public static final transient int USER_LIST_PROJECT = 5;
	public static final transient int USER_DATA_EMPTY = 6;

	@XStreamAlias("objectVariableList")
	private Map<Sprite, List<UserVariable>> spriteVariables;

	@XStreamAlias("userBrickVariableList")
	private Map<UserBrick, List<UserVariable>> userBrickVariables = new HashMap<>();

	@XStreamAlias("objectListOfList")
	private Map<Sprite, List<UserList>> spriteListOfLists;

	private transient Project project;

	public DataContainer(Project project) {
		spriteVariables = new HashMap<>();
		spriteListOfLists = new HashMap<>();

		this.project = project;
	}

	private DataContainer() {
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

	public void setProject(Project project) {
		this.project = project;
	}

	public List<UserVariable> getProjectVariables() {
		if (project == null) {
			project = ProjectManager.getInstance().getCurrentProject();
		}
		return project.getProjectVariables();
	}

	public Map<Sprite, List<UserVariable>> getSpriteVariableMap() {
		return spriteVariables;
	}

	public List<UserList> getProjectLists() {
		if (project == null) {
			project = ProjectManager.getInstance().getCurrentProject();
		}
		return project.getProjectLists();
	}

	public DataAdapter createDataAdapter(Context context, Sprite sprite) {
		List<UserVariable> userBrickVariables = new LinkedList<>();
		List<UserVariable> spriteVariables = getOrCreateVariableListForSprite(sprite);
		List<UserList> spriteUserList = getOrCreateUserListListForSprite(sprite);
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
		List<UserList> spriteUserList = getOrCreateUserListListForSprite(sprite);
		return new DataAdapter(context, spriteUserList, getProjectLists(), spriteVariables, getProjectVariables(), userBrickVariables);
	}

	public UserVariable getUserVariable(String userVariableName, Sprite sprite) {
		UserVariable userVariable;
		userVariable = findUserVariable(userVariableName, getOrCreateVariableListForSprite(sprite));
		if (userVariable == null) {
			userVariable = findUserVariable(userVariableName, getProjectVariables());
		}

		UserBrick userBrick = getCurrentUserBrick();
		if (userVariable == null && userBrick != null) {
			userVariable = findUserVariable(userVariableName, getOrCreateVariableListForUserBrick(userBrick));
		}
		return userVariable;
	}

	public UserVariable addSpriteVariableIfDoesNotExist(String userVariableName, Sprite sprite) {
		UserVariable addedUserVariable = null;
		List<UserVariable> list = spriteVariables.get(sprite);

		if (list == null) {
			list = new ArrayList<>();
		}

		for (UserVariable variable : list) {
			if (variable.getName().equals(userVariableName)) {
				addedUserVariable = variable;
			}
		}

		if (addedUserVariable == null) {
			addedUserVariable = new UserVariable(userVariableName);
			list.add(addedUserVariable);
			spriteVariables.put(sprite, list);
		}
		return addedUserVariable;
	}

	public UserList addSpriteListIfDoesNotExist(String userListName, Sprite sprite) {
		UserList addedUserList = null;
		List<UserList> lists = spriteListOfLists.get(sprite);

		if (lists == null) {
			lists = new ArrayList<>();
		}

		for (UserList list : lists) {
			if (list.getName().equals(userListName)) {
				addedUserList = list;
			}
		}

		if (addedUserList == null) {
			addedUserList = new UserList(userListName);
			lists.add(addedUserList);
			spriteListOfLists.put(sprite, lists);
		}
		return addedUserList;
	}

	public UserVariable addUserBrickVariableToUserBrickIfNotExists(UserBrick userBrick, String userVariableName, Object userVariableValue) {
		List<UserVariable> varList = getOrCreateVariableListForUserBrick(userBrick);
		UserVariable userVariableToAdd = null;

		for (UserVariable existingVariable : varList) {
			if (existingVariable.getName().equals(userVariableName)) {
				userVariableToAdd = existingVariable;
				break;
			}
		}

		if (userVariableToAdd == null) {
			userVariableToAdd = new UserVariable(userVariableName, userVariableValue);
			varList.add(userVariableToAdd);
		}

		return userVariableToAdd;
	}

	public UserVariable addUserBrickVariableToUserBrick(UserBrick userBrick, String userVariableName,
			Object userVariableValue) {
		List<UserVariable> varList = getOrCreateVariableListForUserBrick(userBrick);
		UserVariable userVariableToAdd = new UserVariable(userVariableName, userVariableValue);
		varList.add(userVariableToAdd);
		return userVariableToAdd;
	}

	public UserVariable addSpriteUserVariable(String userVariableName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		return addSpriteUserVariableToSprite(currentSprite, userVariableName);
	}

	public UserList renameSpriteUserList(String newName, String oldName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		List<UserList> varList = getOrCreateUserListListForSprite(currentSprite);
		UserList userListToRename = findUserList(oldName, varList);
		userListToRename.setName(newName);
		return userListToRename;
	}

	public UserVariable renameSpriteUserVariable(String newName, String oldName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		List<UserVariable> varList = getOrCreateVariableListForSprite(currentSprite);
		UserVariable userVariableToRename = findUserVariable(oldName, varList);
		userVariableToRename.setName(newName);
		return userVariableToRename;
	}

	public UserVariable addSpriteUserVariableToSprite(Sprite sprite, String userVariableName) {
		List<UserVariable> varList = getOrCreateVariableListForSprite(sprite);
		UserVariable userVariableToAdd = new UserVariable(userVariableName);
		varList.add(userVariableToAdd);
		return userVariableToAdd;
	}

	public UserVariable addProjectUserVariable(String userVariableName) {
		UserVariable userVariableToAdd = new UserVariable(userVariableName);
		getProjectVariables().add(userVariableToAdd);
		return userVariableToAdd;
	}

	public UserVariable renameProjectUserVariable(String newName, String oldName) {
		UserVariable userVariableToRename = findUserVariable(oldName, getProjectVariables());
		userVariableToRename.setName(newName);
		return userVariableToRename;
	}

	/**
	 * This function deletes the user variable with userVariableName in the current context.
	 *
	 * The current context consists of all global variables, the sprite variables for the current sprite,
	 * and the user brick variables for the current user brick.
	 */
	public void deleteUserVariableByName(String userVariableName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		UserBrick userBrick = getCurrentUserBrick();
		List<UserVariable> context = getUserVariableContext(userVariableName, userBrick, currentSprite);
		if (context != null) {
			UserVariable variableToDelete = findUserVariable(userVariableName, context);
			if (variableToDelete != null) {
				context.remove(variableToDelete);
				if (userBrick != null) {
					List<UserScriptDefinitionBrickElement> currentElements = userBrick.getUserScriptDefinitionBrickElements();
					for (int id = 0; id < currentElements.size(); id++) {
						if (currentElements.get(id).getText().equals(userVariableName) && currentElements.get(id).isVariable()) {
							int alpha = userBrick.getAlphaValue();
							Context alphaView = userBrick.getDefinitionBrick().getViewWithAlpha(alpha).getContext();
							userBrick.getDefinitionBrick().removeVariablesInFormulas(currentElements.get(id).getText(), alphaView);
							currentElements.remove(id);
						}
					}
				}
			}
		}
	}

	public List<UserVariable> getOrCreateVariableListForSprite(Sprite sprite) {
		List<UserVariable> variables = spriteVariables.get(sprite);

		if (variables == null) {
			variables = new ArrayList<>();
			spriteVariables.put(sprite, variables);
		}
		return variables;
	}

	public List<UserVariable> getVariableListForSprite(Sprite sprite) {
		List<UserVariable> variables = spriteVariables.get(sprite);

		if (variables == null) {
			variables = new ArrayList<>();
		}
		return variables;
	}

	public void cleanVariableListForSprite(Sprite sprite) {
		List<UserVariable> variables = spriteVariables.get(sprite);
		if (variables != null) {
			variables.clear();
		}
		spriteVariables.remove(sprite);
	}

	public UserVariable getUserVariable(String name, UserBrick userBrick, Sprite currentSprite) {
		List<UserVariable> contextList = getUserVariableContext(name, userBrick, currentSprite);
		return findUserVariable(name, contextList);
	}

	/**
	 * This function finds the user variable with userVariableName in the current context.
	 *
	 * The current context consists of all global variables, the sprite variables for the current sprite,
	 * and the user brick variables for the current user brick.
	 */
	public List<UserVariable> getUserVariableContext(String name, UserBrick userBrick, Sprite currentSprite) {
		UserVariable variableToReturn;
		List<UserVariable> spriteVariables = getOrCreateVariableListForSprite(currentSprite);
		variableToReturn = findUserVariable(name, spriteVariables);
		if (variableToReturn != null) {
			return spriteVariables;
		}

		if (userBrick != null) {
			List<UserVariable> userBrickVariables = getOrCreateVariableListForUserBrick(userBrick);
			variableToReturn = findUserVariable(name, userBrickVariables);
			if (variableToReturn != null) {
				return userBrickVariables;
			}
		}

		variableToReturn = findUserVariable(name, getProjectVariables());
		if (variableToReturn != null) {
			return getProjectVariables();
		}
		return null;
	}

	public UserVariable findUserVariable(String name, List<UserVariable> variables) {
		if (variables == null) {
			return null;
		}
		for (UserVariable variable : variables) {
			if (variable.getName().equals(name)) {
				return variable;
			}
		}
		return null;
	}

	public void resetAllDataObjects() {
		resetAllUserLists();
		resetAllUserVariables();
	}

	public void resetLocalDataObjects() {
		for (Sprite currentSprite : spriteVariables.keySet()) {
			resetUserVariables(spriteVariables.get(currentSprite));
		}

		for (Sprite currentSprite : spriteListOfLists.keySet()) {
			resetUserLists(spriteListOfLists.get(currentSprite));
		}
	}

	private void resetAllUserVariables() {
		resetUserVariables(getProjectVariables());

		for (Sprite currentSprite : spriteVariables.keySet()) {
			resetUserVariables(spriteVariables.get(currentSprite));
		}
	}

	private void resetUserVariables(List<UserVariable> userVariableList) {
		for (UserVariable userVariable : userVariableList) {
			userVariable.setValue(0.0);
		}
	}

	public UserList getUserList(String userListName, Sprite sprite) {
		UserList userList;
		userList = findUserList(userListName, getOrCreateUserListListForSprite(sprite));
		if (userList == null) {
			userList = findUserList(userListName, getProjectLists());
		}
		return userList;
	}

	public UserList addSpriteUserList(String userListName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		return addSpriteUserListToSprite(currentSprite, userListName);
	}

	public UserList addSpriteUserListToSprite(Sprite sprite, String userListName) {
		UserList userListToAdd = new UserList(userListName);
		List<UserList> listOfUserLists = getOrCreateUserListListForSprite(sprite);
		listOfUserLists.add(userListToAdd);
		return userListToAdd;
	}

	public UserList addProjectUserList(String userListName) {
		UserList userListToAdd = new UserList(userListName);
		getProjectLists().add(userListToAdd);
		return userListToAdd;
	}

	public UserList renameProjectUserList(String newName, String oldName) {
		UserList userListToRename = findUserList(oldName, getProjectLists());
		userListToRename.setName(newName);
		return userListToRename;
	}

	public void deleteUserListByName(String userListName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		UserList listToDelete;
		List<UserList> spriteVariables = getOrCreateUserListListForSprite(currentSprite);
		listToDelete = findUserList(userListName, spriteVariables);
		if (listToDelete != null) {
			spriteVariables.remove(listToDelete);
		}

		listToDelete = findUserList(userListName, getProjectLists());
		if (listToDelete != null) {
			getProjectLists().remove(listToDelete);
		}
	}

	public List<UserList> getOrCreateUserListListForSprite(Sprite sprite) {
		List<UserList> userLists = spriteListOfLists.get(sprite);
		if (userLists == null) {
			userLists = new ArrayList<>();
			spriteListOfLists.put(sprite, userLists);
		}
		return userLists;
	}

	public List<UserList> getUserListListForSprite(Sprite sprite) {
		List<UserList> userLists = spriteListOfLists.get(sprite);
		if (userLists == null) {
			userLists = new ArrayList<>();
		}
		return userLists;
	}

	public void cleanUserListForSprite(Sprite sprite) {
		List<UserList> listOfUserLists = spriteListOfLists.get(sprite);
		if (listOfUserLists != null) {
			listOfUserLists.clear();
		}
		spriteListOfLists.remove(sprite);
	}

	public UserList findUserList(String name, List<UserList> userLists) {
		if (userLists == null) {
			return null;
		}
		for (UserList userList : userLists) {
			if (userList.getName().equals(name)) {
				return userList;
			}
		}
		return null;
	}

	private void resetAllUserLists() {
		resetUserLists(getProjectLists());

		for (Sprite currentSprite : spriteListOfLists.keySet()) {
			resetUserLists(spriteListOfLists.get(currentSprite));
		}
	}

	private void resetUserLists(List<UserList> userVariableList) {
		for (UserList userList : userVariableList) {
			userList.getList().clear();
		}
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

	public boolean existSpriteVariable(UserVariable variable, Sprite sprite) {
		List<UserVariable> list = spriteVariables.get(sprite);
		if (list == null) {
			return false;
		}
		return list.contains(variable);
	}

	public boolean existSpriteVariableByName(String variableName, Sprite sprite) {
		if (sprite == null) {
			return false;
		}
		List<UserVariable> list = spriteVariables.get(sprite);
		if (list == null) {
			return false;
		}
		for (UserVariable variable : list) {
			if (variable.getName().equals(variableName)) {
				return true;
			}
		}
		return false;
	}

	public boolean existSpriteListByName(String listName, Sprite sprite) {
		if (sprite == null) {
			return false;
		}
		List<UserList> lists = spriteListOfLists.get(sprite);
		if (lists == null) {
			return false;
		}
		for (UserList list : lists) {
			if (list.getName().equals(listName)) {
				return true;
			}
		}
		return false;
	}

	public boolean existListInAnySprite(String listName, List<Sprite> sprites) {
		for (Sprite sprite : sprites) {
			if (existSpriteListByName(listName, sprite)) {
				return true;
			}
		}
		return false;
	}

	public boolean existVariableInAnySprite(String variableName, List<Sprite> sprites) {
		for (Sprite sprite : sprites) {
			if (existSpriteVariableByName(variableName, sprite)) {
				return true;
			}
		}
		return false;
	}

	public boolean existProjectVariable(UserVariable variable) {
		return getProjectVariables().contains(variable);
	}

	public boolean existSpriteList(UserList userList, Sprite sprite) {
		List<UserList> list = spriteListOfLists.get(sprite);
		if (list == null) {
			return false;
		}
		return list.contains(userList);
	}

	public boolean existProjectList(UserList list) {
		return getProjectLists().contains(list);
	}

	public boolean existProjectVariableWithName(String name) {
		for (UserVariable variable : getProjectVariables()) {
			if (name.equals(variable.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean existProjectListWithName(String name) {
		for (UserList list : getProjectLists()) {
			if (name.equals(list.getName())) {
				return true;
			}
		}
		return false;
	}

	public Integer getTypeOfUserVariable(String userVariableName, Sprite sprite) {
		UserVariable userVariable;
		userVariable = findUserVariable(userVariableName, getOrCreateVariableListForSprite(sprite));
		if (userVariable != null) {
			return USER_VARIABLE_SPRITE;
		}
		userVariable = findUserVariable(userVariableName, getProjectVariables());
		if (userVariable != null) {
			return USER_VARIABLE_PROJECT;
		}
		UserBrick userBrick = getCurrentUserBrick();
		if (userBrick != null) {
			userVariable = findUserVariable(userVariableName, getOrCreateVariableListForUserBrick(userBrick));
			if (userVariable != null) {
				return USER_VARIABLE_USERBRICK;
			}
		}
		return USER_VARIABLE_PROJECT;
	}

	public Integer getTypeOfUserList(String userListName, Sprite sprite) {
		UserList userList;
		userList = findUserList(userListName, getOrCreateUserListListForSprite(sprite));
		if (userList != null) {
			return USER_LIST_SPRITE;
		}
		userList = findUserList(userListName, getProjectLists());
		if (userList != null) {
			return USER_LIST_PROJECT;
		}
		return -1;
	}

	public List<UserList> getSpriteListOfLists(Sprite sprite) {
		return spriteListOfLists.get(sprite);
	}

	public UserBrick getCurrentUserBrick() {
		return ProjectManager.getInstance().getCurrentUserBrick();
	}

	public void deleteUserVariableFromUserBrick(UserBrick userBrick, String userVariableName) {
		List<UserVariable> context = userBrickVariables.get(userBrick);
		UserVariable variableToDelete = findUserVariable(userVariableName, context);
		if (variableToDelete != null) {
			context.remove(variableToDelete);
		}
	}

	public List<UserVariable> getOrCreateVariableListForUserBrick(UserBrick userBrick) {
		//TODO: Ask Stefan Jaindl if this works
		if (userBrick == null) {
			return new ArrayList<>();
		}
		List<UserVariable> variables = userBrickVariables.get(userBrick);

		if (variables == null) {
			variables = new ArrayList<>();
			userBrickVariables.put(userBrick, variables);
		}

		return variables;
	}

	public String getUniqueVariableName(Context context) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		String name = context.getResources().getString(R.string.new_user_brick_variable).trim();

		int variableCounter = 0;
		String originalName = name;
		name += " " + variableCounter;

		while (getUserVariable(name, currentSprite) != null) {
			variableCounter++;
			name = originalName + " " + variableCounter;
		}
		name = originalName + " " + variableCounter;

		return name;
	}

	public void setUserBrickVariables(UserBrick key, List<UserVariable> userVariables) {
		userBrickVariables.put(key, userVariables);
	}
}
