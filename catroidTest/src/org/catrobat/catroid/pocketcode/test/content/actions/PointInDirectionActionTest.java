/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.pocketcode.test.content.actions;

import org.catrobat.catroid.pocketcode.content.Sprite;
import org.catrobat.catroid.pocketcode.content.actions.ExtendedActions;
import org.catrobat.catroid.pocketcode.content.actions.PointInDirectionAction;
import org.catrobat.catroid.pocketcode.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.pocketcode.formulaeditor.Formula;

import android.test.AndroidTestCase;

public class PointInDirectionActionTest extends AndroidTestCase {

	public void testPointRight() {
		Sprite sprite = new Sprite("test");
		PointInDirectionAction action = ExtendedActions.pointInDirection(sprite,
				new Formula(Direction.DIRECTION_RIGHT.getDegrees()));
		action.act(1.0f);
		assertEquals("Wrong direction", 0f, sprite.look.getRotation(), 1e-3);
	}

	public void testPointLeft() {
		Sprite sprite = new Sprite("test");
		PointInDirectionAction action = ExtendedActions.pointInDirection(sprite,
				new Formula(Direction.DIRECTION_LEFT.getDegrees()));
		action.act(1.0f);
		assertEquals("Wrong direction", 180f, sprite.look.getRotation(), 1e-3);
	}

	public void testPointUp() {
		Sprite sprite = new Sprite("test");
		PointInDirectionAction action = ExtendedActions.pointInDirection(sprite,
				new Formula(Direction.DIRECTION_UP.getDegrees()));
		action.act(1.0f);
		assertEquals("Wrong direction", 90f, sprite.look.getRotation(), 1e-3);
	}

	public void testPointDown() {
		Sprite sprite = new Sprite("test");
		PointInDirectionAction action = ExtendedActions.pointInDirection(sprite,
				new Formula(Direction.DIRECTION_DOWN.getDegrees()));
		action.act(1.0f);
		assertEquals("Wrong direction", -90f, sprite.look.getRotation(), 1e-3);
	}

	public void testRotateAndPoint() {
		Sprite sprite = new Sprite("test");
		sprite.look.setRotation(-42);
		PointInDirectionAction action = ExtendedActions.pointInDirection(sprite,
				new Formula(Direction.DIRECTION_RIGHT.getDegrees()));
		action.act(1.0f);
		assertEquals("Wrong direction", 0f, sprite.look.getRotation(), 1e-3);
	}

}
