/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.ChangeYByBrick;

public class ChangeYByBrickTest extends AndroidTestCase {

	private int yMovement = 100;

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite x position", 0f, sprite.costume.getXPosition());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.costume.getYPosition());

		int yPosition = (int) sprite.costume.getYPosition();

		ChangeYByBrick changeYByBrick = new ChangeYByBrick(sprite, yMovement);
		changeYByBrick.execute();

		yPosition += yMovement;
		assertEquals("Incorrect sprite y position after ChangeYByBrick executed", (float) yPosition,
				sprite.costume.getYPosition());
	}

	public void testNullSprite() {
		ChangeYByBrick brick = new ChangeYByBrick(null, yMovement);

		try {
			brick.execute();
			fail("Execution of ChangeYByBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

	public void testBoundaryPositions() {
		Sprite sprite = new Sprite("testSprite");

		int yPosition = 10;
		sprite.costume.setXYPosition(sprite.costume.getXPosition(), yPosition);
		ChangeYByBrick changeYByBrick = new ChangeYByBrick(sprite, Integer.MAX_VALUE);
		changeYByBrick.execute();

		assertEquals("ChangeYByBrick failed to place Sprite at maximum y integer value", Integer.MAX_VALUE,
				(int) sprite.costume.getYPosition());

		yPosition = -10;
		sprite.costume.setXYPosition(sprite.costume.getXPosition(), yPosition);
		changeYByBrick = new ChangeYByBrick(sprite, Integer.MIN_VALUE);
		changeYByBrick.execute();

		assertEquals("ChangeYByBrick failed to place Sprite at minimum y integer value", Integer.MIN_VALUE,
				(int) sprite.costume.getYPosition());

	}
}
