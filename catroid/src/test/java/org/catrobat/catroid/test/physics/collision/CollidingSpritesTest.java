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

package org.catrobat.catroid.test.physics.collision;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.CollidingSprites;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(JUnit4.class)
public class CollidingSpritesTest {
	final Sprite sprite1 = new Sprite("sprite1");
	final Sprite sprite2 = new Sprite("sprite2");

	@Test
	public void equalsWorksWithSameSpriteOrder() {
		CollidingSprites sprites1 = new CollidingSprites(sprite1, sprite2);
		CollidingSprites sprites2 = new CollidingSprites(sprite1, sprite2);

		assertEquals(sprites1, sprites2);
	}

	@Test
	public void equalsDoesNotDependOnSpriteOrder() {
		CollidingSprites sprites1 = new CollidingSprites(sprite1, sprite2);
		CollidingSprites sprites2 = new CollidingSprites(sprite2, sprite1);

		assertEquals(sprites1, sprites2);
	}

	@Test
	public void equalsFailsForUnequalCollidingSprites() {
		Sprite sprite3 = new Sprite("sprite3");

		CollidingSprites sprites1 = new CollidingSprites(sprite1, sprite2);
		CollidingSprites sprites2 = new CollidingSprites(sprite3, sprite2);
		CollidingSprites sprites3 = new CollidingSprites(sprite1, null);

		assertNotEquals(sprites1, sprites2);
		assertNotEquals(sprites1, sprites3);
	}

	@Test
	public void hashcodeWorksWithSameSpriteOrder() {
		CollidingSprites sprites1 = new CollidingSprites(sprite1, sprite2);
		CollidingSprites sprites2 = new CollidingSprites(sprite2, sprite1);

		assertEquals(sprites1.hashCode(), sprites2.hashCode());
	}

	@Test
	public void hashcodeDoesNotDependOnSpriteOrder() {
		CollidingSprites sprites1 = new CollidingSprites(sprite1, sprite2);
		CollidingSprites sprites2 = new CollidingSprites(sprite2, sprite1);

		assertEquals(sprites1.hashCode(), sprites2.hashCode());
	}
}
