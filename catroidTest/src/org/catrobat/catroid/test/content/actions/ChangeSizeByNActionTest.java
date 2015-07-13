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

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ChangeSizeByNAction;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;

public class ChangeSizeByNActionTest extends InstrumentationTestCase {

	private static final float INITIALIZED_VALUE = 100f;
	private static final float CHANGE_VALUE = 44.4f;
	private static final String NOT_NUMERICAL_STRING = "size";
	private static final float CHANGE_SIZE = 20f;
	private static final float DELTA = 0.0001f;
	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		super.setUp();
	}

	public void testSize() {
		assertEquals("Unexpected initial sprite size value", INITIALIZED_VALUE, sprite.look.getSizeInUserInterfaceDimensionUnit());

		ExtendedActions.changeSizeByN(sprite, new Formula(CHANGE_SIZE)).act(1.0f);
		assertEquals("Incorrect sprite size value after ChangeSizeByNBrick executed", INITIALIZED_VALUE + CHANGE_SIZE,
				sprite.look.getSizeInUserInterfaceDimensionUnit(), DELTA);

		ExtendedActions.changeSizeByN(sprite, new Formula(-CHANGE_SIZE)).act(1.0f);
		assertEquals("Incorrect sprite size value after ChangeSizeByNBrick executed", INITIALIZED_VALUE,
				sprite.look.getSizeInUserInterfaceDimensionUnit(), DELTA);
	}

	public void testNullSprite() {
		ChangeSizeByNAction action = ExtendedActions.changeSizeByN(null, new Formula(CHANGE_SIZE));
		try {
			action.act(1.0f);
			fail("Execution of ChangeSizeByNBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
		}
	}

	public void testBrickWithStringFormula() {
		ExtendedActions.changeSizeByN(sprite, new Formula(String.valueOf(CHANGE_VALUE))).act(1.0f);
		assertEquals("Incorrect sprite size value after ChangeSizeByNBrick executed", INITIALIZED_VALUE + CHANGE_VALUE,
				sprite.look.getSizeInUserInterfaceDimensionUnit());

		ExtendedActions.changeSizeByN(sprite, new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals("Incorrect sprite size value after ChangeSizeByNBrick executed", INITIALIZED_VALUE + CHANGE_VALUE,
				sprite.look.getSizeInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		ExtendedActions.changeSizeByN(sprite, null).act(1.0f);
		assertEquals("Incorrect sprite size value after ChangeSizeByNBrick executed", INITIALIZED_VALUE,
				sprite.look.getSizeInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		ExtendedActions.changeSizeByN(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Incorrect sprite size value after ChangeSizeByNBrick executed", INITIALIZED_VALUE,
				sprite.look.getSizeInUserInterfaceDimensionUnit());
	}
}
