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
import org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick;

import android.test.AndroidTestCase;

public class ClearGraphicEffectBrickTest extends AndroidTestCase {

	public void testClearGraphicEffect() {
		float value = 0.8f;
		Sprite sprite = new Sprite("new sprite");
		sprite.look.setAlphaValue(value);
		assertEquals("Look hasn't ghost effect.", value, sprite.look.getAlphaValue());
		sprite.look.setBrightnessValue(value);
		assertEquals("Look hasn't brightness effect.", value, sprite.look.getBrightnessValue());

		ClearGraphicEffectBrick clearGraphicEffectBrick = new ClearGraphicEffectBrick(sprite);
		clearGraphicEffectBrick.execute();
		assertEquals("Look's ghost effect is removed.", 1f, sprite.look.getAlphaValue());
		assertEquals("Look's brightness effect is removed.", 1f, sprite.look.getBrightnessValue());
	}

	public void testNullSprite() {
		ClearGraphicEffectBrick clearGraphicEffectBrick = new ClearGraphicEffectBrick(null);
		try {
			clearGraphicEffectBrick.execute();
			fail("Execution of ClearGraphicEffectBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}
}
