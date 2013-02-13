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
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ChangeGhostEffectByNBrick;

import android.test.AndroidTestCase;

public class ChangeBrightnessByNBrickTest extends AndroidTestCase {

	private final float brighter = 50.5f;
	private final float dimmer = -20.8f;

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite brightness value", 1f, sprite.look.getBrightnessValue());

		float brightness = sprite.look.getBrightnessValue();
		brightness += brighter / 100f;

		ChangeBrightnessByNBrick brick1 = new ChangeBrightnessByNBrick(sprite, brighter);
		brick1.execute();
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessByNBrick executed", brightness,
				sprite.look.getBrightnessValue());

		brightness = sprite.look.getBrightnessValue();
		brightness += dimmer / 100f;

		ChangeBrightnessByNBrick brick2 = new ChangeBrightnessByNBrick(sprite, dimmer);
		brick2.execute();
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessByNBrick executed", brightness,
				sprite.look.getBrightnessValue());
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
