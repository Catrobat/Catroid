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
import org.catrobat.catroid.content.actions.ChangeBrightnessByNAction;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;

import android.test.AndroidTestCase;

public class ChangeBrightnessByNActionTest extends AndroidTestCase {

	private final Formula brighter = new Formula(50.5f);
	private final Formula dimmer = new Formula(-20.8f);

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite brightness value", 1f, sprite.look.getBrightness());

		float brightness = sprite.look.getBrightness();
		brightness += brighter.interpretFloat(sprite) / 100f;

		ChangeBrightnessByNAction action1 = ExtendedActions.changeBrightnessByN(sprite, brighter);
		sprite.look.addAction(action1);
		action1.act(1.0f);
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessByNBrick executed", brightness,
				sprite.look.getBrightness());

		brightness = sprite.look.getBrightness();
		brightness += dimmer.interpretFloat(sprite) / 100f;

		ChangeBrightnessByNAction action2 = ExtendedActions.changeBrightnessByN(sprite, dimmer);
		sprite.look.addAction(action2);
		action2.act(1.0f);
		assertEquals("Incorrect sprite brightness value after ChangeBrightnessByNBrick executed", brightness,
				sprite.look.getBrightness());
	}

	public void testNullSprite() {
		ChangeBrightnessByNAction action = ExtendedActions.changeBrightnessByN(null, brighter);
		try {
			action.act(1.0f);
			fail("Execution of ChangeBrightnessByNBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
		}
	}
}
