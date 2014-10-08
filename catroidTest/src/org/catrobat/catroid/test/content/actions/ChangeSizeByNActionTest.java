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

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ChangeSizeByNAction;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;

public class ChangeSizeByNActionTest extends InstrumentationTestCase {

	private static final float CHANGE_SIZE = 20f;
	private static final float DELTA = 0.0001f;

	public void testSize() {
		Sprite sprite = new Sprite("testSprite");
		float initialSize = sprite.look.getSizeInUserInterfaceDimensionUnit();
		assertEquals("Unexpected initial sprite size value", 100f, initialSize);

		ChangeSizeByNAction action = ExtendedActions.changeSizeByN(sprite, new Formula(CHANGE_SIZE));
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("Incorrect sprite size value after ChangeSizeByNBrick executed", initialSize + CHANGE_SIZE,
				sprite.look.getSizeInUserInterfaceDimensionUnit(), DELTA);

		action = ExtendedActions.changeSizeByN(sprite, new Formula(-CHANGE_SIZE));
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("Incorrect sprite size value after ChangeSizeByNBrick executed", initialSize,
				sprite.look.getSizeInUserInterfaceDimensionUnit(), DELTA);
	}

	public void testNullSprite() {
		ChangeSizeByNAction action = ExtendedActions.changeSizeByN(null, new Formula(CHANGE_SIZE));
		try {
			action.act(1.0f);
			fail("Execution of ChangeSizeByNBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException nullPointerException) {
			assertTrue("Exception thrown successful", true);
		}
	}

}
