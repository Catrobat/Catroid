/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.formulaeditor;

import android.content.Context;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.adapter.UserListAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserListContainer implements Serializable {
	private static final long serialVersionUID = 1L;

	@XStreamAlias("programListOfLists")
	private List<UserList> projectLists;
	@XStreamAlias("objectListOfList")
	private Map<Sprite, List<UserList>> spriteListOfLists;

	public UserListContainer() {
		projectLists = new ArrayList<UserList>();
		spriteListOfLists = new HashMap<Sprite, List<UserList>>();
	}

	public UserListAdapter createUserListAdapter(Context context, Sprite sprite) {
		return new UserListAdapter(context, getOrCreateVariableListForSprite(sprite), projectLists);
	}

	public UserList getUserList(String userListName, Sprite sprite) {
		UserList userList;
		userList = findUserList(userListName, getOrCreateVariableListForSprite(sprite));
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
		List<UserList> listOfUserLists = getOrCreateVariableListForSprite(sprite);
		listOfUserLists.add(userListToAdd);
		return userListToAdd;
	}

	public UserList addProjectUserList(String userListName) {
		UserList userListToAdd = new UserList(userListName);
		projectLists.add(userListToAdd);
		return userListToAdd;
	}

	public void deleteUserListByName(String userVariableName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		UserList listToDelete;
		List<UserList> spriteVariables = getOrCreateVariableListForSprite(currentSprite);
		listToDelete = findUserList(userVariableName, spriteVariables);
		if (listToDelete != null) {
			spriteVariables.remove(listToDelete);
		}

		listToDelete = findUserList(userVariableName, projectLists);
		if (listToDelete != null) {
			projectLists.remove(listToDelete);
		}
	}

	private List<UserList> getOrCreateVariableListForSprite(Sprite sprite) {
		List<UserList> userLists = spriteListOfLists.get(sprite);
		if (userLists == null) {
			userLists = new ArrayList<UserList>();
			spriteListOfLists.put(sprite, userLists);
		}
		return userLists;
	}

	public void cleanVariableListForSprite(Sprite sprite) {
		List<UserList> vars = spriteListOfLists.get(sprite);
		if (vars != null) {
			vars.clear();
		}
		spriteListOfLists.remove(sprite);
	}

	private UserList findUserList(String name, List<UserList> userLists) {
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

	public void resetAllUserLists() {

		resetUserLists(projectLists);

		Iterator<Sprite> spriteIterator = spriteListOfLists.keySet().iterator();
		while (spriteIterator.hasNext()) {
			Sprite currentSprite = spriteIterator.next();
			resetUserLists(spriteListOfLists.get(currentSprite));
		}
	}

	private void resetUserLists(List<UserList> userVariableList) {
		for (UserList userList : userVariableList) {
			userList.getList().clear();
		}
	}
}