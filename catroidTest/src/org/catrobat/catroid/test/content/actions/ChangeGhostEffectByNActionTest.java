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
import org.catrobat.catroid.content.actions.ChangeGhostEffectByNAction;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;

public class ChangeGhostEffectByNActionTest extends AndroidTestCase {

	private static final float DELTA = 0.01f;
	private static final float CHANGE_VALUE = 98.7f;
	private static final String NOT_NUMERICAL_STRING = "ghotst";
	private final Formula increaseGhostEffect = new Formula(100f);
	private final Formula decreaseGhostEffect = new Formula(-10f);

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite ghost effect value", 0f,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit());

		float ghostEffect = sprite.look.getTransparencyInUserInterfaceDimensionUnit();
		ghostEffect += increaseGhostEffect.interpretDouble(sprite);

		ChangeGhostEffectByNAction action1 = ExtendedActions.changeGhostEffectByN(sprite, new Formula(
				increaseGhostEffect.interpretDouble(sprite)));
		sprite.look.addAction(action1);
		action1.act(1.0f);
		assertEquals("Incorrect sprite ghost effect value after ChangeGhostEffectByNBrick executed", ghostEffect,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit());

		ghostEffect = sprite.look.getTransparencyInUserInterfaceDimensionUnit();
		ghostEffect += decreaseGhostEffect.interpretDouble(sprite);

		ChangeGhostEffectByNAction action2 = ExtendedActions.changeGhostEffectByN(sprite, new Formula(
				decreaseGhostEffect.interpretDouble(sprite)));
		sprite.look.addAction(action2);
		action2.act(1.0f);
		assertEquals("Incorrect sprite ghost effect value after ChangeGhostEffectByNBrick executed", ghostEffect,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit());
	}

	public void testNullSprite() {
		ChangeGhostEffectByNAction action = ExtendedActions.changeGhostEffectByN(null, increaseGhostEffect);
		try {
			action.act(1.0f);
			fail("Execution of ChangeGhostEffectByNBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			assertTrue("Exception thrown as aspected", true);

		}
	}

	public void testStringFormula() {
		Sprite sprite = new Sprite("testSprite");
		ChangeGhostEffectByNAction action = ExtendedActions.changeGhostEffectByN(sprite,
				new Formula(String.valueOf(CHANGE_VALUE)));
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("Incorrect sprite ghost effect value after ChangeGhostEffectByNBrick executed", CHANGE_VALUE,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit(), DELTA);
		sprite.look.removeAction(action);

		action = ExtendedActions.changeGhostEffectByN(sprite, new Formula(NOT_NUMERICAL_STRING));
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("Incorrect sprite ghost effect value after ChangeGhostEffectByNBrick executed", CHANGE_VALUE,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit(), DELTA);
		sprite.look.removeAction(action);
	}
}
