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
import at.tugraz.ist.catroid.content.bricks.SetXBrick;

public class SetXBrickTest extends AndroidTestCase {

	private int xPosition = 100;

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite x position", 0f, sprite.costume.getXPosition());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.costume.getYPosition());

		SetXBrick setXBrick = new SetXBrick(sprite, xPosition);
		setXBrick.execute();

		assertEquals("Incorrect sprite x position after SetXBrick executed", (float) xPosition,
				sprite.costume.getXPosition());
	}

	public void testNullSprite() {
		SetXBrick setXBrick = new SetXBrick(null, xPosition);

		try {
			setXBrick.execute();
			fail("Execution of PlaceAtBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

	public void testBoundaryPositions() {
		Sprite sprite = new Sprite("testSprite");

		SetXBrick setXBrick = new SetXBrick(sprite, Integer.MAX_VALUE);
		setXBrick.execute();

		assertEquals("SetXBrick failed to place Sprite at maximum x integer value", Integer.MAX_VALUE,
				(int) sprite.costume.getXPosition());

		setXBrick = new SetXBrick(sprite, Integer.MIN_VALUE);
		setXBrick.execute();

		assertEquals("SetXBrick failed to place Sprite at minimum x integer value", Integer.MIN_VALUE,
				(int) sprite.costume.getXPosition());
	}
}
