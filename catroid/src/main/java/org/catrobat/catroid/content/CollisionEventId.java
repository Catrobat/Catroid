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

import com.google.common.base.Objects;

public class CollisionEventId extends EventId {
	public final Sprite sprite1;
	public final Sprite sprite2;

	public CollisionEventId(Sprite sprite1, Sprite sprite2) {
		this.sprite1 = sprite1;
		this.sprite2 = sprite2;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CollisionEventId) {
			CollisionEventId collisionEventId = (CollisionEventId) o;
			return (Objects.equal(sprite1, collisionEventId.sprite1) || Objects.equal(sprite1, collisionEventId.sprite2))
					&& (Objects.equal(sprite2, collisionEventId.sprite1) || Objects.equal(sprite2, collisionEventId.sprite2));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (sprite1 == null ? 0 : sprite1.hashCode()) + (sprite2 == null ? 0 : sprite2.hashCode());
	}
}
