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
package org.catrobat.catroid.test.content.sprite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Group;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LookTest {

	private Sprite sprite;

	@Before
	public void setUp() {
		Group parentGroup = new Group();
		sprite = new Sprite("test");
		parentGroup.addActor(sprite.look);
	}

	@Test
	public void testTouchDownFlipped() {
		final int width = 1;
		final int height = 1;

		Look look = new Look(sprite) {
			{
				pixmap = TestUtils.createRectanglePixmap(width, height, Color.RED);
			}
		};
		look.setSize(width, height);

		assertTrue(look.doTouchDown(0, 0, 0));
	}

	@Test
	public void testTouchDownFlippedWithAlpha() {
		final int width = 2;
		final int height = 1;

		Look look = new Look(sprite) {
			{
				pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
				pixmap.drawPixel(0, 0, Color.RED.toIntBits());
			}
		};
		look.setSize(width, height);

		assertTrue(look.doTouchDown(0, 0, 0));
		assertFalse(look.doTouchDown(1, 0, 0));
	}
}
