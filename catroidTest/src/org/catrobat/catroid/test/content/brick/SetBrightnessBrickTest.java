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
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;

import android.test.InstrumentationTestCase;

public class SetBrightnessBrickTest extends InstrumentationTestCase {

	private double brightnessValue = 50.1;

	public void testBrightnessEffect() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial brightness value", 1f, sprite.look.getBrightnessValue());

		SetBrightnessBrick setBrightnessBrick = new SetBrightnessBrick(sprite, brightnessValue);
		setBrightnessBrick.execute();
		assertEquals("Incorrect brightness value after SetBrightnessBrick executed", (float) brightnessValue / 100f,
				sprite.look.getBrightnessValue());
	}

	public void testNullSprite() {
		SetBrightnessBrick setBrightnessBrick = new SetBrightnessBrick(null, brightnessValue);

		try {
			setBrightnessBrick.execute();
			fail("Execution of SetGhostEffectBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

	public void testNegativeBrightnessValue() {
		Sprite sprite = new Sprite("testSprite");
		SetBrightnessBrick setBrightnessBrick = new SetBrightnessBrick(sprite, -brightnessValue);
		setBrightnessBrick.execute();
		assertEquals("Incorrect sprite scale value after SetGhostEffectBrick executed", 0f,
				sprite.look.getBrightnessValue());
	}
}
