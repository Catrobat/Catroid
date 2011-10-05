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
import at.tugraz.ist.catroid.content.bricks.MoveNStepsBrick;

public class MoveNStepsBrickTest extends AndroidTestCase {

	public void testMoveHorizontalForward() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 10, sprite.getXPosition());
		assertEquals("Wrong y-position", 0, sprite.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 20, sprite.getXPosition());
		assertEquals("Wrong y-position", 0, sprite.getYPosition());
	}

	public void testMoveHorizontalBackward() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, -10);

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", -10, sprite.getXPosition());
		assertEquals("Wrong y-position", 0, sprite.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", -20, sprite.getXPosition());
		assertEquals("Wrong y-position", 0, sprite.getYPosition());
	}

	public void testMoveVerticalUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		sprite.setDirection(0);

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 0, sprite.getXPosition());
		assertEquals("Wrong y-position", 10, sprite.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 0, sprite.getXPosition());
		assertEquals("Wrong y-position", 20, sprite.getYPosition());
	}

	public void testMoveVerticalDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		sprite.setDirection(180);

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 0, sprite.getXPosition());
		assertEquals("Wrong y-position", -10, sprite.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 0, sprite.getXPosition());
		assertEquals("Wrong y-position", -20, sprite.getYPosition());
	}

	public void testMoveDiagonalRightUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		sprite.setDirection(45);

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 7, sprite.getXPosition());
		assertEquals("Wrong y-position", 7, sprite.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 14, sprite.getXPosition());
		assertEquals("Wrong y-position", 14, sprite.getYPosition());
	}

	public void testMoveDiagonalLeftUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		sprite.setDirection(-45);

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", -7, sprite.getXPosition());
		assertEquals("Wrong y-position", 7, sprite.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", -14, sprite.getXPosition());
		assertEquals("Wrong y-position", 14, sprite.getYPosition());
	}

	public void testMoveDiagonalRightDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		sprite.setDirection(135);

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 7, sprite.getXPosition());
		assertEquals("Wrong y-position", -7, sprite.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 14, sprite.getXPosition());
		assertEquals("Wrong y-position", -14, sprite.getYPosition());
	}

	public void testMoveDiagonalLeftDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		sprite.setDirection(-135);

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", -7, sprite.getXPosition());
		assertEquals("Wrong y-position", -7, sprite.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", -14, sprite.getXPosition());
		assertEquals("Wrong y-position", -14, sprite.getYPosition());
	}

	public void testMoveOther() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		sprite.setDirection(80);

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 10, sprite.getXPosition());
		assertEquals("Wrong y-position", 2, sprite.getYPosition());

		sprite.setDirection(40);
		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 16, sprite.getXPosition());
		assertEquals("Wrong y-position", 10, sprite.getYPosition());

	}

}
