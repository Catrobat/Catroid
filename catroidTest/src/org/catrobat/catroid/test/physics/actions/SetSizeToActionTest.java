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
package org.catrobat.catroid.test.physics.actions;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.SetSizeToAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsWorld;

public class SetSizeToActionTest extends PhysicsActionTestCase {

	private class PhysicsLookMock extends PhysicsLook {
		private float scale = Float.MIN_VALUE;

		public PhysicsLookMock(Sprite sprite, PhysicsWorld physicsWorld) {
			super(sprite, physicsWorld);
		}

		@Override
		public void setScale(float scaleX, float scaleY) {
			// scaleX and scaleY have same value if called via SetSizeToAction
			scale = scaleX;
		}

		public float getScale() {
			return this.scale;
		}
	}

	public void testSizeLarger() {
		float targetSize = 150f;
		Formula sizeFormula = new Formula(targetSize);
		PhysicsLookMock mockLook = new PhysicsLookMock(sprite, physicsWorld);

		sprite.look = mockLook;
		SetSizeToAction setSizeToAction = new SetSizeToAction();
		setSizeToAction.setSprite(sprite);
		setSizeToAction.setSize(sizeFormula);

		setSizeToAction.act(1f);
		float targetScale = targetSize/100f;
		float floatThreshold = 0.05f;
		assertTrue("Size is not being set to correct scale", (targetScale - mockLook.getScale()) < floatThreshold);
	}
}
