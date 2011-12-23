/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.ChangeGhostEffectBrick;

public class ChangeGhostEffectBrickTest extends AndroidTestCase {

	private final float increaseGhostEffect = 1f;
	private final float decreaseGhostEffect = -0.1f;

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite ghost effect value", 1f, sprite.costume.getAlphaValue());

		float ghostEffect = sprite.costume.getAlphaValue();
		ghostEffect -= increaseGhostEffect;

		ChangeGhostEffectBrick changeGhostEffectBrick1 = new ChangeGhostEffectBrick(sprite, increaseGhostEffect * 100);
		changeGhostEffectBrick1.execute();
		assertEquals("Incorrect sprite ghost effect value after ChangeGhostEffectBrick executed", ghostEffect,
				sprite.costume.getAlphaValue());

		ghostEffect = sprite.costume.getAlphaValue();
		ghostEffect -= decreaseGhostEffect;
		ChangeGhostEffectBrick changeGhostEffectBrick2 = new ChangeGhostEffectBrick(sprite, decreaseGhostEffect * 100);
		changeGhostEffectBrick2.execute();
		assertEquals("Incorrect sprite ghost effect value after ChangeGhostEffectBrick executed", ghostEffect,
				sprite.costume.getAlphaValue());
	}

	public void testNullSprite() {
		ChangeGhostEffectBrick brick = new ChangeGhostEffectBrick(null, increaseGhostEffect);
		try {
			brick.execute();
			fail("Execution of ChangeGhostEffectBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

}
