/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.formulaeditor.function.UserVariableEntry;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class UserVariable implements Serializable, UserData<Object> {

	private static final long serialVersionUID = 1L;

	protected String name;
	protected int initialIndex = -1;
	protected UUID deviceValueKey;
	private transient boolean visible = true;
	private transient boolean dummy = false;

	private transient UserVariableEntry userVariableEntry;

	public UserVariable() {
		this.userVariableEntry = new UserVariableEntry();
	}

	public UserVariable(String name) {
		this.name = name;
		this.userVariableEntry = new UserVariableEntry();
		this.deviceValueKey = UUID.randomUUID();
	}

	public UserVariable(final String name, final Object value) {
		this.name = name;
		this.userVariableEntry = new UserVariableEntry();
		userVariableEntry.setValue(value);
		this.deviceValueKey = UUID.randomUUID();
	}

	public UserVariable(UserVariable variable) {
		this.name = variable.name;
		this.userVariableEntry = new UserVariableEntry(variable.userVariableEntry);
		this.deviceValueKey = UUID.randomUUID();
	}

	public int getInitialIndex() {
		return initialIndex;
	}

	public void setInitialIndex(int initialIndex) {
		this.initialIndex = initialIndex;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Object getValue() {
		if (this instanceof UserList) {
			return ((UserList) this).getValue();
		} else {
			return userVariableEntry.getValue();
		}
	}

	@Override
	public void setValue(Object value) {
		if (this instanceof UserList) {
			this.setValue(value);
		} else {
			userVariableEntry.setValue(value);
		}
	}

	public boolean getVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isDummy() {
		return dummy;
	}

	public void setDummy(boolean dummy) {
		this.dummy = dummy;
	}

	@Override
	public void reset() {
		this.userVariableEntry = new UserVariableEntry();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		return ((UserVariable) obj).name.equals(name);
	}

	public boolean hasSameValue(UserVariable variableToCheck) {
		return variableToCheck.userVariableEntry.hasSameValue(userVariableEntry);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public UUID getDeviceKey() {
		return deviceValueKey;
	}

	public void setDeviceValueKey(UUID deviceValueFileName) {
		this.deviceValueKey = deviceValueFileName;
	}

	public boolean isList() {
		return userVariableEntry.isList();
	}

	public void setIsList(boolean isList) {
		userVariableEntry.setIsList(isList);
	}

	public int getListSize() {
		if (this instanceof UserList) {
			return ((UserList) this).getSize();
		} else {
			return (userVariableEntry.getSize());
		}
	}

	public Object getListItem(int index) {
		if (this instanceof UserList) {
			return ((UserList) this).getValue().get(index);
		} else if (isList()) {
			return ((List<Object>) userVariableEntry.getValue()).get(index);
		}
		return null;
	}

	public int getIndexOfListItem(Object item) {
		if (this instanceof UserList) {
			return ((UserList) this).getIndexOf(item);
		} else if (isList()) {
			return ((List<Object>) userVariableEntry.getValue()).indexOf(item);
		}
		return 0;
	}

	public void addListItem(Object listItem) {
		if (this instanceof UserList) {
			((UserList) this).getValue().add(listItem);
		} else if (isList()) {
			((List<Object>) userVariableEntry.getValue()).add(listItem);
		}
	}

	public void deleteListItemAtIndex(int index) {
		if (this instanceof UserList) {
			((UserList) this).getValue().remove(index);
		} else if (isList()) {
			((List<Object>) userVariableEntry.getValue()).remove(index);
		}
	}

	public void insertListItemAtIndex(int index, Object item) {
		if (this instanceof UserList) {
			((UserList) this).getValue().add(index, item);
		} else if (isList()) {
			((List<Object>) userVariableEntry.getValue()).add(index, item);
		}
	}

	public void setListItemAtIndex(int index, Object item) {
		if (this instanceof UserList) {
			((UserList) this).getValue().set(index, item);
		} else if (isList()) {
			((List<Object>) userVariableEntry.getValue()).set(index, item);
		}
	}
}