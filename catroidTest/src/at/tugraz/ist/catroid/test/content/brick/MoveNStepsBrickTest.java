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
import at.tugraz.ist.catroid.content.bricks.MoveNStepsBrick;

public class MoveNStepsBrickTest extends AndroidTestCase {

	public void testMoveHorizontalForward() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		brick.execute();
		assertEquals("Wrong x-position", 10, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 0, sprite.costume.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", 20, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 0, sprite.costume.getYPosition());
	}

	public void testMoveHorizontalBackward() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, -10);

		brick.execute();
		assertEquals("Wrong x-position", -10, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 0, sprite.costume.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", -20, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 0, sprite.costume.getYPosition());
	}

	public void testMoveVerticalUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		sprite.costume.rotation = 0;

		brick.execute();
		assertEquals("Wrong x-position", 0, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 10, sprite.costume.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", 0, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 20, sprite.costume.getYPosition());
	}

	public void testMoveVerticalDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		sprite.costume.rotation = 180;

		brick.execute();
		assertEquals("Wrong x-position", 0, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", -10, sprite.costume.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", 0, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", -20, sprite.costume.getYPosition());
	}

	public void testMoveDiagonalRightUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		sprite.costume.rotation = 45;

		brick.execute();
		assertEquals("Wrong x-position", 7, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 7, sprite.costume.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", 14, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 14, sprite.costume.getYPosition());
	}

	public void testMoveDiagonalLeftUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		sprite.costume.rotation = -45;

		brick.execute();
		assertEquals("Wrong x-position", -7, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 7, sprite.costume.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", -14, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 14, sprite.costume.getYPosition());
	}

	public void testMoveDiagonalRightDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		sprite.costume.rotation = 135;

		brick.execute();
		assertEquals("Wrong x-position", 7, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", -7, sprite.costume.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", 14, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", -14, sprite.costume.getYPosition());
	}

	public void testMoveDiagonalLeftDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		sprite.costume.rotation = -135;

		brick.execute();
		assertEquals("Wrong x-position", -7, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", -7, sprite.costume.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", -14, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", -14, sprite.costume.getYPosition());
	}

	public void testMoveOther() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		sprite.costume.rotation = 80;

		brick.execute();
		assertEquals("Wrong x-position", 10, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 2, sprite.costume.getYPosition());

		sprite.costume.rotation = 40;
		brick.execute();
		assertEquals("Wrong x-position", 16, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 10, sprite.costume.getYPosition());

	}

}
