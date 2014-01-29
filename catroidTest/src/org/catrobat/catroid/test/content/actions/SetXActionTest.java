/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import org.catrobat.catroid.content.actions.SetXAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class SetXActionTest extends AndroidTestCase {

	private Formula xPosition = new Formula(100);
	private static final float VALUE = 91f;
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite x position", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.look.getYInUserInterfaceDimensionUnit());

		SetXAction action = ExtendedActions.setX(sprite, xPosition);
		action.act(1.0f);

		assertEquals("Incorrect sprite x position after SetXBrick executed", xPosition.interpretFloat(sprite),
				sprite.look.getXInUserInterfaceDimensionUnit());
	}

	public void testNullSprite() {
		SetXAction action = ExtendedActions.setX(null, xPosition);
		try {
			action.act(1.0f);
			fail("Execution of PlaceAtBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			assertTrue("Exception thrown as expected", true);
		}
	}

	public void testBoundaryPositions() {
		Sprite sprite = new Sprite("testSprite");

		SetXAction action = ExtendedActions.setX(sprite, new Formula(Integer.MAX_VALUE));
		action.act(1.0f);

		assertEquals("SetXBrick failed to place Sprite at maximum x integer value", Integer.MAX_VALUE,
				(int) sprite.look.getXInUserInterfaceDimensionUnit());

		action = ExtendedActions.setX(sprite, new Formula(Integer.MIN_VALUE));
		action.act(1.0f);

		assertEquals("SetXBrick failed to place Sprite at minimum x integer value", Integer.MIN_VALUE,
				(int) sprite.look.getXInUserInterfaceDimensionUnit());
	}

	public void testBrickWithStringFormula() {
		Sprite sprite = new Sprite("testSprite");
		SetXAction action = ExtendedActions.setX(sprite, new Formula(String.valueOf(VALUE)));
		action.act(1.0f);
		assertEquals("Incorrect sprite x position after SetXBrick executed", VALUE,
				sprite.look.getXInUserInterfaceDimensionUnit());

		action = ExtendedActions.setX(sprite, new Formula(String.valueOf(NOT_NUMERICAL_STRING)));
		action.act(1.0f);
		assertEquals("Incorrect sprite x position after SetXBrick executed", VALUE,
				sprite.look.getXInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		Sprite sprite = new Sprite("testSprite");
		SetXAction action = ExtendedActions.setX(sprite, null);
		action.act(1.0f);
		assertEquals("Incorrect sprite x position after SetXBrick executed", 0f,
				sprite.look.getXInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		Sprite sprite = new Sprite("testSprite");
		SetXAction action = ExtendedActions.setX(sprite, new Formula(Double.NaN));
		action.act(1.0f);
		assertEquals("Incorrect sprite x position after SetXBrick executed", 0f,
				sprite.look.getXInUserInterfaceDimensionUnit());
	}
}
