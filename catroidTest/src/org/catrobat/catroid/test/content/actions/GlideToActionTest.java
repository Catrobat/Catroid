/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physic.content.ActionFactory;

public class GlideToActionTest extends AndroidTestCase {

	Formula xPosition = new Formula(100);
	Formula yPosition = new Formula(100);
	Formula duration = new Formula(1000);

	public void testNormalBehavior() throws InterruptedException {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite x position", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
		sprite.look.setWidth(100.0f);
		sprite.look.setHeight(50.0f);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createGlideToAction(sprite, xPosition, yPosition, duration);
		sprite.look.addAction(action);
		long currentTimeDelta = System.currentTimeMillis();
		do {
			currentTimeDelta = System.currentTimeMillis() - currentTimeDelta;
		} while (!action.act(currentTimeDelta));

		assertEquals("Incorrect sprite x position after GlideToBrick executed", xPosition.interpretFloat(sprite),
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Incorrect sprite y position after GlideToBrick executed", yPosition.interpretFloat(sprite),
				sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNullActor() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createGlideToAction(null, xPosition, yPosition, duration);
		try {
			action.act(1.0f);
			fail("Execution of GlideToBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			assertTrue("Exception thrown successful", true);
		}
	}

	public void testBoundaryPositions() {
		Sprite sprite = new Sprite("testSprite");

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createPlaceAtAction(sprite, new Formula(Integer.MAX_VALUE), new Formula(
				Integer.MAX_VALUE));
		sprite.look.addAction(action);
		action.act(1.0f);

		assertEquals("PlaceAtBrick failed to place Sprite at maximum x float value", (float) Integer.MAX_VALUE,
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("PlaceAtBrick failed to place Sprite at maximum y float value", (float) Integer.MAX_VALUE,
				sprite.look.getYInUserInterfaceDimensionUnit());

		action = factory.createPlaceAtAction(sprite, new Formula(Integer.MIN_VALUE), new Formula(Integer.MIN_VALUE));
		sprite.look.addAction(action);
		action.act(1.0f);

		assertEquals("PlaceAtBrick failed to place Sprite at minimum x float value", (float) Integer.MIN_VALUE,
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("PlaceAtBrick failed to place Sprite at minimum y float value", (float) Integer.MIN_VALUE,
				sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testPauseResume() throws InterruptedException {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite x position", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
		sprite.look.setWidth(100.0f);
		sprite.look.setHeight(50.0f);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createGlideToAction(sprite, xPosition, yPosition, duration);
		sprite.look.addAction(action);
		long currentTimeDelta = System.currentTimeMillis();
		do {
			currentTimeDelta = System.currentTimeMillis() - currentTimeDelta;
			if (currentTimeDelta > 400) {
				sprite.pause();
				Thread.sleep(200);
				sprite.resume();
			}
		} while (!action.act(currentTimeDelta));

		assertEquals("Incorrect sprite x position after GlideToBrick executed", xPosition.interpretFloat(sprite),
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Incorrect sprite y position after GlideToBrick executed", yPosition.interpretFloat(sprite),
				sprite.look.getYInUserInterfaceDimensionUnit());
	}
}
