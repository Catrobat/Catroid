/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.GoToRandomPositionAction;

public class GoToRandomPositionActionTest extends AndroidTestCase {

	private Sprite sprite;
	private Sprite dummySprite;
	private GoToRandomPositionAction action;

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		dummySprite = new Sprite("dummySprite");
		action = (GoToRandomPositionAction) sprite.getActionFactory().createGoToAction(
				sprite, dummySprite, BrickValues.GO_TO_RANDOM_POSITION);
		super.setUp();
	}

	public void testGoToOtherSpriteAction() throws InterruptedException {
		sprite.look.setXInUserInterfaceDimensionUnit(0f);
		sprite.look.setYInUserInterfaceDimensionUnit(0f);

		assertEquals("Unexpected initial sprite x position", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.look.getYInUserInterfaceDimensionUnit());

		action.act(1f);

		assertEquals("Incorrect sprite x position after GoToRandomPositionAction executed", action.getRandomXPosition(),
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Incorrect sprite y position after GoToRandomPositionAction executed", action.getRandomYPosition(),
				sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNullActor() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createGoToAction(null, dummySprite, BrickValues.GO_TO_RANDOM_POSITION);
		try {
			action.act(1.0f);
			fail("Execution of GoToBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
		}
	}
}
