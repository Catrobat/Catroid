/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.actions;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.MoveNStepsAction;

import android.test.AndroidTestCase;

public class MoveNStepsActionTest extends AndroidTestCase {

	public void testMoveHorizontalForward() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, 10);

		action.act(1.0f);
		assertEquals("Wrong x-position", 10f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", 0f, sprite.look.getYPosition());

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", 20f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", 0f, sprite.look.getYPosition());
	}

	public void testMoveHorizontalBackward() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, -10);

		action.act(1.0f);
		assertEquals("Wrong x-position", -10f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", 0f, sprite.look.getYPosition());

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", -20f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", 0f, sprite.look.getYPosition());
	}

	public void testMoveVerticalUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, 10);

		sprite.look.setRotation(90);

		action.act(1.0f);
		assertEquals("Wrong x-position", 0f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", 10f, sprite.look.getYPosition());

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", 0f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", 20f, sprite.look.getYPosition());
	}

	public void testMoveVerticalDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, 10);

		sprite.look.setRotation(-90);

		action.act(1.0f);
		assertEquals("Wrong x-position", 0f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", -10f, sprite.look.getYPosition());

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", 0f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", -20f, sprite.look.getYPosition());
	}

	public void testMoveDiagonalRightUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, 10);

		sprite.look.setRotation(45);

		action.act(1.0f);
		assertEquals("Wrong x-position", 7f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", 7f, sprite.look.getYPosition());

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", 14f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", 14f, sprite.look.getYPosition());
	}

	public void testMoveDiagonalLeftUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, 10);

		sprite.look.setRotation(135);

		action.act(1.0f);
		assertEquals("Wrong x-position", -7f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", 7f, sprite.look.getYPosition());

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", -14f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", 14f, sprite.look.getYPosition());
	}

	public void testMoveDiagonalRightDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, 10);

		sprite.look.setRotation(-45);

		action.act(1.0f);
		assertEquals("Wrong x-position", 7f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", -7f, sprite.look.getYPosition());

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", 14f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", -14f, sprite.look.getYPosition());
	}

	public void testMoveDiagonalLeftDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, 10);

		sprite.look.setRotation(-135);

		action.act(1.0f);
		assertEquals("Wrong x-position", -7f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", -7f, sprite.look.getYPosition());

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", -14f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", -14f, sprite.look.getYPosition());
	}

	public void testMoveOther() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, 10);

		sprite.look.setRotation(10);

		action.act(1.0f);
		assertEquals("Wrong x-position", 10f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", 2f, sprite.look.getYPosition());

		sprite.look.setRotation(50);

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", 16f, sprite.look.getXPosition());
		assertEquals("Wrong y-position", 10f, sprite.look.getYPosition());

	}

}
