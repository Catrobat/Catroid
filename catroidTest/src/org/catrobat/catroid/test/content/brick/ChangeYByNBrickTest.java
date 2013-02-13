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
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;

import android.test.AndroidTestCase;

public class ChangeYByNBrickTest extends AndroidTestCase {

	private int yMovement = 100;

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite x position", 0f, sprite.look.getXPosition());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.look.getYPosition());

		int yPosition = (int) sprite.look.getYPosition();

		ChangeYByNBrick changeYByNBrick = new ChangeYByNBrick(sprite, yMovement);
		changeYByNBrick.execute();

		yPosition += yMovement;
		assertEquals("Incorrect sprite y position after ChangeYByNBrick executed", (float) yPosition,
				sprite.look.getYPosition());
	}

	public void testNullSprite() {
		ChangeYByNBrick brick = new ChangeYByNBrick(null, yMovement);

		try {
			brick.execute();
			fail("Execution of ChangeYByNBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

	public void testBoundaryPositions() {
		Sprite sprite = new Sprite("testSprite");

		int yPosition = 10;
		sprite.look.setXYPosition(sprite.look.getXPosition(), yPosition);
		ChangeYByNBrick changeYByNBrick = new ChangeYByNBrick(sprite, Integer.MAX_VALUE);
		changeYByNBrick.execute();

		assertEquals("ChangeYByNBrick failed to place Sprite at maximum y integer value", Integer.MAX_VALUE,
				(int) sprite.look.getYPosition());

		yPosition = -10;
		sprite.look.setXYPosition(sprite.look.getXPosition(), yPosition);
		changeYByNBrick = new ChangeYByNBrick(sprite, Integer.MIN_VALUE);
		changeYByNBrick.execute();

		assertEquals("ChangeYByNBrick failed to place Sprite at minimum y integer value", Integer.MIN_VALUE,
				(int) sprite.look.getYPosition());

	}
}
