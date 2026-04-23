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
package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class UserDataHashMap extends HashMap<Brick.BrickData, UserData> implements Cloneable {

	private static final long serialVersionUID = 9030965461744658052L;

	@NotNull
	@Override
	public UserDataHashMap clone() {
		UserDataHashMap copiedMap = new UserDataHashMap();
		for (Map.Entry<Brick.BrickData, UserData> entry : entrySet()) {
			UserData userData = null;
			if (entry.getValue() != null) {
				if (Brick.BrickData.isUserList(entry.getKey())) {
					userData = new UserList((UserList) entry.getValue());
				} else {
					userData = new UserVariable((UserVariable) entry.getValue());
				}
			}

			copiedMap.put(entry.getKey(), userData);
		}
		return copiedMap;
	}
}
