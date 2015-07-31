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

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.formulaeditor.Formula;

public class PointInDirectionActionTest extends AndroidTestCase {

	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		super.setUp();
	}

	public void testPointRight() {
		ExtendedActions.pointInDirection(sprite, new Formula(Direction.RIGHT.getDegrees())).act(1.0f);
		assertEquals("Wrong direction", 90f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
	}

	public void testPointLeft() {
		ExtendedActions.pointInDirection(sprite, new Formula(Direction.LEFT.getDegrees())).act(1.0f);
		assertEquals("Wrong direction", -90f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
	}

	public void testPointUp() {
		ExtendedActions.pointInDirection(sprite, new Formula(Direction.UP.getDegrees())).act(1.0f);
		assertEquals("Wrong direction", 0f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
	}

	public void testPointDown() {
		ExtendedActions.pointInDirection(sprite, new Formula(Direction.DOWN.getDegrees())).act(1.0f);
		assertEquals("Wrong direction", 180f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
	}

	public void testRotateAndPoint() {
		Sprite sprite = new Sprite("test");
		sprite.look.setRotation(-42);
		ExtendedActions.pointInDirection(sprite, new Formula(Direction.RIGHT.getDegrees())).act(1.0f);
		assertEquals("Wrong direction", 90f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
	}

	public void testBrickWithStringFormula() {
		ExtendedActions.pointInDirection(sprite, new Formula(String.valueOf(Direction.RIGHT.getDegrees()))).act(1.0f);
		assertEquals("Wrong direction", (float) Direction.RIGHT.getDegrees(),
				sprite.look.getDirectionInUserInterfaceDimensionUnit());

		ExtendedActions.pointInDirection(sprite, new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals("Wrong direction", (float) Direction.RIGHT.getDegrees(),
				sprite.look.getDirectionInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		ExtendedActions.pointInDirection(sprite, null).act(1.0f);
		assertEquals("Wrong direction", 0f, sprite.look.getDirectionInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		ExtendedActions.pointInDirection(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Wrong direction", 90f, sprite.look.getDirectionInUserInterfaceDimensionUnit());
	}
}
