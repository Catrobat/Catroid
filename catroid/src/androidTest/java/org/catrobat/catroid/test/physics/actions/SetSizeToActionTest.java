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
package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.test.physics.PhysicsTestRule;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SetSizeToActionTest {

	private PhysicsLook physicsLook;
	private PhysicsObject physicsObject;
	public static final float SIZE_COMPARISON_DELTA = 1.0f;

	@Rule
	public PhysicsTestRule rule = new PhysicsTestRule();

	private Sprite sprite;

	@Before
	public void setUp() throws Exception {
		sprite = rule.sprite;
		this.physicsLook = (PhysicsLook) sprite.look;
		this.physicsObject = (PhysicsObject) Reflection.getPrivateField(physicsLook, "physicsObject");
	}

	@Test
	public void testSizeLarger() {
		Vector2 oldAabbDimensions = physicsObject.getBoundaryBoxDimensions();
		float oldCircumference = physicsObject.getCircumference();
		float scaleFactor = 500.0f;
		performSetSizeToAction(scaleFactor);

		Vector2 newAabbDimensions = physicsObject.getBoundaryBoxDimensions();
		float newCircumference = physicsObject.getCircumference();
		assertEquals(oldAabbDimensions.x * (scaleFactor / 100.0f), newAabbDimensions.x, SIZE_COMPARISON_DELTA * scaleFactor / 100f);
		assertEquals(oldAabbDimensions.y * (scaleFactor / 100.0f), newAabbDimensions.y, SIZE_COMPARISON_DELTA * scaleFactor / 100f);
		assertEquals(oldCircumference * (scaleFactor / 100.0f), newCircumference, SIZE_COMPARISON_DELTA * scaleFactor / 100f);
	}

	@Test
	public void testSizeSmaller() {
		float smallerSizeComparisonDelta = 1.5f;
		Vector2 oldAabbDimensions = physicsObject.getBoundaryBoxDimensions();
		float oldCircumference = physicsObject.getCircumference();
		float scaleFactor = 10.0f;
		performSetSizeToAction(scaleFactor);

		Vector2 newAabbDimensions = physicsObject.getBoundaryBoxDimensions();
		float newCircumference = physicsObject.getCircumference();
		assertEquals(oldAabbDimensions.x * (scaleFactor / 100.0f), newAabbDimensions.x, smallerSizeComparisonDelta);
		assertEquals(oldAabbDimensions.y * (scaleFactor / 100.0f), newAabbDimensions.y, smallerSizeComparisonDelta);
		assertEquals(oldCircumference * (scaleFactor / 100.0f), newCircumference, smallerSizeComparisonDelta);
	}

	@Test
	public void testSizeSame() {
		Vector2 oldAabbDimensions = physicsObject.getBoundaryBoxDimensions();
		float oldCircumference = physicsObject.getCircumference();
		float scaleFactor = 100.0f;
		performSetSizeToAction(scaleFactor);

		Vector2 newAabbDimensions = physicsObject.getBoundaryBoxDimensions();
		float newCircumference = physicsObject.getCircumference();
		assertEquals(oldAabbDimensions.x, newAabbDimensions.x, TestUtils.DELTA);
		assertEquals(oldAabbDimensions.y, newAabbDimensions.y, TestUtils.DELTA);
		assertEquals(oldCircumference, newCircumference, TestUtils.DELTA);
	}

	@Test
	public void testSizeSmallerAndOriginal() {
		Vector2 oldAabbDimensions = physicsObject.getBoundaryBoxDimensions();
		float oldCircumference = physicsObject.getCircumference();
		float scaleFactor = 25.0f;
		performSetSizeToAction(scaleFactor);
		scaleFactor = 100.0f;
		performSetSizeToAction(scaleFactor);

		Vector2 newAabbDimensions = physicsObject.getBoundaryBoxDimensions();
		float newCircumference = physicsObject.getCircumference();
		assertEquals(oldAabbDimensions.x, newAabbDimensions.x, TestUtils.DELTA);
		assertEquals(oldAabbDimensions.y, newAabbDimensions.y, TestUtils.DELTA);
		assertEquals(oldCircumference, newCircumference, TestUtils.DELTA);
	}

	@Test
	public void testSizeZero() {
		float scaleFactor = 0.0f;
		performSetSizeToAction(scaleFactor);

		Vector2 newAabbDimensions = physicsObject.getBoundaryBoxDimensions();
		float newCircumference = physicsObject.getCircumference();
		assertEquals(1, newAabbDimensions.x, TestUtils.DELTA);
		assertEquals(1, newAabbDimensions.y, TestUtils.DELTA);
		assertEquals(0.0f, newCircumference, TestUtils.DELTA);
	}

	private void performSetSizeToAction(float scaleFactor) {
		sprite.getActionFactory().createSetSizeToAction(sprite, new SequenceAction(),
				new Formula(scaleFactor)).act(1.0f);
	}
}
