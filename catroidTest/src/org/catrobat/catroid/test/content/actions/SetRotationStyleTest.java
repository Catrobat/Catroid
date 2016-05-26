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

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.formulaeditor.Formula;

public class SetRotationStyleTest extends AndroidTestCase {

	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = new Sprite("testSprite");
	}

	public void testNormalMode() {
		ExtendedActions.setRotationStyle(sprite, new Formula(Look.ROTATION_STYLE_ALL_AROUND));
		ExtendedActions.pointInDirection(sprite, new Formula(Direction.RIGHT.getDegrees())).act(1.0f);

		assertEquals("Wrong direction", 90f, sprite.look.getDirectionInUserInterfaceDimensionUnit());
	}

	public void testLRMode() {
		ExtendedActions.setRotationStyle(sprite, new Formula(Look.ROTATION_STYLE_LEFT_RIGHT_ONLY));
		ExtendedActions.pointInDirection(sprite, new Formula(Direction.LEFT.getDegrees())).act(1.0f);

		assertEquals("Wrong direction", -90f, sprite.look.getDirectionInUserInterfaceDimensionUnit());
	}

	public void testNoMode() {
		ExtendedActions.setRotationStyle(sprite, new Formula(Look.ROTATION_STYLE_NONE));
		ExtendedActions.pointInDirection(sprite, new Formula(Direction.UP.getDegrees())).act(1.0f);

		assertEquals("Wrong direction", 0f, sprite.look.getDirectionInUserInterfaceDimensionUnit());
	}
}
