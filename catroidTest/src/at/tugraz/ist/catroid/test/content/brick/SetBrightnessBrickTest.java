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

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.SetBrightnessBrick;

public class SetBrightnessBrickTest extends InstrumentationTestCase {

	private double brightnessValue = 50.1;

	public void testBrightnessEffect() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial brightness value", 1f, sprite.costume.getBrightnessValue());

		SetBrightnessBrick setBrightnessBrick = new SetBrightnessBrick(sprite, brightnessValue);
		setBrightnessBrick.execute();
		assertEquals("Incorrect brightness value after SetBrightnessBrick executed", (float) brightnessValue / 100f,
				sprite.costume.getBrightnessValue());
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
				sprite.costume.getBrightnessValue());
	}
}
