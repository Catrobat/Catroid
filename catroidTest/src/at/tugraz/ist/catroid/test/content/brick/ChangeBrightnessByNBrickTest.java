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
import at.tugraz.ist.catroid.content.bricks.ChangeBrightnessByNBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeGhostEffectByNBrick;

public class ChangeBrightnessByNBrickTest extends AndroidTestCase {

	private final float brighter = 50.5f;
	private final float dimmer = -20.8f;

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite brightness value", 1f, sprite.costume.getBrightnessValue());

		float brightness = sprite.costume.getBrightnessValue();
		brightness += brighter / 100f;

		ChangeBrightnessByNBrick brick1 = new ChangeBrightnessByNBrick(sprite, brighter);
		brick1.execute();
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessByNBrick executed", brightness,
				sprite.costume.getBrightnessValue());

		brightness = sprite.costume.getBrightnessValue();
		brightness += dimmer / 100f;

		ChangeBrightnessByNBrick brick2 = new ChangeBrightnessByNBrick(sprite, dimmer);
		brick2.execute();
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessByNBrick executed", brightness,
				sprite.costume.getBrightnessValue());
	}

	public void testNullSprite() {
		ChangeGhostEffectByNBrick brick = new ChangeGhostEffectByNBrick(null, brighter);
		try {
			brick.execute();
			fail("Execution of ChangeBrightnessByNBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}
}
