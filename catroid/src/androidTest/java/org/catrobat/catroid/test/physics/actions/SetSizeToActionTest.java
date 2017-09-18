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
package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsProperties;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;
import org.catrobat.catroid.test.utils.TestUtils;

public class SetSizeToActionTest extends PhysicsBaseTest {

	private PhysicsProperties physicsProperties;
	public static final float SIZE_COMPARISON_DELTA = 1.0f;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.physicsProperties = sprite.getPhysicsProperties();
	}

	public void testSizeLarger() {
		Vector2 oldAabbDimensions = physicsProperties.getBoundaryBoxDimensions();
		float oldCircumference = physicsProperties.getCircumference();
		float scaleFactor = 500.0f;
		performSetSizeToAction(scaleFactor);

		Vector2 newAabbDimensions = physicsProperties.getBoundaryBoxDimensions();
		float newCircumference = physicsProperties.getCircumference();
		assertEquals("Size is not being set to correct scale", oldAabbDimensions.x * (scaleFactor / 100.0f), newAabbDimensions.x, SIZE_COMPARISON_DELTA * scaleFactor / 100f);
		assertEquals("Size is not being set to correct scale", oldAabbDimensions.y * (scaleFactor / 100.0f), newAabbDimensions.y, SIZE_COMPARISON_DELTA * scaleFactor / 100f);
		assertEquals("Circumference is not being updated", oldCircumference * (scaleFactor / 100.0f), newCircumference, SIZE_COMPARISON_DELTA * scaleFactor / 100f);
	}

	public void testSizeSmaller() {
		float smallerSizeComparisonDelta = 1.5f;
		Vector2 oldAabbDimensions = physicsProperties.getBoundaryBoxDimensions();
		float oldCircumference = physicsProperties.getCircumference();
		float scaleFactor = 10.0f;
		performSetSizeToAction(scaleFactor);

		Vector2 newAabbDimensions = physicsProperties.getBoundaryBoxDimensions();
		float newCircumference = physicsProperties.getCircumference();
		assertEquals("Size is not being set to correct scale", oldAabbDimensions.x * (scaleFactor / 100.0f), newAabbDimensions.x, smallerSizeComparisonDelta);
		assertEquals("Size is not being set to correct scale", oldAabbDimensions.y * (scaleFactor / 100.0f), newAabbDimensions.y, smallerSizeComparisonDelta);
		assertEquals("Circumference is not being updated", oldCircumference * (scaleFactor / 100.0f), newCircumference, smallerSizeComparisonDelta);
	}

	public void testSizeSame() {
		Vector2 oldAabbDimensions = physicsProperties.getBoundaryBoxDimensions();
		float oldCircumference = physicsProperties.getCircumference();
		float scaleFactor = 100.0f;
		performSetSizeToAction(scaleFactor);

		Vector2 newAabbDimensions = physicsProperties.getBoundaryBoxDimensions();
		float newCircumference = physicsProperties.getCircumference();
		assertEquals("Size is not being set to correct scale", oldAabbDimensions.x, newAabbDimensions.x, TestUtils.DELTA);
		assertEquals("Size is not being set to correct scale", oldAabbDimensions.y, newAabbDimensions.y, TestUtils.DELTA);
		assertEquals("Circumference is not being updated", oldCircumference, newCircumference, TestUtils.DELTA);
	}

	public void testSizeSmallerAndOriginal() {
		Vector2 oldAabbDimensions = physicsProperties.getBoundaryBoxDimensions();
		float oldCircumference = physicsProperties.getCircumference();
		float scaleFactor = 25.0f;
		performSetSizeToAction(scaleFactor);
		scaleFactor = 100.0f;
		performSetSizeToAction(scaleFactor);

		Vector2 newAabbDimensions = physicsProperties.getBoundaryBoxDimensions();
		float newCircumference = physicsProperties.getCircumference();
		assertEquals("Size is not being set to correct scale", oldAabbDimensions.x, newAabbDimensions.x, TestUtils.DELTA);
		assertEquals("Size is not being set to correct scale", oldAabbDimensions.y, newAabbDimensions.y, TestUtils.DELTA);
		assertEquals("Circumference is not being updated", oldCircumference, newCircumference, TestUtils.DELTA);
	}

	public void testSizeZero() {
		float scaleFactor = 0.0f;
		performSetSizeToAction(scaleFactor);

		Vector2 newAabbDimensions = physicsProperties.getBoundaryBoxDimensions();
		float newCircumference = physicsProperties.getCircumference();
		assertEquals("Size is not being set to correct scale", 1, newAabbDimensions.x, TestUtils.DELTA);
		assertEquals("Size is not being set to correct scale", 1, newAabbDimensions.y, TestUtils.DELTA);
		assertEquals("Circumference is not being updated", 0.0f, newCircumference, TestUtils.DELTA);
	}

	private void performSetSizeToAction(float scaleFactor) {
		sprite.getActionFactory().createSetSizeToAction(sprite, new Formula(scaleFactor)).act(1.0f);
	}
}
