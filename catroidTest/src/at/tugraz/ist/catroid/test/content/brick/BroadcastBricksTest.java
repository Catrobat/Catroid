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
		sprite.getScriptList().add(script);

		BroadcastScript broadcastScript = new BroadcastScript("broadcastScript", sprite);
		int testPosition = 100;
		SetXBrick testBrick = new SetXBrick(sprite, testPosition);
		broadcastScript.setBroadcastMessage(message);
		broadcastScript.addBrick(testBrick);
		sprite.getScriptList().add(broadcastScript);

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
		sprite.getScriptList().add(scriptWait);

		BroadcastScript broadcastScript = new BroadcastScript("broadcastScript", sprite);
		WaitBrick waitBrick = new WaitBrick(sprite, 500);
		int secTestPosition = 20;
		SetXBrick secXBrick = new SetXBrick(sprite, secTestPosition);
		broadcastScript.setBroadcastMessage(message);
		broadcastScript.addBrick(waitBrick);
		broadcastScript.addBrick(secXBrick);
		sprite.getScriptList().add(broadcastScript);

		sprite.startStartScripts();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		assertEquals("Broadcast and wait failed", testPosition, sprite.getXPosition());
	}
}
