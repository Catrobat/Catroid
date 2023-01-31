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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserList extends UserVariable implements Serializable {

	private transient List<Object> list;
	private UUID deviceListKey;
	private transient boolean visible = true;

	public UserList() {
		list = new ArrayList<>();
	}

	public UserList(final String name) {
		this.name = name;
		this.list = new ArrayList<>();
		this.deviceValueKey = UUID.randomUUID();
	}

	public UserList(final String name, final List<Object> value) {
		this.name = name;
		this.list = value;
		this.deviceValueKey = UUID.randomUUID();
	}

	public UserList(UserList userList) {
		this.name = userList.name;
		this.list = new ArrayList<>(userList.list);
		this.deviceValueKey = UUID.randomUUID();
	}

	public int getInitialIndex() {
		return initialIndex;
	}

	public void setInitialIndex(int initialIndex) {
		this.initialIndex = initialIndex;
	}

	@Override
	public List<Object> getValue() {
		return list;
	}

	@Override
	public void setValue(Object list) {
		this.list.clear();
		try {
			this.list.addAll((List<Object>)list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addListItem(Object item) {
		list.add(item);
	}

	public int getIndexOf(Object listItem) {
		return this.list.indexOf(listItem);
	}

	@Override
	public void reset() {
		list.clear();
	}

	public boolean hasSameListSize(UserList listToCheck) {
		return listToCheck.list.size() == list.size();
	}

	public int getSize() { return list.size(); }
}
