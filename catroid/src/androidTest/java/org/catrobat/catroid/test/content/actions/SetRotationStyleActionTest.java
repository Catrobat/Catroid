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

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsProperties;
import org.catrobat.catroid.test.BaseTest;

public class SetRotationStyleActionTest extends BaseTest {

	private Sprite sprite;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = createSprite("testSprite");
	}

	public void testNormalMode() {
		ActionFactory factory = sprite.getActionFactory();
		Action rotationStyleAction = factory.createSetRotationStyleAction(sprite, Look.ROTATION_STYLE_ALL_AROUND);
		Action pointInDirectionAction = factory.createPointInDirectionAction(sprite, new Formula(Direction.RIGHT
				.getDegrees()));

		rotationStyleAction.act(1.0f);
		pointInDirectionAction.act(1.0f);
		assertEquals("Wrong direction", 90f, sprite.look.getDirectionInUserInterfaceDimensionUnit());
	}

	public void testNoMode() {
		ActionFactory factory = sprite.getActionFactory();
		Action rotationStyleAction = factory.createSetRotationStyleAction(sprite, Look.ROTATION_STYLE_NONE);
		Action pointInDirectionAction = factory.createPointInDirectionAction(sprite, new Formula(Direction.LEFT
				.getDegrees()));

		rotationStyleAction.act(1.0f);
		pointInDirectionAction.act(1.0f);

		assertEquals("Wrong direction", -90f, sprite.look.getDirectionInUserInterfaceDimensionUnit());
	}

	public void testLRMode() {
		ActionFactory factory = sprite.getActionFactory();
		Action rotationStyleAction = factory.createSetRotationStyleAction(sprite, Look.ROTATION_STYLE_LEFT_RIGHT_ONLY);
		Action pointInDirectionAction = factory.createPointInDirectionAction(sprite, new Formula(Direction.LEFT
				.getDegrees()));

		rotationStyleAction.act(1.0f);
		pointInDirectionAction.act(1.0f);

		assertEquals("Wrong direction", -90f, sprite.look.getDirectionInUserInterfaceDimensionUnit());
	}

	//Directions here get funky because in physics there is no UI Degree Offset as in the normal looks
	//Right is Left, Left is Right, Up is Up and Down is Down

	public void testNormalModeInPhysics() {
		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		Look look = new Look(sprite);

		look.setRotationMode(Look.ROTATION_STYLE_ALL_AROUND);

		look.setRotation((float) Direction.RIGHT.getDegrees());
		assertEquals("Wrong physics object angle (Right)", 90f, physicsProperties.getDirection());
		assertEquals("Wrong direction (Right)", 90f, look.getRotation());

		look.setRotation((float) Direction.LEFT.getDegrees());
		assertEquals("Wrong physics object angle (Left)", -90f, physicsProperties.getDirection());
		assertEquals("Wrong direction (Left)", -90f, look.getRotation());

		look.setRotation((float) Direction.UP.getDegrees());
		assertEquals("Wrong physics object angle (Up)", 0f, physicsProperties.getDirection());
		assertEquals("Wrong direction (Up)", 0f, look.getRotation());

		look.setRotation((float) Direction.DOWN.getDegrees());
		assertEquals("Wrong physics object angle (Down)", 180f, physicsProperties.getDirection());
		assertEquals("Wrong direction (Down)", 180f, look.getRotation());
	}

	public void testNoModeInPhysics() {
		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		Look look = new Look(sprite);

		look.setRotationMode(Look.ROTATION_STYLE_NONE);

		look.setRotation((float) Direction.RIGHT.getDegrees());
		assertEquals("Wrong physics object angle (Right)", 90f, physicsProperties.getDirection());
		assertEquals("Wrong direction (Right)", 0f, look.getRotation());

		look.setRotation((float) Direction.LEFT.getDegrees());
		assertEquals("Wrong physics object angle (Left)", -90f, physicsProperties.getDirection());
		assertEquals("Wrong direction (Left)", 0f, look.getRotation());

		look.setRotation((float) Direction.UP.getDegrees());
		assertEquals("Wrong physics object angle (Up)", 0f, physicsProperties.getDirection());
		assertEquals("Wrong direction (Up)", 0f, look.getRotation());

		look.setRotation((float) Direction.DOWN.getDegrees());
		assertEquals("Wrong physics object angle (Down)", 180f, physicsProperties.getDirection());
		assertEquals("Wrong direction (Down)", 0f, look.getRotation());
	}

	public void testLRModeInPhysics() {
		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		Look look = new Look(sprite);

		look.setRotationMode(Look.ROTATION_STYLE_LEFT_RIGHT_ONLY);

		look.setRotation((float) Direction.RIGHT.getDegrees());
		assertEquals("Wrong physics object angle (Right)", 90f, physicsProperties.getDirection());
		assertEquals("Wrong direction (Right)", 0f, look.getRotation());

		look.setRotation((float) Direction.LEFT.getDegrees());
		assertEquals("Wrong physics object angle (Left)", -90f, physicsProperties.getDirection());
		assertEquals("Wrong direction (Left)", 0f, look.getRotation());

		look.setRotation((float) Direction.UP.getDegrees());
		assertEquals("Wrong physics object angle (Up)", 0f, physicsProperties.getDirection());
		assertEquals("Wrong direction (Up)", 0f, look.getRotation());

		look.setRotation((float) Direction.DOWN.getDegrees());
		assertEquals("Wrong physics object angle (Down)", 180f, physicsProperties.getDirection());
		assertEquals("Wrong direction (Down)", 0f, look.getRotation());
	}
}
