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

public class ChangeColorByNActionTest extends AndroidTestCase {

	private static final float INITIALIZED_VALUE = 0;
	private static final String NOT_NUMERICAL_STRING = "color";
	private static final float DELTA = 1;
	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		sprite = new SingleSprite("testSprite");
		super.setUp();

		sprite.getActionFactory().createSetColorAction(sprite, new Formula(INITIALIZED_VALUE)).act(1.0f);
	}

	public void testNormalBehavior() {
		assertEquals("Unexpected initial sprite color value", INITIALIZED_VALUE,
				sprite.look.getColorInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeColorByNAction(sprite, new Formula(DELTA)).act(1.0f);
		assertEquals("Incorrect sprite color value after ChangeColorByNAction executed", INITIALIZED_VALUE + DELTA,
				sprite.look.getColorInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeColorByNAction(sprite, new Formula(-DELTA)).act(1.0f);
		assertEquals("Incorrect sprite color value after ChangeColorByNAction executed", INITIALIZED_VALUE,
				sprite.look.getColorInUserInterfaceDimensionUnit());
	}

	public void testNullSprite() {
		Action action = sprite.getActionFactory().createChangeColorByNAction(null, new Formula(DELTA));
		try {
			action.act(1.0f);
			fail("Execution of ChangeColorByN with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			assertTrue("Exception thrown correctly", true);
		}
	}

	public void testBrickWithStringFormula() {
		sprite.getActionFactory().createChangeColorByNAction(sprite, new Formula(String.valueOf(DELTA))).act(1.0f);
		assertEquals("Incorrect sprite color value after ChangeColorByNAction executed", INITIALIZED_VALUE
				+ DELTA, sprite.look.getColorInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeColorByNAction(sprite, new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals("Incorrect sprite color value after ChangeColorByNAction executed", INITIALIZED_VALUE
				+ DELTA, sprite.look.getColorInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		sprite.getActionFactory().createChangeColorByNAction(sprite, null).act(1.0f);
		assertEquals("Incorrect sprite color value after ChangeColorByNBrick executed", 25.0f,
				sprite.look.getColorInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		sprite.getActionFactory().createChangeColorByNAction(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Incorrect sprite color value after ChangeColorByNBrick executed", INITIALIZED_VALUE,
				sprite.look.getColorInUserInterfaceDimensionUnit());
	}

	public void testWrapAround() {
		sprite.getActionFactory().createSetColorAction(sprite, new Formula(199.0f)).act(1.0f);
		assertEquals("Unexpected initial sprite color value", 199.0f,
				sprite.look.getColorInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeColorByNAction(sprite, new Formula(DELTA)).act(1.0f);
		assertEquals("Incorrect sprite color value after ChangeColorByNAction executed", 0.0f,
				sprite.look.getColorInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeColorByNAction(sprite, new Formula(-DELTA)).act(1.0f);
		assertEquals("Incorrect sprite color value after ChangeColorByNAction executed", 199.0f,
				sprite.look.getColorInUserInterfaceDimensionUnit());
	}
}
