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
package org.catrobat.catroid.test.content.brick;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.ChangeGhostEffectByNBrick;

import android.test.AndroidTestCase;

public class ChangeGhostEffectByNBrickTest extends AndroidTestCase {

	private final float increaseGhostEffect = 1f;
	private final float decreaseGhostEffect = -0.1f;

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite ghost effect value", 1f, sprite.look.getAlphaValue());

		float ghostEffect = sprite.look.getAlphaValue();
		ghostEffect -= increaseGhostEffect;

		ChangeGhostEffectByNBrick changeGhostEffectByNBrick1 = new ChangeGhostEffectByNBrick(sprite,
				increaseGhostEffect * 100);
		//		changeGhostEffectByNBrick1.execute();
		assertEquals("Incorrect sprite ghost effect value after ChangeGhostEffectByNBrick executed", ghostEffect,
				sprite.look.getAlphaValue());

		ghostEffect = sprite.look.getAlphaValue();
		ghostEffect -= decreaseGhostEffect;
		ChangeGhostEffectByNBrick changeGhostEffectByNBrick2 = new ChangeGhostEffectByNBrick(sprite,
				decreaseGhostEffect * 100);
		//		changeGhostEffectByNBrick2.execute();
		assertEquals("Incorrect sprite ghost effect value after ChangeGhostEffectByNBrick executed", ghostEffect,
				sprite.look.getAlphaValue());
	}

	public void testNullSprite() {
		ChangeGhostEffectByNBrick brick = new ChangeGhostEffectByNBrick(null, increaseGhostEffect);
		try {
			//			brick.execute();
			fail("Execution of ChangeGhostEffectByNBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

}
