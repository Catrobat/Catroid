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
import org.catrobat.catroid.content.actions.ChangeGhostEffectByNAction;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;

import android.test.AndroidTestCase;

public class ChangeGhostEffectByNActionTest extends AndroidTestCase {

	private final Formula increaseGhostEffect = new Formula(1f);
	private final Formula decreaseGhostEffect = new Formula(-0.1f);

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite ghost effect value", 1f, sprite.look.getAlphaValue());

		float ghostEffect = sprite.look.getAlphaValue();
		ghostEffect -= increaseGhostEffect.interpretDouble(sprite);

		ChangeGhostEffectByNAction action1 = ExtendedActions.changeGhostEffectByN(sprite, new Formula(
				increaseGhostEffect.interpretDouble(sprite) * 100.0f));
		sprite.look.addAction(action1);
		action1.act(1.0f);
		assertEquals("Incorrect sprite ghost effect value after ChangeGhostEffectByNBrick executed", ghostEffect,
				sprite.look.getAlphaValue());

		ghostEffect = sprite.look.getAlphaValue();
		ghostEffect -= decreaseGhostEffect.interpretDouble(sprite);

		ChangeGhostEffectByNAction action2 = ExtendedActions.changeGhostEffectByN(sprite, new Formula(
				decreaseGhostEffect.interpretDouble(sprite) * 100.0f));
		sprite.look.addAction(action2);
		action2.act(1.0f);
		assertEquals("Incorrect sprite ghost effect value after ChangeGhostEffectByNBrick executed", ghostEffect,
				sprite.look.getAlphaValue());
	}

	public void testNullSprite() {
		ChangeGhostEffectByNAction action = ExtendedActions.changeGhostEffectByN(null, increaseGhostEffect);
		try {
			action.act(1.0f);
			fail("Execution of ChangeGhostEffectByNBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

}
