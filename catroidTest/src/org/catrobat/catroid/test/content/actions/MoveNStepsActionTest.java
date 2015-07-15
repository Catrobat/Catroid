/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.actions;

import android.test.AndroidTestCase;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.MoveNStepsAction;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.formulaeditor.Formula;

public class MoveNStepsActionTest extends AndroidTestCase {
	private final float delta = 0.0001f;

	private Sprite sprite;
	private final float steps = 10f;
	private final float diagonalStepLength = 7.07106f;
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("Test");
	}

	public void testMoveHorizontalForward() {
		Action moveNStepsAction = ExtendedActions.moveNSteps(sprite, new Formula(steps));
		executeTest(moveNStepsAction, steps, 0);
	}

	public void testMoveHorizontalBackward() {
		Action moveNStepsAction = ExtendedActions.moveNSteps(sprite, new Formula(-steps));
		executeTest(moveNStepsAction, -steps, 0);
	}

	public void testMoveVerticalUp() {
		sprite.look.setDirectionInUserInterfaceDimensionUnit((float) Direction.UP.getDegrees());
		Action moveNStepsAction = ExtendedActions.moveNSteps(sprite, new Formula(steps));

		executeTest(moveNStepsAction, 0, steps);
	}

	public void testMoveVerticalDown() {
		sprite.look.setDirectionInUserInterfaceDimensionUnit((float) Direction.DOWN.getDegrees());
		Action moveNStepsAction = ExtendedActions.moveNSteps(sprite, new Formula(steps));

		executeTest(moveNStepsAction, 0, -steps);
	}

	public void testMoveDiagonalRightUp() {
		sprite.look.setDirectionInUserInterfaceDimensionUnit(45);
		MoveNStepsAction moveNStepsAction = ExtendedActions.moveNSteps(sprite, new Formula(steps));

		executeTest(moveNStepsAction, diagonalStepLength, diagonalStepLength);
	}

	public void testMoveDiagonalLeftUp() {
		sprite.look.setDirectionInUserInterfaceDimensionUnit(-45);
		MoveNStepsAction moveNStepsAction = ExtendedActions.moveNSteps(sprite, new Formula(steps));

		executeTest(moveNStepsAction, -diagonalStepLength, diagonalStepLength);
	}

	public void testMoveDiagonalRightDown() {
		sprite.look.setDirectionInUserInterfaceDimensionUnit(135);
		MoveNStepsAction moveNStepsAction = ExtendedActions.moveNSteps(sprite, new Formula(steps));

		executeTest(moveNStepsAction, diagonalStepLength, -diagonalStepLength);
	}

	public void testMoveDiagonalLeftDown() {
		sprite.look.setDirectionInUserInterfaceDimensionUnit(-135);
		MoveNStepsAction moveNStepsAction = ExtendedActions.moveNSteps(sprite, new Formula(steps));

		executeTest(moveNStepsAction, -diagonalStepLength, -diagonalStepLength);
	}

	public void testMoveOther() {
		sprite.look.setDirectionInUserInterfaceDimensionUnit(100);
		MoveNStepsAction action = ExtendedActions.moveNSteps(sprite, new Formula(10));

		action.act(1.0f);
		checkPosition(9.848078f, -1.7364818f);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(-30);

		action.restart();
		action.act(1.0f);
		checkPosition(4.848078f, 6.923773f);
	}

	private void executeTest(Action moveNStepsAction, float expectedX, float expectedY) {
		moveNStepsAction.act(1.0f);
		checkPosition(expectedX, expectedY);

		moveNStepsAction.restart();
		moveNStepsAction.act(1.0f);
		checkPosition(2 * expectedX, 2 * expectedY);
	}

	public void testBrickWithValidStringFormula() {
		Action moveNStepsAction = ExtendedActions.moveNSteps(sprite, new Formula(String.valueOf(steps)));
		executeTest(moveNStepsAction, steps, 0);
	}

	public void testBrickWithInValidStringFormula() {
		Action moveNStepsAction = ExtendedActions.moveNSteps(sprite, new Formula(NOT_NUMERICAL_STRING));
		executeTest(moveNStepsAction, 0f, 0);
	}

	public void testNullFormula() {
		Action moveNStepsAction = ExtendedActions.moveNSteps(sprite, null);
		executeTest(moveNStepsAction, 0f, 0);
	}

	public void testNotANumberFormula() {
		Action moveNStepsAction = ExtendedActions.moveNSteps(sprite, new Formula(Double.NaN));
		executeTest(moveNStepsAction, 0f, 0);
	}

	private void checkPosition(float expectedX, float expectedY) {
		assertEquals("Wrong x-position", expectedX, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals("Wrong y-position", expectedY, sprite.look.getYInUserInterfaceDimensionUnit(), delta);
	}
}
