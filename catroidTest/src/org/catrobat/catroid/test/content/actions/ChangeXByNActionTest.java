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

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;

public class ChangeXByNActionTest extends AndroidTestCase {

	private static final float CHANGE_VALUE = 55.5f;
	private static final String NOT_NUMERICAL_STRING = "xPosition";
    private Sprite sprite;

    @Override
    protected void setUp() throws Exception {
        sprite = new Sprite("testSprite");
        super.setUp();
    }

	public void testNormalBehavior() {
		assertEquals("Unexpected initial sprite x position", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		int xPosition = (int) sprite.look.getXInUserInterfaceDimensionUnit();
		xPosition += CHANGE_VALUE;
		sprite.getActionFactory().createChangeXByNAction(sprite, new Formula(CHANGE_VALUE)).act(1.0f);
		assertEquals("Incorrect sprite x position after ChangeXByNBrick executed", (float) xPosition,
				sprite.look.getXInUserInterfaceDimensionUnit());
	}

	public void testNullSprite() {
		Action action = sprite.getActionFactory().createChangeXByNAction(null, new Formula(CHANGE_VALUE));
		try {
			action.act(1.0f);
			fail("Execution of ChangeXByNBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			assertTrue("Exception thrown successful", true);
		}
	}

	public void testBoundaryPositions() {
		int xPosition = 10;
		sprite.look.setPositionInUserInterfaceDimensionUnit(xPosition, sprite.look.getYInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeXByNAction(sprite, new Formula(Integer.MAX_VALUE)).act(1.0f);
		assertEquals("ChangeXByNBrick failed to place Sprite at maximum x integer value", Integer.MAX_VALUE,
				(int) sprite.look.getXInUserInterfaceDimensionUnit());

		xPosition = -10;
		sprite.look.setPositionInUserInterfaceDimensionUnit(xPosition, sprite.look.getYInUserInterfaceDimensionUnit());
		sprite.getActionFactory().createChangeXByNAction(sprite, new Formula(Integer.MIN_VALUE)).act(1.0f);
		assertEquals("ChangeXByNBrick failed to place Sprite at minimum x integer value", Integer.MIN_VALUE,
				(int) sprite.look.getXInUserInterfaceDimensionUnit());

	}

	public void testBrickWithStringFormula() {
		sprite.getActionFactory().createChangeXByNAction(sprite, new Formula(String.valueOf(CHANGE_VALUE))).act(1.0f);
		assertEquals("Incorrect sprite x position after ChangeXByNBrick executed", CHANGE_VALUE,
				sprite.look.getXInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeXByNAction(sprite, new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals("Incorrect sprite x position after ChangeXByNBrick executed", CHANGE_VALUE,
				sprite.look.getXInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		sprite.getActionFactory().createChangeXByNAction(sprite, null).act(1.0f);
		assertEquals("Incorrect sprite x position after ChangeXByNBrick executed", 0f,
				sprite.look.getXInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		sprite.getActionFactory().createChangeXByNAction(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Incorrect sprite x position after ChangeXByNBrick executed", 0f,
				sprite.look.getXInUserInterfaceDimensionUnit());
	}
}
