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
import org.catrobat.catroid.content.actions.ChangeTransparencyByNAction;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;

public class ChangeTransparencyByNActionTest extends AndroidTestCase {

	private static final float DELTA = 0.01f;
	private static final float INCREASE_VALUE = 98.7f;
	private static final float DECREASE_VALUE = -33.3f;
	private static final String NOT_NUMERICAL_STRING = "ghosts";
	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		super.setUp();
	}

	public void testNormalBehavior() {
		assertEquals("Unexpected initial sprite ghost effect value", 0f,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit());

		ExtendedActions.changeTransparencyByN(sprite, new Formula(INCREASE_VALUE)).act(1.0f);
		assertEquals("Incorrect sprite ghost effect value after ChangeTransparencyByNBrick executed", INCREASE_VALUE,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit());

		ExtendedActions.changeTransparencyByN(sprite, new Formula(DECREASE_VALUE)).act(1.0f);
		assertEquals("Incorrect sprite ghost effect value after ChangeTransparencyByNBrick executed", INCREASE_VALUE + DECREASE_VALUE,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit());
	}

	public void testNullSprite() {
		ChangeTransparencyByNAction action = ExtendedActions.changeTransparencyByN(null, new Formula(INCREASE_VALUE));
		try {
			action.act(1.0f);
			fail("Execution of ChangeTransparencyByNBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
		}
	}

	public void testBrickWithStringFormula() {
		ExtendedActions.changeTransparencyByN(sprite, new Formula(String.valueOf(INCREASE_VALUE))).act(1.0f);
		assertEquals("Incorrect sprite ghost effect value after ChangeTransparencyByNBrick executed", INCREASE_VALUE,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit(), DELTA);

		ExtendedActions.changeTransparencyByN(sprite, new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals("Incorrect sprite ghost effect value after ChangeTransparencyByNBrick executed", INCREASE_VALUE,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit(), DELTA);
	}

	public void testNullFormula() {
		ExtendedActions.changeTransparencyByN(sprite, null).act(1.0f);
		assertEquals("Incorrect sprite ghost effect value after ChangeTransparencyByNBrick executed", 0f,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		ExtendedActions.changeTransparencyByN(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Incorrect sprite ghost effect value after ChangeTransparencyByNBrick executed", 0f,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit());
	}
}
