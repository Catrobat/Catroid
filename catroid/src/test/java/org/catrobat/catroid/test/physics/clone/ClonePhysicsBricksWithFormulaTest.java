/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.test.physics.clone;

import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.physics.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physics.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.physics.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physics.content.bricks.SetMassBrick;
import org.catrobat.catroid.physics.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.physics.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.physics.content.bricks.TurnRightSpeedBrick;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static junit.framework.Assert.assertNotSame;

import static org.junit.Assert.assertSame;

@RunWith(Parameterized.class)
public class ClonePhysicsBricksWithFormulaTest {

	private static final int BRICK_FORMULA_VALUE = 0;
	private static final String BRICK_INVALID_FORMULA_VALUE = "1";
	private static final String CLONE_BRICK_FORMULA_VALUE = "2";

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"SetBounceBrick", new SetBounceBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.PHYSICS_BOUNCE_FACTOR},
				{"SetFrictionBrick", new SetFrictionBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.PHYSICS_FRICTION},
				{"SetGravityBrickX", new SetGravityBrick(new Formula(BRICK_FORMULA_VALUE), new Formula(BRICK_INVALID_FORMULA_VALUE)), Brick.BrickField.PHYSICS_GRAVITY_X},
				{"SetGravityBrickY", new SetGravityBrick(new Formula(BRICK_INVALID_FORMULA_VALUE), new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.PHYSICS_GRAVITY_Y},
				{"SetMassBrick", new SetMassBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.PHYSICS_MASS},
				{"SetVelocityBrickX", new SetVelocityBrick(new Formula(BRICK_FORMULA_VALUE), new Formula(BRICK_INVALID_FORMULA_VALUE)), Brick.BrickField.PHYSICS_VELOCITY_X},
				{"SetVelocityBrickY", new SetVelocityBrick(new Formula(BRICK_INVALID_FORMULA_VALUE), new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.PHYSICS_VELOCITY_Y},
				{"TurnRightSpeedBrick", new TurnRightSpeedBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.PHYSICS_TURN_RIGHT_SPEED},
				{"TurnLeftSpeedBrick", new TurnLeftSpeedBrick(new Formula(BRICK_FORMULA_VALUE)), Brick.BrickField.PHYSICS_TURN_LEFT_SPEED},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public FormulaBrick brick;

	@Parameterized.Parameter(2)
	public Brick.BrickField brickField;

	private Sprite sprite = new SingleSprite("testSprite");
	private Formula brickFormula;
	private Formula cloneBrickFormula;

	@Before
	public void setUp() throws CloneNotSupportedException {
		FormulaBrick cloneBrick = (FormulaBrick) brick.clone();
		brickFormula = brick.getFormulaWithBrickField(brickField);
		cloneBrickFormula = cloneBrick.getFormulaWithBrickField(brickField);
	}

	@Test
	public void testChangeBrickField() throws InterpretationException {
		cloneBrickFormula.setRoot(new FormulaElement(FormulaElement.ElementType.NUMBER, CLONE_BRICK_FORMULA_VALUE, null));
		assertNotSame(brickFormula.interpretInteger(sprite), cloneBrickFormula.interpretInteger(sprite));
	}

	@Test
	public void testBrickFieldValidValue() throws InterpretationException {
		assertSame(BRICK_FORMULA_VALUE, brickFormula.interpretInteger(sprite));
	}

	@Test
	public void testBrickFieldEquals() throws InterpretationException {
		assertSame(brickFormula.interpretInteger(sprite), cloneBrickFormula.interpretInteger(sprite));
	}
}
