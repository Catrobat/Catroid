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
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;

import android.test.AndroidTestCase;

public class ChangeXByNBrickTest extends AndroidTestCase {

	private int xMovement = 100;

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite x position", 0f, sprite.costume.getXPosition());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.costume.getYPosition());

		int xPosition = (int) sprite.costume.getXPosition();

		ChangeXByNBrick changeXByNBrick = new ChangeXByNBrick(sprite, xMovement);
		changeXByNBrick.execute();

		xPosition += xMovement;
		assertEquals("Incorrect sprite x position after ChangeXByNBrick executed", (float) xPosition,
				sprite.costume.getXPosition());
	}

	public void testNullSprite() {
		ChangeXByNBrick changeXByNBrick = new ChangeXByNBrick(null, xMovement);
		try {
			changeXByNBrick.execute();
			fail("Execution of ChangeXByNBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

	public void testBoundaryPositions() {
		Sprite sprite = new Sprite("testSprite");

		int xPosition = 10;
		sprite.costume.setXYPosition(xPosition, sprite.costume.getYPosition());
		ChangeXByNBrick changeXByNBrick = new ChangeXByNBrick(sprite, Integer.MAX_VALUE);
		changeXByNBrick.execute();

		assertEquals("ChangeXByNBrick failed to place Sprite at maximum x integer value", Integer.MAX_VALUE,
				(int) sprite.costume.getXPosition());

		xPosition = -10;
		sprite.costume.setXYPosition(xPosition, sprite.costume.getYPosition());
		changeXByNBrick = new ChangeXByNBrick(sprite, Integer.MIN_VALUE);
		changeXByNBrick.execute();

		assertEquals("ChangeXByNBrick failed to place Sprite at minimum x integer value", Integer.MIN_VALUE,
				(int) sprite.costume.getXPosition());

	}
}
