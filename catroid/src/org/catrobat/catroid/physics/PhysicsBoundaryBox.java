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
package org.catrobat.catroid.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsBoundaryBox {

	public static final int FRAME_SIZE = 5;

	private final World world;

	public enum BoundaryBoxIdentifier { BBI_HORIZONTAL, BBI_VERTICAL }

	public PhysicsBoundaryBox(World world) {
		this.world = world;
	}

	/**
	 * TODO: Create only one body with four shapes (sides). Refactor test after that.
	 *
	 * @param height
	 * @param width
	 */
	public void create(int width, int height) {
		float boxWidth = PhysicsWorldConverter.convertNormalToBox2dCoordinate(width);
		float boxHeight = PhysicsWorldConverter.convertNormalToBox2dCoordinate(height);
		float boxElementSize = PhysicsWorldConverter.convertNormalToBox2dCoordinate(PhysicsBoundaryBox.FRAME_SIZE);
		float halfBoxElementSize = boxElementSize / 2.0f;

		// Top
		createSide(new Vector2(0.0f, (boxHeight / 2.0f) + halfBoxElementSize), boxWidth, boxElementSize, BoundaryBoxIdentifier.BBI_HORIZONTAL);
		// Bottom
		createSide(new Vector2(0.0f, -(boxHeight / 2.0f) - halfBoxElementSize), boxWidth, boxElementSize, BoundaryBoxIdentifier.BBI_HORIZONTAL);
		// Left
		createSide(new Vector2(-(boxWidth / 2.0f) - halfBoxElementSize, 0.0f), boxElementSize, boxHeight, BoundaryBoxIdentifier.BBI_VERTICAL);
		// Right
		createSide(new Vector2((boxWidth / 2.0f) + halfBoxElementSize, 0.0f), boxElementSize, boxHeight, BoundaryBoxIdentifier.BBI_VERTICAL);
	}

	private void createSide(Vector2 center, float width, float height, BoundaryBoxIdentifier identifier) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.allowSleep = false;

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2.0f, height / 2f, center, 0.0f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.filter.maskBits = PhysicsWorld.MASK_BOUNDARYBOX;
		fixtureDef.filter.categoryBits = PhysicsWorld.CATEGORY_BOUNDARYBOX;

		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		body.setUserData(identifier);

		//		for (Fixture f : body.getFixtureList()) {
		//			Filter filter = new Filter();
		//			filter.categoryBits = 0;
		//			filter.maskBits = 0;
		//			f.setFilterData(filter);
		//		}
	}
}
