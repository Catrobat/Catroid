/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;

public class SetColorActionTest extends InstrumentationTestCase {

	private static final float COLOR = 100.0f;
	private Formula color = new Formula(COLOR);
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		sprite = new SingleSprite("testSprite");
		super.setUp();
	}

	public void testColorEffect() {
		assertEquals("Unexpected initial color value", 0, (int) sprite.look.getColorInUserInterfaceDimensionUnit());
		sprite.getActionFactory().createSetColorAction(sprite, color).act(1.0f);
		assertEquals("Incorrect color value after SetColorTo executed", COLOR, sprite.look.getColorInUserInterfaceDimensionUnit());
		sprite.getActionFactory().createSetColorAction(sprite, color);
	}

	public void testValueAboveMax() {
		final float highColor = 1000;

		assertEquals("Unexpected initial color value", 0, (int) sprite.look.getColorInUserInterfaceDimensionUnit());
		sprite.getActionFactory().createSetColorAction(sprite, new Formula(highColor)).act(1.0f);
		assertEquals("Incorrect color value after SetColorTo executed", (highColor % 200), sprite.look.getColorInUserInterfaceDimensionUnit());
	}

	public void testNullSprite() {
		Action action = sprite.getActionFactory().createSetColorAction(null, color);
		try {
			action.act(1.0f);
			fail("Execution of SetColorToBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			assertTrue("Exception thrown as expected", true);
		}
	}

	public void testBrickWithStringFormula() {
		sprite.getActionFactory().createSetColorAction(sprite, new Formula(String.valueOf(COLOR))).act(1.0f);
		assertEquals("Incorrect sprite color value after SetColorToBrick executed",
				COLOR, sprite.look.getColorInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createSetColorAction(sprite, new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals("Incorrect sprite color value after SetBrightnessBrick executed",
				COLOR, sprite.look.getColorInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		sprite.getActionFactory().createSetColorAction(sprite, null).act(1.0f);
		assertEquals("Incorrect sprite color value after SetColorTo executed",
				0, (int) sprite.look.getColorInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		sprite.getActionFactory().createSetColorAction(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Incorrect sprite color value after SetColor executed",
				0, (int) sprite.look.getColorInUserInterfaceDimensionUnit());
	}
}
