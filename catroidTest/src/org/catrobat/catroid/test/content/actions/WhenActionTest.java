/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.actions;

import android.test.AndroidTestCase;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;

public class WhenActionTest extends AndroidTestCase {

	public void testWhenBrick() throws InterruptedException {
		int testPosition = 100;

		Sprite sprite = new Sprite("new sprite");
		WhenScript whenScript = new WhenScript();
		whenScript.setAction(1);
		Brick placeAtBrick = new PlaceAtBrick(testPosition, testPosition);
		whenScript.addBrick(placeAtBrick);
		sprite.addScript(whenScript);
		sprite.createWhenScriptActionSequence(whenScript.getAction());

		while (!sprite.look.getAllActionsAreFinished()) {
			sprite.look.act(1.0f);
		}

		assertEquals("Simple broadcast failed", (float) testPosition, sprite.look.getX());
	}
}
