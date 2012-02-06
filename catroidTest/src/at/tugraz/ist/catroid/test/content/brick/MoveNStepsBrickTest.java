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
import at.tugraz.ist.catroid.content.bricks.MoveNStepsBrick;

public class MoveNStepsBrickTest extends AndroidTestCase {

	public void testMoveHorizontalForward() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 10f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 0f, sprite.costume.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 20f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 0f, sprite.costume.getYPosition());
	}

	public void testMoveHorizontalBackward() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, -10);

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", -10f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 0f, sprite.costume.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", -20f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 0f, sprite.costume.getYPosition());
	}

	public void testMoveVerticalUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		sprite.costume.rotation = 90;

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 10f, sprite.costume.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 20f, sprite.costume.getYPosition());
	}

	public void testMoveVerticalDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		sprite.costume.rotation = -90;

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", -10f, sprite.costume.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", -20f, sprite.costume.getYPosition());
	}

	public void testMoveDiagonalRightUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		sprite.costume.rotation = 45;

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 7f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 7f, sprite.costume.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 14f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 14f, sprite.costume.getYPosition());
	}

	public void testMoveDiagonalLeftUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		sprite.costume.rotation = 135;

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", -7f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 7f, sprite.costume.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", -14f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 14f, sprite.costume.getYPosition());
	}

	public void testMoveDiagonalRightDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		sprite.costume.rotation = -45;

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 7f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", -7f, sprite.costume.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 14f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", -14f, sprite.costume.getYPosition());
	}

	public void testMoveDiagonalLeftDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		sprite.costume.rotation = -135;

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", -7f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", -7f, sprite.costume.getYPosition());

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", -14f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", -14f, sprite.costume.getYPosition());
	}

	public void testMoveOther() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick moveNStepsBrick = new MoveNStepsBrick(sprite, 10);

		sprite.costume.rotation = 10;

		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 10f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 2f, sprite.costume.getYPosition());

		sprite.costume.rotation = 50;
		moveNStepsBrick.execute();
		assertEquals("Wrong x-position", 16f, sprite.costume.getXPosition());
		assertEquals("Wrong y-position", 10f, sprite.costume.getYPosition());

	}

}
