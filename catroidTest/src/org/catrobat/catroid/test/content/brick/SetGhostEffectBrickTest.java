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
import org.catrobat.catroid.content.bricks.SetGhostEffectBrick;

import android.test.InstrumentationTestCase;

public class SetGhostEffectBrickTest extends InstrumentationTestCase {

	private double effectValue = 50.5;

	public void testGhostEffect() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite scale value", 1f, sprite.costume.getAlphaValue());

		SetGhostEffectBrick setGhostEffectBrick = new SetGhostEffectBrick(sprite, effectValue);
		setGhostEffectBrick.execute();
		assertEquals("Incorrect sprite scale value after SetGhostEffectBrick executed",
				(100 - (float) effectValue) / 100, sprite.costume.getAlphaValue());
	}

	public void testNullSprite() {
		SetGhostEffectBrick setGhostEffectBrick = new SetGhostEffectBrick(null, effectValue);

		try {
			setGhostEffectBrick.execute();
			fail("Execution of SetGhostEffectBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}
}
