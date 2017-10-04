/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import org.catrobat.catroid.test.BaseTest;
import org.catrobat.catroid.utils.TouchUtil;

public class GoToTouchPositionActionTest extends BaseTest {

	private static final float EXPECTED_X_POSITION = 20f;
	private static final float EXPECTED_Y_POSITION = 25f;
	private Sprite sprite;
	private Sprite dummySprite;
	private Action action;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = createSprite("testSprite");
		dummySprite = createSprite("dummySprite");
		action = sprite.getActionFactory().createGoToAction(sprite, dummySprite, BrickValues.GO_TO_TOUCH_POSITION);
	}

	public void testGoToTouchPositionAction() throws InterruptedException {
		sprite.look.setXInUserInterfaceDimensionUnit(0f);
		sprite.look.setYInUserInterfaceDimensionUnit(0f);

		assertEquals("Unexpected initial sprite x position", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.look.getYInUserInterfaceDimensionUnit());

		TouchUtil.setDummyTouchForTest(EXPECTED_X_POSITION, EXPECTED_Y_POSITION);

		action.act(1f);

		assertEquals("Incorrect sprite x position after GoToTouchPositionAction executed", EXPECTED_X_POSITION,
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Incorrect sprite y position after GoToTouchPositionAction executed", EXPECTED_Y_POSITION,
				sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNullActor() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createGoToAction(null, dummySprite, BrickValues.GO_TO_TOUCH_POSITION);
		try {
			action.act(1.0f);
			fail("Execution of GoToBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
		}
	}
}
