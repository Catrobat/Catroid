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
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.SetBrightnessAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class SetBrightnessActionTest extends InstrumentationTestCase {

	private static final float BRIGHTNESS = 91f;
	private Formula brightness = new Formula(BRIGHTNESS);
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		super.setUp();
	}

	public void testBrightnessEffect() {
		assertEquals("Unexpected initial brightness value", 100f,
				sprite.look.getBrightnessInUserInterfaceDimensionUnit());
		ExtendedActions.setBrightness(sprite, brightness).act(1.0f);
		assertEquals("Incorrect brightness value after SetBrightnessBrick executed",
				BRIGHTNESS, sprite.look.getBrightnessInUserInterfaceDimensionUnit());
	}

	public void testNullSprite() {
		SetBrightnessAction action = ExtendedActions.setBrightness(null, brightness);
		try {
			action.act(1.0f);
			fail("Execution of SetBrightnessBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
		}
	}

	public void testNegativeBrightnessValue() {
		ExtendedActions.setBrightness(sprite, new Formula(-BRIGHTNESS)).act(1.0f);
		assertEquals("Incorrect sprite scale value after SetBrightnessBrick executed", 0f,
				sprite.look.getBrightnessInUserInterfaceDimensionUnit());
	}

	public void testBrickWithStringFormula() {
		ExtendedActions.setBrightness(sprite, new Formula(String.valueOf(BRIGHTNESS))).act(1.0f);
		assertEquals("Incorrect sprite scale value after SetBrightnessBrick executed", BRIGHTNESS,
				sprite.look.getBrightnessInUserInterfaceDimensionUnit());

		ExtendedActions.setBrightness(sprite, new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals("Incorrect sprite scale value after SetBrightnessBrick executed", BRIGHTNESS,
				sprite.look.getBrightnessInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		ExtendedActions.setBrightness(sprite, null).act(1.0f);
		assertEquals("Incorrect sprite size value after SetBrightnessBrick executed", 0f,
				sprite.look.getBrightnessInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		ExtendedActions.setBrightness(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Incorrect sprite size value after SetBrightnessBrick executed", 100f,
				sprite.look.getBrightnessInUserInterfaceDimensionUnit());
	}
}
