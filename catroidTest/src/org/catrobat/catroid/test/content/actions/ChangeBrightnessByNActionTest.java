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
import org.catrobat.catroid.content.actions.ChangeBrightnessByNAction;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;

public class ChangeBrightnessByNActionTest extends AndroidTestCase {

	private static final float INITIALIZED_VALUE = 100f;
	private static final String NOT_NUMERICAL_STRING = "brightness";
	private static final float BRIGHTER_VALUE = 50.5f;
	private static final float DIMMER_VALUE = -20.8f;
	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		super.setUp();
	}

	public void testNormalBehavior() {
		assertEquals("Unexpected initial sprite brightness value", INITIALIZED_VALUE,
				sprite.look.getBrightnessInUserInterfaceDimensionUnit());

		ExtendedActions.changeBrightnessByN(sprite, new Formula(BRIGHTER_VALUE)).act(1.0f);
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessByNBrick executed", INITIALIZED_VALUE + BRIGHTER_VALUE,
				sprite.look.getBrightnessInUserInterfaceDimensionUnit());

		ExtendedActions.changeBrightnessByN(sprite, new Formula(DIMMER_VALUE)).act(1.0f);
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessByNBrick executed", INITIALIZED_VALUE + BRIGHTER_VALUE + DIMMER_VALUE,
				sprite.look.getBrightnessInUserInterfaceDimensionUnit());
	}

	public void testNullSprite() {
		ChangeBrightnessByNAction action = ExtendedActions.changeBrightnessByN(null, new Formula(BRIGHTER_VALUE));
		try {
			action.act(1.0f);
			fail("Execution of ChangeBrightnessByNBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
		}
	}

	public void testBrickWithStringFormula() {
		ExtendedActions.changeBrightnessByN(sprite, new Formula(String.valueOf(BRIGHTER_VALUE))).act(1.0f);
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessByNBrick executed", INITIALIZED_VALUE
				+ BRIGHTER_VALUE, sprite.look.getBrightnessInUserInterfaceDimensionUnit());

		ExtendedActions.changeBrightnessByN(sprite, new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessByNBrick executed", INITIALIZED_VALUE
				+ BRIGHTER_VALUE, sprite.look.getBrightnessInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		ExtendedActions.changeBrightnessByN(sprite, null).act(1.0f);
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessByNBrick executed", INITIALIZED_VALUE,
				sprite.look.getBrightnessInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		ExtendedActions.changeBrightnessByN(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessByNBrick executed", INITIALIZED_VALUE,
				sprite.look.getBrightnessInUserInterfaceDimensionUnit());
	}
}
