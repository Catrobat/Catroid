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

import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class GoToOtherSpritePositionActionTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static final float DESTINATION_X_POSITION = 150f;
	private static final float DESTINATION_Y_POSITION = 300f;
	private Sprite sprite;
	private Sprite destinationSprite;
	private Action action;

	@Before
	public void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		destinationSprite = new Sprite("destinationSprite");
		action = sprite.getActionFactory().createGoToAction(sprite, destinationSprite,
				BrickValues.GO_TO_OTHER_SPRITE_POSITION);
	}

	@Test
	public void testGoToOtherSpritePositionAction() throws InterruptedException {
		destinationSprite.look.setXInUserInterfaceDimensionUnit(DESTINATION_X_POSITION);
		destinationSprite.look.setYInUserInterfaceDimensionUnit(DESTINATION_Y_POSITION);

		sprite.look.setXInUserInterfaceDimensionUnit(0f);
		sprite.look.setYInUserInterfaceDimensionUnit(0f);

		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());

		action.act(1f);

		assertEquals(DESTINATION_X_POSITION, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(DESTINATION_Y_POSITION, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNullActor() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createGoToAction(null, destinationSprite, BrickValues.GO_TO_OTHER_SPRITE_POSITION);
		exception.expect(NullPointerException.class);
		action.act(1.0f);
	}
}
