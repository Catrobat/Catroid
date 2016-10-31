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

public class GoToOtherSpritePositionActionTest extends AndroidTestCase {

	private static final float DESTINATION_X_POSITION = 150f;
	private static final float DESTINATION_Y_POSITION = 300f;
	private Sprite sprite;
	private Sprite destinationSprite;
	private Action action;

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		destinationSprite = new Sprite("destinationSprite");
		action = sprite.getActionFactory().createGoToAction(sprite, destinationSprite,
				BrickValues.GO_TO_OTHER_SPRITE_POSITION);
		super.setUp();
	}

	public void testGoToOtherSpritePositionAction() throws InterruptedException {
		destinationSprite.look.setXInUserInterfaceDimensionUnit(DESTINATION_X_POSITION);
		destinationSprite.look.setYInUserInterfaceDimensionUnit(DESTINATION_Y_POSITION);

		sprite.look.setXInUserInterfaceDimensionUnit(0f);
		sprite.look.setYInUserInterfaceDimensionUnit(0f);

		assertEquals("Unexpected initial sprite x position", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.look.getYInUserInterfaceDimensionUnit());

		action.act(1f);

		assertEquals("Incorrect sprite x position after GoToOtherSpritePositionAction executed", DESTINATION_X_POSITION,
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Incorrect sprite y position after GoToOtherSpritePositionAction executed", DESTINATION_Y_POSITION,
				sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNullActor() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createGoToAction(null, destinationSprite, BrickValues.GO_TO_OTHER_SPRITE_POSITION);
		try {
			action.act(1.0f);
			fail("Execution of GoToBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
		}
	}
}
