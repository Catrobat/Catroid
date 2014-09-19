/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilder;


public class SetSizeToActionTest extends PhysicsBaseTest {


	private PhysicsLook physicsLook;
	private PhysicsObject physicsObject;


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.physicsLook = (PhysicsLook) sprite.look;
		this.physicsObject = (PhysicsObject) Reflection.getPrivateField(physicsLook, "physicsObject");
	}


	public void testSizeLarger() {
		Vector2 oldAABBDimensions = getAABBDimensions();
		float oldCircumference = physicsObject.getCircumference();
		float scaleFactor = 500.0f;
		performSetSizeToAction(scaleFactor);

		Vector2 newAABBDimensions = getAABBDimensions();
		float newCircumference = physicsObject.getCircumference();
		assertEquals("Size is not being set to correct scale", oldAABBDimensions.x * (scaleFactor / 100.0f), newAABBDimensions.x, TestUtils.DELTA);
		assertEquals("Size is not being set to correct scale", oldAABBDimensions.y * (scaleFactor / 100.0f), newAABBDimensions.y, TestUtils.DELTA);
		assertEquals("Circumference is not being updated", oldCircumference * (scaleFactor / 100.0f), newCircumference, TestUtils.DELTA);
	}

	public void testSizeSmaller() {
		Vector2 oldAABBDimensions = getAABBDimensions();
		float oldCircumference = physicsObject.getCircumference();
		float scaleFactor = 10.0f;
		performSetSizeToAction(scaleFactor);

		Vector2 newAABBDimensions = getAABBDimensions();
		float newCircumference = physicsObject.getCircumference();
		assertEquals("Size is not being set to correct scale", oldAABBDimensions.x * (scaleFactor / 100.0f), newAABBDimensions.x, TestUtils.DELTA);
		assertEquals("Size is not being set to correct scale", oldAABBDimensions.y * (scaleFactor / 100.0f), newAABBDimensions.y, TestUtils.DELTA);
		assertEquals("Circumference is not being updated", oldCircumference * (scaleFactor / 100.0f), newCircumference, TestUtils.DELTA);
	}

	public void testSizeSame() {
		Vector2 oldAABBDimensions = getAABBDimensions();
		float oldCircumference = physicsObject.getCircumference();
		float scaleFactor = 100.0f;
		performSetSizeToAction(scaleFactor);

		Vector2 newAABBDimensions = getAABBDimensions();
		float newCircumference = physicsObject.getCircumference();
		assertEquals("Size is not being set to correct scale", oldAABBDimensions.x * (scaleFactor / 100.0f), newAABBDimensions.x, TestUtils.DELTA);
		assertEquals("Size is not being set to correct scale", oldAABBDimensions.y * (scaleFactor / 100.0f), newAABBDimensions.y, TestUtils.DELTA);
		assertEquals("Circumference is not being updated", oldCircumference * (scaleFactor / 100.0f), newCircumference, TestUtils.DELTA);
	}

	public void testSizeSmallerAndOriginal() {
		Vector2 oldAABBDimensions = getAABBDimensions();
		float oldCircumference = physicsObject.getCircumference();
		float scaleFactor = 25.0f;
		performSetSizeToAction(scaleFactor);
		scaleFactor = 100.0f;
		performSetSizeToAction(scaleFactor);

		Vector2 newAABBDimensions = getAABBDimensions();
		float newCircumference = physicsObject.getCircumference();
		assertEquals("Size is not being set to correct scale", oldAABBDimensions.x, newAABBDimensions.x, TestUtils.DELTA);
		assertEquals("Size is not being set to correct scale", oldAABBDimensions.y, newAABBDimensions.y, TestUtils.DELTA);
		assertEquals("Circumference is not being updated", oldCircumference, newCircumference, TestUtils.DELTA);
	}

	public void testSizeZero() {
		float scaleFactor = 0.0f;
		performSetSizeToAction(scaleFactor);

		Vector2 newAABBDimensions = getAABBDimensions();
		float newCircumference = physicsObject.getCircumference();
		assertEquals("Size is not being set to correct scale", 1, newAABBDimensions.x, TestUtils.DELTA);
		assertEquals("Size is not being set to correct scale", 1, newAABBDimensions.y, TestUtils.DELTA);
		assertEquals("Circumference is not being updated", (float) Math.sqrt(2.0f * Math.pow(0.5f , 2.0f)), newCircumference, TestUtils.DELTA);
	}


	private Vector2 getAABBDimensions() {
		Vector2 lowerLeft = new Vector2();
		Vector2 upperRight = new Vector2();
		physicsObject.getBoundaryBox(lowerLeft, upperRight);
		float aabbWidth = Math.abs(upperRight.x - lowerLeft.x) + 1.0f;
		float aabbHeight = Math.abs(upperRight.y - lowerLeft.y) + 1.0f;
		return new Vector2(aabbWidth, aabbHeight);
	}

	private void performSetSizeToAction(float scaleFactor) {
		sprite.getActionFactory().createSetSizeToAction(sprite, new Formula(scaleFactor)).act(1.0f);
	}

}
