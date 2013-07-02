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
import org.catrobat.catroid.formulaeditor.Formula;

import android.test.AndroidTestCase;

public class MoveNStepsActionTest extends AndroidTestCase {

	private final float delta = 0.001f;

	public void testMoveHorizontalForward() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, new Formula(10));

		action.act(1.0f);
		assertEquals("Wrong x-position", 10f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong y-position", 0f, sprite.look.getYInUserInterfaceDimensionUnit());

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", 20f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong y-position", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testMoveHorizontalBackward() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, new Formula(-10));

		action.act(1.0f);
		assertEquals("Wrong x-position", -10f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong y-position", 0f, sprite.look.getYInUserInterfaceDimensionUnit());

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", -20f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong y-position", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testMoveVerticalUp() {
		Sprite sprite = new Sprite("test");
		final float stepLength = 10.0f;
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, new Formula(stepLength));

		sprite.look.setRotation(90);

		action.act(1.0f);
		assertEquals("Wrong x-position", 0f, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals("Wrong y-position", 10f, sprite.look.getYInUserInterfaceDimensionUnit(), delta);

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", 0f, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals("Wrong y-position", 20f, sprite.look.getYInUserInterfaceDimensionUnit(), delta);
	}

	public void testMoveVerticalDown() {
		Sprite sprite = new Sprite("test");
		final float stepLength = 10.0f;
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, new Formula(stepLength));
		sprite.look.setRotation(-90);

		action.act(1.0f);
		assertEquals("Wrong x-position", 0f, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals("Wrong y-position", -10f, sprite.look.getYInUserInterfaceDimensionUnit(), delta);

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", 0f, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals("Wrong y-position", -20f, sprite.look.getYInUserInterfaceDimensionUnit(), delta);
	}

	public void testMoveDiagonalRightUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, new Formula(10));
		sprite.look.setRotation(45);

		final float stepLength = 7.07106f;

		action.act(1.0f);
		assertEquals("Wrong x-position", stepLength, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals("Wrong y-position", stepLength, sprite.look.getYInUserInterfaceDimensionUnit(), delta);

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", 2 * stepLength, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals("Wrong y-position", 2 * stepLength, sprite.look.getYInUserInterfaceDimensionUnit(), delta);
	}

	public void testMoveDiagonalLeftUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, new Formula(10));
		sprite.look.setRotation(135);

		final float stepLength = 7.07106f;

		action.act(1.0f);
		assertEquals("Wrong x-position", -stepLength, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals("Wrong y-position", stepLength, sprite.look.getYInUserInterfaceDimensionUnit(), delta);

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", 2 * -stepLength, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals("Wrong y-position", 2 * stepLength, sprite.look.getYInUserInterfaceDimensionUnit(), delta);
	}

	public void testMoveDiagonalRightDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, new Formula(10));
		sprite.look.setRotation(-45);

		final float stepLength = 7.07106f;

		action.act(1.0f);
		assertEquals("Wrong x-position", stepLength, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals("Wrong y-position", -stepLength, sprite.look.getYInUserInterfaceDimensionUnit(), delta);

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", 2 * stepLength, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals("Wrong y-position", 2 * -stepLength, sprite.look.getYInUserInterfaceDimensionUnit(), delta);
	}

	public void testMoveDiagonalLeftDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, new Formula(10));
		sprite.look.setRotationInUserInterfaceDimensionUnit(-135);

		final float stepLength = 7.07106f;

		action.act(1.0f);
		assertEquals("Wrong x-position", -stepLength, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals("Wrong y-position", -stepLength, sprite.look.getYInUserInterfaceDimensionUnit(), delta);

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", 2 * -stepLength, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals("Wrong y-position", 2 * -stepLength, sprite.look.getYInUserInterfaceDimensionUnit(), delta);
	}

	public void testMoveOther() {
		Sprite sprite = new Sprite("test");
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, new Formula(10));
		sprite.look.setRotation(10);

		float stepLengthX = 9.848f;
		float stepLengthY = 1.736f;

		action.act(1.0f);
		assertEquals("Wrong x-position", stepLengthX, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals("Wrong y-position", stepLengthY, sprite.look.getYInUserInterfaceDimensionUnit(), delta);

		sprite.look.setRotation(50);
		stepLengthX = 16.275f;
		stepLengthY = 9.396f;

		action.restart();
		action.act(1.0f);
		assertEquals("Wrong x-position", stepLengthX, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals("Wrong y-position", stepLengthY, sprite.look.getYInUserInterfaceDimensionUnit(), delta);

	}

}
