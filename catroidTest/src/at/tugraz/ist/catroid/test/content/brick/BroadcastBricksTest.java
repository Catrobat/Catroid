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
import at.tugraz.ist.catroid.content.BroadcastScript;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.BroadcastBrick;
import at.tugraz.ist.catroid.content.bricks.BroadcastWaitBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;

public class BroadcastBricksTest extends AndroidTestCase {

	public void testBroadcast() {
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript(sprite);
		BroadcastBrick broadcastBrick = new BroadcastBrick(sprite);
		String message = "simpleTest";
		broadcastBrick.setSelectedMessage(message);
		script.addBrick(broadcastBrick);
		sprite.addScript(script);

		BroadcastScript broadcastScript = new BroadcastScript(sprite);
		int testPosition = 100;
		SetXBrick testBrick = new SetXBrick(sprite, testPosition);
		broadcastScript.setBroadcastMessage(message);
		broadcastScript.addBrick(testBrick);
		sprite.addScript(broadcastScript);

		sprite.startStartScripts();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
		}

		assertEquals("Simple broadcast failed", testPosition, (int) sprite.costume.getXPosition());
	}

	public void testBroadcastWait() {
		Sprite sprite = new Sprite("spriteOne");
		Script scriptWait = new StartScript(sprite);
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(sprite);
		String message = "waitTest";
		broadcastWaitBrick.setSelectedMessage(message);
		int testPosition = 100;
		SetXBrick setXBrick = new SetXBrick(sprite, testPosition);
		scriptWait.addBrick(broadcastWaitBrick);
		scriptWait.addBrick(setXBrick);
		sprite.addScript(scriptWait);

		BroadcastScript broadcastScript = new BroadcastScript(sprite);
		WaitBrick waitBrick = new WaitBrick(sprite, 500);
		int setTestPosition = 20;
		SetXBrick setXBrick2 = new SetXBrick(sprite, setTestPosition);
		broadcastScript.setBroadcastMessage(message);
		broadcastScript.addBrick(waitBrick);
		broadcastScript.addBrick(setXBrick2);
		sprite.addScript(broadcastScript);

		sprite.startStartScripts();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
		}

		assertEquals("Broadcast and wait failed", testPosition, (int) sprite.costume.getXPosition());
	}
}
