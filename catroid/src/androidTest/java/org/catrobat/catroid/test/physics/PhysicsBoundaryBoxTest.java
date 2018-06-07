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
		assertEquals(5, PhysicsBoundaryBox.FRAME_SIZE);
		assertEquals(0x0004, PhysicsWorld.MASK_BOUNDARYBOX);
		assertEquals(0x0002, PhysicsWorld.CATEGORY_BOUNDARYBOX);
	}

	public void testProperties() {
		assertEquals(0, world.getBodyCount());
		new PhysicsBoundaryBox(world).create(40, 40);
		assertEquals(4, world.getBodyCount());

		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);
		assertEquals(4, bodies.size);
		for (Body body : bodies) {
			assertEquals(BodyType.StaticBody, body.getType());

			Array<Fixture> fixtures = body.getFixtureList();
			assertEquals(1, fixtures.size);
			for (Fixture fixture : fixtures) {
				Filter filter = fixture.getFilterData();
				assertEquals(PhysicsWorld.MASK_BOUNDARYBOX, filter.maskBits);
				assertEquals(PhysicsWorld.CATEGORY_BOUNDARYBOX, filter.categoryBits);
			}
		}
	}
}
