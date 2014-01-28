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

public class ChangeYByNActionTest extends AndroidTestCase {

	private Formula yMovement = new Formula(100);

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite x position", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.look.getYInUserInterfaceDimensionUnit());

		int yPosition = (int) sprite.look.getYInUserInterfaceDimensionUnit();

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createChangeYByNAction(sprite, yMovement);
		sprite.look.addAction(action);
		action.act(1.0f);

		yPosition += yMovement.interpretInteger(sprite);
		assertEquals("Incorrect sprite y position after ChangeYByNBrick executed", (float) yPosition,
				sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNullSprite() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createChangeYByNAction(null, yMovement);

		try {
			action.act(1.0f);
			fail("Execution of ChangeYByNBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			assertTrue("Exception thrown successful", true);
		}
	}

	public void testBoundaryPositions() {
		Sprite sprite = new Sprite("testSprite");

		int yPosition = 10;
		sprite.look.setPositionInUserInterfaceDimensionUnit(sprite.look.getXInUserInterfaceDimensionUnit(), yPosition);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createChangeYByNAction(sprite, new Formula(Integer.MAX_VALUE));
		sprite.look.addAction(action);
		action.act(1.0f);

		assertEquals("ChangeYByNBrick failed to place Sprite at maximum y integer value", Integer.MAX_VALUE,
				(int) sprite.look.getYInUserInterfaceDimensionUnit());

		yPosition = -10;
		sprite.look.setPositionInUserInterfaceDimensionUnit(sprite.look.getXInUserInterfaceDimensionUnit(), yPosition);

		action = factory.createChangeYByNAction(sprite, new Formula(Integer.MIN_VALUE));
		sprite.look.addAction(action);
		action.act(1.0f);

		assertEquals("ChangeYByNBrick failed to place Sprite at minimum y integer value", Integer.MIN_VALUE,
				(int) sprite.look.getYInUserInterfaceDimensionUnit());

	}
}
