/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.ChangeXByBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeYByBrick;

public class ChangeYByBrickTest extends AndroidTestCase {

	private int yMovement = 100;

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite x position", 0, sprite.getXPosition());
		assertEquals("Unexpected initial sprite y position", 0, sprite.getYPosition());

		int yPosition = sprite.getYPosition();

		ChangeYByBrick brick = new ChangeYByBrick(sprite, yMovement);
		brick.execute();

		yPosition += yMovement;
		assertEquals("Incorrect sprite y position after ChangeYByBrick executed", yPosition, sprite.getYPosition());
	}

	public void testNullSprite() {
		ChangeXByBrick brick = new ChangeXByBrick(null, yMovement);

		try {
			brick.execute();
			fail("Execution of ChangeYByBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException e) {
			// expected behavior
		}
	}

	public void testBoundaryPositions() {
		Sprite sprite = new Sprite("testSprite");

		int yPosition = 10;
		sprite.setXYPosition(sprite.getXPosition(), yPosition);
		ChangeYByBrick brick = new ChangeYByBrick(sprite, Integer.MAX_VALUE);
		brick.execute();

		assertEquals("ChangeYByBrick failed to place Sprite at maximum y integer value", Integer.MAX_VALUE,
				sprite.getYPosition());

		yPosition = -10;
		sprite.setXYPosition(sprite.getXPosition(), yPosition);
		brick = new ChangeYByBrick(sprite, Integer.MIN_VALUE);
		brick.execute();

		assertEquals("ChangeYByBrick failed to place Sprite at minimum y integer value", Integer.MIN_VALUE,
				sprite.getYPosition());

	}
}
