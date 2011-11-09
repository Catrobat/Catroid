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
import at.tugraz.ist.catroid.content.bricks.ShowBrick;

public class ShowBrickTest extends AndroidTestCase {
	public void testShow() {
		Sprite sprite = new Sprite("new sprite");
		sprite.hide();
		assertFalse("Sprite is still visible after calling hide", sprite.isVisible());

		ShowBrick showBrick = new ShowBrick(sprite);
		showBrick.execute();
		assertTrue("Sprite is not visible after ShowBrick executed", sprite.isVisible());
	}

	public void testNullSprite() {
		ShowBrick showBrick = new ShowBrick(null);
		try {
			showBrick.execute();
			fail("Execution of ShowBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException e) {
			// expected behavior
		}
	}
}
