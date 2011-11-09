/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
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
		Script script = new StartScript("startScript", sprite);
		BroadcastBrick broadcastBrick = new BroadcastBrick(sprite);
		String message = "simpleTest";
		broadcastBrick.setSelectedMessage(message);
		script.addBrick(broadcastBrick);
		sprite.addScript(script);

		BroadcastScript broadcastScript = new BroadcastScript("broadcastScript", sprite);
		int testPosition = 100;
		SetXBrick testBrick = new SetXBrick(sprite, testPosition);
		broadcastScript.setBroadcastMessage(message);
		broadcastScript.addBrick(testBrick);
		sprite.addScript(broadcastScript);

		sprite.startStartScripts();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		assertEquals("Simple broadcast failed", testPosition, sprite.getXPosition());
	}

	public void testBroadcastWait() {
		Sprite sprite = new Sprite("spriteOne");
		Script scriptWait = new StartScript("scriptWait", sprite);
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(sprite);
		String message = "waitTest";
		broadcastWaitBrick.setSelectedMessage(message);
		int testPosition = 100;
		SetXBrick xBrick = new SetXBrick(sprite, testPosition);
		scriptWait.addBrick(broadcastWaitBrick);
		scriptWait.addBrick(xBrick);
		sprite.addScript(scriptWait);

		BroadcastScript broadcastScript = new BroadcastScript("broadcastScript", sprite);
		WaitBrick waitBrick = new WaitBrick(sprite, 500);
		int secTestPosition = 20;
		SetXBrick secXBrick = new SetXBrick(sprite, secTestPosition);
		broadcastScript.setBroadcastMessage(message);
		broadcastScript.addBrick(waitBrick);
		broadcastScript.addBrick(secXBrick);
		sprite.addScript(broadcastScript);

		sprite.startStartScripts();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		assertEquals("Broadcast and wait failed", testPosition, sprite.getXPosition());
	}
}
