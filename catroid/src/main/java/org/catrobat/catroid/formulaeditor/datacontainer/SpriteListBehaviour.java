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

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class SpriteListBehaviour extends SpriteDataBehaviour<Sprite, UserList> {

	private DataContainer dataContainer;

	SpriteListBehaviour(DataContainer dataContainer) {
		this.dataContainer = dataContainer;
	}

	@Override
	protected void reset(List<UserList> dataList) {
		for (UserList userList : dataList) {
			userList.getList().clear();
		}
	}

	@Override
	protected Map<Sprite, List<UserList>> getDataMap() {
		return dataContainer.getSpriteListMap();
	}

	@Override
	protected UserList newInstance(String name) {
		return new UserList(name);
	}

	@Override
	protected UserList newInstance(String name, Object value) {
		UserList list = new UserList(name);
		setValue(list, value);
		return list;
	}

	@Override
	protected String getDataName(UserList data) {
		return data.getName();
	}

	@Override
	protected void setDataName(UserList data, String name) {
		data.setName(name);
	}

	@Override
	protected String getKeyName(Sprite key) {
		return key.getName();
	}

	@Override
	protected void setValue(UserList dataToAdd, Object value) {
		List<Object> values = new ArrayList<>();
		values.add(value);
		dataToAdd.setList(values);
	}

	@Override
	protected boolean isClone(Sprite key) {
		return key.isClone();
	}
}
