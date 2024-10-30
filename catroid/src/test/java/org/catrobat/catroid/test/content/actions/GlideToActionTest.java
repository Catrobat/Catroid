/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.test.StaticSingletonInitializer.initializeStaticSingletonMethods;

@RunWith(JUnit4.class)
public class GlideToActionTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static final float X_POSITION = 12f;
	private static final float Y_POSITION = 150f;
	private static final float DURATION = 225f;
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private static final String NOT_NUMERICAL_STRING2 = "NOT_NUMERICAL_STRING2";
	private static final String NOT_NUMERICAL_STRING3 = "NOT_NUMERICAL_STRING3";
	Formula xPosition = new Formula(X_POSITION);
	Formula yPosition = new Formula(Y_POSITION);
	Formula duration = new Formula(DURATION);
	private Sprite sprite;

	@Before
	public void setUp() throws Exception {
		initializeStaticSingletonMethods();
		sprite = new Sprite("testSprite");
	}

	@Test
	public void testNormalBehavior() throws InterruptedException {
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());
		sprite.look.setWidth(100.0f);
		sprite.look.setHeight(50.0f);

		Action action = sprite.getActionFactory().createGlideToAction(sprite, new SequenceAction(), xPosition, yPosition, duration);
		long currentTimeDelta = System.currentTimeMillis();
		do {
			currentTimeDelta = System.currentTimeMillis() - currentTimeDelta;
		} while (!action.act(currentTimeDelta));

		assertEquals(X_POSITION, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(Y_POSITION, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test(expected = NullPointerException.class)
	public void testNullActor() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createGlideToAction(null, new SequenceAction(), xPosition, yPosition, duration);
		action.act(1.0f);
	}

	@Test
	public void testBoundaryPositions() {
		Sprite sprite = new Sprite("testSprite");
		sprite.getActionFactory().createPlaceAtAction(sprite, new SequenceAction(), new Formula(Integer.MAX_VALUE), new Formula(
				Integer.MAX_VALUE)).act(1.0f);
		assertEquals((float) Integer.MAX_VALUE, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals((float) Integer.MAX_VALUE, sprite.look.getYInUserInterfaceDimensionUnit());

		sprite.getActionFactory().createPlaceAtAction(sprite, new SequenceAction(), new Formula(Integer.MIN_VALUE), new Formula(
				Integer.MIN_VALUE)).act(1.0f);
		assertEquals((float) Integer.MIN_VALUE, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals((float) Integer.MIN_VALUE, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testBrickWithStringFormula() {
		Action action = sprite.getActionFactory().createGlideToAction(sprite, new SequenceAction(), new Formula(String.valueOf(Y_POSITION)),
				new Formula(String.valueOf(DURATION)), new Formula(String.valueOf(X_POSITION)));

		long currentTimeDelta = System.currentTimeMillis();
		do {
			currentTimeDelta = System.currentTimeMillis() - currentTimeDelta;
		} while (!action.act(currentTimeDelta));

		assertEquals(Y_POSITION, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(DURATION, sprite.look.getYInUserInterfaceDimensionUnit());

		action = sprite.getActionFactory().createGlideToAction(sprite, new SequenceAction(), new Formula(NOT_NUMERICAL_STRING), new Formula(
				NOT_NUMERICAL_STRING2), new Formula(NOT_NUMERICAL_STRING3));

		currentTimeDelta = System.currentTimeMillis();
		do {
			currentTimeDelta = System.currentTimeMillis() - currentTimeDelta;
		} while (!action.act(currentTimeDelta));

		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNullFormula() {
		Action action = sprite.getActionFactory().createGlideToAction(sprite, new SequenceAction(), null, null, null);

		long currentTimeDelta = System.currentTimeMillis();
		do {
			currentTimeDelta = System.currentTimeMillis() - currentTimeDelta;
		} while (!action.act(currentTimeDelta));

		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNotANumberFormula() {
		Action action = sprite.getActionFactory().createGlideToAction(sprite, new SequenceAction(), new Formula(Double.NaN),
				new Formula(Double.NaN), new Formula(Double.NaN));

		long currentTimeDelta = System.currentTimeMillis();
		do {
			currentTimeDelta = System.currentTimeMillis() - currentTimeDelta;
		} while (!action.act(currentTimeDelta));

		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}
}
