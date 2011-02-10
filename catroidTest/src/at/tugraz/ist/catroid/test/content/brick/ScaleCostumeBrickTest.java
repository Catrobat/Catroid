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
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrickBase;
import at.tugraz.ist.catroid.content.brick.gui.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class ScaleCostumeBrickTest extends AndroidTestCase {
	private double scale = 2.7;

	public void testScale() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite scale value", 1.0,
				sprite.getScale());

		ScaleCostumeBrick brick = new ScaleCostumeBrick(sprite, scale);
		brick.execute();
		assertEquals(
				"Incorrect sprite scale value after ScaleCostumeBrick executed",
				scale, sprite.getScale());
	}

	public void testNullSprite() {
		ScaleCostumeBrick brick = new ScaleCostumeBrick(null, scale);
		
		try {
			brick.execute();
			fail("Execution of ScaleCostumeBrick with null Sprite did not cause " +
					"a NullPointerException to be thrown");
		} catch (NullPointerException e) {
			// expected behavior
		}
	}

	public void testBoundaryScale() {
		Sprite sprite = new Sprite("testSprite");

		ScaleCostumeBrick brick = new ScaleCostumeBrick(sprite,
				Double.MAX_VALUE);
		brick.execute();
		assertEquals("ScaleCostumeBrick failed to scale Sprite to maximum double value",
				Double.MAX_VALUE, sprite.getScale());

		brick = new ScaleCostumeBrick(sprite, Double.MIN_VALUE);
		brick.execute();
		assertEquals("ScaleCostumeBrick failed to scale Sprite to minimum double value",
				Double.MIN_VALUE, sprite.getScale());
	}

	public void testZeroScale() {
		Sprite sprite = new Sprite("testSprite");

		ScaleCostumeBrick brick = new ScaleCostumeBrick(sprite, 0.0);

		try {
			brick.execute();
			fail("Execution of ScaleCostumeBrick with 0.0 scale did not cause a " +
					"IllegalArgumentException to be thrown.");
		} catch (IllegalArgumentException e) {
			// expected behavior
		}
	}

	public void testNegativeScale() {
		Sprite sprite = new Sprite("testSprite");

		ScaleCostumeBrick brick = new ScaleCostumeBrick(sprite, -scale);

		try {
			brick.execute();
			fail("Execution of ScaleCostumeBrick with negative scale did not cause" +
					" a IllegalArgumentException to be thrown.");
		} catch (IllegalArgumentException e) {
			// expected behavior
		}
	}

}
