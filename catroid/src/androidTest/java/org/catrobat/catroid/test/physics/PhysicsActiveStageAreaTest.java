/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.content.ActionPhysicsFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;

@RunWith(AndroidJUnit4.class)
public class PhysicsActiveStageAreaTest {

	private static final float EXPECTED_CIRCUMFERENCE_125X125 = (float) Math.sqrt(2 * Math.pow(125 / 2f, 2));
	private static final float CIRCUMFERENCE_COMPARISON_DELTA = 1.0f;
	private PhysicsObject physicsObject;
	private PhysicsLook physicsLook;

	@Rule
	public PhysicsTestRule rule = new PhysicsTestRule();

	private Sprite sprite;
	private PhysicsWorld physicsWorld;
	private Project project;

	@Before
	public void setUp() throws Exception {
		sprite = rule.sprite;
		physicsWorld = rule.physicsWorld;
		project = rule.project;
		physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsLook = ((PhysicsLook) sprite.look);
	}

	@Test
	public void testCircumferenceCalculation() {
		assertEquals(EXPECTED_CIRCUMFERENCE_125X125, physicsObject.getCircumference(), CIRCUMFERENCE_COMPARISON_DELTA);
	}

	@Test
	public void testCenteredObjectIsActive() {
		physicsObject.setPosition(0, 0);
		physicsLook.updatePhysicsObjectState(true);
		assertFalse(physicsLook.isHangedUp());
	}

	@Test
	public void testXOutOfBounds() {
		physicsObject.setX(PhysicsWorld.activeArea.x / 2.0f
				+ physicsObject.getCircumference() - 1);
		physicsWorld.step(0.05f);
		physicsLook.updatePhysicsObjectState(true);
		assertFalse(physicsLook.isHangedUp());

		physicsObject.setX(PhysicsWorld.activeArea.x / 2.0f
				+ physicsObject.getCircumference() + 1);
		physicsWorld.step(0.05f);
		physicsLook.updatePhysicsObjectState(true);
		assertTrue(physicsLook.isHangedUp());
	}

	@Test
	public void testYOutOfBounds() {
		physicsObject.setY(PhysicsWorld.activeArea.y / 2.0f
				+ physicsObject.getCircumference() - 1);
		physicsWorld.step(0.05f);
		physicsLook.updatePhysicsObjectState(true);
		assertFalse(physicsLook.isHangedUp());

		physicsObject.setY(PhysicsWorld.activeArea.y / 2.0f
				+ physicsObject.getCircumference() + 1);
		physicsWorld.step(0.05f);
		physicsLook.updatePhysicsObjectState(true);
		assertTrue(physicsLook.isHangedUp());
	}

	@Test
	public void testNegativeXYOutOfBounds() {
		physicsObject.setX(-PhysicsWorld.activeArea.x / 2.0f
				- physicsObject.getCircumference() - 1);
		physicsObject.setY(-PhysicsWorld.activeArea.y / 2.0f
				- physicsObject.getCircumference() - 1);
		physicsWorld.step(0.05f);
		physicsLook.updatePhysicsObjectState(true);
		assertTrue(physicsLook.isHangedUp());
	}

	@Test
	public void testResumeAfterXYHangup() {
		physicsObject.setX(PhysicsWorld.activeArea.x / 2.0f
				+ physicsObject.getCircumference() + 1);
		physicsObject.setY(PhysicsWorld.activeArea.y / 2.0f
				+ physicsObject.getCircumference() + 1);
		physicsWorld.step(0.05f);
		physicsLook.updatePhysicsObjectState(true);
		assertTrue(physicsLook.isHangedUp());

		physicsObject.setPosition(0.0f, 0.0f);
		physicsWorld.step(0.05f);
		physicsLook.updatePhysicsObjectState(true);
		assertFalse(physicsLook.isHangedUp());
	}

	@Test
	public void testSpriteLargerThanActiveAreaHangupAndResume() throws Exception {
		String rectangle8192x8192FileName = PhysicsTestUtils.getInternalImageFilenameFromFilename("rectangle_8192x8192.png");

		File rectangle8192x8192File = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.rectangle_8192x8192,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				rectangle8192x8192FileName,
				1);

		sprite = new Sprite("TestSprite");
		sprite.look = new PhysicsLook(sprite, physicsWorld);
		sprite.setActionFactory(new ActionPhysicsFactory());
		LookData lookdata = PhysicsTestUtils.generateLookData(rectangle8192x8192File);
		sprite.look.setLookData(lookdata);
		physicsWorld.step(0.05f);
		physicsLook.updatePhysicsObjectState(true);
		assertNotNull(sprite.look.getLookData());

		physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsLook = ((PhysicsLook) sprite.look);
		assertFalse(physicsLook.isHangedUp());

		physicsObject.setX(PhysicsWorld.activeArea.x / 2.0f
				+ physicsObject.getCircumference() + 1);
		physicsObject.setY(PhysicsWorld.activeArea.y / 2.0f
				+ physicsObject.getCircumference() + 1);
		physicsWorld.step(0.05f);
		physicsLook.updatePhysicsObjectState(true);
		assertTrue(physicsLook.isHangedUp());

		physicsObject.setPosition(0.0f, 0.0f);
		physicsWorld.step(0.05f);
		physicsLook.updatePhysicsObjectState(true);
		assertFalse(physicsLook.isHangedUp());
	}
}
