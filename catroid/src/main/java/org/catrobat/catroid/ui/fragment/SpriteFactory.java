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
package org.catrobat.catroid.ui.fragment;

import com.badlogic.gdx.physics.box2d.Body;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.GroupItemSprite;
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsProperties;

public class SpriteFactory {

	public static final String SPRITE_BASE = Sprite.class.getSimpleName();
	public static final String SPRITE_SINGLE = SingleSprite.class.getSimpleName();
	public static final String SPRITE_GROUP = GroupSprite.class.getSimpleName();
	public static final String SPRITE_GROUP_ITEM = GroupItemSprite.class.getSimpleName();

	public Sprite newInstance(String type) {
		return newInstance(type, null);
	}

	public Sprite newInstance(String type, String name) {
		Sprite sprite = null;

		if (type.equals(SPRITE_SINGLE) || type.equals(SPRITE_BASE)) {
			sprite = new SingleSprite(name);
			Body body = ProjectManager.getInstance().getCurrentScene().getPhysicsWorld().createBody();
			PhysicsProperties physicsProperties = new PhysicsProperties(body, sprite);
			sprite.setPhysicsProperties(physicsProperties);
		} else if (type.equals(SPRITE_GROUP)) {
			sprite = new GroupSprite(name);
		} else if (type.equals(SPRITE_GROUP_ITEM)) {
			sprite = new GroupItemSprite(name);
		}

		return sprite;
	}
}
