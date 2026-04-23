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

package org.catrobat.catroid.physics;

import org.catrobat.catroid.content.Sprite;

import java.util.Objects;

public class CollidingSprites {
	public final Sprite sprite1;
	public final Sprite sprite2;

	public CollidingSprites(Sprite sprite1, Sprite sprite2) {
		this.sprite1 = sprite1;
		this.sprite2 = sprite2;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CollidingSprites)) {
			return false;
		}
		CollidingSprites that = (CollidingSprites) o;
		return (Objects.equals(sprite1, that.sprite1) && Objects.equals(sprite2, that.sprite2))
				|| (Objects.equals(sprite2, that.sprite1) && Objects.equals(sprite1, that.sprite2));
	}

	@Override
	public int hashCode() {
		return Objects.hash(sprite1) + Objects.hash(sprite2);
	}
}
