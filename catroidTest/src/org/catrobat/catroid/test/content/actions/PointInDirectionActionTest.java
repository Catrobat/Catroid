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
import org.catrobat.catroid.content.bricks.conditional.PointInDirectionBrick.Direction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physic.content.ActionFactory;

public class PointInDirectionActionTest extends AndroidTestCase {

	public void testPointRight() {
		Sprite sprite = new Sprite("test");
		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createPointInDirectionAction(sprite, new Formula(Direction.RIGHT.getDegrees()));
		action.act(1.0f);
		assertEquals("Wrong direction", 90f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
	}

	public void testPointLeft() {
		Sprite sprite = new Sprite("test");
		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createPointInDirectionAction(sprite, new Formula(Direction.LEFT.getDegrees()));
		action.act(1.0f);
		assertEquals("Wrong direction", -90f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
	}

	public void testPointUp() {
		Sprite sprite = new Sprite("test");
		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createPointInDirectionAction(sprite, new Formula(Direction.UP.getDegrees()));
		action.act(1.0f);
		assertEquals("Wrong direction", 0f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
	}

	public void testPointDown() {
		Sprite sprite = new Sprite("test");
		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createPointInDirectionAction(sprite, new Formula(Direction.DOWN.getDegrees()));
		action.act(1.0f);
		assertEquals("Wrong direction", 180f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
	}

	public void testRotateAndPoint() {
		Sprite sprite = new Sprite("test");
		ActionFactory factory = sprite.getActionFactory();
		sprite.look.setRotation(-42);
		Action action = factory.createPointInDirectionAction(sprite, new Formula(Direction.RIGHT.getDegrees()));
		action.act(1.0f);
		assertEquals("Wrong direction", 90f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
	}

}
