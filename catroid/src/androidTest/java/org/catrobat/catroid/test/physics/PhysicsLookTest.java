/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilder;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class PhysicsLookTest {

	private PhysicsWorld physicsWorld;
	private final String projectName = "testProject";
	private File testImage;
	private String testImageFilename;
	private Sprite sprite;

	static {
		GdxNativesLoader.load();
	}

	@Before
	public void setUp() throws Exception {
		physicsWorld = new PhysicsWorld(1920, 1600);
		File projectDir = new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName);
		if (projectDir.exists()) {
			StorageOperations.deleteDir(projectDir);
		}
		testImageFilename = PhysicsTestUtils.getInternalImageFilenameFromFilename("testImage.png");
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);

		testImage = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				R.raw.multible_mixed_polygons,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				testImageFilename,
				1);

		sprite = new SingleSprite("TestSprite");
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(projectName);
		physicsWorld = null;
		sprite = null;
	}

	@Test
	public void testShapeComputationOfLook() {
		PhysicsShapeBuilder physicsShapeBuilder = PhysicsShapeBuilder.getInstance();

		LookData lookData = new LookData();
		lookData.setFile(testImage);
		lookData.setName(testImage.getName());
		sprite.getLookList().add(lookData);
		Pixmap pixmap = null;
		pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);

		Shape[] shapes = physicsShapeBuilder.getScaledShapes(lookData, sprite.look.getSizeInUserInterfaceDimensionUnit() / 100f);

		assertThat(shapes.length, is(greaterThan(0)));
		physicsShapeBuilder.reset();
	}

	@Test
	public void testPositionAndAngle() {
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		PhysicsLook physicsLook = new PhysicsLook(sprite, physicsWorld);

		float x = 1.2f;
		physicsLook.setX(x);
		assertEquals(x, physicsObject.getX());
		assertEquals(x, physicsLook.getX());

		float y = -3.4f;
		physicsLook.setY(y);
		assertEquals(y, physicsObject.getY());
		assertEquals(y, physicsLook.getY());

		x = 5.6f;
		y = 7.8f;
		physicsLook.setPosition(x, y);
		assertEquals(new Vector2(x, y), physicsObject.getPosition());
		assertEquals(x, physicsLook.getX());
		assertEquals(y, physicsLook.getY());

		float rotation = 9.0f;
		physicsLook.setRotation(rotation);
		assertEquals(rotation, physicsObject.getDirection());

		assertEquals(x, physicsLook.getX());
		assertEquals(y, physicsLook.getY());
		assertEquals(rotation, physicsLook.getRotation());
	}

	@Test
	public void testSetScale() throws Exception {
		LookData lookData = new LookData();
		lookData.setFile(testImage);
		lookData.setName(testImageFilename);
		sprite.getLookList().add(lookData);
		Pixmap pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);

		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		PhysicsLook physicsLook = new PhysicsLook(sprite, physicsWorld);
		physicsLook.setLookData(lookData);

		float testScaleFactor = 1.1f;

		Vector2[] expectedVertices = new Vector2[] {
			new Vector2(10.84f, -7.31f),
			new Vector2(10.84f, -0.6f),
			new Vector2(9.63f, 10.62f),
			new Vector2(-10.84f, 10.62f),
			new Vector2(-10.84f, 6.44f),
			new Vector2(-3.35f, -7.31f),
			new Vector2(-0.06f, -10.61f),
			new Vector2(7.54f, -10.61f),
		};

		physicsLook.setScale(testScaleFactor, testScaleFactor);
		Shape[] scaledShapes = (Shape[]) Reflection.getPrivateField(physicsObject, "shapes");
		assertNotNull(scaledShapes);
		assertEquals(1, scaledShapes.length);

		Shape scaledShape = scaledShapes[0];
		assertEquals(Shape.Type.Polygon, scaledShape.getType());

		int scaledVertexCount = ((PolygonShape) scaledShape).getVertexCount();
		assertEquals(8, scaledVertexCount);
		Vector2[] scaledVertices = new Vector2[8];
		for (int idx = 0; idx < scaledVertexCount; idx++) {
			scaledVertices[idx] = new Vector2();
			((PolygonShape) scaledShape).getVertex(idx, scaledVertices[idx]);
		}
		assertArrayEquals(expectedVertices, scaledVertices);
	}

	@Test
	public void testSetLookDataWithNullPixmap() {
		LookData lookData = new LookData();
		lookData.setFile(testImage);
		lookData.setName(testImage.getName());

		sprite.look = new PhysicsLook(sprite, physicsWorld);
		sprite.look.setLookData(lookData);
	}

	@Test
	public void testDefaultValueEqualityOfPhysicsLookAndLook() {
		PhysicsLook physicsLook = new PhysicsLook(sprite, physicsWorld);
		Look look = new Look(sprite);

		assertEquals(physicsLook.getAngularVelocityInUserInterfaceDimensionUnit(),
				look.getAngularVelocityInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getXVelocityInUserInterfaceDimensionUnit(),
				look.getXVelocityInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getYVelocityInUserInterfaceDimensionUnit(),
				look.getYVelocityInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getX(), look.getX());

		assertEquals(physicsLook.getY(), look.getY());

		assertEquals(physicsLook.getRotation(), look.getRotation());

		assertEquals(physicsLook.getLookData(), look.getLookData());

		assertEquals(physicsLook.haveAllThreadsFinished(), look.haveAllThreadsFinished());

		assertEquals(physicsLook.getImagePath(), look.getImagePath());

		assertEquals(physicsLook.getXInUserInterfaceDimensionUnit(), look.getXInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getYInUserInterfaceDimensionUnit(), look.getYInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getAngularVelocityInUserInterfaceDimensionUnit(),
				look.getAngularVelocityInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getXVelocityInUserInterfaceDimensionUnit(),
				look.getXVelocityInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getYVelocityInUserInterfaceDimensionUnit(),
				look.getYVelocityInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getWidthInUserInterfaceDimensionUnit(),
				look.getWidthInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getHeightInUserInterfaceDimensionUnit(),
				look.getHeightInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getDirectionInUserInterfaceDimensionUnit(),
				look.getDirectionInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getSizeInUserInterfaceDimensionUnit(),
				look.getSizeInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getTransparencyInUserInterfaceDimensionUnit(),
				look.getTransparencyInUserInterfaceDimensionUnit());

		assertEquals(physicsLook.getBrightnessInUserInterfaceDimensionUnit(),
				look.getBrightnessInUserInterfaceDimensionUnit());
	}

	@Test
	public void testCloneValues() {
		PhysicsWorld world = new PhysicsWorld();

		Sprite originSprite = new Sprite("Origin");
		PhysicsLook originLook = new PhysicsLook(originSprite, world);
		PhysicsObject originPhysicsObject = world.getPhysicsObject(originSprite);

		Sprite cloneSprite = new Sprite("Clone");
		PhysicsLook cloneLook = new PhysicsLook(cloneSprite, world);
		PhysicsObject clonePhysicsObject = world.getPhysicsObject(cloneSprite);

		originLook.setXInUserInterfaceDimensionUnit(10);
		originLook.setBrightnessInUserInterfaceDimensionUnit(32);
		originPhysicsObject.setMass(10);

		originLook.copyTo(cloneLook);

		assertEquals(originLook.getXInUserInterfaceDimensionUnit(), cloneLook.getXInUserInterfaceDimensionUnit());
		assertEquals(originLook.getBrightnessInUserInterfaceDimensionUnit(), cloneLook.getBrightnessInUserInterfaceDimensionUnit());
		assertEquals(originPhysicsObject.getMass(), clonePhysicsObject.getMass());
	}
}
