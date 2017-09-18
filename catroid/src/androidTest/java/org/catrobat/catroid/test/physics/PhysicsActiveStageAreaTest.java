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

package org.catrobat.catroid.test.physics;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.physics.PhysicsProperties;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;

import java.io.File;

public class PhysicsActiveStageAreaTest extends PhysicsBaseTest {

	private static final float EXPECTED_CIRCUMFERENCE_125X125 = (float) Math.sqrt(2 * Math.pow(125 / 2f, 2));
	private static final float CIRCUMFERENCE_COMPARISON_DELTA = 1.0f;
	private PhysicsProperties physicsProperties;
	private Look look;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		physicsProperties = sprite.getPhysicsProperties();
		look = sprite.look;
	}

	public void testCircumferenceCalculation() {
		assertEquals("calculated rectangle circumference not equal to expected value",
				EXPECTED_CIRCUMFERENCE_125X125, physicsProperties.getCircumference(), CIRCUMFERENCE_COMPARISON_DELTA);
	}

	public void testCenteredObjectIsActive() {
		physicsProperties.setPosition(0, 0);
		look.updatePhysicsObjectState(true);
		assertTrue("physicsProperties in center of stage is hung up", !look.isHangedUp());
	}

	public void testXOutOfBounds() {
		physicsProperties.setX(PhysicsWorld.activeArea.x / 2.0f
				+ physicsProperties.getCircumference() - 1);
		physicsWorld.step(0.05f);
		look.updatePhysicsObjectState(true);
		assertTrue("physicsProperties inside active area is hung up", !look.isHangedUp());

		physicsProperties.setX(PhysicsWorld.activeArea.x / 2.0f
				+ physicsProperties.getCircumference() + 1);
		physicsWorld.step(0.05f);
		look.updatePhysicsObjectState(true);
		assertTrue("physicsProperties outside active area is not hung up", look.isHangedUp());
	}

	public void testYOutOfBounds() {
		physicsProperties.setY(PhysicsWorld.activeArea.y / 2.0f
				+ physicsProperties.getCircumference() - 1);
		physicsWorld.step(0.05f);
		look.updatePhysicsObjectState(true);
		assertTrue("physicsProperties inside active area is hung up", !look.isHangedUp());

		physicsProperties.setY(PhysicsWorld.activeArea.y / 2.0f
				+ physicsProperties.getCircumference() + 1);
		physicsWorld.step(0.05f);
		look.updatePhysicsObjectState(true);
		assertTrue("physicsProperties outside active area is not hung up", look.isHangedUp());
	}

	public void testNegativeXYOutOfBounds() {
		physicsProperties.setX(-PhysicsWorld.activeArea.x / 2.0f
				- physicsProperties.getCircumference() - 1);
		physicsProperties.setY(-PhysicsWorld.activeArea.y / 2.0f
				- physicsProperties.getCircumference() - 1);
		physicsWorld.step(0.05f);
		look.updatePhysicsObjectState(true);
		assertTrue("physicsProperties outside active area is not hung up", look.isHangedUp());
	}

	public void testResumeAfterXYHangup() {
		physicsProperties.setX(PhysicsWorld.activeArea.x / 2.0f
				+ physicsProperties.getCircumference() + 1);
		physicsProperties.setY(PhysicsWorld.activeArea.y / 2.0f
				+ physicsProperties.getCircumference() + 1);
		physicsWorld.step(0.05f);
		look.updatePhysicsObjectState(true);
		assertTrue("physicsProperties outside active area is not hung up", look.isHangedUp());

		physicsProperties.setPosition(0.0f, 0.0f);
		physicsWorld.step(0.05f);
		look.updatePhysicsObjectState(true);
		assertTrue("physicsProperties inside active area is hung up", !look.isHangedUp());
	}

	public void testSpriteLargerThanActiveAreaHangupAndResume() throws Exception {
		String rectangle8192x8192FileName = PhysicsTestUtils.getInternalImageFilenameFromFilename("rectangle_8192x8192.png");
		int rectangle8192x8192ResID = R.raw.rectangle_8192x8192;
		File rectangle8192x8192File = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, project.getDefaultScene().getName(),
				rectangle8192x8192FileName, rectangle8192x8192ResID, getInstrumentation().getContext(),
				TestUtils.TYPE_IMAGE_FILE);

		sprite = new SingleSprite("TestSprite");
		sprite.look = new Look(sprite);
		sprite.setActionFactory(new ActionFactory());
		sprite.setPhysicsProperties(new PhysicsProperties(physicsWorld.createBody(), sprite));
		LookData lookdata = PhysicsTestUtils.generateLookData(rectangle8192x8192File);
		sprite.look.setLookData(lookdata);
		physicsWorld.step(0.05f);
		look.updatePhysicsObjectState(true);
		assertTrue("getLookData is null", sprite.look.getLookData() != null);

		physicsProperties = sprite.getPhysicsProperties();
		look = sprite.look;
		assertTrue("huge physicsProperties is hung up at start", !look.isHangedUp());

		physicsProperties.setX(PhysicsWorld.activeArea.x / 2.0f
				+ physicsProperties.getCircumference() + 1);
		physicsProperties.setY(PhysicsWorld.activeArea.y / 2.0f
				+ physicsProperties.getCircumference() + 1);
		physicsWorld.step(0.05f);
		look.updatePhysicsObjectState(true);
		assertTrue("physicsProperties outside active area is not hung up", look.isHangedUp());

		physicsProperties.setPosition(0.0f, 0.0f);
		physicsWorld.step(0.05f);
		look.updatePhysicsObjectState(true);
		assertTrue("physicsProperties inside active area is hung up", !look.isHangedUp());
	}
}
