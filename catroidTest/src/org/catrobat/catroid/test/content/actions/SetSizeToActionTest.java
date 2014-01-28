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
import org.catrobat.catroid.physic.content.ActionFactory;

public class SetSizeToActionTest extends InstrumentationTestCase {

    private static final float SIZE = 70.7f;
	private final Formula size = new Formula(SIZE);
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
    private Sprite sprite;

    @Override
    protected void setUp() throws Exception {
        sprite = new Sprite("testSprite");
        super.setUp();
    }

	public void testSize() {
		assertEquals("Unexpected initial sprite size value", 1f, sprite.look.getScaleX());
		assertEquals("Unexpected initial sprite size value", 1f, sprite.look.getScaleY());

		sprite.getActionFactory().createSetSizeToAction(sprite, size).act(1.0f);
		assertEquals("Incorrect sprite size value after SetSizeToBrick executed", SIZE / 100,
				sprite.look.getScaleX());
		assertEquals("Incorrect sprite size value after SetSizeToBrick executed", SIZE / 100,
				sprite.look.getScaleY());
	}

	public void testNegativeSize() {
		float initialSize = sprite.look.getSizeInUserInterfaceDimensionUnit();
		assertEquals("Unexpected initial sprite size value", 100f, initialSize);

		sprite.getActionFactory().createSetSizeToAction(sprite, new Formula(-10)).act(1.0f);
		assertEquals("Incorrect sprite size value after SetSizeToBrick executed", 0f,
				sprite.look.getSizeInUserInterfaceDimensionUnit());
	}

	public void testNullSprite() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createSetSizeToAction(null, size);
		try {
			action.act(1.0f);
			fail("Execution of SetSizeToBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			assertTrue("Exception thrown as expected", true);
		}
	}

	public void testBrickWithStringFormula() {
		sprite.getActionFactory().createSetSizeToAction(sprite, new Formula(String.valueOf(SIZE))).act(1.0f);
		assertEquals("Incorrect sprite size value after SetSizeToBrick executed", SIZE,
                sprite.look.getSizeInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createSetSizeToAction(sprite, new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals("Incorrect sprite size value after SetSizeToBrick executed", SIZE,
				sprite.look.getSizeInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createSetSizeToAction(sprite, null).act(1.0f);
		assertEquals("Incorrect sprite size value after SetSizeToBrick executed", 0f,
				sprite.look.getSizeInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		sprite.getActionFactory().createSetSizeToAction(sprite, null).act(1.0f);
		assertEquals("Incorrect sprite size value after SetSizeToBrick executed", 0f,
				sprite.look.getSizeInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		sprite.getActionFactory().createSetSizeToAction(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals("Incorrect sprite size value after SetSizeToBrick executed", 100f,
				sprite.look.getSizeInUserInterfaceDimensionUnit());
	}
}
