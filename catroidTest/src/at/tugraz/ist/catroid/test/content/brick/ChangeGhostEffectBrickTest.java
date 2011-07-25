/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.ChangeGhostEffectBrick;

public class ChangeGhostEffectBrickTest extends AndroidTestCase {

	private final double increaseGhostEffect = 100.0;
	private final double decreaseGhostEffect = -10.0;

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite ghost effect value", 0.0, sprite.getGhostEffectValue());

		double ghostEffect = sprite.getGhostEffectValue();
		ghostEffect += increaseGhostEffect;

		ChangeGhostEffectBrick brick1 = new ChangeGhostEffectBrick(sprite, increaseGhostEffect);
		brick1.execute();
		assertEquals("Incorrect sprite ghost effect value after ChangeGhostEffectBrick executed", ghostEffect,
				sprite.getGhostEffectValue());

		ghostEffect = sprite.getGhostEffectValue();
		ghostEffect += decreaseGhostEffect;

		ChangeGhostEffectBrick brick2 = new ChangeGhostEffectBrick(sprite, decreaseGhostEffect);
		brick2.execute();
		assertEquals("Incorrect sprite ghost effect value after ChangeGhostEffectBrick executed", ghostEffect,
				sprite.getGhostEffectValue());
	}

	public void testNullSprite() {
		ChangeGhostEffectBrick brick = new ChangeGhostEffectBrick(null, increaseGhostEffect);
		try {
			brick.execute();
			fail("Execution of ChangeGhostEffectBrick with null Sprite did not cause a "
					+ "NullPointerException to be thrown");
		} catch (NullPointerException e) {
			// expected behavior
		}
	}

}
