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

import android.annotation.SuppressLint;
import android.content.Context;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrickElements;
import org.catrobat.catroid.ui.adapter.DataAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DataContainer implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int INVALID_ID = -1;

	public static final transient int USER_VARIABLE_SPRITE = 0;
	public static final transient int USER_VARIABLE_PROJECT = 1;
	public static final transient int USER_VARIABLE_USERBRICK = 2;
	public static final transient int USER_LIST_SPRITE = 4;
	public static final transient int USER_LIST_PROJECT = 5;
	public static final transient int USER_DATA_EMPTY = 6;

	@XStreamAlias("programVariableList")
	private List<UserVariable> projectVariables;
	@XStreamAlias("objectVariableList")
	private Map<Sprite, List<UserVariable>> spriteVariables;

	@XStreamAlias("userBrickVariableList")
	@SuppressLint("UseSparseArrays")
	private Map<Integer, List<UserVariable>> userBrickVariables = new HashMap<>();

	@XStreamAlias("programListOfLists")
	private List<UserList> projectLists;
	@XStreamAlias("objectListOfList")
	private Map<Sprite, List<UserList>> spriteListOfLists;

	public DataContainer() {
		projectVariables = new ArrayList<>();
		spriteVariables = new HashMap<>();

		projectLists = new ArrayList<>();
		spriteListOfLists = new HashMap<>();
	}

	public List<UserVariable> getProjectVariables() {
		return projectVariables;
	}

	public Map<Sprite, List<UserVariable>> getSpriteVariableMap() {
		return spriteVariables;
	}

	public List<UserList> getProjectLists() {
		return projectLists;
	}

	public Map<Integer, List<UserVariable>> getUserBrickVariables() {
		return userBrickVariables;
	}

	public DataAdapter createDataAdapter(Context context, Sprite sprite) {
		List<UserVariable> userBrickVariables = new LinkedList<>();
		List<UserVariable> spriteVariables = getOrCreateVariableListForSprite(sprite);
		List<UserList> spriteUserList = getOrCreateUserListListForSprite(sprite);
		return new DataAdapter(context, spriteUserList, projectLists, spriteVariables, projectVariables, userBrickVariables);
	}

	public DataAdapter createDataAdapter(Context context, int userBrickId, Sprite sprite, boolean inUserBrick) {
		List<UserVariable> userBrickVariables;
		if (userBrickId == INVALID_ID || !inUserBrick) {
			userBrickVariables = new LinkedList<>();
		} else {
			userBrickVariables = getOrCreateVariableListForUserBrick(userBrickId);
		}
		List<UserVariable> spriteVariables = getOrCreateVariableListForSprite(sprite);
		List<UserList> spriteUserList = getOrCreateUserListListForSprite(sprite);
		return new DataAdapter(context, spriteUserList, projectLists, spriteVariables, projectVariables, userBrickVariables);
	}

	public UserVariable getUserVariable(String userVariableName, Sprite sprite) {
		UserVariable userVariable;
		userVariable = findUserVariable(userVariableName, getOrCreateVariableListForSprite(sprite));
		if (userVariable == null) {
			userVariable = findUserVariable(userVariableName, projectVariables);
		}
		if (userVariable == null && ProjectManager.getInstance().getCurrentUserBrick() != null) {
			int id = ProjectManager.getInstance().getCurrentUserBrick().getUserBrickId();
			userVariable = findUserVariable(userVariableName, getOrCreateVariableListForUserBrick(id));
		}
		return userVariable;
	}

	public UserVariable addSpriteVariableIfDontExist(String userVariableName, Sprite sprite) {
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

	public UserList addSpriteListIfDontExist(String userListName, Sprite sprite) {
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

	public UserList renameSpriteUserList(String newName, String oldName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		List<UserList> varList = getOrCreateUserListListForSprite(currentSprite);
		UserList userListToRename = findUserList(oldName, varList);
		userListToRename.setName(newName);
		return userListToRename;
	}

	public UserVariable addUserBrickUserVariableToUserBrick(int userBrickId, String userVariableName, Object userVariableValue) {
		List<UserVariable> varList = getOrCreateVariableListForUserBrick(userBrickId);
		UserVariable userVariableToAdd = new UserVariable(userVariableName, userVariableValue);
		varList.add(userVariableToAdd);
		return userVariableToAdd;
	}

	public UserVariable addSpriteUserVariable(String userVariableName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		return addSpriteUserVariableToSprite(currentSprite, userVariableName);
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
		projectVariables.add(userVariableToAdd);
		return userVariableToAdd;
	}

	public UserVariable renameProjectUserVariable(String newName, String oldName) {
		UserVariable userVariableToRename = findUserVariable(oldName, projectVariables);
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
		UserBrick currentUserBrick = ProjectManager.getInstance().getCurrentUserBrick();
		int userBrickId = INVALID_ID;
		if (currentUserBrick != null) {
			userBrickId = currentUserBrick.getDefinitionBrick().getUserBrickId();
		}
		List<UserVariable> context = getUserVariableContext(userVariableName, userBrickId, currentSprite);
		if (context != null) {
			UserVariable variableToDelete = findUserVariable(userVariableName, context);
			if (variableToDelete != null) {
				context.remove(variableToDelete);
				if (currentUserBrick != null) {
					UserScriptDefinitionBrickElements currentElements = currentUserBrick.getUserScriptDefinitionBrickElements();
					for (int id = 0; id < currentElements.getUserScriptDefinitionBrickElementList().size(); id++) {
						if (currentElements.getUserScriptDefinitionBrickElementList().get(id).name.equals(userVariableName) && currentElements.getUserScriptDefinitionBrickElementList().get(id).isVariable) {
							int alpha = currentUserBrick.getAlphaValue();
//							Context context
							currentUserBrick.getDefinitionBrick().removeVariablesInFormulas(currentElements.getUserScriptDefinitionBrickElementList().get(id).name, currentUserBrick.getDefinitionBrick().getViewWithAlpha(alpha).getContext());
							currentElements.getUserScriptDefinitionBrickElementList().remove(id);
							currentElements.incrementVersion();
						}
					}
				}
			}
		}
	}

	public void deleteUserVariableFromUserBrick(int userBrickId, String userVariableName) {
		List<UserVariable> context = userBrickVariables.get(userBrickId);
		UserVariable variableToDelete = findUserVariable(userVariableName, context);
		if (variableToDelete != null) {
			context.remove(variableToDelete);
		}
	}

	public List<UserVariable> getOrCreateVariableListForUserBrick(int userBrickId) {
		List<UserVariable> variables = userBrickVariables.get(userBrickId);

		if (variables == null) {
			variables = new ArrayList<UserVariable>();
			userBrickVariables.put(userBrickId, variables);
		}

		return variables;
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
			variables = new ArrayList<UserVariable>();
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

	public UserVariable getUserVariable(String name, int userBrickId, Sprite currentSprite) {
		List<UserVariable> contextList = getUserVariableContext(name, userBrickId, currentSprite);
		return findUserVariable(name, contextList);
	}

	/**
	 * This function finds the user variable with userVariableName in the current context.
	 *
	 * The current context consists of all global variables, the sprite variables for the current sprite,
	 * and the user brick variables for the current user brick.
	 */
	public List<UserVariable> getUserVariableContext(String name, int userBrickId, Sprite currentSprite) {
		UserVariable variableToReturn;
		List<UserVariable> spriteVariables = getOrCreateVariableListForSprite(currentSprite);
		variableToReturn = findUserVariable(name, spriteVariables);
		if (variableToReturn != null) {
			return spriteVariables;
		}

		if (userBrickId != INVALID_ID) {
			List<UserVariable> userBrickVariables = getOrCreateVariableListForUserBrick(userBrickId);
			variableToReturn = findUserVariable(name, userBrickVariables);
			if (variableToReturn != null) {
				return userBrickVariables;
			}
		}

		variableToReturn = findUserVariable(name, projectVariables);
		if (variableToReturn != null) {
			return projectVariables;
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

	private void resetAllUserVariables() {
		resetUserVariables(projectVariables);

		for (Sprite currentSprite : spriteVariables.keySet()) {
			resetUserVariables(spriteVariables.get(currentSprite));
		}
		for (int key : userBrickVariables.keySet()) {
			resetUserVariables(userBrickVariables.get(key));
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
			userList = findUserList(userListName, projectLists);
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
		projectLists.add(userListToAdd);
		return userListToAdd;
	}

	public UserList renameProjectUserList(String newName, String oldName) {
		UserList userListToRename = findUserList(oldName, projectLists);
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

		listToDelete = findUserList(userListName, projectLists);
		if (listToDelete != null) {
			projectLists.remove(listToDelete);
		}
	}

	public List<UserList> getOrCreateUserListListForSprite(Sprite sprite) {
		List<UserList> userLists = spriteListOfLists.get(sprite);
		if (userLists == null) {
			userLists = new ArrayList<UserList>();
			spriteListOfLists.put(sprite, userLists);
		}
		return userLists;
	}

	public List<UserList> getUserListListForSprite(Sprite sprite) {
		List<UserList> userLists = spriteListOfLists.get(sprite);
		if (userLists == null) {
			userLists = new ArrayList<UserList>();
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
		resetUserLists(projectLists);

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
		if (projectLists.size() > 0) {
			return projectLists.get(0);
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
		return projectVariables.contains(variable);
	}

	public boolean existSpriteList(UserList userList, Sprite sprite) {
		List<UserList> list = spriteListOfLists.get(sprite);
		if (list == null) {
			return false;
		}
		return list.contains(userList);
	}

	public boolean existProjectList(UserList list) {
		return projectLists.contains(list);
	}

	public boolean existProjectVariableWithName(String name) {
		for (UserVariable variable : projectVariables) {
			if (name.equals(variable.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean existProjectListWithName(String name) {
		for (UserList list : projectLists) {
			if (name.equals(list.getName())) {
				return true;
			}
		}
		return false;
	}

	public void addUserBrickVariableElements(int key, List<UserVariable> userVariables) {
		List<UserVariable> allUserVariables = userBrickVariables.get(key);

		if (allUserVariables == null) {
			allUserVariables = new ArrayList<UserVariable>();
		} else {
			userBrickVariables.remove(key);
		}
		allUserVariables.addAll(userVariables);
		userBrickVariables.put(key, allUserVariables);
	}

	public Integer getTypeOfUserVariable(String userVariableName, Sprite sprite) {
		UserVariable userVariable;
		userVariable = findUserVariable(userVariableName, getOrCreateVariableListForSprite(sprite));
		if (userVariable != null) {
			return USER_VARIABLE_SPRITE;
		}
		userVariable = findUserVariable(userVariableName, projectVariables);
		if (userVariable != null) {
			return USER_VARIABLE_PROJECT;
		}
		if (ProjectManager.getInstance().getCurrentUserBrick() != null) {
			int id = ProjectManager.getInstance().getCurrentUserBrick().getUserBrickId();
			userVariable = findUserVariable(userVariableName, getOrCreateVariableListForUserBrick(id));
			if (userVariable != null) {
				return USER_VARIABLE_USERBRICK;
			}
		}
		return -1;
	}

	public Integer getTypeOfUserList(String userListName, Sprite sprite) {
		UserList userList;
		userList = findUserList(userListName, getOrCreateUserListListForSprite(sprite));
		if (userList != null) {
			return USER_LIST_SPRITE;
		}
		userList = findUserList(userListName, projectLists);
		if (userList != null) {
			return USER_LIST_PROJECT;
		}
		return -1;
	}

	public List<UserList> getSpriteListOfLists(Sprite sprite) {
		return spriteListOfLists.get(sprite);
	}
}
