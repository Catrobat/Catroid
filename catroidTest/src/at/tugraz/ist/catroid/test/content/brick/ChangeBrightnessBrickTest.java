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
import at.tugraz.ist.catroid.content.bricks.ChangeBrightnessBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeGhostEffectBrick;

public class ChangeBrightnessBrickTest extends AndroidTestCase {

	private final double brighter = 50.5;
	private final double dimmer = -20.8;

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite brightness value", 0.0, sprite.getBrightnessValue());

		double brightness = sprite.getBrightnessValue();
		brightness += brighter;

		ChangeBrightnessBrick brick1 = new ChangeBrightnessBrick(sprite, brighter);
		brick1.execute();
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessBrick executed", brightness,
				sprite.getBrightnessValue());

		brightness = sprite.getBrightnessValue();
		brightness += dimmer;

		ChangeBrightnessBrick brick2 = new ChangeBrightnessBrick(sprite, dimmer);
		brick2.execute();
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessBrick executed", brightness,
				sprite.getBrightnessValue());
	}

	public void testNullSprite() {
		ChangeGhostEffectBrick brick = new ChangeGhostEffectBrick(null, brighter);
		try {
			brick.execute();
			fail("Execution of ChangeBrightnessBrick with null Sprite did not cause a "
					+ "NullPointerException to be thrown");
		} catch (NullPointerException e) {
			// expected behavior
		}
	}
}
