/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.test.StaticSingletonInitializer.initializeStaticSingletonMethods;

@RunWith(JUnit4.class)
public class MoveNStepsActionTest {
	private final float delta = 0.0001f;

	private Sprite sprite;
	private final float steps = 10f;
	private final float diagonalStepLength = 7.07106f;
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private ActionFactory factory;

	@Before
	public void setUp() throws Exception {
		initializeStaticSingletonMethods();
		sprite = new Sprite("Test");
		factory = sprite.getActionFactory();
	}

	@Test
	public void testMoveHorizontalForward() {
		Action moveNStepsAction = factory.createMoveNStepsAction(sprite, new SequenceAction(), new Formula(steps));
		executeTest(moveNStepsAction, steps, 0);
	}

	@Test
	public void testMoveHorizontalBackward() {
		Action moveNStepsAction = factory.createMoveNStepsAction(sprite, new SequenceAction(), new Formula(-steps));
		executeTest(moveNStepsAction, -steps, 0);
	}

	@Test
	public void testMoveVerticalUp() {
		sprite.look.setMotionDirectionInUserInterfaceDimensionUnit(0);
		Action moveNStepsAction = factory.createMoveNStepsAction(sprite, new SequenceAction(), new Formula(steps));

		executeTest(moveNStepsAction, 0, steps);
	}

	@Test
	public void testMoveVerticalDown() {
		sprite.look.setMotionDirectionInUserInterfaceDimensionUnit(180);
		Action moveNStepsAction = factory.createMoveNStepsAction(sprite, new SequenceAction(), new Formula(steps));

		executeTest(moveNStepsAction, 0, -steps);
	}

	@Test
	public void testMoveDiagonalRightUp() {
		sprite.look.setMotionDirectionInUserInterfaceDimensionUnit(45);
		Action moveNStepsAction = factory.createMoveNStepsAction(sprite, new SequenceAction(), new Formula(steps));

		executeTest(moveNStepsAction, diagonalStepLength, diagonalStepLength);
	}

	@Test
	public void testMoveDiagonalLeftUp() {
		sprite.look.setMotionDirectionInUserInterfaceDimensionUnit(-45);
		Action moveNStepsAction = factory.createMoveNStepsAction(sprite, new SequenceAction(), new Formula(steps));

		executeTest(moveNStepsAction, -diagonalStepLength, diagonalStepLength);
	}

	@Test
	public void testMoveDiagonalRightDown() {
		sprite.look.setMotionDirectionInUserInterfaceDimensionUnit(135);
		Action moveNStepsAction = factory.createMoveNStepsAction(sprite, new SequenceAction(), new Formula(steps));

		executeTest(moveNStepsAction, diagonalStepLength, -diagonalStepLength);
	}

	@Test
	public void testMoveDiagonalLeftDown() {
		sprite.look.setMotionDirectionInUserInterfaceDimensionUnit(-135);
		Action moveNStepsAction = factory.createMoveNStepsAction(sprite, new SequenceAction(), new Formula(steps));

		executeTest(moveNStepsAction, -diagonalStepLength, -diagonalStepLength);
	}

	@Test
	public void testMoveOther() {
		sprite.look.setMotionDirectionInUserInterfaceDimensionUnit(100);
		Action action = factory.createMoveNStepsAction(sprite, new SequenceAction(), new Formula(10));

		action.act(1.0f);
		checkPosition(9.848078f, -1.7364818f);

		sprite.look.setMotionDirectionInUserInterfaceDimensionUnit(-30);

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

	@Test
	public void testBrickWithValidStringFormula() {
		Action moveNStepsAction = factory.createMoveNStepsAction(sprite, new SequenceAction(), new Formula(String.valueOf(steps)));
		executeTest(moveNStepsAction, steps, 0);
	}

	@Test
	public void testBrickWithInValidStringFormula() {
		Action moveNStepsAction = factory.createMoveNStepsAction(sprite, new SequenceAction(), new Formula(NOT_NUMERICAL_STRING));
		executeTest(moveNStepsAction, 0f, 0);
	}

	@Test
	public void testNullFormula() {
		Action moveNStepsAction = factory.createMoveNStepsAction(sprite, new SequenceAction(),
				null);
		executeTest(moveNStepsAction, 0f, 0);
	}

	@Test
	public void testNotANumberFormula() {
		Action moveNStepsAction = factory.createMoveNStepsAction(sprite, new SequenceAction(), new Formula(Double.NaN));
		executeTest(moveNStepsAction, 0f, 0);
	}

	private void checkPosition(float expectedX, float expectedY) {
		assertEquals(expectedX, sprite.look.getXInUserInterfaceDimensionUnit(), delta);
		assertEquals(expectedY, sprite.look.getYInUserInterfaceDimensionUnit(), delta);
	}
}
