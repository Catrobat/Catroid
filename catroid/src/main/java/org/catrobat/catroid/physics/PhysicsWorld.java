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
package org.catrobat.catroid.physics;

import android.util.Log;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilder;

import java.util.ArrayList;

public class PhysicsWorld {
	static {
		GdxNativesLoader.load();
	}

	private static final String TAG = PhysicsWorld.class.getSimpleName();

	// CATEGORY
	public static final short CATEGORY_NO_COLLISION = 0x0000;
	public static final short CATEGORY_BOUNDARYBOX = 0x0002;
	public static final short CATEGORY_PHYSICSOBJECT = 0x0004;

	// COLLISION_MODE
	public static final short MASK_BOUNDARYBOX = CATEGORY_PHYSICSOBJECT; // collides with physics_objects
	public static final short MASK_PHYSICSOBJECT = ~CATEGORY_BOUNDARYBOX; // collides with everything but not with the boundarybox
	public static final short MASK_TO_BOUNCE = -1; // collides with everything
	public static final short MASK_NO_COLLISION = 0; // collides with NOBODY

	public static final float ACTIVE_AREA_WIDTH_FACTOR = 3.0f;
	public static final float ACTIVE_AREA_HEIGHT_FACTOR = 2.0f;

	public static final float RATIO = 10.0f;
	public static final int VELOCITY_ITERATIONS = 3;
	public static final int POSITION_ITERATIONS = 3;

	public static final Vector2 DEFAULT_GRAVITY = new Vector2(0.0f, -10.0f);
	public static final boolean IGNORE_SLEEPING_OBJECTS = false;
	public static Vector2 activeArea;

	public static final int STABILIZING_STEPS = 6;
	private final World world = new World(PhysicsWorld.DEFAULT_GRAVITY, PhysicsWorld.IGNORE_SLEEPING_OBJECTS);
	private final ArrayList<Sprite> activeVerticalBounces = new ArrayList<Sprite>();
	private final ArrayList<Sprite> activeHorizontalBounces = new ArrayList<Sprite>();
	private Box2DDebugRenderer renderer;
	private int stabilizingSteCounter = 0;
	private PhysicsBoundaryBox boundaryBox;

	private PhysicsShapeBuilder physicsShapeBuilder = PhysicsShapeBuilder.getInstance();

	public PhysicsWorld() {
		this(ScreenValues.SCREEN_WIDTH, ScreenValues.SCREEN_HEIGHT);
	}

	public PhysicsWorld(int width, int height) {
		boundaryBox = new PhysicsBoundaryBox(world);
		boundaryBox.create(width, height);
		activeArea = new Vector2(width * ACTIVE_AREA_WIDTH_FACTOR, height * ACTIVE_AREA_HEIGHT_FACTOR);
		world.setContactListener(new PhysicsCollision(this));
	}

	public void setBounceOnce(Sprite sprite, PhysicsBoundaryBox.BoundaryBoxIdentifier boundaryBoxIdentifier) {
		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		physicsProperties.setIfOnEdgeBounce(true, sprite);
		switch (boundaryBoxIdentifier) {
			case BBI_HORIZONTAL:
				activeHorizontalBounces.add(sprite);
				break;
			case BBI_VERTICAL:
				activeVerticalBounces.add(sprite);
				break;
		}
	}

	public void step(float deltaTime) {
		if (stabilizingSteCounter < STABILIZING_STEPS) {
			stabilizingSteCounter++;
		} else {
			try {
				world.step(deltaTime, PhysicsWorld.VELOCITY_ITERATIONS, PhysicsWorld.POSITION_ITERATIONS);
			} catch (Exception exception) {
				Log.e(TAG, Log.getStackTraceString(exception));
			}
		}
	}

	public void render(Matrix4 perspectiveMatrix) {
		if (renderer == null) {
			renderer = new Box2DDebugRenderer(PhysicsDebugSettings.Render.RENDER_BODIES,
					PhysicsDebugSettings.Render.RENDER_JOINTS, PhysicsDebugSettings.Render.RENDER_AABB,
					PhysicsDebugSettings.Render.RENDER_INACTIVE_BODIES, PhysicsDebugSettings.Render.RENDER_VELOCITIES,
					PhysicsDebugSettings.Render.RENDER_CONTACTS);
		}
		renderer.render(world, perspectiveMatrix.scl(PhysicsWorld.RATIO));
	}

	public void setGravity(float x, float y) {
		world.setGravity(new Vector2(x, y));
	}

	public Vector2 getGravity() {
		return world.getGravity();
	}

	public void changeLook(PhysicsProperties physicsProperties, Look look) {
		Shape[] shapes = null;
		if (look.getLookData() != null && look.getLookData().getLookFileName() != null) {
			shapes = physicsShapeBuilder.getScaledShapes(look.getLookData(),
					look.getSizeInUserInterfaceDimensionUnit() / 100f);
		}
		physicsProperties.setShape(shapes);
	}

	public void bouncedOnEdge(Sprite sprite, PhysicsBoundaryBox.BoundaryBoxIdentifier boundaryBoxIdentifier) {
		PhysicsProperties physicsProperties = sprite.getPhysicsProperties();
		switch (boundaryBoxIdentifier) {
			case BBI_HORIZONTAL:
				if (activeHorizontalBounces.remove(sprite) && !activeVerticalBounces.contains(sprite)) {
					physicsProperties.setIfOnEdgeBounce(false, sprite);
					PhysicsCollisionBroadcast.fireEvent(PhysicsCollision.generateBroadcastMessage(sprite.getName(),
							PhysicsCollision.COLLISION_WITH_ANYTHING_IDENTIFIER));
				}
				break;
			case BBI_VERTICAL:
				if (activeVerticalBounces.remove(sprite) && !activeHorizontalBounces.contains(sprite)) {
					physicsProperties.setIfOnEdgeBounce(false, sprite);
					PhysicsCollisionBroadcast.fireEvent(PhysicsCollision.generateBroadcastMessage(sprite.getName(),
							PhysicsCollision.COLLISION_WITH_ANYTHING_IDENTIFIER));
				}
				break;
		}
	}

	public Body createBody() {
		BodyDef bodyDef = new BodyDef();
		return world.createBody(bodyDef);
	}
}
