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
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.utils.Reflection;

import android.test.FlakyTest;
import android.test.InstrumentationTestCase;

import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;

public class ForeverActionTest extends InstrumentationTestCase {

	@FlakyTest(tolerance = 3)
	public void testForeverBrick() throws InterruptedException {
		Sprite testSprite = new Sprite("testSprite");
		final Formula deltaY = new Formula(-10);

		RepeatAction action = ExtendedActions.forever(ExtendedActions.sequence(ExtendedActions.changeYByN(testSprite,
				deltaY)));
		int numberOfRepeats = (Integer) Reflection.getPrivateField(action, "repeatCount");
		assertEquals("Executed the wrong number of times!", numberOfRepeats, RepeatAction.FOREVER);
	}
}
