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
package org.catrobat.catroid.formulaeditor;

import android.util.Log;

import org.jetbrains.annotations.TestOnly;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class UserVariable implements Serializable, UserData<Object> {

	private static final long serialVersionUID = 1L;
	private String name;
	private int initialIndex = -1;
	private UUID deviceValueKey;
	private boolean isList = false;
	private final transient List<Object> value;
	private transient boolean visible = true;
	private transient boolean dummy = false;

	public UserVariable(String name) {
		this.name = name;
		this.deviceValueKey = UUID.randomUUID();
		this.value = new ArrayList<>();
	}

	public UserVariable(final String name, final Object value, Boolean isList) {
		this.name = name;
		this.deviceValueKey = UUID.randomUUID();
		this.value = new ArrayList<>();
		if (isList) {
			this.value.addAll((Collection<?>) value);
		} else {
			this.value.add(value);
		}
		this.isList = isList;
	}

	public UserVariable(UserVariable copy) {
		this.name = copy.name;
		this.initialIndex = copy.initialIndex;
		this.deviceValueKey = UUID.randomUUID();
		this.value = new ArrayList<>();
		this.value.addAll(copy.value);
		this.isList = copy.isList;
	}

	@TestOnly
	public UserVariable() {
		this.value = new ArrayList<>();
	}

	@TestOnly
	public UserVariable(boolean isList) {
		this.value = new ArrayList<>();
		this.isList = isList;
	}

	@TestOnly
	public UserVariable(final String name, final Object value) {
		this.name = name;
		this.deviceValueKey = UUID.randomUUID();
		this.value = new ArrayList<>();

		if (value instanceof List<?>) {
			this.value.addAll((Collection<?>) value);
			this.isList = true;
		} else {
			this.value.add(value);
		}
	}

	@TestOnly
	public UserVariable(final String name, final boolean isList) {
		this.name = name;
		this.deviceValueKey = UUID.randomUUID();
		this.value = new ArrayList<>();
		this.isList = isList;
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
		return isList ? value : (value.isEmpty() ? 0d : value.get(0));
	}

	@Override
	public void setValue(Object value) {
		this.value.clear();
		if (value instanceof ArrayList) {
			this.value.addAll((ArrayList<?>) value);
			this.isList = true;
		} else {
			this.value.add(value);
			this.isList = false;
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
		value.clear();
	}

	public void setToEmptyList() {
		value.clear();
		isList = true;
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
		return variableToCheck.value.equals(this.value);
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
		return isList;
	}

	public int getListSize() {
		return value.size();
	}

	public Object getListItem(int index) {
		return (index < 0 || index >= value.size() || !isList) ? null : value.get(index);
	}

	public int getIndexOfListItem(Object item) {
		return value.indexOf(item);
	}

	public void addListItem(Object listItem) {
		try {
			value.add(listItem);
		} catch (Exception exception) {
			Log.d(getClass().getSimpleName(), "addListItem failed", exception);
		}
		isList = true;
	}

	public void deleteListItemAtIndex(int index) {
		try {
			value.remove(index);
		} catch (Exception exception) {
			Log.d(getClass().getSimpleName(), "deleteListItemAtIndex failed", exception);
		}
	}

	public void insertListItemAtIndex(int index, Object item) {
		try {
			value.add(index, item);
		} catch (Exception exception) {
			Log.d(getClass().getSimpleName(), "insertListItemAtIndex failed", exception);
		}
	}

	public void setListItemAtIndex(int index, Object item) {
		try {
			value.set(index, item);
		} catch (Exception exception) {
			Log.d(getClass().getSimpleName(), "setListItemAtIndex failed", exception);
		}
	}

	public static Comparator<UserVariable> userVariableNameComparator =
			(userVariable1, userVariable2) -> userVariable1.name.compareTo(userVariable2.name);
}
