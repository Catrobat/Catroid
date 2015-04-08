/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.test.physics;

import android.test.AndroidTestCase;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.physics.PhysicsBoundaryBox;
import org.catrobat.catroid.physics.PhysicsWorld;

import java.util.Iterator;
import java.util.List;

public class PhysicsBoundaryBoxTest extends AndroidTestCase {
	static {
		GdxNativesLoader.load();
	}

	private World world;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		world = new World(PhysicsWorld.DEFAULT_GRAVITY, PhysicsWorld.IGNORE_SLEEPING_OBJECTS);
	}

	public void testDefaultSettings() {
		assertEquals("Wrong configuration", 5, PhysicsBoundaryBox.FRAME_SIZE);
		assertEquals("Wrong configuration", 0x0004, PhysicsWorld.MASK_BOUNDARYBOX);
		assertEquals("Wrong configuration", 0x0002, PhysicsWorld.CATEGORY_BOUNDARYBOX);
	}

	public void testProperties() {
		assertEquals("World isn't emtpy", 0, world.getBodyCount());
		// TODO[physics] refactor values given to create
		new PhysicsBoundaryBox(world).create(40, 40);
		assertEquals("World contains wrong number of boundary box sides", 4, world.getBodyCount());

		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);
		assertEquals("bodies contains wrong number", 4, bodies.size);
		for (Body body : bodies) {
			assertEquals("BodyType of boundary box side isn't static", BodyType.StaticBody, body.getType());

			//TODO [Physics] need this line -> expected behaviour is what???
			//assertTrue("Body isn't allowed to sleep", body.isSleepingAllowed());

			Array<Fixture> fixtures = body.getFixtureList();
			assertEquals("Body should contain only one shape (side)", 1, fixtures.size);
			for (Fixture fixture : fixtures) {
				Filter filter = fixture.getFilterData();
				assertEquals("Wrong bit mask for collision", PhysicsWorld.MASK_BOUNDARYBOX, filter.maskBits);
				assertEquals("Wrong category bits for collision", PhysicsWorld.CATEGORY_BOUNDARYBOX,
						filter.categoryBits);
			}
		}
	}

	/**
	 * //TODO [Physics] Refactor me!
	 */
	public void testPositionAndSize() {
		//		Values.SCREEN_WIDTH = 800;
		//		Values.SCREEN_HEIGHT = 640;
		//
		//		float halfWidth = Values.SCREEN_WIDTH / 2;
		//		float halfHeight = Values.SCREEN_HEIGHT / 2;
		//		float frameSize = PhysicsBoundaryBox.FRAME_SIZE;
		//
		//		List<Float> boundaryXValues = Arrays.asList(new Float[] { -(halfWidth + frameSize), -halfWidth, halfWidth,
		//				halfWidth + frameSize });
		//		List<Float> boundaryYValues = Arrays.asList(new Float[] { -(halfHeight + frameSize), -halfHeight, halfHeight,
		//				halfHeight + frameSize });
		//
		//		assertEquals(0, world.getBodyCount());
		//		new PhysicsBoundaryBox(world).create();
		//		assertEquals(4, world.getBodyCount());
		//
		//		Body body;
		//		Iterator<Body> bodyIterator = world.getBodies();
		//
		//		while (bodyIterator.hasNext()) {
		//			body = bodyIterator.next();
		//			List<Fixture> fixtures = body.getFixtureList();
		//			assertEquals(1, fixtures.size());
		//			for (Fixture fixture : fixtures) {
		//				assertEquals(Shape.Type.Polygon, fixture.getType());
		//				PolygonShape shape = (PolygonShape) fixture.getShape();
		//				assertEquals(4, shape.getVertexCount());
		//
		//				Vector2 vertex = new Vector2();
		//				for (int index = 0; index < shape.getVertexCount(); index++) {
		//					shape.getVertex(index, vertex);
		//					vertex = PhysicsWorldConverter.vecBox2dToCat(vertex);
		//
		//					assertTrue(boundaryXValues.contains(vertex.x));
		//					assertTrue(boundaryYValues.contains(vertex.y));
		//				}
		//			}
		//		}
	}
}
