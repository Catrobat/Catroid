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

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;

public class SetTransparencyActionTest extends InstrumentationTestCase {

    private static final float TRANSPARENCY = 91f;
	private Formula effect = new Formula(TRANSPARENCY);
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		super.setUp();
	}

	public void testTransparency() {
		assertEquals("Unexpected initial sprite ghost effect value", 0f,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createSetGhostEffectAction(sprite, effect).act(1.0f);
		assertEquals("Incorrect sprite scale value after SetGhostEffectBrick executed",
				TRANSPARENCY, sprite.look.getTransparencyInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createSetGhostEffectAction(sprite, new Formula(-50.0)).act(1.0f);
		assertEquals("Incorrect sprite scale value after SetGhostEffectBrick executed", 0f,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createSetGhostEffectAction(sprite, new Formula(150.0)).act(1.0f);
		assertEquals("Incorrect sprite scale value after SetGhostEffectBrick executed", 100f,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit());
	}

	public void testNullSprite() {
		Action action = sprite.getActionFactory().createSetGhostEffectAction(null, effect);
		try {
			action.act(1.0f);
			fail("Execution of SetTransparencyBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			assertTrue("Exception thrown as expected", true);
		}
	}

	public void testBrickWithStringFormula() {
		sprite.getActionFactory().createSetGhostEffectAction(sprite, new Formula(String.valueOf(TRANSPARENCY))).act(1.0f);
		assertEquals("Incorrect sprite scale value after SetGhostEffectBrick executed", TRANSPARENCY,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createSetGhostEffectAction(sprite, new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals("Incorrect sprite scale value after SetGhostEffectBrick executed", TRANSPARENCY,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		sprite.getActionFactory().createSetGhostEffectAction(sprite, null).act(1.0f);
		assertEquals("Incorrect sprite size value after SetGhostEffectBrick executed", 0f,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit());

	}

	public void testNotANumberFormula() {
		sprite.getActionFactory().createSetGhostEffectAction(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Incorrect sprite size value after SetGhostEffectBrick executed", 0f,
				sprite.look.getTransparencyInUserInterfaceDimensionUnit());
	}
}
