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

import android.graphics.Color;
import android.test.InstrumentationTestCase;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.SetColorToAction;
import org.catrobat.catroid.formulaeditor.Formula;

/**
 * Created by Philipp on 22.04.2015.
 */
public class SetColorToTest extends InstrumentationTestCase {

//	private static final com.badlogic.gdx.graphics.Color COLOR = new com.badlogic.gdx.graphics.Color(com.badlogic.gdx.graphics.Color.RED);
	private int COLOR = 244;
	private Formula color = new Formula(COLOR);
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		super.setUp();
	}

	public void testColorEffect() {
		assertEquals("Unexpected initial color value",(int) 255, (int) sprite.look.getColorInUserInterfaceDimensionUnit());
		ExtendedActions.setColorTo(sprite, color).act(1.0f);
		assertEquals("Incorrect color value after SetColorTo executed", COLOR, sprite.look.getColorInUserInterfaceDimensionUnit());
		ExtendedActions.setColorTo(sprite, color);
	}

	public void testValueAboveMax() {
		final int highColor = 1000;

		assertEquals("Unexpected initial color value",(int) 255, (int) sprite.look.getColorInUserInterfaceDimensionUnit());
		ExtendedActions.setColorTo(sprite, new Formula(highColor)).act(1.0f);
		assertEquals("Incorrect color value after SetColorTo executed", (highColor % 256), sprite.look.getColorInUserInterfaceDimensionUnit());
	}

	public void testNullSprite() {
		SetColorToAction action = ExtendedActions.setColorTo(null, color);
		try {
			action.act(1.0f);
			fail("Execution of SetColorToBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			assertTrue("Exception thrown as expected", true);
		}
	}

	public void testBrickWithStringFormula() {
		ExtendedActions.setColorTo(sprite, new Formula(String.valueOf(COLOR))).act(1.0f);
		assertEquals("Incorrect sprite color value after SetColorToBrick executed",
				COLOR, sprite.look.getColorInUserInterfaceDimensionUnit());

		ExtendedActions.setColorTo(sprite, new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals("Incorrect sprite color value after SetBrightnessBrick executed",
				COLOR, sprite.look.getColorInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		ExtendedActions.setColorTo(sprite, null).act(1.0f);
		assertEquals("Incorrect sprite color value after SetColorTo executed",
				255, (int) sprite.look.getColorInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		ExtendedActions.setBrightness(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Incorrect sprite color value after SetColor executed",
				255, (int) sprite.look.getColorInUserInterfaceDimensionUnit());
	}
}
