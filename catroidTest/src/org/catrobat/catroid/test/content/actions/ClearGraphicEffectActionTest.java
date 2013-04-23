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
package org.catrobat.catroid.test.content.actions;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ClearGraphicEffectAction;
import org.catrobat.catroid.content.actions.ExtendedActions;

import android.test.AndroidTestCase;

public class ClearGraphicEffectActionTest extends AndroidTestCase {

	public void testClearGraphicEffect() {
		float value = 0.8f;
		Sprite sprite = new Sprite("new sprite");
		sprite.look.setAlphaValue(value);
		assertEquals("Look hasn't ghost effect.", value, sprite.look.getAlphaValue());
		sprite.look.setBrightness(value);
		assertEquals("Look hasn't brightness effect.", value, sprite.look.getBrightness());

		ClearGraphicEffectAction action = ExtendedActions.clearGraphicEffect(sprite);
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("Look's ghost effect is removed.", 1f, sprite.look.getAlphaValue());
		assertEquals("Look's brightness effect is removed.", 1f, sprite.look.getBrightness());
	}

	public void testNullSprite() {
		ClearGraphicEffectAction action = ExtendedActions.clearGraphicEffect(null);
		try {
			action.act(1.0f);
			fail("Execution of ClearGraphicEffectBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}
}
