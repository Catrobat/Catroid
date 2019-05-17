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

package org.catrobat.catroid.content.backwardcompatibility;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.XStreamFieldKeyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XStreamAlias("scene")
@XStreamFieldKeyOrder({
		"name",
		"objectList",
		"data"
})
public class SceneUntilLanguageVersion0999 implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private List<Sprite> objectList = new ArrayList<>();
	private LegacyDataContainer data;

	public String getName() {
		return name;
	}

	public List<Sprite> getSpriteList() {
		return objectList;
	}

	public List<UserVariable> getSpriteUserVariables(Sprite sprite) {
		if (data != null && data.spriteVariables.get(sprite) != null) {
			return data.spriteVariables.get(sprite);
		}
		return Collections.emptyList();
	}

	public List<UserList> getSpriteUserLists(Sprite sprite) {
		if (data != null && data.spriteListOfLists.get(sprite) != null) {
			return data.spriteListOfLists.get(sprite);
		}
		return Collections.emptyList();
	}
}
