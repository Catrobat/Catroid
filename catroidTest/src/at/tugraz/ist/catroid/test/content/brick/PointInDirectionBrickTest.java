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
import at.tugraz.ist.catroid.content.bricks.PointInDirectionBrick;

public class PointInDirectionBrickTest extends AndroidTestCase {

	public void testPointRight() {
		Sprite sprite = new Sprite("test");
		PointInDirectionBrick brick = new PointInDirectionBrick(sprite, 90);

		brick.execute();
		assertEquals("Wrong direction", 90, sprite.getDirection(), 1e-3);
	}

	public void testPointLeft() {
		Sprite sprite = new Sprite("test");
		PointInDirectionBrick brick = new PointInDirectionBrick(sprite, -90);

		brick.execute();
		assertEquals("Wrong direction", -90, sprite.getDirection(), 1e-3);
	}

	public void testPointUp() {
		Sprite sprite = new Sprite("test");
		PointInDirectionBrick brick = new PointInDirectionBrick(sprite, 0);

		brick.execute();
		assertEquals("Wrong direction", 0, sprite.getDirection(), 1e-3);
	}

	public void testPointDown() {
		Sprite sprite = new Sprite("test");
		PointInDirectionBrick brick = new PointInDirectionBrick(sprite, 180);

		brick.execute();
		assertEquals("Wrong direction", 180, sprite.getDirection(), 1e-3);
	}

	public void testRotateAndPoint() {
		Sprite sprite = new Sprite("test");
		sprite.setDirection(-42);
		PointInDirectionBrick brick = new PointInDirectionBrick(sprite, 90);

		brick.execute();
		assertEquals("Wrong direction", 90, sprite.getDirection(), 1e-3);
	}

}
