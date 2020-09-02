/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ChangeColorByNActionTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static final float INITIALIZED_VALUE = 0;
	private static final String NOT_NUMERICAL_STRING = "color";
	private static final float DELTA = 1;
	private Sprite sprite;

	@Before
	public void setUp() throws Exception {
		sprite = new Sprite("testSprite");

		sprite.getActionFactory().createSetColorAction(sprite, new Formula(INITIALIZED_VALUE)).act(1.0f);
	}

	@Test
	public void testNormalBehavior() {
		assertEquals(INITIALIZED_VALUE, sprite.look.getColorInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeColorByNAction(sprite, new Formula(DELTA)).act(1.0f);
		assertEquals(INITIALIZED_VALUE + DELTA, sprite.look.getColorInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeColorByNAction(sprite, new Formula(-DELTA)).act(1.0f);
		assertEquals(INITIALIZED_VALUE, sprite.look.getColorInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNullSprite() {
		Action action = sprite.getActionFactory().createChangeColorByNAction(null, new Formula(DELTA));
		exception.expect(NullPointerException.class);
		action.act(1.0f);
	}

	@Test
	public void testBrickWithStringFormula() {
		sprite.getActionFactory().createChangeColorByNAction(sprite, new Formula(String.valueOf(DELTA))).act(1.0f);
		assertEquals(INITIALIZED_VALUE + DELTA, sprite.look.getColorInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeColorByNAction(sprite, new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
		assertEquals(INITIALIZED_VALUE + DELTA, sprite.look.getColorInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNullFormula() {
		sprite.getActionFactory().createChangeColorByNAction(sprite, null).act(1.0f);
		assertEquals(25.0f, sprite.look.getColorInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNotANumberFormula() {
		sprite.getActionFactory().createChangeColorByNAction(sprite, new Formula(Double.NaN)).act(1.0f);
		assertEquals(INITIALIZED_VALUE, sprite.look.getColorInUserInterfaceDimensionUnit());
	}

	@Test
	public void testWrapAround() {
		sprite.getActionFactory().createSetColorAction(sprite, new Formula(199.0f)).act(1.0f);
		assertEquals(199.0f, sprite.look.getColorInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeColorByNAction(sprite, new Formula(DELTA)).act(1.0f);
		assertEquals(0.0f, sprite.look.getColorInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createChangeColorByNAction(sprite, new Formula(-DELTA)).act(1.0f);
		assertEquals(199.0f, sprite.look.getColorInUserInterfaceDimensionUnit());
	}
}
