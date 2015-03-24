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
package org.catrobat.catroid.test.content.sprite;

import android.test.AndroidTestCase;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;

import java.util.HashMap;
import java.util.List;

public class StartResumeSpriteTest extends AndroidTestCase {

	public void testStartThreads() throws InterruptedException {
		double size = 300;
		Sprite testSprite = new Sprite("testSprite");
		Script testScript = new StartScript();
		HideBrick hideBrick = new HideBrick();
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(size);

		testScript.addBrick(hideBrick);
		testScript.addBrick(setSizeToBrick);
		testSprite.addScript(testScript);

		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(1.0f);
		}

		assertFalse("Sprite is not hidden", testSprite.look.visible);
		assertEquals("the size is not as expected", (float) size / 100, testSprite.look.getScaleX());
		assertEquals("the size is not as expected", (float) size / 100, testSprite.look.getScaleY());
	}

	public void testResumeThreads() throws InterruptedException {
		Sprite testSprite = new Sprite("testSprite");
		Script testScript = new StartScript();
		HideBrick hideBrick = new HideBrick();
		WaitBrick waitBrick = new WaitBrick(400);
		ShowBrick showBrick = new ShowBrick();

		testScript.addBrick(hideBrick);
		testScript.addBrick(waitBrick);
		testScript.addBrick(showBrick);
		testSprite.addScript(testScript);

		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		testSprite.look.act(1.0f);
		testSprite.look.act(1.0f);

		testSprite.pause();
		assertFalse("Sprite is not hidden", testSprite.look.visible);
		testSprite.resume();

		testSprite.look.act(1.0f);

		assertTrue("Sprite is hidden", testSprite.look.visible);

		testScript.getBrickList().clear();
		testScript.addBrick(hideBrick);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		assertTrue("Sprite is hidden - this script shall not be execute", testSprite.look.visible);
	}
}
