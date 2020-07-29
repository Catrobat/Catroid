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
package org.catrobat.catroid.content;

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;

import java.util.ArrayList;
import java.util.List;

public class GroupSprite extends Sprite {

	private static final long serialVersionUID = 1L;

	private transient boolean collapsed = true;

	public GroupSprite() {
		super();
	}

	public GroupSprite(String name) {
		super(name);
	}

	public List<GroupItemSprite> getGroupItems() {
		List<Sprite> allSprites = ProjectManager.getInstance().getCurrentlyPlayingScene().getSpriteList();
		List<GroupItemSprite> groupItems = new ArrayList<>();

		int position = allSprites.indexOf(this);

		for (Sprite sprite : allSprites.subList(position + 1, allSprites.size())) {
			if (sprite instanceof GroupItemSprite) {
				groupItems.add((GroupItemSprite) sprite);
			} else {
				break;
			}
		}
		return groupItems;
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
		for (GroupItemSprite item : getGroupItems()) {
			item.setCollapsed(collapsed);
		}
	}

	public int getNumberOfItems() {
		return getGroupItems().size();
	}

	public static List<Sprite> getSpritesFromGroupWithGroupName(String groupName, List<Sprite> sprites) {
		List<Sprite> result = new ArrayList<>();
		int position = 0;
		for (Sprite sprite : sprites) {
			if (groupName.equals(sprite.getName())) {
				break;
			}
			position++;
		}
		for (int childPosition = position + 1; childPosition < sprites.size(); childPosition++) {
			Sprite spriteToCheck = sprites.get(childPosition);
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
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentlyPlayingScene().getSpriteList();
		List<Sprite> groupSprites = getSpritesFromGroupWithGroupName(getName(), spriteList);
		for (Sprite sprite : groupSprites) {
			for (LookData lookData : sprite.getLookList()) {
				lookData.getCollisionInformation().calculate();
			}
		}
	}
}
