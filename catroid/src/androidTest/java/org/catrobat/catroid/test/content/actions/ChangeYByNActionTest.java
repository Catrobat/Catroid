/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;

public class ChangeYByNActionTest extends AndroidTestCase {

	private static final float CHANGE_VALUE = 66.6f;
	private static final String NOT_NUMERICAL_STRING = "yPosition";
	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		sprite = new SingleSprite("testSprite");
		super.setUp();
	}

	public void testNormalBehavior() {
		assertEquals("Unexpected initial sprite x position", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
		sprite.getActionFactory().createChangeYByNAction(sprite, new Formula(CHANGE_VALUE)).act(1.0f);
		assertEquals("Incorrect sprite y position after ChangeYByNBrick executed", CHANGE_VALUE,
				sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNullSprite() {
		Action action = sprite.getActionFactory().createChangeYByNAction(null, new Formula(CHANGE_VALUE));
		try {
			action.act(1.0f);
			fail("Execution of ChangeYByNBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
		}
	}

	public void testBoundaryPositions() {
		int yPosition = 10;
		sprite.look.setPositionInUserInterfaceDimensionUnit(sprite.look.getXInUserInterfaceDimensionUnit(), yPosition);

		sprite.getActionFactory().createChangeYByNAction(sprite, new Formula(Integer.MAX_VALUE)).act(1.0f);
		assertEquals("ChangeYByNBrick failed to place Sprite at maximum y integer value", Integer.MAX_VALUE,
				(int) sprite.look.getYInUserInterfaceDimensionUnit());

		yPosition = -10;
		sprite.look.setPositionInUserInterfaceDimensionUnit(sprite.look.getXInUserInterfaceDimensionUnit(), yPosition);

		sprite.getActionFactory().createChangeYByNAction(sprite, new Formula(Integer.MIN_VALUE)).act(1.0f);
		assertEquals("ChangeYByNBrick failed to place Sprite at minimum y integer value", Integer.MIN_VALUE,
				(int) sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testBrickWithStringFormula() {
		sprite.getActionFactory().createChangeYByNAction(sprite, new Formula(String.valueOf(CHANGE_VALUE))).act(1.0f);
		assertEquals("Incorrect sprite y position after ChangeYByNBrick executed", CHANGE_VALUE,
				sprite.look.getYInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeYByNAction(sprite, new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals("Incorrect sprite y position after ChangeYByNBrick executed", CHANGE_VALUE,
				sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		sprite.getActionFactory().createChangeYByNAction(sprite, null).act(1.0f);
		assertEquals("Incorrect sprite y position after ChangeYByNBrick executed", 0f,
				sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		sprite.getActionFactory().createChangeYByNAction(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Incorrect sprite y position after ChangeYByNBrick executed", 0f,
				sprite.look.getYInUserInterfaceDimensionUnit());
	}
}
