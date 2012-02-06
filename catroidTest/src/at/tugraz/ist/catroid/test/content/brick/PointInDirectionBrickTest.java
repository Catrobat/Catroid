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
import at.tugraz.ist.catroid.content.bricks.PointInDirectionBrick;
import at.tugraz.ist.catroid.content.bricks.PointInDirectionBrick.Direction;

public class PointInDirectionBrickTest extends AndroidTestCase {

	public void testPointRight() {
		Sprite sprite = new Sprite("test");
		PointInDirectionBrick pointInDirectionBrick = new PointInDirectionBrick(sprite, Direction.DIRECTION_RIGHT);

		pointInDirectionBrick.execute();
		assertEquals("Wrong direction", 0f, sprite.costume.rotation, 1e-3);
	}

	public void testPointLeft() {
		Sprite sprite = new Sprite("test");
		PointInDirectionBrick pointInDirectionBrick = new PointInDirectionBrick(sprite, Direction.DIRECTION_LEFT);

		pointInDirectionBrick.execute();
		assertEquals("Wrong direction", 180f, sprite.costume.rotation, 1e-3);
	}

	public void testPointUp() {
		Sprite sprite = new Sprite("test");
		PointInDirectionBrick pointInDirectionBrick = new PointInDirectionBrick(sprite, Direction.DIRECTION_UP);

		pointInDirectionBrick.execute();
		assertEquals("Wrong direction", 90f, sprite.costume.rotation, 1e-3);
	}

	public void testPointDown() {
		Sprite sprite = new Sprite("test");
		PointInDirectionBrick pointInDirectionBrick = new PointInDirectionBrick(sprite, Direction.DIRECTION_DOWN);

		pointInDirectionBrick.execute();
		assertEquals("Wrong direction", -90f, sprite.costume.rotation, 1e-3);
	}

	public void testRotateAndPoint() {
		Sprite sprite = new Sprite("test");
		sprite.costume.rotation = -42;
		PointInDirectionBrick pointInDirectionBrick = new PointInDirectionBrick(sprite, Direction.DIRECTION_RIGHT);

		pointInDirectionBrick.execute();
		assertEquals("Wrong direction", 0f, sprite.costume.rotation, 1e-3);
	}

}
