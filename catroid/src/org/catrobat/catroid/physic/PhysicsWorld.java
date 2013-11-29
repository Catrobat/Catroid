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
package org.catrobat.catroid.physic;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physic.shapebuilder.PhysicsShapeBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PhysicsWorld {
	static {
		GdxNativesLoader.load();
	}

	public final static float RATIO = 40.0f;
	public final static int VELOCITY_ITERATIONS = 8;
	public final static int POSITION_ITERATIONS = 3;

	public final static Vector2 DEFAULT_GRAVITY = new Vector2(0.0f, -10.0f);
	public final static boolean IGNORE_SLEEPING_OBJECTS = false;

	public final static int STABILIZING_STEPS = 6;

	private final World world = new World(PhysicsWorld.DEFAULT_GRAVITY, PhysicsWorld.IGNORE_SLEEPING_OBJECTS);
	private final Map<Sprite, PhysicsObject> physicsObjects = new HashMap<Sprite, PhysicsObject>();
	private Box2DDebugRenderer renderer;
	private int stabilizingStep = 0;

	private PhysicsShapeBuilder physicsShapeBuilder = new PhysicsShapeBuilder();

	public PhysicsWorld(int width, int height) {
		new PhysicsBoundaryBox(world).create(width, height);
	}

	public void step(float deltaTime) {
		if (stabilizingStep < STABILIZING_STEPS) {
			stabilizingStep++;
		} else {
			try {
				world.step(deltaTime, PhysicsWorld.VELOCITY_ITERATIONS, PhysicsWorld.POSITION_ITERATIONS);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		updateSprites();
	}

	private void updateSprites() {
		PhysicsObject physicsObject;
		Look look;
		for (Entry<Sprite, PhysicsObject> entry : physicsObjects.entrySet()) {
			physicsObject = entry.getValue();
			physicsObject.setIfOnEdgeBounce(false);

			look = entry.getKey().look;
			look.setXInUserInterfaceDimensionUnit(physicsObject.getX());
			look.setYInUserInterfaceDimensionUnit(physicsObject.getY());
			look.setDirectionInUserInterfaceDimensionUnit(physicsObject.getDirection());
		}
	}

	public void render(Matrix4 perspectiveMatrix) {
		if (renderer == null) {
			renderer = new Box2DDebugRenderer(PhysicsDebugSettings.Render.RENDER_BODIES,
					PhysicsDebugSettings.Render.RENDER_JOINTS, PhysicsDebugSettings.Render.RENDER_AABBs,
					PhysicsDebugSettings.Render.RENDER_INACTIVE_BODIES, PhysicsDebugSettings.Render.RENDER_VELOCITIES);
		}
		renderer.render(world, perspectiveMatrix.scl(PhysicsWorld.RATIO));
	}

	public void setGravity(float x, float y) {
		world.setGravity(new Vector2(x, y));
	}

	public void changeLook(PhysicsObject physicsObject, Look look) {
		physicsObject.setShape(physicsShapeBuilder.getShape(look.getLookData(),
				look.getSizeInUserInterfaceDimensionUnit() / 100f));
	}

	public PhysicsObject getPhysicObject(Sprite sprite) {
		if (sprite == null) {
			throw new NullPointerException();
		}

		if (physicsObjects.containsKey(sprite)) {
			return physicsObjects.get(sprite);
		}

		PhysicsObject physicsObject = createPhysicObject();
		physicsObjects.put(sprite, physicsObject);
		changeLook(physicsObject, sprite.look);

		return physicsObject;
	}

	private PhysicsObject createPhysicObject() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.bullet = true;

		return new PhysicsObject(world.createBody(bodyDef));
	}
}
