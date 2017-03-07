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
package org.catrobat.catroid.content;

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;

import java.util.ArrayList;
import java.util.List;

public class GroupSprite extends Sprite {
	private static final long serialVersionUID = 1L;

	private transient boolean isExpanded = false;

	public GroupSprite(String name) {
		super(name);
	}

	public GroupSprite() {
		super();
	}

	public boolean shouldBeExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean expanded) {
		isExpanded = expanded;
	}

	public static List<Sprite> getSpritesFromGroupWithGroupName(String groupName) {
		List<Sprite> result = new ArrayList<Sprite>();
		List<Sprite> spriteList = ProjectManager.getInstance().getSceneToPlay().getSpriteList();
		int position = 0;
		for (Sprite sprite : spriteList) {
			if (groupName.equals(sprite.getName())) {
				break;
			}
			position++;
		}
		for (int childPosition = position + 1; childPosition < spriteList.size(); childPosition++) {
			Sprite spriteToCheck = spriteList.get(childPosition);
			if (spriteToCheck instanceof GroupItemSprite) {
				result.add(spriteToCheck);
			} else {
				break;
			}
		}
		return result;
	}

	@Override
	public void createCollisionPolygons() {
		Log.i("GroupSprite", "Creating Collision Polygons for all Sprites of group!");
		List<Sprite> groupSprites = getSpritesFromGroupWithGroupName(getName());
		for (Sprite sprite : groupSprites) {
			for (LookData lookData : sprite.getLookDataList()) {
				lookData.getCollisionInformation().calculate();
			}
		}
	}
}
