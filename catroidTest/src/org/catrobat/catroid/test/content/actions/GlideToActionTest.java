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
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.GlideToAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class GlideToActionTest extends AndroidTestCase {

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

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		super.setUp();
	}

	public void testNormalBehavior() throws InterruptedException {
		assertEquals("Unexpected initial sprite x position", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
		sprite.look.setWidth(100.0f);
		sprite.look.setHeight(50.0f);

		GlideToAction action = ExtendedActions.glideTo(sprite, xPosition, yPosition, duration);
		long currentTimeDelta = System.currentTimeMillis();
		do {
			currentTimeDelta = System.currentTimeMillis() - currentTimeDelta;
		} while (!action.act(currentTimeDelta));

		assertEquals("Incorrect sprite x position after GlideToBrick executed", X_POSITION,
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Incorrect sprite y position after GlideToBrick executed", Y_POSITION,
				sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNullActor() {
		GlideToAction action = ExtendedActions.glideTo(null, xPosition, yPosition, duration);
		try {
			action.act(1.0f);
			fail("Execution of GlideToBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
		}
	}

	public void testBoundaryPositions() {
		ExtendedActions.placeAt(sprite, new Formula(Integer.MAX_VALUE), new Formula(
				Integer.MAX_VALUE)).act(1.0f);
		assertEquals("PlaceAtBrick failed to place Sprite at maximum x float value", (float) Integer.MAX_VALUE,
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("PlaceAtBrick failed to place Sprite at maximum y float value", (float) Integer.MAX_VALUE,
				sprite.look.getYInUserInterfaceDimensionUnit());

		ExtendedActions.placeAt(sprite, new Formula(Integer.MIN_VALUE), new Formula(Integer.MIN_VALUE)).act(1.0f);
		assertEquals("PlaceAtBrick failed to place Sprite at minimum x float value", (float) Integer.MIN_VALUE,
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("PlaceAtBrick failed to place Sprite at minimum y float value", (float) Integer.MIN_VALUE,
				sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testPauseResume() throws InterruptedException {
		assertEquals("Unexpected initial sprite x position", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
		sprite.look.setWidth(100.0f);
		sprite.look.setHeight(50.0f);

		GlideToAction action = ExtendedActions.glideTo(sprite, xPosition, yPosition, duration);
		long currentTimeDelta = System.currentTimeMillis();
		do {
			currentTimeDelta = System.currentTimeMillis() - currentTimeDelta;
			if (currentTimeDelta > 400) {
				sprite.pause();
				Thread.sleep(200);
				sprite.resume();
			}
		} while (!action.act(currentTimeDelta));

		assertEquals("Incorrect sprite x position after GlideToBrick executed", X_POSITION,
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Incorrect sprite y position after GlideToBrick executed", Y_POSITION,
				sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testBrickWithStringFormula() {
		GlideToAction action = ExtendedActions.glideTo(sprite, new Formula(String.valueOf(Y_POSITION)),
				new Formula(String.valueOf(DURATION)), new Formula(String.valueOf(X_POSITION)));

		long currentTimeDelta = System.currentTimeMillis();
		do {
			currentTimeDelta = System.currentTimeMillis() - currentTimeDelta;
		} while (!action.act(currentTimeDelta));

		assertEquals("Incorrect sprite x position after GlideToBrick executed", Y_POSITION,
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Incorrect sprite y position after GlideToBrick executed", DURATION,
				sprite.look.getYInUserInterfaceDimensionUnit());

		action = ExtendedActions.glideTo(sprite, new Formula(NOT_NUMERICAL_STRING), new Formula(NOT_NUMERICAL_STRING2),
				new Formula(NOT_NUMERICAL_STRING3));

		currentTimeDelta = System.currentTimeMillis();
		do {
			currentTimeDelta = System.currentTimeMillis() - currentTimeDelta;
		} while (!action.act(currentTimeDelta));

		assertEquals("Incorrect sprite x position after GlideToBrick executed", 0f,
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Incorrect sprite y position after GlideToBrick executed", 0f,
				sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		GlideToAction action = ExtendedActions.glideTo(sprite, null, null, null);

		long currentTimeDelta = System.currentTimeMillis();
		do {
			currentTimeDelta = System.currentTimeMillis() - currentTimeDelta;
		} while (!action.act(currentTimeDelta));

		assertEquals("Incorrect sprite x position after GlideToBrick executed", 0f,
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Incorrect sprite y position after GlideToBrick executed", 0f,
				sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		GlideToAction action = ExtendedActions.glideTo(sprite, new Formula(Double.NaN), new Formula(Double.NaN),
				new Formula(Double.NaN));

		long currentTimeDelta = System.currentTimeMillis();
		do {
			currentTimeDelta = System.currentTimeMillis() - currentTimeDelta;
		} while (!action.act(currentTimeDelta));

		assertEquals("Incorrect sprite x position after GlideToBrick executed", 0f,
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Incorrect sprite y position after GlideToBrick executed", 0f,
				sprite.look.getYInUserInterfaceDimensionUnit());
	}
}
