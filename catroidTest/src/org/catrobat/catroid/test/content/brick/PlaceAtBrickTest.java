/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.brick;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;

import android.test.AndroidTestCase;

public class PlaceAtBrickTest extends AndroidTestCase {

	private int xPosition = 100;
	private int yPosition = 200;

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite x position", 0f, sprite.look.getXPosition());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.look.getYPosition());

		PlaceAtBrick brick = new PlaceAtBrick(sprite, xPosition, yPosition);
		brick.execute();

		assertEquals("Incorrect sprite x position after PlaceAtBrick executed", xPosition,
				(int) sprite.look.getXPosition());
		assertEquals("Incorrect sprite y position after PlaceAtBrick executed", yPosition,
				(int) sprite.look.getYPosition());
	}

	public void testNullSprite() {
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(null, xPosition, yPosition);
		try {
			placeAtBrick.execute();
			fail("Execution of PlaceAtBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException e) {
			// expected behavior
		}
	}

	public void testBoundaryPositions() {
		Sprite sprite = new Sprite("testSprite");

		PlaceAtBrick placeAtBrick = new PlaceAtBrick(sprite, Integer.MAX_VALUE, Integer.MAX_VALUE);
		placeAtBrick.execute();

		assertEquals("PlaceAtBrick failed to place Sprite at maximum x integer value", Integer.MAX_VALUE,
				(int) sprite.look.getXPosition());
		assertEquals("PlaceAtBrick failed to place Sprite at maximum y integer value", Integer.MAX_VALUE,
				(int) sprite.look.getYPosition());

		placeAtBrick = new PlaceAtBrick(sprite, Integer.MIN_VALUE, Integer.MIN_VALUE);
		placeAtBrick.execute();

		assertEquals("PlaceAtBrick failed to place Sprite at minimum x integer value", Integer.MIN_VALUE,
				(int) sprite.look.getXPosition());
		assertEquals("PlaceAtBrick failed to place Sprite at minimum y integer value", Integer.MIN_VALUE,
				(int) sprite.look.getYPosition());
	}
}
