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
package org.catrobat.catroid.formulaeditor.datacontainer;

import android.support.annotation.NonNull;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataContainer implements Serializable {

	private static final long serialVersionUID = 1L;
	@XStreamAlias("objectVariableList")
	private Map<Sprite, List<UserVariable>> spriteVariables = new HashMap<>();
	@XStreamAlias("objectListOfList")
	private Map<Sprite, List<UserList>> spriteListOfLists = new HashMap<>();
	@XStreamAlias("userBrickVariableList")
	private Map<UserBrick, List<UserVariable>> userBrickVariables = new HashMap<>();

	private transient UserDataListWrapper<UserVariable> projectUserVariables = new UserDataListWrapper<>();
	private transient UserDataListWrapper<UserList> projectUserLists = new UserDataListWrapper<>();

	private transient UserDataMapWrapper<Sprite, UserVariable> spriteUserVariables =
			new UserDataMapWrapper<>(spriteVariables);
	private transient UserDataMapWrapper<Sprite, UserList> spriteUserLists =
			new UserDataMapWrapper<>(spriteListOfLists);
	private transient UserDataMapWrapper<UserBrick, UserVariable> userBrickUserVariables =
			new UserDataMapWrapper<>(userBrickVariables);

	public DataContainer() {
	}

	public DataContainer(Project project) {
		setProjectUserData(project);
	}

	protected Object readResolve() {
		spriteUserVariables = new UserDataMapWrapper<>(spriteVariables);
		spriteUserLists = new UserDataMapWrapper<>(spriteListOfLists);
		userBrickUserVariables = new UserDataMapWrapper<>(userBrickVariables);
		return this;
	}

	protected Object writeReplace() {
		Iterator<Map.Entry<Sprite, List<UserVariable>>> varIterator = spriteVariables.entrySet().iterator();
		while (varIterator.hasNext()) {
			Map.Entry<Sprite, List<UserVariable>> entry = varIterator.next();
			if (entry.getValue().isEmpty()) {
				varIterator.remove();
			}
		}

		Iterator<Map.Entry<Sprite, List<UserList>>> listIterator = spriteListOfLists.entrySet().iterator();
		while (listIterator.hasNext()) {
			Map.Entry<Sprite, List<UserList>> entry = listIterator.next();
			if (entry.getValue().isEmpty()) {
				listIterator.remove();
			}
		}

		return this;
	}

	public void setProjectUserData(Project project) {
		projectUserVariables = new UserDataListWrapper<>(project.getProjectVariables());
		projectUserLists = new UserDataListWrapper<>(project.getProjectLists());
	}

	public void setSpriteUserData(SupportDataContainer supportDataContainer) {
		for (Sprite sprite : supportDataContainer.spriteVariables.keySet()) {
			if (sprite != null) {
				spriteVariables.put(sprite, supportDataContainer.spriteVariables.get(sprite));
			}
		}

		for (Sprite sprite : supportDataContainer.spriteListOfLists.keySet()) {
			if (sprite != null) {
				spriteListOfLists.put(sprite, supportDataContainer.spriteListOfLists.get(sprite));
			}
		}

		for (UserBrick userBrick : supportDataContainer.userBrickVariables.keySet()) {
			userBrickVariables.put(userBrick, supportDataContainer.userBrickVariables.get(userBrick));
		}
	}

	public void setUserBrickVariables(UserBrick key, List<UserVariable> userVariables) {
		userBrickVariables.put(key, userVariables);
	}

	public boolean addUserVariable(UserVariable var) {
		return !spriteUserVariables.contains(var.getName()) && projectUserVariables.add(var);
	}

	public boolean addUserVariable(Sprite sprite, UserVariable var) {
		return !projectUserVariables.contains(var.getName()) && spriteUserVariables.add(sprite, var);
	}

	public UserVariable getProjectUserVariable(String name) {
		return projectUserVariables.get(name);
	}

	public List<UserVariable> getProjectUserVariables() {
		return projectUserVariables.getList();
	}

	public List<UserVariable> getSpriteUserVariables(Sprite sprite) {
		return spriteUserVariables.get(sprite);
	}

	public UserVariable getUserVariable(Sprite sprite, String name) {
		UserBrick currentUserBrick = ProjectManager.getInstance().getCurrentUserBrick();
		return getUserVariable(sprite, currentUserBrick, name);
	}

	public UserVariable getUserVariable(Sprite sprite, UserBrick userBrick, String name) {
		UserVariable var = spriteUserVariables.get(sprite, name);
		if (var == null) {
			var = userBrickUserVariables.get(userBrick, name);
		}
		if (var == null) {
			var = projectUserVariables.get(name);
		}
		return var;
	}

	public void removeUserVariable(String name) {
		spriteUserVariables.remove(name);
		userBrickUserVariables.remove(name);
		projectUserVariables.remove(name);
	}

	private void resetUserVariables() {
		resetUserVariables(projectUserVariables.getList());

		for (Sprite sprite : spriteUserVariables.keySet()) {
			resetUserVariables(spriteUserVariables.get(sprite));
		}
	}

	private void resetUserVariables(@NonNull List<UserVariable> variables) {
		for (UserVariable var : variables) {
			var.reset();
		}
	}

	public boolean addUserList(UserList list) {
		return !spriteUserLists.contains(list.getName()) && projectUserLists.add(list);
	}

	public boolean addUserList(Sprite sprite, UserList list) {
		return !projectUserLists.contains(list.getName()) && spriteUserLists.add(sprite, list);
	}

	public List<UserList> getProjectUserLists() {
		return projectUserLists.getList();
	}

	public List<UserList> getSpriteUserLists(Sprite sprite) {
		return spriteUserLists.get(sprite);
	}

	public UserList getUserList(String name) {
		return projectUserLists.get(name);
	}

	public UserList getUserList(Sprite sprite, String name) {
		UserList list = spriteUserLists.get(sprite, name);
		if (list == null) {
			list = projectUserLists.get(name);
		}
		return list;
	}

	public void removeUserList(String name) {
		spriteUserLists.remove(name);
		projectUserLists.remove(name);
	}

	private void resetUserLists() {
		resetUserLists(projectUserLists.getList());

		for (Sprite sprite : spriteUserLists.keySet()) {
			resetUserLists(spriteUserLists.get(sprite));
		}
	}

	private void resetUserLists(@NonNull List<UserList> lists) {
		for (UserList list : lists) {
			list.reset();
		}
	}

	public boolean addUserVariable(UserBrick userBrick, UserVariable var) {
		return userBrickUserVariables.add(userBrick, var);
	}

	public List<UserVariable> getUserBrickUserVariables(UserBrick userBrick) {
		return userBrickUserVariables.get(userBrick);
	}

	public void copySpriteUserData(Sprite srcSprite, DataContainer srcDataContainer, Sprite dstSprite) {
		for (UserVariable variable : srcDataContainer.getSpriteUserVariables(srcSprite)) {
			addUserVariable(dstSprite, new UserVariable(variable));
		}
		for (UserList list : srcDataContainer.getSpriteUserLists(srcSprite)) {
			addUserList(dstSprite, new UserList(list));
		}
	}

	public void removeSpriteUserData(Sprite sprite) {
		spriteUserVariables.remove(sprite);
		spriteUserLists.remove(sprite);

		for (UserBrick userBrick : sprite.getUserBrickList()) {
			userBrickUserVariables.remove(userBrick);
		}
	}

	public void removeUserDataOfClones() {
		Iterator<Map.Entry<Sprite, List<UserVariable>>> varIterator = spriteVariables.entrySet().iterator();
		while (varIterator.hasNext()) {
			Map.Entry<Sprite, List<UserVariable>> entry = varIterator.next();
			if (entry.getKey().isClone) {
				varIterator.remove();
			}
		}

		Iterator<Map.Entry<Sprite, List<UserList>>> listIterator = spriteListOfLists.entrySet().iterator();
		while (listIterator.hasNext()) {
			Map.Entry<Sprite, List<UserList>> entry = listIterator.next();
			if (entry.getKey().isClone) {
				listIterator.remove();
			}
		}
	}

	public void resetUserData() {
		resetUserLists();
		resetUserVariables();
	}

	public void updateSpriteUserDataMapping(Sprite previousKey, Sprite newKey) {
		spriteUserVariables.updateKey(previousKey, newKey);
		spriteUserLists.updateKey(previousKey, newKey);
	}
}
