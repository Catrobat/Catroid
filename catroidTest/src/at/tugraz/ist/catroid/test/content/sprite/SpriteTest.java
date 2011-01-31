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
package at.tugraz.ist.catroid.test.content.sprite;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class SpriteTest extends AndroidTestCase {
	public void testDefaultConstructor() {
		final String spriteName = "new sprite";
		Sprite sprite = new Sprite(spriteName);
		assertEquals("Unexpected Sprite name", spriteName, sprite.getName());
		assertEquals("Unexpected default x position", 0, sprite.getXPosition());
		assertEquals("Unexpected default y position", 0, sprite.getYPosition());
		assertEquals("Unexpected default z position", 0, sprite.getZPosition());
		assertEquals("Unexpected default scale", 1.0, sprite.getScale());
		assertTrue("Unexpected default visibility", sprite.isVisible());
		assertNull("Unexpected Sprite costume", sprite.getCurrentCostume());
	}
	
	public void testPositionConstructor() {
		final String spriteName = "new sprite";
		final int xPosition = 100;
		final int yPosition = -500;
		Sprite sprite = new Sprite(spriteName, xPosition, yPosition);
		assertEquals("Unexpected Sprite name", spriteName, sprite.getName());
		assertEquals("Unexpected x position", xPosition, sprite.getXPosition());
		assertEquals("Unexpected y position", yPosition, sprite.getYPosition());
		assertEquals("Unexpected default z position", 0, sprite.getZPosition());
		assertEquals("Unexpected default scale", 1.0, sprite.getScale());
		assertTrue("Unexpected default visibility", sprite.isVisible());
		assertNull("Unexpected Sprite costume", sprite.getCurrentCostume());
		
		sprite = new Sprite(spriteName, Integer.MAX_VALUE, Integer.MIN_VALUE);
		assertEquals("Failed to set Sprite X position to maximum Integer value", Integer.MAX_VALUE, sprite.getXPosition());
		assertEquals("Failed to set Sprite Y position to minimum Integer value", Integer.MIN_VALUE, sprite.getYPosition());
	}
	
	public void testSetScale() {
		Sprite sprite = new Sprite("new sprite");
		final double scale = 2.0;
		sprite.setScale(scale);
		assertEquals("Unexpected scale", scale, sprite.getScale());
		
		final double hugeScale = 10.0e100;
		sprite.setScale(hugeScale);
		assertEquals("Failed to scale sprite to a very large size", hugeScale, sprite.getScale());
		
		final double tinyScale = 10.0e-100;
		sprite.setScale(tinyScale);
		assertEquals("Failed to scale sprite to a very small size", tinyScale, sprite.getScale());
	}
	
	public void testSetNegativeScale() {
		try {
			new Sprite("new sprite").setScale(-1.0);
			fail("Setting sprite scale to a negative value succeeded, which it shouldn't!");
		} catch(IllegalArgumentException e) {
			// expected behavior
		}
	}
	
	public void testSetZeroScale() {
		try {
			new Sprite("new sprite").setScale(0.0);
			fail("Setting sprite scale to zero succeeded, which it shouldn't!");
		} catch(IllegalArgumentException e) {
			// expected behavior
		}
	}
	
	public void testShowAndHide() {
		Sprite sprite = new Sprite("new sprite");
		assertTrue("Unexpected default visibility", sprite.isVisible());
		sprite.hide();
		assertFalse("Sprite still visible after calling hide method", sprite.isVisible());
		sprite.show();
		assertTrue("Sprite not visible after calling show method", sprite.isVisible());
	}
	
	// TODO: Costume tests
}
