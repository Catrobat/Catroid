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

import android.support.test.runner.AndroidJUnit4;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.GoToRandomPositionAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class GoToRandomPositionActionTest {

	private Sprite sprite;
	private Sprite dummySprite;
	private GoToRandomPositionAction action;

	@Before
	public void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		dummySprite = new Sprite("dummySprite");
		action = (GoToRandomPositionAction) sprite.getActionFactory().createGoToAction(
				sprite, dummySprite, BrickValues.GO_TO_RANDOM_POSITION);
	}

	@Test
	public void testGoToOtherSpriteAction() throws InterruptedException {
		sprite.look.setXInUserInterfaceDimensionUnit(0f);
		sprite.look.setYInUserInterfaceDimensionUnit(0f);

		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());

		action.act(1f);

		assertEquals(action.getRandomXPosition(), sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(action.getRandomYPosition(), sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
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
