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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserList implements Serializable, UserData<List<Object>> {

	private static final long serialVersionUID = 1L;

	private String name;
	private int initialIndex = -1;
	private UUID deviceListKey;
	private transient List<Object> list;

	public UserList() {
		list = new ArrayList<>();
	}

	public UserList(final String name) {
		this.name = name;
		this.list = new ArrayList<>();
		this.deviceListKey = UUID.randomUUID();
	}

	public UserList(final String name, final List<Object> value) {
		this.name = name;
		this.list = value;
		this.deviceListKey = UUID.randomUUID();
	}

	public UserList(UserList userList) {
		this.name = userList.name;
		this.list = new ArrayList<>(userList.list);
		this.deviceListKey = UUID.randomUUID();
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
	public List<Object> getValue() {
		return list;
	}

	@Override
	public void setValue(List<Object> list) {
		this.list = list;
	}

	public void addListItem(Object listItem) {
		this.list.add(listItem);
	}

	public int getIndexOf(Object listItem) {
		return this.list.indexOf(listItem);
	}

	@Override
	public void reset() {
		list.clear();
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
		return ((UserList) obj).name.equals(name);
	}

	public boolean hasSameListSize(UserList listToCheck) {
		return listToCheck.list.size() == list.size();
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public UUID getDeviceKey() {
		return deviceListKey;
	}

	public void setDeviceListKey(UUID deviceListFileName) {
		this.deviceListKey = deviceListFileName;
	}
}
