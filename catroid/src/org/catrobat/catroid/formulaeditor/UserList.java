/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

public class UserList implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private transient List<Object> list;

	public UserList() {
		list = new ArrayList<Object>();
	}

	public UserList(final String name) {
		this.name = name;
		this.list = new ArrayList<Object>();
	}

	public UserList(final String name, final List<Object> value) {
		this.name = name;
		this.list = value;
	}

	public List<Object> getList() {
		return list;
	}

	public void addListItem(Object listItem) {
		this.list.add(listItem);
	}

	public void setList(List<Object> list) {
		this.list = list;
	}

	public String getName() {
		return name;
	}
}
